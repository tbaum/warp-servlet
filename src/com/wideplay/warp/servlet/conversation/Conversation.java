package com.wideplay.warp.servlet.conversation;

import com.google.inject.ImplementedBy;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Jan 1, 2008
 * Time: 7:29:13 PM
 *
 * <p>
 * A service for users to start and end conversations. The default impl should not be
 * changed.
 *
 * Calling {@code Conversation.begin()} starts a new conversation with an auto-generated key.
 * You can specify the key with an overloaded version of {@code begin(String)}. If a conversation
 * with the given key is already active, an exception is raised. Ending a conversation is done
 * similarly (but can only be done from the context of the conversation itself). Cleanup of long-running
 * dead conversations should be done via customized implementations of {@code ConversationStore},
 * rather than via this interface. 
 * </p>
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
@ImplementedBy(ConversationImpl.class)
public interface Conversation {
    void begin();

    void begin(String key);

    void end();
}
