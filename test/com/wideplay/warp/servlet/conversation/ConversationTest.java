package com.wideplay.warp.servlet.conversation;

import com.google.inject.*;
import com.wideplay.warp.servlet.ContextManagerTestDelegator;
import com.wideplay.warp.servlet.Servlets;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.*;
import org.testng.annotations.Test;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Dec 23, 2007
 * Time: 3:20:08 PM
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
public class ConversationTest {

    public static class TestConvObject {
        final String value = UUID.randomUUID().toString();
    }

    @Test
    public final void conversationScopingOfInstances() {
        //create mocks
        HttpServletRequest request = createDummyRequest();
        HttpServletResponse response = createMock(HttpServletResponse.class);



        //begin mock script ***
        response.addCookie(isA(Cookie.class));


        //run mock script ***
        replay(response);
        ContextManagerTestDelegator.setInjector(Guice.createInjector(new AbstractModule() {
            protected void configure() {
                install(Servlets.configure().filters().buildModule());
                bind(TestConvObject.class).in(Servlets.CONVERSATION_SCOPE);
            }
        }));
        ContextManagerTestDelegator.set(request, response);


        Conversation conversation = ContextManagerTestDelegator.getInjector().getInstance(ConversationImpl.class);
        conversation.begin();

        //current request is part of conv...
        final Injector injector = ContextManagerTestDelegator.getInjector();

        final TestConvObject object = injector.getInstance(TestConvObject.class);
        assert null != object;
        assert object == injector.getInstance(TestConvObject.class) : "injector did not returned scoped copy of object"; 
        assert object == injector.getInstance(TestConvObject.class) : "injector did not returned scoped copy of object";
        assert object == injector.getInstance(TestConvObject.class) : "injector did not returned scoped copy of object";
        assert object == injector.getInstance(TestConvObject.class) : "injector did not returned scoped copy of object"; 

        assert null != request.getAttribute(CookieContinuation.WARPCONVID) : "conv key was not stashed in request";

        //clear
        ContextManagerTestDelegator.unset();
        ContextManagerTestDelegator.setInjector(null);

        //assert expectations
        verify(response);
    }

    @SuppressWarnings("deprecation")
    private HttpServletRequest createDummyRequest() {
        return new HttpServletRequestWrapper(createNiceMock(HttpServletRequest.class)) {
            private final Map<String, Object> attributes = new HashMap<String, Object>();
            @Override
            public Object getAttribute(String s) {
                return attributes.get(s);
            }

            @Override
            public void setAttribute(String s, Object o) {
                attributes.put(s, o);
            }
        };
    }


    @Test
    public final void conversationContextAndCookiedContinuation() {


        final HttpServletRequest request = createMock(HttpServletRequest.class);
        final Provider<HttpServletRequest> requestProvider = new Provider<HttpServletRequest>() {
            public HttpServletRequest get() {
                return request;
            }
        };


        Injector injector = createMock(Injector.class);
        

        final HttpServletResponse response = createMock(HttpServletResponse.class);
        final Provider<HttpServletResponse> responseProvider = new Provider<HttpServletResponse>() {
            public HttpServletResponse get() {
                return response;
            }
        };


        final ConversationManagerImpl conversationManager = new ConversationManagerImpl(new CookieContinuation(),
                new SimpleSingletonConversationStore(), requestProvider, responseProvider);
        Conversation conversation = new ConversationImpl(conversationManager);



        //teach script***
        expect(injector.getInstance(ConversationManagerImpl.class))
                .andReturn(conversationManager)
                .anyTimes();


        //begin() called
        final String CONVERSATION_UNIQUE_KEY = UUID.randomUUID().toString();

        response.addCookie(isA(Cookie.class));
        expectLastCall().once();

        request.setAttribute(EasyMock.eq(CookieContinuation.WARPCONVID), eq(CONVERSATION_UNIQUE_KEY));
        expectLastCall().once();



        //lookup conv
        expect(request.getAttribute(CookieContinuation.WARPCONVID))
                .andReturn(CONVERSATION_UNIQUE_KEY)
                .times(3);


        //run script
        replay(request, response, injector);
        ContextManagerTestDelegator.setInjector(injector);

        //lets simulate a request first
        ContextManagerTestDelegator.set(request, response);

        //start with auto-key
//        equivalent of conversation.begin();
        conversationManager.set(CONVERSATION_UNIQUE_KEY);

        final TestConvObject testConvObject = new TestConvObject();
        final Provider<TestConvObject> objectProvider = new Provider<TestConvObject>() {
            public TestConvObject get() {
                return testConvObject;
            }
        };
        assert testConvObject == conversationManager.getAndPutIfAbsent(Key.get(TestConvObject.class), objectProvider, request)
                : "did not get stored correctly in conv scoping context";
        assert testConvObject == conversationManager.getAndPutIfAbsent(Key.get(TestConvObject.class), objectProvider, request)
                : "did not get stored correctly in conv scoping context";
        assert testConvObject == conversationManager.getAndPutIfAbsent(Key.get(TestConvObject.class), objectProvider, request)
                : "did not get stored correctly in conv scoping context";


        //end contextual conversation
        conversation.end();

        //end the request
        ContextManagerTestDelegator.unset();

        //assert stuff
        verify(request, response, injector);




        //new request, now carries a cookie for the continuation
        reset(request, response, injector);

        //** teach script
        expect(injector.getInstance(ConversationManagerImpl.class))
                .andReturn(conversationManager)
                .anyTimes();    //not really used...

        expect(request.getAttribute(CookieContinuation.WARPCONVID))
                .andReturn(null)
                .once();   //not set because this is a proper continuation


        //extract conv key from cookies and cache it in the request
        expect(request.getCookies())
                .andReturn(mockCookies(CONVERSATION_UNIQUE_KEY));
        request.setAttribute(CookieContinuation.WARPCONVID, CONVERSATION_UNIQUE_KEY);
        expectLastCall().once();


        
        expect(request.getAttribute(CookieContinuation.WARPCONVID))
                .andReturn(CONVERSATION_UNIQUE_KEY)
                .once();   //now set because this has been continuationified!!

        //** run script
        replay(request, response, injector);
        ContextManagerTestDelegator.set(request, response);

        assert testConvObject == conversationManager.getAndPutIfAbsent(Key.get(TestConvObject.class), objectProvider, request)
                : "Continuation did not work for second request; did not get stored correctly in conv scoping context";
        assert testConvObject == conversationManager.getAndPutIfAbsent(Key.get(TestConvObject.class), objectProvider, request)
                : "Continuation did not work for second request; did not get stored correctly in conv scoping context";

        //end
        ContextManagerTestDelegator.unset();
        ContextManagerTestDelegator.setInjector(null);
        verify(request, response, injector);
    }

    private Cookie[] mockCookies(String key) {
        final Cookie[] cookies = new Cookie[2];

        //a dummy cookie
        Cookie cookie = new Cookie("JSESSIONID", "MOOTYLE");
        cookies[0] = cookie;

        cookie = new Cookie("WARPCONVID", key);
        cookies[1] = cookie;

        return cookies;
    }
}
