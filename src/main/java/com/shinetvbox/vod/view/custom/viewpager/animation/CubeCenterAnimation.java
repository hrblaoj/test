package com.shinetvbox.vod.view.custom.viewpager.animation;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;

import com.shinetvbox.vod.view.custom.viewpager.animation.AnimationUtils.RotationStateListener;

public class CubeCenterAnimation {
	public static final int DIRECTION_NEXT = 1;
	public static final int DIRECTION_PREV = 2;
	
	//View leftView = null;
	//View mContainer = null;
	int mDuration = 200;
	float mCenterX = 0.0f;
	float mCenterY = 0.0f;
	float mDepthZ = 270.0f;
	int mIndex = 0;
	int mDirection = 0;
	
	View leftView = null;
	View rightView = null;
	//View mStartAnimView = null;
	
	private RotationStateListener mRotationStateListener;
	
	public void setRotationStateListener(RotationStateListener l) {
		this.mRotationStateListener = l;
	}
	
	
	public CubeCenterAnimation(){
	}
	
	public void applyRotation(View left, View right, int direction) {
		rightView = right;
		leftView = left;
		mCenterX = left.getWidth()/2;
		mCenterY = left.getHeight()/2;
		mDirection = direction;
		
		
		
		Rotate3dAnimation rotationLeft;
		Rotate3dAnimation rotationRight;
		if(mDirection == DIRECTION_NEXT){
			rotationLeft = new Rotate3dAnimation(-90, 0,
					left.getWidth(), left.getHeight()/2, -mDepthZ, false);
			rotationRight = new Rotate3dAnimation(0, 100,
					0, left.getHeight()/2, mDepthZ, true);
			rotationLeft.setInterpolator(new DecelerateInterpolator());
			rotationRight.setInterpolator(new AccelerateInterpolator());
		} else {
			rotationLeft = new Rotate3dAnimation(0, -100,
					left.getWidth(), left.getHeight()/2, 90, false);
			rotationRight = new Rotate3dAnimation(90, 0, 0, left.getHeight()/2, 0, true);
			rotationLeft.setInterpolator(new AccelerateInterpolator());
			rotationRight.setInterpolator(new DecelerateInterpolator());
		}
		rotationLeft.setDuration(mDuration);
		rotationRight.setDuration(mDuration);
		//rotation.setFillAfter(true);
		//rotationLeft.setFillEnabled(false);
		//rotationRight.setFillEnabled(false);
		rotationLeft.setFillBefore(true);
		rotationRight.setFillBefore(true);
		
		//rotationLeft.setInterpolator(new AccelerateInterpolator());
		//rotationRight.setInterpolator(new DecelerateInterpolator());
		
/*		if(mDirection == DIRECTION_NEXT){
			rotation.setAnimationListener(mAnimationListener);
		} else if(mDirection == DIRECTION_PREV){
			rotation.setAnimationListener(mAnimationListener);
		}*/
		rotationLeft.setAnimationListener(mAnimationListener);
		rightView.startAnimation(rotationRight);
		leftView.startAnimation(rotationLeft);
	}
	
	Animation.AnimationListener mAnimationListener = new Animation.AnimationListener(){

		@Override
		public void onAnimationEnd(Animation animation) {
			// TODO Auto-generated method stub
			//leftView.setVisibility(View.VISIBLE);
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
