package com.wideplay.warp.servlet;

import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.easymock.EasyMock.*;
import com.google.inject.*;

import java.util.Map;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Dec 21, 2007
 * Time: 11:08:48 AM
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
public class ScopesTest {
    private static final Key<ScopesTest> SCOPES_TEST_KEY = Key.get(ScopesTest.class);
    private static final String FLASH_CACHE_KEY = Servlets.FLASH_SCOPE.toString();

    @Test
    public final void requestScope() {
        //create mocks
        HttpServletRequest request = createStrictMock(HttpServletRequest.class);
        @SuppressWarnings("unchecked")
        Provider<ScopesTest> provider = createStrictMock(Provider.class);

        //begin mock script ***

        //first the request is obtained
        expect(request.getAttribute(SCOPES_TEST_KEY.toString()))
                .andReturn(null)
                .once();

        expect(provider.get())
                .andReturn(this);

        //now stashing the instance
        request.setAttribute(SCOPES_TEST_KEY.toString(), this);
        expectLastCall()
                .once();

        //the second get simulation (in the same request)

        expect(request.getAttribute(SCOPES_TEST_KEY.toString()))
                .andReturn(this)
                .once();

        //run mock script ***
        replay(request, provider);
        ContextManager.set(request, null);
       
        final Provider<ScopesTest> scopedProvider = Servlets.REQUEST_SCOPE.scope(SCOPES_TEST_KEY, provider);

        assert this == scopedProvider.get();
        assert this == scopedProvider.get() : "Did not return expected instance";

        ContextManager.unset();
        //assert expectations
        verify(request, provider);
    }

    @Test
    public final void requestScopeViaGuice() {
        //create mocks
        HttpServletRequest request = createStrictMock(HttpServletRequest.class);

        final Injector injector = Guice.createInjector(new AbstractModule() {
                protected void configure() {
                    bind(SCOPES_TEST_KEY).in(Servlets.REQUEST_SCOPE);
                }
        });


        //begin mock script ***

        //first the request is obtained
        expect(request.getAttribute(SCOPES_TEST_KEY.toString()))
                .andReturn(null)
                .once();

        //now stashing the instance
        request.setAttribute(eq(SCOPES_TEST_KEY.toString()), anyObject());
        expectLastCall()
                .once();

        //the second get simulation (in the same request)

        expect(request.getAttribute(SCOPES_TEST_KEY.toString()))
                .andReturn(this)
                .anyTimes();

        //run mock script ***
        replay(request);

        ContextManager.set(request, null);

        //bit of a hack, lets get the first fetch out of the way (because ezmock can't pick up guice's created internal instance):
        injector.getInstance(SCOPES_TEST_KEY);

        //now assert that the injection returns only the ezmock-provided object (i.e. from the request)
        final ScopesTest scopesTest = injector.getInstance(SCOPES_TEST_KEY);

        assert injector.getInstance(SCOPES_TEST_KEY) == scopesTest : "Did not return expected instance";
        assert injector.getInstance(SCOPES_TEST_KEY) == scopesTest : "Did not return expected instance";
        assert this == scopesTest;

        ContextManager.unset();
        //assert expectations
        verify(request);
    }

    @Test
    public final void flashScope() {
        //create mocks
        @SuppressWarnings("unchecked")
        Provider<ScopesTest> provider = createMock(Provider.class);
        HttpServletRequest request = createMock(HttpServletRequest.class);
        HttpServletRequest request2 = createMock(HttpServletRequest.class);
        HttpSession session = createMock(HttpSession.class);

        Map<Key, Object> sessionCache = new HashMap<Key, Object>();
        Map<Key, Object> requestCache = new HashMap<Key, Object>();
        //begin mock script ***


        expect(request.getSession())
                .andReturn(session)
                .anyTimes();

        expect(session.getAttribute(FLASH_CACHE_KEY))
                .andReturn(sessionCache)
                .anyTimes();


       
        expect(request.getAttribute(FLASH_CACHE_KEY))
                .andReturn(requestCache)
                .anyTimes();


        //setup a second request (post flashing)
        expect(request2.getSession())
                .andReturn(session)
                .once();


        expect(request2.getAttribute(FLASH_CACHE_KEY))
                .andReturn(requestCache)
                .anyTimes();

        //because session map is empty, we'll get a create at first
        expect(provider.get())
                .andReturn(this)
                .andReturn(new ScopesTest());



        //run mock script ***
        replay(request, request2, provider, session);

        ContextManager.set(request, null);
        final Provider<ScopesTest> testProvider = Servlets.FLASH_SCOPE.scope(Key.get(ScopesTest.class), provider);

        assert this == testProvider.get(): "Did not return expected instance";

        //now "this" will be in the session and request caches
        assert sessionCache.containsValue(this) : "create + stash did not happen!";
        assert requestCache.containsValue(this) : "create + stash did not happen!";

        assert this == testProvider.get(): "Did not return expected instance";
        assert this == testProvider.get(): "Did not return expected instance";

        ContextManager.unset();



        //start a new request
        requestCache.clear();
        ContextManager.set(request, null);

        assert sessionCache.containsValue(this) : "Flashed object did not persist across requests";

        assert testProvider.get() == this : "Flashed object did not persist across requests";
        assert testProvider.get() == this : "Flashed object did not persist across requests";
        assert testProvider.get() == this : "Flashed object did not persist across requests";
        assert testProvider.get() == this : "Flashed object did not persist across requests";


        ContextManager.unset();







        //start a new request
        requestCache.clear();
        ContextManager.set(request2, null);

        testProvider.get();
        assert !sessionCache.containsValue(this) : "Flashed object did not get cleared on second request";
        System.out.println(sessionCache);

        //session should still contain an instance for the same key however
        assert sessionCache.containsKey(SCOPES_TEST_KEY) : "Flashed object did not get a new instance on second req";

        ContextManager.unset();

        //assert expectations
        verify(request, request2, provider, session);
    }
}
