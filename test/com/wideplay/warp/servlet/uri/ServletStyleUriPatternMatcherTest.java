package com.wideplay.warp.servlet.uri;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.wideplay.warp.servlet.uri.ServletStyleUriPatternMatcher;

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

    @Test(dataProvider = URIS_AND_PATTERNS)
    public final void regexPatternMatching(final String uri, final String pattern, boolean pass) {

        assert pass == new ServletStyleUriPatternMatcher()
                .matches(uri, pattern) : "Expression failed to pass URI matching expectation";

    }
}