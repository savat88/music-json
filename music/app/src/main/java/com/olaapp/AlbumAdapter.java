package com.olaapp;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.app.ProgressDialog;
import android.app.Activity;
 
public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyViewHolder>  implements MediaPlayer.OnBufferingUpdateListener,MediaPlayer.OnCompletionListener {
	
    Context context;
    
    String name, song;
    private MediaPlayer mediaPlayer;
    ArrayList<Album> albumList = new ArrayList<>();
    List<Album> contactListFiltered = new ArrayList<>();
    ImageButton buttonViewOption;
	
	
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView gl, g2;
        ImageView imageView;
        MediaPlayer mediaPlayer;
        ArrayList<Album> albumList = new ArrayList<>();
		
		final Handler handler = new Handler();
        public MyViewHolder(View itemView) {
            super(itemView);
            this.albumList = albumList;

            gl = (TextView) itemView.findViewById(R.id.goi);
            g2 = (TextView) itemView.findViewById(R.id.goi2);
            buttonViewOption = (ImageButton) itemView.findViewById(R.id.textViewOptions);

            imageView = (ImageView) itemView.findViewById(R.id.image);
			
			
			
        }

    }

    public AlbumAdapter(Context context, ArrayList<Album> albumList) {
        this.context = context;
        this.albumList = albumList;
        this.contactListFiltered = albumList;
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewtype) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_card, parent, false);

        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final AlbumAdapter.MyViewHolder holder, final int position) {
        final Album album = albumList.get(position);
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();//โหลดรูป
        
        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
      
        holder.gl.setText(album.getSong()); //ชื่อค้นหา
        holder.g2.setText(album.getArtists()); //ชื่อช่อง
        name = album.getUrl(); //ลิ้ง ทำงานหลัง
        song = album.getSong(); //ชื่อค้นหาแสดงส่วนหัว
		
		
		
        Glide.with(context).load(album.getCover_image()).into(holder.imageView);
		
		holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					
					final Album savat = albumList.get(position);

					Intent intent = new Intent(context, Main2Activity.class);
					intent.putExtra("url", savat.getUrl());
					intent.putExtra("image", savat.getCover_image());
					intent.putExtra("song", savat.getSong());
					intent.putExtra("artists", savat.getArtists());
					context.startActivity(intent);
                    
				}
			});
			
		buttonViewOption.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					
					final ProgressDialog mDialog = new ProgressDialog(context);


					AsyncTask<String,String,String> mp3Play = new AsyncTask<String, String, String>() {

						
						@Override
						protected void onPreExecute() {
							mDialog.setMessage("กำลังเซื่อมต่อเซิฟเวอร์");
							mDialog.show();
						}

						@Override
						protected String doInBackground(String... params) {
							try{
								mediaPlayer.setDataSource(params[0]);
								mediaPlayer.prepare();
							}
							catch (Exception ex)
							{

							}
							return "";
						}

						@Override
						protected void onPostExecute(String s) {
							
							if (!mediaPlayer.isPlaying()) 
							{
								mediaPlayer.start();
								buttonViewOption.setImageResource(R.drawable.sapu);
							}
							else
							{
								mediaPlayer.stop();
								buttonViewOption.setImageResource(R.drawable.sap);
								
							}

							
							mDialog.dismiss();
						
						}

					};

					mp3Play.execute(album.getUrl());


				}
			});

		mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);
	}

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public void setfilter(ArrayList<Album> newlist) {
        albumList = new ArrayList<>();
        albumList.addAll(newlist);
        notifyDataSetChanged();
    }
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
        
    }


    public void onCompletion(MediaPlayer mp) {
        buttonViewOption.setImageResource(R.drawable.sapu);


    }
	
}

