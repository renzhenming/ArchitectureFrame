package com.rzm.commonlibrary.general.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by renzhenming on 2018/4/14.
 * <p>
 * 自定义FloatingActionButton的Behavior，后期如果有这个需求，可以仿照这个来实现
 */

public class FloatingActionButtonBehevior extends FloatingActionButton.Behavior {

    /**
     * 不设置构造方法会崩溃，可以看源码找原因
     *
     * @param context
     * @param attrs
     */
    public FloatingActionButtonBehevior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 当前FloatingActionButton是否显示
     */
    private boolean mIsShow = true;

    /**
     * 当coordinatorLayout 的子View试图开始嵌套滑动的时候被调用。当返回值为true的时候表明
     * * coordinatorLayout 充当nested scroll parent 处理这次滑动，需要注意的是只有当返回值为true
     * 的时候，Behavior 才能收到后面的一些nested scroll 事件回调（如：onNestedPreScroll、onNestedScroll等）
     * 这个方法有个重要的参数nestedScrollAxes，表明处理的滑动的方向。
     *
     * @param coordinatorLayout 和Behavior 绑定的View的父CoordinatorLayout
     * @param child             和Behavior 绑定的View
     * @param directTargetChild
     * @param target
     * @param nestedScrollAxes  嵌套滑动 应用的滑动方向，看 {@link ViewCompat#SCROLL_AXIS_HORIZONTAL},
     *                          {@link ViewCompat#SCROLL_AXIS_VERTICAL}
     * @return
     */
    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
        // nestedScrollAxes 滑动关联的轴，我们只关心垂直的滑动
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    /**
     * 进行嵌套滚动时被调用
     *
     * @param coordinatorLayout
     * @param child
     * @param target
     * @param dxConsumed        target 已经消费的x方向的距离
     * @param dyConsumed        target 已经消费的y方向的距离
     * @param dxUnconsumed      x 方向剩下的滚动距离
     * @param dyUnconsumed      y 方向剩下的滚动距离
     */
    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        // 根据情况执行动画 一个显示 一个是影藏
        if (dyConsumed > 0) {
            //向上滑动
            if (mIsShow) {
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
                child.animate().translationY(params.bottomMargin + child.getMeasuredHeight()).setDuration(400).start();
                mIsShow = false;
            }
        } else {
            //向下滑动
            if (!mIsShow) {
                child.animate().translationY(0).setDuration(400).start();
                mIsShow = true;
            }
        }

    }


    /**
     * 表示是否给应用了Behavior 的View 指定一个依赖的布局，通常，当依赖的View 布局发生变化时 *
     * 不管被被依赖View 的顺序怎样，被依赖的View也会重新布局 *
     * @param parent *
     * @param child 绑定behavior 的View *
     * @param dependency 依赖的view *
     * @return 如果child 是依赖的指定的View 返回true,否则返回false
     */
    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        return super.layoutDependsOn(parent, child, dependency);
    }

    /**
     * 当被依赖的View 状态（如：位置、大小）发生变化时，这个方法被调用 *
     * @param parent *
     * @param child *
     * @param dependency *
     * @return
     */
    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        return super.onDependentViewChanged(parent, child, dependency);
    }
    /**
     * 嵌套滚动发生之前被调用 *
     * 在nested scroll child 消费掉自己的滚动距离之前，嵌套滚动每次被nested scroll child *
     * 更新都会调用onNestedPreScroll。注意有个重要的参数consumed，可以修改这个数组表示你消费 *
     * 了多少距离。假设用户滑动了100px,child 做了90px 的位移，你需要把 consumed［1］的值改成90， *
     * 这样coordinatorLayout就能知道只处理剩下的10px的滚动。 *
     * @param coordinatorLayout *
     * @param child *
     * @param target *
     * @param dx 用户水平方向的滚动距离 *
     * @param dy 用户竖直方向的滚动距离 *
     * @param consumed
     */
    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dx, int dy, int[] consumed) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
    }

    /**
     * 嵌套滚动结束时被调用，这是一个清除滚动状态等的好时机。 *
     * @param coordinatorLayout *
     * @param child *
     * @param target
     */
    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target) {
        super.onStopNestedScroll(coordinatorLayout, child, target);
    }

    /**
     * onStartNestedScroll返回true才会触发这个方法，接受滚动处理后回调，可以在这个 *
     * 方法里做一些准备工作，如一些状态的重置等。 *
     * @param coordinatorLayout *
     * @param child *
     * @param directTargetChild *
     * @param target *
     * @param nestedScrollAxes
     */
    @Override
    public void onNestedScrollAccepted(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
        super.onNestedScrollAccepted(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    /**
     * 用户松开手指并且会发生惯性动作之前调用，参数提供了速度信息，可以根据这些速度信息 *
     * 决定最终状态，比如滚动Header，是让Header处于展开状态还是折叠状态。返回true 表 *
     * 示消费了fling. * *
     * @param coordinatorLayout *
     * @param child * @param target *
     * @param velocityX x 方向的速度 *
     * @param velocityY y 方向的速度 *
     * @return
     */
    @Override
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, float velocityX, float velocityY) {
        return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY);
    }

    /**
     * 摆放子 View 的时候调用，可以重写这个方法对子View 进行重新布局
     */
    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, FloatingActionButton child, int layoutDirection) {
        return super.onLayoutChild(parent, child, layoutDirection);
    }

}
