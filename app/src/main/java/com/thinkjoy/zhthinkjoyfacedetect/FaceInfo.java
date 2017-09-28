package com.thinkjoy.zhthinkjoyfacedetect;

import android.graphics.Bitmap;

/**
 * Created by thinkjoy on 17-9-15.
 */

public class FaceInfo {
    public String name;
    public String path1;
    public String path2;

    public FaceInfo(String name, String path1, String path2) {
        this.name = name;
        this.path1 = path1;
        this.path2 = path2;
    }
    public String getName() {
        return name;
    }
    public String getPath1() {
        return path1;
    }
    public String getPath2() {
        return path2;
    }
}
