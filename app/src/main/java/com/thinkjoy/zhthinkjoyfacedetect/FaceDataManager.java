package com.thinkjoy.zhthinkjoyfacedetect;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.thinkjoy.zhthinkjoyfacedetectlib.FaceFeature;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thinkjoy on 17-9-19.
 */

public class FaceDataManager extends SQLiteOpenHelper {
    public  List<String> mFaceNameList;
    public List<FaceFeature> mFaceFeatureList;
    private static FaceDataManager mFaceDataManager;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "haiersmartair.db";
    private static final String TABLE_PERSON_NAME = "persontable";
    private static SQLiteDatabase sqLiteDatabase;
    public void addFace(String name, FaceFeature faceFeature) {
        mFaceNameList.add(name);
        mFaceFeatureList.add(faceFeature);
        insertMember(name, faceFeature);
    }

    public String getCreateTablePersonCmd(String tableName) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("CREATE TABLE [" + tableName + "] (");
        stringBuffer.append("[member_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
        stringBuffer.append("[member_name] TEXT,");
        stringBuffer.append("[member_feature] BLOB)");
        return stringBuffer.toString();
    }

    public float[] byteArrayToFloatArray(byte[] byteArray) {
        float[] floatArray = new float[128];
        ByteBuffer byteBuffer = ByteBuffer.allocate(128 * 4);
        byteBuffer.put(byteArray);
        byteBuffer.flip();
        for (int i = 0; i < 128; ++i) {
            floatArray[i] = byteBuffer.getFloat();
        }
        return floatArray;
    }
    public byte[] floatArrayToByteArray(float[] floatArray) {
        byte [] byteArray = new byte[128 * 4];
        ByteBuffer byteBuffer = ByteBuffer.allocate(128 * 4);
        for (int i = 0; i < 128; ++i) {
            byteBuffer.putFloat(floatArray[i]);
        }
        byteBuffer.flip();
        byteBuffer.get(byteArray, 0, 128 * 4);
        return byteArray;
    }
    public void getMemberInfo() {
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_PERSON_NAME, null);
        mFaceNameList = new ArrayList<>();
        mFaceFeatureList = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                String name = c.getString(c.getColumnIndex("member_name"));
                byte[] member_features = c.getBlob(c.getColumnIndex("member_feature"));
                mFaceNameList.add(name);
                FaceFeature faceFeature = new FaceFeature(byteArrayToFloatArray(member_features));
                mFaceFeatureList.add(faceFeature);
            } while (c.moveToNext());
        }
    }
    public void insertMember(String name, FaceFeature faceFeature) {
        byte[] bytes = floatArrayToByteArray(faceFeature.features);
        sqLiteDatabase.beginTransaction();
        sqLiteDatabase.execSQL("INSERT INTO " + TABLE_PERSON_NAME +
        " VALUES(null, ?, ?)", new Object[] {
                name,
                bytes
        });
        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
    }

    private FaceDataManager(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        sqLiteDatabase = getWritableDatabase();
        getMemberInfo();
    }
    public static FaceDataManager getInstance(Context context) {
        if (mFaceDataManager == null) {
            mFaceDataManager = new FaceDataManager(context);
        }
        return mFaceDataManager;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(getCreateTablePersonCmd(TABLE_PERSON_NAME));
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
