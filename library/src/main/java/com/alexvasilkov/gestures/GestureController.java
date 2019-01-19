package com.alexvasilkov.gestures;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.OverScroller;

import com.alexvasilkov.gestures.internal.MovementBounds;
import com.alexvasilkov.gestures.utils.FloatScroller;
import com.alexvasilkov.gestures.utils.MathUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class GestureController implements View.OnTouchListener {

    private static final float FLING_COEFFICIENT = 0.9f;

    private static final PointF tmpPointF = new PointF();
    private static final RectF tmpRectF = new RectF();
    private static final float[] tmpPointArr = new float[2];

    private final int touchSlop;
    private final int minVelocity;
    private final int maxVelocity;

    private OnGestureListener gestureListener;
    private final List<OnStateChangeListener> stateListeners = new ArrayList<>();

    private final AnimationEngine animationEngine;

    private final GestureDetector gestureDetector;
    private final ScaleGestureDetector scaleDetector;
    private final RotationGestureDetector rotateDetector;

    private boolean isInterceptTouchCalled;
    private boolean isInterceptTouchDisallowed;
    private boolean isScrollDetected;
    private boolean isScaleDetected;
    private boolean isRotationDetected;

    private float pivotX = Float.NaN;
    private float pivotY = Float.NaN;
    private float endPivotX = Float.NaN;
    private float endPivotY = Float.NaN;

    private boolean isStateChangedDuringTouch;
    private boolean isRestrictZoomRequested;
    private boolean isRestrictRotationRequested;
    private boolean isAnimatingInBounds;

    private final OverScroller flingScroller;
    private final FloatScroller stateScroller;

    private final MovementBounds flingBounds;
    private final State stateStart = new State();
    private final State stateEnd = new State();

    private final View targetView;
    private final Settings settings;
    private final State state = new State();
    private final State prevState = new State();
    private final StateController stateController;

    public GestureController(@NonNull View view) {
        final Context context = view.getContext();

        targetView = view;
        settings = new Settings();
        stateController = new StateController(settings);

        animationEngine = new LocalAnimationEngine(view);
        InternalGesturesListener internalListener = new InternalGesturesListener();
        gestureDetector = new GestureDetector(context, internalListener);
        scaleDetector = new ScaleGestureDetectorFixed(context, internalListener);
        rotateDetector = new RotationGestureDetector(internalListener);

        flingScroller = new OverScroller(context);
        stateScroller = new FloatScroller();

        flingBounds = new MovementBounds(settings);

        final ViewConfiguration configuration = ViewConfiguration.get(context);
        touchSlop = configuration.getScaledTouchSlop();
        minVelocity = configuration.getScaledMinimumFlingVelocity();
        maxVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    public void setOnGesturesListener(@Nullable OnGestureListener listener) {
        gestureListener = listener;
    }

    public void addOnStateChangeListener(@NonNull OnStateChangeListener listener) {
        stateListeners.add(listener);
    }

    public void removeOnStateChangeListener(OnStateChangeListener listener) {
        stateListeners.remove(listener);
    }

    public Settings getSettings() {
        return settings;
    }

    public State getState() {
        return state;
    }

    public StateController getStateController() {
        return stateController;
    }

    public void updateState() {
        stateController.applyZoomPatch(state);
        stateController.applyZoomPatch(prevState);
        stateController.applyZoomPatch(stateStart);
        stateController.applyZoomPatch(stateEnd);

        boolean reset = stateController.updateState(state);
        if (reset) {
            notifyStateReset();
        } else {
            notifyStateUpdated();
        }
    }

    public void resetState() {
        stopAllAnimations();
        boolean reset = stateController.resetState(state);
        if (reset) {
            notifyStateReset();
        } else {
            notifyStateUpdated();
        }
    }

    private void animateKeepInBounds() {
        animateStateTo(state, true);
    }

    private void animateStateTo(@Nullable State endState) {
        animateStateTo(endState, true);
    }

    private void animateStateTo(@Nullable State endState, boolean keepInBounds) {
        if (endState == null) {
            return;
        }

        State endStateRestricted = null;
        if (keepInBounds) {
            endStateRestricted = stateController.restrictStateBoundsCopy(endState, prevState, pivotX, pivotY);
        }
        if (endStateRestricted == null) {
            endStateRestricted = endState;
        }

        if (endStateRestricted.equals(state)) {
            return;
        }

        stopAllAnimations();

        isAnimatingInBounds = keepInBounds;
        stateStart.set(state);
        stateEnd.set(endStateRestricted);

        if (!Float.isNaN(pivotX) && !Float.isNaN(pivotY)) {
            tmpPointArr[0] = pivotX;
            tmpPointArr[1] = pivotY;
            MathUtils.computeNewPosition(tmpPointArr, stateStart, stateEnd);
            endPivotX = tmpPointArr[0];
            endPivotY = tmpPointArr[1];
        }

        stateScroller.startScroll(0f, 1f);
        animationEngine.start();

    }

    private boolean isAnimatingState() {
        return !stateScroller.isFinished();
    }

    private boolean isAnimatingFling() {
        return !flingScroller.isFinished();
    }

    private void stopStateAnimation() {
        if (isAnimatingState()) {
            stateScroller.forceFinished();
            onStateAnimationFinished();
        }
    }

    private void stopFlingAnimation() {
        if (isAnimatingFling()) {
            flingScroller.forceFinished(true);
            onFlingAnimationFinished(true);
        }
    }

    private void stopAllAnimations() {
        stopStateAnimation();
        stopFlingAnimation();
    }

    private void onStateAnimationFinished() {
        isAnimatingInBounds = false;
        pivotX = Float.NaN;
        pivotY = Float.NaN;
    }

    private void onFlingAnimationFinished(boolean forced) {
        if (!forced) {
            animateKeepInBounds();
        }
    }

    private void notifyStateUpdated() {
        prevState.set(state);
        for (OnStateChangeListener listener : stateListeners) {
            listener.onStateChanged(state);
        }
    }

    private void notifyStateReset() {
        for (OnStateChangeListener listener : stateListeners) {
            listener.onStateReset(prevState, state);
        }
        notifyStateUpdated();
    }

    @Override
    public boolean onTouch(@NonNull View view, @NonNull MotionEvent event) {
        if (!isInterceptTouchCalled) {
            onTouchInternal(view, event);
        }
        isInterceptTouchCalled = false;
        return settings.isEnabled();
    }

    private void onTouchInternal(@NonNull View view, @NonNull MotionEvent event) {
        MotionEvent viewportEvent = MotionEvent.obtain(event);
        viewportEvent.offsetLocation(-view.getPaddingLeft(), -view.getPaddingTop());

        gestureDetector.setIsLongpressEnabled(view.isLongClickable());
        gestureDetector.onTouchEvent(viewportEvent);
        scaleDetector.onTouchEvent(viewportEvent);
        rotateDetector.onTouchEvent(viewportEvent);

        if (isStateChangedDuringTouch) {
            isStateChangedDuringTouch = false;

            stateController.restrictStateBounds(state, prevState, pivotX, pivotY, true, false);

            if (!state.equals(prevState)) {
                notifyStateUpdated();
            }
        }

        if (isRestrictZoomRequested || isRestrictRotationRequested) {
            isRestrictZoomRequested = false;
            isRestrictRotationRequested = false;

            State restrictedState = stateController.restrictStateBoundsCopy(state, prevState, pivotX, pivotY);
            animateStateTo(restrictedState, false);
        }

        if (viewportEvent.getActionMasked() == MotionEvent.ACTION_UP || viewportEvent.getActionMasked() == MotionEvent.ACTION_CANCEL) {
            onUpOrCancel(viewportEvent);
        }

        if (!isInterceptTouchDisallowed && shouldDisallowInterceptTouch(viewportEvent)) {
            isInterceptTouchDisallowed = true;

            final ViewParent parent = view.getParent();
            if (parent != null) {
                parent.requestDisallowInterceptTouchEvent(true);
            }
        }

        viewportEvent.recycle();

    }

    private boolean shouldDisallowInterceptTouch(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE: {
                stateController.getMovementArea(state, tmpRectF);
                final boolean isPannable = State.compare(tmpRectF.width()) > 0 || State.compare(tmpRectF.height()) > 0;

                if (isPannable) {
                    return true;
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN: {
                return settings.isZoomEnabled() || settings.isRotationEnabled();
            }
            default:
        }

        return false;
    }

    private boolean onDown(@NonNull MotionEvent event) {
        isInterceptTouchDisallowed = false;

        stopFlingAnimation();

        if (gestureListener != null) {
            gestureListener.onDown(event);
        }

        return false;
    }

    private void onUpOrCancel(@NonNull MotionEvent event) {
        isScrollDetected = false;
        isScaleDetected = false;
        isRotationDetected = false;

        if (!isAnimatingFling() && !isAnimatingInBounds) {
            animateKeepInBounds();
        }

        if (gestureListener != null) {
            gestureListener.onUpOrCancel(event);
        }
    }

    private boolean onSingleTapUp(@NonNull MotionEvent event) {
        if (!settings.isDoubleTapEnabled()) {
            targetView.performClick();
        }
        return gestureListener != null && gestureListener.onSingleTapUp(event);
    }

    private void onLongPress(@NonNull MotionEvent event) {
        if (settings.isEnabled()) {
            targetView.performLongClick();

            if (gestureListener != null) {
                gestureListener.onLongPress(event);
            }
        }
    }

    private boolean onScroll(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float dx, float dy) {
        if (isAnimatingState()) {
            return false;
        }

        if (!isScrollDetected) {
            isScrollDetected = Math.abs(e2.getX() - e1.getX()) > touchSlop || Math.abs(e2.getY() - e1.getY()) > touchSlop;

            if (isScrollDetected) {
                return false;
            }
        }

        if (isScrollDetected) {
            state.translateBy(-dx, -dy);
            isStateChangedDuringTouch = true;
        }

        return isScrollDetected;
    }

    private boolean onFling(float vx, float vy) {
        if (isAnimatingState()) {
            return false;
        }

        stopFlingAnimation();
        flingBounds.set(state).extend(state.getX(), state.getY());
        flingScroller.fling(
                Math.round(state.getX()), Math.round(state.getY()),
                limitFlingVelocity(vx * FLING_COEFFICIENT),
                limitFlingVelocity(vy * FLING_COEFFICIENT),
                Integer.MIN_VALUE, Integer.MAX_VALUE,
                Integer.MIN_VALUE, Integer.MAX_VALUE);

        animationEngine.start();
        return true;
    }

    private int limitFlingVelocity(float velocity) {
        if (Math.abs(velocity) < minVelocity) {
            return 0;
        } else if (Math.abs(velocity) >= maxVelocity) {
            return (int) Math.signum(velocity) * maxVelocity;
        } else {
            return Math.round(velocity);
        }
    }

    private boolean onFlingScroll(int dx, int dy) {
        float prevX = state.getX();
        float prevY = state.getY();
        float toX = prevX + dx;
        float toY = prevY + dy;

        flingBounds.restrict(toX, toY, tmpPointF);
        toX = tmpPointF.x;
        toY = tmpPointF.y;

        state.translateTo(toX, toY);
        return !State.equals(prevX, toX) || !State.equals(prevY, toY);
    }

    private boolean onSingleTapConfirmed(MotionEvent event) {
        if (settings.isDoubleTapEnabled()) {
            targetView.performClick();
        }

        return gestureListener != null && gestureListener.onSingleTapConfirmed(event);
    }

    private boolean onDoubleTapEvent(MotionEvent event) {
        if (!settings.isDoubleTapEnabled()) {
            return false;
        }

        if (event.getActionMasked() != MotionEvent.ACTION_UP) {
            return false;
        }

        if (isScaleDetected) {
            return false;
        }

        if (gestureListener != null && gestureListener.onDoubleTap(event)) {
            return true;
        }

        animateStateTo(stateController.toggleMinMaxZoom(state, event.getX(), event.getY()));
        return true;
    }

    private boolean onScaleBegin() {
        isScaleDetected = settings.isZoomEnabled();
        return isScaleDetected;
    }

    private boolean onScale(ScaleGestureDetector detector) {
        if (!settings.isZoomEnabled() || isAnimatingState()) {
            return false;
        }

        final float scaleFactor = detector.getScaleFactor();
        pivotX = detector.getFocusX();
        pivotY = detector.getFocusY();
        state.zoomBy(scaleFactor, pivotX, pivotY);
        isStateChangedDuringTouch = true;
        return true;
    }

    private void onScaleEnd() {
        isScaleDetected = false;
        isRestrictZoomRequested = true;
    }

    private boolean onRotationBegin() {
        isRotationDetected = settings.isRotationEnabled();
        return isRotationDetected;
    }

    private boolean onRotate(RotationGestureDetector detector) {
        if (!settings.isRotationEnabled() || isAnimatingState()) {
            return false;
        }

        pivotX = detector.getFocusX();
        pivotY = detector.getFocusY();
        state.rotateBy(detector.getRotationDelta(), pivotX, pivotY);
        isStateChangedDuringTouch = true;
        return true;
    }

    private void onRotationEnd() {
        isRotationDetected = false;
        isRestrictRotationRequested = true;
    }

    private class LocalAnimationEngine extends AnimationEngine {
        LocalAnimationEngine(@NonNull View view) {
            super(view);
        }

        @Override
        public boolean onStep() {
            boolean shouldProceed = false;

            if (isAnimatingFling()) {
                int prevX = flingScroller.getCurrX();
                int prevY = flingScroller.getCurrY();

                if (flingScroller.computeScrollOffset()) {
                    int dx = flingScroller.getCurrX() - prevX;
                    int dy = flingScroller.getCurrY() - prevY;

                    if (!onFlingScroll(dx, dy)) {
                        stopFlingAnimation();
                    }

                    shouldProceed = true;
                }

                if (!isAnimatingFling()) {
                    onFlingAnimationFinished(false);
                }
            }

            if (isAnimatingState()) {
                stateScroller.computeScroll();
                float factor = stateScroller.getCurr();

                if (Float.isNaN(pivotX) || Float.isNaN(pivotY) || Float.isNaN(endPivotX) || Float.isNaN(endPivotY)) {
                    MathUtils.interpolate(state, stateStart, stateEnd, factor);
                } else {
                    MathUtils.interpolate(state, stateStart, pivotX, pivotY, stateEnd, endPivotX, endPivotY, factor);
                }

                shouldProceed = true;

                if (!isAnimatingState()) {
                    onStateAnimationFinished();
                }
            }

            if (shouldProceed) {
                notifyStateUpdated();
            }

            return shouldProceed;
        }
    }

    public interface OnStateChangeListener {
        void onStateChanged(State state);

        void onStateReset(State oldState, State newState);
    }

    public interface OnGestureListener {
        void onDown(@NonNull MotionEvent event);

        void onUpOrCancel(@NonNull MotionEvent event);

        boolean onSingleTapUp(@NonNull MotionEvent event);

        boolean onSingleTapConfirmed(@NonNull MotionEvent event);

        void onLongPress(@NonNull MotionEvent event);

        boolean onDoubleTap(@NonNull MotionEvent event);
    }

    private class InternalGesturesListener implements
            GestureDetector.OnGestureListener,
            GestureDetector.OnDoubleTapListener,
            ScaleGestureDetector.OnScaleGestureListener,
            RotationGestureDetector.OnRotationGestureListener {

        @Override
        public boolean onSingleTapConfirmed(@NonNull MotionEvent event) {
            return GestureController.this.onSingleTapConfirmed(event);
        }

        @Override
        public boolean onDoubleTap(@NonNull MotionEvent event) {
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(@NonNull MotionEvent event) {
            return GestureController.this.onDoubleTapEvent(event);
        }

        @Override
        public boolean onDown(@NonNull MotionEvent event) {
            return GestureController.this.onDown(event);
        }

        @Override
        public void onShowPress(@NonNull MotionEvent event) {
        }

        @Override
        public boolean onSingleTapUp(@NonNull MotionEvent event) {
            return GestureController.this.onSingleTapUp(event);
        }

        @Override
        public boolean onScroll(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
            return GestureController.this.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public void onLongPress(@NonNull MotionEvent event) {
            GestureController.this.onLongPress(event);
        }

        @Override
        public boolean onFling(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
            return GestureController.this.onFling(velocityX, velocityY);
        }

        @Override
        public boolean onRotate(@NonNull RotationGestureDetector detector) {
            return GestureController.this.onRotate(detector);
        }

        @Override
        public boolean onRotationBegin(@NonNull RotationGestureDetector detector) {
            return GestureController.this.onRotationBegin();
        }

        @Override
        public void onRotationEnd(@NonNull RotationGestureDetector detector) {
            GestureController.this.onRotationEnd();
        }

        @Override
        public boolean onScale(@NonNull ScaleGestureDetector detector) {
            return GestureController.this.onScale(detector);
        }

        @Override
        public boolean onScaleBegin(@NonNull ScaleGestureDetector detector) {
            return GestureController.this.onScaleBegin();
        }

        @Override
        public void onScaleEnd(@NonNull ScaleGestureDetector detector) {
            GestureController.this.onScaleEnd();
        }
    }
}
