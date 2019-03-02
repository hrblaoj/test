package com.shinetvbox.vod.view.custom.viewpager.animation;

import android.view.View;

public class AnimationUtils {
	public static final int DIRECTION_NEXT = 1;
	public static final int DIRECTION_PREV = 2;
	
	FlipCenterAnimation mFlipCenterAnimation;
	CubeCenterAnimation mCubeCenterAnimation;
	
	public interface RotationStateListener{
		void onAnimationFinish(int direction);
	}
	
	public void setRotationStateListener(RotationStateListener l) {
		mFlipCenterAnimation.setRotationStateListener(l);
		mCubeCenterAnimation.setRotationStateListener(l);
	}
	
	
	public AnimationUtils(){	
		mFlipCenterAnimation = new FlipCenterAnimation();
		mCubeCenterAnimation = new CubeCenterAnimation();
	}
	
	public void applyRotation(View left, View right, int direction) {
		mFlipCenterAnimation.applyRotation(left, right, direction);
	}
	
	public void applyCenterCube(View left, View right, int direction){
		mCubeCenterAnimation.applyRotation(left, right, direction);
	}
}
