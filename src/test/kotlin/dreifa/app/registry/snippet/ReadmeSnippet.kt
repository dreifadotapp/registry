package dreifa.app.registry.snippet

import dreifa.app.registry.Registry

// snippets of code for the README

interface Red
interface Green
interface Blue

class RedThing : Red {}
class BlueThing : Blue {}
open class GreenThing : Green {}

// service with constructor params
class FooService (private val red: Red, private val green : Green, private val blueThing: BlueThing) {}

val foo = FooService(RedThing(), GreenThing(), BlueThing())

// service with registry
class FooService2(registry: Registry) {
    private val red = registry.get(Red::class.java)
    private val green = registry.get(Green::class.java)
    private val blueThing = registry.get(BlueThing::class.java)
}

val reg = Registry().store(RedThing()).store(GreenThing()).store(BlueThing())
val foo2 = FooService2(reg)

interface Square
interface Circle

class GreenSquare : GreenThing(), Square {}
class GreenCircle : GreenThing(), Circle {}
class ASquare : Square {}


val reg2 = reg.store(GreenSquare()).store(GreenCircle()).store(ASquare())

//reg2.store(GreenSquare()).store(GreenCircle())

// fine, only on Circe
val circle = reg.get(Circle::class.java)

// fine, only one blue thing
val blueThing = reg.get(BlueThing::class.java)

// fails -what type of Square?
val square = reg.get(Square::class.java)

// fails -what type of GreenThing?
val greenThing = reg.get(GreenThing::class.java)

data class DatabaseConnectionString(val connection : String)

