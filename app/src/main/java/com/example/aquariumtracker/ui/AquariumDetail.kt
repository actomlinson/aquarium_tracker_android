package com.example.aquariumtracker.ui

import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.aquariumtracker.R
import com.example.aquariumtracker.database.model.Image
import com.example.aquariumtracker.ui.viewmodel.AquariumSelector
import com.example.aquariumtracker.ui.viewmodel.AquariumViewModel
import com.example.aquariumtracker.ui.viewmodel.ImageViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import java.util.*

class AquariumDetail : Fragment(), AquariumDeleteDialog.DeleteDialogListener {

    private val numTabs: Int = 4
    private val aqSelector: AquariumSelector by activityViewModels()
    private lateinit var aqViewModel: AquariumViewModel
    private val imageViewModel: ImageViewModel by activityViewModels()

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

        aqViewModel = ViewModelProvider(this).get(AquariumViewModel::class.java)
        //imageViewModel = ViewModelProvider(this).get(ImageViewModel::class.java)
        Log.i("AquariumDetail", imageViewModel.toString())

        val viewPager = view.findViewById<ViewPager2>(R.id.pager)
        val demoCollectionAdapter = DemoCollectionAdapter(this, numTabs)
        viewPager.adapter = demoCollectionAdapter

        val tabLayout = view.findViewById<TabLayout>(R.id.tabs_aquarium)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabLayout.getTabAt(position)?.text
            tab.icon = tabLayout.getTabAt(position)?.icon
        }.attach()

        val tabTextTable = listOf(
            getString(R.string.aquarium_view_overview),
            getString(R.string.menu_gallery),
            getString(R.string.aquarium_view_params),
            //getString(R.string.aquarium_view_livestock),
            getString(R.string.aquarium_view_reminder)
        )

        val tabIconTable = listOf(
            ResourcesCompat.getDrawable(resources, R.drawable.ic_wave, null),
            ResourcesCompat.getDrawable(resources, R.drawable.ic_camera, null),
            ResourcesCompat.getDrawable(resources, R.drawable.ic_chart, null),
            //ResourcesCompat.getDrawable(resources, R.drawable.ic_fish, null),
            ResourcesCompat.getDrawable(resources, R.drawable.ic_calendar, null)
        )

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
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_add, menu)
        inflater.inflate(R.menu.menu_delete, menu)
        inflater.inflate(R.menu.menu_gallery, menu)
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
                    1 -> dispatchTakePictureIntent()//getImagesFromGallery()
                    2 -> navController.navigate(R.id.action_aquariumFragment_to_addMeasurement)
                    //4 -> navController.navigate(R.id.action_aquariumFragment_to_addMeasurement)
                    3 -> navController.navigate(R.id.action_aquariumFragment_to_reminderAdd)
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
            R.id.action_gallery -> {
                getImagesFromGallery()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val REQUEST_GET_IMAGES = 1
        private const val REQUEST_TAKE_PHOTO = 2
    }

    /**
     * All selected documents are returned to the calling application with persistable read
     * and write permission grants. If you want to maintain access to the documents across
     * device reboots, you need to explicitly take the persistable permissions using
     * ContentResolver#takePersistableUriPermission(Uri, int).
     * */

    private fun getImagesFromGallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply{
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivityForResult(intent, REQUEST_GET_IMAGES)
        }
    }

    private fun getGalleryUri(): Uri? {
        // Add a specific media item.
        val resolver = requireContext().applicationContext.contentResolver
        val photoCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val cal = Calendar.getInstance()
        val newPhotoDetails = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "AquariumTracker/" + cal.timeInMillis.toString())
        }
        return resolver.insert(photoCollection, newPhotoDetails)
    }

    private fun dispatchTakePictureIntent() {
        val photoURI = getGalleryUri()
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
        }
        aqSelector.selected.observe(viewLifecycleOwner, Observer {
            lifecycleScope.launch {
                imageViewModel.insert(Image(im_id = 0, aq_id = 1, uri = photoURI.toString()))
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_GET_IMAGES && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val count = data.clipData?.itemCount
                if (count != null && data.clipData != null) {
                    for (i in 0 until count) {
                        val uri = data.clipData?.getItemAt(i)?.uri
                        aqSelector.selected.observe(viewLifecycleOwner, Observer {
                            lifecycleScope.launch {
                                imageViewModel.insert(Image(im_id = 0, aq_id = it, uri = uri.toString()))
                            }
                        })
                    }
                }
            }
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
            1 -> GalleryDetail()
            2 -> ParameterDetail()
            3 -> ReminderList()
            else -> AquariumOverview()
        }

    }
}
