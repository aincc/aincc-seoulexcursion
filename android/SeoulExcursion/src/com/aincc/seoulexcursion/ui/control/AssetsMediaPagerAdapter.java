package com.aincc.seoulexcursion.ui.control;

import java.util.List;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aincc.lib.cache.ImageLoader;
import com.aincc.lib.cache.ImageWorker.ImageWorkerAdapter;
import com.aincc.lib.ui.widget.page.APagerAdapter;
import com.aincc.seoulexcursion.App;
import com.aincc.seoulexcursion.R;
import com.aincc.seoulexcursion.util.Logger;
import com.aincc.seoulopenapi.model.AssetsMedia;

/**
 * 
 * <h3><b>AssetsMediaPagerAdapter</b></h3></br>
 * 
 * 문화재 미디어 정보 표시 페이저 어댑터
 * 
 * @author aincc@barusoft.com
 * @version 1.0.0
 * @since 1.0.0
 */
public class AssetsMediaPagerAdapter extends APagerAdapter
{
	private ImageLoader imageLoader;
	private ImageWorkerAdapter adapter;

	/**
	 * 
	 * @since 1.0.0
	 * @param context
	 */
	public AssetsMediaPagerAdapter(Context context, List<? extends PageItem> pages)
	{
		super(context, pages);

		// 이미지 어댑터
		adapter = new ImageWorkerAdapter()
		{

			@Override
			public int getSize()
			{
				return AssetsMediaPagerAdapter.this.pages.size();
			}

			@Override
			public Object getItem(int num)
			{
				AssetsMedia media = ((MediaPageItem) AssetsMediaPagerAdapter.this.pages.get(num)).media;
				return media.FILEPATH + media.FILENAME;
			}
		};

		// 이미지 로더
		imageLoader = App.getImageLoader(adapter);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position)
	{
		Logger.d("instantiateItem " + position);
		ViewGroup layout = (ViewGroup) super.instantiateItem(container, position);
		MediaPageItem page = (MediaPageItem) pages.get(position);
		ImageView iv = (ImageView) layout.findViewById(R.id.image);
		AssetsMedia media = page.media;

		// 이미지 타입인 경우 이미지 표시
		if (media.FILETYPE.equals("1"))
		{
			imageLoader.loadImage(media.FILEPATH + media.FILENAME, iv);
		}
		else
		{
		}
		return layout;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object)
	{
		Logger.d("destroyItem " + position);
		((ViewPager) container).removeView((View) object);
		object = null;
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
	public static class MediaPageItem extends PageItem
	{
		/**
		 * 미디어 정보
		 */
		public AssetsMedia media;

		public MediaPageItem(int layout, AssetsMedia media)
		{
			super(layout);
			this.media = media;
		}
	}
}
