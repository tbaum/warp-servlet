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
 * Time: 12:51:48 PM
 *
 * This test looks at a filter that dispatches to guice-managed servlets.
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
public class FilterDispatchIntegrationTest {
    private static int inits, doFilters, destroys;

    @BeforeMethod
    public final void reset() {
        inits = 0;
        doFilters = 0;
        destroys = 0;
    }


    @Test
    public final void dispatchRequestToManagedPipeline() throws ServletException, IOException {
        final Injector injector = Guice.createInjector(Servlets.configure()
                .filters()
                .filter("/*").through(TestFilter.class)
                .filter("*.html").through(TestFilter.class)
                .filter("/*").through(Key.get(TestFilter.class))

                //these filters should never fire
                .filter("/index/*").through(Key.get(TestFilter.class))
                .filter("*.jsp").through(Key.get(TestFilter.class))

                .servlets()
                .buildModule()
        );

        final ManagedFilterPipeline pipeline = injector.getInstance(ManagedFilterPipeline.class);

        pipeline.initPipeline(null, injector);

        //create ourselves a mock request with test URI
        HttpServletRequest requestMock = createMock(HttpServletRequest.class);

        expect(requestMock.getServletPath())
                .andReturn("/index.html")
                .times(5);


        //dispatch request
        replay(requestMock);

        pipeline.dispatch(injector, requestMock, null, createMock(FilterChain.class));

        pipeline.destroyPipeline(injector);

        verify(requestMock);

        
        assert inits == 5 && doFilters == 3 && destroys == 5 : "lifecycle states did not fire correct number of times-- inits: " + 
                inits + "; dos: " + doFilters + "; destroys: " + destroys;
    }

    @Test
    public final void dispatchNoFiltersFire() throws ServletException, IOException {
        final Injector injector = Guice.createInjector(Servlets.configure()
                .filters()

                .filter("/public/*").through(TestFilter.class)
                .filter("*.html").through(TestFilter.class)
                .filter("*.xml").through(Key.get(TestFilter.class))

                //these filters should never fire
                .filter("/index/*").through(Key.get(TestFilter.class))
                .filter("*.jsp").through(Key.get(TestFilter.class))

                .buildModule()
        );

        final ManagedFilterPipeline pipeline = injector.getInstance(ManagedFilterPipeline.class);

        pipeline.initPipeline(null, injector);

        //create ourselves a mock request with test URI
        HttpServletRequest requestMock = createMock(HttpServletRequest.class);

        expect(requestMock.getServletPath())
                .andReturn("/index.xhtml")
                .times(5);


        //dispatch request
        replay(requestMock);

        pipeline.dispatch(injector, requestMock, null, createMock(FilterChain.class));

        pipeline.destroyPipeline(injector);

        verify(requestMock);


        assert inits == 5 && doFilters == 0 && destroys == 5 : "lifecycle states did not fire correct number of times-- inits: " +
                inits + "; dos: " + doFilters + "; destroys: " + destroys;
    }

    @Test
    public final void dispatchFilterPipelineWithRegexMatching() throws ServletException, IOException {
        final Injector injector = Guice.createInjector(Servlets.configure()
                .filters()
                
                .filterRegex("/[A-Za-z]*").through(TestFilter.class)
                .filterRegex("/index").through(TestFilter.class)

                //these filters should never fire
                .filterRegex("\\w").through(Key.get(TestFilter.class))

                .buildModule()
        );

        final ManagedFilterPipeline pipeline = injector.getInstance(ManagedFilterPipeline.class);

        pipeline.initPipeline(null, injector);

        //create ourselves a mock request with test URI
        HttpServletRequest requestMock = createMock(HttpServletRequest.class);

        expect(requestMock.getServletPath())
                .andReturn("/index")
                .times(3);


        //dispatch request
        replay(requestMock);

        pipeline.dispatch(injector, requestMock, null, createMock(FilterChain.class));

        pipeline.destroyPipeline(injector);

        verify(requestMock);


        assert inits == 3 && doFilters == 2 && destroys == 3 : "lifecycle states did not fire correct number of times-- inits: " +
                inits + "; dos: " + doFilters + "; destroys: " + destroys;
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
