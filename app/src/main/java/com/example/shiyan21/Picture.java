package com.example.shiyan21;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Picture extends AppCompatActivity {  //图片类，对图片进行处理
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    private String imgP;
    private ImageView picture;
    private Uri imageUri;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {  //初始化控件
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        Button takePhoto=(Button)findViewById(R.id.take_photo);
        Button chooseFromAlbum=(Button)findViewById(R.id.choose_from_album);
        Button submit=(Button)findViewById(R.id.submit);
        picture=(ImageView)findViewById(R.id.picture);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        String data = pref.getString("data","");
        if(data != ""){
            Bitmap bitmap = BitmapFactory.decodeFile(data);
            imgP=data;
            picture.setImageBitmap(bitmap);
        }
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  //设置监听器
                //创建File对象，用于存储拍照后的图片
                File outputImage=new File(getExternalCacheDir(),"output_image.jpg");
                try {
                    if(outputImage.exists()){
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(Build.VERSION.SDK_INT >= 24){
                    imageUri = FileProvider.getUriForFile(Picture.this,
                            "com.example.shiyan21.fileprovider",outputImage);//查找存储在File对象中的图片URL地址
                }else {
                    imageUri = Uri.fromFile(outputImage);
                }

                //启动相机程序
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,TAKE_PHOTO);
            }
        });

        chooseFromAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   //选择相机或相册的点击事件监听器
                if (ContextCompat.checkSelfPermission(Picture.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(Picture.this,new
                            String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else {
                    openAlbum();
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("imgP",imgP);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }


    /*****************************************************************************************************************/
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        //将拍摄的图片显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().
                                openInputStream(imageUri));
                        picture.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if(resultCode == RESULT_OK){
                    //判断手机系统版本号
                    if(Build.VERSION.SDK_INT >= 19){
                        //4.4以上系统使用此方法处理图片
                        handleImageOnKitKat(data);
                    }else{
                        //4.4以下系统使用此方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                }
            default:
                break;
        }
    }
    /*****************************************************************************************************************/
    private void handleImageBeforeKitKat(Intent data) {
        Uri uri=data.getData();
        String imagePath = getImagePath(uri,null);
        displayImage(imagePath);
    }

    private void handleImageOnKitKat(Intent data) {         //返回图片URL路径
        String imagePath = null;
        Uri uri = data.getData();
        if(DocumentsContract.isDocumentUri(this,uri)){
            //如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id=docId.split(":")[1];//解析出数字格式的id
                String selection=MediaStore.Images.Media._ID+"="+id;
                imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri= ContentUris.withAppendedId(Uri.parse("content:" +
                        "//downloads/public_downloads"),Long.valueOf(docId));
                imagePath=getImagePath(contentUri,null);
            }else if("context".equalsIgnoreCase(uri.getScheme())){
                //如果是content类型的Uri，则用普通方式处理
                imagePath=getImagePath(uri,null);
            }else if("file".equalsIgnoreCase(uri.getScheme())){
                //如果是file类型的Uri，直接获取图片路径即可
                imagePath = uri.getPath();
            }
            displayImage(imagePath);//根据图片路径显示图片
        }
    }

    private String getImagePath(Uri uri, String selection) {
        String path=null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
    private void displayImage(String imagePath) {
        if(imagePath != null){
            editor=pref.edit();
            editor.putString("data",imagePath);
            imgP=imagePath;
            editor.apply();
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            picture.setImageBitmap(bitmap);

        }else {
            Toast.makeText(this,"fail to get image",Toast.LENGTH_SHORT).show();
        }
    }

    private void openAlbum(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);    //打开相册
    }

    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults){
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else {
                    Toast.makeText(this,"You denied this permission",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

}