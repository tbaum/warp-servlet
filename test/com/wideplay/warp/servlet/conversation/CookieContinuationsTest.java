package com.wideplay.warp.servlet.conversation;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.wideplay.warp.servlet.ContextManagerTestDelegator;
import com.wideplay.warp.servlet.Servlets;
import com.wideplay.warp.servlet.WebFilter;
import static org.easymock.EasyMock.*;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Jan 2, 2008
 * Time: 2:29:45 PM
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
public class CookieContinuationsTest {
    private TestConvObject testConvObject;
    private static final String TESTCONV_ID = "oohahh";

    @BeforeMethod
    public void prepare() {
        testConvObject = null;
        ContextManagerTestDelegator.setInjector(null);

    }


    //a test object that is conv-scoped
    @ConversationScoped
    public static class TestConvObject {
        private final String value = UUID.randomUUID().toString(); 
    }



    //this is a convoluted test that asserts the same conv and the same instance are preserved across requests using easymock
    //TODO I need to triangulate this using a manual mocking system
    @Test
    public final void continueConvAcrossRequests() throws ServletException, IOException {
        ConversationContext conversationContext = new HashMapConversationContext();

        //create mocks
        HttpServletRequest request =  createNiceMock(HttpServletRequest.class);
        HttpServletResponse response = createNiceMock(HttpServletResponse.class);

        final ConversationStore store = createMock(ConversationStore.class);


        //teach helpful scripts
        expect(request.getCookies())
                .andReturn(new Cookie[0]);

        //the conv id gets set
        expect(request.getAttribute(CookieContinuation.WARPCONVID))
                .andReturn(TESTCONV_ID);


        //start new conv
        expect(store.newConversation(isA(String.class)))
                .andReturn(conversationContext)
                .anyTimes();

        //get the conv id (ignore the autocreated one and pretend this is it)
        expect(store.get(TESTCONV_ID))
                .andReturn(conversationContext)
                .anyTimes();




        final Injector injector = Guice.createInjector(Servlets.configure()
                .filters()
                .buildModule(),
                new AbstractModule() {
                    protected void configure() {
                        bindScope(ConversationScoped.class, Servlets.CONVERSATION_SCOPE);
                        bind(ConversationStore.class).toInstance(store);
                    }
                }
        );

        final WebFilter filter = new WebFilter();
        FilterChain filterChain = scriptedFilterChain(injector);
        filter.init(mockFilterConfig(injector));

        replay(request, response, store);

        //simulate request1
        filter.doFilter(request, response, filterChain);

        assert null != testConvObject;
        TestConvObject temp = testConvObject;

        testConvObject = null;

//        verify(request, response, store);



        // ***** SECOND REQUEST  ******


        //teach continuation
        request = createNiceMock(HttpServletRequest.class);
        response = createNiceMock(HttpServletResponse.class);

        

        //look for continuation from request (not available yet, so return null)
        expect(request.getAttribute(CookieContinuation.WARPCONVID))
                .andReturn(null);


        //now look for it from the cookie set
        expect(request.getCookies())
                .andReturn(cookies());


        //now that it has been found, it will be set on the request for easy retrieval
        request.setAttribute(CookieContinuation.WARPCONVID, TESTCONV_ID);
        expectLastCall().once();

        

        replay(request, response);

        //simulate request2 (in this request we continue the conv with a set cookie)
        filter.doFilter(request, response, filterChain);

        assert null != testConvObject;
        assert temp == testConvObject;
        //noinspection StringEquality
        assert temp.value == testConvObject.value;


        filter.destroy();
    }


    //returns a simulated cookie (from a prev conv)
    @NotNull
    private Cookie[] cookies() {
        final Cookie[] cookies = new Cookie[1];

        cookies[0] = new Cookie(CookieContinuation.WARPCONVID, TESTCONV_ID);

        return cookies;
    }

    @SuppressWarnings({"OverlyComplexAnonymousInnerClass"})
    private FilterConfig mockFilterConfig(final Injector injector) {
        return new FilterConfig() {
            public String getFilterName() {
                return null;
            }

            public ServletContext getServletContext() {
                final ServletContext mock = createMock(ServletContext.class);
                expect(mock.getAttribute(Injector.class.getName()))
                        .andReturn(injector);

                replay(mock);

                return mock;
            }

            public String getInitParameter(String s) {
                return null;
            }

            public Enumeration getInitParameterNames() {
                return null;
            }
        };
    }

    private FilterChain scriptedFilterChain(final Injector injector) {
        return new FilterChain() {
            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse)
                    throws IOException, ServletException {

                //start conv if not already started
                injector.getInstance(Conversation.class).begin();

                //this is where the request processing is actually done (after webfilter is applied)
                testConvObject = injector.getInstance(TestConvObject.class);


            }
        };
    }
}
