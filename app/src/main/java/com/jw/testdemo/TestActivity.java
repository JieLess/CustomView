package com.jw.testdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Administrator on 2017/2/10.
 */

public class TestActivity extends AppCompatActivity {

    AvatarListView mAvatar, mAvatar2;
    AvatarView mAvatar3, mAvatar4;
    SwitchButton switchButton;
    GradientProgressBar progressbar;
    AvatarSimpleView mAvatar5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_test);

        mAvatar = (AvatarListView) findViewById(R.id.avatar);
        mAvatar2 = (AvatarListView) findViewById(R.id.avatar2);
        mAvatar3 = (AvatarView) findViewById(R.id.avatar3);
        mAvatar4 = (AvatarView) findViewById(R.id.avatar4);
        mAvatar5 = (AvatarSimpleView) findViewById(R.id.avatar5);
        switchButton = (SwitchButton) findViewById(R.id.switch_button);
        progressbar = (GradientProgressBar) findViewById(R.id.progressbar);

        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        Bitmap avatar1 = BitmapFactory.decodeResource(getResources(), R.drawable.headshow1);
        Bitmap avatar2 = BitmapFactory.decodeResource(getResources(), R.drawable.headshow2);
        Bitmap avatar3 = BitmapFactory.decodeResource(getResources(), R.drawable.headshow3);
        Bitmap avatar4 = BitmapFactory.decodeResource(getResources(), R.drawable.headshow4);
        Bitmap avatar5 = BitmapFactory.decodeResource(getResources(), R.drawable.headshow5);
        Bitmap avatar6 = BitmapFactory.decodeResource(getResources(), R.drawable.headshow6);
        bitmaps.add(avatar1);
        bitmaps.add(avatar2);
        bitmaps.add(avatar3);
        bitmaps.add(avatar4);
        bitmaps.add(avatar5);
        bitmaps.add(avatar6);

        mAvatar.setImageBitmaps(bitmaps);
        mAvatar2.setImageBitmaps(bitmaps);

        mAvatar3.setAvatarBitmap(avatar4);
        mAvatar3.setText("燕赵歌");
        mAvatar3.setOnline(true);
        mAvatar3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAvatar3.setCheck(!mAvatar3.isCheck());
            }
        });

        mAvatar4.setAvatarBitmap(avatar1);
        mAvatar4.setText("越前龙马");
        mAvatar4.setOnline(true);
        mAvatar4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAvatar4.setCheck(!mAvatar4.isCheck());
            }
        });

        mAvatar5.setAvatarBitmap(avatar6);

        /*switchButton.setChecked(true);
        switchButton.isChecked();
        switchButton.toggle();     //switch state
        switchButton.toggle(false);//switch without animation
        switchButton.setShadowEffect(true);//disable shadow effect
        switchButton.setEnabled(false);//disable button
        switchButton.setEnableEffect(false);//disable the switch animation
        switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                //TODO do your job
            }
        });*/

        progressbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressbar.setProgress(new Random().nextInt(100));
            }
        });
    }
}
