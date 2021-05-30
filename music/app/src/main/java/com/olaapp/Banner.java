package com.olaapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.logging.Handler;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


public class Banner {

    private Context mContext;
    private Activity savat;
    private View popupView;
    private View testView;
    private  boolean focusable;
    private  boolean asDropDown;
    private  boolean fillScreen;
    private PopupWindow popupWindow;

    public static int TOP = Gravity.TOP;
    public static int BOTTOM = Gravity.BOTTOM;

    public static int SUCCESS = 1;


    private boolean showBanner = false;
    private int gravity = TOP;
    private int delay = 1500;
    private int duration = 0;
    private int bannerType;

    private TextView textMessage;
    private RelativeLayout rlCancel;

    private Integer animationStyle;

    private int layout;

    private String TAG = getClass().getName();

    private static Banner instance;

    public interface BannerListener {
        void onViewClickListener(View view);
    }


    public Banner(){

    }

    public  Banner(View view, final Activity activity) {

        this.savat = activity;
        this.testView = view;
    }

    public static Banner make(View view,Activity activity, int bannerType, String message, int position) {

        if(instance == null){
            instance = new Banner();
        }else {
            if(instance.showBanner){
                instance.dismissBanner();
            }
        }
		instance.testView = view;
		instance.savat = activity;
		instance.setBannerLayout(bannerType);
		instance.setLayout(instance.layout);
		instance.setBannerText(message);
		instance.setDuration(0);
		instance.setGravity(position);
		instance.setCancelButton();
		instance.setAnimationstyle();
		instance.fillScreen = false;
		instance.asDropDown = false;

        return instance;
    }


    public static Banner make(View view,Activity activity, int bannerType, String message, int position, int duration) {
        if(instance == null){
            instance = new Banner();
        }else {
            if(instance.showBanner){
                instance.dismissBanner();
            }
        }
        instance.testView = view;
        instance.savat = activity;
        instance.setBannerLayout(bannerType);
        instance.setLayout(instance.layout);
        instance.setBannerText(message);
        instance.setDuration(duration);
        instance.setGravity(position);
        instance.setCancelButton();
        instance.setAnimationstyle();
        instance.fillScreen = false;
        instance.asDropDown = false;
        return instance;
    }


    public static Banner make(View view,Activity activity, int position, int Customlayout) {

        if(instance == null){
            instance = new Banner();
        }else {
            if(instance.showBanner){
                instance.dismissBanner();
            }
        }
        instance.testView = view;
        instance.savat = activity;
        instance.setLayout(Customlayout);
        instance.setDuration(0);
        instance.setGravity(position);
        instance.fillScreen = false;
        instance.asDropDown = false;

        return instance;
    }


    public static Banner make(View view,Activity activity, int position, int Customlayout,boolean asDropDown) {

        if(instance == null){
            instance = new Banner();
        }else {
            if(instance.showBanner){
                instance.dismissBanner();
            }
        }
        instance.testView = view;
        instance.savat = activity;
        instance.setLayout(Customlayout);
        instance.setDuration(0);
        instance.setGravity(position);
        instance.asDropDown = asDropDown;
        instance.fillScreen = false;

        return instance;
    }

    public static Banner make(View view,Activity activity, int Customlayout,boolean fillScreen) {

        if(instance == null){
            instance = new Banner();
        }else {
            if(instance.showBanner){
                instance.dismissBanner();
            }
        }
        instance.testView = view;
        instance.savat = activity;
        instance.setLayout(Customlayout);
        instance.setDuration(0);
        instance.fillScreen = fillScreen;
        instance.asDropDown = false;

        return instance;
    }
	public static Banner getInstance(){
        if(instance == null){
            instance = new Banner();
        }
        return instance;
    }

    public int getDelay() {
        return delay;
    }


    public void setDelay(int delay) {
        this.delay = delay;
    }


    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
    }


    public int getGravity() {
        return gravity;
    }


    public void setFocusable(boolean focusable) {
        this.focusable = focusable;
    }


    public boolean isFocusable() {
        return focusable;
    }


    private void setBannerLayout(int type){

        bannerType = type;
        int result = 0;
        switch (bannerType){
            case 1:
                result = R.layout.success;
                break;

        }
        layout =  result;
    }



    private void setBannerText(String text){
        switch (bannerType){
            case 1:
                textMessage = popupView.findViewById(R.id.success_message);
                textMessage.setText(text);
                break;

        }
    }

    public void setAsDropDown(boolean asDropDown) {
        this.asDropDown = asDropDown;
    }

    public boolean isAsDropDown() {
        return asDropDown;
    }

    public void setFillScreen(boolean fillScreen) {
        this.fillScreen = fillScreen;
    }

    public boolean isFillScreen() {
        return fillScreen;
    }


    private void setCancelButton(){
        if(duration > 0){
            rlCancel.setVisibility(View.INVISIBLE);
        }else {
            rlCancel.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						popupWindow.dismiss();
					}
				});
        }
    }
	public void setLayout(int layout){
        if(savat != null){
            LayoutInflater inflater = (LayoutInflater)
				savat.getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            popupView = inflater.inflate(layout, null);
            rlCancel = popupView.findViewById(R.id.rlCancel);
        }
    }


    public View getBannerView() {
        return popupView;
    }

    public void dismissBanner(){
        try{
            popupWindow.dismiss();
            showBanner = false;
            asDropDown = false;
        }catch (Exception e){
            Log.e(TAG,e.toString());
        }
    }

    public void show(){

        if(savat!=null){
			showBanner = true;

			int width = LinearLayout.LayoutParams.MATCH_PARENT;
			int height = LinearLayout.LayoutParams.WRAP_CONTENT;

			if(fillScreen){
				height = LinearLayout.LayoutParams.MATCH_PARENT;
			}

			popupWindow = new PopupWindow(popupView, width, height, focusable);

			if(animationStyle != null){
				popupWindow.setAnimationStyle(animationStyle);
			}

			testView.post(new Runnable() {
                    public void run() {
                        if(asDropDown){
                            popupWindow.showAsDropDown(testView,0,0);
                        }else{
                            popupWindow.showAtLocation(testView, gravity, 0, 0);
                        }
                    }
                });

			autoDismiss(duration);

        }
    }

    private void setAnimationstyle(){
        if(gravity == TOP)
            animationStyle = R.style.topAnimation;
        else if (gravity == BOTTOM)
            animationStyle = R.style.bottomAnimation;
    }

    public void setCustomAnimationStyle(int customAnimationStyle){
        animationStyle =customAnimationStyle;
    }



    private void autoDismiss(int duration){
        if(duration > 0){
            android.os.Handler handler = new android.os.Handler();
            handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						dismissBanner();
					}
				},duration);
        }
    }
}
