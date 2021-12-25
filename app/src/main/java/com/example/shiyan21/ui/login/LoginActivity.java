package com.example.shiyan21.ui.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;


import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shiyan21.BaseActivity;
import com.example.shiyan21.MainActivity;
import com.example.shiyan21.R;

public class LoginActivity extends BaseActivity {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private EditText accountEdit;
    private EditText passwordEdit;
    private Button login;
    private Button create;
    private String save;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        accountEdit = (EditText)findViewById(R.id.account);
        passwordEdit= (EditText)findViewById(R.id.password);
        login=(Button)findViewById(R.id.login);
        create=(Button)findViewById(R.id.create);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save=pref.getString("save","");
                String account="";
                String password="";
                String message=save;
                int j=0;
                for(int i=0;i<message.length();i++){
                    if(message.charAt(i)==','){
                        account += message.substring(j,i);
                        j=i;
                    }
                    if(message.charAt(i)=='.'){
                        password += message.substring(j,i);
                        j=i;
                    }
                }
                password=password.substring(1);
                String[] n1=account.split("\\.");
                String[] p1=password.split("\\,");
                boolean flag=false;
                String account1 = accountEdit.getText().toString();
                String password1 = passwordEdit.getText().toString();
                for(int i = 0 ; i < n1.length;i++){
                    if(n1[i].equals(account1) && p1[i].equals(password1)){
                        flag=true;
                        break;
                    }
                }
                if (flag){
                    Toast.makeText(LoginActivity.this,"登陆成功",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("author",account1);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "用户名或密码错误",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  //登陆界面监听器
                String account = accountEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                String save1=pref.getString("save","");
                save1 += account+","+password+".";
                editor=pref.edit();
                editor.putString("save",save1);
                editor.apply();
                Toast.makeText(LoginActivity.this,"创建成功！",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("author",account);
                startActivity(intent);
                finish();
            }
        });
    }
}