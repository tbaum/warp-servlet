package com.wideplay.warp.servlet;

import com.google.inject.Injector;
import com.google.inject.Singleton;
import net.jcip.annotations.Immutable;
import org.jetbrains.annotations.Nullable;

import javax.servlet.*;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Dec 19, 2007
 * Time: 8:22:32 PM
 *
 * A wrapping dispatcher for servlets, in much the same way as ManagedFilterPipeline is for filters.
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 * @see com.wideplay.warp.servlet.ManagedFilterPipeline
 */
@Immutable @Singleton
class ManagedServletPipeline {
    private final List<ServletDefinition> servletDefinitions;

    public ManagedServletPipeline(List<ServletDefinition> servletDefinitions) {
        this.servletDefinitions = Collections.unmodifiableList(servletDefinitions);
    }


    public void init(ServletContext servletContext, Injector injector) throws ServletException {
        for (ServletDefinition servletDefinition : servletDefinitions) {
            servletDefinition.init(servletContext, injector);
        }
    }

    public boolean service(Injector injector, ServletRequest request, ServletResponse response) throws IOException, ServletException {
        
        //stop at the first matching servlet and service
        for (ServletDefinition servletDefinition : servletDefinitions) {
            if (servletDefinition.service(injector, request, response))
                return true;
        }

        //false means there was no match, so continue down the webapp servlet chain
        return false;
    }

    public void destroy(Injector injector) {
        for (ServletDefinition servletDefinition : servletDefinitions) {
            servletDefinition.destroy(injector);
        }
    }

    @Nullable
    public RequestDispatcher getRequestDispatcher(String path, final Injector injector) {
        for (final ServletDefinition servletDefinition : servletDefinitions) {
            if (servletDefinition.shouldServe(path))
                return new RequestDispatcher() {

                    public void forward(ServletRequest servletRequest, ServletResponse servletResponse)
                            throws ServletException, IOException {

                        if (servletResponse.isCommitted())
                            throw new IllegalStateException("Response has been committed--you can only call forward before committing the response (hint: don't flush buffers)");

                        //clear buffer before forwarding
                        servletResponse.resetBuffer();

                        //TODO what does forward really do?
                        servletDefinition.doService(injector, servletRequest, servletResponse);
                    }

                    public void include(ServletRequest servletRequest, ServletResponse servletResponse)
                            throws ServletException, IOException {

                        //route to the target servlet
                        servletDefinition.doService(injector, servletRequest, servletResponse);
                    }
                };
        }

        //otherwise, can't process
        return null;
    }
}
