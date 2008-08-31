package com.wideplay.warp.servlet;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Singleton;
import static org.easymock.EasyMock.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Dec 19, 2007
 * Time: 7:28:31 PM
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
public class FilterPipelineTest {
    private Injector injector;


    @BeforeMethod
    public final void setupFilterPipeline() {
        injector = Guice.createInjector(Servlets.configure()
                .filters()

                .filter("/*").through(TestFilter.class)
                .filter("*.html").through(TestFilter.class)
                .filter("/*").through(Key.get(TestFilter.class))
                .filter("*.jsp").through(Key.get(TestFilter.class))

                //these filters should never fire
                .filter("/index/*").through(Key.get(NeverFilter.class))
                .filter("/public/login/*").through(Key.get(NeverFilter.class))

                .servlets()
                
                .buildModule()
        );

        ContextManager.setInjector(null);
    }

    @Test
    public final void dispatchViaWebFilter() throws ServletException, IOException {
        //create mocks
        FilterConfig filterConfig = createMock(FilterConfig.class);
        ServletContext servletContext = createMock(ServletContext.class);
        HttpServletRequest request = createMock(HttpServletRequest.class);
        FilterChain proceedingFilterChain = createMock(FilterChain.class);



        //begin mock script ***

        expect(filterConfig.getServletContext())
                .andReturn(servletContext)
                .once();

        expect(servletContext.getAttribute(WarpServletContextListener.INJECTOR_NAME))
                .andReturn(injector);

        expect(request.getServletPath())
                .andReturn("/public/login.jsp")
                .anyTimes();


        //no conv
        expect(request.getQueryString())
                .andReturn(null);


        //at the end, proceed down webapp's normal filter chain
        proceedingFilterChain.doFilter(isA(HttpServletRequest.class), (ServletResponse) isNull());
        expectLastCall().once();




        //run mock script ***
        replay(filterConfig, servletContext, request, proceedingFilterChain);

        final WebFilter webFilter = new WebFilter();


        webFilter.init(filterConfig);

        webFilter.doFilter(request, null, proceedingFilterChain);

        webFilter.destroy();




        //assert expectations
        verify(filterConfig, servletContext, request, proceedingFilterChain);
    }

    @Singleton
    public static class TestFilter implements Filter {
        public void init(FilterConfig filterConfig) throws ServletException {
        }

        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
            filterChain.doFilter(servletRequest, servletResponse);
        }

        public void destroy() {
        }
    }

    @Singleton
    public static class NeverFilter implements Filter {
        public void init(FilterConfig filterConfig) throws ServletException {
        }

        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
            assert false : "This filter should never have fired";
        }

        public void destroy() {
        }
    }
}
