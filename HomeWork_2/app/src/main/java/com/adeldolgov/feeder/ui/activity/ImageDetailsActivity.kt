package com.adeldolgov.feeder.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.adeldolgov.feeder.R
import com.adeldolgov.feeder.util.extension.*
import com.adeldolgov.feeder.util.imageloader.GlideImageLoader
import com.adeldolgov.feeder.util.imageloader.ImageLoader
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_details_image.*
import java.io.File


class ImageDetailsActivity : AppCompatActivity(R.layout.activity_details_image) {

    companion object {
        private const val IMAGE_ARG = "IMAGE_ARG"
        private const val TRANSITION_ARG = "TRANSITION_ARG"

        private const val WRITE_STORAGE_PERMISSION_REQUEST_CODE = 515

        fun createIntent(context: Context, imageUrl: String, transitionName: String): Intent {
            return Intent(context, ImageDetailsActivity::class.java).apply {
                putExtra(IMAGE_ARG, imageUrl)
                putExtra(TRANSITION_ARG, transitionName)
            }
        }
    }

    private val imageLoader: ImageLoader = GlideImageLoader()
    private val compositeDisposable = CompositeDisposable()
    private var photoUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(appToolbar)
        supportActionBar?.setTitle(R.string.details_watch)
        detailsImage.transitionName = intent.getStringExtra(TRANSITION_ARG)
        photoUrl = intent.getStringExtra(IMAGE_ARG)
        photoUrl?.let {
            imageLoader.loadPoster(it, detailsImage)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> { savePhotoFileOrAskForPermissions() }
            R.id.share -> { openShareChooserIfImageViewReady() }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.image_details_menu, menu)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    private fun savePhotoFileOrAskForPermissions(){
        if (writeStoragePermissionGranted()) photoUrl?.let { saveBitmapFromUrl(it) }
        else requestStoragePermission(WRITE_STORAGE_PERMISSION_REQUEST_CODE)
    }

    private fun openShareChooserIfImageViewReady() {
        (detailsImage.drawable as BitmapDrawable?)?.bitmap?.let {
            sharePhotoFile(getString(R.string.share), saveImageToCache(it))
        }
    }

    private fun saveBitmapFromUrl(url: String) {
        compositeDisposable.addAll(
            Single.fromCallable { imageLoader.getFileFromUrl(url, this) }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribeBy(
                onSuccess = { savePhotoFileAsync(it) },
                onError = {
                    runOnUiThread{
                        toast(it.message?:getString(R.string.error_image_loading))
                    }
                }
            )
        )
    }

    private fun savePhotoFileAsync(it: File) {
        saveImageFileToPictures(it)?.let {
            runOnUiThread {
                toast(getString(R.string.success_save_picture))
            }
        } ?: run {
            runOnUiThread {
                toast(getString(R.string.error_save_picture))
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == WRITE_STORAGE_PERMISSION_REQUEST_CODE &&
            permissions[0] == Manifest.permission.WRITE_EXTERNAL_STORAGE &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            photoUrl?.let { saveBitmapFromUrl(it) }
        } else {
            toast(getString(R.string.permission_storage))
        }
    }
}