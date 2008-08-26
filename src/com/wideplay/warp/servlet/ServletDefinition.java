package com.wideplay.warp.servlet;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.wideplay.warp.servlet.uri.UriPatternMatcher;
import net.jcip.annotations.Immutable;
import org.jetbrains.annotations.Nullable;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.*;

/**
 *
 * <p>
 *
 *  An internal representation of a servlet definition mapped to a particular URI pattern. Also performs
 *  the request dispatch to that servlet. How nice and OO =)
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
        this.initParams = Collections.unmodifiableMap(new HashMap<String, String>(initParams));
    }

    boolean shouldServe(String uri) {
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
     * Wrapper around the service chain to ensure a servlet is servicing what it must and provides it with
     * a wrapped request.
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

        final boolean serve = shouldServe(((HttpServletRequest) servletRequest).getServletPath());

        //invocations of the chain end at the first matched servlet
        if (serve) {
            doService(injector, servletRequest, servletResponse);
        }


        //return false if no servlet matched (so we can proceed down to the webapp's servlets)
        return serve;
    }



    /**
     * Utility that delegates to the actual service method of the servlet wrapped with a contextual request.
     * 
     * We need to suppress deprecation coz we use HttpServletRequestWrapper, which implements deprecated
     *  API for backwards compatibility.
     */
    @SuppressWarnings({"JavaDoc", "deprecation"})
    void doService(Injector injector, final ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        //wrap request & compute correct request paths for this servlet:
        //noinspection OverlyComplexAnonymousInnerClass
        HttpServletRequest request = new HttpServletRequestWrapper((HttpServletRequest)servletRequest) {
            private String path;
            private boolean pathComputed = false;   //must use a boolean on the memo field, because null is a legal value (TODO no, it's not)

            private boolean pathInfoComputed = false;
            private String pathInfo;   //must use a boolean on the memo field, because null is a legal value


            @Override
            public String getPathInfo() {
                if (!pathInfoComputed) {
                    final int servletPathLength = getServletPath().length();
                    pathInfo = getRequestURI()
                                    .substring(getContextPath().length())
                                    .replaceAll("[/]{2,}", "/")
                                    .substring(servletPathLength);

                    //corner case, when servlet path and request path match exactly (without trailing '/'), pathinfo is null
                    if ("".equals(pathInfo) && servletPathLength != 0)
                        pathInfo = null;

                    pathInfoComputed = true;
                }

                return pathInfo;
            }

            @Override
            public String getServletPath() {
                //warp-servlet path = registered path in uri-mapping
                return computePath();
            }

            @Override
            public String getPathTranslated() {
                final String info = getPathInfo();

                return (null == info) ? null : getRealPath(info);
            }

            @Nullable
            //memoizer pattern
            private String computePath() {
                if (!pathComputed) {
                    path = patternMatcher.extractPath(pattern);
                    pathComputed = true;

                    if (null == path)
                        path = super.getServletPath();
                }

                return path;
            }
        };


        injector.getInstance(servletKey)
                .service(request, servletResponse);
    }

}