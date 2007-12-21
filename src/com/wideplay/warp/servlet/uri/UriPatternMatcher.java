package com.wideplay.warp.servlet.uri;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Dec 19, 2007
 * Time: 5:45:10 PM
 *
 * <p>
 * A general interface for testing a URI against a URI pattern. Can be plugged
 * with regex, ant-style matching, etc.
 * </p>
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
public interface UriPatternMatcher {
    boolean matches(String uri, String pattern);
}
