package com.dimitarduino.domasno1

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecnikAdapter(private val context: Context, private val recnikList : ArrayList<Zbor>) :

    RecyclerView.Adapter<RecnikAdapter.ZborViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ZborViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.zbor_vo_recnik, parent, false)

        return ZborViewHolder(view)
    }

    override fun onBindViewHolder(holder: ZborViewHolder, position: Int) {
        val momentalenZbor = recnikList[position]

        holder.mkZbor.text = momentalenZbor.mkZbor + " -> " + momentalenZbor.enZbor

        holder.btn_izmeniZbor.setOnClickListener {
            (context as MainActivity).popolniPodatociEdit(momentalenZbor.mkZbor!!, momentalenZbor.enZbor!!)
        }
    }

    override fun getItemCount(): Int {
        return recnikList.size
    }

    class ZborViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mkZbor = itemView.findViewById<TextView>(R.id.mkZbor)
        val btn_izmeniZbor = itemView.findViewById<Button>(R.id.btn_izmeniZbor)
    }
}