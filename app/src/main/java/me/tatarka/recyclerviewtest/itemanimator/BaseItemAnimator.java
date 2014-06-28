package me.tatarka.recyclerviewtest.itemanimator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseItemAnimator extends RecyclerView.ItemAnimator {
    private List<PendingAnimator> pendingAnimations = new ArrayList<PendingAnimator>();
    private List<PendingAnimator> runningAnimations = new ArrayList<PendingAnimator>();
    private int removePendingCount;
    private int movePendingCount;

    public abstract PendingAnimator.Add onAdd(RecyclerView.ViewHolder viewHolder);
    public abstract PendingAnimator.Remove onRemove(RecyclerView.ViewHolder viewHolder);
    public abstract PendingAnimator.Move onMove(RecyclerView.ViewHolder viewHolder, int fromX, int fromY, int toX, int toY);

    @Override
    public void runPendingAnimations() {
        if (pendingAnimations.isEmpty()) return;

        for (final PendingAnimator animation: pendingAnimations) {
            runningAnimations.add(animation);
            if (animation instanceof PendingAnimator.Remove) {
                ViewCompat.postOnAnimation(animation.viewHolder.itemView, new Runnable() {
                    @Override
                    public void run() {
                        animation.animate(new OnRemoveAnimatorEnd(animation));
                    }
                });
            } else if (animation instanceof PendingAnimator.Add) {
                int delay = 0;
                if (movePendingCount > 0) delay += getMoveDuration();
                if (removePendingCount > 0) delay += getRemoveDuration();
                ViewCompat.postOnAnimationDelayed(animation.viewHolder.itemView, new Runnable() {
                    @Override
                    public void run() {
                        animation.animate(new OnAddAnimatorEnd(animation));
                    }
                }, delay);
            } else if (animation instanceof PendingAnimator.Move) {
                int delay = 0;
                if (removePendingCount > 0) delay += getRemoveDuration();
                ViewCompat.postOnAnimationDelayed(animation.viewHolder.itemView, new Runnable() {
                    @Override
                    public void run() {
                        animation.animate(new OnMoveAnimatorEnd(animation));
                    }
                }, delay);
            }
        }

        removePendingCount = 0;
        movePendingCount = 0;
        pendingAnimations.clear();
    }

    @Override
    public boolean animateAdd(final RecyclerView.ViewHolder viewHolder) {
        pendingAnimations.add(onAdd(viewHolder));
        return true;
    }

    @Override
    public boolean animateRemove(RecyclerView.ViewHolder viewHolder) {
        removePendingCount++;
        pendingAnimations.add(onRemove(viewHolder));
        return true;
    }

    @Override
    public boolean animateMove(RecyclerView.ViewHolder viewHolder, int fromX, int fromY, int toX, int toY) {
        movePendingCount++;
        pendingAnimations.add(onMove(viewHolder, fromX, fromY, toX, toY));
        return true;
    }

    @Override
    public void endAnimation(RecyclerView.ViewHolder viewHolder) {
        for (PendingAnimator animation: pendingAnimations) {
            if (animation.viewHolder == viewHolder) {
                removeAnimation(animation);
                pendingAnimations.remove(animation);
                break;
            }
        }
        for (PendingAnimator animation: runningAnimations) {
            if (animation.viewHolder == viewHolder) {
                animation.cancel();
                removeAnimation(animation);
                runningAnimations.remove(animation);
                break;
            }
        }
        dispatchFinishedWhenDone();
    }

    @Override
    public void endAnimations() {
        for (PendingAnimator animation : pendingAnimations) {
            removeAnimation(animation);
        }
        for (PendingAnimator animation : runningAnimations) {
            removeAnimation(animation);
        }
        movePendingCount = 0;
        removePendingCount = 0;
        pendingAnimations.clear();
        runningAnimations.clear();
        dispatchFinishedWhenDone();
    }

    private void removeAnimation(PendingAnimator animation) {
        animation.cancel();
        if (animation instanceof PendingAnimator.Add) {
            dispatchAddFinished(animation.viewHolder);
        } else if (animation instanceof PendingAnimator.Remove) {
            removePendingCount--;
            dispatchRemoveFinished(animation.viewHolder);
        } else if (animation instanceof PendingAnimator.Move) {
            movePendingCount--;
            dispatchMoveFinished(animation.viewHolder);
        }
    }

    @Override
    public boolean isRunning() {
        return !runningAnimations.isEmpty();
    }

    private void dispatchFinishedWhenDone() {
        if (!isRunning()) {
            dispatchAnimationsFinished();
        }
    }


    public class OnAnimatorEnd extends AnimatorListenerAdapter {
        PendingAnimator animation;

        OnAnimatorEnd(PendingAnimator animation) {
            this.animation = animation;
        }

        public void onAnimationEnd() {
            runningAnimations.remove(animation);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            onAnimationEnd();
        }
    }

    private class OnRemoveAnimatorEnd extends OnAnimatorEnd {
        OnRemoveAnimatorEnd(PendingAnimator animation) {
            super(animation);
        }

        @Override
        public void onAnimationEnd() {
            super.onAnimationEnd();
            dispatchRemoveFinished(animation.viewHolder);
            dispatchFinishedWhenDone();
        }
    }

    private class OnAddAnimatorEnd extends OnAnimatorEnd {
        OnAddAnimatorEnd(PendingAnimator animation) {
            super(animation);
        }

        @Override
        public void onAnimationEnd() {
            super.onAnimationEnd();
            dispatchAddFinished(animation.viewHolder);
            dispatchFinishedWhenDone();
        }
    }

    private class OnMoveAnimatorEnd extends OnAnimatorEnd {
        OnMoveAnimatorEnd(PendingAnimator animation) {
            super(animation);
        }

        @Override
        public void onAnimationEnd() {
            super.onAnimationEnd();
            dispatchMoveFinished(animation.viewHolder);
            dispatchFinishedWhenDone();
        }
    }
}
