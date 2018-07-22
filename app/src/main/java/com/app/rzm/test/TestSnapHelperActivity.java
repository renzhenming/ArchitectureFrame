package com.app.rzm.test;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.rzm.R;

import java.util.ArrayList;
import java.util.List;

public class TestSnapHelperActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private SnapAdapter mSnapAdapter;
    private List<String> mList = new ArrayList<>();
    private SnapToStartHelper mPagerSnapHeler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_snap_helper);
        mList.add("0");
        mList.add("1");
        mList.add("2");
        mList.add("3");
        mList.add("4");
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        mSnapAdapter = new SnapAdapter();
        mPagerSnapHeler = new SnapToStartHelper();
        mPagerSnapHeler.attachToRecyclerView(mRecyclerView);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.left = 15;
                outRect.right = 15;
            }
        });
        mRecyclerView.setAdapter(mSnapAdapter);
        new SnapHelper() {
            @Nullable
            @Override
            public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView) {
                return new int[0];
            }

            @Nullable
            @Override
            public View findSnapView(RecyclerView.LayoutManager layoutManager) {
                return null;
            }

            @Override
            public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
                return 0;
            }
        };
    }

    class SnapToStartHelper extends PagerSnapHelper{

        private OrientationHelper horizontalHelper;
        private OrientationHelper verticalHelper;

        @Nullable
        @Override
        public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView) {
            int[] out = new int[2];
            if (layoutManager.canScrollHorizontally()){
                out[0] = distanceToStart(targetView,getHorizontalHelper(layoutManager));
            }else{
                out[0]=0;
            }

            if (layoutManager.canScrollVertically()){
                out[1] = distanceToStart(targetView,getVerticalHelper(layoutManager));
            }else{
                out[1] =0;
            }
            return out;
        }

        @Override
        public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
            System.out.println("velocityX:"+velocityX+",velocityY:"+velocityY);
            int paddingLeft = layoutManager.getPaddingLeft();
            System.out.println("paddingLeft:"+paddingLeft);
            View childAt = layoutManager.getChildAt(0);
            int left = childAt.getLeft();
            System.out.println("left:"+left);
            if (left == 15){
                return RecyclerView.NO_POSITION;
            }
            return super.findTargetSnapPosition(layoutManager, velocityX, velocityY);
        }

        /**
         * getDecoratedStart
         *
         * Returns the start of the view including its decoration and margin.
         * <p>
         * For example, for the horizontal helper, if a View's left is at pixel 20, has 2px left
         * decoration and 3px left margin, returned value will be 15px.

         */
        private int distanceToStart(View targetView, OrientationHelper helper) {
            System.out.println("helper.getDecoratedStart(targetView):"+helper.getDecoratedStart(targetView)+" helper.getStartAfterPadding():"+helper.getStartAfterPadding());
            return helper.getDecoratedStart(targetView) - helper.getStartAfterPadding();
        }
        public OrientationHelper getVerticalHelper(RecyclerView.LayoutManager manager){
            if (verticalHelper == null)
                verticalHelper = OrientationHelper.createVerticalHelper(manager);
            return verticalHelper;
        }
        public OrientationHelper getHorizontalHelper(RecyclerView.LayoutManager manager){
            if (horizontalHelper == null)
                horizontalHelper = OrientationHelper.createHorizontalHelper(manager);
            return horizontalHelper;
        }
    }

    class SnapAdapter extends RecyclerView.Adapter{

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(TestSnapHelperActivity.this).inflate(R.layout.item_test_snap_helper,parent,false);
            SnapHolder holder = new SnapHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            SnapHolder snapHolder = (SnapHolder) holder;
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        class SnapHolder extends RecyclerView.ViewHolder{

            public SnapHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
