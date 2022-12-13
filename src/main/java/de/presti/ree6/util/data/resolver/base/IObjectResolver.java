package de.presti.ree6.util.data.resolver.base;

/**
 * Resolver Interface used to resolve an Object Instance.
 * @param <R> Object class that should be resolved.
 * @param <T> Object instance used to resolve {@link <R>}
 */
public interface IObjectResolver<R, T> {

    /**
     * Method used to resolve the Object of {@link R}.
     * @param t The Object used to resolve.
     * @return The Object of {@link R}.
     */
    R resolve(T t);
}
