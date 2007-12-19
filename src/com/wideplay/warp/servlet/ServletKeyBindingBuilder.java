package com.wideplay.warp.servlet;

import com.google.inject.Key;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Dec 19, 2007
 * Time: 2:02:13 PM
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
public interface ServletKeyBindingBuilder {
    ServletBindingBuilder with(Class<? extends HttpServlet> servletKey);

    ServletBindingBuilder with(Key<? extends HttpServlet> servletKey);

    ServletBindingBuilder with(Class<? extends HttpServlet> servletKey, Map<String, String> contextParams);

    ServletBindingBuilder with(Key<? extends HttpServlet> servletKey, Map<String, String> contextParams);
}
