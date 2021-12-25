package com.example.shiyan21;

import android.os.Bundle;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

//主活动类
public class MainActivity extends BaseActivity{
    private List<Integer> IDList = new ArrayList<>();
    private List<String> TADList = new ArrayList<>();
    ArrayAdapter simpleAdapter;
    Button ButtonSeek;
    EditText EditTextSeek;
    String EditTextSeekString ;
    Intent intent1;
    String author;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intent1= getIntent();
        author=intent1.getStringExtra("author");
        InitNote();
        Button ButtonAdd;
        ButtonAdd = (Button)findViewById(R.id.ButtonAdd);
        ButtonAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, Add.class);
                intent.putExtra("author",author);
                startActivity(intent);
            }
        });

        simpleAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1,TADList);  //配置适配器
        ListView ListView = (ListView)findViewById(R.id.ListView);
        ListView.setAdapter(simpleAdapter);       //将两个list中的值通过ArrayList显示出来


        ButtonSeek = findViewById(R.id.ButtonSeek);
        EditTextSeek = findViewById(R.id.EditTextSeek);
        ButtonSeek.setOnClickListener(new View.OnClickListener(){       //点击跳转查询界面
            @Override
            public void onClick(View v){
                EditTextSeekString="";
                EditTextSeekString = String.valueOf(EditTextSeek.getText());
                //Log.d("title is ",EditTextSeekString);
                if(EditTextSeekString.length()==0){             //查询为空，给出提示信息
                    RefreshTADList();
                    Toast.makeText(MainActivity.this,"查询值不能为空",Toast.LENGTH_LONG).show();
                }
                else{           //否则通过intent给查询界面传入查询的title
                    Intent intent = new Intent(MainActivity.this, Research.class);
                    //intent.putExtra("tranTitle",EditTextSeekString);
                    intent.putExtra("tranTitletoRE",EditTextSeekString);
                    startActivity(intent);

                }
            }
        });

        ListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){      //配置ArrayList点击按钮
            @Override
            public void  onItemClick(AdapterView<?> parent, View view , int position , long id){
                int tran = IDList.get(position);        //点击不同的行，返回不同的id，根据ID对应其相应的文本
                Intent intent = new Intent(MainActivity.this, Edit.class);
                intent.putExtra("tran",tran);
                startActivity(intent);      //通过intent传输
            }
        });
    }

    private void InitNote() {       //进行数据填装
        MyDataBaseHelper dbHelper = new MyDataBaseHelper(this,"Diary.db",null,1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();     //通过dbhelper获得可写文件
        Cursor cursor  = db.rawQuery("select * from Diary",null);
        IDList.clear();
        TADList.clear();        //清空两个list
        while(cursor.moveToNext()){
            int id=cursor.getInt(cursor.getColumnIndex("id"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String name = cursor.getString(cursor.getColumnIndex("author"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            if(name.equals(author)){
                IDList.add(id);
                TADList.add(title + "\n" + date);      //对两个list填充数据
            }

        }
    }

    public void RefreshTADList(){       //返回该界面时刷新的方法
        int size = TADList.size();
        //if(size>0){
        TADList.removeAll(TADList);
        IDList.removeAll(IDList);
        simpleAdapter.notifyDataSetChanged();       //清空两个list中的值
        //}
        MyDataBaseHelper dbHelper = new MyDataBaseHelper(this,"Diary.db",null,1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();         //实例化SQLitedatabase
        Cursor cursor  = db.rawQuery("select * from Diary",null);
        while(cursor.moveToNext()){         //对两个list重新赋予值
            int id=cursor.getInt(cursor.getColumnIndex("id"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String name = cursor.getString(cursor.getColumnIndex("author"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            if(name.equals(author)){
                IDList.add(id);
                TADList.add(title+"\n"+ date);      //对两个list填充数据
            }
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        RefreshTADList();       //调用刷新方法
    }

}