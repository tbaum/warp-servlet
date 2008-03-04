package com.wideplay.warp.servlet;

import com.google.inject.Injector;
import net.jcip.annotations.Immutable;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Dec 19, 2007
 * Time: 12:59:48 PM
 *
 * <p>
 * Register this filter in web.xml above all other filters (typically), this is needed in order to
 * dispatch requests to warp-servlet managed filters and servlets.
 *
 * Take a look at http://www.wideplay.com for more details and examples.
 * </p>
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
@Immutable
public final class WebFilter implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {
        final ServletContext servletContext = filterConfig.getServletContext();
        
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

        //destroy all registered filters & servlets in that order
        injector.getInstance(ManagedFilterPipeline.class)
                .destroyPipeline(injector);

        //clear reference to injector
        ContextManager.setInjector(null);
    }
}
