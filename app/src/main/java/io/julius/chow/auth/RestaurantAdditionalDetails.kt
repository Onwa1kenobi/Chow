package io.julius.chow.auth


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.schibstedspain.leku.LATITUDE
import com.schibstedspain.leku.LOCATION_ADDRESS
import com.schibstedspain.leku.LONGITUDE
import com.schibstedspain.leku.LocationPickerActivity
import io.julius.chow.R
import io.julius.chow.base.extension.observe
import io.julius.chow.base.extension.setImageUrl
import io.julius.chow.main.image.ImagePickerActivity
import io.julius.chow.main.image.ImagePickerActivity.showImagePickerOptions
import kotlinx.android.synthetic.main.fragment_restaurant_additional_details.*
import java.io.IOException


class RestaurantAdditionalDetails : Fragment() {

    private lateinit var authViewModel: AuthViewModel

    private var isEditMode = false

    private val bannerImage = MutableLiveData<String?>()
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authViewModel = ViewModelProviders.of(activity!!).get(AuthViewModel::class.java)

        val safeArgs: RestaurantAdditionalDetailsArgs by navArgs()
        isEditMode = safeArgs.isEditMode
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_restaurant_additional_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isEditMode) {
            // Fetch the current user details
            authViewModel.getCurrentUser()

            // Change toolbar title to reflect the edit mode action
            toolbar.title = "Update Information"

            // Set back button to finish the activity
            toolbar.setNavigationOnClickListener {
                activity?.finish()
            }
        } else {
            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }

        field_restaurant_location.setOnClickListener {
            val locationPickerIntent = LocationPickerActivity.Builder()
                .withLocation(6.864689, 7.4079863)
                .withGeolocApiKey(getString(R.string.google_api_key))
//                .withSearchZone("es_ES")
//                .withSearchZone(SearchZoneRect(LatLng(26.525467, -18.910366), LatLng(43.906271, 5.394197)))
//                .withDefaultLocaleSearchZone()
//                .shouldReturnOkOnBackPressed()
//                .withStreetHidden()
//                .withCityHidden()
//                .withZipCodeHidden()
//                .withSatelliteViewHidden()
                .withGooglePlacesEnabled()
                .withGoogleTimeZoneEnabled()
//                .withVoiceSearchHidden()
                .withUnnamedRoadHidden()
                .build(context!!.applicationContext)

            startActivityForResult(locationPickerIntent, REQUEST_CODE_MAP)
        }

        banner_image.setOnClickListener {
            Dexter.withActivity(activity)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.areAllPermissionsGranted()) {
                            showImagePickerOptions()
                        }

                        if (report.isAnyPermissionPermanentlyDenied) {
                            showSettingsDialog()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: List<PermissionRequest>,
                        token: PermissionToken
                    ) {
                        token.continuePermissionRequest()
                    }
                }).check()
        }

        button_done.setOnClickListener { v ->

            parent_layout.forEach { if (it is TextInputLayout) it.isErrorEnabled = false }

            when {
                field_restaurant_name.text.toString().trim().isEmpty() -> layout_name_field_wrapper.error =
                    "Please enter the restaurant name"
                field_email_address.text.toString().trim().isEmpty() -> layout_email_field_wrapper.error =
                    "Please enter your email address"
                field_restaurant_address.text.toString().trim().isEmpty() -> layout_address_field_wrapper.error =
                    "Please enter your fully qualified address"
                field_restaurant_description.text.toString().trim().isEmpty() -> layout_description_field_wrapper.error =
                    "We need a story to add to your profile. Please tell us something...anything!!"
                field_restaurant_location.text.toString().trim().isEmpty() -> layout_location_field_wrapper.error =
                    "Your location will help people locate you."
                bannerImage.value == null -> Snackbar.make(
                    v,
                    "You need to upload a banner image.",
                    Snackbar.LENGTH_LONG
                ).apply {
                    this.view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent))
                    show()
                }

                else -> {
                    authViewModel.completeRestaurantRegistration(
                        field_restaurant_name.text.toString().trim(),
                        field_email_address.text.toString().trim(),
                        field_restaurant_address.text.toString().trim(),
                        field_restaurant_description.text.toString().trim(),
                        field_restaurant_location.text.toString().substringAfterLast(","),
                        latitude,
                        longitude
                    )
                }
            }
        }

        observe(authViewModel.authContractData) {
            it?.getContentIfNotHandled()?.let { data ->
                when (data) {
                    is AuthViewContract.ProgressDisplay -> {
                        if (data.display) progress_bar.visibility = View.VISIBLE else progress_bar.visibility =
                            View.INVISIBLE

                        // Enable/Disable data views
                        button_done.isEnabled = !data.display
                        parent_layout.forEach { view -> view.isEnabled = !data.display }
                    }

                    is AuthViewContract.MessageDisplay -> {
                        layout_name_field_wrapper.error = data.message
                    }

                    is AuthViewContract.NavigateToHome -> {
                        // If this fragment was not entered to edit user info, navigate to main activity;
                        // else, finish the current activity
                        if (!isEditMode) {
                            // Navigate to the MainActivity and finish this current activity
//                            Navigation.findNavController(activity!!, R.id.navigation_host_fragment)
//                                .navigate(R.id.action_mainActivity)
                        }
                        activity?.finish()
                    }

                    else -> {
                        // Do nothing
                    }
                }
            }
        }

        observe(authViewModel.currentRestaurant) {
            field_restaurant_name.setText(it.name)
            field_restaurant_address.setText(it.address)
        }

        observe(bannerImage, { uri: String? ->
            banner_image.setImageUrl(uri)
        })
    }

    private fun showImagePickerOptions() {
        showImagePickerOptions(context, object : ImagePickerActivity.PickerOptionListener {
            override fun onTakeCameraSelected() {
                launchCameraIntent()
            }

            override fun onChooseGallerySelected() {
                launchGalleryIntent()
            }
        })
    }

    private fun launchCameraIntent() {
        val intent = Intent(context, ImagePickerActivity::class.java)
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE)

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true)
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 16) // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 9)

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true)
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000)
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000)

        startActivityForResult(intent, REQUEST_IMAGE)
    }

    private fun launchGalleryIntent() {
        val intent = Intent(context, ImagePickerActivity::class.java)
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE)

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true)
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 16) // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 9)
        startActivityForResult(intent, REQUEST_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == REQUEST_IMAGE) {
                val uri = data.getParcelableExtra<Uri>("path")
                try {
                    // You can update this bitmap to your server
                    val bitmap = MediaStore.Images.Media.getBitmap(context!!.contentResolver, uri)

                    // loading profile image from local cache
                    bannerImage.postValue(uri.toString())
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            } else if (requestCode == REQUEST_CODE_MAP) {
                latitude = data.getDoubleExtra(LATITUDE, 0.0)
                longitude = data.getDoubleExtra(LONGITUDE, 0.0)
                field_restaurant_location.setText(data.getStringExtra(LOCATION_ADDRESS))
            }
        }
    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private fun showSettingsDialog() {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(getString(R.string.dialog_permission_title))
        builder.setMessage(getString(R.string.dialog_permission_message))
        builder.setPositiveButton(getString(R.string.go_to_settings)) { dialog, _ ->
            dialog.cancel()
            openSettings()
        }
        builder.setNegativeButton(getString(android.R.string.cancel)) { dialog, _ ->
            dialog.cancel()
        }
        builder.show()

    }

    // navigating user to app settings
    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", context!!.packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }

    companion object {
        const val EDIT_MODE = "isEditMode"
        const val REQUEST_IMAGE = 100
        const val REQUEST_CODE_MAP = 409
    }
}
