package de.presti.ree6.util.data.resolver.base;

/**
 * Resolver Interface used to resolve a Class Instance.
 * @param <R> The Class to resolve.
 */
public interface IClassResolver<R> {

    /**
     * Method used to resolve the Class of {@link R}.
     *
     * @return The Class of {@link R}.
     */
    R resolveClass();

}
