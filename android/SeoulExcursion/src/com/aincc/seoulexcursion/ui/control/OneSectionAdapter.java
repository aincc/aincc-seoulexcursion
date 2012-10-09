package com.aincc.seoulexcursion.ui.control;

import java.util.List;

import android.view.View;
import android.widget.TextView;

import com.aincc.lib.ui.widget.list.section.AmazingAdapter;
import com.aincc.seoulexcursion.R;
import com.aincc.seoulexcursion.util.SeoulFont;

/**
 * 
 * <h3><b>OneSectionAdapter</b></h3></br>
 * 
 * 한개의 섹션으로 구성된 어댑터
 * 
 * @author aincc@barusoft.com
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class OneSectionAdapter<T> extends AmazingAdapter
{
	private List<?> listdata = null;

	/**
	 * 리스트 설정
	 * 
	 * @since 1.0.0
	 * @param listdata
	 */
	public void setList(List<?> listdata)
	{
		this.listdata = listdata;
	}

	/**
	 * 
	 * @since 1.0.0
	 * @return
	 */
	public List<?> getList()
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
			return listdata.size();
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getItem(int position)
	{
		if (null != listdata)
		{
			return (T) listdata.get(position);
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
		return 0;
	}

	@Override
	public int getSectionForPosition(int position)
	{
		if (null != listdata)
		{
			return 0;
		}
		return -1;
	}

	@Override
	public String[] getSections()
	{
		// 섹션명을 지정할 코드를 추가한다.
		String[] res = new String[1];
		res[0] = "";
		return res;
	}
}
