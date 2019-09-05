package com.example.contactreader

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentResolverCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*

private const val REQ_CODE_CONTACT = 10001
typealias Phone = ContactsContract.CommonDataKinds.Phone

class MainActivity : AppCompatActivity() {
    private val adapter: ContactAdapter by lazy(LazyThreadSafetyMode.NONE) { ContactAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        setUpContactList()

        fab.setOnClickListener { insertNewContact() }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_CONTACTS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), REQ_CODE_CONTACT)
            } else {
                loadContacts()
            }
        } else {
            loadContacts()
        }
    }

    private fun insertNewContact() {
        
    }

    private fun setUpContactList() {
        rv.adapter = adapter
    }

    private fun loadContacts() {
        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.PHOTO_URI
        )
        val contactList: MutableList<Contact> = mutableListOf()
        val contactMap = hashMapOf<String, Contact>()
        ContentResolverCompat.query(
            this.contentResolver,
            ContactsContract.Contacts.CONTENT_URI,
            projection,
            null, null,
            null, null
        ).use {
            val idIndex = it.getColumnIndex(ContactsContract.Contacts._ID)
            val nameIndex = it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            val photoIndex = it.getColumnIndex(ContactsContract.Contacts.PHOTO_URI)
            var name: String?
            var id: String?
            var photo: String?
            var contact: Contact
            while (it.moveToNext()) {
                id = it.getString(idIndex)
                name = it.getString(nameIndex)
                photo = it.getString(photoIndex)
                contact = Contact(id, name, photo)
                contactList += contact
                contactMap[id] = contact
            }
        }
        //Timber
        adapter.submitList(contactList)
        findAndMatchNumber(contactMap)
        findAndMatchEmail(contactMap)
        Log.d("loadContacts", "contacts:${contactList}")
    }

    private fun findAndMatchEmail(contactMap: HashMap<String, Contact>) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun findAndMatchNumber(contactMap: HashMap<String, Contact>) {
        val projection = arrayOf(
            Phone.CONTACT_ID,
            Phone.NUMBER,
            Phone.TYPE
        )
        ContentResolverCompat.query(
            this.contentResolver,
            Phone.CONTENT_URI,
            projection,
            null, null,
            null, null
        ).use {
            val idIndex = it.getColumnIndex(Phone.CONTACT_ID)
            val numIndex = it.getColumnIndex(Phone.NUMBER)
            val typeIndex = it.getColumnIndex(Phone.TYPE)

            var number: String?
            var id: String?
            var typeId: Int
            var type: String
            var phone: Number
            var contact: Contact?

            while (it.moveToNext()) {
                id = it.getString(idIndex)
                contact = contactMap[id]
                if (contact != null) {
                    number = it.getString(numIndex)
                    typeId = it.getInt(typeIndex)
                    type = Phone.getTypeLabel(resources, typeId, "Custom").toString()
                    phone = Number(type, number)
                    contact.numbers += phone
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_CODE_CONTACT
            && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            loadContacts()
        } else {
            Toast.makeText(this, "please give permission", Toast.LENGTH_LONG).show()
        }
    }
}
