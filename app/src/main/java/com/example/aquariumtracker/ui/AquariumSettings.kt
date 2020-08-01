package com.example.aquariumtracker.ui

import android.os.Bundle
import android.view.*
import android.widget.CalendarView
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.aquariumtracker.R
import com.example.aquariumtracker.database.model.Aquarium
import com.example.aquariumtracker.viewmodels.AquariumViewModel
import com.example.aquariumtracker.viewmodels.ParameterViewModel
import kotlinx.coroutines.launch

class AquariumSettings : Fragment() {

    private lateinit var aqViewModel: AquariumViewModel
    private lateinit var paramViewModel: ParameterViewModel

    private lateinit var aqNameInput: EditText
    private lateinit var aqSizeInput: EditText
    private lateinit var aqDateInput: CalendarView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_aquarium_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        aqViewModel = ViewModelProvider(this).get(AquariumViewModel::class.java)
        paramViewModel = ViewModelProvider(this).get(ParameterViewModel::class.java)
        aqNameInput = view.findViewById<EditText>(R.id.name_input)
        aqSizeInput = view.findViewById<EditText>(R.id.size_input)
        aqDateInput = view.findViewById<CalendarView>(R.id.calendarView)
    }

    private fun saveAquarium() {
        val aq = Aquarium(aq_id = 0, nickname = aqNameInput.text.toString(),
            size = aqSizeInput.text.toString().toDouble(),
            startDate = aqDateInput.date, startDateStr = aqDateInput.date.toString())

        lifecycleScope.launch {
            val newAqID = aqViewModel.insert(aq)
            paramViewModel.createDefaultParametersForAquarium(newAqID.toInt())
        }
//        newAqID.observe(viewLifecycleOwner, Observer {
//            paramViewModel.createDefaultParametersForAquarium(it.toInt())
//        })
        findNavController().navigate(R.id.action_aquariumSettings_to_nav_aquarium_list)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_save, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_save -> {
                saveAquarium()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}