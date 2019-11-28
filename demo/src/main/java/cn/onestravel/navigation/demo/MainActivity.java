package cn.onestravel.navigation.demo;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import cn.onestravel.bottomview.demo.R;
import cn.onestravel.navigation.view.OneBottomNavigationBar;


public class MainActivity extends AppCompatActivity {

    private OneBottomNavigationBar bottomView;
    private FrameLayout mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mainFragment = ( findViewById(R.id.main_fragment));
        bottomView = ((OneBottomNavigationBar) findViewById(R.id.BottomLayout));
        bottomView.setMenu(R.menu.navigation_menu);
        bottomView.setFragmentManager(getFragmentManager(),mainFragment);
        bottomView.addFragment(R.id.tab1,new FirstFragment());
        bottomView.addFragment(R.id.tab2,new SecondFragment());
        bottomView.addFragment(R.id.tab3,new ThirdFragment());
        bottomView.addFragment(R.id.tab4,new FourFragment());
        bottomView.addFragment(R.id.tab5,new FiveFragment());
        bottomView.setFloatingEnable(true);
        bottomView.setItemIconTint(R.drawable.item_check);
        bottomView.setItemColorStateList(R.drawable.item_check);
        bottomView.setMsgCount(0,32);
        bottomView.setMsgCount(4,111);
        bottomView.setMsgCount(3,1);
        bottomView.setMsgCount(1,-1);
        bottomView.setOnItemSelectedListener(new OneBottomNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(OneBottomNavigationBar.Item item, int position) {
                if(position==1){
                    bottomView.setFloatingEnable(true);
                }else {
                    bottomView.setFloatingEnable(false);
                }
            }
        });
    }




}
