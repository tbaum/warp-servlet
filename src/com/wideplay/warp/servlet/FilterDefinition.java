package com.wideplay.warp.servlet;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.wideplay.warp.servlet.uri.UriPatternMatcher;
import net.jcip.annotations.Immutable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Dec 19, 2007
 * Time: 5:43:10 PM
 *
 * <p>
 *
 *  An internal representation of a filter definition against a particular URI pattern.
 * </p>
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
@Immutable
class FilterDefinition {
    private final String pattern;
    private final Key<? extends Filter> filterKey;
    private final UriPatternMatcher patternMatcher;
    private final Map<String, String> initParams;

    public FilterDefinition(String pattern, Key<? extends Filter> filterKey, UriPatternMatcher patternMatcher, Map<String, String> initParams) {
        this.pattern = pattern;
        this.filterKey = filterKey;
        this.patternMatcher = patternMatcher;
        this.initParams = Collections.unmodifiableMap(initParams);
    }

    private boolean shouldFilter(String uri) {
        return patternMatcher.matches(uri, pattern);
    }

    public Filter getFilter(Injector injector) {
        return injector.getInstance(filterKey);
    }

    public void init(final ServletContext servletContext, Injector injector) throws ServletException {
        //this may or may not be a singleton, but is only initialized once
        final Filter filter = injector.getInstance(filterKey);


        //initialize our filter with the configured context params and servlet context
        //noinspection OverlyComplexAnonymousInnerClass,AnonymousInnerClassWithTooManyMethods
        filter.init(new FilterConfig() {
            public String getFilterName() {
                return filterKey.toString();
            }

            public ServletContext getServletContext() {
                return servletContext;
            }

            public String getInitParameter(String s) {
                return initParams.get(s);
            }

            public Enumeration getInitParameterNames() {
                //noinspection InnerClassTooDeeplyNested,AnonymousInnerClassWithTooManyMethods
                return new Enumeration() {
                    private final Iterator<String> paramNames = initParams.keySet().iterator();

                    public boolean hasMoreElements() {
                        return paramNames.hasNext();
                    }

                    public Object nextElement() {
                        return paramNames.next();
                    }
                };
            }
        });
    }

    public void destroy(Injector injector) {
        //may or may not be singleton, upto user to work out properly
        final Filter filter = injector.getInstance(filterKey);

        filter.destroy();
    }

    public void doFilter(Injector injector, ServletRequest servletRequest, ServletResponse servletResponse, FilterChainInvocation filterChainInvocation)
            throws IOException, ServletException {

        final String path = ((HttpServletRequest) servletRequest).getServletPath();

        if (shouldFilter(path)) {
            Logger log = LoggerFactory.getLogger(FilterDefinition.class);

            log.debug("Dispatching filter: " + filterKey + " for " + path);
            injector.getInstance(filterKey)
                .doFilter(servletRequest, servletResponse, filterChainInvocation);
        }
        else
            //otherwise proceed down chain anyway
            filterChainInvocation.doFilter(servletRequest, servletResponse);
    }
}
