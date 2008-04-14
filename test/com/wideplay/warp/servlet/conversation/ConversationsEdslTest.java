package com.wideplay.warp.servlet.conversation;

import org.testng.annotations.Test;
import static com.wideplay.warp.servlet.conversation.ContinuationStrategy.*;
import static com.wideplay.warp.servlet.conversation.StoreKind.*;

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
