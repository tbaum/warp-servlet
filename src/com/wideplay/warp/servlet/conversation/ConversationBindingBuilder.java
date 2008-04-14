package com.wideplay.warp.servlet.conversation;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Jan 1, 2008
 * Time: 8:19:32 PM
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
public interface ConversationBindingBuilder {
    StoreBindingBuilder using(ContinuationStrategy strategy);
}
