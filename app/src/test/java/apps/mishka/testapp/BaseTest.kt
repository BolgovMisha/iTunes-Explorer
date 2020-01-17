package apps.mishka.testapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Rule

open class BaseTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    fun waitForAsyncCodeExecuted() = runBlocking {
        delay(1000)
    }
}