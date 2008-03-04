package com.wideplay.warp.servlet.conversation;

/**
 * Created with IntelliJ IDEA.
 * User: dhanji
 * Date: Jan 1, 2008
 * Time: 8:10:49 PM
 *
 * <p>
 * A public factory to bootstrap module configuration. If you use the {@code Servlets.CONVERSATION_SCOPE},
 * you *must* register a module using one of the available options provided by this class. For instance:
 *
 * </p>
 * <pre>
 *  Guice.createInjector(..., Servlets.configure().filters().buildModule(),
 *          Conversations.configure()
 *              .using(COOKIES)
 *              .in(MEMORY)
 *
 *              .buildModule()
 *   ); 
 * </pre>
 *
 * @author Dhanji R. Prasanna (dhanji gmail com)
 */
public final class Conversations {
    private Conversations() {
    }

    public static ConversationBindingBuilder configure() {
        return null;
    }
}
