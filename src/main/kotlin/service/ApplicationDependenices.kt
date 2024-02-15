package service

interface ApplicationDependencies : AutoCloseable {
    fun first(): Int
    fun second(): Int
    fun third(): Int
    override fun close() {
        println("Closing ApplicationDependencies")
    }
}