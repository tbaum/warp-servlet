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
 *  Scope for Flash scope (2 consecutive requests). Not bound by default, use if you wish by adding
 * this line to your Guice module:
 * </p>
 *
 * <pre>
 *  bindScope(FlashScoped.class, Servlets.FLASH_SCOPE);
 * </pre>
 *
 * <p>
 *  Flash scope is semantically under session scope. When a flash scoped object
 *  requested for the first time for injection, it is created and stashed in the "flash-cache".
 *  The next time it is injected (in a new HTTP request), it is cleared from the flash-cache. Flash
 *  scope is useful when you want to implement the post-and-redirect design pattern in your web
 *  applications. 
 * </p>
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
@ScopeAnnotation
@Retention(RetentionPolicy.RUNTIME)
public @interface FlashScoped {
}