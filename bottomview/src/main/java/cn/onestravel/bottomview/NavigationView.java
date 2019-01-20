package cn.onestravel.bottomview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.MenuRes;
import android.support.constraint.ConstraintLayout;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.LinearLayout;

/**
 * @author onestravel
 * @createTime 2019/1/20 9:48 AM
 * @description TODO
 */
public class NavigationView extends ConstraintLayout implements MenuBuilder.ItemInvoker, MenuView {

    public NavigationView(Context context) {
        super(context);
        init(context,null,0);
    }

    public NavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs,0);
    }

    public NavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs,defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        if(attrs!=null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.StyleNavigationView);
            int menuRes = ta.getResourceId(R.styleable.StyleNavigationView_menu,0);
            if(menuRes!=0){
                Menu view = (Menu) LayoutInflater.from(context).inflate(menuRes,null);
                int count = view.size();
            }
        }
    }


    @SuppressLint("RestrictedApi")
    @Override
    public boolean invokeItem(MenuItemImpl menuItem) {
        Log.e("Navigation",menuItem.getTitle().toString());
        return false;
    }

    @Override
    public void initialize(MenuBuilder menuBuilder) {
        Log.e("Navigation",menuBuilder.toString());
    }

    @Override
    public int getWindowAnimations() {
        return 0;
    }
}
