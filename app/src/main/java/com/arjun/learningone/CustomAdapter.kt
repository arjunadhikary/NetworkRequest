package com.arjun.learningone

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView


class ViewHolder(view:View){
    val tvName = view.findViewById<TextView>(R.id.tvName)!!
    val tvArtist = view.findViewById<TextView>(R.id.tvArtist)!!
    val tvSummary = view.findViewById<TextView>(R.id.tvSummary)!!
}
class CustomAdapter(context: Context, private val resId:Int, private val dataList:ArrayList<DataMap>)
    :ArrayAdapter<DataMap>(context,resId,dataList) {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return dataList.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
       val view:View
        val viewHolder:ViewHolder
        if(convertView == null){
             view =  inflater.inflate(resId,parent,false)
             viewHolder = ViewHolder(view)
            view.tag = viewHolder
        }else{
            view = convertView
            viewHolder = view.tag as ViewHolder
        }




        viewHolder.tvArtist.text = dataList[position].title
        viewHolder.tvName.text = dataList[position].artist
        viewHolder.tvSummary.text = dataList[position].link
        return view





    }
}