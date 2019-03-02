package com.shinetvbox.vod.view.custom.viewpager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.shinetvbox.vod.data.constants.Constants;
import com.shinetvbox.vod.view.custom.viewpager.animation.AnimationUtils;

import java.lang.ref.WeakReference;


public class LtViewPager extends LtAbsLayout implements IStepDispaly {
    // 打印标志
    private static final String LOG_TAG = "Shine LtViewPager";
    private static final boolean DBG = false;
    //动画
    private AnimationUtils mAnimationUtils = new AnimationUtils();
    private boolean isAnimationStart = false;
    private boolean isAnimationEnable = true;
    public boolean hitTimerProtect = false;
    // 适配器
    protected Adapter mAdapter;
    // 页面变动监听  滑动翻页完成 当前页数变化 总页数变化
    private OnPageChangeListener mOnPageChangeListener;

    // 当前页
    private PageScrollItem mCurrentPageItem = new PageScrollItem();
    // 下一页
    private PageScrollItem mNextPageItem = new PageScrollItem();
    // 上一页
    private PageScrollItem mPrevPageItem = new PageScrollItem();
    // 滑动到上一页
    public boolean mbScrollToPrevPage;
    // 滑动到下一页
    public boolean mbScrollToNextPage;

    // 最后一次刷新页面时的总页数 刷新时对比是否变化，是则调用监听器事件.
    private int miLastRefreshListPageCount;

    // 当前偏移位置
    private int miCurrentOffsetX;
    private int miCurrentOffsetY;
    // 目标偏移位置
    private int miDestinationOffsetX;
    private int miDestinationOffsetY;
    // 暂停坐标位置(当移动到此坐标时暂停移动)
    private int miPauseOffsetX;
    private int miPauseOffsetY;


    // 每次移动偏移值
    private static final int SCROLL_STEP = 128;
    private int miScrollStep = SCROLL_STEP;
    // 每次间隔时间(MS)
    private static final int SCROLL_STEP_TIME = 0; //50;
    // 滚动多少数
    private static final int SCROLL_STEP_COUNT = 1;

    // 正在移动
    private boolean mbScrolling;

    // 用户按下标志
    private boolean mbMotionDown;

    // 用户按下的坐标
    private int miMotionDownX;
    private int miMotionDownY;
    // 开始拖动的距离
    private static final int START_DRAG_MIN_OFFSET = 20;
    // 移动距离是拖动距离的倍数
    private static final int MULTIPLE_DRAG_OFFSET = 1;
    // 拖动方向
    private boolean mbDragLeft;
    private boolean mbDragRight;
    private boolean mbDragUp;
    private boolean mbDragDown;

    private boolean mScrollYDirection = true;

    private boolean isFirstDownEvent = false;

    private Scroller mScroller;
    private boolean isAutoScroll = false;

    //有可能有拖动子控件操作， 此变量为开关
    public boolean mIsMotionDownStillAnim = false;

    //子控件拖动， 容器本身不滑动， 此变量为开关
    public boolean mIsEnableScroll = true;

    AnimationUtils.RotationStateListener mRotationStateListener = new AnimationUtils.RotationStateListener() {

        @Override
        public void onAnimationFinish(int direction) {
            // TODO Auto-generated method stub
            logi( "onAnimationFinish direction=" + direction );
            if (direction == AnimationUtils.DIRECTION_NEXT) {
                scrollComplete();
            } else if (direction == AnimationUtils.DIRECTION_PREV) {
                scrollComplete();
            }
        }
    };
    // UI更新handler
    private Handler mUpdateUIHandler = null;

    // 构造方法 如果使用xml来构建视图则必须实现此构造方法
    public LtViewPager(Context context, AttributeSet attrs) {
        super( context, attrs );
        init( context );
    }

    // 构造方法
    public LtViewPager(Context context) {
        super( context );
        init( context );
    }

    private void init(Context context) {
        mUpdateUIHandler = new UpdateUIHandler( this );
        mAnimationUtils.setRotationStateListener( mRotationStateListener );
        //是否支持上下滑动
        mScrollYDirection = false;
        mScroller = new Scroller( context, sInterpolator );
    }

    private static final Interpolator sInterpolator = new Interpolator() {
        public float getInterpolation(float t) {
            return 1 - (1-t)*(1-t);
        }
    };

    public void setScrollYDirection(boolean b) {
        this.mScrollYDirection = b;
    }

    public void setAnimationEnable(boolean b) {
        isAnimationEnable = b;
    }

    /**
     * 设置适配器
     */
    public void setAdapter(Adapter adapter) {
        if (mCurrentPageItem.mPageView != null) {
            mCurrentPageItem.mPageView.removeAllViews();
            mCurrentPageItem.mPageView = null;
        }
        if (this.mPrevPageItem.mPageView != null) {
            mPrevPageItem.mPageView.removeAllViews();
            mPrevPageItem.mPageView = null;
        }
        if (this.mNextPageItem.mPageView != null) {
            mNextPageItem.mPageView.removeAllViews();
            mNextPageItem.mPageView = null;
        }

        mAdapter = adapter;
        // 当前页数
        int iCurrentPage = 0;

        // 当前偏移位置
        miCurrentOffsetX = 0;
        miCurrentOffsetY = 0;
        // 目标偏移位置
        miDestinationOffsetX = 0;
        miDestinationOffsetY = 0;
        // 暂停坐标位置(当移动到此坐标时暂停移动)
        miPauseOffsetX = 0;
        miPauseOffsetY = 0;

        this.removeAllViews();

        // 最后一次刷新页面时的总页数 刷新时对比是否变化，是则调用监听器事件.
        miLastRefreshListPageCount = 0;
    }

    /**
     * 设置监听器
     */
    public void setOnPageChangeListener(OnPageChangeListener l) {
        mOnPageChangeListener = l;
    }

    /**
     * 获得当前页数
     *
     * @return int 从0开始计数
     */
    public int getCurrentPageNumber() {
        if (null == mCurrentPageItem) {
            return 0;
        } else {
            return mCurrentPageItem.miPageNumber;
        }
    }

    /**
     * 获得总页数
     *
     * @return int
     */
    public int getPageCount() {
        if (null != mAdapter) {
            return mAdapter.getCount();
        }
        return 0;
    }

    /**
     * 获得下一页数
     *
     * @return int
     */
    public int getNextPageNumber() {
        if (null == mCurrentPageItem) {
            return 0;
        } else {
            int iNumber = mCurrentPageItem.miPageNumber + 1;
            if (iNumber >= getPageCount()) {
                iNumber = /*0*/-1;
            }
            return iNumber;
        }
    }

    /**
     * 获得上一页数
     *
     * @return int
     */
    public int getPrevPageNumber() {
        if (null == mCurrentPageItem) {
            return 0;
        } else {
            int iNumber = mCurrentPageItem.miPageNumber - 1;
/*			if(iNumber < 0){//如果当前页为第0页，则iNumber为-1，则跳转到最后一页
				int iPageCount = getPageCount();
				if(iPageCount > 0){
					iNumber = iPageCount - 1;
				}else{
					iNumber = 0;
				}
			}*/
            return iNumber;
        }
    }

    /**
     * 预先加载下一页视图
     *
     * @param iPageNumber 页数
     */
    private void createPage(PageScrollItem pageItem, int iPageNumber) {

        // 如果已经初始化
        if (null != pageItem.mPageView
                && pageItem.miPageNumber == iPageNumber
                && pageItem.mbInitViewInstance) {
            return;
        }

        logi("createPage number=" + iPageNumber );
        // 加载下一个页面视图
        PageView view = mAdapter.getPage( iPageNumber, pageItem.mPageView, this );
        logi("createPage number=" + iPageNumber + "  end" );
        // 记录
        pageItem.mPageView = view;
        pageItem.miPageNumber = iPageNumber;
        if (!pageItem.mbInitViewInstance) {
            pageItem.mbInitViewInstance = true;
        }
    }

    boolean isCreatePrevPage = false;

    private void createPrevPage() {
        isCreatePrevPage = true;
        int createPage = mPrevPageItem.miPageNumber;
        logi("createPrevPage mPrevPageItem.miPageNumber=" + mPrevPageItem.miPageNumber );
        PageView view = mAdapter.getPage( mPrevPageItem.miPageNumber, mPrevPageItem.mPageView, this );
        mPrevPageItem.mPageView = view;
        if (!mPrevPageItem.mbInitViewInstance) {
            mPrevPageItem.mbInitViewInstance = true;
        }

        if (mCurrentPageItem.mPageView != null && !mCurrentPageItem.mPageView.isClickable()) {
            mCurrentPageItem.mPageView.setClickable( true );
        }
        if (mNextPageItem.mPageView != null && !mNextPageItem.mPageView.isClickable()) {
            mNextPageItem.mPageView.setClickable( true );
        }
        if (mPrevPageItem.mPageView != null && !mPrevPageItem.mPageView.isClickable()) {
            mPrevPageItem.mPageView.setClickable( true );
        }

        if (indexOfChild( view ) < 0) {
            // 添加子节点
            logi("addNextPageView addView" );
            int X = ((LtAbsLayout.LtLayoutParams) mCurrentPageItem.mPageView.getLayoutParams()).x;
            LtAbsLayout.LtLayoutParams layoutParams1 = (LtAbsLayout.LtLayoutParams) view.getLayoutParams();
            RelativeLayout.LayoutParams layoutParamsThis = (RelativeLayout.LayoutParams) this.getLayoutParams();
            if (layoutParams1.x != X + layoutParamsThis.width) {
                layoutParams1.setXY( X + layoutParamsThis.width, 0 );
                view.setLayoutParams( layoutParams1 );
            }
            view.layout( layoutParams1.x, 0, layoutParams1.x + layoutParams1.width, 0 + layoutParams1.height );

            this.addView( view );
        }

        if (createPage != mPrevPageItem.miPageNumber) {
            mUpdateUIHandler.sendEmptyMessage( UpdateUIHandler.CREATE_PREV_PAGE );
        } else {
            isCreatePrevPage = false;
        }
    }

    boolean isCreateNextPage = false;

    private void createNextPage() {
        logi(mNextPageItem.mbInitViewInstance+"createNextPage mNextPageItem.miPageNumber=" + mNextPageItem.miPageNumber );
        isCreateNextPage = true;
        int createPage = mNextPageItem.miPageNumber;

        logi("createNextPage mNextPageItem.miPageNumber=" + mNextPageItem.miPageNumber );
        PageView view = mAdapter.getPage( mNextPageItem.miPageNumber, mNextPageItem.mPageView, this );
        mNextPageItem.mPageView = view;
        if (!mNextPageItem.mbInitViewInstance) {
            mNextPageItem.mbInitViewInstance = true;
            if (indexOfChild( view ) < 0) {
                // 添加子节点
                logi("addNextPageView addView" );
                int X = ((LtAbsLayout.LtLayoutParams) mCurrentPageItem.mPageView.getLayoutParams()).x;
                LtAbsLayout.LtLayoutParams layoutParams1 = (LtAbsLayout.LtLayoutParams) view.getLayoutParams();
                RelativeLayout.LayoutParams layoutParamsThis = (RelativeLayout.LayoutParams) this.getLayoutParams();
                if (layoutParams1.x != X + layoutParamsThis.width) {
                    layoutParams1.setXY( X + layoutParamsThis.width, 0 );
                    view.setLayoutParams( layoutParams1 );
                }
                view.layout( layoutParams1.x, 0, layoutParams1.x + layoutParams1.width, 0 + layoutParams1.height );

                this.addView( view );
            }
        }

        if (mCurrentPageItem.mPageView != null && !mCurrentPageItem.mPageView.isClickable()) {
            mCurrentPageItem.mPageView.setClickable( true );
        }
        if (mNextPageItem.mPageView != null && !mNextPageItem.mPageView.isClickable()) {
            mNextPageItem.mPageView.setClickable( true );
        }
        if (mPrevPageItem.mPageView != null && !mPrevPageItem.mPageView.isClickable()) {
            mPrevPageItem.mPageView.setClickable( true );
        }

        if (createPage != mNextPageItem.miPageNumber) {
            //createPrevPage();
            mUpdateUIHandler.sendEmptyMessage( UpdateUIHandler.CREATE_NEXT_PAGE );
        } else {
            isCreateNextPage = false;
        }
    }

    /**
     * 预加载上下页视图
     */
    private void preCreatePage() {
        // 如果正在移动
        if (mbScrolling) {
            return;
        }
        int iPrevNumber = getPrevPageNumber();
        int iNextNumber = getNextPageNumber();
        if (iPrevNumber >= 0) {
            addPrevPageView( iPrevNumber, -this.getWidth(), 0 );
        }
        if (iNextNumber >= 0) {
            addNextPageView( iNextNumber, this.getWidth(), 0 );
        }
        if (mCurrentPageItem.mPageView != null && !mCurrentPageItem.mPageView.isClickable()) {
            mCurrentPageItem.mPageView.setClickable( true );
        }
        if (mNextPageItem.mPageView != null && !mNextPageItem.mPageView.isClickable()) {
            mNextPageItem.mPageView.setClickable( true );
        }
        if (mPrevPageItem.mPageView != null && !mPrevPageItem.mPageView.isClickable()) {
            mPrevPageItem.mPageView.setClickable( true );
        }
        //isAnimationStart = false;
        mbScrolling = false;
        dump( "preCreatePage" );
    }


    /**
     * 添加下一个要显示的页面
     *
     * @param iPageNumber 页数
     */
    private void addNextPageView(int iPageNumber, int left, int top) {
        logi("addNextPageView  iPageNumber=" + iPageNumber + "  left=" + left );
        // 加载下一个页面视图
        createPage( mNextPageItem, iPageNumber );

        if (mNextPageItem.mPageView == null)
            return;

        // 改变位置
        PageView view = mNextPageItem.mPageView;
        //logi( "addNextPageView childCount="+this.getChildCount()+" mNextPageItem.mPageView="+view);
        // 不存在
        if (indexOfChild( view ) < 0) {
            // 添加子节点
            logi("addNextPageView addView" );
            int X = ((LtAbsLayout.LtLayoutParams) mCurrentPageItem.mPageView.getLayoutParams()).x;
            LtAbsLayout.LtLayoutParams layoutParams1 = (LtAbsLayout.LtLayoutParams) mNextPageItem.mPageView.getLayoutParams();
            RelativeLayout.LayoutParams layoutParamsThis = (RelativeLayout.LayoutParams) this.getLayoutParams();
            if (layoutParams1.x != X + layoutParamsThis.width) {
                layoutParams1.setXY( X + layoutParamsThis.width, top );
                mNextPageItem.mPageView.setLayoutParams( layoutParams1 );
            }
            view.layout( layoutParams1.x, top, layoutParams1.x + layoutParams1.width, top + layoutParams1.height );

            this.addView( view );
        }

    }

    /**
     * 添加下一个要显示的页面
     *
     * @param iPageNumber 页数
     */
    private void addPrevPageView(int iPageNumber, int left, int top) {
        logi("addPrevPageView  iPageNumber=" + iPageNumber + "  left=" + left );
        // 加载下一个页面视图
        createPage( mPrevPageItem, iPageNumber );

        if (mPrevPageItem.mPageView == null)
            return;

        // 改变位置
        PageView view = mPrevPageItem.mPageView;

        // 不存在
        if (indexOfChild( view ) < 0) {
            // 添加子节点
            logi("addPrevPageView addView" );

            int X = ((LtAbsLayout.LtLayoutParams) mCurrentPageItem.mPageView.getLayoutParams()).x;
            LtAbsLayout.LtLayoutParams layoutParams1 = (LtAbsLayout.LtLayoutParams) mPrevPageItem.mPageView.getLayoutParams();
            RelativeLayout.LayoutParams layoutParamsThis = (RelativeLayout.LayoutParams) this.getLayoutParams();
            if (layoutParams1.x != X - layoutParamsThis.width) {
                layoutParams1.setXY( X - layoutParamsThis.width, top );
                mPrevPageItem.mPageView.setLayoutParams( layoutParams1 );
            }
            view.layout( layoutParams1.x, top, layoutParams1.x + layoutParams1.width, top + layoutParams1.height );

            this.addView( view );
        }
    }


    /**
     * 滚动页面到指定位置<br>
     * 相对于列表的位置.<br>
     */
    private void scrollToOffset(int x, int y) {
        this.scrollTo( -x, -y );
        // 当前偏移位置
        miCurrentOffsetX = x;
        miCurrentOffsetY = y;
    }

    /**
     * 滑动一步
     */
    private void scrollStep() {
/*		if(miCurrentOffsetX == miDestinationOffsetX
				&& miCurrentOffsetY == miDestinationOffsetY){
			scrollComplete();
			return;
		}*/
        logi( "  scrollStep");
        // 如果是暂停
        if (miCurrentOffsetX == miPauseOffsetX
                && miCurrentOffsetY == miPauseOffsetY) {
            // 提交下一次滑动
            postScrollStep( 20 );
            logi("scrollStep 1" );
            return;
        }
        logi("mbMotionDown=" + mbMotionDown + "  miDestinationOffsetX=" + miDestinationOffsetX + "  miPauseOffsetX=" + miPauseOffsetX + "  miCurrentOffsetX=" + miCurrentOffsetX );
        if ( (!mbMotionDown || mIsMotionDownStillAnim) && miPauseOffsetX == miDestinationOffsetX) {
            isAutoScroll = true;
            mScroller.computeScrollOffset();
            int sx = getScrollX();
            int sy = getScrollY();

            int dx;
            if (sx > 0) {
                dx = Math.abs( miDestinationOffsetX ) - sx;
            } else {
                dx = -((miDestinationOffsetX) + sx);
            }
            int duration = 200;
            logi("sx=" + sx + "  sy=" + sy + "  dx=" + dx );
            mScroller.startScroll( sx, sy, dx, 0, duration );
            postInvalidate();
            return;
        }

        int iNextX;
        int iNextY;
        if (miDestinationOffsetX > miCurrentOffsetX) {
            iNextX = miCurrentOffsetX + miScrollStep;
            logi("miDestinationOffsetX > miCurrentOffsetX 1 :"+iNextX+""+miCurrentOffsetX+""+miScrollStep );
            if (iNextX > miDestinationOffsetX) {
                iNextX = miDestinationOffsetX;
                logi("miDestinationOffsetX > miCurrentOffsetX 2 :"+iNextX+""+miCurrentOffsetX+""+miScrollStep );
            }
            if (iNextX > miPauseOffsetX) {
                iNextX = miPauseOffsetX;
                logi("miDestinationOffsetX > miCurrentOffsetX 3 :"+iNextX+""+miCurrentOffsetX+""+miScrollStep );
            }
        } else if (miDestinationOffsetX < miCurrentOffsetX) {
            iNextX = miCurrentOffsetX - miScrollStep;
            logi("miDestinationOffsetX < miCurrentOffsetX 1 :"+iNextX+""+miCurrentOffsetX+""+miScrollStep );
            if (iNextX < miDestinationOffsetX) {
                logi("miDestinationOffsetX < miCurrentOffsetX 2 :"+iNextX+""+miCurrentOffsetX+""+miScrollStep );
                iNextX = miDestinationOffsetX;
            }
            if (iNextX < miPauseOffsetX) {
                logi("miDestinationOffsetX < miCurrentOffsetX 3 :"+iNextX+""+miCurrentOffsetX+""+miScrollStep );
                iNextX = miPauseOffsetX;
            }
        } else {
            iNextX = miDestinationOffsetX;
            logi("iNextX = miDestinationOffsetX :"+iNextX+""+miCurrentOffsetX+""+miScrollStep );
        }

        if (miDestinationOffsetY > miCurrentOffsetY) {
            iNextY = miCurrentOffsetY + miScrollStep;
            if (iNextY > miDestinationOffsetY) {
                iNextY = miDestinationOffsetY;
            }
            if (iNextY > miPauseOffsetY) {
                iNextY = miPauseOffsetY;
            }
        } else if (miDestinationOffsetY < miCurrentOffsetY) {
            iNextY = miCurrentOffsetY - miScrollStep;
            if (iNextY < miDestinationOffsetY) {
                iNextY = miDestinationOffsetY;
            }
            if (iNextY < miPauseOffsetY) {
                iNextY = miPauseOffsetY;
            }
        } else {
            iNextY = miDestinationOffsetY;
        }
        logi("iNextX=" + iNextX + "  miDestinationOffsetX=" + miDestinationOffsetX + "  isFirstDownEvent=" + isFirstDownEvent );

        if (isFirstDownEvent) {
            isFirstDownEvent = false;
            if (iNextX == miDestinationOffsetX
                    && iNextY == miDestinationOffsetY) {
                scrollToOffset( iNextX / 2, iNextY );
            } else {
                scrollToOffset( iNextX, iNextY );
            }
            // 提交下一次滑动
            postScrollStep( 0 );
        } else {
            // 完成
            logi( "iNextX == miDestinationOffsetX "+iNextX+"miDestinationOffsetX"+miDestinationOffsetX);
            if (iNextX == miDestinationOffsetX
                    && iNextY == miDestinationOffsetY) {
                scrollComplete();
            } else {
                // 滑动
                scrollToOffset( iNextX, iNextY );
                // 提交下一次滑动
                postScrollStep( 0 );
            }
        }
    }

    private void animationStart() {
        int X = ((LtAbsLayout.LtLayoutParams) mCurrentPageItem.mPageView.getLayoutParams()).x;
        if (mbScrollToNextPage && mNextPageItem.mPageView != null) {
            LtAbsLayout.LtLayoutParams layoutParams = (LtAbsLayout.LtLayoutParams) mNextPageItem.mPageView.getLayoutParams();
            layoutParams.setXY( X, 0 );
            mNextPageItem.mPageView.setLayoutParams( layoutParams );
        } else if (mPrevPageItem.mPageView != null) {
            LtAbsLayout.LtLayoutParams layoutParams = (LtAbsLayout.LtLayoutParams) mPrevPageItem.mPageView.getLayoutParams();
            layoutParams.setXY( X, 0 );
            mPrevPageItem.mPageView.setLayoutParams( layoutParams );
        }
    }

    private void dump(String str) {
        if (!DBG) return;
        int nextX = -1;
        int currentX = -1;
        int prevX = -1;
        LtAbsLayout.LtLayoutParams ll;
        if (mNextPageItem.mPageView != null) {
            ll = (LtAbsLayout.LtLayoutParams) mNextPageItem.mPageView.getLayoutParams();
            nextX = ll.x;
        }
        if (mCurrentPageItem.mPageView != null) {
            ll = (LtAbsLayout.LtLayoutParams) mCurrentPageItem.mPageView.getLayoutParams();
            currentX = ll.x;
        }
        if (mPrevPageItem.mPageView != null) {
            ll = (LtAbsLayout.LtLayoutParams) mPrevPageItem.mPageView.getLayoutParams();
            prevX = ll.x;
        }
        logi( str + "  dump nextX=" + nextX + " p=" + mNextPageItem.miPageNumber + "  currentX=" + currentX + " p=" + mCurrentPageItem.miPageNumber + "  prevX=" + prevX + " p=" + mPrevPageItem.miPageNumber );
        logi( str + "  dump miDestinationOffsetX=" + miDestinationOffsetX + "  miPauseOffsetX=" + miPauseOffsetX + "  miCurrentOffsetX=" + miCurrentOffsetX );
    }

    /**
     * 滑动完成
     */
    protected void scrollComplete() {
        logi( "scrollComplete" );
        if (mCurrentPageItem == null || mCurrentPageItem.mPageView == null) {
            return;
        }

        // 更新按下点击位置,否则翻页速度太快.
        if (mbMotionDown && MULTIPLE_DRAG_OFFSET > 1) {
            miMotionDownX += miCurrentOffsetX / MULTIPLE_DRAG_OFFSET;
            miMotionDownY += miCurrentOffsetY / MULTIPLE_DRAG_OFFSET;
            logi("scrollComplete mbMotionDown && MULTIPLE_DRAG_OFFSET > 1" + miMotionDownX + " miMotionDownY=" + miMotionDownY);
        }

        // 交换
        if (mbScrollToNextPage && mNextPageItem.mPageView != null) {
            PageScrollItem tmpItem1 = mPrevPageItem;
            PageScrollItem tmpItem2 = mCurrentPageItem;
            PageScrollItem tmpItem3 = mNextPageItem;
            mPrevPageItem = tmpItem2;
            mCurrentPageItem = tmpItem3;
            mNextPageItem = tmpItem1;
        } else if (mbScrollToPrevPage && mPrevPageItem.mPageView != null) {
            PageScrollItem tmpItem1 = mPrevPageItem;
            PageScrollItem tmpItem2 = mCurrentPageItem;
            PageScrollItem tmpItem3 = mNextPageItem;
            mPrevPageItem = tmpItem3;
            mCurrentPageItem = tmpItem1;
            mNextPageItem = tmpItem2;
        }
        dump( "scrollComplete1" );

        //移动页面位置
        if (mCurrentPageItem.mPageView != null) {
            LtAbsLayout.LtLayoutParams currentL = (LtAbsLayout.LtLayoutParams) mCurrentPageItem.mPageView.getLayoutParams();
            if (currentL.x != this.getWidth() * mCurrentPageItem.miPageNumber) {
                currentL.setXY( this.getWidth() * mCurrentPageItem.miPageNumber, 0 );
                mCurrentPageItem.mPageView.setLayoutParams( currentL );
            }

            if (mNextPageItem.mPageView != null) {
                LtAbsLayout.LtLayoutParams nextL = (LtAbsLayout.LtLayoutParams) mNextPageItem.mPageView.getLayoutParams();
                if (nextL.x != currentL.x + this.getWidth()) {
                    nextL.setXY( currentL.x + this.getWidth(), 0 );
                    mNextPageItem.mPageView.setLayoutParams( nextL );
                }
            }

            if (mPrevPageItem.mPageView != null) {
                LtAbsLayout.LtLayoutParams prevL = (LtAbsLayout.LtLayoutParams) mPrevPageItem.mPageView.getLayoutParams();
                if (prevL.x != currentL.x - this.getWidth()) {
                    prevL.setXY( currentL.x - this.getWidth(), 0 );
                    mPrevPageItem.mPageView.setLayoutParams( prevL );
                }
            }

            scrollTo( currentL.x, 0 );

            miCurrentOffsetX = -this.getWidth() * (mCurrentPageItem.miPageNumber);
            if (miCurrentOffsetY != 0) {
                miCurrentOffsetY = 0;
            }
            // 目标偏移位置
            miDestinationOffsetX = miCurrentOffsetX;
            if (miDestinationOffsetY != 0) {
                miDestinationOffsetY = 0;
            }
            // 暂停坐标位置(当移动到此坐标时暂停移动)
            miPauseOffsetX = miCurrentOffsetX;
            miPauseOffsetY = 0;
            logi("mbScrollToNextPage=" + mbScrollToNextPage + "  mbScrollToPrevPage=" + mbScrollToPrevPage );
            if (mbScrollToNextPage) {
                int iNextNumber = getNextPageNumber();
                logi("iNextNumber=" + iNextNumber );
                if (iNextNumber >= 0) {
                    mNextPageItem.miPageNumber = iNextNumber;
                    if (!isCreateNextPage) {
                        if (mUpdateUIHandler.hasMessages( UpdateUIHandler.CREATE_NEXT_PAGE )) {
                            logi( "scrollComplete has UpdateUIHandler.CREATE_NEXT_PAGE" );
                            mUpdateUIHandler.removeMessages( UpdateUIHandler.CREATE_NEXT_PAGE );
                        }
                        mUpdateUIHandler.sendEmptyMessage( UpdateUIHandler.CREATE_NEXT_PAGE );
                    }
                }
            } else if (mbScrollToPrevPage) {
                int iPrevNumber = getPrevPageNumber();
                logi("iPrevNumber=" + iPrevNumber );
                if (iPrevNumber >= 0) {
                    mPrevPageItem.miPageNumber = iPrevNumber;
                    if (!isCreatePrevPage) {
                        if (mUpdateUIHandler.hasMessages( UpdateUIHandler.CREATE_PREV_PAGE )) {
                            logi( "scrollComplete has UpdateUIHandler.CREATE_PREV_PAGE" );
                            mUpdateUIHandler.removeMessages( UpdateUIHandler.CREATE_PREV_PAGE );
                        }
                        mUpdateUIHandler.sendEmptyMessage( UpdateUIHandler.CREATE_PREV_PAGE );
                    }
                }
            }
        }


        if (!mCurrentPageItem.mPageView.isShown()) {
            mCurrentPageItem.mPageView.setVisibility( View.VISIBLE );
        }

        // 监听页数变化事件
        if (null != mOnPageChangeListener) {
/*			Message msg = mUpdateUIHandler.obtainMessage(UpdateUIHandler.NOTIFY_SCROLL_COMPLETE, mCurrentPageItem.miPageNumber, 0);
			mUpdateUIHandler.sendMessage(msg);*/
            // 监听当前页数变化事件
            Message msg1 = mUpdateUIHandler.obtainMessage( UpdateUIHandler.NOTIFY_CURRENT_PAGE_NUM_CHANGE, mCurrentPageItem.miPageNumber, 0 );
            mUpdateUIHandler.sendMessage( msg1 );
        }

        // 预加载上下页视图
        //postCreatePrevNextPage();
        //postCreatePrevNextPage(0);
        isAnimationStart = false;
        mbScrolling = false;

        //取消点击保护， 动画结束
        hitTimerProtect = false;
        dump( "scrollComplete21" );
        logi( "scrollComplete21" );
    }

    /**
     * 滑动到上一个页面<br>
     * 必须在UI线程调用<br>
     *
     * @param bSmoothScroll 是否有滑动过程
     */
    public void scrollToPrevPage(boolean bSmoothScroll) {
        if(hitTimerProtect) return;
        if (mCurrentPageItem.miPageNumber <= 0) {//如果当前为第一页，不可向前滑动
            return;
        }
        hitTimerProtect = true;
        // 正在移动
        if (mbScrolling) {
            logi( "Is Scrolling, End Scroll First." );
            scrollComplete();
        }

        // 上一页标志
        mbScrollToPrevPage = true;
        mbScrollToNextPage = false;

        // 目标偏移位置
        //miDestinationOffsetX = this.getWidth();
        miDestinationOffsetX = -(mCurrentPageItem.miPageNumber - 1) * this.getWidth();
        miDestinationOffsetY = 0;
        // 暂停坐标位置(当移动到此坐标时暂停移动)
        miPauseOffsetX = miDestinationOffsetX;
        miPauseOffsetY = miDestinationOffsetY;

        // 步长
        miScrollStep = (this.getWidth() / SCROLL_STEP_COUNT);// + 100;

        // 正在移动
        mbScrolling = true;

        int iPrevNumber = getPrevPageNumber();
        // 生成新的页面
        if (iPrevNumber >= 0)
            addPrevPageView( getPrevPageNumber(), -this.getWidth(), 0 );


        if (bSmoothScroll) {
            if (getAnimationType() == Constants.ANIMATION_DEFAULT) {
                // 滑动页面命令
                postScrollStep( 0 );
            }
        } else {
            scrollComplete();
        }
    }

    /**
     * 滑动到下一个页面<br>
     * 必须在UI线程调用<br>
     *
     * @param bSmoothScroll 是否有滑动过程
     */
    public void scrollToNextPage(boolean bSmoothScroll) {
        if(hitTimerProtect) return;
        if (mCurrentPageItem.miPageNumber >= mAdapter.getCount() - 1) {//如果当前为最后一页，不可向后滑动
            return;
        }
        hitTimerProtect = true;
        //logi( "scrollToNextPage start.");

        // 正在移动
        if (mbScrolling) {
            logi( "Is Scrolling, End Scroll First." );
            scrollComplete();
        }

        // 下一页标志
        mbScrollToPrevPage = false;
        mbScrollToNextPage = true;

        // 目标偏移位置
        miDestinationOffsetX = -this.getWidth() * (mCurrentPageItem.miPageNumber + 1);
        miDestinationOffsetY = 0;
        // 暂停坐标位置(当移动到此坐标时暂停移动)
        miPauseOffsetX = miDestinationOffsetX;
        miPauseOffsetY = miDestinationOffsetY;

        // 步长
        miScrollStep = (this.getWidth() / SCROLL_STEP_COUNT);// + 100;

        // 正在移动
        mbScrolling = true;

        // 生成新的页面
        int iNextNumber = getNextPageNumber();
        if (iNextNumber >= 0)
            addNextPageView( iNextNumber, this.getWidth(), 0 );

        //logi( "addNextPageView end.");

        if (bSmoothScroll) {
            if (getAnimationType() == Constants.ANIMATION_DEFAULT) {
                // 滑动页面命令
                postScrollStep( 0 );
            }
        } else {
            scrollComplete();
        }
    }

    /**
     * 跳转到指定页
     */
    public void setPageNumber(int pageNum) {
        //页码设置错误，直接返回
        if (pageNum < 0 || pageNum >= mAdapter.getCount()) {
            logi( "setPageNumber=" + pageNum + " is out of bound" );
            return;
        }
        //如果设置的页码就是当前页码，则直接返回。
        if (mCurrentPageItem.miPageNumber == pageNum) {
            logi( "setPageNumber=" + pageNum + " is not change,return" );
            return;
        }
        //设置的页码是下一页，则直接滑动到下一页
        if (mCurrentPageItem.miPageNumber + 1 == pageNum) {
            scrollToNextPage( false );
            return;
        }
        //设置的页码是上一页，则直接滑动到上一页
        if (mCurrentPageItem.miPageNumber - 1 == pageNum) {
            scrollToPrevPage( false );
            return;
        }

        // 是否改变当前页数
        boolean bChangeCurrentPageNumber = true;

        // 是否改变总页数
        boolean bChangeTotalPageNumber = false;

        // 正在移动
        if (mbScrolling) {
            logi( "Is Scrolling, End Scroll First." );
            scrollComplete();
        }

        //设置当前页码
        mCurrentPageItem.miPageNumber = pageNum;

        int iPageCount = getPageCount();
        // 最后一次刷新页面时的总页数 刷新时对比是否变化，是则调用监听器事件.
        if (iPageCount != miLastRefreshListPageCount) {
            // 改变总页数标志
            bChangeTotalPageNumber = true;
            miLastRefreshListPageCount = iPageCount;
        }

        // 用handler在下一次动作中执行,先显示当前页面.
        mCurrentPageItem.mPageView = mAdapter.getPage( mCurrentPageItem.miPageNumber, mCurrentPageItem.mPageView, this );
        mCurrentPageItem.mbInitViewInstance = true;
        logi("refreshpage childCount" + this.getChildCount() + " mCurrentPageItem.mPageView=" + mCurrentPageItem.mPageView );
        // 不存在
        if (indexOfChild( mCurrentPageItem.mPageView ) < 0) {
            // 添加子节点
            logi("refreshpage  addView" );
            this.addView( mCurrentPageItem.mPageView );
        }

        // 上一页
        mPrevPageItem.mbInitViewInstance = false;
        // 下一页
        mNextPageItem.mbInitViewInstance = false;

        // 预加载上下页视图
        postCreatePrevNextPage();

        //刷新view的位置和相关参数
        resetView();

        // 监听页数变化事件
        if (null != mOnPageChangeListener) {
            if (bChangeTotalPageNumber) {
                logi("Total Page Change Event." );
                mOnPageChangeListener.onTotalPageNumberChange( this, mCurrentPageItem.miPageNumber, iPageCount );
            }
            if (bChangeCurrentPageNumber) {
                hitTimerProtect = false;
                mOnPageChangeListener.onCurrentPageNumberChange( this, mCurrentPageItem.miPageNumber );
            }
        }
        this.invalidate();
    }

    /**
     * 刷新当前页面
     */
    public void refreshPage(boolean bResetPageNumber) {

        // 正在移动
        if (mbScrolling) {
            scrollComplete();
        }

        // 是否改变当前页数
        boolean bChangeCurrentPageNumber = false;
        // 是否改变总页数
        boolean bChangeTotalPageNumber = false;

        // 页数
        if (bResetPageNumber) {
            // 改变当前页数标志
            if (mCurrentPageItem.miPageNumber > 0) {
                bChangeCurrentPageNumber = true;
            }
            mCurrentPageItem.miPageNumber = 0;
        }

        int iPageCount = getPageCount();
        // 最后一次刷新页面时的总页数 刷新时对比是否变化，是则调用监听器事件.
        if (iPageCount != miLastRefreshListPageCount) {
            // 改变总页数标志
            bChangeTotalPageNumber = true;
            miLastRefreshListPageCount = iPageCount;
        }

        // 如果当前页数大于等于总页数
        if (mCurrentPageItem.miPageNumber > 0) {
            if (mCurrentPageItem.miPageNumber >= iPageCount) {
                // 改变当前页数标志
                bChangeCurrentPageNumber = true;

                if (iPageCount > 0) {
                    mCurrentPageItem.miPageNumber = iPageCount - 1;
                } else {
                    mCurrentPageItem.miPageNumber = 0;
                }
            }
        }

        // 用handler在下一次动作中执行,先显示当前页面.
        mCurrentPageItem.mPageView = mAdapter.getPage( mCurrentPageItem.miPageNumber, mCurrentPageItem.mPageView, this );
        mCurrentPageItem.mbInitViewInstance = true;
        logi("refreshpage childCount" + this.getChildCount() + " mCurrentPageItem.mPageView=" + mCurrentPageItem.mPageView );
        // 不存在
        int num = indexOfChild( mCurrentPageItem.mPageView );
        if (num < 0) {
            // 添加子节点
            logi("refreshpage  addView" );
            this.addView( mCurrentPageItem.mPageView );
        }
        num = indexOfChild( mCurrentPageItem.mPageView );
        //刷新view的位置和相关参数
        resetView();

        // 上一页
        mPrevPageItem.mbInitViewInstance = false;
        // 下一页
        mNextPageItem.mbInitViewInstance = false;
        if(bResetPageNumber){
            // 上一页
//            mPrevPageItem.mbInitViewInstance = false;
            mPrevPageItem.miPageNumber = -1;
            // 下一页
//            mNextPageItem.mbInitViewInstance = false;
            mNextPageItem.miPageNumber = -1;
        }

        // 预加载上下页视图
        postCreatePrevNextPage();

        // 监听页数变化事件
        if (null != mOnPageChangeListener) {
            if (bChangeTotalPageNumber) {
                logi( "Total Page Change Event." );
                mOnPageChangeListener.onTotalPageNumberChange( this, mCurrentPageItem.miPageNumber, iPageCount );
            } else if (bChangeCurrentPageNumber) {
                mOnPageChangeListener.onCurrentPageNumberChange( this, mCurrentPageItem.miPageNumber );
            }
        }
        //hitTimerProtect = false;
        this.invalidate();
    }
    //刷新view的位置和相关参数
    private void resetView() {
        if (mCurrentPageItem.mPageView != null) {

            LtAbsLayout.LtLayoutParams currentL = (LtAbsLayout.LtLayoutParams) mCurrentPageItem.mPageView.getLayoutParams();
            if (currentL.x != this.getWidth() * mCurrentPageItem.miPageNumber) {
                //if(true){
                currentL.setXY( this.getWidth() * mCurrentPageItem.miPageNumber, 0 );
                mCurrentPageItem.mPageView.setLayoutParams( currentL );
            }

            if (mNextPageItem.mPageView != null) {
                LtAbsLayout.LtLayoutParams nextL = (LtAbsLayout.LtLayoutParams) mNextPageItem.mPageView.getLayoutParams();
                nextL.setXY( currentL.x + this.getWidth(), 0 );
                mNextPageItem.mPageView.setLayoutParams( nextL );
            }

            if (mPrevPageItem.mPageView != null) {
                LtAbsLayout.LtLayoutParams prevL = (LtAbsLayout.LtLayoutParams) mPrevPageItem.mPageView.getLayoutParams();
                prevL.setXY( currentL.x - this.getWidth(), 0 );
                mPrevPageItem.mPageView.setLayoutParams( prevL );
            }

            scrollTo( currentL.x, 0 );

            miCurrentOffsetX = -this.getWidth() * (mCurrentPageItem.miPageNumber);
            miCurrentOffsetY = 0;
            // 目标偏移位置
            miDestinationOffsetX = miCurrentOffsetX;
            miDestinationOffsetY = 0;
            // 暂停坐标位置(当移动到此坐标时暂停移动)
            miPauseOffsetX = miCurrentOffsetX;
            miPauseOffsetY = 0;
        }
        dump( "resetView" );
    }

    /**
     * 修改当前页面码<br>
     * 一般用于页面返回时，跳转到之前记录的页数<br>
     */
    public void setCurrentPageNumber(int iPageNumber) {
        if (mCurrentPageItem.miPageNumber == iPageNumber) {
            return;
        }
        mCurrentPageItem.miPageNumber = iPageNumber;
        // 上一页
        mPrevPageItem.mbInitViewInstance = false;
        // 下一页
        mNextPageItem.mbInitViewInstance = false;
    }

    /**
     * 开始拖动
     *
     * @param xDelta 目标偏移坐标
     */
    private void startDragX(int xDelta, boolean isScroll) {
        // 正在移动
        if (mbScrolling) {
			logi("Is Scrolling, End Scroll First.");
            scrollComplete();
        }

        // 拖动方向
        mbDragLeft = false;
        mbDragRight = false;
        mbDragUp = false;
        mbDragDown = false;

        if (xDelta == 0) {
            return;
        }
        logi("mPrevPageItem.miPageNumber=" + mPrevPageItem.miPageNumber + "  xDelta=" + xDelta );
        if (xDelta > 0) {
            if (mCurrentPageItem.miPageNumber == 0) {//如果当前为第一页，则不可以向右滑动
                return;
            }

            // 方向
            mbDragRight = true;

            // 上一页标志
            mbScrollToPrevPage = true;
            mbScrollToNextPage = false;

            // 目标偏移位置
            miDestinationOffsetX = -this.getWidth() * (mCurrentPageItem.miPageNumber - 1);
            miDestinationOffsetY = 0;
            // 暂停坐标位置(当移动到此坐标时暂停移动)
            miPauseOffsetX = -this.getWidth() * mCurrentPageItem.miPageNumber + xDelta;
            miPauseOffsetY = miDestinationOffsetY;

            // 步长
            miScrollStep = (this.getWidth() / SCROLL_STEP_COUNT);// + 100;

            // 正在移动
            mbScrolling = true;
            if (isScroll) {
                // 滑动页面命令
                postScrollStep( 0 );
            }
        } else {
            logi("startDragX  miPageNumber=" + mCurrentPageItem.miPageNumber + "" +
                    "   mAdapter.getCount()=" + mAdapter.getCount() );
            if (mCurrentPageItem.miPageNumber == this.mAdapter.getCount() - 1) {//如果当前为最后一页，则不可以向左滑动
                return;
            }
            // 方向
            mbDragLeft = true;

            // 下一页标志
            mbScrollToPrevPage = false;
            mbScrollToNextPage = true;

            // 目标偏移位置
            miDestinationOffsetX = -this.getWidth() * (mCurrentPageItem.miPageNumber + 1);
            miDestinationOffsetY = 0;
            // 暂停坐标位置(当移动到此坐标时暂停移动)
            miPauseOffsetX = -this.getWidth() * mCurrentPageItem.miPageNumber + xDelta;
            miPauseOffsetY = miDestinationOffsetY;

            // 步长
            miScrollStep = (this.getWidth() / SCROLL_STEP_COUNT);// + 100;

            // 正在移动
            mbScrolling = true;

            if (isScroll) {
                // 滑动页面命令
                postScrollStep( 0 );
            }
        }

        // 监听页面滑动方向事件
        if (null != mOnPageChangeListener) {
            mOnPageChangeListener.onPageDirectionChange( mbDragLeft, mbDragRight, mbDragDown, mbDragUp );
        }
    }

    /**
     * 改变拖动位置
     */
    private void changePauseOffset(int xDelta, int yDelta) {
        xDelta = xDelta - this.getWidth() * mCurrentPageItem.miPageNumber;

        logi("changePauseOffset");

        // 拖动方向
        // 往左拖动下一页
        if (mbDragLeft && mbScrollToNextPage) {
            // 同一方向
            if (xDelta <= miCurrentOffsetX) {
                logi("changePauseOffset xDelta <= miCurrentOffsetX");
                miPauseOffsetX = xDelta;
                return;
            }

            // 往反方向拖动  往右恢复到当前页
            mbDragLeft = false;
            mbDragRight = true;
            // 回到当前页
            mbScrollToNextPage = false;
            mbScrollToPrevPage = false;
            // 目标位置
            miDestinationOffsetX = -(mCurrentPageItem.miPageNumber) * this.getWidth();

            // 暂停位置
            miPauseOffsetX = xDelta;
            logi("reverse direction." );
            return;
        }

        // 往左恢复到当前页
        if (mbDragLeft && !mbScrollToNextPage) {
            // 同一方向
            if (xDelta <= miCurrentOffsetX && xDelta <= this.getWidth() * mCurrentPageItem.miPageNumber) {
                miPauseOffsetX = xDelta;
                return;
            }

            // 往反方向拖动  往右恢复到上一页
            mbDragLeft = false;
            mbDragRight = true;
            // 滑动到上一页
            mbScrollToNextPage = false;
            mbScrollToPrevPage = true;
            // 目标位置
            miDestinationOffsetX = -(mCurrentPageItem.miPageNumber - 1) * this.getWidth();

            // 暂停位置
            miPauseOffsetX = xDelta;
            logi("reverse direction." );
            return;
        }

        // 往右拖动上一页
        if (mbDragRight && mbScrollToPrevPage) {
            // 同一方向
            if (xDelta >= miCurrentOffsetX) {
                logi("changePauseOffset xDelta >= miCurrentOffsetX");
                miPauseOffsetX = xDelta;
                return;
            }

            // 往反方向拖动  往右恢复到当前页
            mbDragLeft = true;
            mbDragRight = false;
            // 回到当前页
            mbScrollToNextPage = false;
            mbScrollToPrevPage = false;
            // 目标位置
            miDestinationOffsetX = -(mCurrentPageItem.miPageNumber) * this.getWidth();

            // 暂停位置
            miPauseOffsetX = xDelta;
            logi("reverse direction." );
            return;
        }

        // 往右恢复到当前页
        if (mbDragRight && !mbScrollToPrevPage) {
            // 同一方向
            if (xDelta >= miCurrentOffsetX && xDelta >= this.getWidth() * mCurrentPageItem.miPageNumber) {
                miPauseOffsetX = xDelta;
                return;
            }

            // 往反方向拖动  往左恢复到下一页
            mbDragLeft = true;
            mbDragRight = false;
            // 滑动到上一页
            mbScrollToNextPage = true;
            mbScrollToPrevPage = false;
            // 目标位置
            miDestinationOffsetX = -(mCurrentPageItem.miPageNumber + 1) * this.getWidth();

            // 暂停位置
            miPauseOffsetX = xDelta;
            logi("reverse direction." );
            return;
        }


        // 往上拖动下一页
        if (mbDragUp && mbScrollToNextPage) {
            // 同一方向
            if (yDelta <= miCurrentOffsetY) {
                miPauseOffsetY = yDelta;
                return;
            }

            // 往反方向拖动  往下恢复到当前页
            mbDragUp = false;
            mbDragDown = true;
            // 回到当前页
            mbScrollToNextPage = false;
            mbScrollToPrevPage = false;
            // 目标位置
            miDestinationOffsetY = 0;

            // 暂停位置
            miPauseOffsetY = yDelta;
            logi("reverse direction." );
            return;
        }

        // 往上恢复到当前页
        if (mbDragUp && !mbScrollToNextPage) {
            // 同一方向
            if (yDelta <= miCurrentOffsetY) {
                miPauseOffsetY = yDelta;
                return;
            }

            // 往反方向拖动  往下恢复到上一页
            mbDragUp = false;
            mbDragDown = true;
            // 滑动到上一页
            mbScrollToNextPage = false;
            mbScrollToPrevPage = true;
            // 目标位置
            miDestinationOffsetY = this.getHeight();

            // 暂停位置
            miPauseOffsetY = yDelta;
            logi("reverse direction." );
            return;
        }

        // 往下拖动上一页
        if (mbDragDown && mbScrollToPrevPage) {
            // 同一方向
            if (yDelta >= miCurrentOffsetY) {
                miPauseOffsetY = yDelta;
                return;
            }

            // 往反方向拖动  往上恢复到当前页
            mbDragUp = true;
            mbDragDown = false;
            // 回到当前页
            mbScrollToNextPage = false;
            mbScrollToPrevPage = false;
            // 目标位置
            miDestinationOffsetY = 0;

            // 暂停位置
            miPauseOffsetY = yDelta;
            logi("reverse direction." );
            return;
        }

        // 往下恢复到当前页
        if (mbDragDown && !mbScrollToPrevPage) {
            // 同一方向
            if (yDelta >= miCurrentOffsetY) {
                miPauseOffsetY = yDelta;
                return;
            }

            // 往反方向拖动  往上恢复到下一页
            mbDragUp = true;
            mbDragDown = false;
            // 滑动到上一页
            mbScrollToNextPage = true;
            mbScrollToPrevPage = false;
            // 目标位置
            miDestinationOffsetY = -this.getHeight();

            // 暂停位置
            miPauseOffsetY = yDelta;
            logi("reverse direction." );
            return;
        }


    }

    /**
     * 取消拖动,直接滑动终点位置
     */
    private void cancelPauseOffset() {
        logi("cancelPauseOffset");
        // 暂停坐标位置(当移动到此坐标时暂停移动)
        miPauseOffsetX = miDestinationOffsetX;
        miPauseOffsetY = miDestinationOffsetY;
    }

    /**
     * 每一个页面类
     */
    public static class PageView extends LtAbsLayout {
        // 构造方法 如果使用xml来构建视图则必须实现此构造方法
        public PageView(Context context, AttributeSet attrs) {
            super( context, attrs );
        }

        // 构造方法
        public PageView(Context context) {
            super( context );

        }

        /*
        public void setScrollEnable(boolean isScroll){
            scrollEnable = isScroll;
        }
        */
    }

    private static class PageScrollItem {
        // 视图
        public PageView mPageView = null;
        // 页数
        public int miPageNumber = 0;
        // 视图已经加载数据,初始化完毕可以直接显示。
        public boolean mbInitViewInstance = false;
    }

    /**
     * 异步处理器 更新界面显示<br>
     * 主要为了在非UI线程调用 <br>
     */
    private class UpdateUIHandler extends Handler {

        // 异式处理器 参数
        // 滚动页面
        public static final int SCROLL_STEP = 0;
        // 预加载上下页视图
        public static final int CREATE_PREV_NEXT_PAGE = 1;

        public static final int NOTIFY_SCROLL_COMPLETE = 2;

        public static final int NOTIFY_CURRENT_PAGE_NUM_CHANGE = 3;

        public static final int CREATE_PREV_PAGE = 4;

        public static final int CREATE_NEXT_PAGE = 5;


        // 弱引用 防止内存泄露
        private WeakReference<LtViewPager> mViewPager;

        // 构造
        public UpdateUIHandler(LtViewPager view) {
            super();
            mViewPager = new WeakReference<LtViewPager>( view );
        }

        @Override
        public void handleMessage(Message msg) {
            LtViewPager viewPager = mViewPager.get();
            if (null == viewPager) {
                return;
            }

            switch (msg.what) {

                // 滚动页面
                case (SCROLL_STEP):
                    // 正在移动
                    if (viewPager.mbScrolling) {
                        viewPager.scrollStep();
                    }
                    break;

                // 预加载上下页视图
                case (CREATE_PREV_NEXT_PAGE):
                    viewPager.preCreatePage();
                    break;
                case NOTIFY_SCROLL_COMPLETE:
                    if (mOnPageChangeListener != null) {
                        mOnPageChangeListener.onScrollComplete( LtViewPager.this, msg.arg1 );
                    }
                    break;
                case NOTIFY_CURRENT_PAGE_NUM_CHANGE:
                    if (mOnPageChangeListener != null) {
                        mOnPageChangeListener.onCurrentPageNumberChange( LtViewPager.this, msg.arg1 );
                    }
                    if (mOnPageChangeListener != null) {
                        mOnPageChangeListener.onScrollComplete( LtViewPager.this, msg.arg1 );
                    }
                    break;
                case CREATE_PREV_PAGE:
                    createPrevPage();
                    break;
                case CREATE_NEXT_PAGE:
                    createNextPage();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 滑动页面
     */
    public void postScrollStep(int iDelayMillis) {
        // 重复命令
        if (mUpdateUIHandler.hasMessages( UpdateUIHandler.SCROLL_STEP )) {
            return;
        }
        Message msg = mUpdateUIHandler.obtainMessage( UpdateUIHandler.SCROLL_STEP );
        mUpdateUIHandler.sendMessageDelayed( msg, iDelayMillis );
    }

    /**
     * 滑动页面
     */
    public void postScrollStep() {
        postScrollStep( SCROLL_STEP_TIME );
    }

    /**
     * 预加载上下页视图
     */
    public void postCreatePrevNextPage(int iDelayMillis) {
        // 重复命令
        if (mUpdateUIHandler.hasMessages( UpdateUIHandler.CREATE_PREV_NEXT_PAGE )) {
            return;
        }
        Message msg = mUpdateUIHandler.obtainMessage( UpdateUIHandler.CREATE_PREV_NEXT_PAGE );
        mUpdateUIHandler.sendMessageDelayed( msg, iDelayMillis );
    }

    /**
     * 预加载上下页视图
     */
    public void postCreatePrevNextPage() {
        postCreatePrevNextPage( 10/*10*/ );
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        logi( "onTouchEvent acition"+event.getAction()+" x y "+event.getX());

        if(!mIsEnableScroll) return true;
        if (hitTimerProtect) return true;
        final int action = event.getAction();
        final int x = (int) event.getX();
        final int y = (int) event.getY();
        logi("onTouchEvent ");

        if (!mScrollYDirection && (mbDragDown || mbDragUp)) {
            return true;
        }

        logi("onTouchEvent action=" + action + " x=" + x + " y=" + y );
        if (isAnimationStart) {
            return true;
        }

        switch (action) {

            case MotionEvent.ACTION_DOWN: {
                // 用户按下标志
                mbMotionDown = true;
                logi( "onTouchEvent ACTION_DOWN is true"+ mbMotionDown+""+Thread.currentThread().getStackTrace()[2].getLineNumber());
                logi("onTouchEvent ");
                // 如果正在移动
                if (mbScrolling) {
                    logi( "Is Scrolling, End Scroll First." );
                    if (!mScroller.isFinished()) {
                        mScroller.abortAnimation();
                    }
                    scrollComplete();
                } else if (isAutoScroll) {
                    isAutoScroll = false;
                    mScroller.abortAnimation();
                    scrollComplete();
                }

                // 记录点击位置
                miMotionDownX = x;
                miMotionDownY = y;
                isFirstDownEvent = true;
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                logi("onTouchEvent ACTION_MOVE");
                // 用户按下标志
                if (!mbMotionDown) {
                    break;
                }

                // 计算偏移
                int xDelta = (x - miMotionDownX) * MULTIPLE_DRAG_OFFSET;
                int yDelta = (y - miMotionDownY) * MULTIPLE_DRAG_OFFSET;


                // 未滑动
                //如果超出范围窗体范围直接不进入动画
                logi( "onTouchEvent x this.getWidth"+x+"=="+ this.getWidth());
                if( x < 0 || x > this.getWidth() )//|| !mIsEnableScroll )
                    break;
                if (!mbScrolling ) {
                    int iAbsXDelta = Math.abs( xDelta );
                    int iAbsYDelta = Math.abs( yDelta );
                    if (iAbsXDelta >= START_DRAG_MIN_OFFSET
                            || iAbsYDelta >= START_DRAG_MIN_OFFSET) {
                        boolean isScroll = true;
                        if (getAnimationType() != Constants.ANIMATION_DEFAULT) {
                            isScroll = false;
                        }

                        logi("onTouchEvent ACTION_MOVE xDelta is"+xDelta);

                        startDragX( xDelta, isScroll );
                        // 开始动画
                        /*if(iAbsXDelta >= iAbsYDelta){
						startDragX(xDelta, isScroll);
						return true;
					}else if(mScrollYDirection){
						startDragY(yDelta);
					}*/
                    }
                    //break;
                }

                // 正在滑动
                changePauseOffset( xDelta, yDelta );
                break;
            }


            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                logi("onTouchEvent ACTION_UP ACTION_CANCEL");
                // 正在滑动
                if (mbScrolling) {
                    hitTimerProtect = true;
                    cancelPauseOffset();
                }
                // 用户按下标志
                logi( "onTouchEvent ACTION_DOWN is true"+ mbMotionDown+""+Thread.currentThread().getStackTrace()[2].getLineNumber());
                mbMotionDown = false;

                break;
            }

        }
        logi("move" );
        return true;
    }

    /**
     * 点击其子view(这里指当中的MyTextView)。当手指按下屏幕是，事件的触发属性是:
     * layout的dispatchTouchEvent.action_down->layout的onInterceptTouchEvent.action_down->子View的dispatchTouchEvent.action_down
     * ->子view的onTouchEvent.action_down->layout的onTouchEvent.action_down->layout的dispatchTouchEvent.actionUp->layout的
     * onTouchEvent.actionUp
     * 返回值为false时，会依次触发action_down,事件会继续分发下去
     */

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        logi( "onInterceptTouchEvent acition"+ev.getAction()+" x y "+ev.getX());

        if(!mIsEnableScroll) return true;
        if (hitTimerProtect) return true;

        final int action = ev.getAction();
        // 如果正在移动
        if (((action == MotionEvent.ACTION_MOVE) && mbScrolling) || isAnimationStart) {
            return true;
        }

        logi("onInterceptTouchEvent action=" + action + "  x=" + ev.getX() + "  Y=" + ev.getY() );

        switch (action) {

            case MotionEvent.ACTION_DOWN: {
                dump( "onInterceptTouchEvent" );
                logi( "onInterceptTouchEvent" );
                // 用户按下标志
                mbMotionDown = true;
                logi( "onTouchEvent ACTION_DOWN mbMotionDown is true"+ mbMotionDown+""+Thread.currentThread().getStackTrace()[2].getLineNumber());

                // 如果正在移动
                if (mbScrolling) {
                    logi( "Is Scrolling, End Scroll First." );
                    if (!mScroller.isFinished()) {
                        mScroller.abortAnimation();
                    }
                    scrollComplete();
                } else if (isAutoScroll) {
                    logi( "Is isAutoScroll, End Scroll First." );
                    isAutoScroll = false;
                    mScroller.abortAnimation();
                    scrollComplete();
                }

                // 记录点击位置
                miMotionDownX = (int) ev.getX();
                miMotionDownY = (int) ev.getY();
                isFirstDownEvent = true;

                break;
            }

            case MotionEvent.ACTION_MOVE: {
                logi( "onInterceptTouchEvent Action Move" );
                // 用户按下标志
                if (!mbMotionDown) {
                    break;
                }

                // 计算偏移
                int xDelta = ((int) ev.getX() - miMotionDownX) * MULTIPLE_DRAG_OFFSET;
                int yDelta = ((int) ev.getY() - miMotionDownY) * MULTIPLE_DRAG_OFFSET;
                logi("onInterceptTouchEvent ACTION_MOVE xDelta x miMotionDownX"+xDelta+"=="+ev.getX()+"=="+miMotionDownX);

                logi( "onIntercept x this.getWidth"+ev.getX()+"=="+ this.getWidth());
                // 未滑动
                if (!mbScrolling) {
                    int iAbsXDelta = Math.abs( xDelta );
                    int iAbsYDelta = Math.abs( yDelta );
                    if (iAbsXDelta >= START_DRAG_MIN_OFFSET
                            || iAbsYDelta >= START_DRAG_MIN_OFFSET) {
                        boolean isScroll = true;
                        if (isAnimationEnable && getAnimationType() != Constants.ANIMATION_DEFAULT) {
                            isScroll = false;
                        }

                        // 开始动画
                        logi("onInterceptTouchEvent ACTION_MOVE xDelta is"+xDelta);
                        startDragX( xDelta, isScroll );
//						if(iAbsXDelta >= iAbsYDelta){
//							startDragX(xDelta, isScroll);
//						}else if(mScrollYDirection){
//							startDragY(yDelta);
//						}
                        logi( "onIntercept move return the true");
                        return true;
                    }
                    //break;
                }

                // 正在滑动
                changePauseOffset( xDelta, yDelta );
                break;
            }

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                logi( "onInterceptTouchEvent Action_up Move" );
                // 正在滑动
                if (mbScrolling) {
                    cancelPauseOffset();
                }
                // 用户按下标志
                mbMotionDown = false;
                break;
            }

        }
        logi("move mbScrolling=" + mbScrolling );
        return (mbScrolling);
    }



    // 适配器
    public static interface Adapter {

        /**
         * 总页数
         */
        public int getCount();

        /**
         * 获得当前页面的视图
         *
         * @param position    页数
         * @param convertView 循环利用的视图页面
         * @param parent      父节点视图
         * @return
         */
        public PageView getPage(int position, PageView convertView, ViewGroup parent);

        public PageView GetPageNoData(PageView convertView);

    }

    /**
     * 页数变动事件   监听滑动完成事件
     */
    public static interface OnPageChangeListener {
        /**
         * 滑动完成
         *
         * @param iPageNumber 从0开始计算
         */
        public void onScrollComplete(LtViewPager viewPager, int iPageNumber);

        /**
         * 当前页数改变<br>
         * 一般是在刷新页面时 当前页数大于等于总页数
         */
        public void onCurrentPageNumberChange(LtViewPager viewPager, int iPageNumber);

        /**
         * 总页数改变<br>
         * 一般是在刷新页面时 总页数变化
         */
        public void onTotalPageNumberChange(LtViewPager viewPager, int iCurrentPageNumber, int iTotalPageNumber);

        /**
         * 滑动方向<br>
         * 左滑、右滑、下滑、上滑
         */
        public void onPageDirectionChange(boolean mbDragLeft, boolean mbDragRight, boolean mbDragDown, boolean mbDragUp);

    }

    @Override
    public void onShowComplete() {
        // 加载下一个页面视图
        createPage( mPrevPageItem, 0 );
        createPage( mNextPageItem, 0 );
    }

    /**
     * 获得当前页面视图<br>
     * 用于启动滚动字幕.<br>
     *
     * @return PageView
     */
    public PageView getCurrentPageView() {
        return mCurrentPageItem.mPageView;
    }

    /**
     * 获得上一页面视图<br>
     * 用于停止滚动字幕.<br>
     *
     * @return PageView
     */
    public PageView getPrevPageView() {
        return mPrevPageItem.mPageView;
    }

    /**
     * 获得下一页面视图<br>
     * 用于停止滚动字幕.<br>
     *
     * @return PageView
     */
    public PageView getNextPageView() {
        return mNextPageItem.mPageView;
    }

    /**
     * 获得当前页面视图是否加载<br>
     * 用于更新已点高亮.<br>
     */
    protected boolean getCurrentPageInitInstance() {
        return mCurrentPageItem.mbInitViewInstance;
    }

    /**
     * 获得上一页面视图是否加载<br>
     * 用于更新已点高亮.<br>
     */
    protected boolean getPrevPageInitInstance() {
        return mPrevPageItem.mbInitViewInstance;
    }

    /**
     * 获得下一页面视图是否加载<br>
     * 用于更新已点高亮.<br>
     */
    protected boolean getNextPageInitInstance() {
        return mNextPageItem.mbInitViewInstance;
    }

    private int getAnimationType() {
        return 0;
        //return SharedPreferencesUtil.getPage3DType(getContext());
    }

    @Override
    public void computeScroll() {
        logi( "computeScroll");
        super.computeScroll();
        if (!isAutoScroll/* && !mbScrolling*/) {
            return;
        }

        //先判断mScroller滚动是否完成
        if (mScroller.computeScrollOffset()) {
            //log("computeScroll X()="+mScroller.getCurrX()+"  Y="+mScroller.getCurrY()+"  mbMotionDown="+mbMotionDown);

            if (mbMotionDown && !mIsMotionDownStillAnim) {//如果当前有按下事件，则结束滑动
                isAutoScroll = false;
                mScroller.abortAnimation();
                scrollComplete();
                return;
            }

            //这里调用View的scrollTo()完成实际的滚动
            scrollTo( mScroller.getCurrX(), mScroller.getCurrY() );
            miCurrentOffsetX = -mScroller.getCurrX();
            miCurrentOffsetY = -mScroller.getCurrY();
            //必须调用该方法，否则不一定能看到滚动效果
            postInvalidate();
            if (mScroller.getCurrX() == -miDestinationOffsetX) {
                isAutoScroll = false;
                mScroller.abortAnimation();
                scrollComplete();
                return;
            }
        } else {
            if (mScroller.isFinished()) {
                logi("computeScroll_2" );
                isAutoScroll = false;
                scrollComplete();
            }
            return;
        }
    }

    private void logi(String str) {
        //Log.i( LOG_TAG, str );
    }


    //by Bati 2018-07-25
    //刷新当前页面
    public void RefreshPageNoData(boolean bOnlyCurrent) {

        //如果当前页没有创建， 不进行刷新
        if((getCurrentPageNumber()!=0&& mPrevPageItem.mPageView == null) || mCurrentPageItem.mPageView == null || mNextPageItem.mPageView == null ){
            return;
        }
        // 正在移动
        if (mbScrolling) {
            scrollComplete();
        }
        // 用handler在下一次动作中执行,先显示当前页面.
        //这里不在请求数据,

        mAdapter.GetPageNoData(mCurrentPageItem.mPageView);

        logi("refreshpage childCount" + this.getChildCount() + " mCurrentPageItem.mPageView=" + mCurrentPageItem.mPageView );

        //刷新上下页
        if(!bOnlyCurrent) {
            mAdapter.GetPageNoData(mPrevPageItem.mPageView);
            mAdapter.GetPageNoData(mNextPageItem.mPageView);
        }
        this.invalidate();
    }


    /**
     * 预先加载下一页视图
     */
    private void CreatePageNoData(PageScrollItem pageItem) {

        // 如果已经初始化
        // 加载下一个页面视图
        PageView view = mAdapter.GetPageNoData(pageItem.mPageView);
    }

    public void SendPageNum(){
        if (null != mOnPageChangeListener) {
            logi( "Total Page Change Event." );
            int iPageCount = getPageCount();
            mOnPageChangeListener.onTotalPageNumberChange( this, mCurrentPageItem.miPageNumber, iPageCount );
            mOnPageChangeListener.onCurrentPageNumberChange( this, mCurrentPageItem.miPageNumber );
        }
    }



}
