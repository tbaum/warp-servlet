package com.wideplay.warp.servlet;

import com.google.inject.Injector;
import net.jcip.annotations.NotThreadSafe;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * On: 25/09/2007
 *
 * A Filter chain impl which basically passes itself to the "current" filter and iterates
 *  the chain on doFilter().
 *
 * Following this, it attempts to dispatch to warp-servlet's registered servlets using the
 *  ManagedServletPipeline.
 *
 * And the end, it proceeds to the web.xml (default) servlet filter chain, if necessary.
 *
 * @author Dhanji R. Prasanna
 * @since 1.0
 */
@NotThreadSafe
class FilterChainInvocation implements FilterChain {
    private final List<FilterDefinition> filterDefinitions;
    private final FilterChain proceedingChain;
    private final Injector injector;
    private final ManagedServletPipeline servletPipeline;

    //state variable tracks current iteration
    private int index = -1;

    public FilterChainInvocation(List<FilterDefinition> filterDefinitions, ManagedServletPipeline servletPipeline,
                                 FilterChain proceedingChain, Injector injector) {

        this.filterDefinitions = filterDefinitions;
        this.servletPipeline = servletPipeline;
        this.proceedingChain = proceedingChain;
        this.injector = injector;
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException, ServletException {
        index++;

        //dispatch down the chain while there are more filters
        if (index < filterDefinitions.size()) {
            filterDefinitions.get(index)
                    .doFilter(injector, servletRequest, servletResponse, this);
        } else {

            //we've reached the end of the chain, let's try to dispatch to a servlet
            final boolean serviced = servletPipeline.service(injector, servletRequest, servletResponse);

            //dispatch to the normal filter chain only if one of our servlets did not match; and be done
            if (!serviced)
                proceedingChain.doFilter(servletRequest, servletResponse);
        }
    }
}