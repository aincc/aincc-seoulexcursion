package com.aincc.seoulexcursion.ui.control;

import java.util.List;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aincc.lib.ui.widget.page.APagerAdapter;
import com.aincc.seoulexcursion.util.Logger;
import com.aincc.lib.util.Utils;

/**
 * 
 * <h3><b>MainPagerAdapter</b></h3></br>
 * 
 * 메인페이지 페이저 어댑터
 * 
 * @author aincc@barusoft.com
 * @version 1.0.0
 * @since 1.0.0
 */
public class MainPagerAdapter extends APagerAdapter
{
	/**
	 * 
	 * @since 1.0.0
	 * @param context
	 */
	public MainPagerAdapter(Context context, List<? extends PageItem> pages)
	{
		super(context, pages);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position)
	{
		Logger.d("instantiateItem " + position);
		ViewGroup layout = (ViewGroup) super.instantiateItem(container, position);
		MainPageItem page = (MainPageItem) pages.get(position);
		ImageView iv = (ImageView) layout.findViewById(page.view);
		try
		{
			iv.setImageBitmap(Utils.decodeSampledBitmapFromResource(context.getResources(), page.drawable, 320, 320));
		}
		catch (OutOfMemoryError e)
		{
			e.printStackTrace();
		}
		return layout;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object)
	{
		Logger.d("destroyItem " + position);
		super.destroyItem(container, position, object);
		// Utils.unbindDrawables((View) object);
		// ((ViewPager) container).removeView((View) object);
		// object = null;
	}

	/**
	 * 
	 * <h3><b>PageItem</b></h3></br>
	 * 
	 * 페이지 아이템
	 * 
	 * @author aincc@barusoft.com
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	public static class MainPageItem extends PageItem
	{
		/**
		 * 대표이미지 뷰 아이디
		 */
		public int view;

		/**
		 * 대표이미지 아이디
		 */
		public int drawable;

		public MainPageItem(int layout, int view, int drawable)
		{
			super(layout);
			this.view = view;
			this.drawable = drawable;
		}
	}
}
