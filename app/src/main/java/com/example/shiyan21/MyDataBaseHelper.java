package com.example.shiyan21;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//对数据库进行创建，包括记事录的id、文本标题、作者信息、内容信息、图片和数据时间
public class MyDataBaseHelper extends SQLiteOpenHelper{
    public static  final String CREATE_NOTE = "create table Diary("
            + "id integer primary key autoincrement,"
            + "title text not null,"
            + "author text,"
            + "content text,"
            + "imgP text,"
            + "date datetime not null default current_time)";
    private Context mContext;
    public MyDataBaseHelper(Context context,String name,SQLiteDatabase.CursorFactory factory,int version){
        super(context,name,factory,version);
        mContext = context;
    }
    @Override
    public void  onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_NOTE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
    }
}