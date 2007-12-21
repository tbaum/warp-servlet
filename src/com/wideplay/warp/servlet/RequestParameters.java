package com.wideplay.warp.servlet;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Dec 21, 2007
 * Time: 2:13:22 PM
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
@BindingAnnotation
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestParameters {
}
