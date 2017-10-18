package com.thinkjoy.zhthinkjoyfacedetect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


/**
 * Created by thinkjoy on 17-9-15.
 */

public class AddFaceActivity extends Activity implements View.OnClickListener{
    private ImageButton bt_back;
    private Button bt_submit;
    private ImageView iv_member_photo1;
    private ImageView iv_member_photo2;
    private EditText et_input_name;
    static int RESULT_LOAD_IMAGE1 = 1;
    static int RESULT_LOAD_IMAGE2 = 2;
    Boolean isPhoto1Choose = false;
    Boolean isPhoto2Choose = false;
    Bitmap photo1;
    Bitmap photo2;
    private String pathPhoto1;
    private String pathPhoto2;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.layout_add_family_member);
        bt_back = (ImageButton)findViewById(R.id.bt_back);
        bt_submit = (Button)findViewById(R.id.bt_submit);
        iv_member_photo1 = (ImageView)findViewById(R.id.iv_member_photo1);
        iv_member_photo2 = (ImageView)findViewById(R.id.iv_member_photo2);
        et_input_name = (EditText)findViewById(R.id.et_input_name);
        bt_back.setOnClickListener(this);
        bt_submit.setOnClickListener(this);
        iv_member_photo1.setOnClickListener(this);
        iv_member_photo2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_back:
                finish();
                break;
            case R.id.bt_submit:
                if (isPhoto1Choose != true || isPhoto2Choose != true) {
                    break;
                }
                if (et_input_name.getText().toString().trim().equals("")) {
                    Toast.makeText(getApplicationContext(), "输入不能为空" , Toast.LENGTH_SHORT).show();
                    break;
                }

                Intent data = new Intent();
                data.putExtra("name", et_input_name.getText().toString());
                data.putExtra("photo1", pathPhoto1);
                data.putExtra("photo2", pathPhoto2);
                setResult(RESULT_OK, data);
                finish();
                break;
            case R.id.iv_member_photo1:
                startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), RESULT_LOAD_IMAGE1);
                break;
            case R.id.iv_member_photo2:
                startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), RESULT_LOAD_IMAGE2);
                break;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,
                    null, null, null, null);
            ;
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            Log.i("filePath", picturePath);
            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
            cursor.close();
            if (requestCode == RESULT_LOAD_IMAGE1) {
                if (photo1 != null) {
                    photo1.recycle();
                }
                photo1 = bitmap;
                pathPhoto1 = picturePath;
                iv_member_photo1.setImageBitmap(bitmap);
                isPhoto1Choose = true;
            } else if (requestCode == RESULT_LOAD_IMAGE2) {
                if (photo2 != null) {
                    photo2.recycle();
                }
                photo2 = bitmap;
                pathPhoto2 = picturePath;
                iv_member_photo2.setImageBitmap(bitmap);
                isPhoto2Choose = true;
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (photo1 != null) {
            photo1.recycle();
        }
        if (photo2 != null) {
            photo2.recycle();
        }
    }
}
