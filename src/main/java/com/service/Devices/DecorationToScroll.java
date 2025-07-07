package com.service.Devices;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class DecorationToScroll extends RecyclerView.ItemDecoration {
        private final int paddingRight;

        public DecorationToScroll(int paddingRight) {
            this.paddingRight = paddingRight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.right = paddingRight;
        }
    }
