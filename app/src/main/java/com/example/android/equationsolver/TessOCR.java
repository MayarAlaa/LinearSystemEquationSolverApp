package com.example.android.equationsolver;

import android.app.Application;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Lenovo on 4/18/2018.
 */

public class TessOCR extends Application {

    public static TessOCR instance =null;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        copyTessDataForTextRecognizor();
    }

    private String tessDataPath()
    {
        return TessOCR.instance.getExternalFilesDir(null)+"/tessdata/";
    }

    public String getTessDataParentDirectory()
    {
        return TessOCR.instance.getExternalFilesDir(null).getAbsolutePath();
    }

    private void copyTessDataForTextRecognizor()
    {

        Runnable run = new Runnable() {
            @Override
            public void run() {
                AssetManager assetManager = TessOCR.instance.getAssets();
                OutputStream out =null;
                try {
                    InputStream in = assetManager.open("eng.traineddata");
                    String tesspath = instance.tessDataPath();
                    File tessFolder = new File(tesspath);
                    if (!tessFolder.exists())
                        tessFolder.mkdir();
                    String tessData = tesspath + "/" + "eng.traineddata";
                    File tessFile = new File(tessData);
                    if (!tessFile.exists()) {
                        out = new FileOutputStream(tessData);
                        byte[] buffer = new byte[1024];
                        int read = in.read(buffer);
                        while (read != -1) {
                            out.write(buffer, 0, read);
                            read = in.read(buffer);
                        }
                        Log.d("TessOCR", " finish copy tess file  ");


                    } else
                        Log.d("TessOCR", " tess file exist  ");

                } catch (Exception e)
                {
                    Log.d("TessOCR", "couldn't copy with the following error : "+e.toString());
                }finally {
                    try {
                        if(out!=null)
                            out.close();
                    }catch (Exception exx)
                    {

                    }
                }
            }
        };
        new Thread(run).start();
    }
}





















