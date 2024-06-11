package com.android.medialerta.presentation.contact

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.provider.ContactsContract
import androidx.lifecycle.ViewModel

class ContactViewModel(private val contentResolver : ContentResolver) : ViewModel() {
    @SuppressLint("Range")
    fun getStarredContact(): Pair<String?, String?> {
        val resolver: ContentResolver = contentResolver;
        val cursor = resolver.query(
            ContactsContract.Contacts.CONTENT_URI, null, null, null, null
        )
        if (cursor != null) {
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    if (cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                            .toInt() > 0
                    ) {
                        val phone = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null, null, null, null
                        )
                        while (phone!!.moveToNext()) {
                            if (phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.STARRED))
                                    .toInt() > 0
                            ) {
                                var contactName =
                                    phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                                var phoneNumber =
                                    phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                                return Pair(contactName, phoneNumber)
                            }
                        }
                        phone.close()
                    }
                }
            }
        }
        cursor!!.close()
        return Pair(null, null)
    }
}