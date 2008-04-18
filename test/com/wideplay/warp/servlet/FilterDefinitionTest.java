package com.wideplay.warp.servlet;

import com.google.inject.Injector;
import com.google.inject.Key;
import static com.wideplay.warp.servlet.uri.UriPatternType.SERVLET;
import static com.wideplay.warp.servlet.uri.UriPatternType.get;
import static org.easymock.EasyMock.*;
import org.testng.annotations.Test;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dhanji R. Prasanna (dhanji@gmail com)
 */
public class FilterDefinitionTest {

    @Test
    public final void filterInitAndConfig() throws ServletException {

        Injector injector = createMock(Injector.class);

        final MockFilter mockFilter = new MockFilter();
        expect(injector.getInstance(Key.get(Filter.class)))
                .andReturn(mockFilter)
                .anyTimes();


        replay(injector);

        //some init params
        final Map<String, String> initParams = new HashMap<String, String>() {{
            put("ahsd", "asdas24dok");
            put("ahssd", "asdasd124ok");
            put("ahfsasd", "asda124sdok");
            put("ahsasgd", "a124sdasdok");
            put("ahsd124124", "as124124124dasdok");
        }};

        final FilterDefinition filterDef = new FilterDefinition("/*", Key.get(Filter.class), get(SERVLET), initParams);
        assert filterDef.getFilter(injector) instanceof MockFilter;

        ServletContext servletContext = createMock(ServletContext.class);
        final String contextName = "thing__!@@44";
        expect(servletContext.getServletContextName())
                .andReturn(contextName);

        replay(servletContext);

        filterDef.init(servletContext, injector);

        final FilterConfig filterConfig = mockFilter.getConfig();
        assert null != filterConfig;
        assert contextName.equals(filterConfig.getServletContext().getServletContextName());
        assert Key.get(Filter.class).toString().equals(filterConfig.getFilterName());

        final Enumeration names = filterConfig.getInitParameterNames();
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();

            assert initParams.containsKey(name);
            assert initParams.get(name).equals(filterConfig.getInitParameter(name));
        }
    }

    @Test
    public final void filterCreateDispatchDestroy() throws ServletException, IOException {
        Injector injector = createMock(Injector.class);
        HttpServletRequest request = createMock(HttpServletRequest.class);

        final MockFilter mockFilter = new MockFilter();
        expect(injector.getInstance(Key.get(Filter.class)))
                .andReturn(mockFilter)
                .anyTimes();

        expect(request.getServletPath())
                .andReturn("/index.html");

        replay(injector, request);

        final FilterDefinition filterDef = new FilterDefinition("/*", Key.get(Filter.class), get(SERVLET), new HashMap<String, String>());
        assert filterDef.getFilter(injector) instanceof MockFilter;

        //should fire on mockfilter now
        filterDef.init(createMock(ServletContext.class), injector);

        assert mockFilter.isInit() : "Init did not fire";

        final boolean proceed[] = new boolean[1];
        filterDef.doFilter(injector, request, null, new FilterChainInvocation(null, null, null, null) {
            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException, ServletException {
                proceed[0] = true;
            }
        });

        assert proceed[0] : "Filter did not proceed down chain";

        filterDef.destroy(injector);
        assert mockFilter.isDestroy() : "Destroy did not fire";

        verify(injector, request);

    }

    @Test
    public final void filterCreateDispatchDestroySupressChain() throws ServletException, IOException {
        Injector injector = createMock(Injector.class);
        HttpServletRequest request = createMock(HttpServletRequest.class);

        final MockFilter mockFilter = new MockFilter() {
            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
                setRun(true);

                //suppress rest of chain...
            }
        };
        expect(injector.getInstance(Key.get(Filter.class)))
                .andReturn(mockFilter)
                .anyTimes();

        expect(request.getServletPath())
                .andReturn("/index.html");

        replay(injector, request);

        final FilterDefinition filterDef = new FilterDefinition("/*", Key.get(Filter.class), get(SERVLET), new HashMap<String, String>());
        assert filterDef.getFilter(injector) instanceof MockFilter;

        //should fire on mockfilter now
        filterDef.init(createMock(ServletContext.class), injector);

        assert mockFilter.isInit() : "Init did not fire";

        final boolean proceed[] = new boolean[1];
        filterDef.doFilter(injector, request, null, new FilterChainInvocation(null, null, null, null) {
            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException, ServletException {
                proceed[0] = true;
            }
        });

        assert !proceed[0] : "Filter did not suppress chain";

        filterDef.destroy(injector);
        assert mockFilter.isDestroy() : "Destroy did not fire";

        verify(injector, request);

    }

    private static class MockFilter implements Filter {
        private boolean init;
        private boolean destroy;
        private boolean run;
        private FilterConfig config;

        public void init(FilterConfig filterConfig) throws ServletException {
            init = true;

            this.config = filterConfig;
        }

        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
            run = true;

            //proceed
            filterChain.doFilter(servletRequest, servletResponse);
        }

        protected void setRun(boolean run) {
            this.run = run;
        }

        public void destroy() {
            destroy = true;
        }

        public boolean isInit() {
            return init;
        }

        public boolean isDestroy() {
            return destroy;
        }

        public boolean isRun() {
            return run;
        }

        public FilterConfig getConfig() {
            return config;
        }
    }
}
