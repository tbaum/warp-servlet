package com.wideplay.warp.servlet;

import com.google.inject.Module;

/**
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 * @see com.wideplay.warp.servlet.Servlets#configure() Mapping EDSL
 */
public interface ServletBindingBuilder {
    /**
     * See EDSL in {@link Servlets#configure()} for details on usage.
     *
     * @param urlPattern A servlet style URL pattern (/* or *.html)
     * @return Returns next step in builder chain.
     */
    ServletKeyBindingBuilder serve(String urlPattern);

    /**
     * See EDSL in {@link Servlets#configure()} for details on usage.
     *
     * @param regex A regular expression to map to.
     * @return Returns next step in builder chain.
     */
    ServletKeyBindingBuilder serveRegex(String regex);

    /**
     * See EDSL in {@link Servlets#configure()} for details on usage.
     *
     * @param listener A Class of type {@link com.wideplay.warp.servlet.LifecycleListener}
     *  to register for lifecycle events.
     *
     * @return Returns next step in builder chain.
     */
//    ServletKeyBindingBuilder listen(Class<? extends LifecycleListener> listener);

    /**
     * See EDSL in {@link Servlets#configure()} for details on usage.
     *
     * @return Returns configured Guice Module. Use install() or pass into createInjector().
     */
    Module buildModule();
}
