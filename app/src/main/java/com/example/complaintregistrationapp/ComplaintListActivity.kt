package com.example.complaintregistrationapp

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ComplaintListActivity : AppCompatActivity() {

    private lateinit var rvComplaints: RecyclerView
    private lateinit var tvEmptyMessage: TextView
    private lateinit var adapter: ComplaintAdapter
    private val complaintList = mutableListOf<Complaint>()
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complaint_list)

        // TO FIX YOUR ISSUE: Use the same explicit URL as MainActivity
        val firebaseUrl = "https://complaintapp-57d0a-default-rtdb.firebaseio.com/"
        database = FirebaseDatabase.getInstance(firebaseUrl).getReference("complaints")

        // Initialize UI elements
        rvComplaints = findViewById(R.id.rvComplaints)
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage)

        // Use LinearLayoutManager
        rvComplaints.layoutManager = LinearLayoutManager(this)
        
        // Initialize and set adapter
        adapter = ComplaintAdapter(complaintList)
        rvComplaints.adapter = adapter

        fetchComplaints()
    }

    private fun fetchComplaints() {
        // Fetch complaints from Realtime Database
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                complaintList.clear()
                if (snapshot.exists()) {
                    for (complaintSnapshot in snapshot.children) {
                        val complaint = complaintSnapshot.getValue(Complaint::class.java)
                        if (complaint != null) {
                            complaintList.add(complaint)
                        }
                    }
                    // Show newest complaints first (reverse chronological)
                    complaintList.sortByDescending { it.date }
                }

                // Show empty TextView if no complaints exist
                if (complaintList.isEmpty()) {
                    tvEmptyMessage.visibility = View.VISIBLE
                    rvComplaints.visibility = View.GONE
                } else {
                    tvEmptyMessage.visibility = View.GONE
                    rvComplaints.visibility = View.VISIBLE
                }
                
                // Update RecyclerView adapter
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ComplaintListActivity, "Error fetching data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}