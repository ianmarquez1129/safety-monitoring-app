package com.zmci.safetymonitoringapp

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator


class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val relativeLayout = findViewById<RelativeLayout>(R.id.layout1)
        val dotsIndicator = findViewById<DotsIndicator>(R.id.dots_indicator)

        val animationDrawable = relativeLayout.background as AnimationDrawable
        addAnimation(animationDrawable)

        viewPager = findViewById(R.id.slideViewPager)
        viewPagerAdapter = ViewPagerAdapter(this)
        viewPager.adapter = viewPagerAdapter

        dotsIndicator.attachTo(viewPager)

        viewPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        relativeLayout.setBackgroundResource(R.drawable.gradient_animation123)
                        val animationDrawable = relativeLayout.background as AnimationDrawable
                        addAnimation(animationDrawable)
                    }
                    1 -> {
                        relativeLayout.setBackgroundResource(R.drawable.gradient_animation456)
                        val animationDrawable = relativeLayout.background as AnimationDrawable
                        addAnimation(animationDrawable)
                    }
                    2 -> {
                        relativeLayout.setBackgroundResource(R.drawable.gradient_animation789)
                        val animationDrawable = relativeLayout.background as AnimationDrawable
                        addAnimation(animationDrawable)
                    }
                    else -> {
                        relativeLayout.setBackgroundResource(R.drawable.gradient_animation123)
                        val animationDrawable = relativeLayout.background as AnimationDrawable
                        addAnimation(animationDrawable)
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        })

        val btnSkip = findViewById<Button>(R.id.btnSkip)
        btnSkip.setOnClickListener {
            val i = Intent(this,MainActivity2::class.java)
            startActivity(i)
            finish()
        }

    }

    fun addAnimation(animationDrawable: AnimationDrawable){
        animationDrawable.setEnterFadeDuration(2500)
        animationDrawable.setExitFadeDuration(1000)
        animationDrawable.start()
    }

}