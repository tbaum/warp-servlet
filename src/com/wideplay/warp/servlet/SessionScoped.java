package com.wideplay.warp.servlet;

import com.google.inject.ScopeAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <p>
 *  Scope for HTTP Sessions. Exactly the same as Guice's request scope. 
 * Not bound by default, use if you wish by adding this line to your Guice module:
 * </p>
 *
 * <pre>
 *  bindScope(SessionScoped.class, Servlets.SESSION_SCOPE);
 * </pre>
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
@ScopeAnnotation
@Retention(RetentionPolicy.RUNTIME)
public @interface SessionScoped {
}