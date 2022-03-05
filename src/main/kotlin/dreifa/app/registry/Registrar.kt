package dreifa.app.registry

/**
 * A standard interface for registering a group of classes in a module
 *
 * The intention is that each module will  expose one or more Registrar's
 * to its clients and that these will automatically all the ncessary
 * objects in the registry
 *
 */
interface Registrar {
    /**
     * The intention is that each module will expose one or more Registrar(s)
     * to it's clients, and that these will automatically all the necessary
     * objects in the registry by calling this method.
     *
     * The `strict` parameter is mainly intended to deal with the difference between
     * unit tests and dev, where it is probably ok to add any missing dependencies dynamically
     * and production where its probably better to fail.
     */
    fun register(registry: Registry = Registry(), strict: Boolean = false): Registry
}