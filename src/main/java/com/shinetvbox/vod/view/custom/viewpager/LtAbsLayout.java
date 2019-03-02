package com.shinetvbox.vod.view.custom.viewpager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.TextView;

/*import com.lstech.ktv.skin.SkinImageLoader;
import com.lstech.ktv.ui.widget.LtImageView;
import com.lstech.ktv.ui.widget.LtTextView;*/

@SuppressWarnings("deprecation")
public class LtAbsLayout extends AbsoluteLayout implements
		IRecreatableDisplayResource {
	protected boolean bNeedCreateDisplayResource = false;
	protected boolean mbSaveImageCache = false;
	protected String mstrImageName;

	public LtAbsLayout(Context paramContext) {
		super(paramContext);
	}

	public LtAbsLayout(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
	}

	public LtAbsLayout(Context paramContext, AttributeSet paramAttributeSet,
                       int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
	}

	public View getChildByID(ViewGroup viewGroup, int paramInt) {
		View localView = null;
		if (viewGroup == null)
			return null;
		int count = viewGroup.getChildCount();
		for (int i = 0; i < count; i++){
			localView = viewGroup.getChildAt(i);
			if (localView != null && localView.getId() == paramInt) {
				return localView;
			}
		}
		return localView;
	}

	public View getChildByID(int paramInt) {
		View localView = null;
		int c = getChildCount();
		if (c <= 0)
			return localView;
		for (int j = 0; j < c; j++) {
			localView = getChildAt(j);
			if (localView != null && localView.getId() == paramInt) {
				return localView;
			}
		}
		return null;
	}

	@SuppressLint("NewApi")
	public void onCloseDisplayRelease() {
		if (getBackground() != null) {
			setBackground(null);
		}
		//this.setb
		bNeedCreateDisplayResource = true;
		int count = getChildCount();
		if (count <= 0)
			return;
		View localView = null;
		
		for (int i = 0; i < count; i++) {
			localView = getChildAt(i);
			if ((localView instanceof IRecreatableDisplayResource))
				((IRecreatableDisplayResource) localView)
						.onCloseDisplayRelease();
		}
	}

	@SuppressLint("NewApi")
	protected void onCloseDisplayReleaseBackGroundText() {
		if (getBackground() != null)
			setBackground(null);
		bNeedCreateDisplayResource = true;
		int count = getChildCount();
		if (count <= 0)
			return;
		View localView;
		for (int i = 0; i < count; i++) {
			localView = getChildAt(0);
			if (localView instanceof LtAbsLayout)
				((LtAbsLayout) localView).onCloseDisplayReleaseBackGroundText();
			if (localView instanceof TextView)
				((IRecreatableDisplayResource) localView)
						.onCloseDisplayRelease();
		}
	}

	protected void onCloseDisplayReleaseImageText() {
		bNeedCreateDisplayResource = true;
		int count = getChildCount();
		if (count <= 0)
			return;
		View localView;
		for (int i = 0; i < count; i++) {
			localView = getChildAt(i);
			if ((localView instanceof LtAbsLayout))
				((LtAbsLayout) localView).onCloseDisplayReleaseImageText();
			if (localView instanceof TextView
					|| localView instanceof ImageView)
				((IRecreatableDisplayResource) localView)
						.onCloseDisplayRelease();
		}
	}

	public void onOpenDisplayCreate() {
		System.out.println("LtABsLayout------->onOpendispalycreate");
		if (bNeedCreateDisplayResource) {
/*			if ((mstrImageName != null) && (mstrImageName.length() > 0)
					&& (getBackground() == null))
				setBackground(SkinImageLoader.loadDrawable(getResources(),
						mstrImageName, mbSaveImageCache));
			bNeedCreateDisplayResource = false;*/
		}
		int count = getChildCount();
		if (count <= 0)
			return;
		View localView;
		//System.out.println("LtabsLayout-------> " + getChildCount());
		for (int i = 0; i < count; i++) {
			localView = getChildAt(i);
			if (localView instanceof IRecreatableDisplayResource)
				((IRecreatableDisplayResource) localView).onOpenDisplayCreate();
		}
	}

	public void setImageName(String paramString, boolean paramBoolean) {
		mstrImageName = paramString;
		mbSaveImageCache = paramBoolean;
	}

	public static class LtLayoutParams extends LayoutParams {
		public LtLayoutParams(int width, int height, int x,
				int y) {
			super(width, height, x, y);
		}

		public void setParam(int paramInt1, int paramInt2, int paramInt3,
				int paramInt4) {
			x = paramInt1;
			y = paramInt2;
			width = paramInt3;
			height = paramInt4;
		}

		public void setSize(int paramInt1, int paramInt2) {
			width = paramInt1;
			height = paramInt2;
		}

		public void setXY(int paramInt1, int paramInt2) {
			x = paramInt1;
			y = paramInt2;
		}
		
		public int getX()  {
			return x;
		}
		
		public int getY()  {
			return y;
		}
	}
}