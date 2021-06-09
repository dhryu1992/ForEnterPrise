package com.showorld.data

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.showorld.R
import java.util.*

class NoteAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>(), OnNoteItemClickListener {

    var items = ArrayList<Note>()

    var listener: OnNoteItemClickListener? = null

    var layoutType = 0

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.note_item, viewGroup, false))
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        viewHolder.setItem(item)
        viewHolder.setLayoutType(layoutType)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun addItem(item: Note) {
        items.add(item)
    }

    @JvmName("setItems1")
    fun setItems(items: ArrayList<Note>) {
        this.items = items
    }

    fun getItem(position: Int) : Note {
        return items.get(position)
    }

    fun setOnItemClickListener(listener: OnNoteItemClickListener) {
        this.listener = listener
    }

    override fun onItemClick(holder: ViewHolder?, view: View?, position: Int) {
        if (listener != null) {
            listener!!.onItemClick(holder, view, position)
        }
    }

    fun switchLayout(position: Int) {
        layoutType = position
    }

}

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {

        var layout1: LinearLayout? = null
        var layout2: LinearLayout? = null

        var moodImageView: ImageView? = null
        var moodImageView2: ImageView? = null

        var pictureExistsImageView: ImageView? = null
        var pictureImageView: ImageView? = null

        var weatherImageView: ImageView? = null
        var weatherImageView2: ImageView? = null

        var contentsTextView: TextView? = null
        var contentsTextView2: TextView? = null

        var locationTextView: TextView? = null
        var locationTextView2: TextView? = null

        var dateTextView: TextView? = null
        var dateTextView2: TextView? = null

        constructor(itemView: View, listener: OnNoteItemClickListener?, layoutType: Int) : this(itemView) {
            super.itemView

            layout1 = itemView.findViewById(R.id.layout1)
            layout2 = itemView.findViewById(R.id.layout2)
            moodImageView = itemView.findViewById(R.id.moodImageView)
            moodImageView2 = itemView.findViewById(R.id.moodImageView2)
            pictureExistsImageView = itemView.findViewById(R.id.pictureExistsImageView)
            pictureImageView = itemView.findViewById(R.id.pictureImageView)
            weatherImageView = itemView.findViewById(R.id.weatherImageView)
            weatherImageView2 = itemView.findViewById(R.id.weatherImageView2)
            contentsTextView = itemView.findViewById(R.id.contentsTextView)
            contentsTextView2 = itemView.findViewById(R.id.contentsTextView2)
            locationTextView = itemView.findViewById(R.id.locationTextView)
            locationTextView2 = itemView.findViewById(R.id.locationTextView2)
            dateTextView = itemView.findViewById(R.id.dateTextView)
            dateTextView2 = itemView.findViewById(R.id.dateTextView2)

            itemView.setOnClickListener {  view ->
                val position: Int? = adapterPosition
                if (position != null) {
                    listener?.onItemClick(this@ViewHolder, view, position)
                }
            }
            setLayoutType(layoutType)
        }

        fun setItem(item: Note) {
            val mood: String? = item.getMood()
            val moodIndex = mood!!.toInt()
            setMoodImage(moodIndex)

            val picturePath = item.getPicture()
            if (picturePath != null && picturePath != "") {
                pictureExistsImageView!!.visibility = View.VISIBLE
                pictureImageView!!.visibility = View.VISIBLE
                pictureImageView!!.setImageURI(Uri.parse("file://$picturePath"))
            } else {
                pictureExistsImageView!!.visibility = View.GONE
                pictureImageView!!.visibility = View.GONE
                pictureImageView!!.setImageResource(R.drawable.noimagefound)
            }

            //set weather
            val weather: String? = item.getWeather()
            val weatherIndex = weather!!.toInt()
            setWeatherImage(weatherIndex)

            contentsTextView!!.text = item.getContents()
            contentsTextView2!!.text = item.getContents()

            locationTextView!!.text = item.getAddress()
            locationTextView2!!.text = item.getAddress()

            dateTextView!!.text = item.getCreateDateStr()
            dateTextView2!!.text = item.getCreateDateStr()
        }

        fun setMoodImage(moodIndex: Int) {
            when (moodIndex) {
                0 -> {
                    moodImageView!!.setImageResource(R.drawable.smile1_48)
                    moodImageView2!!.setImageResource(R.drawable.smile1_48)
                }
                1 -> {
                    moodImageView!!.setImageResource(R.drawable.smile2_48)
                    moodImageView2!!.setImageResource(R.drawable.smile2_48)
                }
                2 -> {
                    moodImageView!!.setImageResource(R.drawable.smile3_48)
                    moodImageView2!!.setImageResource(R.drawable.smile3_48)
                }
                3 -> {
                    moodImageView!!.setImageResource(R.drawable.smile4_48)
                    moodImageView2!!.setImageResource(R.drawable.smile4_48)
                }
                4 -> {
                    moodImageView!!.setImageResource(R.drawable.smile5_48)
                    moodImageView2!!.setImageResource(R.drawable.smile5_48)
                }
                else -> {
                    moodImageView!!.setImageResource(R.drawable.smile3_48)
                    moodImageView2!!.setImageResource(R.drawable.smile3_48)
                }
            }
        }

        fun setWeatherImage(weatherIndex: Int) {
            when (weatherIndex) {
                0 -> {
                    weatherImageView!!.setImageResource(R.drawable.weather_icon_1)
                    weatherImageView2!!.setImageResource(R.drawable.weather_icon_1)
                }
                1 -> {
                    weatherImageView!!.setImageResource(R.drawable.weather_icon_2)
                    weatherImageView2!!.setImageResource(R.drawable.weather_icon_2)
                }
                2 -> {
                    weatherImageView!!.setImageResource(R.drawable.weather_icon_3)
                    weatherImageView2!!.setImageResource(R.drawable.weather_icon_3)
                }
                3 -> {
                    weatherImageView!!.setImageResource(R.drawable.weather_icon_4)
                    weatherImageView2!!.setImageResource(R.drawable.weather_icon_4)
                }
                4 -> {
                    weatherImageView!!.setImageResource(R.drawable.weather_icon_5)
                    weatherImageView2!!.setImageResource(R.drawable.weather_icon_5)
                }
                5 -> {
                    weatherImageView!!.setImageResource(R.drawable.weather_icon_6)
                    weatherImageView2!!.setImageResource(R.drawable.weather_icon_6)
                }
                6 -> {
                    weatherImageView!!.setImageResource(R.drawable.weather_icon_7)
                    weatherImageView2!!.setImageResource(R.drawable.weather_icon_7)
                }
                else -> {
                    weatherImageView!!.setImageResource(R.drawable.weather_icon_1)
                    weatherImageView2!!.setImageResource(R.drawable.weather_icon_1)
                }
            }
        }

        fun setLayoutType(layoutType: Int) {
            if (layoutType == 0) {
                layout1!!.visibility = View.VISIBLE
                layout2!!.visibility = View.GONE
            } else if (layoutType == 1) {
                layout1!!.visibility = View.GONE
                layout2!!.visibility = View.VISIBLE
            }
        }
    }







