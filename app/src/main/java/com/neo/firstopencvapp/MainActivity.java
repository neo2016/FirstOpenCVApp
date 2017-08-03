package com.neo.firstopencvapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ImageView m_ivDes;

    static {
        if(!OpenCVLoader.initDebug()){

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        m_ivDes = (ImageView) findViewById(R.id.iv_des);

    }


    public void processImg(View v){
        HoughCircle();
    }

    /**
     * Canny边缘检测
     */
    private void CannyDetect(){
        Bitmap bmSrc = BitmapFactory.decodeResource(getResources(),R.mipmap.jsp_src);
        Bitmap bmDes;
        Mat mtSrc = new Mat(bmSrc.getHeight(),bmSrc.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(bmSrc,mtSrc);
        Mat mtGray = new Mat();
        Mat mtCannyEdge = new Mat();
        Imgproc.cvtColor(mtSrc,mtGray,Imgproc.COLOR_BGR2GRAY);
        Imgproc.Canny(mtGray,mtCannyEdge,10,100);
        bmDes = Bitmap.createBitmap(mtSrc.cols(),mtSrc.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mtCannyEdge,bmDes);
        m_ivDes.setImageBitmap(bmDes);
    }

    /**
     * Harris角点检测
     */
    private void HarrisDetect(){
        Bitmap bmSrc = BitmapFactory.decodeResource(getResources(),R.mipmap.jsp_src);
        Mat mtSrc = new Mat(bmSrc.getHeight(),bmSrc.getWidth(), CvType.CV_8UC4);
        Bitmap bmDes;
        bmDes = Bitmap.createBitmap(mtSrc.cols(),mtSrc.rows(), Bitmap.Config.ARGB_8888);
        Utils.bitmapToMat(bmSrc,mtSrc);
        Mat mtGray = new Mat();
        Imgproc.cvtColor(mtSrc,mtGray,Imgproc.COLOR_BGR2GRAY);
        Mat tempDes = new Mat();
        Imgproc.cornerHarris(mtGray,tempDes,2,3,0.04);
        Mat tempDstNorm = new Mat();
        Core.normalize(tempDes,tempDstNorm,0,255,Core.NORM_MINMAX);
        Mat corners = new Mat();
        Core.convertScaleAbs(tempDstNorm,corners);
        Random r = new Random();
        for(int i=0;i<tempDstNorm.cols();i++){
            for(int j =0;j<tempDstNorm.rows();j++){
                double[] value = tempDstNorm.get(j,i);
                if(value[0]>150){
                    Core.circle(corners,new Point(i,j),5,new Scalar(r.nextInt(255)),2);
                }
            }
        }
        Utils.matToBitmap(corners,bmDes);
        m_ivDes.setImageBitmap(bmDes);
    }

    /**
     * 霍夫直线变换
     */
    private void HoughLine(){

    }

    /**
     * 霍夫圆变换
     */
    private void HoughCircle(){
        Bitmap bmSrc = BitmapFactory.decodeResource(getResources(),R.mipmap.jsp_src);
        Mat mtSrc = new Mat(bmSrc.getHeight(),bmSrc.getWidth(), CvType.CV_8UC4);
        Bitmap bmDes;
        bmDes = Bitmap.createBitmap(mtSrc.cols(),mtSrc.rows(), Bitmap.Config.ARGB_8888);
        Mat mtGray = new Mat();
        Imgproc.cvtColor(mtSrc,mtGray,Imgproc.COLOR_BGR2GRAY);
        Mat mtCannyEdges = new Mat();
        Mat mtCircles = new Mat();
        Mat mtHoughCircles = new Mat();
        Imgproc.Canny(mtGray,mtCannyEdges,10,100);
        Imgproc.HoughCircles(mtCannyEdges,mtCircles,Imgproc.CV_HOUGH_GRADIENT,1,mtGray.rows()/8);
        mtHoughCircles.create(mtCannyEdges.rows(),mtCannyEdges.cols(),CvType.CV_8UC1);
        for(int i=0;i<mtCircles.cols();i++){
            double[] parameters = mtCircles.get(0,i);
            double x,y;
            int r;
            x = parameters[0];
            y = parameters[1];
            r = (int) parameters[2];

            Point center = new Point(x,y);
            Core.circle(mtHoughCircles,center,r,new Scalar(255,0,0),1);
        }
        Utils.matToBitmap(mtHoughCircles,bmDes);
        m_ivDes.setImageBitmap(bmDes);
    }


}
