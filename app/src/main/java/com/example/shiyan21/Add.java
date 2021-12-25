package com.example.shiyan21;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.database.sqlite.SQLiteDatabase;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.lang.String;

//添加活动类
public class Add extends BaseActivity implements OnClickListener{
    String Title,Content,simpleDate,imgP;
    Button ButtonAddCancel,ButtonAddSave,insert_img;
    EditText EditTextAddTitle,EditTextAddContent,EditTextAddAuthor;
    String Author;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        //初始化控件
        ButtonAddCancel = (Button)findViewById(R.id.ButtonAddCancel);
        ButtonAddSave = (Button)findViewById(R.id.ButtonAddSave);
        EditTextAddContent = findViewById(R.id.EditTextAddContent);
        EditTextAddTitle = findViewById(R.id.EditTextAddTitle);
        EditTextAddAuthor = findViewById(R.id.EditTextAddAuthor);
        imageView=(ImageView)findViewById(R.id.image_view);
        insert_img=(Button)findViewById(R.id.insert_img);
        Intent intent1=getIntent();
        Author=intent1.getStringExtra("author");
        EditTextAddAuthor.setText(Author);
        //取消加入按钮、保存按钮、插入图片按钮监听器
        ButtonAddCancel.setOnClickListener(this);
        ButtonAddSave.setOnClickListener(this);
        insert_img.setOnClickListener(this);

    }
    @Override
    public void onClick(View v){  //取消加入按钮、保存按钮、插入图片按钮的点击事件
        MyDataBaseHelper dbHelper = new MyDataBaseHelper(this,"Diary.db",null,1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (v.getId()) {
            case R.id.ButtonAddSave:
                Date date = new Date();  //创建Date对象
                DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//配置时间格式
                simpleDate = simpleDateFormat.format(date);
                ContentValues values = new ContentValues();
                Title = String.valueOf(EditTextAddTitle.getText()); //获取需要储存的值
                Content = String.valueOf(EditTextAddContent.getText());
                Log.d("Title",Title);
                if(Title.length()==0){   //标题为空给出提示
                    Toast.makeText(this, "请输入一个标题", Toast.LENGTH_LONG).show();
                }else {
                    values.put("title", Title);
                    values.put("author",Author);
                    values.put("content", Content);
                    values.put("date", simpleDate);
                    values.put("imgP",imgP);
                    db.insert("Diary", null, values);  //将值传入数据库中
                    Add.this.setResult(RESULT_OK, getIntent());
                    Add.this.finish();
                }
                break;

            case R.id.ButtonAddCancel:
                Add.this.setResult(RESULT_OK,getIntent());
                Add.this.finish();
                break;
            case R.id.insert_img:
                Intent intent=new Intent(Add.this,Picture.class);
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
                break;
        }
    }
}