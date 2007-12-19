package com.wideplay.warp.servlet;

import com.google.inject.Injector;
import com.google.inject.Singleton;
import net.jcip.annotations.Immutable;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Dec 19, 2007
 * Time: 8:22:32 PM
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
@Immutable
@Singleton
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
}
