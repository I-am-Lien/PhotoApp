package com.li.photoeditorapp.ultils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.HandlerThread;
import android.view.View;
import android.widget.Toast;

import com.li.photoeditorapp.R;
import com.li.photoeditorapp.common.Constanst;
import com.li.photoeditorapp.data.base.ImgEditedDatabase;
import com.li.photoeditorapp.data.base.entity.ImageEdited;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;

public class ImageUtils {

    public static Bitmap getImageBitMap(Uri uri, Context context) {
        Bitmap bitmap = null;
        try {
            InputStream is = context.getContentResolver().openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(is);
            is.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;

    }
    public static Bitmap copyBitMap(Bitmap originBitMap)
    {
        final CountDownLatch latch = new CountDownLatch(1);
        final Bitmap[] value = new Bitmap[1];
        Thread uiThread = new HandlerThread(Constanst.KEY_HANDLER){
            @Override
            public void run(){
                value[0] = originBitMap.copy(Bitmap.Config.ARGB_8888, true);
                latch.countDown();
            }
        };
        uiThread.start();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return value[0];
    }
    public static void saveImageEdited(Activity context, Bitmap bitmap) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String path = Parser.getInstance().bitMapToString(context, bitmap);
                ImgEditedDatabase.getInstance(context).getImageDao().insertImage(new ImageEdited(path));
            }
        });
        thread.start();
    }

    public static void saveImage(Context context,Bitmap bitmap) {
        FileOutputStream outStream = null;
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard.getAbsolutePath() + Constanst.IMAGE_PATH);
        dir.mkdirs();
        String fileName = String.format("%d.jpg", System.currentTimeMillis());
        File outFile = new File(dir, fileName);
        try {
            outStream = new FileOutputStream(outFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            Toast.makeText(context, context.getResources().getString(R.string.Saved), Toast.LENGTH_LONG).show();
            outStream.flush();
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context,  context.getResources().getString(R.string.UnSaved), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, context.getResources().getString(R.string.UnSaved), Toast.LENGTH_LONG).show();
        }
    }
    public static Bitmap viewToImage(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);

        return returnedBitmap;
    }


}
