package cn.onestravel.one.navigation.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout

import cn.onestravel.bottomview.demo.R
import cn.onestravel.one.navigation.view.OneBottomNavigationBar
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        oneBottomLayout.setMenu(R.menu.navigation_menu)
        oneBottomLayout.setFragmentManager(fragmentManager, mainFragment )
        oneBottomLayout.addFragment(R.id.tab1, FirstFragment())
        oneBottomLayout.addFragment(R.id.tab2, SecondFragment())
        oneBottomLayout.addFragment(R.id.tab3, ThirdFragment())
        oneBottomLayout.addFragment(R.id.tab4, FourFragment())
        oneBottomLayout.addFragment(R.id.tab5, FiveFragment())
        oneBottomLayout.setFloatingEnable(true)
        oneBottomLayout.setItemIconTint(R.drawable.item_check)
        oneBottomLayout.setItemColorStateList(R.drawable.item_check)
        oneBottomLayout.setMsgCount(0, 32)
        oneBottomLayout.setMsgCount(4, 111)
        oneBottomLayout.setMsgCount(3, 1)
        oneBottomLayout.setMsgCount(1, -1)
        oneBottomLayout.setOnItemSelectedListener { item, position ->
            if (position == 1) {
                oneBottomLayout.setFloatingEnable(true)
            } else {
                oneBottomLayout.setFloatingEnable(false)
            }
        }

    }


}
