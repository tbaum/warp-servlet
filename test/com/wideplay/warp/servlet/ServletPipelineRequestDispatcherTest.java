package com.wideplay.warp.servlet;

import com.google.inject.Injector;
import com.google.inject.Key;
import static com.wideplay.warp.servlet.uri.UriPatternType.SERVLET;
import static com.wideplay.warp.servlet.uri.UriPatternType.get;
import static org.easymock.EasyMock.*;
import org.testng.annotations.Test;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

/**
 * @author Dhanji R. Prasanna (dhanji@gmail com)
 */
public class ServletPipelineRequestDispatcherTest {
    private static final Key<HttpServlet> HTTP_SERLVET_KEY = Key.get(HttpServlet.class);
    private static final String A_KEY = "thinglyDEgintly" + new Date() + UUID.randomUUID();
    private static final String A_VALUE = ServletPipelineRequestDispatcherTest.class.toString()
            + new Date() + UUID.randomUUID();

    @Test
    public final void includeManagedServlet() throws IOException, ServletException {
        final ServletDefinition servletDefinition =
                new ServletDefinition("blah.html", Key.get(HttpServlet.class), get(SERVLET), new HashMap<String, String>());

        final Injector injector = createMock(Injector.class);
        final HttpServletRequest mockRequest = createMock(HttpServletRequest.class);

        expect(mockRequest.getAttribute(A_KEY))
                .andReturn(A_VALUE);

        final boolean[] run = new boolean[1];
        final HttpServlet mockServlet = new HttpServlet() {
            protected void service(HttpServletRequest request, HttpServletResponse httpServletResponse) throws ServletException, IOException {
                run[0] = true;

                final Object o = request.getAttribute(A_KEY);
                assert A_VALUE.equals(o) : "Wrong attrib returned - " + o;
            }
        };

        expect(injector.getInstance(HTTP_SERLVET_KEY))
                .andReturn(mockServlet);

        replay(injector, mockRequest);

        final RequestDispatcher dispatcher = new ManagedServletPipeline(Arrays.asList(servletDefinition))
                .getRequestDispatcher("blah.html", injector);

        assert null != dispatcher;
        dispatcher.include(mockRequest, createMock(HttpServletResponse.class));

        assert run[0] : "Include did not dispatch to our servlet!";

        verify(injector, mockRequest);
    }

    @Test
    public final void forwardToManagedServlet() throws IOException, ServletException {
        final ServletDefinition servletDefinition =
                new ServletDefinition("blah.html", Key.get(HttpServlet.class), get(SERVLET), new HashMap<String, String>());

        final Injector injector = createMock(Injector.class);
        final HttpServletRequest mockRequest = createMock(HttpServletRequest.class);
        final HttpServletResponse mockResponse = createMock(HttpServletResponse.class);

        expect(mockRequest.getAttribute(A_KEY))
                .andReturn(A_VALUE);

        expect(mockResponse.isCommitted())
                .andReturn(false);

        mockResponse.resetBuffer();
        expectLastCall().once();

        final boolean[] run = new boolean[1];
        final HttpServlet mockServlet = new HttpServlet() {
            protected void service(HttpServletRequest request, HttpServletResponse httpServletResponse) throws ServletException, IOException {
                run[0] = true;

                final Object o = request.getAttribute(A_KEY);
                assert A_VALUE.equals(o) : "Wrong attrib returned - " + o;
            }
        };

        expect(injector.getInstance(HTTP_SERLVET_KEY))
                .andReturn(mockServlet);

        replay(injector, mockRequest, mockResponse);

        final RequestDispatcher dispatcher = new ManagedServletPipeline(Arrays.asList(servletDefinition))
                .getRequestDispatcher("blah.html", injector);

        assert null != dispatcher;
        dispatcher.forward(mockRequest, mockResponse);

        assert run[0] : "Include did not dispatch to our servlet!";

        verify(injector, mockRequest, mockResponse);
    }


    @Test(expectedExceptions = IllegalStateException.class)
    public final void forwardToManagedServletFailureOnCommittedBuffer() throws IOException, ServletException {
        final ServletDefinition servletDefinition =
                new ServletDefinition("blah.html", Key.get(HttpServlet.class), get(SERVLET), new HashMap<String, String>());

        final Injector injector = createMock(Injector.class);
        final HttpServletRequest mockRequest = createMock(HttpServletRequest.class);
        final HttpServletResponse mockResponse = createMock(HttpServletResponse.class);

        expect(mockResponse.isCommitted())
                .andReturn(true);

        final HttpServlet mockServlet = new HttpServlet() {
            protected void service(HttpServletRequest request, HttpServletResponse httpServletResponse) throws ServletException, IOException {

                final Object o = request.getAttribute(A_KEY);
                assert A_VALUE.equals(o) : "Wrong attrib returned - " + o;
            }
        };

        replay(injector, mockRequest, mockResponse);

        final RequestDispatcher dispatcher = new ManagedServletPipeline(Arrays.asList(servletDefinition))
                .getRequestDispatcher("blah.html", injector);

        assert null != dispatcher;

        try {
            dispatcher.forward(mockRequest, mockResponse);
        } finally {
            verify(injector, mockRequest, mockResponse);
        }

    }
}
