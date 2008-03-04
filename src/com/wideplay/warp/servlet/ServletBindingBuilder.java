package com.wideplay.warp.servlet;

import com.google.inject.Module;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Dec 19, 2007
 * Time: 1:56:44 PM
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
public interface ServletBindingBuilder {
    ServletKeyBindingBuilder serve(String urlPattern);

    ServletKeyBindingBuilder serveRegex(String regex);

    Module buildModule();
}
