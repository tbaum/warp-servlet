package com.wideplay.warp.servlet;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Module;
import com.wideplay.warp.servlet.uri.UriPatternType;
import net.jcip.annotations.NotThreadSafe;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Builds the guice module that binds configured filters, with their wrapper FilterDefinitions.
 * Is part of the binding EDSL.
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 * @see com.wideplay.warp.servlet.Servlets
 */
@NotThreadSafe  //intended to be confined to a single thread and disposed after injector creation
class FiltersModuleBuilder extends AbstractModule implements FilterBindingBuilder {
    private List<FilterDefinition> filterDefinitions = new ArrayList<FilterDefinition>();

    //invoked on injector config
    @Override
    protected void configure() {
        //bind these filter definitions to a config placeholder object
        bind(ManagedFilterPipeline.class)
                .toInstance(new ManagedFilterPipeline(filterDefinitions));
    }

    //the first level of the EDSL--

    public FilterKeyBindingBuilder filter(String urlPattern) {
        return new FilterKeyBindingBuilderImpl(urlPattern, UriPatternType.SERVLET);
    }

    public FilterKeyBindingBuilder filterRegex(String regex) {
        return new FilterKeyBindingBuilderImpl(regex, UriPatternType.REGEX);
    }

    public ServletBindingBuilder servlets() {
        return new ServletsModuleBuilder(this);
    }

    //shortcut method if there are no servlets to configure
    public Module buildModule() {
        return new ServletsModuleBuilder(this);
    }

    //non-static inner class so it can access state of enclosing module class
    private class FilterKeyBindingBuilderImpl implements FilterKeyBindingBuilder {
        private final String uriPattern;
        private final UriPatternType uriPatternType;

        private FilterKeyBindingBuilderImpl(String uriPattern, UriPatternType uriPatternType) {
            this.uriPattern = uriPattern;
            this.uriPatternType = uriPatternType;
        }

        public FilterBindingBuilder through(Class<? extends Filter> filterKey) {
            return through(Key.get(filterKey));
        }

        public FilterBindingBuilder through(Key<? extends Filter> filterKey) {
            return through(filterKey, new HashMap<String, String>());
        }

        public FilterBindingBuilder through(Class<? extends Filter> filterKey, Map<String, String> contextParams) {
            //careful you don't accidentally make this method recursive!! thank you IntelliJ IDEA!
            return through(Key.get(filterKey), contextParams);
        }

        public FilterBindingBuilder through(Key<? extends Filter> filterKey, Map<String, String> contextParams) {
            filterDefinitions.add(new FilterDefinition(uriPattern, filterKey, UriPatternType.get(uriPatternType), contextParams));

            return FiltersModuleBuilder.this;
        }
    }
}
