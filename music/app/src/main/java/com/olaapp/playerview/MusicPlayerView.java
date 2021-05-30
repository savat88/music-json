package com.olaapp.playerview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.olaapp.R;
public class MusicPlayerView extends View implements OnPlayPauseToggleListener {

	
	private static Rect mRectText;

	
	private static Paint mPaintTime;

	
	private RectF rectF;

	
	private static Paint mPaintProgressEmpty;

	
	private static Paint mPaintProgressLoaded;

	
	private OnClickListener onClickListener;

	
	private static Paint mPaintButton;

	
	private static Region mButtonRegion;

	
	private static Paint mPaintCover;

	
	private Bitmap mBitmapCover;

	
	private BitmapShader mShader;

	private float mCoverScale;

	
	private int mHeight;
	private int mWidth;

	
	private float mCenterX;
	private float mCenterY;

	
	private int mRotateDegrees;

	
	private Handler mHandlerRotate;

	
	private final Runnable mRunnableRotate = new Runnable() {
		@Override public void run() {
			if (isRotating) {

				if (currentProgress > maxProgress) {
					currentProgress = 0;
					setProgress(currentProgress);
					//stop(); เวลาจบจะหยุด
				}

				updateCoverRotate();
				mHandlerRotate.postDelayed(mRunnableRotate, ROTATE_DELAY);
			}
		}
	};

	
	private Handler mHandlerProgress;

	
	private Runnable mRunnableProgress = new Runnable() {
		@Override public void run() {
			if (isRotating) {
				currentProgress++;
				mHandlerProgress.postDelayed(mRunnableProgress, PROGRESS_SECOND_MS);
			}
		}
	};

	
	private boolean isRotating;

	
	private static int ROTATE_DELAY = 10;

	
	private static int PROGRESS_SECOND_MS = 1000;

	
	private static int VELOCITY = 1;

	
	private int mCoverColor = Color.GRAY;

	
	private float mButtonRadius = 120f;

	private int mButtonColor = Color.DKGRAY;

	
	private int mProgressEmptyColor = 0x20FFFFFF;

	
	private int mProgressLoadedColor = 0xFF00815E;

	
	private int mTextSize = 40;

	
	private int mTextColor = 0xFFFFFFFF;

	
	private int currentProgress = 0;

	
	private int maxProgress = 100;

	
	private boolean isAutoProgress = true;

	
	private boolean mProgressVisibility = true;

	
	private static final long PLAY_PAUSE_ANIMATION_DURATION = 200;

	
	private PlayPauseDrawable mPlayPauseDrawable;

	
	private AnimatorSet mAnimatorSet;

	private boolean mFirstDraw = true;

	
	public MusicPlayerView(Context context) {
		super(context);
		init(context, null);
	}

	
	public MusicPlayerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	
	public MusicPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public MusicPlayerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context, attrs);
	}

	
	private void init(Context context, AttributeSet attrs) {

		setWillNotDraw(false);
		mPlayPauseDrawable = new PlayPauseDrawable(context);
		mPlayPauseDrawable.setCallback(callback);
		mPlayPauseDrawable.setToggleListener(this);

	
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.playerview);
		Drawable mDrawableCover = a.getDrawable(R.styleable.playerview_cover);
		if (mDrawableCover != null) mBitmapCover = drawableToBitmap(mDrawableCover);

		mButtonColor = a.getColor(R.styleable.playerview_buttonColor, mButtonColor);
		mProgressEmptyColor =
			a.getColor(R.styleable.playerview_progressEmptyColor, mProgressEmptyColor);
		mProgressLoadedColor =
			a.getColor(R.styleable.playerview_progressLoadedColor, mProgressLoadedColor);
		mTextColor = a.getColor(R.styleable.playerview_textColor, mTextColor);
		mTextSize = a.getDimensionPixelSize(R.styleable.playerview_textSize, mTextSize);
		a.recycle();

		mRotateDegrees = 0;

		
		mHandlerRotate = new Handler();

		
		mHandlerProgress = new Handler();

	
		mPaintButton = new Paint();
		mPaintButton.setAntiAlias(true);
		mPaintButton.setStyle(Paint.Style.FILL);
		mPaintButton.setColor(mButtonColor);

		
		mPaintProgressEmpty = new Paint();
		mPaintProgressEmpty.setAntiAlias(true);
		mPaintProgressEmpty.setColor(mProgressEmptyColor);
		mPaintProgressEmpty.setStyle(Paint.Style.STROKE);
		mPaintProgressEmpty.setStrokeWidth(12.0f);

		mPaintProgressLoaded = new Paint();
		mPaintProgressEmpty.setAntiAlias(true);
		mPaintProgressLoaded.setColor(mProgressLoadedColor);
		mPaintProgressLoaded.setStyle(Paint.Style.STROKE);
		mPaintProgressLoaded.setStrokeWidth(12.0f);

		mPaintTime = new Paint();
		mPaintTime.setColor(mTextColor);
		mPaintTime.setAntiAlias(true);
		mPaintTime.setTextSize(mTextSize);

		
		rectF = new RectF();
		mRectText = new Rect();
	}

	
	@Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		mWidth = MeasureSpec.getSize(widthMeasureSpec);
		mHeight = MeasureSpec.getSize(heightMeasureSpec);

		int minSide = Math.min(mWidth, mHeight);
		mWidth = minSide;
		mHeight = minSide;

		this.setMeasuredDimension(mWidth, mHeight);

		mCenterX = mWidth / 2f;
		mCenterY = mHeight / 2f;

		
		rectF.set(20.0f, 20.0f, mWidth - 20.0f, mHeight - 20.0f);

		
		mButtonRadius = mWidth / 8.0f;

		
		mPlayPauseDrawable.resize((1.2f * mButtonRadius / 5.0f), (3.0f * mButtonRadius / 5.0f) + 10.0f,
								  (mButtonRadius / 5.0f));

		mPlayPauseDrawable.setBounds(0, 0, mWidth, mHeight);

		mButtonRegion = new Region((int) (mCenterX - mButtonRadius), (int) (mCenterY - mButtonRadius),
								   (int) (mCenterX + mButtonRadius), (int) (mCenterY + mButtonRadius));

		createShader();

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	
	@Override protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (mShader == null) return;

	
		float radius = mCenterX <= mCenterY ? mCenterX - 75.0f : mCenterY - 75.0f;
		canvas.rotate(mRotateDegrees, mCenterX, mCenterY);
		canvas.drawCircle(mCenterX, mCenterY, radius, mPaintCover);

		
		canvas.rotate(-mRotateDegrees, mCenterX, mCenterY);

		
		canvas.drawCircle(mCenterX, mCenterY, mButtonRadius, mPaintButton);

		if (mProgressVisibility) {
		
			canvas.drawArc(rectF, 145, 250, false, mPaintProgressEmpty);

	
			canvas.drawArc(rectF, 145, calculatePastProgressDegree(), false, mPaintProgressLoaded);

			
			String leftTime = secondsToTime(calculateLeftSeconds());
			mPaintTime.getTextBounds(leftTime, 0, leftTime.length(), mRectText);

			canvas.drawText(leftTime, (float) (mCenterX * Math.cos(Math.toRadians(35.0))) + mWidth / 2.0f
							- mRectText.width() / 1.5f,
							(float) (mCenterX * Math.sin(Math.toRadians(35.0))) + mHeight / 2.0f + mRectText.height()
							+ 15.0f, mPaintTime);

			
			String passedTime = secondsToTime(calculatePassedSeconds());
			mPaintTime.getTextBounds(passedTime, 0, passedTime.length(), mRectText);

			canvas.drawText(passedTime,
							(float) (mCenterX * -Math.cos(Math.toRadians(35.0))) + mWidth / 2.0f
							- mRectText.width() / 3.0f,
							(float) (mCenterX * Math.sin(Math.toRadians(35.0))) + mHeight / 2.0f + mRectText.height()
							+ 15.0f, mPaintTime);
		}

		

		mPlayPauseDrawable.draw(canvas);
	}

	
	private Bitmap drawableToBitmap(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		}

		int width = drawable.getIntrinsicWidth();
		width = width > 0 ? width : 1;
		int height = drawable.getIntrinsicHeight();
		height = height > 0 ? height : 1;

		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);

		return bitmap;
	}

	
	private void createShader() {

		if (mWidth == 0) return;

		
		if (mBitmapCover == null) {
			mBitmapCover = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
			mBitmapCover.eraseColor(mCoverColor);
		}

		mCoverScale = ((float) mWidth) / (float) mBitmapCover.getWidth();

		mBitmapCover =
			Bitmap.createScaledBitmap(mBitmapCover, (int) (mBitmapCover.getWidth() * mCoverScale),
									  (int) (mBitmapCover.getHeight() * mCoverScale), true);

		mShader = new BitmapShader(mBitmapCover, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
		mPaintCover = new Paint();
		mPaintCover.setAntiAlias(true);
		mPaintCover.setShader(mShader);
	}

	
	public void updateCoverRotate() {
		mRotateDegrees += VELOCITY;
		mRotateDegrees = mRotateDegrees % 360;
		postInvalidate();
	}

	
	public boolean isRotating() {
		return isRotating;
	}

	
	public void start() {

		isRotating = true;
		mPlayPauseDrawable.setPlaying(isRotating);
		mHandlerRotate.removeCallbacksAndMessages(null);
		mHandlerRotate.postDelayed(mRunnableRotate, ROTATE_DELAY);
		if (isAutoProgress) {
			mHandlerProgress.removeCallbacksAndMessages(null);
			mHandlerProgress.postDelayed(mRunnableProgress, PROGRESS_SECOND_MS);
		}
		postInvalidate();
	}

	
	public void stop() {
		isRotating = false;
		mPlayPauseDrawable.setPlaying(isRotating);
		postInvalidate();
	}

	
	public void setVelocity(int velocity) {
		if (velocity > 0) VELOCITY = velocity;
	}

	
	public void setCoverDrawable(int coverDrawable) {
		Drawable drawable = getContext().getResources().getDrawable(coverDrawable);
		mBitmapCover = drawableToBitmap(drawable);
		createShader();
		postInvalidate();
	}

	
	public void setCoverDrawable(Drawable drawable) {
		mBitmapCover = drawableToBitmap(drawable);
		createShader();
		postInvalidate();
	}

	
	public void setCoverURL(String imageUrl) {
		Picasso.with(getContext()).load(imageUrl).into(target);
	}

	
	private Target target = new Target() {
		@Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
			mBitmapCover = bitmap;
			createShader();
			postInvalidate();
		}

		@Override public void onBitmapFailed(Drawable errorDrawable) {

		}

		@Override public void onPrepareLoad(Drawable placeHolderDrawable) {

		}
	};

	
	@Override public boolean onTouchEvent(MotionEvent event) {

		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
					return true;
				}
			case MotionEvent.ACTION_UP: {
					if (mButtonRegion.contains((int) x, (int) y)) {
						if (onClickListener != null) onClickListener.onClick(this);
					}
				}
				break;

			default: break;
		}

		return super.onTouchEvent(event);
	}

	
	@Override public void setOnClickListener(OnClickListener l) {
		onClickListener = l;
	}

	
	private Bitmap getResizedBitmap(Bitmap bm, float newHeight, float newWidth) {
		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
		return resizedBitmap;
	}

	
	public void setButtonColor(int color) {
		mButtonColor = color;
		mPaintButton.setColor(mButtonColor);
		postInvalidate();
	}

	
	public void setProgressEmptyColor(int color) {
		mProgressEmptyColor = color;
		mPaintProgressEmpty.setColor(mProgressEmptyColor);
		postInvalidate();
	}

	
	public void setProgressLoadedColor(int color) {
		mProgressLoadedColor = color;
		mPaintProgressLoaded.setColor(mProgressLoadedColor);
		postInvalidate();
	}

	
	public void setMax(int maxProgress) {
		this.maxProgress = maxProgress;
		postInvalidate();
	}

	
	public void setProgress(int currentProgress) {
		if (0 <= currentProgress && currentProgress <= maxProgress) {
			this.currentProgress = currentProgress;
			postInvalidate();
		}
	}

	
	public int getProgress() {
		return currentProgress;
	}

	
	private int calculateLeftSeconds() {
		return maxProgress - currentProgress;
	}

	
	private int calculatePassedSeconds() {
		return currentProgress;
	}

	
	private String secondsToTime(int seconds) {
		String time = "";

		String minutesText = String.valueOf(seconds / 60);
		if (minutesText.length() == 1) minutesText = "0" + minutesText;

		String secondsText = String.valueOf(seconds % 60);
		if (secondsText.length() == 1) secondsText = "0" + secondsText;

		time = minutesText + ":" + secondsText;

		return time;
	}

	
	private int calculatePastProgressDegree() {
		return (250 * currentProgress) / maxProgress;
	}

	
	public void setAutoProgress(boolean isAutoProgress) {
		this.isAutoProgress = isAutoProgress;
	}

	
	public void setTimeColor(int color) {
		mTextColor = color;
		mPaintTime.setColor(mTextColor);
		postInvalidate();
	}

	public void setProgressVisibility(boolean mProgressVisibility) {
		this.mProgressVisibility = mProgressVisibility;
		postInvalidate();
	}

	
	Drawable.Callback callback = new Drawable.Callback() {
		@Override public void invalidateDrawable(Drawable who) {
			postInvalidate();
		}

		@Override public void scheduleDrawable(Drawable who, Runnable what, long when) {

		}

		@Override public void unscheduleDrawable(Drawable who, Runnable what) {

		}
	};

	@Override public void onToggled() {
		toggle();
	}

	
	public void toggle() {
		if (mAnimatorSet != null) {
			mAnimatorSet.cancel();
		}

		mAnimatorSet = new AnimatorSet();
		final Animator pausePlayAnim = mPlayPauseDrawable.getPausePlayAnimator();
		mAnimatorSet.setInterpolator(new DecelerateInterpolator());
		mAnimatorSet.setDuration(PLAY_PAUSE_ANIMATION_DURATION);
		mAnimatorSet.playTogether(pausePlayAnim);
		mAnimatorSet.start();
	}
}
