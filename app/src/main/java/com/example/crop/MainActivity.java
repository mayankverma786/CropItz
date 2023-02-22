package com.example.crop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getColor(R.color.teal_200)));
        }
        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color=\"black\">"+ getString(R.string.app_name)+"</font>"));
        // showing the back button in action bar
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        Button pickbutton = findViewById(R.id.button);
        pickbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCropActivity();
            }
        });
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static final int PICK_IMAGE = 1;

    private void startCropActivity() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    public static class Utils {
        public static Bitmap getMutableBitmap(Bitmap bitmap) {
            if (bitmap.isMutable()) {
                return bitmap;
            } else {
                Bitmap mutableBitmap = bitmap.copy(bitmap.getConfig(), true);
                bitmap.recycle();
                return mutableBitmap;
            }
        }
    }

    public static Bitmap waterMarkBitmap(Bitmap src, String watermark) {
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap mutableBitmap = Utils.getMutableBitmap(src);
        Bitmap result = Bitmap.createBitmap(w, h, mutableBitmap.getConfig());

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0f, 0f, null);

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(92f);
        paint.setAntiAlias(true);
        paint.setAlpha(150);  // accepts value between 0 to 255, 0 means 100% transparent, 255 means 100% opaque.
        paint.setUnderlineText(false);
        canvas.drawText(watermark, w / 10f, h / 4f, paint);

        return result;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            CropImage.activity(data.getData())
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                // Load the original image
                Bitmap originalBitmap = Utils.getMutableBitmap(BitmapFactory.decodeFile(resultUri.getPath()));

                // Add the watermark
                Bitmap watermarkedBitmap = waterMarkBitmap(originalBitmap, "by Reena");

                ImageView myImageView = findViewById(R.id.imageView);
                myImageView.setImageBitmap(watermarkedBitmap);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}