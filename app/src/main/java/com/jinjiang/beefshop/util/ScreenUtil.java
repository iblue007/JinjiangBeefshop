package com.jinjiang.beefshop.util;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.lang.reflect.Field;

/**
 * 屏幕显示相关工具类
 */
public class ScreenUtil {

	private static float currentDensity = 0;

	private static float scaledDensity = 0;

	/**
	 * 大屏幕的高度
	 */
	private final static int LARDGE_SCREEN_HEIGHT = 960;
	/**
	 * 大屏幕的宽度
	 */
	final static int LARDGE_SCREEN_WIDTH = 720;

	private final static int M9_SCREEN_WIDTH = 640;

	/**
	 * 返回屏幕尺寸(宽)
	 * 
	 * @param context
	 * @return int
	 */
	public static int getCurrentScreenWidth(Context context) {
		DisplayMetrics metrics = getDisplayMetrics(context);
		boolean isLand = isOrientationLandscape(context);
		if (isLand) {
			return metrics.heightPixels;
		}
		return metrics.widthPixels;
	}

	/**
	 * 返回屏幕尺寸(高)
	 * 
	 * @param context
	 * @return int
	 */
	public static int getCurrentScreenHeight(Context context) {
		DisplayMetrics metrics = getDisplayMetrics(context);
		boolean isLand = isOrientationLandscape(context);
		if (isLand) {
			return metrics.widthPixels;
		}
		return metrics.heightPixels;
	}

	/**
	 * 返回屏幕尺寸
	 * 
	 * @param context
	 * @return DisplayMetrics
	 */
	public static DisplayMetrics getDisplayMetrics(Context context) {
		return context.getResources().getDisplayMetrics();
	}

	/**
	 * 重新计算cellLayout上单元格的高宽，防止因屏幕高度获取不对引发的错误
	 * 
	 * @return int[]
	 */
	public static int[] getScreenWH(Context context) {
		int[] screenWH = { 320, 480 };
		if (context == null) {
			Log.e("ScreenUtil.getScreenWH", "ApplicationContext is null!");
			return screenWH;
		}

		final WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		final Display display = windowManager.getDefaultDisplay();
		boolean isPortrait = display.getWidth() < display.getHeight();
		final int width = isPortrait ? display.getWidth() : display.getHeight();
		final int height = isPortrait ? display.getHeight() : display.getWidth();
		screenWH[0] = width;
		screenWH[1] = height;

		return screenWH;
	}

	/**
	 * 是否是更大的屏幕 高度大于 960 宽度不等于640
	 * 
	 * @return boolean
	 */
	public static boolean isExLardgeScreen(Context context) {
		int[] wh = getScreenWH(context);
		// return wh[0] >= LARDGE_SCREEN_WIDTH && wh[1] >= LARDGE_SCREEN_HEIGHT;
		return wh[1] >= LARDGE_SCREEN_HEIGHT && wh[0] != M9_SCREEN_WIDTH;
	}

	/**
	 * 判断是否横屏
	 * 
	 * @param context
	 * @return boolean
	 */
	public static boolean isOrientationLandscape(Context context) {
		if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			return true;
		}
		return false;
	}

	/**
	 * dp转px
	 * 
	 * @param context
	 * @param dipValue
	 * @return int
	 */
	public static int dip2px(Context context, float dipValue) {
		if (currentDensity > 0)
			return (int) (dipValue * currentDensity + 0.5f);

		currentDensity = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * currentDensity + 0.5f);
	}

	/**
	 * sp转px
	 * 
	 * @param context
	 * @param spValue
	 * @return int
	 */
	public static int sp2px(Context context, float spValue) {
		if (scaledDensity > 0)
			return (int) (spValue * scaledDensity + 0.5f);

		scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * scaledDensity + 0.5f);
	}

	/**
	 * 将px值转换为dip或dp值，保证尺寸大小不变
	 * 
	 * @param pxValue
	 * @param scale （DisplayMetrics类中属性density）
	 * @return
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	
	/** 获取通知栏高度，获取失败则返回25dip */
	public static int getStatusBarHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			int x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
			statusBarHeight = dip2px(context, 25);
		}
		return statusBarHeight;
	}

}
