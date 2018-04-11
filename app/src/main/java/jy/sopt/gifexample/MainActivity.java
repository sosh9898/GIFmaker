package jy.sopt.gifexample;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.gifencoder.AnimatedGifEncoder;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    final static int SELECT_MOVIE = 1111;
    RecyclerView rcv;
    VideoView videoView;
    TextView curTime, totalTime;
    SeekBar seekBar;
    Button loadbtn, playbtn, pausebtn, capturebtn, savebtn;
    Handler updateHandler = new Handler();
    List<Bitmap> list;

    MediaMetadataRetriever mediaMetadataRetriever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        seekBarSetting();
        permissionCheck();

    }

    public void initView(){
        videoView = (VideoView)findViewById(R.id.videoView);
        rcv = (RecyclerView) findViewById(R.id.rcv);
        curTime = (TextView) findViewById(R.id.turTime);
        totalTime = (TextView) findViewById(R.id.totalTime);
        loadbtn = (Button) findViewById(R.id.loadbtn);
        pausebtn = (Button) findViewById(R.id.btnPause);
        playbtn = (Button) findViewById(R.id.btnPlay);
        capturebtn = (Button) findViewById(R.id.capture_btn);
        savebtn = (Button) findViewById(R.id.save_btn);
        capturebtn.setOnClickListener(this);
        loadbtn.setOnClickListener(this);
        playbtn.setOnClickListener(this);
        pausebtn.setOnClickListener(this);
        savebtn.setOnClickListener(this);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        mediaMetadataRetriever = new MediaMetadataRetriever();
    }

    public void seekBarSetting(){
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                videoView.seekTo(seekBar.getProgress());
            }
        });
    }

    public void permissionCheck(){
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }


        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.loadbtn:
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.setType("video/*");
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            try {
                startActivityForResult(i, SELECT_MOVIE);
            } catch (android.content.ActivityNotFoundException e) {
                e.printStackTrace();
            }
            break;
            case R.id.btnPlay:
                playVideo();
                break;
            case R.id.btnPause:
                pauseVideo();

                break;
            case R.id.capture_btn:
                captureFrame();
                break;
            case R.id.save_btn:
                saveGIF();
                break;

            default:
                break;
        }
    }
    public byte[] generateGIF() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        encoder.setDelay(100);
        encoder.start(bos);

        for (Bitmap bitmap : list) {
            encoder.addFrame(bitmap);
        }
        list.clear();
        encoder.finish();

        return bos.toByteArray();
    }

    public void saveGIF(){
        File rootPath = new File(Environment.getExternalStorageDirectory(), "gif");
            if(!rootPath.exists()) {
                rootPath.mkdirs();
            }

        FileOutputStream outStream = null;

        try{
            outStream = new FileOutputStream(rootPath+ "/핵데이가즈아!.gif");
            outStream.write(generateGIF());
            outStream.close();
            Toast.makeText(this, " ??? " ,Toast.LENGTH_SHORT).show();
        }catch(Exception e){
            Toast.makeText(this, e.toString() ,Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == RESULT_OK)
        {
           if (requestCode == SELECT_MOVIE)
            {
                Uri uri = intent.getData();
                loadVideo(uri);
            }
        }
    }

    @SuppressLint("NewApi")
    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            }
        }
        return null;
    }




    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }


    public void loadVideo(Uri uri) {

        Toast.makeText(getApplicationContext(), "Loading Video. Plz wait", Toast.LENGTH_LONG).show();
        videoView.setVideoURI(uri);
        videoView.requestFocus();

        // 토스트 다이얼로그를 이용하여 버퍼링중임을 알린다.
        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
             @Override
             public boolean onInfo(MediaPlayer mp, int what, int extra) {
                   switch(what){
                      case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                      //  Progress Diaglog 출력
                      Toast.makeText(getApplicationContext(), "Buffering", Toast.LENGTH_LONG).show();
                      break;
                      case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                      // Progress Dialog 삭제
                      Toast.makeText(getApplicationContext(), "Buffering finished.\nResume playing", Toast.LENGTH_LONG).show();
                      videoView.start();
                      break;
                      }
                      return false;
                    }
                }

        );

        // 플레이 준비가 되면, seekBar와 PlayTime을 세팅하고 플레이를 한다.
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
                long finalTime = videoView.getDuration();
                totalTime.setText(String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime)))
                );
                seekBar.setMax((int) finalTime);
                seekBar.setProgress(0);
                updateHandler.postDelayed(updateVideoTime, 100);

            }
        });

//        new NDK().getNDKTestString();

        mediaMetadataRetriever.setDataSource(getPath(this,uri));


    }

    public void captureFrame(){
        int currentPosition = videoView.getCurrentPosition();

        for(int i = currentPosition; i<videoView.getDuration(); i+=300) {
            Bitmap bitmap = mediaMetadataRetriever
                    .getFrameAtTime(i * 1000);
            list.add(Bitmap.createScaledBitmap(bitmap, 500, 500, true));

            bitmap.recycle();
        }
    }


    public void playVideo(){
        videoView.requestFocus();
        videoView.start();

    }

    public void pauseVideo(){
        videoView.pause();
    }

    // seekBar를 이동시키기 위한 쓰레드 객체
    // 100ms 마다 viewView의 플레이 상태를 체크하여, seekBar를 업데이트 한다.
    private Runnable updateVideoTime = new Runnable(){
        public void run(){
            long currentPosition = videoView.getCurrentPosition();
            curTime.setText(currentPosition/1000+"");
            seekBar.setProgress((int) currentPosition);
            updateHandler.postDelayed(this, 100);

        }
    };
}

