package com.example.shiyan21;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Date;

public class Edit extends BaseActivity implements OnClickListener{  //编辑活动类
    Button ButtonDelete,ButtonSave,ButtonCancel,insert_img;
    EditText EditTextContent,EditTextTitle,EditTextEditAuthor;
    ImageView imageView;
    int tran = 0;
    String Author="";
    String imgP;
    MyDataBaseHelper dbHelper = new MyDataBaseHelper(this,"Diary.db",null,1);

    private void InitNote() {       //进行数据填装
        MyDataBaseHelper dbHelper = new MyDataBaseHelper(this,"Diary.db",null,1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor  = db.query("Diary",new String[]{"id","title","author","content","imgP"},
                "id=?",new String[]{tran+""},null,null,null);
        if(cursor.moveToNext()) {       //根据mainactivity传来的id值选择数据库中对应的行，将值返回
            do {
                String Title = cursor.getString(cursor.getColumnIndex("title"));
                Author = cursor.getString(cursor.getColumnIndex("author"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                imgP = cursor.getString(cursor.getColumnIndex("imgP"));
                Bitmap bitmap = BitmapFactory.decodeFile(imgP);
                imageView.setImageBitmap(bitmap);
                EditTextContent.setText(content);
                EditTextTitle.setText(Title);
                EditTextEditAuthor.setText(Author);
            } while (cursor.moveToNext());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        EditTextContent = (EditText)findViewById(R.id.EditTextEditContent);
        EditTextTitle = (EditText)findViewById(R.id.EditTextEditTitle) ;
        ButtonCancel = (Button)findViewById(R.id.ButtonCancel);
        ButtonSave = (Button)findViewById(R.id.ButtonSave);
        ButtonDelete = (Button)findViewById(R.id.ButtonDelete);
        EditTextEditAuthor = findViewById(R.id.EditTextEditAuthor);
        insert_img = (Button)findViewById(R.id.insert_img);
        imageView = (ImageView)findViewById(R.id.image_view);

        ButtonCancel.setOnClickListener(this);
        ButtonSave.setOnClickListener(this);
        ButtonDelete.setOnClickListener(this);
        insert_img.setOnClickListener(this);

        Intent intent = getIntent();
        tran = intent.getIntExtra("tran",-1);       //取出mainactivity传来的id值

        InitNote();


    }
    @Override
    public void onClick(View v){
        switch (v.getId()){

            case R.id.ButtonDelete:     //将对应的id行删除

                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.delete("Diary","id=?",new String[]{tran+""});
                Edit.this.setResult(RESULT_OK,getIntent());
                Edit.this.finish();
                break;
            case R.id.ButtonSave:       //保存该界面的数据
                SQLiteDatabase db1 = dbHelper.getWritableDatabase();
                Date date = new Date();
                ContentValues values = new ContentValues();
                String Title = String.valueOf(EditTextTitle.getText());
                String Content = String.valueOf(EditTextContent.getText());
                if(Title.length()==0){
                    Toast.makeText(this, "请输入一个标题", Toast.LENGTH_LONG).show();
                }else {
                    values.put("title", Title);
                    values.put("content", Content);
                    values.put("imgP",imgP);
                    db1.update("Diary", values, "id=?", new String[]{tran + ""});        //对数据进行更新
                    Edit.this.setResult(RESULT_OK, getIntent());
                    Edit.this.finish();
                }

                break;


            case R.id.ButtonCancel:
                Edit.this.setResult(RESULT_OK,getIntent());
                Edit.this.finish();
                break;
            case R.id.insert_img:
                Intent intent=new Intent(Edit.this,Picture.class);
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