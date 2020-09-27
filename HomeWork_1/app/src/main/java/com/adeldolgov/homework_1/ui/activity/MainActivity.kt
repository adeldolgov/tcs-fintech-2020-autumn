package com.adeldolgov.homework_1.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.adeldolgov.homework_1.R
import com.adeldolgov.homework_1.extension.contactsPermissionGranted
import com.adeldolgov.homework_1.ui.adapter.ContactAdapter
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    companion object {
        private const val READ_CONTACTS_PERMISSION_CODE = 512
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getContactsBtn.setOnClickListener {
            if (this.contactsPermissionGranted()) startGetContactsActivityWithResult()
            else requestContactsPermission()
        }
    }

    private fun startGetContactsActivityWithResult() {
        startActivityForResult(
            Intent(this, LoadingContactsActivity::class.java),
            LoadingContactsActivity.GET_CONTACTS_REQUEST_CODE
        )
    }

    private fun requestContactsPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_CONTACTS
            )
        ) {
            Toast.makeText(this, R.string.contacts_permission, Toast.LENGTH_SHORT).show()
        }
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_CONTACTS),
            READ_CONTACTS_PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_CONTACTS_PERMISSION_CODE &&
            permissions[0] == Manifest.permission.READ_CONTACTS &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            startGetContactsActivityWithResult()
        } else {
            Toast.makeText(this, R.string.contacts_permission, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LoadingContactsActivity.GET_CONTACTS_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            getContactsBtn.visibility = View.GONE
            mainRecyclerView.apply {
                adapter =
                    ContactAdapter(data?.getParcelableArrayListExtra(LoadingContactsActivity.FETCH_CONTACTS_RESULT_EXTRA)!!)
            }
        }
    }
}