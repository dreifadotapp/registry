package dreifa.app.registry

import java.util.HashMap

/**
 * Simple registry for basic DI
 */
@Suppress("UNCHECKED_CAST")
class Registry {
    private var registry: MutableMap<Class<*>, Any> = HashMap()

    constructor()

    constructor(`object`: Any) {
        registry[`object`.javaClass] = `object`
    }

    constructor(vararg objects: Any) {
        for (o in objects) {
            registry[o.javaClass] = o
        }
    }

    // only used byu clone() method
    private constructor(reg: Map<Class<*>, Any>) {
        registry = HashMap(reg)
    }

    /*
      Store an object in the registry
     */
    fun store(`object`: Any): Registry {
        registry[`object`.javaClass] = `object`
        return this
    }

    /*
      Store an object in the registry, checking to see if there is already an
      object that is retrievable via the supplied `clazz` (i.e. get(clazz) will
      succeed), in which case that instance is replaced
     */
    fun storeOrReplace(`object`: Any, clazz: Class<*>): Registry {
        try {
            val existing = get(clazz)
            val entry = registry.entries.single() { it.value == existing }
            registry.remove(entry.key)
        } catch (ignoreMe: NotFoundException) {
        }

        registry[`object`.javaClass] = `object`
        return this
    }

    @Deprecated(message = "use storeOrReplace")
    fun store(`object`: Any, clazz: Class<*>): Registry {
        return storeOrReplace(`object`, clazz)
    }


    /*
      Find an object by class or interface name. The name
      must be fully qualified.
     */
    fun get(clazzName: String): Any {
        val matches = HashSet<Any>()

        if (Class.forName(clazzName).isInterface) {
            val clazz = Class.forName(clazzName)
            for ((_, value) in registry) {

                if (clazz.isAssignableFrom(value.javaClass)) {
                    matches.add(value)
                }
            }
        } else {
            for ((_, value) in registry) {
                if (value::class.qualifiedName == clazzName) {
                    matches.add(value)
                }

                // check subclasses
                var superClazz = value.javaClass.superclass as Class<Any>
                while (superClazz.name != "java.lang.Object") {
                    if (clazzName == superClazz.name) {
                        matches.add(value)
                    }
                    superClazz = superClazz.superclass as Class<Any>
                }
            }

        }
        if (matches.isEmpty()) throw NotFoundException(clazzName)
        if (matches.size > 1) throw DuplicateException(clazzName, matches as Set<Any>)
        return matches.single()
    }

    /*
      Find an object by class or interface.
     */
    fun <T> get(clazz: Class<T>): T {
        val matches = HashSet<T>()
        if (registry.containsKey(clazz)) {
            matches.add((registry[clazz] as T))
        }

        for ((_, value) in registry) {

            if (clazz.isInterface) {
                if (clazz.isAssignableFrom(value.javaClass)) {
                    matches.add(value as T)
                }
            }

            // check subclasses
            var superClazz = value.javaClass.superclass as Class<Any>
            while (superClazz.name != "java.lang.Object") {
                if (clazz.name == superClazz.name) {
                    matches.add(value as T)
                }
                superClazz = superClazz.superclass as Class<Any>
            }
        }

        if (matches.isEmpty()) throw NotFoundException(clazz.name)
        if (matches.size > 1) throw DuplicateException(clazz.name, matches as Set<Any>)
        return matches.single()
    }

    fun <T> getOrElse(clazz: Class<T>, other: T): T {
        return try {
            get(clazz)
        } catch (ex: RuntimeException) {
            other
        }
    }

    fun <T> getOrNull(clazz: Class<T>): T? {
        return try {
            get(clazz)
        } catch (ex: RuntimeException) {
            null
        }
    }

    fun <T> contains(clazz: Class<T>): Boolean {
        return try {
            get(clazz)
            true
        } catch (ex: RuntimeException) {
            false
        }
    }

    fun <T> missing(clazz: Class<T>): Boolean {
        return !contains(clazz)
    }

    fun <T> getOrElse(clazzName: String, other: Any): Any {
        return try {
            get(clazzName)
        } catch (ex: RuntimeException) {
            other
        }
    }

    fun <T> getOrNull(clazzName: String): Any? {
        return try {
            get(clazzName)
        } catch (ex: RuntimeException) {
            null
        }
    }

    fun contains(clazzName: String): Boolean {
        return try {
            get(clazzName)
            true
        } catch (ex: RuntimeException) {
            false
        }
    }

    fun <T> missing(clazzName: String): Boolean {
        return !contains(clazzName)
    }

    fun flush() {
        registry = HashMap<Class<*>, Any>()
    }

    /**
     * Make a copy of the original registry with the stored
     * objects overridden.
     *
     * @param objects
     * @return
     */
    fun clone(): Registry {
        val cloned = HashMap<Class<*>, Any>(registry.size)
        registry.entries.forEach { cloned.put(it.key, it.value) }
        return Registry(cloned)
    }

    class NotFoundException(clazzName: String) :
        RuntimeException("Class or Interface `$clazzName` in not in the registry")

    class DuplicateException(clazzName: String, matches: Set<Any>) :
        RuntimeException("Class or Interface `$clazzName` is in the registry multiple times - ${matches.joinToString()}\"")

}


