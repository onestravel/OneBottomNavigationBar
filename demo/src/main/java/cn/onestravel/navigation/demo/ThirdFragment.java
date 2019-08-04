package cn.onestravel.navigation.demo;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import cn.onestravel.bottomview.demo.R;

/**
 * @author onestravel
 * @createTime 2019-08-04 11:52
 * @description TODO
 */
public class ThirdFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container,false);
        TextView tv = view.findViewById(R.id.tv);
        tv.setText("ThirdFragment");
        return view;
    }
}
