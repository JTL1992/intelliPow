package com.harmazing.intelligentpow.UI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.harmazing.intelligentpow.R;
import com.harmazing.intelligentpow.tools.AppConfig;

/**
 * Created by JTL on 2014/9/17.
 * 主界面的侧滑菜单Fragment
 */
public class PersonDataListFragment  extends ListFragment {
    String flag;
    SampleAdapter adapter;

    /**
     * 生成list
     * @param inflater 启动器
     * @param container 容器
     * @param savedInstanceState 储存
     * @return list
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list, null);
    }
    /*启动后添加listview内容*/
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListView().setSelector(getResources().getDrawable(R.drawable.bg_selector_person));
        adapter = new SampleAdapter(getActivity());
        adapter.add(new SampleItem("首页", R.drawable.icon_homeback));
        adapter.add(new SampleItem("个人中心", R.drawable.icon_person_list));
        adapter.add(new SampleItem("修改密码", R.drawable.icon_safe_setting));
        adapter.add(new SampleItem("切换账号", R.drawable.icon_changeuser));
        adapter.add(new SampleItem("舒睡曲线", R.drawable.icon_sleep));
        adapter.add(new SampleItem("设置", R.drawable.icon_setting));
//        adapter.add(new SampleItem("修改密码", R.drawable.icon_safe_setting));
//        adapter.add(new SampleItem("切换账号", R.drawable.icon_changeuser));
        adapter.add(new SampleItem("退出登录", R.drawable.icon_out));

        setListAdapter(adapter);
    }

    /**
     * 定义每个list内容
     */
    private class SampleItem {
        public String tag;
        public int iconRes;
        public SampleItem(String tag, int iconRes) {
            this.tag = tag;
            this.iconRes = iconRes;
        }
    }

    /**
     * 为listview写一个适配器SampleAdapter
     */
    public class SampleAdapter extends ArrayAdapter<SampleItem> {

        public SampleAdapter(Context context) {
            super(context, 0);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row, null);
            }

            ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);
            icon.setImageResource(getItem(position).iconRes);
            if(getItem(position).tag == "退出登录") {
                TextView title = (TextView) convertView.findViewById(R.id.row_title);
                title.setText(getItem(position).tag);
                title.setTextColor(Color.rgb(164,41,10));
            }
            else{
                TextView title = (TextView) convertView.findViewById(R.id.row_title);
                title.setText(getItem(position).tag);
            }
            if (getItem(position).tag.equals(flag)){
                TextView title = (TextView) convertView.findViewById(R.id.row_title);
                title.setText(getItem(position).tag);
                title.setTextColor(Color.rgb(255,84,10));
            }
            else {
                TextView title = (TextView) convertView.findViewById(R.id.row_title);
                title.setText(getItem(position).tag);
            }
            return convertView;
        }

    }


    /**
     * listView点击事件的响应
     * @param lv listview
     * @param v  View
     * @param position 位置
     * @param id  id
     */
    @Override
    public void onListItemClick(ListView lv, View v, int position, long id) {
        Fragment newContent = null;

          int itemId = 0;
          switch (position) {
              case 0:
                  newContent = new MainTitleFragement();
                  flag = "首页";
                  setListAdapter(adapter);
                  itemId = 0;
              break;
              case 1:
                  newContent = new PersonFragment();
                  flag = "个人中心";
                  setListAdapter(adapter);
                  itemId = 1;
                  break;

              case 4:
                  newContent = new AboutUsFragment();
                  flag = "舒睡曲线";
                  setListAdapter(adapter);
                  itemId = 4;
               break;
              case 2:
                  newContent = new SafeSettingFragment();
                  flag = "修改密码";
                  setListAdapter(adapter);
                  itemId = 2;
               break;
              case 3:
//                  newContent = new MainAtyFragement();
//                   flag = "切换账号";
//                  setListAdapter(adapter);
                  Intent intent = new Intent(getActivity(),LoginAty.class);
                  intent.putExtra("user",AppConfig.getInstance().getUsername());
                  intent.putExtra("password",AppConfig.getInstance().getPassword());
                  AppConfig.getInstance().setUsername("");
                  AppConfig.getInstance().setPassword("");
                  AppConfig.getInstance().setUserId(null);
//                  AppConfig.getInstance().clear();
                  startActivity(intent);
                  getActivity().finish();
                 break;
              case 5:
                  newContent = new SettingFragment();
                  flag = "设置";
                  setListAdapter(adapter);
                  itemId = 5;
                  break;
              case 6:
//                  newContent = new MainAtyFragement();
                   showExitAlert();
                 break;
          }


             if(newContent != null)
                  switchFragment(newContent ,itemId);

     }
    private void switchFragment(Fragment fragment ,int itemId) {
        if (getActivity() == null)
            return;

        if (getActivity() instanceof MainAty) {
            MainAty fca = (MainAty) getActivity();
            fca.switchContent(fragment, itemId);

        }
    }

    /**
     * 自定义退出提示对话框
     */
    private void showExitAlert() {
        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        dialog.show();
        android.view.Window window = dialog.getWindow();
        window.setContentView(R.layout.dailog_layout);
        Button confirm = (Button) window.findViewById(R.id.btn_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.exit(0); // 退出应用...
            }
        });
        Button cancel = (Button) window.findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
}
