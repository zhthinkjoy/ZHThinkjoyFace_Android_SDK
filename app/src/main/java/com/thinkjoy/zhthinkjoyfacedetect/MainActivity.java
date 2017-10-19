package com.thinkjoy.zhthinkjoyfacedetect;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import com.thinkjoy.zhthinkjoyfacedetectlib.FaceFeature;
import com.thinkjoy.zhthinkjoyfacedetectlib.FaceLandMark;
import com.thinkjoy.zhthinkjoyfacedetectlib.FaceRectangle;
import com.thinkjoy.zhthinkjoyfacedetectlib.ZHThinkjoyFace;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener{
    private Handler handler;
    private Button bt_add_face;
    private FaceOverlayView fv_draw_rect;
    private ZHThinkjoyFace zhThinkjoyFace;
    private GlobalFlag globalFlag;
    private Handler faceAddHandler;
    private CameraPreview cameraPreview;
    private FaceDataManager faceDataManager;
    private Boolean isActivityPause = false;
    static {
        System.loadLibrary("face");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        zhThinkjoyFace = ZHThinkjoyFace.getInstance(this);
        globalFlag = GlobalFlag.getInstance();
        cameraPreview = (CameraPreview) findViewById(R.id.cv_camera_preview);
        fv_draw_rect = (FaceOverlayView) findViewById(R.id.fv_draw_rect);
        bt_add_face = (Button)findViewById(R.id.bt_add_face);
        bt_add_face.setOnClickListener(this);
        fv_draw_rect.setWindowSize();
        HandlerThread handlerThread = new HandlerThread("faceDetect");
        handlerThread.start();
        final HandlerThread faceAddThread = new HandlerThread("faceAdd");
        faceAddThread.start();
        faceDataManager = FaceDataManager.getInstance(this);
        faceAddHandler = new Handler(faceAddThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case GlobalInfo.MSG_ADD_FACE:
                        FaceInfo faceInfo = (FaceInfo)msg.obj;
                        String path1 = faceInfo.getPath1();
                        String path2 = faceInfo.getPath2();
                        String name = faceInfo.getName();
                        List<FaceRectangle> faceIndexList = new ArrayList<>();
                        List<FaceLandMark> faceKeyPointList = new ArrayList<>();
                        List<FaceFeature> faceFeatureList = new ArrayList<>();
                        Bitmap bitmap1 = BitmapFactory.decodeFile(path1);
                        zhThinkjoyFace.faceDetectAndFeatureExtract(bitmap1, faceIndexList, faceKeyPointList, faceFeatureList);
                        bitmap1.recycle();
                        Bitmap bitmap2 = BitmapFactory.decodeFile(path2);
                        zhThinkjoyFace.faceDetectAndFeatureExtract(bitmap2, faceIndexList, faceKeyPointList, faceFeatureList);
                        bitmap2.recycle();
                        for (int i = 0; i < faceFeatureList.size(); ++i) {
                                faceDataManager.addFace(name, faceFeatureList.get(i));
                        }
                        break;
                }
            }
        };

        handler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (isActivityPause == true) {
                    globalFlag.isFaceDetectFinished = true;
                    return;
                }
                switch (msg.what) {

                    case GlobalInfo.MSG_FACE_TEST:
                        Bitmap bitmap3 = (Bitmap)msg.obj;
                        List<FaceRectangle> faceRectangleList = new ArrayList<>();
                        List<FaceLandMark> faceLandMarkList = new ArrayList<>();
                        List<FaceFeature> faceFeatureList = new ArrayList<>();
                        long time1 = System.currentTimeMillis();
                        if (isActivityPause == true) {
                            globalFlag.isFaceDetectFinished = true;
                            return;
                        }
                        zhThinkjoyFace.faceDetect(bitmap3, faceRectangleList, faceLandMarkList);
                        long time2 = System.currentTimeMillis();
                        if (isActivityPause == true) {
                            globalFlag.isFaceDetectFinished = true;
                            return;
                        }
                        zhThinkjoyFace.featureExtract(bitmap3, faceLandMarkList, faceFeatureList);
                        long time3 = System.currentTimeMillis();
                        List<double[]> simProbList = new ArrayList<>();
                        if (faceRectangleList.size() > 0) {
                            for (int i = 0; i < faceRectangleList.size(); ++i) {
                                double[] simprobs = zhThinkjoyFace.featureCompare(faceFeatureList.get(i), faceDataManager.mFaceFeatureList);
                                    simProbList.add(simprobs);
                            }
                            fv_draw_rect.setFaceDetectResult(faceRectangleList, faceLandMarkList, time2 - time1, time3 - time2, simProbList);
                            if (isActivityPause == false)
                                fv_draw_rect.postInvalidate();
                        } else {
                            fv_draw_rect.setFaceDetectResult(faceRectangleList, faceLandMarkList, time2 - time1, time3 - time2);
                            if (isActivityPause == false)
                            fv_draw_rect.postInvalidate();
                        }
                        globalFlag.isFaceDetectFinished = true;
                        break;
                }
            }

        };
        cameraPreview.setHandler(handler);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_add_face:
                startActivityForResult(new Intent(this, AddFaceActivity.class), GlobalInfo.REQUEST_ADD_MEMBER_CODE);
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        final Intent data = intent;
        if (requestCode== GlobalInfo.REQUEST_ADD_MEMBER_CODE)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                Bundle bundle=data.getExtras();
                String name = bundle.getString("name");
                String str1 =bundle.getString("photo1");
                String str2 = bundle.getString("photo2");
                FaceInfo faceInfo = new FaceInfo(name, str1, str2);
                Message msg = new Message();
                msg.what = GlobalInfo.MSG_ADD_FACE;
                msg.obj = faceInfo;
                faceAddHandler.sendMessage(msg);
            }
        }
    }
    @Override
    protected  void onPause() {
        super.onPause();
        isActivityPause = true;
    }

    @Override
    protected  void onResume() {
        super.onResume();
        isActivityPause = false;
    }

}
