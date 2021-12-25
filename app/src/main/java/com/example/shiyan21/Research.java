package com.example.shiyan21;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Date;

public class Research extends BaseActivity implements OnClickListener{  //搜索记事本文本类，设置监听器
    Button ButtonDelete,ButtonSave,ButtonCancel,insert_img;
    EditText EditTextContent,EditTextTitle,EditTextREAuthor;
    ImageView imageView;
    String tranTitle;
    String Author;
    String imgP;
    MyDataBaseHelper dbHelper = new MyDataBaseHelper(this,"Diary.db",null,1);

    private void InitNote() {
        MyDataBaseHelper dbHelper = new MyDataBaseHelper(this,"Diary.db",null,1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();     //同上，获得可写文件
        Cursor cursor  = db.query("Diary",new String[]{"id","title","content","author","imgP"},
                "title=?",new String[]{tranTitle+""},null,null,null);

        if(cursor.moveToNext()) {       //逐行查找，得到匹配信息
            do {
                String Title = cursor.getString(cursor.getColumnIndex("title"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                Author = cursor.getString(cursor.getColumnIndex("author"));
                imgP = cursor.getString(cursor.getColumnIndex("imgP"));
                Bitmap bitmap = BitmapFactory.decodeFile(imgP);
                imageView.setImageBitmap(bitmap);
                EditTextREAuthor.setText(Author);
                EditTextContent.setText(content);
                EditTextTitle.setText(Title);
            } while (cursor.moveToNext());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) { //初始化控件
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_research);
        EditTextContent = (EditText)findViewById(R.id.EditTextREEditContent);
        EditTextTitle = (EditText)findViewById(R.id.EditTextREEditTitle) ;
        ButtonCancel = (Button)findViewById(R.id.ButtonRECancel);
        ButtonSave = (Button)findViewById(R.id.ButtonRESave);
        ButtonDelete = (Button)findViewById(R.id.ButtonREDelete);
        EditTextREAuthor = findViewById(R.id.EditTextREAuthor);
        insert_img=(Button)findViewById(R.id.insert_img);
        imageView=(ImageView)findViewById(R.id.image_view);

        //设置监听器
        ButtonCancel.setOnClickListener(this);
        ButtonSave.setOnClickListener(this);
        ButtonDelete.setOnClickListener(this);
        insert_img.setOnClickListener(this);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        tranTitle = extras.getString("tranTitletoRE");      //接受主界面传来的title值

        InitNote();


    }
    @Override
    public void onClick(View v){  //点击事件
        switch (v.getId()){

            case R.id.ButtonREDelete:       //删除该标题的日志
                Log.d("title is ",tranTitle);

                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.delete("Diary","title=?",new String[]{tranTitle+""});     //进行字符串匹配
                Research.this.setResult(RESULT_OK,getIntent());
                Research.this.finish();
                break;
            case R.id.ButtonRESave:         //将文本界面内容保存
                SQLiteDatabase db1 = dbHelper.getWritableDatabase();        //获取可写文件
                Date date = new Date();
                ContentValues values = new ContentValues();         //获取信息
                String Title = String.valueOf(EditTextTitle.getText());
                String Content = String.valueOf(EditTextContent.getText());
                if(Title.length()==0){
                    Toast.makeText(this, "请输入一个标题", Toast.LENGTH_LONG).show();
                }else {
                    values.put("title", Title);         //填装信息
                    values.put("content", Content);
                    values.put("imgP",imgP);
                    Author = String.valueOf(EditTextREAuthor.getText());
                    values.put("author",Author);
                    db1.update("Diary", values, "title=?", new String[]{tranTitle + ""});    //字符串匹配
                    Research.this.setResult(RESULT_OK, getIntent());     //返回主界面
                    Research.this.finish();
                }

                break;


            case R.id.ButtonRECancel:
                Research.this.setResult(RESULT_OK,getIntent());
                Research.this.finish();
                break;
            case R.id.insert_img:
                Intent intent=new Intent(Research.this,Picture.class);
                startActivityForResult(intent,1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if(resultCode ==RESULT_OK){
                    imgP = data.getStringExtra("imgP");
                    Bitmap bitmap = BitmapFactory.decodeFile(imgP);
                    imageView.setImageBitmap(bitmap);
                }
                break;
            default:
        }
    }
}