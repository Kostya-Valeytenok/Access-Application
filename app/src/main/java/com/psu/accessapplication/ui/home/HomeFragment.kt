package com.psu.accessapplication.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.adapters.GenericItemAdapter
import com.psu.accessapplication.databinding.FragmentHomeBinding
import com.rainc.facerecognitionmodule.functions.FaceRecognition
import com.rainc.facerecognitionmodule.functions.PersonData
import com.psu.accessapplication.extentions.launch
import com.psu.accessapplication.extentions.updateUI
import com.psu.accessapplication.items.PersonItem
import com.psu.accessapplication.items.SpaceItem
import com.psu.accessapplication.tools.Dimension
import com.psu.accessapplication.tools.PersonDataCache

@ExperimentalStdlibApi
class HomeFragment : Fragment() {

    private val homeViewModel by viewModels<HomeViewModel>()
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val personItemAdapter = GenericItemAdapter()
    private val personAdapter = FastAdapter.with(personItemAdapter)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.initLists()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        personItemAdapter.setPersonItems()
    }

    private fun FragmentHomeBinding.initLists() {
        personList.layoutManager = LinearLayoutManager(context)
        personList.adapter = personAdapter
    }

    private fun GenericItemAdapter.setPersonItems() = launch {
        PersonDataCache.cache.collect { persons ->
            val personsList = (FaceRecognition.RECOGNIZABLE_FACES as List<PersonData>).getPersonList()
            updateUI { setNewList(personsList) }
        }
    }

    private fun List<PersonData>.getPersonList(): List<GenericItem> = buildList {
        add(SpaceItem(Dimension.Dp(8)))
        this@getPersonList.forEach {
            add(PersonItem(it))
            add(SpaceItem(Dimension.Dp(8)))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
