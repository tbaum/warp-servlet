package com.wideplay.warp.servlet.uri;

import net.jcip.annotations.Immutable;

/**
 * Matchers URIs using a given regular expression. No path info is available when using regexes.
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
@Immutable
class RegexUriPatternMatcher implements UriPatternMatcher {
    public boolean matches(String uri, String pattern) {
        return null != uri && uri.matches(pattern);
    }

    public String extractPath(String pattern) {
        return null;
    }
}
