package com.infinite.android.camscanner;

import android.app.Activity;
import android.app.Notification;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by ujwalv on 11/2/2015.
 */
public class ImageTest extends Activity {

    String TAG = "ImageTest";
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imagetest);
        ImageView ivTest = (ImageView) findViewById(R.id.ivTest);


        Uri myUri = Uri.parse(getIntent().getExtras().getString("image"));
        //Uri myUri = Uri.parse(getIntent().getExtras().getString("image"));

        this.getContentResolver().notifyChange(myUri, null);
        ContentResolver cr = this.getContentResolver();

        try
        {
            //bitmap = android.provider.MediaStore.Images.Media.getBitmap(this.getContentResolver(), myUri);
            bitmap = bitmapSampling(myUri);
            ivTest.setImageBitmap(bitmap);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Failed to load", e);
        }
    }

    public Bitmap bitmapSampling(Uri image){
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            String realPath = getRealPathFromURI(getApplicationContext(), image);
            Log.i(TAG, "URI path: " + realPath);

            // File path = new File(image.getPath());
            BitmapFactory.decodeFile(realPath, options);

            int scale = 1;
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;
            Log.i(TAG,"WIDTH: "+width+" HEIGHT:"+height);
            while (options.outWidth / scale / 2 >= width && options.outHeight / scale / 2 >= height) {
                scale = scale * 2;
            }
            Log.i(TAG,"Scale ="+scale);

            BitmapFactory.Options options1 = new BitmapFactory.Options();
            options1.inSampleSize = scale;
            return BitmapFactory.decodeFile(realPath, options1);
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG, "Error Sampling bitmap");
        }
        return null;
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bitmap.recycle();
    }
}
