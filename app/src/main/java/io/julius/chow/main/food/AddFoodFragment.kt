package io.julius.chow.main.food


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import io.julius.chow.R
import io.julius.chow.base.BaseFragment
import io.julius.chow.base.extension.observe
import io.julius.chow.base.extension.setImageUrl
import io.julius.chow.base.extension.viewModel
import io.julius.chow.databinding.FragmentAddFoodBinding
import io.julius.chow.main.image.ImagePickerActivity
import io.julius.chow.model.Food
import kotlinx.android.synthetic.main.fragment_add_food.*
import java.io.IOException


class AddFoodFragment : BaseFragment<FragmentAddFoodBinding>() {

    override val contentResource = R.layout.fragment_add_food

    private lateinit var addFoodViewModel: AddFoodViewModel

    private lateinit var food: Food

    private val bannerImage = MutableLiveData<String?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val safeArgs: AddFoodFragmentArgs by navArgs()
        food = safeArgs.food ?: Food()

        // Inject all requisite objects for this fragment
        appComponent.inject(this)

        //subscription to LiveData in FoodDetailsViewModel
        addFoodViewModel = viewModel(viewModelFactory) {
            observe(foodViewContract) { event ->
                event.getContentIfNotHandled()?.let { data ->
                    when (data) {
                        is FoodViewContract.ProgressDisplay -> {
                            if (data.display) progress_bar.visibility = View.VISIBLE else progress_bar.visibility =
                                View.INVISIBLE

                            // Enable/Disable data views
                            button_save_food.isEnabled = !data.display
                            banner_image.isEnabled = !data.display
                            category_toggle_group.forEach {
                                it.isEnabled = !data.display
                            }
                            parent_layout.forEach { view -> view.isEnabled = !data.display }
                        }

                        is FoodViewContract.MessageDisplay -> {
                            // Display message feedback to user
                            notify(data.message)
                        }

                        is FoodViewContract.SaveSuccess -> {
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        }
        addFoodViewModel.food = food
    }

    override fun initialize(state: Bundle?) {
        if (food.id.isNotBlank()) {
            dataBinding.food = food
            bannerImage.postValue(food.imageUrl)
        }

        observe(bannerImage, { uri: String? ->
            banner_image.setImageUrl(uri)
        })

        toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

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

        button_save_food.setOnClickListener { v ->

            parent_layout.forEach { if (it is TextInputLayout) it.isErrorEnabled = false }

            when {
                field_food_name.text.toString().trim().isEmpty() -> layout_name_field_wrapper.run {
                    isErrorEnabled = true
                    error = "Please enter the food name"
                }

                field_food_description.text.toString().trim().isEmpty() -> layout_description_field_wrapper.run {
                    isErrorEnabled = true
                    error = "You need to say something about the food...anything!!"
                }
                field_food_price.text.toString().trim().isEmpty() -> layout_price_field_wrapper.run {
                    isErrorEnabled = true
                    error = "Eh...We need to know how much"
                }
                category_toggle_group.checkedButtonId == View.NO_ID -> Snackbar.make(
                    v,
                    "Please select a category.",
                    Snackbar.LENGTH_LONG
                ).apply {
                    this.view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent))
                    show()
                }
                bannerImage.value.isNullOrBlank() -> Snackbar.make(
                    v,
                    "You need to upload a banner image.",
                    Snackbar.LENGTH_LONG
                ).apply {
                    this.view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent))
                    show()
                }

                else -> {
                    addFoodViewModel.saveFood(
                        field_food_name.text.toString().trim(),
                        field_food_description.text.toString().trim(),
                        field_food_price.text.toString().replace(",", "").trim().toDouble(),
                        dataBinding.root.findViewById<MaterialButton>(
                            category_toggle_group.checkedButtonId
                        ).text.toString().toLowerCase(),
                        bannerImage.value.toString()
                    )
                }
            }
        }
    }

    private fun showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(context, object : ImagePickerActivity.PickerOptionListener {
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
                    // loading profile image from local cache
                    bannerImage.postValue(uri.toString())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
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
        const val REQUEST_IMAGE = 100
    }
}
