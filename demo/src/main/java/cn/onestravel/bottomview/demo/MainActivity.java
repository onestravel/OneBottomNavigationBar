package cn.onestravel.bottomview.demo;

import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import cn.onestravel.bottomview.BottomView;
import cn.onestravel.bottomview.utils.EventUtils;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((BottomView) findViewById(R.id.BottomLayout)).setOnItemSelectedListener(new BottomView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(BottomView.Item item, int position) {
                Toast.makeText(MainActivity.this, "点击了第" + position + "个", Toast.LENGTH_SHORT).show();
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
