package com.thinkjoy.zhthinkjoyfacedetect;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import com.thinkjoy.zhthinkjoyfacedetectlib.ZHThinkjoyFace;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by thinkjoy on 17-9-12.
 */


/**
 * Created by thinkjoy on 17-8-8.
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {

    SurfaceHolder surfaceHolder ;
    private Handler handler;
    private Camera camera ;
    private ZHThinkjoyFace zhThinkjoyFace;
    private GlobalFlag globalFlag;

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        zhThinkjoyFace = ZHThinkjoyFace.getInstance(getContext());
        zhThinkjoyFace.init();
        globalFlag = GlobalFlag.getInstance();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try{
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            List<Camera.Size> sizeList = camera.getParameters().getSupportedPictureSizes();
            int selectNum = -1;
            int i = 0;
            for (i = 0; i < sizeList.size(); ++i) {
                if (sizeList.get(i).width == GlobalInfo.CAMERA_SIZE) {
                    selectNum = i;
                    break;
                }
            }

            if (selectNum == -1) {
                Boolean orderAsec = true;
                for (i = 0; i < sizeList.size() - 1; ++i) {
                    if (sizeList.get(i).width > sizeList.get(i + 1).width) {
                        orderAsec = false;
                        break;
                    }
                }
                if (orderAsec) {
                    for (i = sizeList.size(); i < sizeList.size(); --i) {
                        if (sizeList.get(i).width >= GlobalInfo.CAMERA_SIZE) {
                            selectNum = i;
                        } else {
                            break;
                        }
                    }
                } else {
                    for (i = 0; i < sizeList.size(); ++i) {
                        if (sizeList.get(i).width >= GlobalInfo.CAMERA_SIZE) {
                            selectNum = i;
                        } else {
                            break;
                        }
                    }
                }
            }
            Camera.Parameters params = camera.getParameters();
            Camera.Size size = sizeList.get(selectNum);
            params.setPreviewSize(size.width, size.height);
            params.setPictureSize(size.width, size.height);
            camera.setParameters(params);
            holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            camera.setPreviewDisplay(holder);
            camera.setDisplayOrientation(90);
            camera.setPreviewCallback(this);
            camera.startPreview() ;
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        if (camera != null) {
            camera.startPreview();
        }
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if(camera != null) {
            camera.setPreviewCallback(null);
            camera.release() ;
            camera = null ;
        }
    }
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

        if (handler != null ) {
            Camera.Size previewSize = camera.getParameters().getPreviewSize();
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            newOpts.inJustDecodeBounds = true;
            YuvImage yuvimage = new YuvImage(
                    data,
                    ImageFormat.NV21,
                    previewSize.width,
                    previewSize.height,
                    null);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            yuvimage.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), 100, baos);// 80--JPG图片的质量[0-100],100最高
            byte[] rawImage = baos.toByteArray();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmapRaw = BitmapFactory.decodeByteArray(rawImage, 0, rawImage.length, options);
            Bitmap bitmap = PhotoUtils.resizeBitmapAndRotate90(bitmapRaw, GlobalInfo.IMAGE_WIDTH, GlobalInfo.IMAGE_HEIGHT);
            if (bitmap != null && (globalFlag.isFaceDetectFinished == true)) {
                    Message msg = new Message();
                    msg.what = GlobalInfo.MSG_FACE_TEST;
                    msg.obj = bitmap;
                    handler.sendMessage(msg);
                globalFlag.isFaceDetectFinished = false;
            }
        }
    }
    public void setHandler(Handler handler) {
        this.handler = handler;
    }
}
