package com.wideplay.warp.servlet;

import net.jcip.annotations.Immutable;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Dec 19, 2007
 * Time: 5:47:24 PM
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
@Immutable
class ServletStyleUriPatternMatcher implements UriPatternMatcher {
    public boolean matches(String uri, String pattern) {
        if (null == uri)
            return false;

        if (pattern.startsWith("*")) {
            return uri.endsWith(pattern.substring(1));
        } else if (pattern.endsWith("*")) {
            return uri.startsWith(pattern.substring(0, pattern.length() - 1));
        }

        throw new IllegalArgumentException("URI Pattern invalid for servlet-style pattern matching: " + pattern);
    }
}
