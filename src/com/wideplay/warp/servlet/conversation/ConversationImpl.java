package com.wideplay.warp.servlet.conversation;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.jcip.annotations.Immutable;

import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Dec 23, 2007
 * Time: 3:21:34 PM
 *
 * <p>
 *
 * An interface to the *current* (or potential) conversation. Inject this into your objects, pages
 * or services to start or stop the current conversation.
 *
 * This is *not* the scope. If the target object (receiving this object via injection
 * has *less* than singleton scope, I strongly recommend injecting a Provider&lt;Conversation&gt;
 * instead)
 *
 * </p>
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
@Immutable @Singleton
class ConversationImpl implements Conversation {
    private final ConversationManager conversationManager;

    @Inject
    ConversationImpl(ConversationManager conversationManager) {
        this.conversationManager = conversationManager;
    }

    public void begin() {
        conversationManager.set(UUID.randomUUID().toString());
    }

    public void begin(String key) {
        conversationManager.set(key);
    }

    public void end() {
    }

}
