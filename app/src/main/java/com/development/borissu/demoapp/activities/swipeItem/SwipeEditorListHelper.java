package com.development.borissu.demoapp.activities.swipeItem;


import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;


import com.development.borissu.demoapp.R;

import butterknife.ButterKnife;

public class SwipeEditorListHelper {
    private static final String TAG = SwipeEditorListHelper.class.getSimpleName();
    // 設定是否左右功能項的layout要跟著使用者拖拉變寬
    private final boolean CAN_ACTION_LAYOUT_EXPAND = false;
    //region 用自定義的View
    private SwipeableViewHolder currentVH;
    // 定義一個監聽onMove的介面
    private ItemOnMoveListener itemOnMoveListener;

    public void setItemOnMoveListener(ItemOnMoveListener itemOnMoveListener) {
        this.itemOnMoveListener = itemOnMoveListener;
    }

    public interface ItemOnMoveListener {
        void itemOnMove(int fromPos, int toPos);
    }

    private static int type;

    private static final int TYPE_GRID = 0;
    private static final int TYPE_LINEAR_HORIZONTAL = 1;
    private static final int TYPE_LINEAR_VERTICAL = 2;

    public SwipeEditorListHelper(RecyclerView recyclerView) {
        // 依照recyclerView的LayoutManager設定支援的拖曳 ,swipe方向 以及layout檔相關的判斷值
        int dragDirs;
        int swipeDirs;

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

        if (layoutManager instanceof GridLayoutManager) { //矩陣的recyclerView
            dragDirs = ItemTouchHelper.START | ItemTouchHelper.END | ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            swipeDirs = 0;
            type = TYPE_GRID;

        } else if (layoutManager instanceof LinearLayoutManager) { //線性的recyclerView
            if (((LinearLayoutManager) layoutManager).getOrientation() == LinearLayoutManager.VERTICAL) { //垂直的LinearLayoutManager
                dragDirs = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                swipeDirs = ItemTouchHelper.START | ItemTouchHelper.END;
                type = TYPE_LINEAR_HORIZONTAL;

            } else {  //水平的LinearLayoutManager
                dragDirs = ItemTouchHelper.START | ItemTouchHelper.END;
                swipeDirs = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                type = TYPE_LINEAR_VERTICAL;

            }
        } else { //理論上用不到的default值
            dragDirs = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            swipeDirs = ItemTouchHelper.START | ItemTouchHelper.END;
            type = TYPE_LINEAR_HORIZONTAL;
        }

        mRecyclerView = recyclerView;
        init(recyclerView, dragDirs, swipeDirs);
    }

    ItemTouchHelper.Callback callback;
    ItemTouchHelper itemTouchHelper;
    RecyclerView mRecyclerView;

    public void init(RecyclerView recyclerView, int dragDirs, int swipeDirs) {
        // 加入偵測觸碰事件的listener
        RecyclerView.OnItemTouchListener itemTouchListener = createItemTouchListener();
        recyclerView.addOnItemTouchListener(itemTouchListener);
        // 加入swipe跟drag偵測的helper callback
        callback = createItemTouchCallback(dragDirs, swipeDirs);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public void setDragMode(boolean isDrageMode) {
        if (isDrageMode) {
            itemTouchHelper = new ItemTouchHelper(callback);
            itemTouchHelper.attachToRecyclerView(mRecyclerView);
        } else {
            itemTouchHelper.attachToRecyclerView(null);
            itemTouchHelper = null;
        }
    }

    /**
     * 自訂ItemTouchHelper.Callback
     */
    private ItemTouchHelper.Callback createItemTouchCallback(int dragDirs, int swipeDirs) {
        ItemTouchHelper.Callback callback = new BaseCallBack(dragDirs, swipeDirs) {

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Log.d(TAG, "onChildDraw " + dX + " " + dY + " " + actionState + " " + isCurrentlyActive + " " + viewHolder.getAdapterPosition());

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    SwipeableViewHolder swipeableViewHolder = (SwipeableViewHolder) viewHolder;

                    if (type == TYPE_LINEAR_HORIZONTAL) {   //垂直linear的swipe
                        if (dX > 0) {
                            dX = (dX * swipeableViewHolder.originalLeftWidth) / swipeableViewHolder.vMainContent.getWidth();
                            // 當移動距離超過左邊功能項layout寬度
                            if (dX > swipeableViewHolder.originalLeftWidth) {
                                // Way1 : 讓左邊功能項layout寬度跟著變化
                                if (CAN_ACTION_LAYOUT_EXPAND) {
                                    ViewGroup actionView = swipeableViewHolder.vActionLeft;
                                    if (actionView.getChildCount() > 0) {
                                        ViewGroup.LayoutParams params = actionView.getLayoutParams();
                                        if ((int) Math.abs(dX) != params.width) {
                                            int width = (int) Math.abs(dX);
                                            swipeableViewHolder.setWidth(actionView, width);
                                            swipeableViewHolder.setAllTranslationX(-width);
                                        }
                                    }
                                } else {
                                    // Way2 : 單純鎖死寬度（不會跟著變化）
                                    dX = swipeableViewHolder.originalLeftWidth;
                                }
                            }
                        }
                        if (dX < 0) {
                            dX = (dX * swipeableViewHolder.originalRightWidth) / swipeableViewHolder.vMainContent.getWidth();
                            // 當移動距離超過右邊功能項layout寬度
                            if (Math.abs(dX) > swipeableViewHolder.originalRightWidth) {
                                // Way1 : 讓左邊功能項layout寬度跟著變化
                                if (CAN_ACTION_LAYOUT_EXPAND) {
                                    ViewGroup actionView = swipeableViewHolder.vActionRight;
                                    if (actionView.getChildCount() > 0) {
                                        ViewGroup.LayoutParams params = actionView.getLayoutParams();
                                        if ((int) Math.abs(dX) != params.width) {
                                            swipeableViewHolder.setWidth(actionView, (int) Math.abs(dX));
                                        }
                                    }
                                } else {
                                    // Way2 : 單純鎖死寬度（不會跟著變化）
                                    dX = -swipeableViewHolder.originalRightWidth;
                                }
                            }
                        }
                        viewHolder.itemView.scrollTo(-(int) dX, 0);
                        swipeableViewHolder.isActionOpening = dX != 0;

                    } else {  //水平linear的swipe
                        if (dY > 0) {
                            dY = (dY * swipeableViewHolder.originalUpHeight) / swipeableViewHolder.vMainContent.getHeight();
                            // 當移動距離超過上面功能項layout高度
                            if (dY > swipeableViewHolder.originalUpHeight) {
                                // Way1 : 讓上面功能項layout高度跟著變化
                                if (CAN_ACTION_LAYOUT_EXPAND) {
                                    ViewGroup actionView = swipeableViewHolder.vActionUp;
                                    if (actionView.getChildCount() > 0) {
                                        ViewGroup.LayoutParams params = actionView.getLayoutParams();
                                        if ((int) Math.abs(dY) != params.height) {
                                            int height = (int) Math.abs(dY);
                                            swipeableViewHolder.setHeight(actionView, height);
                                            swipeableViewHolder.setAllTranslationY(-height);
                                        }
                                    }
                                } else {
                                    // Way2 : 單純鎖死高度（不會跟著變化）
                                    dY = swipeableViewHolder.originalUpHeight;
                                }
                            }
                        }
                        if (dY < 0) {
                            dY = (dY * swipeableViewHolder.originalDownHeight) / swipeableViewHolder.vMainContent.getHeight();
                            // 當移動距離超過右邊功能項layout寬度
                            if (Math.abs(dY) > swipeableViewHolder.originalDownHeight) {
                                // Way1 : 讓左邊功能項layout寬度跟著變化
                                if (CAN_ACTION_LAYOUT_EXPAND) {
                                    ViewGroup actionView = swipeableViewHolder.vActionDown;
                                    if (actionView.getChildCount() > 0) {
                                        ViewGroup.LayoutParams params = actionView.getLayoutParams();
                                        if ((int) Math.abs(dY) != params.height) {
                                            swipeableViewHolder.setHeight(actionView, (int) Math.abs(dY));
                                        }
                                    }
                                } else {
                                    // Way2 : 單純鎖死寬度（不會跟著變化）
                                    dY = -swipeableViewHolder.originalDownHeight;
                                }
                            }
                        }
                        viewHolder.itemView.scrollTo(0, -(int) dY);
                        swipeableViewHolder.isActionOpening = dY != 0;
                    }

                } else {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                Log.d(TAG, "clearView " + viewHolder.getAdapterPosition());
                viewHolder.itemView.scrollTo(0, 0);
                SwipeableViewHolder swipeableViewHolder = (SwipeableViewHolder) viewHolder;
                swipeableViewHolder.isActionOpening = false;
            }
        };
        return callback;
    }

    /**
     * 自定義SimpleOnItemTouchListener
     */
    @NonNull
    private RecyclerView.SimpleOnItemTouchListener createItemTouchListener() {
        return new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                Log.d(TAG, "onInterceptTouchEvent " + e);
                //排除ACTION_MOVE，否則無法觸發ACTION_UP
                if (e.getAction() == MotionEvent.ACTION_MOVE) {
                    return false;
                }

                View itemView = rv.findChildViewUnder(e.getX(), e.getY());
                if (itemView != null) {
                    SwipeEditorListHelper.SwipeableViewHolder viewHolder = (SwipeEditorListHelper.SwipeableViewHolder) rv.findContainingViewHolder(itemView);
                    if (viewHolder != null) {
                        currentVH = viewHolder;
                        boolean result = viewHolder.onTouch(e);
                        Log.d(TAG, "ItemTouchListener viewHolder.isActionOpening " + viewHolder.getAdapterPosition() + " " + viewHolder.isActionOpening);
                        if (!result && viewHolder.isActionOpening) {
                            result = viewHolder.detector.onTouchEvent(e);
                        }
                        Log.d(TAG, "ItemTouchListener viewHolder result = " + result);
                        return result;
                    }
                }
                return false;
            }

        };
    }

    //endregion 用自定義的View

    /**
     * Base ItemTouchHelper.SimpleCallback
     */
    private class BaseCallBack extends ItemTouchHelper.SimpleCallback {

        public BaseCallBack(int dragDirs, int swipeDirs) {
            super(dragDirs, swipeDirs);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            Log.d(TAG, "BaseCallBac onMove " + viewHolder.getAdapterPosition());
//            return false;

            //取得onMove的值並執行itemOnMove事件
            final int fromPos = viewHolder.getAdapterPosition();
            final int toPos = target.getAdapterPosition();
            // move item from `fromPos` to `toPos` in adapter.
            itemOnMoveListener.itemOnMove(fromPos, toPos);
            return true;// true if moved, false otherwise
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            Log.d(TAG, "BaseCallBack onSwiped " + viewHolder.getAdapterPosition() + " " + direction);
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (viewHolder != null) {
                int currentPos = viewHolder.getAdapterPosition();
                if (lastPos >= 0 && lastPos != currentPos) {
                    lastPos = currentPos;
                }
                Log.d(TAG, "BaseCallBack onSelectedChanged " + currentPos + " " + actionState);
            } else {
                Log.d(TAG, "BaseCallBack onSelectedChanged " + actionState);
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            SwipeableViewHolder swipeableViewHolder = (SwipeableViewHolder) viewHolder;
            Log.d(TAG, "getMovementFlags " + viewHolder.getAdapterPosition() + " " + swipeableViewHolder.needLockMoveFlag);
            if (swipeableViewHolder.needLockMoveFlag || currentVH.getAdapterPosition() != viewHolder.getAdapterPosition()) {
                swipeableViewHolder.needLockMoveFlag = false;
                return 0;
            }
            return super.getMovementFlags(recyclerView, viewHolder);
        }

        protected RecyclerView.ViewHolder lastViewHolder;
        protected int lastPos;
    }

    /**
     * 協助建立基本架構的View
     *
     * @param parent   RecyclerView
     * @param layoutId 原本的list item layout
     */
    public static View createSwipeView(LayoutInflater layoutInflater, ViewGroup parent, @LayoutRes int layoutId) {
        View containerView;
        if (type == TYPE_GRID) {
            containerView = layoutInflater.inflate(layoutId, parent, false);
        } else if (type == TYPE_LINEAR_HORIZONTAL) {
            containerView = layoutInflater.inflate(R.layout.item_horiz_swipeable, parent, false);
            ViewGroup mainView = containerView.findViewById(R.id.view_main_content);
            View itemView = layoutInflater.inflate(layoutId, mainView, false);
            mainView.addView(itemView);
        } else {
            containerView = layoutInflater.inflate(R.layout.item_verti_swipeable, parent, false);
            ViewGroup mainView = containerView.findViewById(R.id.view_main_content);
            View itemView = layoutInflater.inflate(layoutId, mainView, false);
            mainView.addView(itemView);
        }
        return containerView;
    }

    /**
     * 基本架構的ViewHolder
     */
    public static class SwipeableViewHolder extends RecyclerView.ViewHolder {
        public ViewGroup vMainContent, vActionRight, vActionLeft, vActionDown, vActionUp;
        public int originalRightWidth, originalLeftWidth, originalDownHeight, originalUpHeight;
        public int actionWidth;
        public int actionHeight;
        public boolean isActionOpening = false;
        public boolean needLockMoveFlag = false;

        public GestureDetector detector;

        public SwipeableViewHolder(View itemView, ViewGroup viewGroup) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            // grid類型沒有滑動 , 故不處理
            if (type == TYPE_LINEAR_HORIZONTAL) {
                vMainContent = itemView.findViewById(R.id.view_main_content);
                vActionRight = itemView.findViewById(R.id.view_action_right);
                vActionLeft = itemView.findViewById(R.id.view_action_left);
                ViewGroup.LayoutParams paramsMain = vMainContent.getLayoutParams();
                paramsMain.width = viewGroup.getWidth();
                vMainContent.setLayoutParams(paramsMain);
            } else if (type == TYPE_LINEAR_VERTICAL) {
                vMainContent = itemView.findViewById(R.id.view_main_content);
                vActionDown = itemView.findViewById(R.id.view_action_down);
                vActionUp = itemView.findViewById(R.id.view_action_up);
                ViewGroup.LayoutParams paramsMain = vMainContent.getLayoutParams();
                paramsMain.height = viewGroup.getHeight();
                vMainContent.setLayoutParams(paramsMain);
            }

            detector = new GestureDetector(itemView.getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    needLockMoveFlag = true;
                    Log.d(TAG, "onSingleTapUp " + getAdapterPosition());
                    return false;
                }
            });
        }

        protected void addRightAction(final View view) {
            if (vActionRight != null) {
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        originalRightWidth += view.getWidth();
                        setWidth(vActionRight, originalRightWidth);
                        Log.d(TAG, "ViewHolder addRightAction width += " + view.getWidth() + " = " + originalRightWidth);
                    }
                });
                vActionRight.addView(view);
                vActionRight.setBackground(view.getBackground());
            }
        }

        protected void addLeftAction(final View view) {
            if (vActionLeft != null) {
                vActionLeft.addView(view);
                setWidth(view, actionWidth);
                originalLeftWidth += actionWidth;
                Log.d(TAG, "ViewHolder addLeftAction width += " + view.getWidth() + " = " + originalRightWidth);
                setWidth(vActionLeft, originalLeftWidth);
                setAllTranslationX(-originalLeftWidth);
                vActionLeft.setBackground(view.getBackground());
            }
        }

        protected void addDownAction(final View view) {
            if (vActionDown != null) {
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        originalDownHeight += view.getHeight();
                        setHeight(vActionDown, originalDownHeight);
                        Log.d(TAG, "ViewHolder addDownAction height += " + view.getHeight() + " = " + originalDownHeight);
                    }
                });
                vActionDown.addView(view);
                vActionDown.setBackground(view.getBackground());
            }
        }

        protected void addUpAction(final View view) {
            if (vActionUp != null) {
                vActionUp.addView(view);
                setHeight(view, actionHeight);
                originalUpHeight += actionHeight;
                Log.d(TAG, "ViewHolder addUpAction height += " + view.getHeight() + " = " + originalUpHeight);
                setHeight(vActionUp, originalUpHeight);
                setAllTranslationY(-originalUpHeight);
                vActionUp.setBackground(view.getBackground());
            }
        }

        protected void setAllTranslationX(int translationX) {
            vActionLeft.setTranslationX(translationX);
            vMainContent.setTranslationX(translationX);
            vActionRight.setTranslationX(translationX);
        }

        protected void setAllTranslationY(int translationY) {
            vActionUp.setTranslationY(translationY);
            vMainContent.setTranslationY(translationY);
            vActionDown.setTranslationY(translationY);
        }

        protected void setWidth(View view, int width) {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.width = width;
            view.setLayoutParams(params);
        }

        protected void setHeight(View view, int height) {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.height = height;
            view.setLayoutParams(params);
        }

        /**
         * touch事件判定
         */
        public boolean onTouch(MotionEvent e) {
            int x = (int) (e.getX() - itemView.getX() + itemView.getScrollX());
            int y = (int) (e.getY() - itemView.getY());
            Log.d(TAG, "Vh onTouch = " + e.getX() + " " + e.getY() + " -> " + x + " " + y);
            boolean eventHandled = tryHandleEvent(vActionRight, e, x, y);
            if (!eventHandled) {
                eventHandled = tryHandleEvent(vActionLeft, e, x, y);
            }
            return eventHandled;
        }

        private boolean tryHandleEvent(ViewGroup viewGroup, MotionEvent e, int x, int y) {
            if (viewGroup != null) {
                Rect rect = new Rect();
                viewGroup.getHitRect(rect);
                Log.d(TAG, "tryHandleEvent getHitRect = " + rect + " " + rect.contains(x, y));
                if (rect.contains(x, y)) {
                    // 重新設定以itemView位置為基準的MotionEvent
                    MotionEvent newE = MotionEvent.obtain(e);
                    x -= viewGroup.getX();
                    Log.d(TAG, "tryHandleEvent -> " + x + " " + y);
                    newE.setLocation(x, y);
                    boolean result = viewGroup.dispatchTouchEvent(newE);
                    newE.recycle();
                    return result;
                }
            }
            return false;
        }

        public View getLeftActionRightChild() {
            if (vActionLeft.getChildCount() > 0) {
                return vActionLeft.getChildAt(vActionLeft.getChildCount() - 1);
            }
            return null;
        }

        public View getRightActionLeftChild() {
            if (vActionRight.getChildCount() > 0) {
                if (vActionRight.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
                    return vActionRight.getChildAt(vActionRight.getChildCount() - 1);
                } else {
                    return vActionRight.getChildAt(0);
                }
            }
            return null;
        }
    }
}
