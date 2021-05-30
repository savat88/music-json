package com.olaapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View.OnClickListener;
import android.app.Notification;
import android.support.v4.app.NotificationCompat;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.app.NotificationManager;
import android.widget.RemoteViews;
import android.app.PendingIntent;
import android.os.Build;
import java.text.DateFormat;
import java.util.Date;
import com.olaapp.playerview.MusicPlayerView;

public class Main2Activity extends Activity implements OnCompletionListener, SeekBar.OnSeekBarChangeListener {

    private ImageButton btnPlay,btnPlay1;

    
    private SeekBar songProgressBar;
    private TextView songCurrentDurationLabel;
    private TextView songTotalDurationLabel;
    ImageView imageView;
    private MediaPlayer mp;
    private Handler mHandler = new Handler();
    private Utilities utils;
    private int seekForwardTime = 5000; 
    private int seekBackwardTime = 5000;
    
    String url, image, song, artists;
    View testview;
    Activity savat;
	private RemoteViews viewBig;
	
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String SAVAT = "play";
	MusicPlayerView mpv;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player);
		savat = this;
		buttonClick();
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        image = intent.getStringExtra("image");
        song = intent.getStringExtra("song");
        artists = intent.getStringExtra("artists");
		
		testview = findViewById(android.R.id.content);
        btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        ImageButton btnForward = (ImageButton) findViewById(R.id.btnForward);
        ImageButton btnBackward = (ImageButton) findViewById(R.id.btnBackward);
        
		mpv = (MusicPlayerView) findViewById(R.id.ik);
      //  imageView = (ImageView) findViewById(R.id.ik);
      //  Glide.with(Main2Activity.this).load(image).into(imageView);
		mpv.setMax(100);
		mpv.setProgress(0);
		
		mpv.setCoverURL(image);
		mpv.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View v) {
					if (mpv.isRotating()) {
						mpv.stop();
						mp.pause();
					} else {
						mpv.start();
						mp.start();
					}
				}
			});
	
		
        
        songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
        TextView songTitleLabel = (TextView) findViewById(R.id.songTitle);
        songTitleLabel.setText(song);
        songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
        songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);
        TextView artis = (TextView) findViewById(R.id.art);
        artis.setText(artists);
		
		new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					btnPlay1.performClick(); //performClick = กดออโต้
					mpv.start();
				}
			}, 500);
	
        btnPlay.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
                
                final ProgressDialog mDialog = new ProgressDialog(Main2Activity.this);


                AsyncTask<String, String, String> mp3Play = new AsyncTask<String, String, String>() {

                    @Override
                    protected void onPreExecute() {
                        mDialog.setMessage("กำลังโหลด...");
                        mDialog.show();
                    }

                    @Override
                    protected String doInBackground(String... params) {
                        try {
                            mp.setDataSource(params[0]);
                            mp.prepare();
                        } catch (Exception ex) {

                        }
                        return "";
                    }

                    @Override
                    protected void onPostExecute(String s) {
						
						
						
                        if (!mp.isPlaying()) {
                            mp.start();
							//saveData();
                            btnPlay.setImageResource(R.drawable.ic_pause);
							
                        } else {
                            mp.pause();
                            btnPlay.setImageResource(R.drawable.ic_play);
                        }

                        updateProgressBar();
                        mDialog.dismiss();
						showNotification();
                    }
                };

                mp3Play.execute(url); // ตั้ง url เว็บ

                mp.start();
            }
        });

        mp = new MediaPlayer();
        utils = new Utilities();

     
        songProgressBar.setOnSeekBarChangeListener(this);
        mp.setOnCompletionListener(this);
      
        btnForward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                
                int currentPosition = mp.getCurrentPosition();
                
                if (currentPosition + seekForwardTime <= mp.getDuration()) {
                   
                    mp.seekTo(currentPosition + seekForwardTime);
                } else {
                    
                    mp.seekTo(mp.getDuration());
                }
            }
        });

     
        btnBackward.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                
                int currentPosition = mp.getCurrentPosition();
              
                if (currentPosition - seekBackwardTime >= 0) {
                    
                    mp.seekTo(currentPosition - seekBackwardTime);
                } else {
                   
                    mp.seekTo(0);
                }

            }
        });

        
    }

	@Override
	public void onBackPressed() {
		
		mp.pause();
		super.onBackPressed();
	}

	public void buttonClick() {
		
		btnPlay1 = (ImageButton) findViewById(R.id.btnPlay);
		btnPlay1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!mp.isPlaying()) {
						mp.start();
						}
				} 
				
			}); 
		
		
	}
	
	private void showNotification() {
        Banner.make(testview, savat,Banner.SUCCESS,"ชื่อช่อง: " + artists ,Banner.TOP,5000).show();
    }
	
	
	public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        
        editor.putBoolean(SAVAT, mp.isPlaying());

        editor.apply();

        Toast.makeText(this, "เทสการทำงาน", Toast.LENGTH_SHORT).show();
    }
	
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mp.getDuration();
            long currentDuration = mp.getCurrentPosition();

          
            songTotalDurationLabel.setText("" + utils.milliSecondsToTimer(totalDuration));
           
            songCurrentDurationLabel.setText("" + utils.milliSecondsToTimer(currentDuration));

            
            int progress = (int) (utils.getProgressPercentage(currentDuration, totalDuration));
           
            songProgressBar.setProgress(progress);

           
            mHandler.postDelayed(this, 100);
        }
    };

   
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

    }

   
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mp.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

    
        mp.seekTo(currentPosition);

     
        updateProgressBar();
    }

  
    @Override
    public void onCompletion(MediaPlayer arg0) {
        btnPlay.setImageResource(R.drawable.ic_play);
    }
}
