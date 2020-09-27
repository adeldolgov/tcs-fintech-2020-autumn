package com.adeldolgov.homework_1.ui.activity

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.adeldolgov.homework_1.R
import com.adeldolgov.homework_1.service.ContactsService
import com.adeldolgov.homework_1.utils.Status
import kotlinx.android.synthetic.main.activity_loading_contacts.*

class LoadingContactsActivity : AppCompatActivity() {

    companion object {
        const val GET_CONTACTS_REQUEST_CODE = 157
        const val FETCH_CONTACTS_RESULT_EXTRA = "fetch_contacts_result_extra"
    }

    private val contactsReceiver = initContactsReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading_contacts)
        startGetContactsService(this)
    }

    override fun onStart() {
        super.onStart()
        val intentFilter = IntentFilter(ContactsService.CONTACTS_INTENT_FILTER)
        val localBroadcastManager = LocalBroadcastManager.getInstance(this)
        localBroadcastManager.registerReceiver(contactsReceiver, intentFilter)
    }

    override fun onStop() {
        val localBroadcastManager = LocalBroadcastManager.getInstance(this)
        localBroadcastManager.unregisterReceiver(contactsReceiver)
        super.onStop()
    }

    private fun initContactsReceiver(): BroadcastReceiver {
        return object : BroadcastReceiver() {
            override fun onReceive(contxt: Context?, intent: Intent?) {
                when (intent?.getSerializableExtra(ContactsService.RESULT_EXTRA)) {
                    Status.SUCCESS -> processSuccess(intent)
                    Status.LOADING -> processLoading()
                    Status.ERROR -> processError(intent)
                }
            }
        }
    }

    private fun startGetContactsService(context: Context) {
        val intent = Intent(context, ContactsService::class.java)
        context.startService(intent)
    }

    private fun processError(intent: Intent) {
        progressCircular.isVisible = false
        statusText.text =
            getString(
                R.string.status_error,
                intent.getStringExtra(ContactsService.DATA_EXTRA)
            )
    }

    private fun processLoading() {
        progressCircular.isVisible = true
        statusText.setText(R.string.status_fetching_contacts)
    }

    private fun processSuccess(intent: Intent) {
        progressCircular.isVisible = false
        statusText.setText(R.string.status_success)
        setResult(
            Activity.RESULT_OK,
            Intent().putParcelableArrayListExtra(
                FETCH_CONTACTS_RESULT_EXTRA,
                intent.getParcelableArrayListExtra(ContactsService.DATA_EXTRA)
            )
        )
        finish()
    }

}