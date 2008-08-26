package com.wideplay.warp.servlet.uri;

/**
 * 
 * <p>
 * A general interface for testing a URI against a URI pattern. Can be plugged
 * with regex, servlet-style matching, etc.
 * </p>
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
public interface UriPatternMatcher {
    /**
     *
     * @param uri A contextual aka relative Request URI (not a complete one).
     * @param pattern A String containing some pattern that this service can match for
     * @return Returns true if the uri matches the pattern.
     */
    boolean matches(String uri, String pattern);

    /**
     *
     * @param pattern A String containing some pattern that this service can match for
     * @return Returns a canonical servlet path from this pattern. For instance, if the pattern is
     *  {@code /home/*} then the path extracted will be {@code /home}.
     *  Each pattern matcher implementation must decide and publish what a canonical path represents.
     *
     *  Note: This only works for the servlet-style pattern matcher.
     *
     */
    String extractPath(String pattern);
}
