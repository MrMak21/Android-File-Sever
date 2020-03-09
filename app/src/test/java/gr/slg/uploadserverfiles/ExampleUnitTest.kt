package gr.slg.uploadserverfiles

import gr.slg.uploadserverfiles.Retrofit.Repo
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    lateinit var repo : Repo


    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun check_file() {
        repo = Repo()
        var result = repo.selectfile(null)
        assertNull(result)
    }
}
