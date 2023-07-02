package com.zmci.safetymonitoringapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter

class ViewPagerAdapter(val context: Context) : PagerAdapter() {

    var layoutInflater: LayoutInflater? = null

    // arrays of image, heading, description
    val imgArray = arrayOf(
        R.drawable.software_engineer,
        R.drawable.howto1,
        R.drawable.howto2,
        R.drawable.howto3,
        R.drawable.howto4,
        R.drawable.howto5,
        R.drawable.undraw_organize_photos,
    )

    val titleArray = arrayOf(
        "WELCOME",
        "INSTRUCTIONS",
        "SET UP DEVICE",
        "CONNECT TO INTERNET",
        "ADD A DEVICE",
        "MONITORING",
        "REMINDERS"
    )

    val arrayDescription = arrayOf(
        "Please read the instructions ahead to learn on how to use the application.",
        "To setup the device:\nPosition the monitoring device in an area with proper lighting.",
        "Setup the monitoring device. Attach it to the tripod and position to your desired angle.",
        "Make sure that the mobile application and device are connected to the internet",
        "Go to the 'Home' page and add a camera by clicking the button with '+' sign.",
        "Open the device and start monitoring.",
        "You can view the detection logs via 'Logs' or clicking the 'View Logs' in each devices."
    )

    override fun getCount(): Int {
        return titleArray.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object` as LinearLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater!!.inflate(R.layout.slider_layout,container,false)

        val img = view.findViewById<ImageView>(R.id.titleImage)
        val textTitle = view.findViewById<TextView>(R.id.textTitle)
        val textDescription = view.findViewById<TextView>(R.id.textDescription)

        img.setImageResource(imgArray[position])
        textTitle.text = titleArray[position]
        textDescription.text = arrayDescription[position]

        container.addView(view)

        return view

    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }

}