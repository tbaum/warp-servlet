package com.wideplay.warp.servlet;

import com.google.inject.Key;

import javax.servlet.Filter;
import java.util.Map;

/**
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 * @see com.wideplay.warp.servlet.Servlets#configure() Mapping EDSL
 */
public interface FilterKeyBindingBuilder {
    FilterBindingBuilder through(Class<? extends Filter> filterKey);

    FilterBindingBuilder through(Key<? extends Filter> filterKey);

    FilterBindingBuilder through(Class<? extends Filter> dummyFilterClass, Map<String, String> contextParams);

    FilterBindingBuilder through(Key<? extends Filter> dummyFilterClass, Map<String, String> contextParams);

}
