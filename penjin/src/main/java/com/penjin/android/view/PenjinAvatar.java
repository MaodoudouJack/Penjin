package com.penjin.android.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.media.Image;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.penjin.android.R;

import org.w3c.dom.Text;

/**
 * Created by maotiancai on 2015/12/21.
 */
public class PenjinAvatar extends FrameLayout {

    private int defaultColor;
    private String defaultName="昵称";

    private ImageView avatarImage;
    private TextView avatarName;

    public PenjinAvatar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context,attrs);
    }

    public PenjinAvatar(Context context) {
        super(context);
    }

    public PenjinAvatar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context,attrs);
    }

    private void initView(Context context,AttributeSet attributeSet)
    {
        TypedArray typedArray=context.obtainStyledAttributes(attributeSet,R.styleable.PenjinAvatar);
        try {
            defaultColor=typedArray.getColor(R.styleable.PenjinAvatar_pjAvatarColor, context.getResources().getColor(R.color.btn_green_noraml));
            String name=typedArray.getString(R.styleable.PenjinAvatar_pjAvatarName);
            if(name!=null)
            {
                defaultName=name;
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }finally {
            typedArray.recycle();
        }
        LayoutInflater.from(context).inflate(R.layout.layout_widget_penjinavatar,this);
        avatarImage=(ImageView)getRootView().findViewById(R.id.avatarImage);
        avatarName=(TextView)getRootView().findViewById(R.id.avatorName);

        setAvatarImage(defaultColor);
        setAvatarName(defaultName);
    }



    public void setAvatarImage(int color)
    {
        GradientDrawable shape= (GradientDrawable) avatarImage.getBackground();
        shape.setColor(color);
    }

    public void setAvatarName(String name)
    {
        if (name.length() < 3) {
            avatarName.setText(name);
        } else if (name.length() == 3) {
            String jianStr = name.substring(1);
            avatarName.setText(jianStr);
        } else if (name.length() > 3) {
            String jianStr = name.substring(2);
            avatarName.setText(jianStr);
        }
    }
}
