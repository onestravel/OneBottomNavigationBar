package cn.onestravel.one.navigation.demo

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import cn.onestravel.bottomview.demo.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.oneBottomLayout
import kotlinx.android.synthetic.main.activity_main_viewpager.*


class MainViewPagerActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main_viewpager)

        oneBottomLayout.setMenu(R.menu.navigation_menu)

        oneBottomLayout.attachViewPager(supportFragmentManager,viewPager, listOf(FirstFragment(),FiveFragment(),FirstFragment(),FiveFragment()))
        oneBottomLayout.setFloatingEnable(true)
        oneBottomLayout.setTopLineColor(Color.RED)
        oneBottomLayout.setItemColorStateList(R.drawable.item_check)
        oneBottomLayout.setMsgCount(0, 32)
        oneBottomLayout.setMsgCount(2, 1)
        oneBottomLayout.setMsgCount(1, -1)
        oneBottomLayout.setOnItemSelectedListener { item, position ->
//            if (position == 1) {
//                oneBottomLayout.setFloatingEnable(true)
//            } else {
//                oneBottomLayout.setFloatingEnable(false)
//            }
            false
        }



    }


}
