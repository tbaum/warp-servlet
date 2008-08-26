package com.wideplay.warp.servlet;

import com.google.inject.Injector;
import net.jcip.annotations.Immutable;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * <p>
 * Register this filter in web.xml above all other filters (typically), this is needed in order to
 * dispatch requests to warp-servlet managed filters and servlets. First you need to register a filter in web.xml:
 *  <pre>
 *  &lt;filter&gt;
 *      &lt;filter-name&gt;warpServletFilter&lt;/filter-name&gt;
 *      &lt;filter-class&gt;<b>com.wideplay.warp.servlet.WebFilter</b>&lt;/filter-class&gt;
 *  &lt;/filter&gt;
 *  &lt;filter-mapping&gt;
 *
 *  &lt;filter-name&gt;warpServletFilter&lt;/filter-name&gt;
 *      &lt;url-pattern&gt;/*&lt;/url-pattern&gt;
 *  &lt;/filter-mapping&gt;
 *  </pre>
 *
 * This filter should appear above every filter that makes use of Guice injection or warp-servlet scopes functionality.
 * Ideally, you want to register ONLY this filter in web.xml and register any other filters using warp-servlet see
 * {@link Servlets#configure()} for details on how to do this.
 * </p>
 * <p>
 * You only really want to place sitemesh and similar purely decorative filters above {@code WebFilter} in web.xml.
 *
 * Take a look at <a href="http://www.wideplay.com">http://www.wideplay.com</a> for more details and examples.
 * </p>
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
@Immutable
public final class WebFilter implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {
        final ServletContext servletContext = filterConfig.getServletContext();

        //setup servlet context for injection
        ContextManager.setServletContext(servletContext);
        
        //get and cache injector in ContextManager
        Injector injector = ContextManager.getInjector();

        //if not available via internal contextmanager...try servletcontext
        if (null == injector) {
            //an injector *must* be available as a servlet context param (registered by a ServletContextListener)
            injector = (Injector) servletContext.getAttribute(WarpServletContextListener.INJECTOR_NAME);

            if (null == injector)
                throw new ServletException(
                        "Cannot run WebFilter without an injector present in the ServletContext attributes. Did you forget " +
                        "to register a subclass of " + WarpServletContextListener.class.getName() + " in web.xml? Or did you try to roll " +
                        "your own but forget to register the injector under key: " +  WarpServletContextListener.INJECTOR_NAME + "?"
                );

            //let's store this one we've found...
            ContextManager.setInjector(injector);
        }

        //initialize all registered filters & servlets in that order
        injector.getInstance(ManagedFilterPipeline.class)
                .initPipeline(servletContext, injector);
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        //first setup the request context
        try {
            ContextManager.set((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse);

            //perform dispatch across the filter pipeline, ensuring that the web.xml filterchain is also honored
            final Injector injector = ContextManager.getInjector();
            injector.getInstance(ManagedFilterPipeline.class)
                    .dispatch(injector, servletRequest, servletResponse, filterChain);

        } finally {
            //clear the request context
            ContextManager.unset();
        }

    }

    public void destroy() {
        final Injector injector = ContextManager.getInjector();

        try {
            //destroy all registered filters & servlets in that order
            injector.getInstance(ManagedFilterPipeline.class)
                    .destroyPipeline(injector);

        } finally {

            //clear reference to injector
            ContextManager.cleanup();
        }

    }
}
