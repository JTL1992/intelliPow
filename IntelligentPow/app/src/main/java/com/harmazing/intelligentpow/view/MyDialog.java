package com.harmazing.intelligentpow.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.harmazing.intelligentpow.R;
import com.harmazing.intelligentpow.UI.LoginAty;
import com.harmazing.intelligentpow.tools.HttpHead;

/**
 * Created by JTL on 2015/3/30.
 * 废弃了（不使用）
 */
public class MyDialog extends Dialog{
    private Context context;
    private OnConfirmListener cf;
    private OnPickerListener pl;
   public String content;
    private int i;
    AlertDialog myDialog;
    public MyDialog(Context context){
        super(context);
        this.context = context;
         myDialog  =  new AlertDialog.Builder(context).create();
    }
    public void setOnConfirmListener(OnConfirmListener cl){
        cf = cl;
    }
    public void setOnPickerListener(OnPickerListener pl){
        this.pl = pl;
    }
    public void picker(){
        if (pl != null){
            pl.onChoose(this,i);
        }
    }
    public void confirm(){
        if (cf != null)
            cf.onConfirm(this,content);
    }
    public void showMyDialog(String title){
        myDialog.show();
        android.view.Window window = myDialog.getWindow(); //获取窗口
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        window.setContentView(R.layout.dialog_change_title);//为窗口添加layout
        TextView tx = (TextView) window.findViewById(R.id.this_title);
        tx.setText(title);
        final EditText editText = (EditText) window.findViewById(R.id.name_door_and_win);
        Button confirm = (Button) window.findViewById(R.id.btn_confirm);
        Button cancel = (Button) window.findViewById(R.id.btn_cancel);
        //取消按钮响应事件
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
            }
        });
        if (title.equals("重新设置域名")) {
            editText.setHint("当前域名为：" + HttpHead.forhead);
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    content = editText.getText().toString();
                    confirm();
                    myDialog.dismiss();
                }
            });
        }

    }
    public interface OnPickerListener{
        public void onChoose(MyDialog myDialog, int i);
    }
}
