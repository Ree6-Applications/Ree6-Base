package de.presti.ree6.util.data.resolver.base;

import java.util.function.Consumer;

/**
 * Resolver Interface used to resolve a Consumer.
 */
public interface IConsumerResolver<T> {

    /**
     * Method used to resolve the Consumer.
     */
    Consumer<Void> resolve(T t);

}
