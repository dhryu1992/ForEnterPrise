package org.techtown.dagger2

import dagger.Component

@Component(modules = [MyModule::class])
interface MyComponent {
    val getstring: String?
}
