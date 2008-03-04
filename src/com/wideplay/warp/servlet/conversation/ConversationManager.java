package com.wideplay.warp.servlet.conversation;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.ImplementedBy;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Jan 1, 2008
 * Time: 7:32:18 PM
 *
 * <p>
 * Internal service manages the state, triggering and context of conversations.
 * The default impl of this interface should *not* be overridden for customized strategies;
 * in fact it should never be changed unless you really understand what you are doing. 
 * </p>
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
@ImplementedBy(ConversationManagerImpl.class)
public interface ConversationManager {
    //creates a new conversation with the given key
    <T> T getAndPutIfAbsent(Key<T> objectKey, Provider<T> creator, HttpServletRequest request);

    void set(String key);
}
