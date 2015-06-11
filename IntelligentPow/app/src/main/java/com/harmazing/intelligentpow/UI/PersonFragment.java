package com.harmazing.intelligentpow.UI;



import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.harmazing.intelligentpow.R;
import com.harmazing.intelligentpow.tools.AppConfig;

/**
 * A simple {@link android.app.Fragment} subclass.
 *个人信息界面的Fragment
 */
public class PersonFragment extends android.support.v4.app.Fragment {
     private TextView name, mobile,type,bizArea,eleArea,address,email,ammeter;

    public PersonFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_person, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        name = (TextView) getActivity().findViewById(R.id.name_u);
        mobile = (TextView) getActivity().findViewById(R.id.num_u);
         type = (TextView) getActivity().findViewById(R.id.edit1);
        bizArea = (TextView) getActivity().findViewById(R.id.edit2);
        eleArea = (TextView) getActivity().findViewById(R.id.edit3);
        address = (TextView) getActivity().findViewById(R.id.edit4);
        email = (TextView) getActivity().findViewById(R.id.edit5);
        ammeter = (TextView) getActivity().findViewById(R.id.edit6);
        //根据登陆界面的返回消息来更改界面信息,如果没有则从AppConfig取
        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null){
        String nameString = bundle.getString("name");
        String mobileString = bundle.getString("mobile");
        String typeString = bundle.getString("type");
        String bizAreaString = bundle.getString("bizArea");
        String eleAreaString = bundle.getString("eleArea");
        String addressString = bundle.getString("address");
        String emailString = bundle.getString("email");
        String ammeterString = bundle.getString("ammeter");
        name.setText(nameString);
            AppConfig.getInstance().setName(nameString);
        mobile.setText(mobileString);
            AppConfig.getInstance().setMobil(mobileString);
        type.setText(typeString);
            AppConfig.getInstance().setType(typeString);
        bizArea.setText(bizAreaString);
            AppConfig.getInstance().setBizarea(bizAreaString);
        eleArea.setText(eleAreaString);
            AppConfig.getInstance().setElearea(eleAreaString);
        address.setText(addressString);
            AppConfig.getInstance().setAddress(addressString);
        email.setText(emailString);
            AppConfig.getInstance().setEmail(emailString);
        ammeter.setText(ammeterString);
            AppConfig.getInstance().setAmmeter(ammeterString);
        }
        else{
            name.setText(AppConfig.getInstance().getName());
            mobile.setText(AppConfig.getInstance().getMobil());
            type.setText(AppConfig.getInstance().getType());
            bizArea.setText(AppConfig.getInstance().getBizarea());
            eleArea.setText(AppConfig.getInstance().getElearea());
            address.setText(AppConfig.getInstance().getAddress());
            email.setText(AppConfig.getInstance().getEmail());
            ammeter.setText(AppConfig.getInstance().getAmmeter());
        }
    }
}
