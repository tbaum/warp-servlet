package com.wideplay.warp.servlet.uri;


/**
 * An enumeration of the available URI-pattern matching styles
 */
public enum UriPatternType {
    SERVLET, REGEX, ANT_PATH,
    ;

    public static UriPatternMatcher get(UriPatternType type) {
        switch (type) {
            case SERVLET:
                return new ServletStyleUriPatternMatcher();
            case REGEX:
                return new RegexUriPatternMatcher();

            //TODO add ant-path style pattern matcher?

            default:
                return null;
        }
    }
}
