package com.wideplay.warp.servlet;

import com.google.inject.Module;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Dec 19, 2007
 * Time: 1:43:47 PM
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
public interface FilterBindingBuilder {
    FilterKeyBindingBuilder filter(String urlPattern);

    FilterKeyBindingBuilder filterRegex(String regex);

    ServletBindingBuilder servlets();

    Module buildModule();
}
