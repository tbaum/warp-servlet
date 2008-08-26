package com.wideplay.warp.servlet;

import com.google.inject.Key;

import javax.servlet.http.HttpServlet;
import java.util.Map;

/**
 *
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 * @see com.wideplay.warp.servlet.Servlets#configure() Mapping EDSL
 */
public interface ServletKeyBindingBuilder {
    ServletBindingBuilder with(Class<? extends HttpServlet> servletKey);

    ServletBindingBuilder with(Key<? extends HttpServlet> servletKey);

    ServletBindingBuilder with(Class<? extends HttpServlet> servletKey, Map<String, String> contextParams);

    ServletBindingBuilder with(Key<? extends HttpServlet> servletKey, Map<String, String> contextParams);
}
