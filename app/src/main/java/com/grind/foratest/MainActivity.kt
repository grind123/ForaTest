package com.grind.foratest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceControl
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.FragmentTransitionImpl
import com.grind.foratest.fragments.SearchFragment

class MainActivity : AppCompatActivity(){



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        if(supportFragmentManager.fragments.size == 0) {
            supportFragmentManager.beginTransaction()
                .add(R.id.main_container, SearchFragment())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack("dsad")
                .commit()
        }

    }

}
