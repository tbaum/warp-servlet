package com.wideplay.warp.servlet;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.wideplay.warp.servlet.uri.UriPatternMatcher;
import net.jcip.annotations.Immutable;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
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
 *  An internal representation of a servlet definition against a particular URI pattern, also performs
 *  the request dispatch.
 * </p>
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
@Immutable
class ServletDefinition {
    private final String pattern;
    private final Key<? extends HttpServlet> servletKey;
    private final UriPatternMatcher patternMatcher;
    private final Map<String, String> initParams;

    public ServletDefinition(String pattern, Key<? extends HttpServlet> servletKey, UriPatternMatcher patternMatcher, Map<String, String> initParams) {
        this.pattern = pattern;
        this.servletKey = servletKey;
        this.patternMatcher = patternMatcher;
        this.initParams = Collections.unmodifiableMap(initParams);
    }

    private boolean shouldServe(String uri) {
        return patternMatcher.matches(uri, pattern);
    }

    public void init(final ServletContext servletContext, Injector injector) throws ServletException {
        //this may or may not be a singleton, but is only initialized once
        final HttpServlet httpServlet = injector.getInstance(servletKey);

        //initialize our servlet with the configured context params and servlet context
        //noinspection OverlyComplexAnonymousInnerClass,AnonymousInnerClassWithTooManyMethods
        httpServlet.init(new ServletConfig() {
            public String getServletName() {
                return servletKey.toString();
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
        final HttpServlet httpServlet = injector.getInstance(servletKey);

        httpServlet.destroy();
    }

    /**
     *
     * @param injector The Guice Injector
     * @param servletRequest Current HTTP request
     * @param servletResponse Current HTTP response
     * @return Returns true if this servlet triggered for the given request. Or false if warp-servlet
     *  should continue dispatching down the servlet pipeline.
     *
     * @throws IOException If thrown by underlying servlet
     * @throws ServletException If thrown by underlying servlet
     */
    public boolean service(Injector injector, ServletRequest servletRequest, ServletResponse servletResponse)
            throws IOException, ServletException {

        final boolean serve = shouldServe(((HttpServletRequest) servletRequest).getRequestURI());

        //invocations of the chain end at the first matched servlet
        if (serve)
            injector.getInstance(servletKey)
                .service(servletRequest, servletResponse);


        //return false if no servlet matched (so we can proceed down the to the webapp's servlets)
        return serve;
    }
}