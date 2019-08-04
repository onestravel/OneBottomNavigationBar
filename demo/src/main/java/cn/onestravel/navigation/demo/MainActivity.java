package cn.onestravel.navigation.demo;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import cn.onestravel.bottomview.demo.R;
import cn.onestravel.navigation.view.BottomNavigationBar;


public class MainActivity extends AppCompatActivity {

    private BottomNavigationBar bottomView;
    private FrameLayout mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mainFragment = ( findViewById(R.id.main_fragment));
        bottomView = ((BottomNavigationBar) findViewById(R.id.BottomLayout));
        bottomView.setMenu(R.menu.navigation_menu);
//        bottomView.setFragmentManager(getFragmentManager(),mainFragment);
        bottomView.addFragment(R.id.tab1,new FirstFragment());
        bottomView.addFragment(R.id.tab2,new SecondFragment());
        bottomView.addFragment(R.id.tab3,new ThirdFragment());
        bottomView.addFragment(R.id.tab4,new FourFragment());
        bottomView.addFragment(R.id.tab5,new FiveFragment());
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
