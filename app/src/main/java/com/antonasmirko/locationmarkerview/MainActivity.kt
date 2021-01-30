package com.antonasmirko.locationmarkerview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.antonasmirko.locationmarkerview.views.LocationMarkerView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val lMView = findViewById<LocationMarkerView>(R.id.location_view_marker)
        val left =
            lMView.mCenterX + lMView.stepFourthTransition * lMView.mDurationSecondTransition * lMView.mCountSecondTransition

    }

}