package org.techtown.dagger2;

import dagger.Component;
import dagger.Provides;

public class MyModule {
    @Provides
    String provideHelloWorld() {
        return "Hello, World!";
    }
}

