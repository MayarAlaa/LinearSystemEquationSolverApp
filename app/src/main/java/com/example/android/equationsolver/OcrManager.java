package com.example.android.equationsolver;

import android.graphics.Bitmap;

import com.googlecode.tesseract.android.TessBaseAPI;


/**
 * Created by Lenovo on 4/18/2018.
 */

public class OcrManager {
    TessBaseAPI baseAPI = null;

    public void initAPI()
    {
        baseAPI = new TessBaseAPI();

        String dataPath = TessOCR.instance.getTessDataParentDirectory();
        baseAPI.init(dataPath,"eng");

    }



    public String startRecognize(Bitmap bitmap)
    {
        if(baseAPI ==null)
            initAPI();
        baseAPI.setImage(bitmap);
        return baseAPI.getUTF8Text();
    }

}
