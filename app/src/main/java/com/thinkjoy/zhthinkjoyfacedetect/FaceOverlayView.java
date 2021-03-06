package com.thinkjoy.zhthinkjoyfacedetect;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


import com.thinkjoy.zhthinkjoyfacedetectlib.FaceConfig;
import com.thinkjoy.zhthinkjoyfacedetectlib.FaceLandMark;
import com.thinkjoy.zhthinkjoyfacedetectlib.FaceRectangle;

import java.util.List;


/**
 * Created by thinkjoy on 17-8-9.
 */

public class FaceOverlayView extends View {

    private Paint mPaint;
    private Paint mFacePaint;
    private Paint mTextPaint;
    private int imageWidth;
    private int imageHeight;
    private int winWidth;
    private int winHeight;
    private long detectTime;
    private long extractTime;

    private List<double[]> simProbList;
    private List<FaceRectangle> faceRectangleList;
    private List<FaceLandMark> faceLandMarkList;
    private FaceDataManager faceDataManager;
    private List<double[]> simProbListSave;
    private List<FaceRectangle> faceRectangleListSave;
    private List<FaceLandMark> faceLandMarkListSave;
    public FaceOverlayView(Context context) {
        super(context);
    }
    public FaceOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FaceOverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);  //
        mPaint.setDither(true);
        mPaint.setColor(Color.RED);   //颜色
        mPaint.setStrokeWidth(10);
        mPaint.setStyle(Paint.Style.STROKE);  //控制是否空心

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setDither(true);
        mTextPaint.setTextSize(30);
        mTextPaint.setColor(Color.GREEN);
        mTextPaint.setStyle(Paint.Style.FILL);

        mFacePaint = new Paint();
        mFacePaint.setAntiAlias(true);
        mFacePaint.setDither(true);
        mFacePaint.setColor(Color.WHITE);
        mFacePaint.setStrokeWidth(5);
        mFacePaint.setStyle(Paint.Style.STROKE);

        imageHeight = GlobalInfo.IMAGE_HEIGHT;
        imageWidth = GlobalInfo.IMAGE_WIDTH;

        faceDataManager = FaceDataManager.getInstance(getContext());
    }
    @Override
    protected void onDraw(Canvas canvas) {
        winHeight = getHeight();
        winWidth = getWidth();

        canvas.drawText("DetectTime: " + Long.toString(detectTime) + "ms", 0, 200, mTextPaint);
        canvas.drawText("ExtractTime: " + Long.toString(extractTime) + "ms", 0, 250, mTextPaint);
        faceRectangleListSave = faceRectangleList;
        if (faceRectangleListSave != null) {
            float left, right, top, bottom;
            for (int i = 0; i < faceRectangleListSave.size(); ++i) {
                top = (int) (faceRectangleListSave.get(i).faceRectangle[0].y * winHeight / imageHeight + 0.5);
                bottom = (int) (faceRectangleListSave.get(i).faceRectangle[1].y * winHeight / imageHeight + 0.5);
                float x1 = faceRectangleListSave.get(i).faceRectangle[0].x;
                float y1 = faceRectangleListSave.get(i).faceRectangle[0].y;
                float x2 = faceRectangleListSave.get(i).faceRectangle[1].x;
                float y2 = faceRectangleListSave.get(i).faceRectangle[1].y;
//                float left = (imageWidth - y1) * winHeight / imageWidth;
//                float top = x1 * winHeight / imageHeight;
//                float right = (imageWidth - y2) * winWidth / imageWidth;
//                float bottom = x2 * winHeight / imageHeight;
                if (GlobalInfo.IS_PREVIEW_REVERSE == true) {
                    right = (int) (faceRectangleListSave.get(i).faceRectangle[0].x * winWidth  / imageWidth+ 0.5);
                    left = (int) (faceRectangleListSave.get(i).faceRectangle[1].x * winWidth / imageWidth+ 0.5);

                } else {
                    left = winWidth - (int) (faceRectangleListSave.get(i).faceRectangle[0].x * winWidth / imageWidth + 0.5);
                    right = winWidth - (int) (faceRectangleListSave.get(i).faceRectangle[1].x * winWidth / imageWidth + 0.5);
                }
                Log.i("FaceDetect", "right:" + faceRectangleListSave.get(i).faceRectangle[1].x + " left:" + faceRectangleListSave.get(i).faceRectangle[0].x + " bottom:" + faceRectangleListSave.get(i).faceRectangle[1].y + " top:" + faceRectangleListSave.get(i).faceRectangle[0].y);
                canvas.drawRect(right, top, left, bottom, mFacePaint);
                simProbListSave = simProbList;
                if (simProbListSave != null && simProbListSave.size() > i && faceDataManager.mFaceFeatureList.size() > 0) {
                    int max_index = 0;
                    for (int j = 1; j < simProbListSave.get(i).length; ++j) {
                        if (simProbListSave.get(i)[max_index] < simProbListSave.get(i)[j]) {
                            max_index = j;
                        }
                    }
                    canvas.drawText("name: " + faceDataManager.mFaceNameList.get(max_index) + " " + "simProb:" + " " + Double.toString(simProbListSave.get(i)[max_index]), right, top + (max_index + 1) * 30, mTextPaint);
                }
            }

        }

        faceLandMarkListSave = faceLandMarkList;
        if (faceLandMarkListSave != null) {
            for (int i= 0; i < faceLandMarkListSave.size(); ++i) {
                for (int j = 0; j < 5; ++j) {
                    float x1;
                    if (GlobalInfo.IS_PREVIEW_REVERSE == false
                            ) {
                        x1 = winWidth -  (int) (faceLandMarkListSave.get(i).faceLandMark[j].x * winWidth / imageWidth);
                    } else {
                        x1 = (int) (faceLandMarkListSave.get(i).faceLandMark[j].x * winWidth / imageWidth);

                    }
//                    x1 = faceLandMarkListSave.get(i).faceLandMark[j].x;
                    float y1 = faceLandMarkListSave.get(i).faceLandMark[j].y * winHeight / imageHeight;

                    float x = (imageWidth - y1) * winWidth / imageWidth;
                    float y = winHeight - x1 * winHeight / imageHeight;
                    canvas.drawPoint(x1, y1, mPaint);
                }
            }
        }

    }


    public void setWindowSize() {
        this.winHeight = this.getHeight();
        this.winWidth = this.getWidth();
    }

    public void setImageSize(int imageWidth, int imageHeight) {
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }


    public void setFaceDetectResult(List<FaceRectangle> faceRectangleList, List<FaceLandMark> faceKeyPointList, long time1, long time2) {
        this.faceRectangleList = faceRectangleList;
        this.faceLandMarkList = faceKeyPointList;
        this.detectTime = time1;
        this.extractTime = time2;
    }
    public void setFaceDetectResult(List<FaceRectangle> faceRectangleList, List<FaceLandMark> faceKeyPointList, long time1, long time2, List<double[]> simProbList) {
        this.faceRectangleList = faceRectangleList;
        this.faceLandMarkList = faceKeyPointList;
        this.simProbList = simProbList;
        this.detectTime = time1;
        this.extractTime = time2;
    }
}
