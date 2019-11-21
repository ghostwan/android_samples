package com.ghostwan.sample.geofencing

import com.ghostwan.sample.geofencing.utils.elseNull
import com.ghostwan.sample.geofencing.utils.ifNotNull
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val test: String? = null
        test?.ifNotNull { println("I'm not null: $test") } ?: elseNull { println("I'm null!") }
    }
}
