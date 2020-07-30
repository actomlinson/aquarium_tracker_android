package com.example.aquariumtracker.database

import com.example.aquariumtracker.database.model.Aquarium
import com.example.aquariumtracker.database.model.Measurement
import com.example.aquariumtracker.database.model.Parameter
import com.example.aquariumtracker.database.model.ParameterWithMeasurements
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.*
import java.util.Calendar.DAY_OF_YEAR

class AquariumTest {

    private lateinit var aquarium: Aquarium

    @Before fun setUp() {
        val time = Calendar.getInstance().timeInMillis
        aquarium = Aquarium(aq_id = 0, size = 30.0, nickname = "TestNickName",
            startDate = time, startDateStr = time.toString()
        )
    }

}

class ParameterWithMeasurementsTests {

    private lateinit var pwms: ParameterWithMeasurements
    private val numMeasurements: Int = 5

    @Before fun setUp() {
        val p = Parameter(0, 0, 0, "TestParameter", "TestUnits")
        val cal = Calendar.getInstance()
        val measureList = List<Measurement>(numMeasurements) {i ->
            Measurement(i, 0, 0.0, Calendar.getInstance().apply { add(DAY_OF_YEAR, i) }.timeInMillis)
        }
        pwms = ParameterWithMeasurements(p, measureList)
    }


    @Test
    fun test_measurement_order() {
        val sortedMeasurements = pwms.getSortedMeasurements()
        for (m in 0 until numMeasurements-1) {
            assertTrue(sortedMeasurements[m].time > sortedMeasurements[m+1].time)
        }
    }
}