package com.harmazing.intelligentpow.UI;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.harmazing.intelligentpow.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import java.util.FormattableFlags;

/**
 * Created by JTL on 2014/9/17.
 * 基于侧滑的一个activity
 */
public class BaseActivity extends SlidingFragmentActivity {
    protected ListFragment mFrag;
    private SlidingMenu.CanvasTransformer mTransformer;
    public BaseActivity() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //侧滑动画，一个Zoom效果
        mTransformer = new SlidingMenu.CanvasTransformer(){
            @Override
            public void transformCanvas(Canvas canvas, float percentOpen) {
                float scale = (float) (percentOpen*0.25 + 0.75);
                canvas.scale(scale, scale, canvas.getWidth()/2, canvas.getHeight()/2);
            }

        };
        // set the Behind View
        setBehindContentView(R.layout.menu_frame);
        if (savedInstanceState == null) {
            FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
            mFrag = new PersonDataListFragment();
            t.replace(R.id.listview, mFrag);
            t.commit();

            mFrag = (ListFragment) this.getSupportFragmentManager().findFragmentById(R.id.listview);
        }
//        ImageView imageView = (ImageView) findViewById(R.id.head);
//        imageView.setOnClickListener(personClickListener);
        // customize the SlidingMenu
        SlidingMenu sm = getSlidingMenu();
        sm.setMode(SlidingMenu.LEFT);
        sm.setShadowWidthRes(R.dimen.shadow_width);
        sm.setShadowDrawable(R.drawable.shadow);
        sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        sm.setFadeDegree(0.35f);
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_actionbar_titile));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sm.setBehindCanvasTransformer(mTransformer);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                toggle();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            toggle();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.main_aty, menu);

        return true;
    }
//    public void changeFragementContent(int Rid, Fragment fragment){
//          Fragment from = getSupportFragmentManager().findFragmentById(R.id.content_frame);
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
////        if (from == null){
////                         ft.replace(Rid, fragment);
////                         ft.addToBackStack(null);
////                         ft.commit();
////        }
////        else{
//            if(!fragment.isAdded())
//                ft.hide(from).add(Rid,fragment);
//            else
//                ft.hide(from).show(fragment);
//
//                ft.commit();
//        }

//    }
//    View.OnClickListener personClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            Fragment newContent = new PersonFragment();
//            changeFragementContent(R.id.content_frame, newContent);
//        }
//    };
}
