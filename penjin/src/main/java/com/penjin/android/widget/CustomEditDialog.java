package com.penjin.android.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.penjin.android.R;

/**
 * Created by maotiancai on 2016/1/13.
 */
public class CustomEditDialog extends Dialog implements View.OnClickListener {

    private TextView title;
    private EditText editText;
    private Button cancle;
    private Button ok;

    private String titleName = null;
    private String editContent = null;

    private CustomEditDialogListner customEditDialogListnerlistner;

    public CustomEditDialog(Context context, int themeResId, String title, String editContent) {
        super(context, themeResId);
        this.titleName = title;
    }

    public CustomEditDialog(Context context) {
        super(context);
    }

    public CustomEditDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected CustomEditDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_add_chuchai);
        initView();
    }

    private void initView() {
        title = (TextView) findViewById(R.id.title);
        editText = (EditText) findViewById(R.id.editText);
        ok = (Button) findViewById(R.id.ok);
        cancle = (Button) findViewById(R.id.cancel);
        ok.setOnClickListener(this);
        cancle.setOnClickListener(this);

     /*   if (titleName != null) {
            this.title.setText(titleName);
        }
        if (editContent != null) {
            this.editText.setHint(editContent);
        }*/
    }

    @Override
    public void show() {
        super.show();
        if (titleName != null) {
            this.title.setText(titleName);
        }
        if (editContent != null) {
            this.editText.setText(null);
            this.editText.setHint(editContent);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok:
                if (this.customEditDialogListnerlistner != null) {
                    this.customEditDialogListnerlistner.ok(this.editText.getEditableText().toString(), null);
                }
                break;
            case R.id.cancel:
                if (this.customEditDialogListnerlistner != null) {
                    this.customEditDialogListnerlistner.cancel(null);
                }
                break;
            default:
                break;
        }
    }

    public interface CustomEditDialogListner {

        public void ok(String msg, View view);

        public void cancel(String msg);

    }

    public void setCustomEditDialogListnerlistner(CustomEditDialogListner customEditDialogListnerlistner) {
        this.customEditDialogListnerlistner = customEditDialogListnerlistner;
    }

    public void setHint(String hint) {
        this.editContent = hint;
    }

    public void setTitle(String title) {
        this.titleName = title;
    }
}
