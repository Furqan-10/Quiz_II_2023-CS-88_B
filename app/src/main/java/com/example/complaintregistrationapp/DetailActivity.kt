package com.example.complaintregistrationapp

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var progressBar: ProgressBar
    private var complaintId: String? = null
    private val firebaseUrl = "https://complaintapp-57d0a-default-rtdb.firebaseio.com/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // Use explicit URL to ensure it connects to the correct database instance
        database = FirebaseDatabase.getInstance(firebaseUrl).getReference("complaints")

        // Enable back button in toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Complaint Details"

        val tvDetailTitle: TextView = findViewById(R.id.tvDetailTitle)
        val tvDetailDate: TextView = findViewById(R.id.tvDetailDate)
        val tvDetailStudentName: TextView = findViewById(R.id.tvDetailStudentName)
        val tvDetailRollNumber: TextView = findViewById(R.id.tvDetailRollNumber)
        val tvDetailCategory: TextView = findViewById(R.id.tvDetailCategory)
        val tvDetailPriority: TextView = findViewById(R.id.tvDetailPriority)
        val tvDetailStatus: TextView = findViewById(R.id.tvDetailStatus)
        val tvDetailDescription: TextView = findViewById(R.id.tvDetailDescription)
        val btnDelete: Button = findViewById(R.id.btnDelete)
        val btnResolve: Button = findViewById(R.id.btnResolve)
        progressBar = findViewById(R.id.detailProgressBar)

        // Get complaint data from intent
        @Suppress("DEPRECATION")
        val complaint = intent.getSerializableExtra("complaint_data") as? Complaint

        complaint?.let {
            complaintId = it.id
            tvDetailTitle.text = it.title
            tvDetailStudentName.text = it.studentName
            tvDetailRollNumber.text = "Roll No: ${it.rollNumber}"
            tvDetailCategory.text = it.category
            tvDetailPriority.text = it.priority
            tvDetailStatus.text = it.status
            tvDetailDescription.text = it.description

            // Format and display date
            val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
            val formattedDate = sdf.format(Date(it.date))
            tvDetailDate.text = "Date: $formattedDate"

            // Hide Resolve button if already resolved
            if (it.status == "Resolved") {
                btnResolve.visibility = View.GONE
            }
        }

        btnDelete.setOnClickListener {
            showDeleteConfirmation()
        }

        btnResolve.setOnClickListener {
            markAsResolved()
        }
    }

    private fun markAsResolved() {
        if (complaintId == null) return

        progressBar.visibility = View.VISIBLE
        
        database.child(complaintId!!).child("status").setValue("Resolved")
            .addOnSuccessListener {
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Complaint marked as Resolved", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Update failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Delete Complaint")
            .setMessage("Are you sure you want to delete this complaint?")
            .setPositiveButton("Delete") { _, _ -> deleteComplaint() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteComplaint() {
        if (complaintId == null) return

        progressBar.visibility = View.VISIBLE

        database.child(complaintId!!).removeValue()
            .addOnSuccessListener {
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Complaint Deleted", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Delete failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}