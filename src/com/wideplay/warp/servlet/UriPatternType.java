package com.wideplay.warp.servlet;

/**
     * An enumeration of the available URI-pattern matching styles
 */
enum UriPatternType {
    SERVLET, REGEX, ANT_PATH,
    ;

    static UriPatternMatcher get(UriPatternType type) {
        switch (type) {
            case SERVLET:
                return new ServletStyleUriPatternMatcher();
            case REGEX:
                return new RegexUriPatternMatcher();

            //TODO add ant-path style pattern matcher

            default:
                return null;
        }
    }
}
