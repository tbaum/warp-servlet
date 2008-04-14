package com.wideplay.warp.servlet;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import static org.easymock.EasyMock.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Dec 19, 2007
 * Time: 12:51:48 PM
 *
 * This test looks at a filter that dispatches to guice-managed servlets.
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
public class ServletPathInfoComplianceTest {
    private static final String CONTEXT_PATH = "/context-path";
    private static int fired;

    private static String currentExpectedPathInfo; //ugh using a static to share data across internal objects =(
    private static String currentExpectedServletPath; //ugh using a static to share data across internal objects =(
    private static final String PATHS_AND_RESULTS = "pathsAndResults";

    //****** NOTE BECAUSE OF THIS UGLY STATIC USE CASE, PLEASE ONLY EVER HAVE 1 TEST METHOD IN THIS CLASS *****

    @BeforeMethod
    public final void reset() {
        fired = 0;
        currentExpectedPathInfo = null;
        currentExpectedServletPath = null;
    }

    @DataProvider(name = PATHS_AND_RESULTS)
    Object[][] getPathsAndResults() {
        return new Object[][] {
//                { URI-PATTERN,      givenPath,        givenRequestURI,                  expectedPathInfo,          expectedServletPath  },
                { "/path/*",     "/path/index.html", "/context-path/path/index.html", "/index.html",       "/path"},
                { "/*",     "/path/index.html", "/context-path/path/index.html", null,       null},
                { "/path/index.html",   "/path/index.html", "/context-path/path/index.html", null,       "/path/index.html"},
                { "/path/index.html",   "/path/index.html", "/path/index.html", null,       "/path/index.html"},
                { "/path/*",     "/path/index.html", "/context-path/path/index.html", "/index.html",       "/path"},
                { "/path/*",     "/path/index.html", "/context-path/path/index.html", "/index.html",       "/path"},
        };
    }


    @Test(dataProvider = PATHS_AND_RESULTS)
    public final void servletPathInfoCompliance(String uriPattern, String givenServletPath, String givenRequestURI,
                                                String expectedPathInfo, String expectedServletPath)
            throws ServletException, IOException {

        currentExpectedPathInfo = expectedPathInfo;
        currentExpectedServletPath = expectedServletPath;

        final Injector injector = Guice.createInjector(Servlets.configure()
                .filters()

                .servlets()
                    .serve(uriPattern).with(TestServlet.class)

                    .buildModule()
        );

        final ManagedFilterPipeline pipeline = injector.getInstance(ManagedFilterPipeline.class);

        pipeline.initPipeline(null, injector);

        //create ourselves a mock request with test URI
        HttpServletRequest requestMock = createMock(HttpServletRequest.class);

        expect(requestMock.getServletPath())
                .andReturn(givenServletPath)   //this is what it comes in like, needs to be translated by warp-servlet
                .anyTimes();

        expect(requestMock.getRequestURI())
                .andReturn(givenRequestURI)
                .anyTimes();

        expect(requestMock.getPathInfo())       //again comes in as null, translate to the real path
                .andReturn(null)
                .anyTimes();


        //dispatch request
        replay(requestMock);

        pipeline.dispatch(injector, requestMock, null, createMock(FilterChain.class));

        pipeline.destroyPipeline(injector);

        verify(requestMock);

        assert fired == 1 : "Servlet was not fired";
    }



    @Singleton
    public static class TestServlet extends HttpServlet {
        public void init(ServletConfig filterConfig) throws ServletException {
        }

        public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException, ServletException {
            HttpServletRequest request = (HttpServletRequest)servletRequest;

            if (null == currentExpectedPathInfo)
                assert null == (request.getPathInfo()) : "Path info was wrong: " + request.getPathInfo();
            else
                assert ServletPathInfoComplianceTest.currentExpectedPathInfo.equals(request.getPathInfo()) : "Path info was wrong: " + request.getPathInfo();

            if (null == currentExpectedServletPath)
                assert null == (request.getServletPath()) : "servletPath info was wrong";
            else
                assert ServletPathInfoComplianceTest.currentExpectedServletPath.equals(request.getServletPath()) : "servletPath info was wrong";

            fired++;

        }

        public void destroy() {
        }
    }
}