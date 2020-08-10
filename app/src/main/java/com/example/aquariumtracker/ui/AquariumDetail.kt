package com.example.aquariumtracker.ui

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.aquariumtracker.R
import com.example.aquariumtracker.viewmodels.AquariumSelector
import com.example.aquariumtracker.viewmodels.AquariumViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class AquariumFragment : Fragment(), AquariumDeleteDialog.DeleteDialogListener {

    private val numTabs : Int = 5
    private val aqSelector: AquariumSelector by activityViewModels()
    private lateinit var aqViewModel: AquariumViewModel

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_aquarium_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

//        var aq_ID = 0
//        aqSelector.selected.observe(viewLifecycleOwner, Observer { i -> i?.let { aq_ID = i } })
        aqViewModel = ViewModelProvider(this).get(AquariumViewModel::class.java)

        val viewPager = view.findViewById<ViewPager2>(R.id.pager)
        val demoCollectionAdapter = DemoCollectionAdapter(this, numTabs)
        viewPager.adapter = demoCollectionAdapter

        val tabLayout = view.findViewById<TabLayout>(R.id.tabs_aquarium)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabLayout.getTabAt(position)?.text
            tab.icon = tabLayout.getTabAt(position)?.icon
        }.attach()

        val tabTextTable = listOf(getString(R.string.aquarium_view_overview),
                                  getString(R.string.menu_gallery),
                                  getString(R.string.aquarium_view_params),
                                  getString(R.string.aquarium_view_livestock),
                                  getString(R.string.aquarium_view_reminder))

        val tabIconTable = listOf(ResourcesCompat.getDrawable(resources, R.drawable.ic_wave, null),
                                  ResourcesCompat.getDrawable(resources, R.drawable.ic_camera, null),
                                  ResourcesCompat.getDrawable(resources, R.drawable.ic_chart, null),
                                  ResourcesCompat.getDrawable(resources, R.drawable.ic_fish, null),
                                  ResourcesCompat.getDrawable(resources, R.drawable.ic_calendar, null))

        tabLayout.getTabAt(0)?.text = tabTextTable[0]
        for (x in 0 until numTabs) {
            tabLayout.getTabAt(x)?.icon = tabIconTable[x]
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                tab.text = tabTextTable[tabLayout.selectedTabPosition]
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {
                tab.text = ""
            }
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

//        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
//        fab.setOnClickListener {
//            Log.i("fab", "aquarium")
//            findNavController().navigate(R.id.action_aquariumFragment_to_addMeasurement)
//        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_add, menu)
        inflater.inflate(R.menu.menu_delete, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_add -> {
                val tabLayout = view?.findViewById<TabLayout>(R.id.tabs_aquarium)
                val navController = findNavController()

                when (tabLayout?.selectedTabPosition) {
                    0 -> navController.navigate(R.id.action_aquariumFragment_to_addMeasurement)
                    1 -> Log.i("tab 1", "")
                    2 -> navController.navigate(R.id.action_aquariumFragment_to_addMeasurement)
                    3 -> navController.navigate(R.id.action_aquariumFragment_to_addMeasurement)
                    4 -> navController.navigate(R.id.action_aquariumFragment_to_reminderAdd)
                    else -> super.onOptionsItemSelected(item)
                }

                return true
            }
            R.id.action_delete -> {
                this.context?.let {
                    val aqDeleteDialog = AquariumDeleteDialog(it, this)
                    aqDeleteDialog.show()
                }
                return true
            }
//            R.id.action_settings -> {
//
//            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDeleteConfirmation(dialog: Dialog) {
        aqSelector.selected.value?.let {
            Log.i("AquariumDetail", it.toString())
            aqViewModel.deleteAquarium(it)
        }
        Log.i("AquariumDeleteDialog", "Deleted")
        findNavController().navigate(R.id.action_aquariumFragment_to_nav_aquarium_list)
    }


}

class DemoCollectionAdapter(fragment: Fragment, numTabs: Int) : FragmentStateAdapter(fragment) {

    private var n: Int = numTabs

    override fun getItemCount(): Int = n

    override fun createFragment(position: Int): Fragment {
        // Return a NEW fragment instance in createFragment(int)

        return when (position) {
            2 -> ParameterList()
            4 -> ReminderList()
            else -> AquariumOverview()
        }

    }
}
