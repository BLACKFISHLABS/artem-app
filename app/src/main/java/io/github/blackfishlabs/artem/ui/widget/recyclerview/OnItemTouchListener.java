package io.github.blackfishlabs.artem.ui.widget.recyclerview;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OnItemTouchListener implements RecyclerView.OnItemTouchListener {

    private RecyclerView mRecyclerView;
    private GestureDetector mGestureDetector;
    private OnItemClickListener mListener;

    public OnItemTouchListener(
            @NonNull Context context, @NonNull RecyclerView recyclerView,
            @NonNull OnItemClickListener listener) {
        mRecyclerView = recyclerView;
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        mGestureDetector.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            notifyEvent(e, true);
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            notifyEvent(e, false);
        }

        private void notifyEvent(MotionEvent e, boolean isClick) {
            final View child = mRecyclerView.findChildViewUnder(e.getX(), e.getY());
            final int position = mRecyclerView.getChildAdapterPosition(child);

            if (isClick) {
                mListener.onSingleTapUp(child, position);
            } else {
                mListener.onLongPress(child, position);
            }
        }
    }
}