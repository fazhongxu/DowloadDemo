package com.xxl.viewfindlib;

import android.app.Activity;
import android.view.View;

/**
 * Created by xxl on 2017/10/12.
 * findViewById辅助类
 */

public class ViewFinder {
    private Activity mActivity;
    private View mView;

    public ViewFinder(Activity activity) {
        this.mActivity = activity;
    }

    public ViewFinder(View view) {
        this.mView = view;
    }

    public View findViewById(int id) {
        return mActivity != null ? mActivity.findViewById(id) : mView.findViewById(id);
    }
}
