package com.infinite.android.camscanner;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CamScanner extends Activity implements View.OnClickListener{

    public static String TAG = "CamScanner";
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case LoaderCallbackInterface.SUCCESS:
                    Log.i(TAG, "OCV loaded");
                    //mCamBridge.enableView();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    Uri tempUri;
    Mat tempMat;
    Bitmap tempBitmap;
    Button b1,b2,b3,b4,b5,b6;
    private Mat tempMatOriginal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cam_scanner);
        b1 = (Button) findViewById(R.id.b1);
        b2 = (Button) findViewById(R.id.b2);
        b3 = (Button) findViewById(R.id.b3);
        b4 = (Button) findViewById(R.id.b4);
        b5 = (Button) findViewById(R.id.b5);
        b6 = (Button) findViewById(R.id.b6);

        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        b3.setOnClickListener(this);
        b4.setOnClickListener(this);
        b5.setOnClickListener(this);
        b6.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!OpenCVLoader.initDebug()){
            Log.e(TAG,"OpenCV not found");
        }else{
            Log.i(TAG,"OpenCV found");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onClick(View v) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ContentValues values  = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"NewPicture");
        values.put(MediaStore.Images.Media.DESCRIPTION,"TemporaryImage");
        tempUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);

        switch (v.getId()){
            case R.id.b1:
                startActivityForResult(cameraIntent,1);
                break;
            case R.id.b2:
                startActivityForResult(cameraIntent,2);
                break;
            case R.id.b3:
                startActivityForResult(cameraIntent,3);
                break;
            case R.id.b4:
                startActivityForResult(cameraIntent,4);
                break;
            case R.id.b5:
                startActivityForResult(cameraIntent,5);
                break;
            case R.id.b6:
                startActivityForResult(cameraIntent,6);
                break;
        }
    }

    private File createTempFile(String pic, String s) {


        try {
            File tempDir = Environment.getExternalStorageDirectory();
            tempDir = new File(tempDir.getAbsolutePath()+"/.temp/");
            if(!tempDir.exists()){
                tempDir.mkdir();
            }
            return File.createTempFile(pic,s,tempDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final Intent in1 = new Intent(this, ImageTest.class);


        switch (requestCode){
            case 1://Normal image
                in1.putExtra("image", tempUri.toString());
                break;
            case 2://Grey image
                tempBitmap = getBitmapFromUri(tempUri);
                tempMat = getMatFromBitmap(tempBitmap);
                //Imgproc.cvtColor(tempMat,tempMat, Imgproc.COLOR_BGR2GRAY,4);
                Imgproc.GaussianBlur(tempMat, tempMat, new org.opencv.core.Size(21, 21), 6);          //the size of the kernel(S) is the region around any pixel, that you want interpolated, not the image size ,sigmaX is noting bus kernel in X
                tempBitmap = getBitmapFromMat(tempMat);                                             //This is greyscaled bitmap
                tempUri = getUriFromBitmap(tempBitmap);
                in1.putExtra("image", tempUri.toString());
                break;
            case 3://Canny image
                /*tempBitmap = getBitmapFromUri(tempUri);
                tempMat = getMatFromBitmap(tempBitmap);
                Imgproc.cvtColor(tempMat, tempMat, Imgproc.COLOR_BGR2GRAY, 4);
                Imgproc.GaussianBlur(tempMat, tempMat, new org.opencv.core.Size(1, 1), 5);
                Imgproc.adaptiveThreshold(tempMat, tempMat, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 13, 7);
                *//*double otsu_thresh = Imgproc.threshold(tempMat,tempMat,0,255,Imgproc.THRESH_OTSU);
                double high_thresh  = otsu_thresh;
                double lower_thresh = otsu_thresh * 0.5;*//*
                //Log.i(TAG, "threshold: " + otsu_thresh);
                Imgproc.Canny(tempMat, tempMat, 50, 100);
                tempBitmap = getBitmapFromMat(tempMat);                                             //This is Canny bitmap
                tempUri = getUriFromBitmap(tempBitmap);
                in1.putExtra("image", tempUri.toString());*/


                tempBitmap = getBitmapFromUri(tempUri);
                tempMat = getMatFromBitmap(tempBitmap);
                Imgproc.cvtColor(tempMat, tempMat, Imgproc.COLOR_BGR2GRAY, 4);
                Imgproc.GaussianBlur(tempMat, tempMat, new org.opencv.core.Size(9,9), 0);
                //Try with different C value to reduce noice outside the primary image
                Imgproc.adaptiveThreshold(tempMat, tempMat, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 71, 15);    //Try with different C value to reduce noice outside the primary image
                //Imgproc.adaptiveThreshold(tempMat, tempMat, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 71, 15);    //Try with different C value to reduce noice outside the primary image

                /*MatOfDouble mu= new MatOfDouble();
                MatOfDouble sigma= new MatOfDouble();
                Core.meanStdDev(tempMat,mu,sigma);
                double[] matofD1 = mu.toArray();
                double[] matofD2 = sigma.toArray();
                Imgproc.Canny(tempMat, tempMat, matofD1[0] - matofD2[0], matofD1[0] + matofD2[0]);*/
                Imgproc.Canny(tempMat, tempMat, 80, 100);
                tempBitmap = getBitmapFromMat(tempMat);                                             //This is Canny bitmap
                tempUri = getUriFromBitmap(tempBitmap);
                in1.putExtra("image", tempUri.toString());
                break;
            case 4:
                tempBitmap = getBitmapFromUri(tempUri);
                tempMat = getMatFromBitmap(tempBitmap);
                Imgproc.cvtColor(tempMat, tempMat, Imgproc.COLOR_BGR2GRAY, 4);
                Imgproc.GaussianBlur(tempMat, tempMat, new org.opencv.core.Size(9, 9), 0);
                Imgproc.adaptiveThreshold(tempMat, tempMat, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 71, 15);
                //Imgproc.adaptiveThreshold(tempMat, tempMat, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 71, 15);    //Try with different C value to reduce noice outside the primary image
                //Imgproc.adaptiveThreshold(tempMat, tempMat, 255, 1,1, 11, 2);
                /*double otsu_thresh = Imgproc.threshold(tempMat,tempMat,0,255,Imgproc.THRESH_OTSU);
                double high_thresh  = otsu_thresh;
                double lower_thresh = otsu_thresh * 0.5;
                Log.d(TAG, "H:" + high_thresh + " L:" + lower_thresh);
                Imgproc.Canny(tempMat, tempMat, lower_thresh, high_thresh);*/
                Imgproc.Canny(tempMat, tempMat, 100, 100);
                tempBitmap = getBitmapFromMat(tempMat);                                             //This is Canny bitmap
                tempUri = getUriFromBitmap(tempBitmap);
                in1.putExtra("image", tempUri.toString());
                break;
            case 5:

                new AsyncTask<Void,Void,Uri>(){

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        Toast.makeText(getApplicationContext(),"Processing,please wait...",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    protected Uri doInBackground(Void... params) {

                        tempBitmap = getBitmapFromUri(tempUri);
                        /**
                         * Could'nt do tempMat = tempMat2 = getMatFromBitmap(tempBitmap);
                         * cos tempMat was losing all the details
                         */
                        tempMatOriginal = getMatFromBitmap(tempBitmap);
                        tempMat = getMatFromBitmap(tempBitmap);
                        //tempBitmap.recycle();
                        Imgproc.cvtColor(tempMat, tempMat, Imgproc.COLOR_BGR2GRAY, 4);
                        Imgproc.GaussianBlur(tempMat, tempMat, new org.opencv.core.Size(9, 9), 0);
                        Imgproc.adaptiveThreshold(tempMat, tempMat, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 71, 15);
                        //Imgproc.adaptiveThreshold(tempMat, tempMat, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 31, 1);    //Try with different C value to reduce noice outside the primary image
                        //Imgproc.adaptiveThreshold(tempMat, tempMat, 255, 1,1, 11, 1);    //Try with different C value to reduce noice outside the primary image
                        Imgproc.Canny(tempMat, tempMat, 100, 100);

                        //Try with different C value to reduce noice outside the primary image
                        //Imgproc.adaptiveThreshold(tempMat, tempMat, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 19, 5);
                        //Approach 1
                        //Imgproc.adaptiveThreshold(tempMat, tempMat, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 19, 5);

                        //Approach 2
                /*double otsu_thresh = Imgproc.threshold(tempMat,tempMat,0,255,Imgproc.THRESH_OTSU);
                double high_thresh  = otsu_thresh;
                double lower_thresh = otsu_thresh * 0.5;
                Log.d(TAG,"H:"+high_thresh+" L:"+lower_thresh);
                Imgproc.Canny(tempMat, tempMat, lower_thresh, high_thresh);*/

                        //Approach 3
                /*MatOfDouble mu= new MatOfDouble();
                MatOfDouble sigma= new MatOfDouble();
                Core.meanStdDev(tempMat, mu, sigma);
                double[] matofD1 = mu.toArray();
                double[] matofD2 = sigma.toArray();
                Imgproc.Canny(tempMat, tempMat,matofD1[0]-matofD2[0],matofD1[0]+matofD2[0]);*/

                /*Mat lines = new Mat();
                int threshold = 70;
                int minLineSize = 30;
                int lineGap = 10;
                Imgproc.HoughLinesP(tempMat, lines, 1, Math.PI / 180, threshold,minLineSize, lineGap);*/

                        List<MatOfPoint> contours = new ArrayList<>();

                        Imgproc.findContours(tempMat, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
                        Log.i(TAG,"Contours size: "+contours.size()+"");

                        // MatOfPoint tempContour = contours.get(0);
                        MatOfPoint2f approxCurve1 = new MatOfPoint2f();
                        MatOfPoint2f approxCurve2 = new MatOfPoint2f();

                        double maxArea = 0;
                        double epsilon =0;                      // Epsilon is the approximation curve bw detected contout and the required SQUARE shape
                        //@link [http://docs.opencv.org/master/dd/d49/tutorial_py_contour_features.html#gsc.tab=0]
                        ;

                        for(int i=0;i<contours.size();i++){
                            MatOfPoint tempContour = contours.get(i);
                            double contourArea = Imgproc.contourArea(tempContour);
                            Log.i(TAG,contourArea+"");
                            if(contourArea>maxArea){
                                maxArea =contourArea;

                                tempContour.convertTo(approxCurve1, CvType.CV_32F);
                                epsilon = 0.02*Imgproc.arcLength(approxCurve1,true);
                                int contourSize = (int)approxCurve1.total();
                                Log.i(TAG,"SIZE-"+contourSize+"");
                                Imgproc.approxPolyDP(approxCurve1, approxCurve2, epsilon, true);
                                //Imgproc.approxPolyDP(approxCurve1, approxCurve1, contourSize * 0.05, true);
                                //if(approxCurve1.total()==4){
                                approxCurve2 = approxCurve1;
                                Log.i(TAG, "i= " + i + " Area= "+contourArea+" Sides= "+approxCurve2.total());
                                //}
                            }
                            //compare this contour to the previous largest contour found
                    /*if(contourArea>maxArea){

                        tempContour.convertTo(approxCurve1, CvType.CV_32F);
                        //MatOfPoint2f contoursMat5 = new MatOfPoint2f( contours.get(i));
                       // MatOfPoint2f mat2f = new MatOfPoint2f(tempContour.toArray());
                        MatOfPoint2f new_mat = new MatOfPoint2f( approxCurve1.toArray() );
                        //int contourSize = (int)approxCurve1.total();

                        MatOfPoint2f approxCurve2 = new MatOfPoint2f();
                        //Imgproc.approxPolyDP(contoursMat5, approx, Imgproc.arcLength(contoursMat5, true) * 0.02, true);
                        // Imgproc.approxPolyDP(approxCurve1,approxCurve2,contourSize*0.05,true);
                        //Imgproc.approxPolyDP(approxCurve1, approxCurve1, Imgproc.arcLength(contoursMat5, true) * 0.02, true);
                        epsilon = 0.02*Imgproc.arcLength(approxCurve1,true);


                        Imgproc.approxPolyDP(approxCurve1,approxCurve2,epsilon,true);
                        if(approxCurve2.total()==4){
                            maxArea = contourArea;
                            approxCurve1 = approxCurve2;
                            Log.i(TAG,"Approx area: "+maxArea);
                        }
                    }*/
                        }


                        double[] tempDouble1 = approxCurve2.get(0,0);
                        double[] tempDouble2 = approxCurve2.get(1,0);
                        double[] tempDouble3 = approxCurve2.get(2,0);
                        double[] tempDouble4 = approxCurve2.get(3,0);

                        Log.e(TAG, "\n1)" + tempDouble1[0] + "  " + tempDouble1[1] +
                                "\n2)" + tempDouble2[0] + "  " + tempDouble2[1] +
                                "\n3)" + tempDouble3[0] + "  " + tempDouble3[1] +
                                "\n4)" + tempDouble4[0] + "  " + tempDouble4[1]);

                        List<double[]> sortedIt = findAndSortPositions(tempDouble1, tempDouble2, tempDouble3, tempDouble4);

                        tempDouble1 = sortedIt.get(0);
                        tempDouble2 = sortedIt.get(1);
                        tempDouble3 = sortedIt.get(2);
                        tempDouble4 = sortedIt.get(3);


                        Point p1=new Point();
                        Point p2=new Point();
                        Point p3=new Point();
                        Point p4=new Point();
                        try {
                            p1 = new Point(tempDouble1[0], tempDouble1[1]);
                            p2 = new Point(tempDouble2[0], tempDouble2[1]);
                            p3 = new Point(tempDouble3[0], tempDouble3[1]);
                            p4 = new Point(tempDouble4[0], tempDouble4[1]);
                        }catch (NullPointerException e){
                            if(p1==null){
                                Toast.makeText(getApplicationContext(),"No squares detected",Toast.LENGTH_LONG).show();
                                Log.e(TAG,"0 point found");
                            }else if(p2==null){
                                Toast.makeText(getApplicationContext(),"No squares detected",Toast.LENGTH_LONG).show();
                                Log.e(TAG, "1 POINT found");
                            }else if(p3==null){
                                Toast.makeText(getApplicationContext(),"No squares detected",Toast.LENGTH_LONG).show();
                                Log.e(TAG, "2 POINT found");
                            }else if(p4==null){
                                Toast.makeText(getApplicationContext(),"No squares detected",Toast.LENGTH_LONG).show();
                                Log.e(TAG, "3 POINT found");
                            }

                        }

                        Log.d(TAG, "Points: " + p1.toString() + " , " + p2.toString() + "  ,  " + p3.toString() + "  ,  " + p4.toString());


                        //To print circle on the canny image
                Imgproc.circle(tempMat, p1, 300, new Scalar(255, 255, 255));
               /* Imgproc.circle(tempMat, p2, 300, new Scalar(255, 255, 255));
                Imgproc.circle(tempMat, p3, 300, new Scalar(255, 255, 255));
                Imgproc.circle(tempMat, p4, 300, new Scalar(255, 255, 255));*/
                        List<Point> source = new ArrayList<>();
                        source.add(p1);
                        source.add(p2);
                        source.add(p3);
                        source.add(p4);

                        Mat startM = Converters.vector_Point2f_to_Mat(source);
                        Mat result = warp(tempMatOriginal, startM);

                        //Mat tmp = new Mat (bm.getHeight(), bm.getWidth(), CvType.CV_64FC4, new Scalar(4));
                /*try {
                    //Imgproc.cvtColor(startM, tmp, Imgproc.COLOR_GRAY2RGBA, 4);
                    bmp = Bitmap.createBitmap(result.cols(), result.rows(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(result, bmp);
                }
                catch (CvException e){Log.d("Exception",e.getMessage());}*/

                        tempBitmap = getBitmapFromMat(result);                                             //This is Canny bitmap
                        tempUri = getUriFromBitmap(tempBitmap);
                        //tempBitmap.recycle();



                        return tempUri;
                    }

                    @Override
                    protected void onPostExecute(Uri aVoid) {
                        Toast.makeText(getApplicationContext(),"Done",Toast.LENGTH_LONG).show();
                        in1.putExtra("image", tempUri.toString());
                        startActivity(in1);
                        tempBitmap.recycle();
                        super.onPostExecute(aVoid);
                    }
                }.execute();




                break;

            case 6:



                /*tempBitmap = getBitmapFromUri(tempUri);
                tempMat2 = getMatFromBitmap(tempBitmap);
                tempMat = getMatFromBitmap(tempBitmap);
                Imgproc.cvtColor(tempMat, tempMat, Imgproc.COLOR_BGR2GRAY, 4);
                Imgproc.GaussianBlur(tempMat, tempMat, new org.opencv.core.Size(1, 1), 5);
                //Try with different C value to reduce noice outside the primary image
                Imgproc.adaptiveThreshold(tempMat, tempMat, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 19, 5);
                Imgproc.Canny(tempMat, tempMat, 50, 100);
                List<MatOfPoint> contours = new ArrayList<>();

                Imgproc.findContours(tempMat, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
                Log.i(TAG,"Contours size: "+contours.size()+"");

                MatOfPoint tempContour = contours.get(0);
                MatOfPoint2f approxCurve = new MatOfPoint2f();
                double maxArea = -1;
                //double maxAreaI = -1;
               // for(int i=0;i<contours.size();i++){
                   // Imgproc.drawContours(tempMat2, contours,6, new Scalar(0, 0, 255), 200);

                //}

                for(int i=0;i<contours.size();i++){
                    tempContour = contours.get(i);
                    double contourArea = Imgproc.contourArea(tempContour);
                    //compare this contour to the previous largest contour found
                    if(contourArea>maxArea){
                        MatOfPoint2f mat2f = new MatOfPoint2f(tempContour.toArray());
                        int contourSize = (int)tempContour.total();

                        MatOfPoint2f approxCurveTemp = new MatOfPoint2f();
                        Imgproc.approxPolyDP(mat2f,approxCurveTemp,contourSize*0.05,true);
                        if(approxCurveTemp.total()==4){
                            //maxAreaI = i;
                            approxCurve = approxCurveTemp;
                            Log.i(TAG,"Approx area: "+maxArea);
                        }
                    }
                }


                double[] tempDouble1 = approxCurve.get(0,0);
                double[] tempDouble2 = approxCurve.get(1,0);
                double[] tempDouble3 = approxCurve.get(2,0);
                double[] tempDouble4 = approxCurve.get(3,0);

                Point p1 = new Point(tempDouble1[0],tempDouble1[1]);
                Point p2 = new Point(tempDouble2[0],tempDouble2[1]);
                Point p3 = new Point(tempDouble3[0],tempDouble3[1]);
                Point p4 = new Point(tempDouble4[0],tempDouble4[1]);

                Log.d(TAG,"Points: "+p1.toString()+" , "+p2.toString()+"  ,  "+p3.toString()+"  ,  "+p4.toString());


                Imgproc.circle(tempMat2, p1, 150, new Scalar(255, 255, 255));
                Imgproc.circle(tempMat2, p2, 150, new Scalar(255, 255, 255));
                Imgproc.circle(tempMat2, p3, 150, new Scalar(255, 255, 255));
                Imgproc.circle(tempMat2, p4, 150, new Scalar(255, 255, 255));
                List<Point> source = new ArrayList<>();
                source.add(p1);
                source.add(p2);
                source.add(p3);
                source.add(p4);

                Mat startM = Converters.vector_Point2f_to_Mat(source);
                //Size size = startM.size();
                Mat result = warp(tempMat2,startM);
                //size = result.size();


                Bitmap bmp = null;
                //Mat tmp = new Mat (bm.getHeight(), bm.getWidth(), CvType.CV_64FC4, new Scalar(4));
                try {
                    //Imgproc.cvtColor(startM, tmp, Imgproc.COLOR_GRAY2RGBA, 4);
                    bmp = Bitmap.createBitmap(result.cols(), result.rows(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(result, bmp);
                }
                catch (CvException e){Log.d("Exception",e.getMessage());}

                //tempBitmap = getBitmapFromMat(result);                                             //This is Canny bitmap
                tempUri = getUriFromBitmap(bmp);
                in1.putExtra("image", tempUri.toString());*/




            break;

        }



        //tempBitmap.recycle();

    }

    /**
     *
     * @param tempDouble1
     * @param tempDouble2
     * @param tempDouble3
     * @param tempDouble4
     *
     * This method sorts and returns values to be placed in proper order
     */
    private List findAndSortPositions(double[] tempDouble1, double[] tempDouble2, double[] tempDouble3, double[] tempDouble4) {
        double TL,TR,BL,BR;


        Map<Double,Double> left = new TreeMap<>();
        left.put(tempDouble1[0],tempDouble1[1]);
        left.put(tempDouble2[0],tempDouble2[1]);
        left.put(tempDouble3[0], tempDouble3[1]);
        left.put(tempDouble4[0], tempDouble4[1]);



        int x=0;
        double[] widthTemp = new double[left.size()];
        double[] heightTemp = new double[left.size()];
        for(Map.Entry<Double,Double> entry: left.entrySet()){
            widthTemp[x] = entry.getKey();
            heightTemp[x] = entry.getValue();
            x++;
        }


        Map<Double,Double> top = new TreeMap<>();
        top.put(heightTemp[0],widthTemp[0]);
        top.put(heightTemp[1],widthTemp[1]);

        double[] topLeft = new double[top.size()];
        double[] bottomLeft = new double[top.size()];
        int y=0;
        for(Map.Entry<Double,Double> maps:top.entrySet()){
            if(y==0){
                topLeft[0] = maps.getValue();
                topLeft[1] = maps.getKey();
            }else if(y==1){
                bottomLeft[0] = maps.getValue();
                bottomLeft[1] = maps.getKey();
            }
            y++;
        }




        top.clear();
        top.put(heightTemp[2], widthTemp[2]);
        top.put(heightTemp[3], widthTemp[3]);

        double[] topRight = new double[top.size()];
        double[] bottomRight = new double[top.size()];
        int z=0;

        for(Map.Entry<Double,Double> maps:top.entrySet()){

            if(z==0){
                topRight[0] = maps.getValue();
                topRight[1] = maps.getKey();
            }else if(z==1){
                bottomRight[0] = maps.getValue();
                bottomRight[1] = maps.getKey();
            }

            z++;
        }
        List<double[]> sortedItems = new ArrayList<>();
        sortedItems.add(topLeft);
        sortedItems.add(bottomLeft);
        sortedItems.add(bottomRight);
        sortedItems.add(topRight);
        Log.e(TAG,"After sorting");
        Log.e(TAG, "\n1)" + topLeft[0] + "  " + topLeft[1] +
                "\n2)" + bottomLeft[0] + "  " + bottomLeft[1] +
                "\n3)" + bottomRight[0] + "  " + bottomRight[1] +
                "\n4)" + topRight[0] + "  " + topRight[1]);

        return sortedItems;
    }

    /*private Mat warp(Mat inputMat, Mat startM) {
        int resultHeight = 1280;
        int resultWidth = 720;

        Mat outMat = new Mat(1280,720,CvType.CV_64FC4,new Scalar(4));
        Point ocvP1 = new Point(resultWidth,resultHeight);
        Point ocvP2 = new Point(resultWidth,0);
        Point ocvP3 = new Point(0,0);
        Point ocvP4 = new Point(0,resultHeight);

        List<Point> dest = new ArrayList<>();
        dest.add(ocvP1);
        dest.add(ocvP2);
        dest.add(ocvP3);
        dest.add(ocvP4);

        Mat endM = Converters.vector_Point2f_to_Mat(dest);
        Mat perspectiveTransform = Imgproc.getPerspectiveTransform(startM,endM);
        Imgproc.warpPerspective(inputMat,outMat,perspectiveTransform,new Size(resultWidth,resultHeight),Imgproc.INTER_CUBIC);

        return outMat;
    }*/

    public Mat warp(Mat inputMat,Mat startM) {

        // If document if in portrait mode,,
        int h1 = inputMat.height();
        int w1 = inputMat.width();
        int h2 = startM.width();
        int w2 = startM.width();

        Log.d(TAG,"H n W :"+h1+" "+w1+"    "+h2+" "+w2);
        int resultWidth = w1;
        int resultHeight = h1;
        //Mat outputMat = new Mat(resultWidth, resultHeight, CvType.CV_8UC4);
        // If document if in landscape mode,,
        /*int resultWidth = 320;
        int resultHeight = 240;
        960
        1280
        */

       // Mat outputMat = new Mat(resultHeight,resultWidth, CvType.CV_64FC4,new Scalar(4));
        Mat outputMat = inputMat;

        //To rotate Image, Untill better way if found
        Point ocvPOut1 = new Point(resultWidth, resultHeight);
        Point ocvPOut2 = new Point(resultWidth, 0);
        Point ocvPOut3 = new Point(0, 0);
        Point ocvPOut4 = new Point(0, resultHeight);
        List<Point> dest = new ArrayList<Point>();

        dest.add(ocvPOut3);
        dest.add(ocvPOut4);


        dest.add(ocvPOut1);
        dest.add(ocvPOut2);





        Mat endM = Converters.vector_Point2f_to_Mat(dest);
        Mat perspectiveTransform = Imgproc.getPerspectiveTransform(startM, endM);

        Imgproc.warpPerspective(inputMat,
                tempMatOriginal,
                perspectiveTransform,
                new Size(resultWidth, resultHeight),
                Imgproc.INTER_CUBIC);

        return tempMatOriginal;
    }


    public Bitmap getBitmapFromUri(Uri uri){
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Uri getUriFromBitmap(Bitmap bitM){
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitM.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), bitM, "Title", null);
            return Uri.parse(path);
    }

    public Mat getMatFromBitmap(Bitmap bitM){
        tempMat = new Mat(bitM.getWidth(), bitM.getHeight(), CvType.CV_8UC1);
        Utils.bitmapToMat(bitM, tempMat);
       // tempBitmap.recycle();
        return tempMat;
    }

    public Bitmap getBitmapFromMat(Mat mat){
        // tempBitmap = Bitmap.createScaledBitmap(bm, 640, 480, false)
        Utils.matToBitmap(mat,tempBitmap);
        return tempBitmap;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        tempBitmap.recycle();
//        tempMat.release();

    }
}
