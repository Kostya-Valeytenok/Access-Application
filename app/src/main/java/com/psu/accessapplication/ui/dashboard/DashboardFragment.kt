package com.psu.accessapplication.ui.dashboard

import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.launch
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.psu.accessapplication.ChoosePictureActivity
import com.psu.accessapplication.databinding.FragmentDashboardBinding
import com.psu.accessapplication.extentions.*
import com.psu.accessapplication.model.Person
import com.psu.accessapplication.tools.DownloadManager
import com.psu.accessapplication.tools.EmptyInputResultContract
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    var currentPhotoPath: String = ""
    val imageUrl =
        "https://avatars.mds.yandex.net/get-zen_doc/249065/pub_5ce393e9da7a8100b3ddbaf5_5ce39b780a0d8b00b24d1d5c/scale_1200"
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private var _binding: FragmentDashboardBinding? = null
    @Inject
    lateinit var downloadManager: DownloadManager

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    var uriT: Uri? = null
    lateinit var imageLoadJob: Deferred<Result<Bitmap>>

    val imagePicker =
        EmptyInputResultContract<Uri>(ChoosePictureActivity::class.java).registerContract(this) {
            launch {
                val image = uploadImageFromUri(it, requireContext())
                launch {
                    dashboardViewModel.analyzeImage(image!!)
                   /* val person = dashboardViewModel.chekUser(image)
                    if (person == null) showErrorMessage()
                    else showFindMessage(person)*/
                }
                updateUI {
                    if (image.notNull()) {
                        binding.photoView.invisible()
                    } else binding.photoView.visible()
                }
            }
        }

    fun checkBitmap(uri: Uri, bitmap: Bitmap?): Bitmap? {
        if (bitmap == null) return null
        return checkRotation(uri, bitmap)
    }

    private fun checkRotation(uri: Uri, bitmap: Bitmap): Bitmap? {
        val ei = ExifInterface(uri.path!!)
        val orientation: Int = ei.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90F)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180F)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270F)
            else -> bitmap
        }
    }

    private fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        imageLoadJob = async { return@async downloadManager.downLoadImage(imageUrl) }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding?.photoView?.setOnClickListener {
            imagePicker.launch()
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
        launch {
            dashboardViewModel.photo.collect {
                binding.imageView.setImageBitmap(it)
            }
        }
    }

    private suspend fun showFindMessage(person: Person) = withContext(Dispatchers.Main) {
        Toast.makeText(
            requireContext(),
            "FIND: ${person.firstName} ${person.secondName}", Toast.LENGTH_LONG
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
