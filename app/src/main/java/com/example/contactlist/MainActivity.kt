package com.example.contactlist

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView

class MainActivity : AppCompatActivity() {

    private val CONTACT_PERMISSION_CODE = 100

    private lateinit var adapter: ContactAdapter
    private lateinit var searchBox: EditText

    private var allContacts = emptyList<Contact>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<FastScrollRecyclerView>(
            R.id.recyclerViewContacts
        )

        searchBox = findViewById(R.id.edtSearch)

        // Sample contacts
        allContacts = listOf(
            Contact("Alice Smith", "9876543211", R.drawable.ic_launcher_foreground),
            Contact("Bob Johnson", "9876543212", R.drawable.ic_launcher_foreground),
            Contact("Charlie Brown", "9876543213", R.drawable.ic_launcher_foreground),
            Contact("David Wilson", "9876543214", R.drawable.ic_launcher_foreground),
            Contact("Emma Davis", "9876543215", R.drawable.ic_launcher_foreground),
            Contact("Frank Miller", "9876543216", R.drawable.ic_launcher_foreground),
            Contact("Grace Taylor", "9876543217", R.drawable.ic_launcher_foreground),
            Contact("Harry Anderson", "9876543218", R.drawable.ic_launcher_foreground),
            Contact("Isabella Thomas", "9876543219", R.drawable.ic_launcher_foreground),
            Contact("James Jackson", "9876543220", R.drawable.ic_launcher_foreground),
            Contact("Katherine White", "9876543221", R.drawable.ic_launcher_foreground),
            Contact("Liam Harris", "9876543222", R.drawable.ic_launcher_foreground),
            Contact("Michael Martin", "9876543223", R.drawable.ic_launcher_foreground),
            Contact("Nora Thompson", "9876543224", R.drawable.ic_launcher_foreground),
            Contact("Oliver Garcia", "9876543225", R.drawable.ic_launcher_foreground),
            Contact("Peter Martinez", "9876543226", R.drawable.ic_launcher_foreground),
            Contact("Rachel Robinson", "9876543227", R.drawable.ic_launcher_foreground),
            Contact("Sophia Clark", "9876543228", R.drawable.ic_launcher_foreground),
            Contact("Thomas Rodriguez", "9876543229", R.drawable.ic_launcher_foreground),
            Contact("William Lewis", "9876543230", R.drawable.ic_launcher_foreground)
        )

        adapter = ContactAdapter(allContacts)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Search / filter
        searchBox.addTextChangedListener(object : android.text.TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                filterContacts(s.toString())
            }

            override fun afterTextChanged(
                s: android.text.Editable?
            ) {
            }
        })

        // Request contact permission
        requestContactPermission()
    }

    private fun requestContactPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                CONTACT_PERMISSION_CODE
            )
        } else {
            loadDeviceContacts()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        if (requestCode == CONTACT_PERMISSION_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            loadDeviceContacts()
        }
    }

    private fun loadDeviceContacts() {

        val deviceContacts = mutableListOf<Contact>()

        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ),
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        cursor?.use {

            val nameIndex = it.getColumnIndex(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
            )

            val phoneIndex = it.getColumnIndex(
                ContactsContract.CommonDataKinds.Phone.NUMBER
            )

            while (it.moveToNext()) {

                val name = it.getString(nameIndex)
                val phone = it.getString(phoneIndex)

                deviceContacts.add(
                    Contact(
                        name = name ?: "Unknown",
                        phone = phone ?: "",
                        avatar = R.drawable.ic_launcher_foreground
                    )
                )
            }
        }

        if (deviceContacts.isNotEmpty()) {
            allContacts = deviceContacts
            filterContacts(searchBox.text.toString())
        }
    }

    private fun filterContacts(searchText: String) {

        val filteredContacts = allContacts.filter { contact ->

            contact.name.contains(
                searchText.trim(),
                ignoreCase = true
            ) || contact.phone.contains(
                searchText.trim()
            )
        }

        adapter.updateList(filteredContacts)
    }
}