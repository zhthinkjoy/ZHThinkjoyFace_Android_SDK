package com.thinkjoy.zhthinkjoyfacedetect;

/**
 * Created by thinkjoy on 17-9-16.
 */

public class GlobalFlag {
    public boolean isFaceDetectFinished;
    private static GlobalFlag globalFlag;
    private GlobalFlag() {
        isFaceDetectFinished = true;
    }
    public static GlobalFlag getInstance() {
        if (globalFlag == null) {
            globalFlag = new GlobalFlag();
        }
        return globalFlag;
    }
}
