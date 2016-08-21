/**
 * FileName:     SatelliteView.java satellite menu
 * All rights Reserved, Designed By 
 * Copyright:    Copyright(C) 2015
 * Company       
 * @author:      renferliu
 * @version      V1.0 
 * Createdate:   2016-08-17
 *
 * Modification  History:
 * Date          Author        Version        Discription
 * -----------------------------------------------------------------------------------
 * 
 * Why & What is modified: 
 */
package com.renfer.testsatellite;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * satellite menu
 * 
 * @author renferliu contact:renfer163@163.com
 * @date 2016-08-17
 * @version 1.0
 * 
 */
public class SatelliteMenu extends ViewGroup {

	/**
	 * menu state
	 */
	public enum Status {
		OPEN, CLOSE
	}

	/**
	 * menu position
	 */
	public interface Layout_Gravity {
		int LEFT_TOP = 0, LEFT_BOTTOM = 1, RIGHT_TOP = 2, RIGHT_BOTTOM = 3;
	}

	private final static int MAIN_MENU_ANIMATION_DURING = 300;

	private final static int MENU_ITEM_ANIMATION_DURING = 2000;

	/**
	 * menu position
	 */
	private int layoutGravity = Layout_Gravity.RIGHT_BOTTOM;

	/**
	 * disatance from main menu to item menu
	 */
	private int distance = 300;

	/**
	 * height of menu item height
	 */
	private int childHeight;

	/**
	 * current statu，default state is close
	 */
	private Status currentStatus = Status.CLOSE;

	/**
	 * main item view of this menu
	 */
	private View mainItem;

	/**
	 * @see OnSatelliteMenuItemClickListener
	 */
	private OnSatelliteMenuItemClickListener onSatelliteMenuItemClickListener;

	/**
	 * @see OnSatelliteMenuStateListener
	 */
	private OnSatelliteMenuStateListener onSatelliteMenuStateListener;

	private View lastItemView;


	private int paddingLeft;
	private int paddingRight;
	private int paddingTop;
	private int paddingBottom;

	private int screenWidth;
	@SuppressWarnings("unused")
	private int screenHeight;

	private View tringleView;

	public SatelliteMenu(Context context) {
		this(context, null);
	}

	public SatelliteMenu(Context context, AttributeSet attrs) {
		super(context, attrs, 0);
		init(context, attrs, 0);
	}

	public SatelliteMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * init attribution
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	private void init(Context context, AttributeSet attrs, int defStyle) {
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.StatelliteMenu, defStyle, 0);
		int n = a.getIndexCount();
		for (int i = 0; i < n; i++) {
			int attr = a.getIndex(i);
			if (attr == R.styleable.StatelliteMenu_layout_gravity) {
				int pos = a.getInt(attr, Layout_Gravity.RIGHT_BOTTOM);
				switch (pos) {
				case Layout_Gravity.LEFT_TOP:
					layoutGravity = Layout_Gravity.LEFT_TOP;
					break;
				case Layout_Gravity.LEFT_BOTTOM:
					layoutGravity = Layout_Gravity.LEFT_BOTTOM;
					break;
				case Layout_Gravity.RIGHT_TOP:
					layoutGravity = Layout_Gravity.RIGHT_TOP;
					break;
				case Layout_Gravity.RIGHT_BOTTOM:
					layoutGravity = Layout_Gravity.RIGHT_BOTTOM;
					break;
				}
			} else if (attr == R.styleable.StatelliteMenu_distance) {
				distance = a.getDimensionPixelSize(attr, distance);
			}
		}

		a.recycle();

		paddingBottom = getPaddingBottom();
		paddingLeft = getPaddingLeft();
		paddingRight = getPaddingRight();
		paddingTop = getPaddingTop();
		getScreenWidthHeight();
	}

	/**
	 * get screen width and height
	 */
	private void getScreenWidthHeight() {
		Display display = ((WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE)).getDefaultDisplay();
		final Point point = new Point();
		try {
			display.getSize(point);
		} catch (java.lang.NoSuchMethodError ignore) { // Older device
			point.x = display.getWidth();
			point.y = display.getHeight();
		}
		screenWidth = point.x;
		screenHeight = point.y;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);// 测量child
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (changed) {
			initMainItem();
			layoutMainItem();

			int count = getChildCount();
			for (int i = 0; i < count - 1; i++) {
				int cl = 0;
				int ct = 0;
				View child = getChildAt(i + 1);
				child.setVisibility(GONE);
				int cWidth = child.getMeasuredWidth();
				int cHeight = child.getMeasuredHeight();
				if (childHeight < cHeight) {
					childHeight = cHeight;
				}
				// if menu on the bottom
				if (isMenuOnTheBottom()) {
					ct = getMeasuredHeight() - cHeight - ct;
				}
				// if menu on the right
				if (isMenuOnTheRight()) {
					cl = getMeasuredWidth() - cWidth - cl;
				}
				child.layout(cl, ct, cl + cWidth, ct + cHeight);
			}
		}
	}

	/**
	 * start MainMenu Animation
	 */
	private void startMainMenuAnimation() {
		AnimatorSet set = new AnimatorSet();
		ObjectAnimator animator = null;
		if (isMenuClose()) {
			animator = ObjectAnimator.ofFloat(mainItem, "rotation", 0f, 360f);
		} else {
			animator = ObjectAnimator.ofFloat(mainItem, "rotation", 0f, -360f);
		}
		animator.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {

			}
		});
		set.play(animator);
		set.setDuration(MAIN_MENU_ANIMATION_DURING).start();
	}

	/**
	 * menu item animation
	 * 
	 * @param position
	 * @param v
	 * @param startX
	 * @param endX
	 * @param startY
	 * @param endY
	 * @param duration
	 */
	private void playMenuItemAnimate(final int position, final View v,
			float startX, float endX, float startY, float endY, int duration) {
		AnimatorSet set = new AnimatorSet();
		ObjectAnimator animatorX = ObjectAnimator.ofFloat(v, "translationX",
				startX, endX);
		ObjectAnimator animatorY = ObjectAnimator.ofFloat(v, "translationY",
				startY, endY);

		set.play(animatorX).with(animatorY);
		if (isMenuClose()) {
			ObjectAnimator animatorAlpha = ObjectAnimator.ofFloat(v, "alpha",
					0f, 1.0f);
			set.play(animatorX).with(animatorAlpha);
		} else {
			ObjectAnimator animatorAlpha = ObjectAnimator.ofFloat(v, "alpha",
					1.0f, 0f);
			set.play(animatorX).with(animatorAlpha);
		}
		set.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				// setItem map resources
				if (isMenuClose()) {
					v.setVisibility(View.VISIBLE);
				} else {
					v.setVisibility(View.GONE);
				}
				// avoid child view gone invalided
				if (v == lastItemView) {
					changeStatus();
					lastItemView = null;
				}

				if (onSatelliteMenuStateListener != null) {
					if (isMenuClose()) {
						onSatelliteMenuStateListener.onMenuOpen(mainItem);
					} else {
						onSatelliteMenuStateListener.onMenuClose(mainItem);
					}
				}

			}

			@Override
			public void onAnimationCancel(Animator animation) {

			}
		});
		set.start();
	}

	/**
	 * is menu closed
	 * 
	 * @return close <code>true</code> open <code>false</code>
	 */
	private boolean isMenuClose() {
		return currentStatus == Status.CLOSE;
	}

	/**
	 * toggle the menu state
	 */
	private void toggleMenu() {
		int count = getChildCount();
		int cl = 0;
		int ct = distance;
		for (int i = 0; i < count - 1; i++) {
			final View childView = getChildAt(i + 1);
			if (i == count - 2) {
				lastItemView = childView;
			}
			childView.setVisibility(View.VISIBLE);
			if (i != 0) {
				cl = cl + (int) screenWidth / (count -1)  ;
			}
			int xflag = 1;
			int yflag = 1;

			if (isMenuOnTheBottom()) {
				yflag = -1;
			}

			if (isMenuOnTheRight()) {
				xflag = -1;
			}

			if (isMenuClose()) {
				playMenuItemAnimate(i, childView, xflag * getX(), xflag * cl,
						0, yflag * ct, MENU_ITEM_ANIMATION_DURING);
				childView.setClickable(true);
				childView.setFocusable(true);
			} else {
				playMenuItemAnimate(i, childView, xflag * cl, xflag * getX(),
						yflag * ct, 0, MENU_ITEM_ANIMATION_DURING);
				childView.setClickable(false);
				childView.setFocusable(false);
			}
			final int pos = i + 1;
			childView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (onSatelliteMenuItemClickListener != null) {
						onSatelliteMenuItemClickListener.onItemClick(v, pos);
					}
				}
			});
		}

	}







	/**
	 * is menu on the right of parent container
	 * 
	 * @return <code>true</code> on the right <code>false</code> on the left
	 */
	private boolean isMenuOnTheRight() {
		if (layoutGravity == Layout_Gravity.RIGHT_BOTTOM
				|| layoutGravity == Layout_Gravity.RIGHT_TOP) {
			return true;
		}
		return false;
	}

	/**
	 * is menu on the bottom of parent container
	 * 
	 * @return <code>true</code> on the bottom <code>false</code> on the top
	 */
	private boolean isMenuOnTheBottom() {
		if (layoutGravity == Layout_Gravity.RIGHT_BOTTOM
				|| layoutGravity == Layout_Gravity.LEFT_BOTTOM) {
			return true;
		}
		return false;
	}

	/**
	 * change the menu status
	 */
	private void changeStatus() {
		currentStatus = isMenuClose() ? Status.OPEN : Status.CLOSE;
	}

	/**
	 * sure the location for main menu item
	 */
	private void layoutMainItem() {
		int l = 0;
		int t = 0;
		int width = l + mainItem.getMeasuredWidth();
		int height = t + mainItem.getMeasuredHeight();
		switch (layoutGravity) {
		case Layout_Gravity.LEFT_TOP:
			l = 0;
			t = 0;
			break;
		case Layout_Gravity.LEFT_BOTTOM:
			l = 0;
			t = getMeasuredHeight() - height;
			break;
		case Layout_Gravity.RIGHT_TOP:
			l = getMeasuredWidth() - width;
			t = 0;
			break;
		case Layout_Gravity.RIGHT_BOTTOM:
			l = getMeasuredWidth() - width;
			t = getMeasuredHeight() - height;
			break;
		}
		mainItem.layout(l, t, l + width, t + height);
	}

	/**
	 * initlizing the main item of menu
	 */
	private void initMainItem() {
		mainItem = getChildAt(0);
		mainItem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startMainMenuAnimation();
				toggleMenu();
			}
		});
	}

	/**
	 * Register a callback to be invoked when this menu item is clicked.
	 * 
	 * @param onSatelliteMenuItemClickListener
	 */
	public void setOnSatelliteMenuItemClickListener(
			OnSatelliteMenuItemClickListener onSatelliteMenuItemClickListener) {
		this.onSatelliteMenuItemClickListener = onSatelliteMenuItemClickListener;
	}

	/**
	 * Register a callback to be invoked when this menu state is invoked.
	 * 
	 * @param onSatelliteMenuItemStateListener
	 */
	public void setOnSatelliteMenuStateListener(
			OnSatelliteMenuStateListener onSatelliteMenuStateListener) {
		this.onSatelliteMenuStateListener = onSatelliteMenuStateListener;
	}

	/**
	 * Interface definition for a callback to be invoked when a view is clicked.
	 * 
	 * @author renferliu contact:renfer163@163.com
	 * @date 2016-08-17
	 * @version 1.0
	 */
	public interface OnSatelliteMenuItemClickListener {

		/**
		 * item click method
		 * 
		 * @param view
		 *            The view within the SatelliteMenu that was clicked (this
		 *            will be a view provided by the menu)
		 * @param pos
		 *            the position of item
		 */
		void onItemClick(View view, int pos);
	}

	/**
	 * Interface definition for a callback to be invoked when this menu state is
	 * invoked.
	 * 
	 * @author renferliu contact:renfer163@163.com
	 * @date 2016-08-18
	 * @version 1.0
	 */
	public interface OnSatelliteMenuStateListener {

		/**
		 * menu is open
		 * 
		 * @param mainView
		 */
		void onMenuOpen(View mainItemView);

		/**
		 * menu is closed
		 * 
		 * @param mainView
		 */
		void onMenuClose(View mainItemView);

	}

	private class DialogResource {
		private int tittleId;
		private int imgId;
		private Point itemPos;
		private View view;

		/**
		 * 
		 * @param tittleId
		 * @param imgId
		 * @param itemPos
		 * @param view
		 */
		private DialogResource(int tittleId, int imgId, Point itemPos, View view) {
			this.tittleId = tittleId;
			this.imgId = imgId;
			this.itemPos = itemPos;
			this.view = view;
			if (this.itemPos == null) {
				this.itemPos = new Point();
			}
		}

		public int getTittleId() {
			return tittleId;
		}

		public void setTittleId(int tittleId) {
			this.tittleId = tittleId;
		}

		public int getImgId() {
			return imgId;
		}

		public void setImgId(int imgId) {
			this.imgId = imgId;
		}

		public Point getItemPos() {
			
			if (view != null) {
				itemPos.set((int) view.getX() + view.getWidth() / 2,
						(int) view.getY());
			}
			return itemPos;
		}

		public void setItemPos(Point itemPos) {
			this.itemPos = itemPos;
		}

		public View getView() {
			return view;
		}

		public void setView(View view) {
			this.view = view;
			if (view != null) {
				itemPos.set((int) view.getX() + view.getWidth() / 2,
						(int) view.getY());
			}
		}

	}
}
