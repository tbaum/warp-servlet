package com.wideplay.warp.servlet;

import com.google.inject.Guice;
import com.google.inject.Singleton;
import com.google.inject.Injector;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import static org.easymock.EasyMock.*;

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
    private static String interceptedRemoteUser;
    private static boolean proxiedCorrectly;

    @BeforeMethod
    void setupFilterChain() {
        injector = Guice.createInjector(Servlets.configure()
                .filters()
                .filter("/*").through(InterceptingFilter.class)

                .servlets()
                .serve("*.html").with(HtmlServlet.class)

                .buildModule()
        );

        //setup the proxy that will be used by the filter to intercept the request

        //reset
        interceptedRemoteUser = "";
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

        expect(request.getRequestURI())
                .andReturn("/thing/index.html")
                .times(2);



        //run mock script ***
        replay(request, filterConfig, servletContext);

        final WebFilter filter = new WebFilter();
        filter.init(filterConfig);

        assert !proxiedCorrectly : "start state is wrong";
        filter.doFilter(request, null, proceedingFilterChain);
        assert proxiedCorrectly : "Request did not get proxied by filter";

        filter.destroy();


        //assert expectations
        verify(request, filterConfig, servletContext);

        assert !proxiedCorrectly : "state did not get reset";

    }


    @Singleton
    public static class InterceptingFilter implements Filter {
        private String remoteUser;

        public void init(FilterConfig filterConfig) throws ServletException {
            remoteUser = interceptedRemoteUser;
        }

        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
            final HttpServletRequestWrapper httpServletRequestWrapper = new HttpServletRequestWrapper((HttpServletRequest) servletRequest) {
                public String getRemoteUser() {
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
    public static class HtmlServlet extends HttpServlet {
        private String proxy;

        public void init(ServletConfig filterConfig) throws ServletException {
            //this trick also helps lifecycle dispatch order is correct
            this.proxy = interceptedRemoteUser;
            proxiedCorrectly = false;
        }

        public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException, ServletException {
            proxiedCorrectly = proxy.equals(((HttpServletRequest)servletRequest).getRemoteUser());
        }

        public void destroy() {
            proxiedCorrectly = false;
            this.proxy = null;
        }
    }

}
