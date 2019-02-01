package cn.onestravel.navigation.demo;

import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import cn.onestravel.bottomview.demo.R;
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
        bottomView.setMsgCount(0,32);
        bottomView.setMsgCount(4,111);
        bottomView.setMsgCount(3,1);
        bottomView.setMsgCount(1,-1);
        bottomView.setOnItemSelectedListener(new BottomNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(BottomNavigationBar.Item item, int position) {
                textView.setText(item.getTitle());
            }
        });
    }


    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        double x = (double) motionEvent.getRawX();
        double y = (double) motionEvent.getRawY() ;

        int action = motionEvent.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                if(EventUtils.isInViewZone(findViewById(R.id.BottomLayout),x,y)){
                    findViewById(R.id.BottomLayout).dispatchTouchEvent(motionEvent);
                }
                break;
        }
        return true;
    }


    private boolean isInChangeImageZone(View view, double x, double y) {
        Rect mChangeImageBackgroundRect = new Rect();
        view.getDrawingRect(mChangeImageBackgroundRect);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        mChangeImageBackgroundRect.left = location[0];
        mChangeImageBackgroundRect.top = location[1];
        mChangeImageBackgroundRect.right = mChangeImageBackgroundRect.right + location[0];
        mChangeImageBackgroundRect.bottom = mChangeImageBackgroundRect.bottom + location[1];
        return mChangeImageBackgroundRect.contains((int)(x), (int)y);
    }

}
