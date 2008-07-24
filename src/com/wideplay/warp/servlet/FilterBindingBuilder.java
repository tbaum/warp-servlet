package com.wideplay.warp.servlet;

import com.google.inject.Module;

/**
 *
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
public interface FilterBindingBuilder {

    /**
     *
     * Maps a filter to URIs using servlet-style pattern matching. See
     * {@link Servlets#configure()} for details.
     *
     * @param urlPattern Any Servlet-style pattern. examples: /*, /html/*, *.html, etc.
     * @return Returns the next binder step.
     *
     * @see com.wideplay.warp.servlet.Servlets#configure()
     */
    FilterKeyBindingBuilder filter(String urlPattern);

    /**
     *
     * Maps a filter to URIs using a regular expression. See {@link Servlets#configure()}
     * for details.
     *
     * @param regex Any Java-style regular expression.
     * @return Returns the next binder step.
     *
     * @see com.wideplay.warp.servlet.Servlets#configure()
     */
    FilterKeyBindingBuilder filterRegex(String regex);

    /**
     * Called as part of the EDSL to start configuring servlets. See
     * {@link Servlets#configure()} for details.
     *
     * @return Returns the next binder step.
     */
    ServletBindingBuilder servlets();

    /**
     * Must be called as the last step to create a module out of the configuration so far. See
     * {@link Servlets#configure()} for details.
     *
     * @return Returns a Guice module to be passed into
     * {@link com.google.inject.Guice#createInjector(com.google.inject.Module[])}.
     */
    Module buildModule();
}
