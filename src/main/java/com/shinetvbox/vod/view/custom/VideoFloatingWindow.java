package com.shinetvbox.vod.view.custom;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.VideoView;

public abstract class VideoFloatingWindow {
	private static final String TAG = "VideoFloatingWindow";
	final static int SCREEN_FULL = 0;// 全屏状态
	final static int SCREEN_DEFAULT = 1;// 默认状态
	final static int SCREEN_HIDE = 2;// 隐藏
	private boolean isFull = false;//是否是全屏
	private int mScreenStatus = SCREEN_DEFAULT;
	private boolean isHideVideo = true;//当全屏点击时判断是否隐藏或缩小窗口
	// 默认状态下视频的位置
//	public static int DEFAULT_ZOOM_STATE_X = 801;
//	public static int DEFAULT_ZOOM_STATE_Y = 88;
//	// 默认状态下悬浮窗的大小
//	public static int DEFAULT_ZOOM_STATE_WIDTH = 466;
//	public static int DEFAULT_ZOOM_STATE_HEIGHT = 211;
//	// 默认状态下视频的大小
//	public static int DEFAULT_VIDEO_WIDTH = 466;
//	public static int DEFAULT_VIDEO_HEIGHT = 211;
//	public static int DEFAULT_VIDEO_PADDING_TOP = 0;

	private static final int hideW = 4;
	private static final int hideH = 4;
	public static int DEFAULT_ZOOM_STATE_X = 794;
	public static int DEFAULT_ZOOM_STATE_Y = 78;
	// 默认状态下悬浮窗的大小
	public static int DEFAULT_ZOOM_STATE_WIDTH = 486;
	public static int DEFAULT_ZOOM_STATE_HEIGHT = 227;
	// 默认状态下视频的大小
	public static int DEFAULT_VIDEO_WIDTH = 466; // 466
	public static int DEFAULT_VIDEO_HEIGHT = 211; //211
	public static int DEFAULT_VIDEO_PADDING_TOP = 7;
	public static int DEFAULT_VIDEO_PADDING_LEFT = 7;
	// 默认状态下视频透明度
	public static float DEFAULT_ALPHA = 1f;


	private int mScreenWidth = 1280;
	private int mScreenHeight = 720;

	private Context mContext;
	WindowManager wm = null;
	RelativeLayout floatingViewObj = null;
	private VideoView mVideoView;

	public VideoFloatingWindow(Context context, RelativeLayout view) {
		mContext = context;
		this.floatingViewObj = view;
		////////////////////////////////////////////////////////////////////////////////////////////////////////////
//		mVideoView = (VideoView) view.findViewById(R.id.video_float_view);
		mVideoView = new VideoView( context );
		Point outsize = new Point(  );
		wm = (WindowManager) mContext.getSystemService(Activity.WINDOW_SERVICE);
		wm.getDefaultDisplay().getSize( outsize );
		mScreenWidth = outsize.x;
		mScreenHeight = outsize.y;
		init();
	}

	/**设置窗体大小*/
	public abstract void setFloatingWindowSize(int x,int y, int width, int height);

	private void init() {
		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
/*		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
				| WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;*/
		params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
				| LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_FULLSCREEN;
		params.type = WindowManager.LayoutParams.TYPE_TOAST;

		params.alpha = 1;
		// 设定内部文字对齐方式
		params.gravity = Gravity.LEFT | Gravity.TOP;
		params.height = 0;
		params.width = 0;
		params.x = 0;//DEFAULT_ZOOM_STATE_X
		params.y = 720;//DEFAULT_ZOOM_STATE_Y

//		params.height = 360;
//		params.width = 640;
//		params.x = 640;
//		params.y = 360;
		wm.addView(floatingViewObj, params);
		boolean is =  mVideoView.getHolder().getSurface().isValid();
	}


	public void update() {

		Log.d("JG", "VideoFloatingWindowVideoFloatingWindowupdate");
		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
				| LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_FULLSCREEN;
		//params.type = LayoutParams.TYPE_BASE_APPLICATION;
		params.alpha = 1;
		// 设定内部文字对齐方式
		params.gravity = Gravity.LEFT | Gravity.TOP;
		params.height = DEFAULT_ZOOM_STATE_HEIGHT;
		params.width = DEFAULT_ZOOM_STATE_WIDTH;
		params.x = DEFAULT_ZOOM_STATE_X;
		params.y = DEFAULT_ZOOM_STATE_Y;
		wm.updateViewLayout(floatingViewObj, params);

		RelativeLayout.LayoutParams layoutParams;
		if(null != mVideoView) {
			layoutParams = (RelativeLayout.LayoutParams) mVideoView.getLayoutParams();
			layoutParams.topMargin = DEFAULT_VIDEO_PADDING_TOP;//视频videoView top
			mVideoView.setLayoutParams(layoutParams);
		}


	}

	public boolean onTouch(View view, MotionEvent motionEvent) {
		//收到隐藏，那么移除这个自动隐藏handler
//		vidoeRemoveHandler();
		Log.d("peter", "onTouch action+" + motionEvent.getAction()+"  mScreenStatus="+mScreenStatus+ "  全屏isFull="+isFull);
		if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
			if(isFull){
//				if (mLocalZoomState) {
//					//显示控制
//					showTools();
//				} else {
//					//隐藏控制
//					hideTools();
//				}

				setVideoScale(SCREEN_DEFAULT);
				isFull = false;
			}else{
				setVideoScale(SCREEN_FULL);
				isFull = true;
			}

		}
		return false;
	}


	/**直接隐藏菜单*/
	public void hideMenu(){
		isFull = false;
//		MainActivity.mMainActivityView.fragmentScreenControl.hide();
//		MainActivity.mMainActivityView.videobg.setVisibility(View.VISIBLE);
	}

	/**直接显示菜单*/
	public void showMenu(){
		isFull = true;
//		MainActivity.mMainActivityView.fragmentScreenControl.show();
//		MainActivity.mMainActivityView.videobg.setVisibility(View.GONE);
	}

	/**是全屏状态*/
	public boolean videoIsFullStatus(){
		if(mScreenStatus == SCREEN_FULL){
			return true;
		}
		return false;
	}

	/**是否是隐藏状态*/
	public boolean videoIsHideStatus(){
		if(mScreenStatus == SCREEN_HIDE){
			return true;
		}
		return false;
	}

	/**
	 * 改变视频窗体大小
	 * @param localZoomState 0全频 ，1默认，2隐藏
	 */
	void setVideoScale(int localZoomState) {

//		if(true)
//			return;
		//LogUtil.i("改变视频窗大小 发来的值："+localZoomState+ "  当前值："+mScreenStatus);
//		if(mScreenStatus == localZoomState){
////			LogUtil.i("改变视频窗大小-重复设置则return");
//			return;
//		}

		WindowManager.LayoutParams params = (LayoutParams) floatingViewObj.getLayoutParams();
//		if(mScreenStatus == SCREEN_FULL && localZoomState != SCREEN_FULL){
//			MyApplication.downDialog();
//		} else {
//			MyApplication.upDialog();
//		}
		if(null == mVideoView)
			return;

		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mVideoView.getLayoutParams();

		//当前页面没有视频窗口时点击全屏视频时直接隐藏
		if(isHideVideo && localZoomState == SCREEN_DEFAULT && mScreenStatus == SCREEN_FULL) {
			localZoomState = SCREEN_HIDE;
		}
		switch (localZoomState) {
		case SCREEN_FULL:
			mScreenStatus = SCREEN_FULL;
			params.width = mScreenWidth;
			params.height = mScreenHeight;
			params.alpha = 1;
			params.x = 0;
			params.y = 0;
			wm.updateViewLayout(floatingViewObj, params);
			floatingViewObj.requestLayout();
			
			layoutParams.topMargin=0;//视频videoView top
			layoutParams.leftMargin = 0;
			////////////////////////////////////////////////////
			mVideoView.setLayoutParams(layoutParams);
			////////////////////////////////////////////////////////////////////////////////////////////////////////////
//			mVideoView.setVideoScale(mScreenWidth, mScreenHeight);
			mVideoView.getHolder().setFixedSize(mScreenWidth, mScreenHeight);

			showMenu();
			changeVideoFloatFrameHide();
			break;
		case SCREEN_DEFAULT:
			isHideVideo = false;
			mScreenStatus = SCREEN_DEFAULT;
			params.width = DEFAULT_ZOOM_STATE_WIDTH;
			params.height = DEFAULT_ZOOM_STATE_HEIGHT;
			params.alpha = DEFAULT_ALPHA;
			params.x = DEFAULT_ZOOM_STATE_X;
			params.y = DEFAULT_ZOOM_STATE_Y;

			wm.updateViewLayout(floatingViewObj, params);
			floatingViewObj.requestLayout();

			layoutParams.topMargin=DEFAULT_VIDEO_PADDING_TOP;//视频videoView top
            layoutParams.leftMargin = DEFAULT_VIDEO_PADDING_LEFT;
            //////////////////////////////////////////////////
			mVideoView.setLayoutParams(layoutParams);
			////////////////////////////////////////////////////////////////////////////////////
//			mVideoView.setVideoScale(DEFAULT_VIDEO_WIDTH, DEFAULT_VIDEO_HEIGHT);
			/* 分辨率相关 */
			mVideoView.getHolder().setFixedSize(DEFAULT_VIDEO_WIDTH, DEFAULT_VIDEO_HEIGHT);

			hideMenu();
			changeVideoFloatFrameShow();
			break;
		case SCREEN_HIDE:
			isHideVideo = true;
			mScreenStatus = SCREEN_HIDE;
//			params.x = 0;
//			params.y = 720;
			params.height = hideH;
			params.width = hideW;
			params.alpha = 0f;
			wm.updateViewLayout(floatingViewObj, params);
			floatingViewObj.requestLayout();
			layoutParams.topMargin=DEFAULT_VIDEO_PADDING_TOP;//视频videoView top
			layoutParams.leftMargin = DEFAULT_VIDEO_PADDING_LEFT;
			mVideoView.setLayoutParams(layoutParams);
			/////////////////////////////////////////////////////////////////////////////////////////////////
//			mVideoView.setVideoScale(hideW, hideH);
			mVideoView.getHolder().setFixedSize(hideW, hideH);
			hideMenu();
			break;
		}
	}

	/**
	 * 隐藏视频窗边框图片
	 */
	abstract void changeVideoFloatFrameHide();
	/**
	 * 显示视频窗边框图片
	 */
	abstract void changeVideoFloatFrameShow();
	
	/**设置视频窗默认大小*/
	public abstract void setVideoScaleDefault();
	
	public void setVideoScaleHide(){
		setVideoScale(SCREEN_HIDE);
	}
	
	public void setVideoScaleFull(){
		setVideoScale(SCREEN_FULL);
		isFull = true;
	}
	
	public void destory(){
		closeWindow();
	}

	private void closeWindow() {
		if (floatingViewObj != null && floatingViewObj.isShown()) {
			Log.d("peter", "remove window");
			wm.removeViewImmediate(floatingViewObj);
			// wm.removeView(floatingViewObj);
		}
	}
}
