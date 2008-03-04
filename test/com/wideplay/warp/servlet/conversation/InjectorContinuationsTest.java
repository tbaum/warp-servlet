package com.wideplay.warp.servlet.conversation;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.*;

import static org.easymock.EasyMock.*;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.AbstractModule;
import com.wideplay.warp.servlet.Servlets;
import com.wideplay.warp.servlet.WebFilter;
import com.wideplay.warp.servlet.ContextManagerTestDelegator;

import java.io.IOException;
import java.util.UUID;
import java.util.Enumeration;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Jan 2, 2008
 * Time: 2:29:45 PM
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
public class InjectorContinuationsTest {
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
                .andReturn(conversationContext);

        //get the conv id (ignore the autocreated one and pretend this is it)
        expect(store.get(TESTCONV_ID))
                .andReturn(conversationContext);


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

        reset(request, response);

        //simulate request2
        filter.doFilter(request, response, filterChain);

        assert null != testConvObject;
        assert temp == testConvObject;


        filter.destroy();
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
