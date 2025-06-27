package com.module.features.cameraqr
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.module.core.navigation.CoreNavigation
import com.module.core.ui.base.BaseFragment
import com.module.core.utils.extensions.sharedviewmodel.ShareViewModel
import com.module.features.cameraqr.databinding.FragmentCameraQrBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class CameraQrFragment : BaseFragment<FragmentCameraQrBinding, CameraQrViewModel>() {

    override val layoutId: Int
        get() = R.layout.fragment_camera_qr

    private val mViewModel: CameraQrViewModel by viewModels()
    private val sharedViewModel: ShareViewModel by activityViewModels()

    @Inject
    lateinit var mCoreNavigation: CoreNavigation

    private val CAMERA_PERMISSION_REQUEST_CODE = 1001

    var viewPager: ViewPager2? = null

    override fun getVM(): CameraQrViewModel = mViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkCameraPermission()
    }

    override fun initView() {
        super.initView()
        // Bỏ phần xử lý click button
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            startQrScanner()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startQrScanner()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Quyền camera bị từ chối, không thể quét QR code",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun startQrScanner() {
        val barcodeView = binding.barcodeScanner
        barcodeView.initializeFromIntent(requireActivity().intent)
        barcodeView.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                result?.text?.let { qrContent ->
                    val tableId = extractTableIdFromUrl(qrContent)
                    if (tableId != null) {
                        _binding?.let { binding ->
                            Toast.makeText(
                                requireContext(),
                                "Table ID: $tableId",
                                Toast.LENGTH_SHORT
                            ).show()
                            Timber.d("Table ID detected: $tableId")
                            mViewModel.setQrCodeContent(tableId)
                            binding.barcodeScanner.pause()
                            // Lưu tableId và chuyển tab tự động
                            sharedViewModel.setSelectedTableId(tableId)
                            viewPager?.let {
                                it.currentItem = 0
                                Timber.d("Auto switched to SalesFragment with Table ID: $tableId")
                            } ?: Timber.e("ViewPager is null, cannot switch to SalesFragment")
                        } ?: run {
                            Timber.w("Binding is null, cannot pause barcode scanner")
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "QR Code không chứa ID bàn hợp lệ",
                            Toast.LENGTH_SHORT
                        ).show()
                        Timber.e("Invalid QR Code URL: $qrContent")
                    }
                }
            }

            override fun possibleResultPoints(resultPoints: MutableList<com.google.zxing.ResultPoint>?) {
                // Không cần xử lý
            }
        })

        barcodeView.resume()
    }

    private fun extractTableIdFromUrl(url: String): String? {
        val regex = Regex("https://vuvanthang\\.website/user/menu/([a-f0-9]{24})")
        return regex.find(url)?.groups?.get(1)?.value
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            _binding?.barcodeScanner?.resume()
                ?: Timber.w("Binding is null, cannot resume barcode scanner")
        }
    }

    override fun onPause() {
        super.onPause()
        _binding?.barcodeScanner?.pause()
            ?: Timber.w("Binding is null, cannot pause barcode scanner")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewPager = null
    }
}