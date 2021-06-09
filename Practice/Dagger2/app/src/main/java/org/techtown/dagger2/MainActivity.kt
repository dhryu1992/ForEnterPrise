package org.techtown.dagger2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.jetbrains.annotations.TestOnly

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    public class ExampleUnitTest {
        @TestOnly
        public testHelloWorld() {
            MyComponent myComponent = DaggerMyComponent.create()

        }
    }
}