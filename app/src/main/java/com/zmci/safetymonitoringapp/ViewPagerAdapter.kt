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
        R.drawable.undraw_cloud_sync_re_02p1,
        R.drawable.undraw_dashboard_re_3b76,
        R.drawable.undraw_organize_photos_re_ogcy,
        R.drawable.undraw_personal_data_re_ihde
    )

    val titleArray = arrayOf(
        "Cloud",
        "Dashboard",
        "Organize",
        "Personal data"
    )

    val arrayDescription = arrayOf(
        "Setup the monitoring device. Attach it to the tripod and position to your desired angle.",
        "Make sure that the mobile application is connected to the internet of the monitoring device.",
        "Go to the 'Detection' page and add a camera by clicking the button with '+' sign.",
        "This is a description of privacy policy."
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