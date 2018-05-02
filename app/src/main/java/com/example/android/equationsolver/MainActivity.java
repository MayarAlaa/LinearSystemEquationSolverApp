package com.example.android.equationsolver;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Bitmap bitmap;
    String textRecognized ;
    Bitmap bitmap_invert;
    Bitmap resultBitmap ;
    public static final int REQUEST_CAPTURE = 1;


  //  to check if OpenCV is loaded
    private static String TAG = "MAIN ACTIVITY";

    static {
        if(OpenCVLoader.initDebug())
        {
            Log.i(TAG , "SUCCESSFUL");
          // Mat  mat = new Mat();
        }
        else
        {
            Log.i(TAG , "UNSUCCESSFUL");
        }
    }

    //A method to negate the image
    private static final int RGB_MASK = 0x00FFFFFF;

    public Bitmap invert(Bitmap original) {
        // Create mutable Bitmap to invert, argument true makes it mutable
        Bitmap inversion = original.copy(android.graphics.Bitmap.Config.ARGB_8888, true);

        // Get info about Bitmap
        int width = inversion.getWidth();
        int height = inversion.getHeight();
        int pixels = width * height;

        // Get original pixels
        int[] pixel = new int[pixels];
        inversion.getPixels(pixel, 0, width, 0, 0, width, height);

        // Modify pixels
        for (int i = 0; i < pixels; i++)
            pixel[i] ^= RGB_MASK;
        inversion.setPixels(pixel, 0, width, 0, 0, width, height);

        // Return inverted Bitmap
        return inversion;
    }

    //A method to enhance the image using OpenCV
    public Bitmap processImage(Bitmap bm)
    {
                  //convert Bitmap to Mat to work with OpenCV functions
            Mat mat = new Mat();
           Bitmap bmp32 = bm.copy(android.graphics.Bitmap.Config.ARGB_8888, true);
            Utils.bitmapToMat(bmp32, mat);

           //Dilate the mat
            Imgproc.dilate(mat, mat, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2, 2)));


           //convert from Mat to Bitmap to pass it to the recognzie text function
            // Android follows RGB color convention, but OpenCV follows BGR color convention

            Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY, 4);

            Utils.matToBitmap(mat, bmp32);

       return bmp32;
    }


    public String Equate(String Equation) {

        Pattern p1 = Pattern.compile("(-?\\d*)\\s?(X|x)?\\s?(\\+)?\\s?(-?\\d+)\\s?=\\s?(-?\\d+)");  //one eqn
        Pattern p2 = Pattern.compile("(-?\\d*)\\s?(X|x)?\\s?(\\+|-)?\\s?(\\d*)\\s?(Y|y)?=\\s?(-?\\d+)\\s+(-?\\d*)\\s?(X|x)?\\s?(\\+|-)?\\s?(\\d*)\\s?(Y|y)?=\\s?(-?\\d+)"); //two eqns
        Pattern p3 = Pattern.compile("(-?\\d*)\\s?(X|x)?\\s?(\\+|-)?\\s?(\\d*)\\s?(Y|y)?\\s?(\\+|-)?\\s?(\\d*)\\s?(Z|z)?=\\s?(-?\\d+)\\s+(-?\\d*)\\s?(X|x)?\\s?(\\+|-)?\\s?(\\d*)\\s?(Y|y)?\\s?(\\+|-)?\\s?(\\d*)\\s?(Z|z)?=\\s?(-?\\d+)\\s+(-?\\d*)\\s?(X|x)?\\s?(\\+|-)?\\s?(\\d*)\\s?(Y|y)?\\s?(\\+|-)?\\s?(\\d*)\\s?(Z|z)?=\\s?(-?\\d+)"); //three eqns
        Matcher m1 = p1.matcher(Equation);
        Matcher m2 = p2.matcher(Equation);
        Matcher m3 = p3.matcher(Equation);

        double a1 = 0, b1 = 0, c1 = 0, d1=0;
        double a2 = 0 , b2 = 0,c2 = 0, d2 =0;
        double x = 0 , y=0 , z=0;
        double[] [] xm = new double[3][3];
        double[] [] ym = new double[3][3];
        double[] [] zm = new double[3][3];
        double [] [] d = new double [3][3];

        if (m1.find()){
            if (m1.group(1).equals(""))
                a1=1;
            else
                a1 = Double.valueOf(m1.group(1));

            b1 = Double.valueOf(m1.group(4));

            c1 = Double.valueOf(m1.group(5));

            x = (c1 - b1) / a1;

            return (" x =  " + x );

        }

        if (m2.find()) {
            if (m2.group(1).equals(""))
                a1=1;
            else if (m2.group(2).equals(""))
                a1=0;
            else
                a1 = Double.valueOf(m2.group(1));

            if (m2.group(4).equals(""))
                b1=1;
            else if (m2.group(5).equals(""))
                b1=0;
            else
                b1 = Double.valueOf(m2.group(4));
            if(m2.group(3).equals("-"))
                b1= -b1;

            c1 = Double.valueOf(m2.group(6));



            if (m2.group(7).equals(""))
                a2=1;
            else if (m2.group(8).equals(""))
                a2=0;
            else
                a2 = Double.valueOf(m2.group(7));

            if (m2.group(10).equals(""))
                b2=1;
            else if (m2.group(11).equals(""))
                b2=0;
            else
                b2 = Double.valueOf(m2.group(10));
            if (m2.group(9).equals("-"))
                b2 = -b2;

            c2 = Double.valueOf(m2.group(12));

            x = ((c1*b2) - (b1*c2)) / ((a1*b2) - (b1*a2));
            y = ((a1*c2) - (c1*a2)) / ((a1*b2) - (b1*a2));
            return (" x =  " + x  + "\n y = " + y);
        }

        if (m3.find()){

            //## d's
            xm[0][0]=Double.valueOf(m3.group(9));
            xm[1][0]=Double.valueOf(m3.group(18));
            xm[2][0]=Double.valueOf(m3.group(27));
            //## b's
            if (m3.group(4).equals(""))
                xm[0][1]=1;
            else if (m3.group(5).equals(""))
                xm[0][1]=0;
            else
                xm[0][1]=Double.valueOf(m3.group(4));
            if(m3.group(3).equals("-"))
                xm[0][1]= -xm[0][1];
            //##
            if (m3.group(13).equals(""))
                xm[1][1]=1;
            else if (m3.group(14).equals(""))
                xm[1][1]=0;
            else
                xm[1][1]=Double.valueOf(m3.group(13));
            if(m3.group(12).equals("-"))
                xm[1][1]= -xm[1][1];
            //##
            if (m3.group(22).equals(""))
                xm[2][1]=1;
            else if (m3.group(23).equals(""))
                xm[2][1]=0;
            else
                xm[2][1]=Double.valueOf(m3.group(22));
            if(m3.group(21).equals("-"))
                xm[2][1]= -xm[2][1];

            //## c's
            if (m3.group(7).equals(""))
                xm[0][2]=1;
            else if (m3.group(8).equals(""))
                xm[0][2]=0;
            else
                xm[0][2]=Double.valueOf(m3.group(7));
            if(m3.group(6).equals("-"))
                xm[0][2]= -xm[0][2];
            //##
            if (m3.group(16).equals(""))
                xm[1][2]=1;
            else if (m3.group(17).equals(""))
                xm[1][2]=0;
            else
                xm[1][2]=Double.valueOf(m3.group(16));
            if(m3.group(15).equals("-"))
                xm[1][2]= -xm[1][2];
            //##
            if (m3.group(25).equals(""))
                xm[2][2]=1;
            else if (m3.group(26).equals(""))
                xm[2][2]=0;
            else
                xm[2][2]=Double.valueOf(m3.group(25));
            if(m3.group(24).equals("-"))
                xm[2][2]= -xm[2][2];
////////////////////////////////////////////////////////////////////////////////
            //******* a's
            if (m3.group(1).equals(""))
                ym[0][0]=1;
            else if (m3.group(2).equals(""))
                ym[0][0]=0;
            else
                ym[0][0]=Double.valueOf(m3.group(1));

            if (m3.group(10).equals(""))
                ym[1][0]=1;
            else if (m3.group(11).equals(""))
                ym[1][0]=0;
            else
                ym[1][0]=Double.valueOf(m3.group(10));

            if (m3.group(19).equals(""))
                ym[2][0]=1;
            else if (m3.group(20).equals(""))
                ym[2][0]=0;
            else
                ym[2][0]=Double.valueOf(m3.group(19));

            //*********************

            ym[0][1]=Double.valueOf(m3.group(9));
            ym[1][1]=Double.valueOf(m3.group(18));
            ym[2][1]=Double.valueOf(m3.group(27));
            //***************************************
            if (m3.group(7).equals(""))
                ym[0][2]=1;
            else if (m3.group(8).equals(""))
                ym[0][2]=0;
            else
                ym[0][2]=Double.valueOf(m3.group(7));
            if(m3.group(6).equals("-"))
                ym[0][2]= -ym[0][2];
            //##
            if (m3.group(16).equals(""))
                ym[1][2]=1;
            else if (m3.group(17).equals(""))
                ym[1][2]=0;
            else
                ym[1][2]=Double.valueOf(m3.group(16));
            if(m3.group(15).equals("-"))
                ym[1][2]= -ym[1][2];
            //##
            if (m3.group(25).equals(""))
                ym[2][2]=1;
            else if (m3.group(26).equals(""))
                ym[2][2]=0;
            else
                ym[2][2]=Double.valueOf(m3.group(25));
            if(m3.group(24).equals("-"))
                ym[2][2]= -ym[2][2];
////////////////////////////////////////////////////////////////////////

            //^^^
            if (m3.group(1).equals(""))
                zm[0][0]=1;
            else if (m3.group(2).equals(""))
                zm[0][0]=0;
            else
                zm[0][0]=Double.valueOf(m3.group(1));

            if (m3.group(10).equals(""))
                zm[1][0]=1;
            else if (m3.group(11).equals(""))
                zm[1][0]=0;
            else
                zm[1][0]=Double.valueOf(m3.group(10));

            if (m3.group(19).equals(""))
                zm[2][0]=1;
            else if (m3.group(20).equals(""))
                zm[2][0]=0;
            else
                zm[2][0]=Double.valueOf(m3.group(19));

            //^^^^

            if (m3.group(4).equals(""))
                zm[0][1]=1;
            else if (m3.group(5).equals(""))
                zm[0][1]=0;
            else
                zm[0][1]=Double.valueOf(m3.group(4));
            if(m3.group(3).equals("-"))
                zm[0][1]= -zm[0][1];
            //##
            if (m3.group(13).equals(""))
                zm[1][1]=1;
            else if (m3.group(14).equals(""))
                zm[1][1]=0;
            else
                zm[1][1]=Double.valueOf(m3.group(13));
            if(m3.group(12).equals("-"))
                zm[1][1]= -zm[1][1];
            //##
            if (m3.group(22).equals(""))
                zm[2][1]=1;
            else if (m3.group(23).equals(""))
                zm[2][1]=0;
            else
                zm[2][1]=Double.valueOf(m3.group(22));
            if(m3.group(21).equals("-"))
                zm[2][1]= -zm[2][1];
            //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
            zm[0][2]=Double.valueOf(m3.group(9));
            zm[1][2]=Double.valueOf(m3.group(18));
            zm[2][2]=Double.valueOf(m3.group(27));
            /////////////////////////////////////////////////////////////////////
            if (m3.group(1).equals(""))
                d[0][0]=1;
            else if (m3.group(2).equals(""))
                d[0][0]=0;
            else
                d[0][0]=Double.valueOf(m3.group(1));

            if (m3.group(10).equals(""))
                d[1][0]=1;
            else if (m3.group(11).equals(""))
                d[1][0]=0;
            else
                d[1][0]=Double.valueOf(m3.group(10));

            if (m3.group(19).equals(""))
                d[2][0]=1;
            else if (m3.group(20).equals(""))
                d[2][0]=0;
            else
                d[2][0]=Double.valueOf(m3.group(19));
            //|||||||||||||||||||||||||||||||||||||||||||||||
            if (m3.group(4).equals(""))
                d[0][1]=1;
            else if (m3.group(5).equals(""))
                d[0][1]=0;
            else
                d[0][1]=Double.valueOf(m3.group(4));
            if(m3.group(3).equals("-"))
                d[0][1]= -d[0][1];
            //##
            if (m3.group(13).equals(""))
                d[1][1]=1;
            else if (m3.group(14).equals(""))
                d[1][1]=0;
            else
                d[1][1]=Double.valueOf(m3.group(13));
            if(m3.group(12).equals("-"))
                d[1][1]= -d[1][1];
            //##
            if (m3.group(22).equals(""))
                d[2][1]=1;
            else if (m3.group(23).equals(""))
                d[2][1]=0;
            else
                d[2][1]=Double.valueOf(m3.group(22));
            if(m3.group(21).equals("-"))
                d[2][1]= -d[2][1];
            //||||||||||||||||||||||||||||||||||||||||||
            if (m3.group(7).equals(""))
                d[0][2]=1;
            else if (m3.group(8).equals(""))
                d[0][2]=0;
            else
                d[0][2]=Double.valueOf(m3.group(7));
            if(m3.group(6).equals("-"))
                d[0][2]= -d[0][2];
            //##
            if (m3.group(16).equals(""))
                d[1][2]=1;
            else if (m3.group(17).equals(""))
                d[1][2]=0;
            else
                d[1][2]=Double.valueOf(m3.group(16));
            if(m3.group(15).equals("-"))
                d[1][2]= -d[1][2];
            //##
            if (m3.group(25).equals(""))
                d[2][2]=1;
            else if (m3.group(26).equals(""))
                d[2][2]=0;
            else
                d[2][2]=Double.valueOf(m3.group(25));
            if(m3.group(24).equals("-"))
                d[2][2]= -d[2][2];
////////////////////////////////////////////////////////////////
            x = (det(xm))/(det(d));
            y = (det(ym))/(det(d));
            z = (det(zm))/(det(d));
            return (" x = " + x + "\n y = " + y + "\n z = " + z );
        }
        else
            return ("error: recapture photo");

    }

    double det(double a[][])
    {
        double x=a[0][0]*((a[1][1]*a[2][2])-(a[2][1]*a[1][2]));
        double y=-a[0][1]*((a[1][0]*a[2][2])-(a[2][0]*a[1][2]));
        double z=a[0][2]*((a[1][0]*a[2][1])-(a[1][1]*a[2][0]));

        double r=x+y+z;
        return r;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button btnCamera = (Button) findViewById(R.id.btnCamera);
        imageView = (ImageView) findViewById(R.id.imageView);

        if (!hasCamera()) {
            btnCamera.setEnabled(false);
        }

        // when recgonize button is clicked , an instance of OCRManager is created
        // and we call its 2 methods initAPI and startRecognize
        Button btnRecognize = (Button) findViewById(R.id.btnRecognize) ;
        final TextView textView = (TextView)findViewById(R.id.textView);
       // final TextView textView2 = (TextView)findViewById(R.id.textView2);

        btnRecognize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OcrManager manager = new OcrManager();
                manager.initAPI();
                //////////////////////////////////////////////////////////////////
                //the following 2 lines are to transform the image to bitmap and shall be removed when the camera works.
             //  Drawable myDrawable = getResources().getDrawable(R.drawable.eqn1);
               // Bitmap bitmap = ((BitmapDrawable) myDrawable).getBitmap();
                ////////////////////////////////////////////////////////////////
                textRecognized = manager.startRecognize(bitmap_invert);
               textView.setText(Equate(textRecognized));
               // textView2.setText(textRecognized);
            }

        });
    }
        public boolean hasCamera(){

          return  getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
        }

        public void LaunchCamera(View v){

            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(i,REQUEST_CAPTURE);

        }
 //Alt + insert then choose override methods then onActivityResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CAPTURE && resultCode == RESULT_OK && data != null)
        {
          Bundle extras = data.getExtras();
          bitmap = (Bitmap)extras.get("data");

            // CONVERTING BITMAP TO negative image
           bitmap_invert =invert(bitmap);

           //Do some image processing
          resultBitmap = processImage(bitmap_invert);

            //set it to the imageView
            imageView.setImageBitmap(bitmap);



        }

    }





    }



