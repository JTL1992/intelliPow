package com.harmazing.intelligentpow.UI;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.harmazing.intelligentpow.R;
import com.harmazing.intelligentpow.tools.HttpHead;
import com.harmazing.intelligentpow.tools.HttpUtil;
import com.harmazing.intelligentpow.tools.LogUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class ForgetSecretAty extends Activity {
    ImageView btnBack;
    Button btnSubmit;
    EditText number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_secret_layout);
        initViews();
        initClickItems();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.forget_secret_aty, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void initViews(){
        btnBack = (ImageView) findViewById(R.id.btn_back);
        btnSubmit = (Button) findViewById(R.id.btn_submit_number);
        number = (EditText) findViewById(R.id.edit_number);
    }
    public void initClickItems(){
        final ProgressDialog progressDialog = new ProgressDialog(ForgetSecretAty.this);
        progressDialog.setMessage("正在为您发送邮件。。。");
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = number.getText().toString();
                RequestParams params = new RequestParams();
                params.put("userCode",phoneNumber);
                if (!phoneNumber.equals("")&&phoneNumber.length() == 11){
                    LogUtil.v("发送邮件",HttpHead.secHead);
                    HttpUtil.post(HttpHead.secHead, params, new JsonHttpResponseHandler() {
                                @Override
                                public void onStart() {
                                    progressDialog.show();
                                    super.onStart();
                                }

                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    super.onSuccess(statusCode, headers, response);
                                    LogUtil.v("secret", response.toString() + statusCode);
                                    try {
                                        Toast.makeText(getApplicationContext(), response.getString("msg"), Toast.LENGTH_LONG).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable
                                        throwable, JSONObject errorResponse) {
                                    super.onFailure(statusCode, headers, throwable, errorResponse);
                                    LogUtil.v("secret", errorResponse.toString() + statusCode);
                                }

                                @Override
                                public void onFinish() {
                                    progressDialog.dismiss();
                                    super.onFinish();
                                }
                            }
                    );
//                    Toast.makeText(getApplicationContext(),"正在努力coding.....",Toast.LENGTH_LONG).show();
                    }
                    else{
                    Toast.makeText(getApplicationContext(),"请输入正确的手机号.",Toast.LENGTH_LONG).show();
                }

            }
        });

    }

}
