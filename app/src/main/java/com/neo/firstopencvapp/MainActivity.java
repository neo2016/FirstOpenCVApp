package com.neo.firstopencvapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
    private TextView m_tvProcessed;


    static {
        if(!OpenCVLoader.initDebug()){

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        m_ivDes = (ImageView) findViewById(R.id.iv_des);
        m_tvProcessed = (TextView) findViewById(R.id.tv_processed);
    }


    public void processImg(View v){
        long start = System.currentTimeMillis();
        HoughCircle();
//        CannyDetect();
        long end = System.currentTimeMillis();
        m_tvProcessed.setText(((end-start)/1000)+"秒");
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
        loadImg2Act(bmDes, mtCannyEdge);
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
        loadImg2Act(bmDes, corners);
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
        long start0 = System.currentTimeMillis();
        Bitmap bmSrc = BitmapFactory.decodeResource(getResources(),R.mipmap.jsp_src);
        Bitmap bmDes;
        Mat mtSrc = new Mat(bmSrc.getHeight(),bmSrc.getWidth(), CvType.CV_8UC4);
        Utils.bitmapToMat(bmSrc,mtSrc);
        bmDes = Bitmap.createBitmap(mtSrc.cols(),mtSrc.rows(), Bitmap.Config.ARGB_8888);
        Mat mtGray = new Mat();
        Imgproc.cvtColor(mtSrc,mtGray,Imgproc.COLOR_BGR2GRAY);
        long start1 = System.currentTimeMillis();

        Mat mtCannyEdges = new Mat();
        Mat mtCircles = new Mat();
        Mat mtHoughCircles = new Mat();
        Imgproc.Canny(mtGray,mtCannyEdges,10,100);
        Log.e(this.getLocalClassName(),"图片Canny转换处理时间："+(start1 - start0));

//        经测试发现，高斯变换耗时很长大概40秒左右
        Imgproc.HoughCircles(mtCannyEdges,mtCircles,Imgproc.CV_HOUGH_GRADIENT,1,mtGray.rows()/8);
        long start2 = System.currentTimeMillis();
        Log.e(this.getLocalClassName(),"图片HoughCircles转换处理时间："+(start2 - start1));
        mtHoughCircles.create(mtCannyEdges.rows(),mtCannyEdges.cols(),CvType.CV_8UC1);
        long start3 = System.currentTimeMillis();
        Log.e(this.getLocalClassName(),"图片HoughCircles创建处理时间："+(start3 - start2));

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
        Log.e(this.getLocalClassName(),"for循环时间："+(System.currentTimeMillis() - start3));

        loadImg2Act(bmDes, mtHoughCircles);
    }

    private void loadImg2Act(Bitmap bmDes, Mat mtGray) {
        Utils.matToBitmap(mtGray,bmDes);
        m_ivDes.setImageBitmap(bmDes);
    }


}
