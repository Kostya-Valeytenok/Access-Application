package com.psu.accessapplication.ui.home

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.adapters.GenericItemAdapter
import com.psu.accessapplication.AnalyzeActivity
import com.psu.accessapplication.databinding.FragmentHomeBinding
import com.rainc.facerecognitionmodule.functions.PersonData
import com.psu.accessapplication.extentions.launch
import com.psu.accessapplication.extentions.loadImage
import com.psu.accessapplication.extentions.registerContract
import com.psu.accessapplication.extentions.updateUI
import com.psu.accessapplication.extentions.uploadImageFromUri
import com.psu.accessapplication.items.PersonItem
import com.psu.accessapplication.tools.ResultContract
import com.rainc.facerecognitionmodule.activity.FaceRecognitionActivity
import com.rainc.facerecognitionmodule.dialog.AddRecognizableBottomSheetDialog
import com.rainc.facerecognitionmodule.functions.RecognitionActivityResult
import com.rainc.facerecognitionmodule.repository.PersonDataSource
import com.rainc.facerecognitionmodule.tools.ImageCache
import com.rainc.facerecognitionmodule.tools.PersonDataSerializer.encodeToString
import com.rainc.facerecognitionmodule.tools.mfra.model.AnalyzeResult
import com.rainc.facerecognitionmodule.tools.mfra.model.Failure
import com.rainc.facerecognitionmodule.tools.mfra.model.Successful
import com.rainc.viewbindingcore.item.SpaceItem
import com.rainc.viewbindingcore.tools.Dimension
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@ExperimentalStdlibApi
class HomeFragment : Fragment() {

    private val homeViewModel by viewModels<HomeViewModel>()
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val personItemAdapter = GenericItemAdapter()
    private val personAdapter = FastAdapter.with(personItemAdapter)

    private val faceRecognitionActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode != Activity.RESULT_OK) return@registerForActivityResult
        val data = it.data ?: return@registerForActivityResult

        val result: RecognitionActivityResult? = FaceRecognitionActivity.resultFromIntent(data)
        if (result is RecognitionActivityResult.NewRecognizable) {
            val newRecognizable: RecognitionActivityResult.NewRecognizable = result
            val recognizableData: FloatArray = newRecognizable.data

            val picture: Uri = newRecognizable.picturePath

            println("recognizableData: $recognizableData")
            println("picture: $picture")
            println("img: ${uploadImageFromUri(picture, requireContext())}")

            launch(Dispatchers.Default) {
                val cacheId = recognizableData.contentHashCode()

                ImageCache.imageCache[cacheId] = ImageCache.ImageCacheItem(
                    data = recognizableData,
                    photo = uploadImageFromUri(
                        picture,
                        requireContext()
                    )?.encodeToString()
                )

                showAddRecognizableBottomSheetDialog(personDataId = cacheId)
            }
        }

    }

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
        binding.addPersonButton.setOnClickListener { recognition(FaceRecognitionActivity.InputParams.CaptureNewImage()) }
        binding.analyzeFaceButton.setOnClickListener {
            recognition(FaceRecognitionActivity.InputParams.Recognition())
        }
    }

    private fun FragmentHomeBinding.initLists() {
        personList.layoutManager = LinearLayoutManager(context)
        personList.adapter = personAdapter
    }

    private fun GenericItemAdapter.setPersonItems() = launch {
        PersonDataSource.personDataViewState.collect{ personItems ->
          val items =  buildList<GenericItem> {
                add(SpaceItem(space = Dimension.Dp(32)))
                personItems.forEach {
                    add(it)
                    it.onDialogShowRequest = { this@HomeFragment.childFragmentManager }
                    add(SpaceItem(space = Dimension.Dp(12)))
                }
                add(SpaceItem(space = Dimension.Dp(80)))
            }
            updateUI { setNewList(items) }
        }
    }

    private fun List<PersonData>.getPersonList(): List<GenericItem> = buildList {
        add(SpaceItem(Dimension.Dp(8)))
        this@getPersonList.forEach {
            add(PersonItem(it))
            add(SpaceItem(Dimension.Dp(8)))
        }
    }

    private suspend fun showFindMessage(result: Successful) = withContext(Dispatchers.Main) {
        val person = result.person
        Toast.makeText(
            requireContext(),
            "FIND: ${person.firstName} ${person.secondName} - ${result.similarity}%", Toast.LENGTH_LONG
        ).show()
    }

    fun recognition(inputParams: FaceRecognitionActivity.InputParams){
        faceRecognitionActivityLauncher.launch(FaceRecognitionActivity.getLaunchIntent(requireContext(), inputParams))
    }

    private suspend fun showAddRecognizableBottomSheetDialog(personDataId:Int){
        updateUI {
            AddRecognizableBottomSheetDialog.newInstance(personDataId = personDataId)
                .show(childFragmentManager, personDataId.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
