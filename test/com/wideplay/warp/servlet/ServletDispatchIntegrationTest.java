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
import javax.servlet.http.HttpServlet;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Dec 19, 2007
 * Time: 12:51:48 PM
 *
 * This test looks at a filter that dispatches to guice-managed servlets.
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
public class ServletDispatchIntegrationTest {
    private static int inits, services, destroys, doFilters;

    @BeforeMethod
    public final void reset() {
        inits = 0;
        services = 0;
        destroys = 0;
        doFilters = 0;
    }


    @Test
    public final void dispatchRequestToManagedPipelineServlets() throws ServletException, IOException {
        final Injector injector = Guice.createInjector(Servlets.configure()
                .filters()

                .servlets()
                    .serve("/*").with(TestServlet.class)

                    //these servets should never fire
                    .serve("*.html").with(NeverServlet.class)
                    .serve("/*").with(Key.get(NeverServlet.class))
                    .serve("/index/*").with(Key.get(NeverServlet.class))
                    .serve("*.jsp").with(Key.get(NeverServlet.class))

                    .buildModule()
        );

        final ManagedFilterPipeline pipeline = injector.getInstance(ManagedFilterPipeline.class);

        pipeline.initPipeline(null, injector);

        //create ourselves a mock request with test URI
        HttpServletRequest requestMock = createMock(HttpServletRequest.class);

        expect(requestMock.getServletPath())
                .andReturn("/index.html")
                .times(1);


        //dispatch request
        replay(requestMock);

        pipeline.dispatch(injector, requestMock, null, createMock(FilterChain.class));

        pipeline.destroyPipeline(injector);

        verify(requestMock);


        assert inits == 5 && services == 1 && destroys == 5 : "lifecycle states did not fire correct number of times-- inits: " +
                inits + "; dos: " + services + "; destroys: " + destroys;
    }

    @Test
    public final void dispatchRequestToManagedPipelineWithFilter() throws ServletException, IOException {
        final Injector injector = Guice.createInjector(Servlets.configure()
                .filters()
                    .filter("/*").through(TestFilter.class)

                .servlets()
                    .serve("/*").with(TestServlet.class)

                    //these servets should never fire
                    .serve("*.html").with(NeverServlet.class)
                    .serve("/*").with(Key.get(NeverServlet.class))
                    .serve("/index/*").with(Key.get(NeverServlet.class))
                    .serve("*.jsp").with(Key.get(NeverServlet.class))

                    .buildModule()
        );

        final ManagedFilterPipeline pipeline = injector.getInstance(ManagedFilterPipeline.class);

        pipeline.initPipeline(null, injector);

        //create ourselves a mock request with test URI
        HttpServletRequest requestMock = createMock(HttpServletRequest.class);

        expect(requestMock.getServletPath())
                .andReturn("/index.html")
                .times(2);


        //dispatch request
        replay(requestMock);

        pipeline.dispatch(injector, requestMock, null, createMock(FilterChain.class));

        pipeline.destroyPipeline(injector);

        verify(requestMock);


        assert inits == 6 && services == 1 && destroys == 6 && doFilters == 1 : "lifecycle states did not fire correct number of times-- inits: " +
                inits + "; dos: " + services + "; destroys: " + destroys;
    }

    @Singleton
    public static class TestServlet extends HttpServlet {
        public void init(ServletConfig filterConfig) throws ServletException {
            inits++;
        }

        public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException, ServletException {
            services++;
        }

        public void destroy() {
            destroys++;
        }
    }

    @Singleton
    public static class NeverServlet extends HttpServlet {
        public void init(ServletConfig filterConfig) throws ServletException {
            inits++;
        }

        public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException, ServletException {
            assert false : "NeverServlet was fired, when it should not have been: ";
        }

        public void destroy() {
            destroys++;
        }
    }


    @Singleton
    public static class TestFilter implements Filter {
        public void init(FilterConfig filterConfig) throws ServletException {
            inits++;
        }

        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
            doFilters++;
            filterChain.doFilter(servletRequest, servletResponse);
        }

        public void destroy() {
            destroys++;
        }
    }
}