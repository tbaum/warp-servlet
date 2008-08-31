package com.wideplay.warp.servlet;

import com.google.inject.ImplementedBy;

/**
 *
 * <p>
 * A service for users to start and end conversations. The default impl should not be
 * changed.
 *
 * Calling {@code Conversation.begin()} starts a new conversation with an auto-generated key.
 *
 * Ending a conversation is done
 * similarly. Cleanup of long-running
 * dead conversations should be done via customized implementations of {@code ConversationStore},
 * rather than via this interface. 
 * </p>
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
@ImplementedBy(UrlRewrittenConversation.class)
public interface Conversation {
    void begin();

    void end();

    /**
     * Analogous to the session URL rewriting utility in Servlet.
     *
     * @param url A url to be emitted under this conversation.
     * @return Returns the rewritten ready for sending to a browser.
     */
    String rewrite(String url);
}
