package com.adeldolgov.feeder.ui.dialogfragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.adeldolgov.feeder.R
import com.adeldolgov.feeder.util.extension.debounceClick
import com.adeldolgov.feeder.util.extension.feederApp
import com.adeldolgov.feeder.util.extension.saveImageToCache
import com.adeldolgov.feeder.util.imageloader.ImageLoader
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_new_post.*
import kotlinx.android.synthetic.main.item_photo_attachment.*
import java.io.File
import javax.inject.Inject


class NewPostBottomSheetDialogFragment : BottomSheetDialogFragment() {

    companion object {
        private const val CHOOSE_PHOTO_REQUEST_CODE = 517
    }

    private var onPostCreateListener: OnPostCreateListener? = null
    private var photoFile: File? = null

    @Inject
    lateinit var imageLoader: ImageLoader

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_new_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postEditText.requestFocus()
        postEditText.addTextChangedListener {
            sendBtn.isEnabled = it?.toString()?.isNotBlank() ?: false
        }
        sendBtn.setOnClickListener {
            onPostCreateListener?.onCreatePost(postEditText.text.toString(), photoFile)
        }
        attachImageBtn.debounceClick { performPhotoSearch() }
        cancelAttachBtn.setOnClickListener {
            photoFile = null
            showBitmapFromUri(false)
        }
    }

    fun showProgressSending(show: Boolean) {
        sendingProgress.isVisible = show
        postEditText.isEnabled = !show
        sendBtn.isInvisible = show
        attachImage.isEnabled = !show
        cancelAttachBtn.isEnabled = !show
        isCancelable = !show
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Widget_App_CardView_BottomSheetDialog)
        requireContext().feederApp().appComponent.inject(this)
    }

    private fun performPhotoSearch() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, CHOOSE_PHOTO_REQUEST_CODE)
    }

    private fun showBitmapFromUri(show: Boolean) {
        if (show) {
            photoFile?.let {
                imageLoader.loadPhotoFromFile(it, attachImage)
                photoContainer.isVisible = true
                photoContainer.animate().scaleY(1f).scaleX(1f).setDuration(250).start()
            }
        } else {
            photoContainer.animate().scaleY(0f).scaleX(0f).setDuration(250).withEndAction {
                photoContainer.isVisible = false
            }.start()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, result: Intent?) {
        super.onActivityResult(requestCode, resultCode, result)
        if (requestCode == CHOOSE_PHOTO_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (result != null) {
                result.data?.let {
                    photoFile = requireContext().saveImageToCache(it)
                    showBitmapFromUri(true)
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): BottomSheetDialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.behavior.state = STATE_EXPANDED
        bottomSheetDialog.behavior.isFitToContents = true
        bottomSheetDialog.behavior.skipCollapsed = true
        return bottomSheetDialog
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnPostCreateListener) {
            onPostCreateListener = context
        } else if (parentFragment is OnPostCreateListener) {
            onPostCreateListener = parentFragment as OnPostCreateListener
        }
    }

    interface OnPostCreateListener {
        fun onCreatePost(text: String, photoFile: File?)
    }

}