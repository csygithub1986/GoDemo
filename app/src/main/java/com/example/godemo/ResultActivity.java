package com.example.godemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.go.algorithm.Detector;

import static android.widget.Toast.LENGTH_SHORT;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

//        Intent intent=getIntent();
//        Bundle bundle=intent.getExtras();
//        Bitmap bitmap = bundle.getParcelable("bitmap");

//        int[] result = Detector.Detect(GlobalEnvironment.ScanedBitmap, GlobalEnvironment.ScanedBitmap.getWidth(), GlobalEnvironment.ScanedBitmap.getHeight(), 19);
//        if (result != null) {
            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageBitmap(GlobalEnvironment.ScanedBitmap);
//        }
        Toast.makeText(this, "成功", LENGTH_SHORT).show();
    }
}
