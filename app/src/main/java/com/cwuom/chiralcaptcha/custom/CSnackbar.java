package com.cwuom.chiralcaptcha.custom;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cwuom.chiralcaptcha.R;

public class CSnackbar {
    private View snackbarView;
    private TextView snackbarText;
    private Context context;

    public CSnackbar(Context context) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        snackbarView = inflater.inflate(R.layout.layout_snackbar_view, null);
        snackbarText = snackbarView.findViewById(R.id.tv_snackbar);
    }

    public void show(String message, int duration) {
        snackbarText.setText(message);
        // 设置背景颜色
        snackbarView.setBackgroundColor(Color.parseColor("#00000000"));
        // 创建 Toast 并设置视图
        Toast toast = new Toast(context);
        toast.setDuration(duration);
        toast.setView(snackbarView);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 30); // 设置位置
        toast.show();
    }
}
