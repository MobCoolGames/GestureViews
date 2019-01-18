package com.alexvasilkov.gestures.transition;

import android.view.View;

import com.alexvasilkov.gestures.animation.ViewPosition;
import com.alexvasilkov.gestures.animation.ViewPositionAnimator;
import com.alexvasilkov.gestures.animation.ViewPositionAnimator.PositionUpdateListener;
import com.alexvasilkov.gestures.views.interfaces.AnimatorView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Extension of {@link ViewsCoordinator} that allows requesting {@link #enter(Object, boolean)} or
 * exit animations, keeps track of {@link PositionUpdateListener} listeners
 * and provides correct implementation of {@link #isLeaving()}.
 * <p>
 * Usage of this class should be similar to {@link ViewPositionAnimator} class.
 */
public class ViewsTransitionAnimator<ID> extends ViewsCoordinator<ID> {

    private final List<PositionUpdateListener> listeners = new ArrayList<>();

    private boolean enterWithAnimation;
    private boolean isEntered;

    public ViewsTransitionAnimator() {
        addPositionUpdateListener(new PositionUpdateListener() {
            @Override
            public void onPositionUpdate(float position, boolean isLeaving) {
                if (position == 0f && isLeaving) {
                    cleanupRequest();
                }
            }
        });
    }

    /**
     * Requests 'from' and 'to' views for given ID and starts enter animation when views are ready.
     *
     * @param id            Item ID for views lookup
     * @param withAnimation Whether to animate entering or immediately jump to entered state
     * @see ViewsCoordinator
     */
    public void enter(@NonNull ID id, boolean withAnimation) {
        enterWithAnimation = withAnimation;
        request(id);
    }

    /**
     * @return Whether 'enter' was not requested recently or animator is in leaving state.
     * Means that animation direction is from final (to) position back to initial (from) position.
     */
    public boolean isLeaving() {
        return getRequestedId() == null || (isReady() && getToView().getPositionAnimator().isLeaving());
    }


    /**
     * Adds position state changes listener that will be notified during animations.
     *
     * @param listener Position listener
     * @see ViewPositionAnimator#addPositionUpdateListener(PositionUpdateListener)
     */
    public void addPositionUpdateListener(@NonNull PositionUpdateListener listener) {
        listeners.add(listener);
        if (isReady()) {
            getToView().getPositionAnimator().addPositionUpdateListener(listener);
        }
    }

    @Override
    public void setFromListener(@NonNull OnRequestViewListener<ID> listener) {
        super.setFromListener(listener);
        if (listener instanceof RequestListener) {
            ((RequestListener<ID>) listener).initAnimator(this);
        }
    }

    @Override
    public void setToListener(@NonNull OnRequestViewListener<ID> listener) {
        super.setToListener(listener);
        if (listener instanceof RequestListener) {
            ((RequestListener<ID>) listener).initAnimator(this);
        }
    }

    @Override
    protected void onFromViewChanged(@Nullable View fromView, @Nullable ViewPosition fromPos) {
        super.onFromViewChanged(fromView, fromPos);

        if (isReady()) {
            if (fromView != null) {
                getToView().getPositionAnimator().update(fromView);
            } else if (fromPos != null) {
                getToView().getPositionAnimator().update(fromPos);
            } else {
                getToView().getPositionAnimator().updateToNone();
            }
        }
    }

    @Override
    protected void onToViewChanged(@Nullable AnimatorView old, @NonNull AnimatorView view) {
        super.onToViewChanged(old, view);

        if (isReady() && old != null) {
            // Animation is in place, we should carefully swap animators
            swapAnimator(old.getPositionAnimator(), view.getPositionAnimator());
        } else {
            if (old != null) {
                cleanupAnimator(old.getPositionAnimator());
            }
            initAnimator(view.getPositionAnimator());
        }
    }

    @Override
    protected void cleanupRequest() {
        if (getToView() != null) {
            cleanupAnimator(getToView().getPositionAnimator());
        }

        isEntered = false;

        super.cleanupRequest();
    }


    private void initAnimator(ViewPositionAnimator animator) {
        for (PositionUpdateListener listener : listeners) {
            animator.addPositionUpdateListener(listener);
        }
    }

    private void cleanupAnimator(ViewPositionAnimator animator) {
        for (PositionUpdateListener listener : listeners) {
            animator.removePositionUpdateListener(listener);
        }
    }

    // Replaces old animator with new one preserving state.
    private void swapAnimator(ViewPositionAnimator old, ViewPositionAnimator next) {
        final float position = old.getPosition();
        final boolean isLeaving = old.isLeaving();
        final boolean isAnimating = old.isAnimating();

        cleanupAnimator(old);

        if (getFromView() != null) {
            next.enter(getFromView(), false);
        } else if (getFromPos() != null) {
            next.enter(getFromPos(), false);
        }

        initAnimator(next);

        next.setState(position, isLeaving, isAnimating);
    }


    public abstract static class RequestListener<ID> implements OnRequestViewListener<ID> {
        private ViewsTransitionAnimator<ID> animator;

        protected void initAnimator(ViewsTransitionAnimator<ID> animator) {
            this.animator = animator;
        }

        protected ViewsTransitionAnimator<ID> getAnimator() {
            return animator;
        }
    }

}
