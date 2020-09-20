package com.adeldolgov.homework_1.service

import android.app.IntentService
import android.content.Intent
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
            broadcaster.sendBroadcast(
                Intent(CONTACTS_INTENT_FILTER).putExtra(
                    RESULT_EXTRA,
                    Status.LOADING
                )
            )
            try {
                val result = fetchContactsFromContactProvider()
                broadcaster.sendBroadcast(
                    Intent(CONTACTS_INTENT_FILTER).putExtra(RESULT_EXTRA, Status.SUCCESS)
                        .putParcelableArrayListExtra(DATA_EXTRA, result)
                )
            } catch (exception: Exception) {
                broadcaster.sendBroadcast(
                    Intent(CONTACTS_INTENT_FILTER).putExtra(RESULT_EXTRA, Status.ERROR)
                        .putExtra(DATA_EXTRA, exception.message)
                )
            }
        }
    }

    private fun fetchContactsFromContactProvider(): ArrayList<ContactData> {
        val result = ArrayList<ContactData>()
        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI, null,
            null, null,
            "upper(" + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + ") ASC"
        )
        while (cursor?.moveToNext()!!) {
            val contactId = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID))
            val displayName =
                cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
            var phoneNumber: String? = null
            if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) == 1) {
                val phoneCursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    arrayOf(contactId.toString()),
                    null
                )
                if (phoneCursor?.moveToFirst()!!) {
                    phoneNumber = phoneCursor.getString(
                        phoneCursor.getColumnIndex
                            (ContactsContract.CommonDataKinds.Phone.NUMBER)
                    )
                }
                phoneCursor.close()
            }
            result.add(ContactData(contactId, displayName, phoneNumber))
        }
        cursor.close()
        return result
    }
}
