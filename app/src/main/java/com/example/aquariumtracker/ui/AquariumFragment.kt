package com.example.aquariumtracker.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.aquariumtracker.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class AquariumFragment : Fragment() {

//    private lateinit var tabLayout : TabLayout
//    private var tabTextTable = listOf(R.string.aquarium_view_overview,
//                                  R.string.aquarium_view_livestock,
//                                    R.string.aquarium_view_plant)

//    private var tabTextTable = listOf(getString(R.string.aquarium_view_overview),
//                                      getString(R.string.aquarium_view_livestock),
//                                      getString(R.string.aquarium_view_plant))
//    private var tabIconTable = listOf(ResourcesCompat.getDrawable(resources, R.drawable.ic_wave, null),
//                                      ResourcesCompat.getDrawable(resources, R.drawable.ic_fish, null),
//                                      ResourcesCompat.getDrawable(resources, R.drawable.ic_plant, null))
    private var numTabs = 3

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_aquarium, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager = view.findViewById<ViewPager2>(R.id.pager)
        val demoCollectionAdapter = DemoCollectionAdapter(this)
        viewPager.adapter = demoCollectionAdapter

        val tabLayout = view.findViewById<TabLayout>(R.id.tabs_aquarium)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabLayout.getTabAt(position)?.text
            tab.icon = tabLayout.getTabAt(position)?.icon
        }.attach()

        val tabTextTable = listOf(getString(R.string.aquarium_view_overview),
                                  getString(R.string.aquarium_view_livestock),
                                  getString(R.string.aquarium_view_plant))

        val tabIconTable = listOf(ResourcesCompat.getDrawable(resources, R.drawable.ic_wave, null),
                                  ResourcesCompat.getDrawable(resources, R.drawable.ic_fish, null),
                                  ResourcesCompat.getDrawable(resources, R.drawable.ic_plant, null))

        tabLayout.getTabAt(0)?.text = getString(R.string.aquarium_view_overview)
        for (x in 0 until numTabs) {
            tabLayout.getTabAt(x)?.icon = tabIconTable[x]
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                Log.i("tab listener", tab.text.toString())
                tab.text = tabTextTable[tabLayout.selectedTabPosition]
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {
                tab.text = ""
            }
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

//        view.findViewById<Button>(R.id.button_first).setOnClickListener {
//            findNavController().navigate(R.id.action_firstFragment_to_secondFragment)
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}

class DemoCollectionAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        // Return a NEW fragment instance in createFragment(int)
        val fragment = AquariumOverviewFragment()

        return fragment
    }
}
