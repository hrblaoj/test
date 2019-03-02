package com.shinetvbox.vod.mycomponents;

import android.media.MediaPlayer;
import android.util.Log;
import android.view.SurfaceHolder;


import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


import static com.shinetvbox.vod.MyApplication.ProcessId;
import static com.shinetvbox.vod.db.KtvDbNative.jni_GetPlayMd5Key;
import static com.shinetvbox.vod.utils.MD5Util.string2MD5;

public class MyMediaPlayer extends MediaPlayer {
	private static final String TAG = MyMediaPlayer.class.getSimpleName();
	
	public static final int STATE_END = -1;
	public static final int STATE_ERROR = 0;
	public static final int STATE_IDLE = 1;
	public static final int STATE_INITIALIZED = 2;
	public static final int STATE_PREPARING = 3;
	public static final int STATE_PREPARED = 4;
	public static final int STATE_STARTED = 5;
	public static final int STATE_PAUSED = 6;
	public static final int STATE_STOPPED = 7;
	public static final int STATE_COMPLETE = 8;
	
	public int mState = STATE_END;
	private OnPreparedListener mOnPreparedListener, mOnPreparedListener2;
	private OnErrorListener mErrorListener, mErrorListener2;
	private OnCompletionListener mCompletionListener, mCompletionListener2;
	private TrackInfo[] mTrackInfo;
	
	public MyMediaPlayer(){
		super();
		mOnPreparedListener2 = new OnPreparedListener() {
			
			@Override
			public void onPrepared(MediaPlayer mp) {
				mState = STATE_PREPARED;
				if(mOnPreparedListener != null) {
					mOnPreparedListener.onPrepared(mp);
				}
				mTrackInfo = null;
				try {
				mTrackInfo = MyMediaPlayer.this.getTrackInfo();
				} catch (Exception e) {
					e.printStackTrace();
					mTrackInfo = null;
				}
			}
		};
		super.setOnPreparedListener(mOnPreparedListener2);
		mErrorListener2 = new OnErrorListener() {
			
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				mState = STATE_ERROR;
				if(mErrorListener != null) {
					mErrorListener.onError(mp, what, extra);
				}
				return false;
			}
		};
		super.setOnErrorListener(mErrorListener2);
		mCompletionListener2 = new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				mState = STATE_COMPLETE;
				if(mCompletionListener != null) {
					mCompletionListener.onCompletion(mp);
				}
			}
		};
		super.setOnCompletionListener(mCompletionListener2);
		mState = STATE_IDLE;
		mTrackInfo = null;
	}
	
	@Override
	public void setOnPreparedListener(OnPreparedListener listener) {
		mOnPreparedListener = listener;
	}
	
	@Override
	public void setOnErrorListener(OnErrorListener listener) {
		mErrorListener = listener;
	}
	
	@Override
	public void setOnCompletionListener(OnCompletionListener listener) {
		mCompletionListener = listener;
	}
	
	@Override
	public void reset() {
		if(mState == STATE_END) {
			dumpStateErr("reset");
			return;
		}
		
		try {
			super.reset();
			mState = STATE_IDLE;
			mTrackInfo = null;
		} catch (Exception e) {
			e.printStackTrace();
			mState = STATE_IDLE;
		}
	}

	/*
	*  path:http请求地址，请求地址的组成由ip + 端口（一开始创建阳光http服务的端口） + /5（http服务需要用这个数字来判断是本地歌曲，所以必须加上）
	*  								+ 歌曲路径（歌曲名称要保证在15位之内不然c层会有越界风险，阳光的歌曲都是6位id，在请求地址中可以不添加后缀名，但歌曲的后缀名必须命名为mpg或MPG）,
	*  path = "http://127.0.0.1:" + port + "/5" + "/sdcard/000000.mpg" 或 path = "http://127.0.0.1:" + port + "/5" + "/sdcard/000000"
	* */
	@Override
	public void setDataSource(String path) throws IOException,
            IllegalArgumentException, SecurityException, IllegalStateException {
		if(mState != STATE_IDLE) {
			dumpStateErr("setDataSource");
			throw new IllegalStateException("current state="+mState);
		}
		
		try {
			//super.setDataSource(path);

			Map<String, String> headers = new HashMap<String, String>();

			/* System.nanoTime() 当前进程id，该进程id一定要和阳光http服务所在的进程id相同 */
			headers.put("shinekey1", ProcessId);
			/* System.nanoTime() 当前时间 */
			String keytmp =  String.valueOf(System.nanoTime());
			headers.put("shinekey2", keytmp);
			/* 用进程id，时间和指定字符串组成一个小写的md5字串 */
			String md5tmp = ProcessId+keytmp+"shine667788gkfjhdsegsagsh";
			String realmd = string2MD5(md5tmp);
			headers.put("shinekey3", realmd);

			Method method = this.getClass().getMethod("setDataSource", new Class[] { String.class, Map.class });
			method.invoke(this, new Object[] {path, headers});
			mState = STATE_INITIALIZED;
//			super.setDataSource(path);

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			mState = STATE_ERROR;
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			mState = STATE_ERROR;
			throw e;
		} catch (IllegalStateException e) {
			e.printStackTrace();
			mState = STATE_ERROR;
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			mState = STATE_ERROR;
			throw new RuntimeException();
		}
	}

	public void setDataSourceLocal(String path) throws IOException,
			IllegalArgumentException, SecurityException, IllegalStateException {
		if(mState != STATE_IDLE) {
			dumpStateErr("setDataSource");
			throw new IllegalStateException("current state="+mState);
		}

		try {
			//super.setDataSource(path);

//			Map<String, String> headers = new HashMap<String, String>();
//
//			/* System.nanoTime() 当前进程id，该进程id一定要和阳光http服务所在的进程id相同 */
//			headers.put("shinekey1", ProcessId);
//			/* System.nanoTime() 当前时间 */
//			String keytmp =  String.valueOf(System.nanoTime());
//			headers.put("shinekey2", keytmp);
//			/* 用进程id，时间和指定字符串组成一个小写的md5字串 */
//			String md5tmp = ProcessId+keytmp+"shine667788gkfjhdsegsagsh";
//			String realmd = string2MD5(md5tmp);
//			headers.put("shinekey3", realmd);
//
//			Method method = this.getClass().getMethod("setDataSource", new Class[] { String.class, Map.class });
//			method.invoke(this, new Object[] {path, headers});
//			mState = STATE_INITIALIZED;
			super.setDataSource(path);

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			mState = STATE_ERROR;
			throw e;
		} catch (SecurityException e) {
			e.printStackTrace();
			mState = STATE_ERROR;
			throw e;
		} catch (IllegalStateException e) {
			e.printStackTrace();
			mState = STATE_ERROR;
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			mState = STATE_ERROR;
			throw new RuntimeException();
		}
	}
	@Override
	public void prepare() throws IOException, IllegalStateException {
		if(mState != STATE_INITIALIZED && mState != STATE_STOPPED) {
			dumpStateErr("prepare");
			throw new IllegalStateException("current state="+mState);
		}
		
		try {
			super.prepare();
			mState = STATE_PREPARED;
		} catch (IllegalStateException e) {
			e.printStackTrace();
			mState = STATE_ERROR;
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			mState = STATE_ERROR;
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			mState = STATE_ERROR;
			throw new RuntimeException();
		}
	}
	
	@Override
	public void prepareAsync() throws IllegalStateException {
		if(mState != STATE_INITIALIZED && mState != STATE_STOPPED) {
			dumpStateErr("prepareAsync");
			throw new IllegalStateException("current state="+mState);
		}
		
		try {
			mState = STATE_PREPARING;
			super.prepareAsync();
		} catch (IllegalStateException e) {
			e.printStackTrace();
			mState = STATE_ERROR;
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			mState = STATE_ERROR;
			throw new RuntimeException();
		}
	}
	
	@Override
	public void start() throws IllegalStateException {
		if(mState != STATE_PREPARED && mState != STATE_STARTED &&
				mState != STATE_PAUSED && mState != STATE_COMPLETE) {
			dumpStateErr("start");
			return;
//			throw new IllegalStateException("current state="+mState);
		}
		
		try {
			super.start();
			mState = STATE_STARTED;
		} catch (IllegalStateException e) {
			e.printStackTrace();
			mState = STATE_ERROR;
//			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			mState = STATE_ERROR;
//			throw new RuntimeException();
		}
	}
	
	@Override
	public void stop() throws IllegalStateException {
		if(mState != STATE_PREPARED && mState != STATE_STARTED &&
				mState != STATE_STOPPED && mState != STATE_PAUSED && mState != STATE_COMPLETE) {
			dumpStateErr("stop");
			return;
//			throw new IllegalStateException("current state="+mState);
		}
		
		try {
			super.stop();
			mState = STATE_STOPPED;
		} catch (IllegalStateException e) {
			e.printStackTrace();
			mState = STATE_ERROR;
//			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			mState = STATE_ERROR;
//			throw new RuntimeException();
		}
	}
	
	@Override
	public void pause() throws IllegalStateException {
		if(mState != STATE_STARTED && mState != STATE_PAUSED && mState != STATE_COMPLETE) {
			dumpStateErr("pause");
			throw new IllegalStateException("current state="+mState);
		}
		
		try {
			super.pause();
			mState = STATE_PAUSED;
		} catch (IllegalStateException e) {
			e.printStackTrace();
			mState = STATE_ERROR;
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			mState = STATE_ERROR;
			throw new RuntimeException();
		}
	}
	
	@Override
	public void seekTo(int msec) throws IllegalStateException {
		if(mState != STATE_PREPARED && mState != STATE_STARTED && mState != STATE_PAUSED && mState != STATE_COMPLETE) {
			dumpStateErr("seekTo");
			return;
//			throw new IllegalStateException("current state="+mState);
		}
		
		try {
			super.seekTo(msec);
		} catch (IllegalStateException e) {
			e.printStackTrace();
			mState = STATE_ERROR;
//			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			mState = STATE_ERROR;
//			throw new RuntimeException();
		}
	}
	
	@Override
	public TrackInfo[] getTrackInfo() throws IllegalStateException {
		if(mState != STATE_PREPARED && mState != STATE_STARTED &&
				mState != STATE_STOPPED && mState != STATE_PAUSED && mState != STATE_COMPLETE) {
			dumpStateErr("getTrackInfo");
			throw new IllegalStateException("current state="+mState);
		}
		if(mTrackInfo != null) {
			return mTrackInfo;
		}
		return super.getTrackInfo();
	}
	@Override
	public void selectTrack(int index) throws IllegalStateException {
		if(mState != STATE_PREPARED && mState != STATE_STARTED &&
				mState != STATE_STOPPED && mState != STATE_PAUSED && mState != STATE_COMPLETE) {
			dumpStateErr("getTrackInfo");
			throw new IllegalStateException("current state="+mState);
		}
		
		super.selectTrack(index);
	}
	
	@Override
	public void release() {
		super.release();
		mOnPreparedListener = null;
		mErrorListener = null;
		mCompletionListener = null;
		mTrackInfo = null;
		mState = STATE_END;
	}
	
	@Override
	public int getCurrentPosition() {
		if(mState == STATE_ERROR || mState == STATE_END) {
			dumpStateErr("getCurrentPosition");
			return 0;
		}
		try {
			return super.getCurrentPosition();
		} catch (Exception e) {
			e.printStackTrace();
			mState = STATE_ERROR;
		}
		return 0;
	}
	
	@Override
	public int getDuration() {
		if(mState != STATE_PREPARED && mState != STATE_STARTED &&
				mState != STATE_STOPPED && mState != STATE_PAUSED && mState != STATE_COMPLETE) {
			dumpStateErr("stop");
			return 0;
		}
		try {
			return super.getDuration();
		} catch (Exception e) {
			e.printStackTrace();
			mState = STATE_ERROR;
		}
		return 0;
	}
	
	@Override
	public int getVideoHeight() {
		if(mState == STATE_ERROR || mState == STATE_END) {
			dumpStateErr("getVideoHeight");
			return 0;
		}
		try {
			return super.getVideoHeight();
		} catch (Exception e) {
			e.printStackTrace();
			mState = STATE_ERROR;
		}
		return 0;
	}
	
	@Override
	public int getVideoWidth() {
		if(mState == STATE_ERROR || mState == STATE_END) {
			dumpStateErr("getVideoWidth");
			return 0;
		}
		try {
			return super.getVideoWidth();
		} catch (Exception e) {
			e.printStackTrace();
			mState = STATE_ERROR;
		}
		return 0;
	}
	
	@Override
	public boolean isPlaying() {
		if(mState == STATE_ERROR || mState == STATE_END) {
			dumpStateErr("isPlaying");
			return false;
		}
		if(mState == STATE_PAUSED){
			return true;
		}
		try {
			return super.isPlaying();
		} catch (Exception e) {
			e.printStackTrace();
			mState = STATE_ERROR;
		}
		return false;
	}
	

	


	private void dumpStateErr(String fun) {
		loge(fun + " error, mCurrentState="+mState);
	}
	
	private void loge(String str){
		Log.d(TAG, str);
	}
}
