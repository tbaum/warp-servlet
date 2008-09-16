package com.wideplay.warp.servlet;

import javax.servlet.ServletContext;

/**
 * <p>
 * A lifecycle hook into the servlet's init and destroy events. Implementors
 * of this interface that register themselves with warp-servlet will receive
 * servlet lifecycle events on deploy and on undeploy.
 * </p>
 *
 *
 * <p>
 * This is useful in performing startup and teardown operations in your application.
 * For instance, to startup or teardown database connection pools, warp-persist
 * {@code PersistenceService} instances or just about anything else.
 *
 * The advantage of using this listener is twofold:
 * </p>
 *
 * <ul>
 *   <li>Your startup and teardown logic can benefit from dependency injection</li>
 *   <li>You don't need to embed infrastructure logic in a servlet or filter</li>
 * </ul>
 *
 * <p>
 * Register the listener in your warp-servlet Guice configuration as follows:
 * </p>
 *
 * <pre>
 *   Guice.createInjector(..., Servlets.configure()
 *      .filters()
 *      .servlets()
 *          .serve("*.html").with(MyServlet.class)
 *
 *      <b>.listen(MyLifecycleListener.class)</b>
 *      .buildModule();
 * </pre>
 *
 * <p>
 * {@code MyLifecycleListener} must be a subclass of interface {@code LifecycleListener}.
 * It may have any scope, but it is highly recommended that you use singleton scoping. This
 * allows the same instance to receive both init and destroy events.
 *
 * However, {@code MyLifecycleListener} may *not* have any web scopes
 * (request, session, conversation, etc.) as these scopes
 * exist only within the context of an HTTP request and aren't available during startup and shutdown.
 * Servlet lifecycle events are fired outside such a context (before or after any request
 * processing occurs, respectively).
 * </p>
 *
 *
 * @author Dhanji R. Prasanna (dhanji@gmail.com)
 */
public interface LifecycleListener {

    /**
     * Called by warp-servlet when the servlet container fires {@code init()} events on startup
     * of your web application (this ties into the {@code init()} invocation on warp-servlet's
     * {@code WebFilter}).
     *
     * @param context Access to the servlet context of this web application.
     *
     * @see javax.servlet.ServletContext
     * @see com.wideplay.warp.servlet.WebFilter#init(javax.servlet.FilterConfig)
     */
    void init(ServletContext context);


    /**
     * Called by warp-servlet when the servlet container fires {@code destroy()} events on shutdown
     * of your web application (this ties into the {@code destroy()} invocation on warp-servlet's
     * {@code WebFilter}).
     *
     *
     * @see com.wideplay.warp.servlet.WebFilter#destroy()
     */
    void destroy();
}
