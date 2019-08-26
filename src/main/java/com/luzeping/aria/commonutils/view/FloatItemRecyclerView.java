package com.luzeping.aria.commonutils.view;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * 可添加浮层的RecyclerView
 * FloatView需要自己定义位置和大小
 * @param <V>
 */
public class FloatItemRecyclerView<V extends RecyclerView> extends FrameLayout {

    private View floatView;

    private V recyclerView;

    private int currentState = -1;

    private int orientation = LinearLayoutManager.HORIZONTAL;

    private View needFloatChild = null;

    private OnFloatViewShowListener onFloatViewShowListener;

    private FloatViewShowHook<V> floatViewShowHook;

    private int FLOAT_VISIBILITY = View.GONE;

    public FloatItemRecyclerView(@NonNull Context context) {
        super(context);
    }

    public FloatItemRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FloatItemRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(FloatViewShowHook<V> floatViewShowHook,View floatView) {
        setFloatViewShowHook(floatViewShowHook);
        setFloatView(floatView);
    }

    public void init(FloatViewShowHook<V> floatViewShowHook,View floatView,int visibility) {
        setFloatItemInvisibleState(visibility);
        setFloatViewShowHook(floatViewShowHook);
        setFloatView(floatView);
    }

    public void setAdatper(RecyclerView.Adapter adatper) {
        if (recyclerView != null)
            recyclerView.setAdapter(adatper);
    }

    public void setFloatViewShowHook(FloatViewShowHook<V> floatViewShowHook) {
        this.floatViewShowHook = floatViewShowHook;
        recyclerView = floatViewShowHook.initFloatItemRecyclerView();
        addRecyclerView();
        if (floatView != null) {
            bringChildToFront(floatView);
            updateViewLayout(floatView,floatView.getLayoutParams());
        }
    }

    public void setOnFloatViewShowListener(OnFloatViewShowListener onFloatViewShowListener) {
        this.onFloatViewShowListener = onFloatViewShowListener;
    }

    public void setFloatView(View floatView) {
        this.floatView = floatView;
        if (floatView.getLayoutParams() == null)
            floatView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(this.floatView);
        floatView.setVisibility(FLOAT_VISIBILITY);
    }

    private void addRecyclerView() {
        if (!(recyclerView.getLayoutManager() instanceof LinearLayoutManager)) throw new IllegalArgumentException("当前仅支持LinearLayoutManager");
        orientation = ((LinearLayoutManager) recyclerView.getLayoutManager()).getOrientation();
        addView(recyclerView,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        //设置滚动监听
        initOnScrollListener();
        initOnLayoutChangedListener();
        initOnChildAttachStateChangeListener();
    }

    private void initOnScrollListener() {
        RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (floatView == null) return;
                currentState = newState;
                switch (newState) {
                    //停止滑动
                    case 0:
                        View tempFirstChild = needFloatChild;
                        //更新浮层位置，覆盖child
                        updateFloatScrollStopTranslate();
                        //如果firstChild没有发生变化，回调floatView滑动停止的监听
                        if (tempFirstChild == needFloatChild && onFloatViewShowListener != null) {
                            onFloatViewShowListener.onScrollStopFloatView(floatView);
                        }
                        break;
                    //开始滑动
                    case 1:
                        //保存第一个child
                        //更新浮层位置
                        updateFloatScrollStartTranslate();
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (floatView == null)return;
                switch (currentState) {
                    // 停止滑动
                    case 0:
                        updateFloatScrollStopTranslate();
                        break;
                    // 开始滑动
                    case 1:
                        updateFloatScrollStartTranslate();
                        break;
                    // Fling
                    case 2:
                        updateFloatScrollStartTranslate();
                        if (onFloatViewShowListener != null) onFloatViewShowListener.onScrollFlingFloatView(floatView);
                        break;

                }
            }
        };
        recyclerView.addOnScrollListener(onScrollListener);
    }

    private void initOnChildAttachStateChangeListener() {
        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                if (view == needFloatChild) clearFirstChild(); //TODO 判断needFloatChild是否正在展示
            }
        });
    }

    private void initOnLayoutChangedListener() {
        recyclerView.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (recyclerView.getAdapter() == null)return;
                clearFirstChild();
                getFirstChild();
                updateFloatScrollStartTranslate();
                showFloatView();
            }
        });
    }

    /**
     * 找到第一个要悬浮item的
     */
    private void getFirstChild() {
        if (needFloatChild != null) return;
        int childPos = calculateShowFloatViewPosition();
        if (childPos != -1) {
            needFloatChild = recyclerView.getChildAt(childPos);
        }
    }

    private void updateFloatScrollStopTranslate() {
        if (needFloatChild == null) getFirstChild();
        updateFloatScrollStartTranslate();
        showFloatView();
    }

    private void updateFloatScrollStartTranslate() {
        if (needFloatChild != null && floatView != null) {
            float x1 = floatView.getX();
            float x2 = needFloatChild.getX();
            int translateX = needFloatChild.getLeft();
            floatView.setTranslationX(translateX);
            float x3 = floatView.getX();
            float x4 = needFloatChild.getX();
            if (onFloatViewShowListener != null) onFloatViewShowListener.onScrollFloatView(floatView);
        }
    }

    public void clearFirstChild() {
        hideFloatView();
        needFloatChild = null;
        //回调监听器
    }

    private int calculateShowFloatViewPosition() {
        if (floatViewShowHook == null) return 0;
        int firstVisiblePosition;
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            firstVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        }else {
            throw new IllegalArgumentException("only support LinearLayoutManager!!!");
        }
        int childCount = recyclerView.getChildCount(); //这里获取的是RecyclerView已显示的item数量
        for (int i = 0;i <childCount; i ++ ) {
            View child = recyclerView.getChildAt(i);
            if (child != null && floatViewShowHook.needShowFloatView(child,firstVisiblePosition +i)) return i;
        }
        return -1;
    }

    private void hideFloatView() {
        if (needFloatChild != null) {
            floatView.setVisibility(FLOAT_VISIBILITY);
            if (onFloatViewShowListener != null) onFloatViewShowListener.onHideFloatView(floatView);
        }
    }

    private void showFloatView() {
        if (needFloatChild != null ) {
            floatView.setVisibility(VISIBLE);
            // 回调显示状态的监听器
            if (onFloatViewShowListener != null) onFloatViewShowListener.
                    onShowFloatView(floatView,recyclerView.getChildAdapterPosition(needFloatChild));
        }
    }

    /**
     * 设置浮层不可见的方式 INVISIBLE 或者 GONE
     * @param visiblity
     */
    public void setFloatItemInvisibleState(int visiblity) {
        FLOAT_VISIBILITY = visiblity;
    }

    /**
     * 显示状态的回调监听器
     */
    public interface OnFloatViewShowListener {

        /**
         * FloatView被显示
         */
        void onShowFloatView(View floatView, int position);

        /**
         * FloatView被隐藏
         */
        void onHideFloatView(View floatView);

        /**
         * FloatView被移动
         */
        void onScrollFloatView(View floatView);

        /**
         * FloatView被处于Fling状态
         */
        void onScrollFlingFloatView(View floatView);

        /**
         * FloatView由滚动变为静止状态
         */
        void onScrollStopFloatView(View floatView);

    }

    /**
     * 根据item设置是否显示浮动的View
     */
    public interface FloatViewShowHook<V extends RecyclerView> {

        /**
         * 当前item是否要显示floatView
         *
         * @param child    itemView
         * @param position 在列表中的位置
         */
        boolean needShowFloatView(View child, int position);

        V initFloatItemRecyclerView();

    }
}
