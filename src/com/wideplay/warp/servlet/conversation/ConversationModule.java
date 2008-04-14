package com.wideplay.warp.servlet.conversation;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Singleton;
import net.jcip.annotations.NotThreadSafe;
import org.jetbrains.annotations.NotNull;

/**
 * @author Dhanji R. Prasanna (dhanji@gmail com)
 */
@NotThreadSafe
class ConversationModule extends AbstractModule implements ConversationBindingBuilder, StoreBindingBuilder, ConversationModuleBuilder {
    private ContinuationStrategy strategy;
    private StoreKind kind;

    protected void configure() {
        switch (strategy) {
            case COOKIES:
                bind(Continuation.class).to(CookieContinuation.class).in(Singleton.class);
                break;
            default:
                throw new IllegalArgumentException("Unknown continuation strategy requested: " + strategy);
        }

        switch (kind) {
            case MEMORY:
                bind(ConversationStore.class).to(SimpleSingletonConversationStore.class).in(Singleton.class);
                break;
            default:
                throw new IllegalArgumentException("Unknown store kind requested: " + kind);
        }
        
    }

    public StoreBindingBuilder using(@NotNull ContinuationStrategy strategy) {
        this.strategy = strategy;

        return this;
    }

    public ConversationModuleBuilder in(@NotNull StoreKind kind) {
        this.kind = kind;

        return this;
    }

    public Module buildModule() {
        return this;
    }
}
