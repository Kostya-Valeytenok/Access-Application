package com.psu.accessapplication.ui.dashboard

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.launch
import androidx.fragment.app.Fragment
import com.psu.accessapplication.AnalyzeActivity
import com.psu.accessapplication.ChoosePictureActivity
import com.psu.accessapplication.databinding.FragmentDashboardBinding
import com.psu.accessapplication.extentions.*
import com.psu.accessapplication.model.AnalyzeResult
import com.psu.accessapplication.model.Failure
import com.psu.accessapplication.model.Successful
import com.psu.accessapplication.tools.DownloadManager
import com.psu.accessapplication.tools.EmptyInputResultContract
import com.psu.accessapplication.tools.ResultContract
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    val imageUrl =
        "https://www.meme-arsenal.com/memes/b08d2860e80a1e124997a1fc0b16093a.jpg"
    private var _binding: FragmentDashboardBinding? = null
    @Inject
    lateinit var downloadManager: DownloadManager

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    var uriT: Uri? = null
    lateinit var imageLoadJob: Deferred<Result<Bitmap>>

    private val imagePicker = EmptyInputResultContract<Uri>(ChoosePictureActivity::class.java).registerContract(this) {
        binding.photoView.invisible()
        photoAnalyzer.launch(it)
    }

    private val photoAnalyzer = ResultContract<Uri,AnalyzeResult>(AnalyzeActivity::class.java).registerContract(this){
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
