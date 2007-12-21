package com.wideplay.warp.servlet;

import com.google.inject.ScopeAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Dec 19, 2007
 * Time: 2:30:59 PM
 *
 * <p>
 *  Scope for Requests. Not bound by default, use if you wish by adding
 * this line to your Guice module:
 * </p>
 *
 * <pre>
 *  bindScope(RequestScoped.class, Servlets.REQUEST_SCOPE);
 * </pre>
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
@ScopeAnnotation
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestScoped {
}
