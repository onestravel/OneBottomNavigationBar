package cn.onestravel.one.navigation.demo

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import cn.onestravel.bottomview.demo.R
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * @author onestravel
 * @createTime 2019-08-04 11:52
 * @description TODO
 */
class FirstFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    fun init() {
        tvName?.let { it.text = "FirstFragment" }
    }
}
