package com.wideplay.warp.servlet.conversation;

import static com.wideplay.warp.servlet.conversation.ContinuationStrategy.COOKIES;
import static com.wideplay.warp.servlet.conversation.StoreKind.MEMORY;
import org.testng.annotations.Test;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Jan 1, 2008
 * Time: 8:16:57 PM
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
public class ConversationsEdslTest {
    @Test
    public final void edslTest() {
        Conversations.configure()
                .using(COOKIES)
                .in(MEMORY)
//
                .buildModule();
    }
}
