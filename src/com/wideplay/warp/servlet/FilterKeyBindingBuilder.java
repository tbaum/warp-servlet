package com.wideplay.warp.servlet;

import com.google.inject.Key;

import javax.servlet.Filter;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Dec 19, 2007
 * Time: 1:44:26 PM
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
public interface FilterKeyBindingBuilder {
    FilterBindingBuilder through(Class<? extends Filter> filterKey);

    FilterBindingBuilder through(Key<? extends Filter> filterKey);

    FilterBindingBuilder through(Class<? extends Filter> dummyFilterClass, Map<String, String> contextParams);

    FilterBindingBuilder through(Key<? extends Filter> dummyFilterClass, Map<String, String> contextParams);

}
