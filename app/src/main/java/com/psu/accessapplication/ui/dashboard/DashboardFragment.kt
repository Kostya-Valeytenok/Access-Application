package com.psu.accessapplication.ui.dashboard

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.psu.accessapplication.databinding.FragmentDashboardBinding
import com.psu.accessapplication.extentions.*
import com.psu.accessapplication.model.Person
import com.psu.accessapplication.tools.DownloadManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    val imageUrl =
        "https://avatars.mds.yandex.net/get-zen_doc/249065/pub_5ce393e9da7a8100b3ddbaf5_5ce39b780a0d8b00b24d1d5c/scale_1200"
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private var _binding: FragmentDashboardBinding? = null
    @Inject lateinit var downloadManager: DownloadManager
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var imageLoadJob: Deferred<Result<Bitmap>>

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
        launch {
            imageLoadJob.await()
                .onSuccess {
                    // dashboardViewModel.analyzeImage(it)

                    val person = dashboardViewModel.chekUser(it)
                    if (person == null) showErrorMessage()
                    else showFindMessage(person)
                }
                .onFailure { println(it) }
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
