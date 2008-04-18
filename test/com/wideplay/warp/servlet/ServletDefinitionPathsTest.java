package com.wideplay.warp.servlet;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.wideplay.warp.servlet.uri.UriPatternType;
import static org.easymock.EasyMock.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

/**
 * Ensure servlet compliance (CGI-style variables)
 *
 * @author Dhanji R. Prasanna (dhanji@gmail com)
 */
public class ServletDefinitionPathsTest {
    private static final String REQUESTS_PATTERNS_AND_EXPECTED_SERVLETPATHS = "__pathsAndPatterns";
    private static final String SERVLETSTYLE_REQUESTS_PATTERNS_AND_EXPECTED_PATHINFOS = "__pathsAndPatternsAndPathInfos";
    private static final String REGEX_REQUESTS_PATTERNS_AND_EXPECTED_PATHINFOS = "__pathsAndPatternsAndPathInfos_REGEX";

    @DataProvider(name = REQUESTS_PATTERNS_AND_EXPECTED_SERVLETPATHS)
    public Object[][] getServletPaths() {
        return new Object[][] {
            {  "/index.html", "*.html", "/index.html" },
            {  "/somewhere/index.html", "*.html", "/somewhere/index.html" },
            {  "/somewhere/index.html", "/*", "" },
            {  "/index.html", "/*", "" },
            {  "/", "/*", "" },
            {  "//", "/*", "" },
            {  "/////", "/*", "" },
            {  "", "/*", "" },
            {  "/thing/index.html", "/thing/*", "/thing" },
            {  "/thing/wing/index.html", "/thing/*", "/thing" },
        };
    }


    @Test(dataProvider = REQUESTS_PATTERNS_AND_EXPECTED_SERVLETPATHS)
    public final void servletPath(final String requestPath, String mapping, final String expectedServletPath)
            throws IOException, ServletException {

        Injector injector = createMock(Injector.class);
        HttpServletRequest request = createMock(HttpServletRequest.class);
        HttpServletResponse response = createMock(HttpServletResponse.class);

        final boolean[] run = new boolean[1];

        //get an instance of this servlet
        expect(injector.getInstance(Key.get(HttpServlet.class)))
                .andReturn(new HttpServlet() {

                    @Override
                    protected void service(HttpServletRequest servletRequest, HttpServletResponse httpServletResponse)
                            throws ServletException, IOException {

                        final String path = servletRequest.getServletPath();
                        assert expectedServletPath.equals(path) :
                                String.format("expected [%s] but was [%s]", expectedServletPath, path);
                        run[0] = true;
                    }
                });

        expect(request.getServletPath())
                .andReturn(requestPath);

        
        replay(injector, request);


        new ServletDefinition(mapping, Key.get(HttpServlet.class), UriPatternType.get(UriPatternType.SERVLET),
                new HashMap<String,String>())

                .doService(injector, request, response);


        assert run[0] : "Servlet did not run!";


    }


    @DataProvider(name = SERVLETSTYLE_REQUESTS_PATTERNS_AND_EXPECTED_PATHINFOS)
    public Object[][] getPathInfosForServletStyle() {
        return new Object[][] {
                //first a mapping of /*
            {  "/path/index.html", "/path", "/*", "/index.html", "" },
            {  "/path//hulaboo///index.html", "/path", "/*", "/hulaboo/index.html", "" },
            {  "/path/", "/path", "/*", "/", "" },
            {  "/path////////", "/path", "/*", "/", "" },


                //now with a servlet mapping of /thing/*
            {  "/path/thing////////", "/path", "/thing/*", "/", "/thing" },
            {  "/path/thing/stuff", "/path", "/thing/*", "/stuff", "/thing" },
            {  "/path/thing/stuff.html", "/path", "/thing/*", "/stuff.html", "/thing" },
            {  "/path/thing", "/path", "/thing/*", null, "/thing" },


                //now with *.xx style mapping
            {  "/path/thing.thing", "/path", "*.thing", null, "/thing.thing" },
            {  "/path///h.thing", "/path", "*.thing", null, "/h.thing" },
            {  "/path///...//h.thing", "/path", "*.thing", null, "/.../h.thing" },
            {  "/path/my/h.thing", "/path", "*.thing", null, "/my/h.thing" },

        };
    }

    @Test(dataProvider = SERVLETSTYLE_REQUESTS_PATTERNS_AND_EXPECTED_PATHINFOS)
    public final void pathInfoWithServletStyleMatching(final String requestUri, final String contextPath, String mapping,
                               final String expectedPathInfo,
                               final String servletPath)
            throws IOException, ServletException {

        Injector injector = createMock(Injector.class);
        HttpServletRequest request = createMock(HttpServletRequest.class);
        HttpServletResponse response = createMock(HttpServletResponse.class);

        final boolean[] run = new boolean[1];

        //get an instance of this servlet
        expect(injector.getInstance(Key.get(HttpServlet.class)))
                .andReturn(new HttpServlet() {

                    @Override
                    protected void service(HttpServletRequest servletRequest, HttpServletResponse httpServletResponse)
                            throws ServletException, IOException {

                        final String path = servletRequest.getPathInfo();

                        if (null == expectedPathInfo)
                            assert null == (path) :
                                    String.format("expected [%s] but was [%s]", expectedPathInfo, path);
                        else
                            assert expectedPathInfo.equals(path) :
                                    String.format("expected [%s] but was [%s]", expectedPathInfo, path);

                        //assert memoizer
                        //noinspection StringEquality
                        assert path == servletRequest.getPathInfo() : "memo field did not work";

                        run[0] = true;
                    }
                });

        expect(request.getRequestURI())
                .andReturn(requestUri);

        expect(request.getServletPath())
                .andReturn(servletPath)
                .anyTimes();

        expect(request.getContextPath())
                .andReturn(contextPath);


        replay(injector, request);


        new ServletDefinition(mapping, Key.get(HttpServlet.class), UriPatternType.get(UriPatternType.SERVLET),
                new HashMap<String,String>())

                .doService(injector, request, response);


        assert run[0] : "Servlet did not run!";

    }


    @DataProvider(name = REGEX_REQUESTS_PATTERNS_AND_EXPECTED_PATHINFOS)
    public Object[][] getPathInfosForRegex() {
        return new Object[][] {
                //first a mapping of /*
            {  "/path/index.html", "/path", "/(.)*", "/index.html", "" },
            {  "/path//hulaboo///index.html", "/path", "/(.)*", "/hulaboo/index.html", "" },
            {  "/path/", "/path", "/(.)*", "/", "" },
            {  "/path////////", "/path", "/(.)*", "/", "" },


                //now with a servlet mapping of /thing/*
            {  "/path/thing////////", "/path", "/thing/(.)*", "/", "/thing" },
            {  "/path/thing/stuff", "/path", "/thing/(.)*", "/stuff", "/thing" },
            {  "/path/thing/stuff.html", "/path", "/thing/(.)*", "/stuff.html", "/thing" },
            {  "/path/thing", "/path", "/thing/(.)*", null, "/thing" },


                //now with *.xx style mapping
            {  "/path/thing.thing", "/path", "(.)*\\.thing", null, "/thing.thing" },
            {  "/path///h.thing", "/path", "(.)*\\.thing", null, "/h.thing" },
            {  "/path///...//h.thing", "/path", "(.)*\\.thing", null, "/.../h.thing" },
            {  "/path/my/h.thing", "/path", "(.)*\\.thing", null, "/my/h.thing" },

        };
    }

    @Test(dataProvider = REGEX_REQUESTS_PATTERNS_AND_EXPECTED_PATHINFOS)
    public final void pathInfoWithRegexMatching(final String requestUri, final String contextPath, String mapping,
                               final String expectedPathInfo,
                               final String servletPath)
            throws IOException, ServletException {

        Injector injector = createMock(Injector.class);
        HttpServletRequest request = createMock(HttpServletRequest.class);
        HttpServletResponse response = createMock(HttpServletResponse.class);

        final boolean[] run = new boolean[1];

        //get an instance of this servlet
        expect(injector.getInstance(Key.get(HttpServlet.class)))
                .andReturn(new HttpServlet() {

                    @Override
                    protected void service(HttpServletRequest servletRequest, HttpServletResponse httpServletResponse)
                            throws ServletException, IOException {

                        final String path = servletRequest.getPathInfo();

                        if (null == expectedPathInfo)
                            assert null == (path) :
                                    String.format("expected [%s] but was [%s]", expectedPathInfo, path);
                        else
                            assert expectedPathInfo.equals(path) :
                                    String.format("expected [%s] but was [%s]", expectedPathInfo, path);

                        //assert memoizer
                        //noinspection StringEquality
                        assert path == servletRequest.getPathInfo() : "memo field did not work";

                        run[0] = true;
                    }
                });

        expect(request.getRequestURI())
                .andReturn(requestUri);

        expect(request.getServletPath())
                .andReturn(servletPath)
                .anyTimes();

        expect(request.getContextPath())
                .andReturn(contextPath);


        replay(injector, request);


        new ServletDefinition(mapping, Key.get(HttpServlet.class), UriPatternType.get(UriPatternType.REGEX),
                new HashMap<String,String>())

                .doService(injector, request, response);


        assert run[0] : "Servlet did not run!";

    }
}
