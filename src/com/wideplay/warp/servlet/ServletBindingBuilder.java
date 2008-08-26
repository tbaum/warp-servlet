package com.wideplay.warp.servlet;

import com.google.inject.Module;

/**
 * 
 * @author Dhanji R. Prasanna (dhanji gmail com)
 * @see com.wideplay.warp.servlet.Servlets#configure() Mapping EDSL
 */
public interface ServletBindingBuilder {
    ServletKeyBindingBuilder serve(String urlPattern);

    ServletKeyBindingBuilder serveRegex(String regex);

    Module buildModule();
}
