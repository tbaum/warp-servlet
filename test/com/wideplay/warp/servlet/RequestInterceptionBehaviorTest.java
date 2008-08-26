package com.wideplay.warp.servlet;

import com.google.inject.*;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import static org.easymock.EasyMock.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Dec 20, 2007
 * Time: 12:29:52 PM
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
public class RequestInterceptionBehaviorTest {
    private Injector injector;
    private String[] interceptedRemoteUser;        //major ugly hacks because we dont have closures
    private Boolean[] proxiedCorrectly;            //hey atleast Im not using statics =P

    private static final String ORIGINAL_USER = "originalUser";
    private static final String INTERCEPTED_REMOTE_USER = "interceptedRemoteUser";
    private static final String PROXIED_CORRECTLY = "proxiedRight";

    @BeforeMethod
    void setupFilterChain() {

        //reset
        interceptedRemoteUser = new String[] {"intercepted"};
        proxiedCorrectly = new Boolean[] { false };
        
        injector = Guice.createInjector(Servlets.configure()
                .filters()
                .filter("/*").through(InterceptingFilter.class)
                .filter("/notThis/*").through(NeverFilter.class)

                .servlets()
                .serve("*.html").with(HtmlServlet.class)

                .buildModule(),
                new AbstractModule() {
                    @SuppressWarnings({"InnerClassTooDeeplyNested"})
                    protected void configure() {
                        bind(String[].class).annotatedWith(Names.named(INTERCEPTED_REMOTE_USER))
                                .toInstance(interceptedRemoteUser);

                        bind(Boolean[].class).annotatedWith(Names.named(PROXIED_CORRECTLY))
                                .toInstance(proxiedCorrectly);

                    }
                }
        );

        //setup the proxy that will be used by the filter to intercept the request

    }

    @Test
    public final void wrapRequestInFilter() throws ServletException, IOException {
        //create mocks
        HttpServletRequest request = createMock(HttpServletRequest.class);
        FilterConfig filterConfig = createMock(FilterConfig.class);
        ServletContext servletContext = createMock(ServletContext.class);
        FilterChain proceedingFilterChain = createMock(FilterChain.class);

        //begin mock script ***
        expect(filterConfig.getServletContext())
                .andReturn(servletContext)
                .once();

        expect(servletContext.getAttribute(WarpServletContextListener.INJECTOR_NAME))
                .andReturn(injector)
                .once();

        expect(request.getServletPath())
                .andReturn("/thing/index.html")
                .anyTimes();

        expect(request.getRemoteUser())
                .andReturn(ORIGINAL_USER)
                .once();



        //run mock script ***
        replay(request, filterConfig, servletContext);

        final WebFilter filter = new WebFilter();
        filter.init(filterConfig);

        assert !proxiedCorrectly[0] : "start state is wrong";
        filter.doFilter(request, null, proceedingFilterChain);
        assert proxiedCorrectly[0] : "Request did not get proxied by filter";

        filter.destroy();


        //assert expectations
        verify(request, filterConfig, servletContext);

        assert !proxiedCorrectly[0] : "state did not get reset";

    }


    @Singleton
    public static class InterceptingFilter implements Filter {
        private String remoteUser;
        @Inject @Named(INTERCEPTED_REMOTE_USER) String[] interceptedRemoteUser;

        public void init(FilterConfig filterConfig) throws ServletException {
            remoteUser = interceptedRemoteUser[0];
        }

        @SuppressWarnings({"InnerClassTooDeeplyNested", "deprecation"})
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
            System.out.println("Running " + InterceptingFilter.class);
            final HttpServletRequestWrapper httpServletRequestWrapper = new HttpServletRequestWrapper((HttpServletRequest) servletRequest) {

                @Override
                public String getRemoteUser() {
                    assert ORIGINAL_USER.equals(super.getRemoteUser()) : "original user was not as expected...";
                    return remoteUser;
                }
            };
            filterChain.doFilter(httpServletRequestWrapper, servletResponse);
        }

        public void destroy() {
            remoteUser = null;
        }
    }

    @Singleton
    public static class NeverFilter implements Filter {

        public void init(FilterConfig filterConfig) throws ServletException {
        }

        @SuppressWarnings({"InnerClassTooDeeplyNested"})
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
            assert false : "This filter should never run";
        }

        public void destroy() {
        }
    }

    @Singleton
    public static class HtmlServlet extends HttpServlet {
        private String proxy;
        @Inject @Named(INTERCEPTED_REMOTE_USER) String[] interceptedRemoteUser;
        @Inject @Named(PROXIED_CORRECTLY) Boolean[] proxiedCorrectly;


        public void init(ServletConfig filterConfig) throws ServletException {
            //this trick also helps lifecycle dispatch order is correct
            this.proxy = interceptedRemoteUser[0];
            proxiedCorrectly[0] = false;
        }

        public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException, ServletException {
            final String remoteUser = ((HttpServletRequest) servletRequest).getRemoteUser();
            proxiedCorrectly[0] = proxy.equals(remoteUser);

            assert !ORIGINAL_USER.equals(remoteUser) : "did not get proxied correctly";
        }

        public void destroy() {
            proxiedCorrectly[0] = false;
            this.proxy = null;
        }
    }

}
