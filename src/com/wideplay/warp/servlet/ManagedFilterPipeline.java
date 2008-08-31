package com.wideplay.warp.servlet;

import com.google.inject.Injector;
import com.google.inject.Singleton;
import net.jcip.annotations.Immutable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 *
 * Central routing/dispatch class handles lifecycle of managed filters, and delegates to the servlet
 *  pipeline.
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
@Immutable @Singleton
class ManagedFilterPipeline {
    private final List<FilterDefinition> filterDefinitions;

    public ManagedFilterPipeline(List<FilterDefinition> filterDefinitions) {
        this.filterDefinitions = Collections.unmodifiableList(filterDefinitions);
    }

    public void initPipeline(ServletContext servletContext, Injector injector) throws ServletException {
        //go down chain and initialize all our filters
        for (FilterDefinition filterDefinition : filterDefinitions) {
            filterDefinition.init(servletContext, injector);
        }

        //initialize servlets...
        injector.getInstance(ManagedServletPipeline.class)
                .init(servletContext, injector);
    }


    public void dispatch(Injector injector, ServletRequest request, ServletResponse response,
                         FilterChain proceedingFilterChain) throws IOException, ServletException {

        //obtain the servlet pipeline to dispatch against (we use the locator to avoid holding refs)
        final ManagedServletPipeline servletPipeline = injector.getInstance(ManagedServletPipeline.class);

        //invocation is patterned after Jetty/Tomcat's filter chain, with iterative dispatch along the pipeline
        new FilterChainInvocation(filterDefinitions, servletPipeline, proceedingFilterChain, injector)
                .doFilter(withDispatcher(request, servletPipeline, injector), response);

    }

    /**
     * Used to create an proxy that dispatches either to the warp-servlet pipeline or the regular pipeline based on
     * uri-path match. Also extracts and sets up Conversation context.
     *
     */
    @SuppressWarnings({ "JavaDoc", "deprecation" })
    private ServletRequest withDispatcher(ServletRequest servletRequest, final ManagedServletPipeline servletPipeline,
                                          final Injector injector) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        //if this URL contains a conversation id
        final int keyOffset = UrlRewrittenConversation.conversationKeyOffset(request);


        //noinspection OverlyComplexAnonymousInnerClass
        return new HttpServletRequestWrapper(request) {

            @Override @NotNull
            public RequestDispatcher getRequestDispatcher(String path) {
                final RequestDispatcher dispatcher = servletPipeline.getRequestDispatcher(path, injector);

                return (null != dispatcher) ? dispatcher : super.getRequestDispatcher(path);
            }

            @Override @Nullable
            public String getQueryString() {
                return keyOffset > -1 ?
                          super.getQueryString().substring(keyOffset)
                        : super.getQueryString();
            }
        };
    }

    public void destroyPipeline(Injector injector) {
        //destroy servlets first
        injector.getInstance(ManagedServletPipeline.class)
                .destroy(injector);

        //go down chain and destroy all our filters
        for (FilterDefinition filterDefinition : filterDefinitions) {
            filterDefinition.destroy(injector);
        }
    }
}
