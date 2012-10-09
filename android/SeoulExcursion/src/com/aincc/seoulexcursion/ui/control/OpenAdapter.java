package com.aincc.seoulexcursion.ui.control;

import java.util.List;

import android.content.Context;
import android.widget.BaseAdapter;

/**
 * 
 * <h3><b>OpenAdapter</b></h3></br>
 * 
 * 일반 리스트뷰 공통 어댑터 
 * 
 * @author aincc@barusoft.com
 * @version 1.0.0
 * @since 1.0.0
 * @param <E>
 */
public abstract class OpenAdapter<T> extends BaseAdapter
{
	protected Context context;
	protected List<T> listData;

	/**
	 * 
	 * @since 1.0.0
	 * @param context
	 * @param listData
	 */
	protected OpenAdapter(Context context, List<T> listData)
	{
		this.context = context;
		this.listData = listData;
	}

	@Override
	public int getCount()
	{
		if (null != listData)
		{
			return listData.size();
		}
		return 0;
	}

	@Override
	public T getItem(int position)
	{
		if (null != listData)
		{
			return listData.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}
}
