package com.wideplay.warp.servlet;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Module;
import com.wideplay.warp.servlet.uri.UriPatternType;
import net.jcip.annotations.NotThreadSafe;

import javax.servlet.http.HttpServlet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Builds the guice module that binds configured servlets, with their wrapper ServletDefinitions.
 * Is part of the binding EDSL. Very similar to {@link FiltersModuleBuilder}.
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 * @see Servlets
 */
@NotThreadSafe  //intended to be confined to a single thread and disposed after injector creation
class ServletsModuleBuilder extends AbstractModule implements ServletBindingBuilder {
    private List<ServletDefinition> servletDefinitions = new ArrayList<ServletDefinition>();
    private final Module filtersModule;

    public ServletsModuleBuilder(Module filtersModule) {
        this.filtersModule = filtersModule;
    }

    //invoked on injector config
    protected void configure() {
        //install preceeding module(s)
        install(new WebModule());
        install(filtersModule);

        //bind these servlet definitions to a singleton pipeline
        bind(ManagedServletPipeline.class)
                .toInstance(new ManagedServletPipeline(servletDefinitions));

    }

    //the first level of the EDSL--


    public ServletKeyBindingBuilder serve(String urlPattern) {
        return new ServletKeyBindingBuilderImpl(urlPattern, UriPatternType.SERVLET);
    }

    public ServletKeyBindingBuilder serveRegex(String regex) {
        return new ServletKeyBindingBuilderImpl(regex, UriPatternType.REGEX);
    }

    public Module buildModule() {
        return this;
    }


    //non-static inner class so it can access state of enclosing module class
    private class ServletKeyBindingBuilderImpl implements ServletKeyBindingBuilder {
        private final String uriPattern;
        private final UriPatternType uriPatternType;

        private ServletKeyBindingBuilderImpl(String uriPattern, UriPatternType uriPatternType) {
            this.uriPattern = uriPattern;
            this.uriPatternType = uriPatternType;
        }

        public ServletBindingBuilder with(Class<? extends HttpServlet> servletKey) {
            return with(Key.get(servletKey));
        }

        public ServletBindingBuilder with(Key<? extends HttpServlet> servletKey) {
            return with(servletKey, new HashMap<String, String>());
        }

        public ServletBindingBuilder with(Class<? extends HttpServlet> servletKey, Map<String, String> contextParams) {
            return with(Key.get(servletKey), contextParams);
        }

        public ServletBindingBuilder with(Key<? extends HttpServlet> servletKey, Map<String, String> contextParams) {
            servletDefinitions.add(new ServletDefinition(uriPattern,  servletKey, UriPatternType.get(uriPatternType), contextParams));

            return ServletsModuleBuilder.this;
        }
    }
}