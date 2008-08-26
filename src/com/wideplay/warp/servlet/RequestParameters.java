package com.wideplay.warp.servlet;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * <p>
 * Use this to inject a map of request parameters. This works exactly
 * like guice-servlet's request parameter support. For example:
 * </p>
 *
 * <code>
 * @Inject @RequestParameters Map<String, String[]> params;
 * </code>
 *
 * <p>
 * Note that bound parameters *must* be an array of Strings (even though they
 * will typically only contain one element). This is because each parameter may
 * have multiple values (example: a multi-select checkbox group).
 * </p>
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
@BindingAnnotation
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestParameters {
}
