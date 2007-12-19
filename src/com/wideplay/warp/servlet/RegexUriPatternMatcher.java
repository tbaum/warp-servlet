package com.wideplay.warp.servlet;

import net.jcip.annotations.Immutable;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Dec 19, 2007
 * Time: 5:46:10 PM
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
@Immutable
class RegexUriPatternMatcher implements UriPatternMatcher {
    public boolean matches(String uri, String pattern) {
        return null != uri && uri.matches(pattern);
    }
}
