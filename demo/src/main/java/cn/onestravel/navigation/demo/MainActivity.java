package cn.onestravel.navigation.demo;

import android.content.res.Resources;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import cn.onestravel.bottomview.demo.R;
import cn.onestravel.navigation.utils.DensityUtils;
import cn.onestravel.navigation.utils.EventUtils;
import cn.onestravel.navigation.view.BottomNavigationBar;


public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private BottomNavigationBar bottomView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = ((TextView) findViewById(R.id.tv));
        bottomView = ((BottomNavigationBar) findViewById(R.id.BottomLayout));
        bottomView.setMenu(R.menu.navigation_menu);
//        bottomView.setFloatingEnable(false);
        bottomView.setItemIconTint(R.drawable.item_check);
        bottomView.setItemColorStateList(R.drawable.item_check);
        bottomView.setMsgCount(0,32);
        bottomView.setMsgCount(4,111);
        bottomView.setMsgCount(3,1);
        bottomView.setMsgCount(1,-1);
        bottomView.setOnItemSelectedListener(new BottomNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(BottomNavigationBar.Item item, int position) {
                textView.setText(item.getTitle());
                if(position==2){
                    bottomView.setFloatingEnable(true);
//                    bottomView.setFloatingUp(DensityUtils.dpToPx(getApplication(),20));
                }else {
                    bottomView.setFloatingEnable(false);
//                    bottomView.setFloatingUp(DensityUtils.dpToPx(getApplication(),0));
                }
            }
        });
    }




}
