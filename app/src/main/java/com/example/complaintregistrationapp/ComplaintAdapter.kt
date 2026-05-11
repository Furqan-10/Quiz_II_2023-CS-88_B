package com.example.complaintregistrationapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ComplaintAdapter(private val complaints: List<Complaint>) :
    RecyclerView.Adapter<ComplaintAdapter.ComplaintViewHolder>() {

    class ComplaintViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvStudentName: TextView = view.findViewById(R.id.tvStudentName)
        val tvRollNumber: TextView = view.findViewById(R.id.tvRollNumber)
        val tvCategory: TextView = view.findViewById(R.id.tvCategory)
        val tvPriority: TextView = view.findViewById(R.id.tvPriority)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComplaintViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_complaint, parent, false)
        return ComplaintViewHolder(view)
    }

    override fun onBindViewHolder(holder: ComplaintViewHolder, position: Int) {
        val complaint = complaints[position]
        
        holder.tvTitle.text = complaint.title
        holder.tvStudentName.text = "Student: ${complaint.studentName}"
        holder.tvRollNumber.text = "Roll No: ${complaint.rollNumber}"
        holder.tvCategory.text = "Category: ${complaint.category}"
        holder.tvPriority.text = complaint.priority
        holder.tvStatus.text = complaint.status

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("complaint_data", complaint)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = complaints.size
}