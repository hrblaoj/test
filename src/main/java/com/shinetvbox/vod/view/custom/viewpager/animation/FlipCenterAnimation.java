package com.shinetvbox.vod.view.custom.viewpager.animation;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import com.shinetvbox.vod.view.custom.viewpager.animation.AnimationUtils.RotationStateListener;

public class FlipCenterAnimation {
	public static final int DIRECTION_NEXT = 1;
	public static final int DIRECTION_PREV = 2;
	
	//View leftView = null;
	//View mContainer = null;
	int mDuration = 80;
	float mCenterX = 0.0f;
	float mCenterY = 0.0f;
	float mDepthZ = 180.0f;
	int mIndex = 0;
	int mDirection = 0;
	
	View leftView = null;
	View rightView = null;
	View mStartAnimView = null;
	
	private RotationStateListener mRotationStateListener;
	
	public void setRotationStateListener(RotationStateListener l) {
		this.mRotationStateListener = l;
	}
	
	
	public FlipCenterAnimation(){
	}
	
	public void applyRotation(View left, View right, int direction) {
		rightView = right;
		leftView = left;
		mCenterX = left.getWidth()/2;
		mCenterY = left.getHeight()/2;
		mStartAnimView = leftView;
		mDirection = direction;
		
		rightView.setVisibility(View.GONE);
		
		Rotate3dAnimation rotation;
		if(mDirection == DIRECTION_NEXT){
			rotation = new Rotate3dAnimation(0, -90,
					mCenterX, mCenterY, mDepthZ, true);
		} else {
			rotation = new Rotate3dAnimation(0, 90,
					mCenterX, mCenterY, mDepthZ, true);
		}
		rotation.setDuration(mDuration);
		//rotation.setFillAfter(true);
		rotation.setFillBefore(true);
		rotation.setInterpolator(new AccelerateInterpolator());
		if(mDirection == DIRECTION_NEXT){
			rotation.setAnimationListener(new DisplayNextView());
			//mStartAnimView.postDelayed(new DisplayNextView().new SwapViews(), mDuration);
		} else if(mDirection == DIRECTION_PREV){
			rotation.setAnimationListener(new DisplayLastView());
			//mStartAnimView.postDelayed(new DisplayLastView().new SwapViews(), mDuration);
		}
		mStartAnimView.startAnimation(rotation);
	}
	
	
/*	public void applyRotation(View v, int direction) {
		leftView = v;
		mCenterX = v.getWidth()/2;
		mCenterY = v.getHeight()/2;
		mStartAnimView = rightView;
		
		mDirection = direction;
		float centerX = mCenterX;
		float centerY = mCenterY;
		Rotate3dAnimation rotation;
		if(mDirection == DIRECTION_NEXT){
			rotation = new Rotate3dAnimation(0, -90,
					centerX, centerY, mDepthZ, true);
		} else {
			rotation = new Rotate3dAnimation(0, 90,
					centerX, centerY, mDepthZ, true);
		}
		rotation.setDuration(mDuration);
		rotation.setFillAfter(true);
		rotation.setInterpolator(new AccelerateInterpolator());
		if(mDirection == DIRECTION_NEXT){
			rotation.setAnimationListener(new DisplayNextView());
		} else if(mDirection == DIRECTION_PREV){
			rotation.setAnimationListener(new DisplayLastView());
		}
		leftView.startAnimation(rotation);
	}
	public void applyRotation(View animView) {
		float centerX = mCenterX;
		float centerY = mCenterY;
		Rotate3dAnimation rotation = new Rotate3dAnimation(0, 90,
				centerX, centerY, mDepthZ, true);
		rotation.setDuration(mDuration);
		rotation.setFillAfter(true);
		rotation.setInterpolator(new AccelerateInterpolator());
		rotation.setAnimationListener(new DisplayNextView());
		animView.startAnimation(rotation);
	}*/
	
	/**
	 * This class listens for the end of the first half of the animation. It
	 * then posts a new action that effectively swaps the views when the
	 * container is rotated 90 degrees and thus invisible.
	 */
	private final class DisplayLastView implements Animation.AnimationListener {
		public void onAnimationStart(Animation animation) {
		}

		public void onAnimationEnd(Animation animation) {
			mStartAnimView.post(new SwapViews());
		}

		public void onAnimationRepeat(Animation animation) {
		}

		public final class SwapViews implements Runnable {
			@Override
			public void run() {
				leftView.setVisibility(View.GONE);
				//rightView.setVisibility(View.GONE);
				//mIndex++;
				//if (0 == mIndex % 2) {
				//	mStartAnimView = leftView;
				//} else {
					mStartAnimView = rightView;
				//}
				mStartAnimView.setVisibility(View.VISIBLE);
				mStartAnimView.requestFocus();
				Rotate3dAnimation rotation = new Rotate3dAnimation(-90, 0,
						mCenterX, mCenterY, mDepthZ, false);
				rotation.setDuration(mDuration);
				//rotation.setFillAfter(true);
				rotation.setFillBefore(true);
				rotation.setInterpolator(new DecelerateInterpolator());
				rotation.setAnimationListener(mAnimationListener);
				mStartAnimView.startAnimation(rotation);
			}
		}
	}
	
	private final class DisplayNextView implements Animation.AnimationListener {
		public void onAnimationStart(Animation animation) {
		}

		public void onAnimationEnd(Animation animation) {
			mStartAnimView.post(new SwapViews());
		}

		public void onAnimationRepeat(Animation animation) {
		}

		private final class SwapViews implements Runnable {
			@Override
			public void run() {
				leftView.setVisibility(View.GONE);
				//rightView.setVisibility(View.GONE);
				//mIndex++;
				//if (0 == mIndex % 2) {
				//	mStartAnimView = leftView;
				//} else {
					mStartAnimView = rightView;
				//}
				mStartAnimView.setVisibility(View.VISIBLE);
				Rotate3dAnimation rotation = new Rotate3dAnimation(90, 0,
						mCenterX, mCenterY, mDepthZ, false);
				rotation.setDuration(mDuration);
				//rotation.setFillAfter(true);
				rotation.setFillBefore(true);
				rotation.setInterpolator(new DecelerateInterpolator());
				rotation.setAnimationListener(mAnimationListener);
				mStartAnimView.startAnimation(rotation);
			}
		}
	}
	
	Animation.AnimationListener mAnimationListener = new Animation.AnimationListener(){

		@Override
		public void onAnimationEnd(Animation animation) {
			// TODO Auto-generated method stub
			leftView.setVisibility(View.VISIBLE);
			//rightView.setVisibility(View.VISIBLE);
			if(mRotationStateListener!=null) {
				mRotationStateListener.onAnimationFinish(mDirection);
			}
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub
			
		}};
}
