package com.aincc.seoulexcursion.ui.control;

import java.util.List;

import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.aincc.lib.ui.widget.list.section.AmazingAdapter;
import com.aincc.seoulexcursion.R;
import com.aincc.seoulexcursion.util.SeoulFont;

/**
 * 
 * <h3><b>SectionAdapter</b></h3></br>
 * 
 * 섹션 어댑터
 * 
 * @author aincc@barusoft.com
 * @version 1.0.0
 * @since 1.0.0
 * @param <T>
 */
public abstract class SectionAdapter<T> extends AmazingAdapter
{
	private List<Pair<String, List<T>>> listdata;

	/**
	 * 리스트 설정
	 * 
	 * @since 1.0.0
	 * @param listdata
	 */
	public void setList(List<Pair<String, List<T>>> listdata)
	{
		this.listdata = listdata;
	}

	/**
	 * 리스트 가져오기
	 * 
	 * @since 1.0.0
	 * @return the listdata
	 */
	public List<Pair<String, List<T>>> getList()
	{
		return listdata;
	}

	/**
	 * 리스트 삭제
	 * 
	 * @since 1.0.0
	 */
	public void clear()
	{
		if (null != listdata)
		{
			listdata.clear();
		}
	}

	@Override
	public int getCount()
	{
		if (null != listdata)
		{
			int res = 0;
			for (int ii = 0; ii < listdata.size(); ii++)
			{
				res += listdata.get(ii).second.size();
			}
			return res;
		}
		return 0;
	}

	@Override
	public T getItem(int position)
	{
		if (null != listdata)
		{
			int c = 0;
			for (int ii = 0; ii < listdata.size(); ii++)
			{
				if (position >= c && position < c + listdata.get(ii).second.size())
				{
					return (T) listdata.get(ii).second.get(position - c);
				}
				c += listdata.get(ii).second.size();
			}
		}
		return null;
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	protected void onNextPageRequested(int page)
	{
	}

	@Override
	protected void bindSectionHeader(View view, int position, boolean displaySectionHeader)
	{
		if (displaySectionHeader)
		{
			view.findViewById(R.id.header).setVisibility(View.VISIBLE);
			TextView title = (TextView) view.findViewById(R.id.headerText);
			title.setTypeface(SeoulFont.getInstance().getSeoulHangang());
			title.setText(getSections()[getSectionForPosition(position)]);
		}
		else
		{
			view.findViewById(R.id.header).setVisibility(View.GONE);
		}
	}

	@Override
	public void configurePinnedHeader(View header, int position, int alpha)
	{
		// 최상위 헤더 표시 스타일 설정
		// header.setBackgroundColor(alpha << 24 | (0xbbffbb));

		TextView title = (TextView) header.findViewById(R.id.headerText);
		title.setTypeface(SeoulFont.getInstance().getSeoulHangang());
		title.setText(getSections()[getSectionForPosition(position)]);
		title.setTextColor(alpha << 24 | (0x000000));
	}

	@Override
	public int getPositionForSection(int section)
	{
		if (section < 0)
		{
			section = 0;
		}
		if (section >= listdata.size())
		{
			section = listdata.size() - 1;
		}
		int c = 0;
		for (int ii = 0; ii < listdata.size(); ii++)
		{
			if (section == ii)
			{
				return c;
			}
			c += listdata.get(ii).second.size();
		}
		return 0;
	}

	@Override
	public int getSectionForPosition(int position)
	{
		int c = 0;
		for (int ii = 0; ii < listdata.size(); ii++)
		{
			if (position >= c && position < c + listdata.get(ii).second.size())
			{
				return ii;
			}
			c += listdata.get(ii).second.size();
		}
		return -1;
	}

	@Override
	public String[] getSections()
	{
		String[] res = new String[listdata.size()];
		for (int ii = 0; ii < listdata.size(); ii++)
		{
			res[ii] = listdata.get(ii).first;
		}
		return res;
	}

}
