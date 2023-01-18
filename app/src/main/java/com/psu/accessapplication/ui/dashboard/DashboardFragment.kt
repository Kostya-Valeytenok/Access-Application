package com.psu.accessapplication.ui.dashboard

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.psu.accessapplication.AnalyzeActivity
import com.psu.accessapplication.ChoosePictureActivity
import com.psu.accessapplication.databinding.FragmentDashboardBinding
import com.psu.accessapplication.extentions.launch
import com.psu.accessapplication.extentions.loadImage
import com.psu.accessapplication.extentions.registerContract
import com.psu.accessapplication.extentions.updateUI
import com.psu.accessapplication.extentions.uploadImageFromUri
import com.rainc.facerecognitionmodule.tools.mfra.model.AnalyzeResult
import com.rainc.facerecognitionmodule.tools.mfra.model.Failure
import com.rainc.facerecognitionmodule.tools.mfra.model.Successful
import com.psu.accessapplication.tools.DownloadManager
import com.psu.accessapplication.tools.EmptyInputResultContract
import com.psu.accessapplication.tools.ResultContract
import com.rainc.facerecognitionmodule.activity.FaceRecognitionActivity
import com.rainc.facerecognitionmodule.dialog.AddRecognizableBottomSheetDialog
import com.rainc.facerecognitionmodule.functions.RecognitionActivityResult
import com.rainc.facerecognitionmodule.tools.ImageCache
import com.rainc.facerecognitionmodule.tools.PersonDataSerializer.encodeToString
import com.sap.virtualcoop.mobileapp.helper.extension.invisible
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject


class DashboardFragment : Fragment() {

    val imageUrl =
        "https://www.meme-arsenal.com/memes/b08d2860e80a1e124997a1fc0b16093a.jpg"
    private var _binding: FragmentDashboardBinding? = null
    val downloadManager: DownloadManager by inject()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    var uriT: Uri? = null
    lateinit var imageLoadJob: Deferred<Result<Bitmap>>

    private val imagePicker = EmptyInputResultContract<Uri>(ChoosePictureActivity::class.java).registerContract(this) {
        binding.photoView.invisible()
        photoAnalyzer.launch(it)
    }

    private val fra = EmptyInputResultContract<Intent>(FaceRecognitionActivity::class.java).registerContract(this){
       val result = FaceRecognitionActivity.resultFromIntent(it)
       println(result)
    }

    private val photoAnalyzer = ResultContract<Uri, AnalyzeResult>(AnalyzeActivity::class.java).registerContract(this){
        launch {
            if(it is Successful){
                showFindMessage(it)
                binding.personPhoto.loadImage(it.person.personImageUrl)
            }else{
                showErrorMessage((it as Failure).massage?:"NOT FIND ERROR")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    private val faceRecognitionActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode != RESULT_OK) return@registerForActivityResult
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addPersonButton.setOnClickListener { recognition(FaceRecognitionActivity.InputParams.CaptureNewImage()) }
        binding.analyzeFaceButton.setOnClickListener {
            recognition(FaceRecognitionActivity.InputParams.Recognition())
        }
        /*  launch {
              imageLoadJob.await()
                  .onSuccess {
                      // dashboardViewModel.analyzeImage(it)

                      val person = dashboardViewModel.chekUser(it)
                      if (person == null) showErrorMessage()
                      else showFindMessage(person)
                  }
                  .onFailure { println(it) }
          }*/
    }

    fun recognition(inputParams: FaceRecognitionActivity.InputParams){
        faceRecognitionActivityLauncher.launch(FaceRecognitionActivity.getLaunchIntent(requireContext(), inputParams))
    }

    suspend fun showAddRecognizableBottomSheetDialog(personDataId:Int){
        updateUI {
            AddRecognizableBottomSheetDialog.newInstance(personDataId = personDataId)
                .show(childFragmentManager, personDataId.toString())
        }
    }

    private suspend fun showFindMessage(result: Successful) = withContext(Dispatchers.Main) {
        val person = result.person
        Toast.makeText(
            requireContext(),
            "FIND: ${person.firstName} ${person.secondName} - ${result.similarity}%", Toast.LENGTH_LONG
        ).show()
    }

    private suspend fun showErrorMessage(message: String = "NOT FIND") {
        Toast.makeText(
            requireContext(),
            "NOT FIND", Toast.LENGTH_LONG
        ).show()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
