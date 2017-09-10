//package com.example.godemo;
//
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import com.go.algorithm.Detector;
//
//import static android.widget.Toast.LENGTH_SHORT;
//
//public class ResultActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_result);
//
////        Intent intent=getIntent();
////        Bundle bundle=intent.getExtras();
////        Bitmap bitmap = bundle.getParcelable("bitmap");
//
////        int[] result = Detector.Detect(GlobalEnvironment.ScanedBitmap, GlobalEnvironment.ScanedBitmap.getWidth(), GlobalEnvironment.ScanedBitmap.getHeight(), 19);
////        if (result != null) {
//            ImageView imageView = (ImageView) findViewById(R.id.imageView);
//            imageView.setImageBitmap(GlobalEnvironment.ScanedBitmap);
////        }
//        Toast.makeText(this, "成功", LENGTH_SHORT).show();
//    }
//}

package com.example.godemo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.go.algorithm.Detector;
import com.ui.algorithm.CalculateResult;
import com.ui.algorithm.SgfReader;
import com.ui.model.Board;
import com.ui.model.Log;

import java.io.File;
import java.io.IOException;


public class ResultActivity extends Activity implements OnTouchListener, OnClickListener, OnCheckedChangeListener {
    RelativeLayout boardLayout;
    ImageView boardImage;
    TextView textResult;
    Button btnAnalyse;
    RadioButton radioCN;
    RadioButton radioJPN;
    RadioGroup radioGroup;
    LinearLayout jpnLayout;

    ImageView[][] _pieceImage = new ImageView[19][19];

    CalculateResult cr = new CalculateResult(19);

    int[][] originalState;

    Handler handler;

    float compensation = 3.75f;
    int rule = 1;

    private TabHost tabHost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        tabHost.addTab(tabHost.newTabSpec("boardGrid").setIndicator("四边").setContent(R.id.boardGrid));
        tabHost.addTab(tabHost.newTabSpec("boardCircles").setIndicator("圆圈").setContent(R.id.boardCircles));
        tabHost.addTab(tabHost.newTabSpec("boardUI").setIndicator("解析图").setContent(R.id.boardUI));
        tabHost.addTab(tabHost.newTabSpec("boardBitmap").setIndicator("扫描图").setContent(R.id.boardBitmap));

        jpnLayout = (LinearLayout) findViewById(R.id.jpnLayout);
        boardLayout = (RelativeLayout) findViewById(R.id.boardLayout);
        boardImage = (ImageView) findViewById(R.id.boardImage);
        textResult = (TextView) findViewById(R.id.textResult);
        btnAnalyse = (Button) findViewById(R.id.btnAnalyse);
        btnAnalyse.setOnClickListener(this);

        radioCN = (RadioButton) findViewById(R.id.radioButtonCN);
        radioJPN = (RadioButton) findViewById(R.id.radioButtonJPN);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
        radioGroup.setOnCheckedChangeListener(this);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;// 宽度
        // int height = dm.heightPixels;// 高度
        // 分为21份
        Board.Width = width;
        Board.OFFSET = width / 40;
        Board.SIZE = width / 20;
        boardLayout.setLayoutParams(new LinearLayout.LayoutParams(width, width));
        boardImage.setLayoutParams(new RelativeLayout.LayoutParams(width, width));
        boardImage.setOnTouchListener(this);

        drawBoard();

        InitBoard(GlobalEnvironment.Data);

        ImageView imageBoard = (ImageView) findViewById(R.id.imageView);
        imageBoard.setImageBitmap(GlobalEnvironment.ScanedBitmap);

        //四边debug
        Bitmap gridBitmap=Bitmap.createBitmap(GlobalEnvironment.ScanedBitmap.getWidth(),GlobalEnvironment.ScanedBitmap.getHeight(),Config.ARGB_8888);
        Detector.GetGrid(gridBitmap);
        ImageView imageViewGrid = (ImageView) findViewById(R.id.imageViewGrid);
        imageViewGrid.setImageBitmap(gridBitmap);

        //圆圈debug
        Bitmap circleBitmap=Bitmap.createBitmap(GlobalEnvironment.ScanedBitmap.getWidth(),GlobalEnvironment.ScanedBitmap.getHeight(),Config.ARGB_8888);
        Detector.GetCircle(circleBitmap);
        ImageView imageViewCircles = (ImageView) findViewById(R.id.imageViewCircles);
        imageViewCircles.setImageBitmap(circleBitmap);
    }

    private void InitBoard(int[] data) {
        originalState=new int[19][19];
        for (int i = 0; i < 19; i++) {
            originalState[i]=new int[19];
            for (int j = 0; j < 19; j++) {
                originalState[i][j] = data[j * 19 + i];
                _pieceImage[i][j] = new ImageView(this);
                _pieceImage[i][j].setLayoutParams(new RelativeLayout.LayoutParams((int) Board.SIZE,
                        (int) Board.SIZE));
                _pieceImage[i][j].setX(Board.OFFSET + i * Board.SIZE);
                _pieceImage[i][j].setY(Board.OFFSET + j * Board.SIZE);
                // _pieceImage[i][j].Stretch = Stretch.Fill;
                boardLayout.addView(_pieceImage[i][j]);
            }
        }
        showState(originalState);
    }

    private void showState(int[][] state) {
        ApplicationInfo appInfo = getApplicationInfo();
        for (int i = 0; i < 19; i++) {
            for (int j = 0; j < 19; j++) {
                int p = state[i][j];
                if (p == 0) {
                    _pieceImage[i][j].setBackground(null);
                    continue;
                }

                String name = "";
                switch (p) {
                    case 1:
                        name = "black";
                        break;
                    case 2:
                        name = "white";
                        break;
                    case 3:
                        name = "deadblack";
                        break;
                    case 4:
                        name = "deadwhite";
                        break;
                    case 5:
                        name = "blackterri";
                        break;
                    case 6:
                        name = "whiteterri";
                        break;
                }
                // 得到该图片的id(name 是该图片的名字，"drawable"
                // 是该图片存放的目录，appInfo.packageName是应用程序的包)
                int resID = getResources().getIdentifier(name, "drawable", appInfo.packageName);
                _pieceImage[i][j].setBackground(getResources().getDrawable(resID));
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        try {
            // 求点击坐标
            int x = ((int) event.getX() - Board.OFFSET) / Board.SIZE;
            int y = ((int) event.getY() - Board.OFFSET) / Board.SIZE;
            if (x < 0) x = 0;
            if (y < 0) y = 0;
            if (x > 18) x = 18;
            if (y > 18) y = 18;
            cr.markDead(originalState, x, y);

            float win = cr.getWin(rule, originalState) - compensation;
            showResult(win);
            showState(originalState);
            return false;
        } catch (Exception e) {
            Log.PrintError(e.toString());
            System.exit(0);
            return false;
        }
    }

    private void showResult(float win) {
        if (win > 0) {
            textResult.setText("黑棋胜" + win + (rule == 1 ? "子" : "目"));
        } else {
            textResult.setText("白棋胜" + -win + (rule == 1 ? "子" : "目"));
        }
    }

    private void drawBoard() {
        // 先用Paint和Canvas画到一张bitmap上，然后将bitmap放进view里
        // Bitmap bmp=BitmapFactory.decodeResource(getResources(), R.drawable.icon);//只读,不能直接在bmp上画
        Bitmap newb = Bitmap.createBitmap(Board.Width, Board.Width, Config.ARGB_8888);

        Canvas canvas = new Canvas(newb);
        canvas.drawColor(Color.TRANSPARENT);

        Paint p = new Paint();
        p.setColor(Color.BLACK);

        int startP = Board.OFFSET + Board.SIZE / 2;
        int stopP = startP + 18 * Board.SIZE;
        for (int i = 0; i < 19; i++) {
            int dynamic = startP + i * Board.SIZE;
            canvas.drawLine(dynamic, startP, dynamic, stopP, p);
            canvas.drawLine(startP, dynamic, stopP, dynamic, p);
        }
        // canvasTemp.drawBitmap(bmp, 50, 50, p);//画图
        boardImage.setImageBitmap(newb);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(btnAnalyse)) {
            // CalculateResult cr = new CalculateResult();
            float win = cr.getWin(rule, originalState) - compensation;
            showResult(win);
            showState(originalState);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.radioButtonCN) {
            compensation = 3.75f;
            jpnLayout.setVisibility(View.GONE);
            rule = 1;
        } else {
            compensation = 6.5f;
            jpnLayout.setVisibility(View.VISIBLE);
            rule = 0;
        }
        float win = cr.getWin(rule, originalState) - compensation;
        showResult(win);
    }
}
