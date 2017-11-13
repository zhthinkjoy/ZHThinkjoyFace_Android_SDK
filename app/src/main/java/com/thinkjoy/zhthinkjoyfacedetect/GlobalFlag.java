package com.thinkjoy.zhthinkjoyfacedetect;

import java.util.LinkedList;

/**
 * Created by thinkjoy on 17-9-16.
 */

public class GlobalFlag {
    public boolean isFaceDetectFinished;
    public LinkedList<byte[]> faceFramList;
    private static GlobalFlag globalFlag;
    public int imageWidth;
    public int imageHeight;

    private GlobalFlag() {
        isFaceDetectFinished = true;
        faceFramList = new LinkedList<>();
    }
    public static GlobalFlag getInstance() {
        if (globalFlag == null) {
            globalFlag = new GlobalFlag();
        }
        return globalFlag;
    }
}
