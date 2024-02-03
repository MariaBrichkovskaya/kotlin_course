package service.impl

import service.ApplicationDependencies

class SecondImplementation(
    private val impl: ApplicationDependencies = FirstImplementation()
) : ApplicationDependencies by impl {
    override fun second() = 222
}