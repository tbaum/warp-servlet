package com.wideplay.warp.servlet.uri;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Dec 19, 2007
 * Time: 8:44:26 PM
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
public class ServletStyleUriPatternMatcherTest {
    private static final String URIS_AND_PATTERNS = "urisAndPatterns";
    private static final String PATTERNS_AND_PATHS= "patternsANDPaths";

    @DataProvider(name = URIS_AND_PATTERNS)
    Object[][] getUrisAndPatterns() {
        return new Object[][] {
                { "/public/login.html", "/*", true },
                { "/public/login.html", "/public/*", true },
                { "/public/login.html", "*.html", true },
                { "/public/login.html", "/public/space/*", false },
                { "/public/login.html", "*.xhtml", false},
                { "/public/login.html", "/public/login.html", true },
        };
    }

    @DataProvider(name = PATTERNS_AND_PATHS)
    Object[][] getPatterns() {
        return new Object[][] {
                { "/*", ""},
                { "/public/*", "/public" },
                { "*.html", null },
                { "/public/space/*", "/public/space" },
                { "*.xhtml", null },
                { "/public/login.html", "/public/login.html" },
                { "/index.html", "/index.html" },
                { "/html/win/*", "/html/win" },
        };
    }

    @Test(dataProvider = URIS_AND_PATTERNS)
    public final void servletStyleMatches(final String uri, final String pattern, boolean pass) {

        assert pass == new ServletStyleUriPatternMatcher()
                .matches(uri, pattern) : "Expression failed to pass URI matching expectation";

    }

    @Test(dataProvider = PATTERNS_AND_PATHS)
    public final void servletStyleExtractPath(final String pattern, final String path) {
        final String extracted = new ServletStyleUriPatternMatcher()
                         .extractPath(pattern);

        if (null == path)
            assert null == extracted : "Extracted path was not as expected: " + extracted;
        else
            assert path.equals(extracted) : "Extracted path was not as expected:" + extracted;

    }
}