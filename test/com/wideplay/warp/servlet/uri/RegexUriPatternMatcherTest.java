package com.wideplay.warp.servlet.uri;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.wideplay.warp.servlet.uri.RegexUriPatternMatcher;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Dec 19, 2007
 * Time: 8:44:26 PM
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
public class RegexUriPatternMatcherTest {
    private static final String URIS_AND_PATTERNS = "urisAndPatterns";

    @DataProvider(name = URIS_AND_PATTERNS)
    Object[][] getUrisAndPatterns() {
        return new Object[][] {
                { "/public/login.html", "/(\\w)*/(\\w)*\\.html", true },
                { "/public/login.html", "/(\\w)*/(\\w)*\\.xml", false },
                { "/public/login.html", "[A-Za-z/.]*", true },
                { "/public/login.html", "/public/(\\w)*\\.html", true },
                { "/public/login.html", "(.)*html", true },
        };
    }

    @Test(dataProvider = URIS_AND_PATTERNS) 
    public final void regexPatternMatching(final String uri, final String pattern, boolean pass) {

        assert pass == new RegexUriPatternMatcher()
                .matches(uri, pattern) : "Expression failed to pass URI matching expectation";

    }
}
