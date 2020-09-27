package com.adeldolgov.homework_1.service

import android.app.IntentService
import android.content.Intent
import android.database.Cursor
import android.provider.ContactsContract
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.adeldolgov.homework_1.data.POJO.ContactData
import com.adeldolgov.homework_1.extension.contactsPermissionGranted
import com.adeldolgov.homework_1.utils.Status


class ContactsService : IntentService("ContactsService") {

    companion object {
        const val CONTACTS_INTENT_FILTER = "com.adeldolgov.homework_1.GetContacts"
        const val RESULT_EXTRA = "result_extra"
        const val DATA_EXTRA = "data_extra"
    }

    override fun onHandleIntent(intent: Intent?) {
        if (this.contactsPermissionGranted()) {
            val broadcaster = LocalBroadcastManager.getInstance(this)
            sendLoadingStatus(broadcaster)
            try {
                val result = fetchContactsFromContactProvider()
                sendSuccessStatus(broadcaster, result)
            } catch (exception: Exception) {
                sendErrorStatus(broadcaster, exception)
            }
        }
    }

    private fun sendErrorStatus(broadcaster: LocalBroadcastManager, exception: Exception) {
        broadcaster.sendBroadcast(
            Intent(CONTACTS_INTENT_FILTER)
                .putExtra(RESULT_EXTRA, Status.ERROR)
                .putExtra(DATA_EXTRA, exception.message)
        )
    }

    private fun sendSuccessStatus(
        broadcaster: LocalBroadcastManager,
        result: ArrayList<ContactData>
    ) {
        broadcaster.sendBroadcast(
            Intent(CONTACTS_INTENT_FILTER)
                .putExtra(RESULT_EXTRA, Status.SUCCESS)
                .putParcelableArrayListExtra(DATA_EXTRA, result)
        )
    }

    private fun sendLoadingStatus(broadcaster: LocalBroadcastManager) {
        broadcaster.sendBroadcast(
            Intent(CONTACTS_INTENT_FILTER)
                .putExtra(RESULT_EXTRA, Status.LOADING)
        )
    }

    private fun fetchContactsFromContactProvider(): ArrayList<ContactData> {
        val result = ArrayList<ContactData>()
        val cursor = getContactsCursor()

        cursor?.use {
            while (it.moveToNext()) {
                val contactId = it.getInt(it.getColumnIndex(ContactsContract.Contacts._ID))
                val displayName =
                    it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                var phoneNumber: String? = null

                if (it.getInt(it.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) == 1) {
                    val phoneCursor = getPhoneCursor(contactId)
                    phoneCursor?.use {
                        if (it.moveToFirst()) {
                            phoneNumber =
                                it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        }
                    }
                }
                result.add(ContactData(contactId, displayName, phoneNumber))
            }
        }
        return result
    }

    private fun getPhoneCursor(contactId: Int): Cursor? {
        return contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
            arrayOf(contactId.toString()),
            null
        )
    }

    private fun getContactsCursor(): Cursor? {
        return contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI, null,
            null, null,
            "upper(" + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + ") ASC"
        )
    }
}
