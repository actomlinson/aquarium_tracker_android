package com.example.aquariumtracker


import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.aquariumtracker.database.AppDatabase
import com.example.aquariumtracker.database.dao.AquariumDAO
import com.example.aquariumtracker.database.dao.ParameterDAO
import com.example.aquariumtracker.database.model.Aquarium
import com.example.aquariumtracker.database.model.Parameter
import com.example.aquariumtracker.utilities.getValue
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

/**
 * test that when you delete something from the database that all it's children columns also get
 * deleted (like if an aquarium gets deleted that all params, measurements do too
 *
 *  test that default params get created correctly
 *
 * */



/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    private lateinit var database: AppDatabase
    private lateinit var aquariumDAO: AquariumDAO
    private lateinit var parameterDAO: ParameterDAO
    private val calendar = Calendar.getInstance()
    private val aqA = Aquarium(0, "A", 1.0, "-", calendar.timeInMillis)
    private val aqB = Aquarium(0, "B", 10.0, "-", calendar.timeInMillis)
    private val aqC = Aquarium(0, "C", 5.0, "-", calendar.timeInMillis)
    private lateinit var aqIDList: List<Long>

    @Before fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        aquariumDAO = database.aquariumDao()
        parameterDAO = database.parameterDao()

        runBlocking {
            aqIDList = aquariumDAO.insertAll(listOf(aqA, aqB, aqC))
        }
        val paramA = Parameter(param_id = 0, p_order = 0, aq_id = aqIDList[0], name = "", units = "")
        runBlocking { parameterDAO.insert(paramA) }

    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun testAquariumIDsSaved() {
        assertEquals(aqIDList.size, 3)
        val aqList = getValue(aquariumDAO.getAquariumList())
        assertThat(aqList?.get(0)?.aq_id, equalTo(aqIDList[0]))
        assertThat(aqList?.get(1)?.aq_id, equalTo(aqIDList[1]))
        assertThat(aqList?.get(2)?.aq_id, equalTo(aqIDList[2]))
    }

    @Test
    fun testGetAquarium() {
        assertThat(getValue(aquariumDAO.getAquarium(aqIDList[0]))?.nickname, equalTo("A"))
    }

    @Test
    fun testAquariumsGetUniqueIDs() {
        for (id1 in aqIDList.indices) {
            for (id2 in aqIDList.indices) {
                if (id1 != id2) {
                    assertNotSame(aqIDList[id1], aqIDList[id2])
                }
            }
        }
    }

    @Test
    fun testDeleteAquarium() {
        runBlocking {
            aquariumDAO.deleteAquarium(aqIDList[0])
        }
        assertNull(getValue(aquariumDAO.getAquarium(aqIDList[0])))
    }

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.aquariumtracker", appContext.packageName)
    }
}