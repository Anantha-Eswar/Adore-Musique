//Created by Anantha Eswar to select, compress and to display the selected Image.

package com.anantheswar.adoremusique.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.anantheswar.adoremusique.R;
import com.anantheswar.adoremusique.utils.Constants;
import com.anantheswar.adoremusique.utils.PreferencesUtility;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ImagePicker extends AppCompatActivity {
    private String action;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //moveTaskToBack(true);
        action = getIntent().getAction();
        if (action.equals(Constants.TEST)) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, getString(R.string.select_a_photo)), 100);
            Bundle params = new Bundle();
            SettingsActivity.mFirebaseAnalytics.logEvent("Adorable_Photo_Opened", params);
            Toast.makeText(this, R.string.select_image, Toast.LENGTH_SHORT).show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    InputStream imageStream = null;
                    try {
                        imageStream = getContentResolver().openInputStream(selectedImageUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                    selectedImage = getResizedBitmap(selectedImage, 1100);// 400 is for example, replace with desired size

                    Uri imguri = getImageUri(getApplicationContext(), selectedImage);
                    String img = imguri.toString();
                    PreferencesUtility.setImagePath(img);
                    MainActivity.albumart.setImageURI(imguri);
                    Toast.makeText(this, R.string.image_selected, Toast.LENGTH_SHORT).show();
                    Bundle params = new Bundle();
                    SettingsActivity.mFirebaseAnalytics.logEvent("Adorable_Photo_Opened", params);
                }
            }
        }
        finish();
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    /* Get the real path from the URI */
    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }


}
