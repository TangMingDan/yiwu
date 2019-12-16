package com.example.yiwu.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
/**
 * Created by 朱旭辉
 * Time  2019/8/17
 * Describe:基础Fragment，所有基础Fragment继承它
 */
public abstract class BaseFragment extends Fragment {
    View view;
    public static final String TAG = BaseFragment.class.getSimpleName();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /**
         * 因为FragmentTabHost下一次再切换回第一个tab的时候还是重新初始化页面，
         * 走进onCreateView方法，
         * 但不同的是他不会再走进onCreate方法。
         * 由此可知，我们fragment里面的对象其实还没有被销毁，只不过view重新绘制了，
         * 我们可以把第一次加载的view存起来，
         * 然后进行判断，这样就可以保存状态了。
         * 看起来非常简单嘛
         */
        if (view==null){
            view = inflater.inflate(getContentResourseId(),null);
            initDate();
            initView();
            return view;
        }else {
            return view;
        }


    }
    protected abstract void initView();//初始化控件
    protected abstract void initDate();//初始话数据
    protected abstract int getContentResourseId();//返回映射ID



}
