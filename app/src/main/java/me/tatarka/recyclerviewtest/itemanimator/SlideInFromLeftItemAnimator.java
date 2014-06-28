package me.tatarka.recyclerviewtest.itemanimator;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

public class SlideInFromLeftItemAnimator extends BaseItemAnimator {
    private View parent;

    public SlideInFromLeftItemAnimator(View parent) {
        this.parent = parent;
    }

    @Override
    public PendingAnimator.Add onAdd(RecyclerView.ViewHolder viewHolder) {
        final View v = viewHolder.itemView;
        v.setTranslationX(-parent.getWidth());

        return new PendingAnimator.Add(viewHolder) {
            @Override
            void animate(OnAnimatorEnd callback) {
                v.animate().setDuration(getAddDuration()).translationX(0)
                        .setInterpolator(new DecelerateInterpolator())
                        .setListener(callback);
            }

            @Override
            void cancel() {
                v.animate().cancel();
                v.setTranslationX(0);
            }
        };
    }

    @Override
    public PendingAnimator.Remove onRemove(RecyclerView.ViewHolder viewHolder) {
        final View v = viewHolder.itemView;
        return new PendingAnimator.Remove(viewHolder) {
            @Override
            void animate(OnAnimatorEnd callback) {
                v.animate().setDuration(getRemoveDuration()).translationX(-parent.getWidth())
                        .setInterpolator(new AccelerateInterpolator())
                        .setListener(callback);
            }

            @Override
            void cancel() {
                v.animate().cancel();
            }
        };
    }

    @Override
    public PendingAnimator.Move onMove(RecyclerView.ViewHolder viewHolder, int fromX, int fromY, int toX, int toY) {
        final View v = viewHolder.itemView;
        v.setTranslationX(fromX - toX);
        v.setTranslationY(fromY - toY);

        return new PendingAnimator.Move(viewHolder) {
            @Override
            void animate(OnAnimatorEnd callback) {
                v.animate().setDuration(getMoveDuration()).translationX(0).translationY(0)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .setListener(callback);
            }

            @Override
            void cancel() {
                v.animate().cancel();
                v.setTranslationX(0);
                v.setTranslationY(0);
            }
        };
    }
}
