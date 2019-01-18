package com.alexvasilkov.gestures.transition;

import android.view.View;

import com.alexvasilkov.gestures.animation.ViewPosition;
import com.alexvasilkov.gestures.views.interfaces.AnimatorView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

/**
 * Main purpose of this class is to synchronize views of same item in two different sources
 * to correctly start or update view transition animation.
 * <p>
 * I.e. we need to have both 'to' and 'from' views which represent same item in
 * {@link RecyclerView} and {@link ViewPager} to start transition between them.
 * But when {@link ViewPager} is scrolled we may also need to scroll {@link RecyclerView} to reveal
 * corresponding item's view.
 * <p>
 * Method {@link #request(Object)} should be called when particular item needs to be synced.
 * This method will trigger methods {@link OnRequestViewListener#onRequestView(Object)} of
 * listeners set by {@link #setFromListener(OnRequestViewListener) setFromListener} and
 * {@link #setToListener(OnRequestViewListener) setToListener} methods.
 */
public class ViewsCoordinator<ID> {

    private OnRequestViewListener<ID> fromListener;
    private OnRequestViewListener<ID> toListener;

    private ID requestedId;
    private ID fromId;
    private ID toId;

    private View fromView;
    private ViewPosition fromPos;
    private AnimatorView toView;

    public void setFromListener(@NonNull OnRequestViewListener<ID> listener) {
        fromListener = listener;
    }

    public void setToListener(@NonNull OnRequestViewListener<ID> listener) {
        toListener = listener;
    }

    public void request(@NonNull ID id) {
        if (fromListener == null) {
            throw new RuntimeException("'from' listener is not set");
        }
        if (toListener == null) {
            throw new RuntimeException("'to' listener is not set");
        }

        cleanupRequest();

        requestedId = id;
        fromListener.onRequestView(id);
        toListener.onRequestView(id);
    }

    public ID getRequestedId() {
        return requestedId;
    }

    public View getFromView() {
        return fromView;
    }

    public ViewPosition getFromPos() {
        return fromPos;
    }

    public AnimatorView getToView() {
        return toView;
    }

    protected void onFromViewChanged(@Nullable View fromView, @Nullable ViewPosition fromPos) {
        // Can be overridden to setup views
    }

    protected void onToViewChanged(@Nullable AnimatorView old, @NonNull AnimatorView view) {
        // Can be overridden to setup views
    }


    public boolean isReady() {
        return requestedId != null && requestedId.equals(fromId) && requestedId.equals(toId);
    }

    protected void cleanupRequest() {
        if (requestedId == null) {
            return;
        }

        fromView = null;
        fromPos = null;
        toView = null;
        requestedId = fromId = toId = null;
    }

    public interface OnRequestViewListener<ID> {
        /**
         * Implementation should find corresponding {@link View} (or {@link ViewPosition})
         * for given {@code index} and provide it back to {@link ViewsCoordinator}.
         * <p>
         * Note, that it may not be possible to provide view right now (i.e. because
         * we should scroll source view to reveal correct view), but it should be provided
         * as soon as it's ready.
         *
         * @param id Item ID for views lookup
         */
        void onRequestView(@NonNull ID id);
    }
}
