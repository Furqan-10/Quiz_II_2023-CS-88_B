package com.example.complaintregistrationapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private lateinit var etStudentName: EditText
    private lateinit var etRollNumber: EditText
    private lateinit var etComplaintTitle: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var spinnerPriority: Spinner
    private lateinit var etDescription: EditText
    private lateinit var btnSubmit: Button
    private lateinit var btnViewComplaints: Button
    private lateinit var progressBar: ProgressBar

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // TO FIX YOUR ISSUE: 
        // 1. Go to Firebase Console -> Realtime Database.
        // 2. Copy the URL (e.g., https://complaintapp-57d0a-default-rtdb.firebaseio.com/)
        // 3. Use it below if data still doesn't show in console.
        val firebaseUrl = "https://complaintapp-57d0a-default-rtdb.firebaseio.com/"
        database = FirebaseDatabase.getInstance(firebaseUrl).getReference("complaints")

        // Connection monitor
        val connectedRef = FirebaseDatabase.getInstance(firebaseUrl).getReference(".info/connected")
        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false
                if (connected) {
                    Log.d("FirebaseConn", "Connected to Firebase")
                } else {
                    Log.d("FirebaseConn", "Not connected to Firebase")
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        // Initialize UI elements
        etStudentName = findViewById(R.id.etStudentName)
        etRollNumber = findViewById(R.id.etRollNumber)
        etComplaintTitle = findViewById(R.id.etComplaintTitle)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        spinnerPriority = findViewById(R.id.spinnerPriority)
        etDescription = findViewById(R.id.etDescription)
        btnSubmit = findViewById(R.id.btnSubmit)
        btnViewComplaints = findViewById(R.id.btnViewComplaints)
        progressBar = findViewById(R.id.progressBar)

        setupSpinners()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnSubmit.setOnClickListener {
            saveComplaint()
        }

        btnViewComplaints.setOnClickListener {
            val intent = Intent(this, ComplaintListActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupSpinners() {
        val categories = arrayOf(
            "IT", "Library", "Transport", "Hostel", "Accounts", 
            "Examination", "Cafeteria", "Administration"
        )
        val priorities = arrayOf("Low", "Medium", "High", "Urgent")

        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = categoryAdapter

        val priorityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, priorities)
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPriority.adapter = priorityAdapter
    }

    private fun saveComplaint() {
        val name = etStudentName.text.toString().trim()
        val rollNo = etRollNumber.text.toString().trim()
        val title = etComplaintTitle.text.toString().trim()
        val description = etDescription.text.toString().trim()
        
        val category = spinnerCategory.selectedItem.toString()
        val priority = spinnerPriority.selectedItem.toString()

        if (name.isEmpty() || rollNo.isEmpty() || title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        progressBar.visibility = View.VISIBLE
        btnSubmit.isEnabled = false

        val complaintId = database.push().key ?: ""
        
        val complaint = Complaint(
            id = complaintId,
            studentName = name,
            rollNumber = rollNo,
            title = title,
            category = category,
            priority = priority,
            description = description,
            status = "Pending",
            date = System.currentTimeMillis()
        )

        database.child(complaintId).setValue(complaint)
            .addOnSuccessListener {
                progressBar.visibility = View.GONE
                btnSubmit.isEnabled = true
                Toast.makeText(this, "Complaint Submitted Successfully", Toast.LENGTH_SHORT).show()
                clearFields()
            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.GONE
                btnSubmit.isEnabled = true
                Toast.makeText(this, "Submission Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearFields() {
        etStudentName.text.clear()
        etRollNumber.text.clear()
        etComplaintTitle.text.clear()
        etDescription.text.clear()
        spinnerCategory.setSelection(0)
        spinnerPriority.setSelection(0)
    }
}