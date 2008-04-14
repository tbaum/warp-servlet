package com.wideplay.warp.servlet.conversation;

import com.google.inject.Key;
import net.jcip.annotations.NotThreadSafe;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Dec 23, 2007
 * Time: 4:47:49 PM
 *
 * A single-thread hashmap impl of a SINGLE conversation (and its set of objects).
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
@NotThreadSafe
class HashMapConversationContext implements ConversationContext {
    private final Map<String, Object> objects = new HashMap<String, Object>();

    @SuppressWarnings("unchecked")
    public <T> T get(Key<T> key) {
        return (T) objects.get(key.toString());
    }

    public <T> void put(Key<T> key, T t) {
        objects.put(key.toString(), t);
    }
}
