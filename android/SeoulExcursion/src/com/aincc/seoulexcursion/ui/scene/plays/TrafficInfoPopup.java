package com.aincc.seoulexcursion.ui.scene.plays;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.aincc.seoulexcursion.R;
import com.aincc.seoulexcursion.ui.control.OpenAdapter;
import com.aincc.seoulexcursion.util.SeoulFont;
import com.aincc.seoulexcursion.util.SeoulUtils;
import com.aincc.seoulopenapi.model.FacilTrafficInfo;

/**
 * 
 * <h3><b>TrafficInfoPopup</b></h3></br>
 * 
 * 교통정보 팝업
 * 
 * @author aincc@barusoft.com
 * @version 1.3.0
 * @since 1.3.0
 */
public class TrafficInfoPopup extends Dialog implements android.view.View.OnClickListener
{
	private ListView listview;
	private TrafficAdapter adapter;
	private ImageButton close;
	private WindowManager.LayoutParams param;
	private Context context;
	private List<FacilTrafficInfo> infos;

	/**
	 * 
	 * @since 1.3.0
	 * @param context
	 */
	private TrafficInfoPopup(Context context, List<FacilTrafficInfo> infos)
	{
		super(context);
		this.context = context;
		this.infos = infos;

		Collections.sort(this.infos, new TrafficComparator());

		getWindow().setBackgroundDrawableResource(android.R.color.transparent);

		param = new WindowManager.LayoutParams();
		param.copyFrom(getWindow().getAttributes());
		param.width = WindowManager.LayoutParams.MATCH_PARENT;
		param.height = WindowManager.LayoutParams.MATCH_PARENT;
		param.gravity = Gravity.TOP;
		param.windowAnimations = R.style.programinfo_dialog_animation;
		getWindow().setAttributes(param);

		View view = getLayoutInflater().inflate(R.layout.popup_trafficinfo, null);
		listview = (ListView) view.findViewById(R.id.listview);
		adapter = new TrafficAdapter(context, this.infos);
		listview.setAdapter(adapter);

		close = (ImageButton) view.findViewById(R.id.close);
		close.setOnClickListener(this);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(view, param);
	}

	/**
	 * 
	 * @since 1.3.0
	 * @param context
	 * @param info
	 * @return 팝업다이얼로그
	 */
	public static TrafficInfoPopup show(Context context, List<FacilTrafficInfo> infos)
	{
		TrafficInfoPopup dialog = new TrafficInfoPopup(context, infos);

		dialog.setTitle(null);
		dialog.setCancelable(true);
		dialog.show();

		return dialog;
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.close:
			dismiss();
			break;
		}
	}

	/**
	 * 
	 * <h3><b>TrafficAdapter</b></h3></br>
	 * 
	 * @author aincc@barusoft.com
	 * @version 1.3.0
	 * @since 1.3.0
	 */
	class TrafficAdapter extends OpenAdapter<FacilTrafficInfo>
	{
		protected TrafficAdapter(Context context, List<FacilTrafficInfo> listData)
		{
			super(context, listData);
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent)
		{
			View view = convertView;
			ViewHolder viewHolder = null;
			if (null == convertView)
			{
				LayoutInflater inflater = LayoutInflater.from(context);
				view = inflater.inflate(R.layout.cell_traffic, parent, false);

				viewHolder = new ViewHolder();
				viewHolder.cellSelector = (LinearLayout) view.findViewById(R.id.cellSelector);
				viewHolder.TRAFTYPE = (TextView) view.findViewById(R.id.TRAFTYPE);
				viewHolder.TRAFTYPE.setTypeface(SeoulFont.getInstance().getSeoulHangang());
				viewHolder.TRAFINFO = (TextView) view.findViewById(R.id.TRAFINFO);
				viewHolder.TRAFINFO.setTypeface(SeoulFont.getInstance().getSeoulHangang());
				view.setTag(viewHolder);
			}
			else
			{
				viewHolder = (ViewHolder) view.getTag();
			}

			viewHolder.cellSelector.setOnClickListener(new View.OnClickListener()
			{

				@Override
				public void onClick(View v)
				{

				}
			});

			FacilTrafficInfo info = listData.get(position);
			// Logger.d("TYPE : " + info.TRAFTYPE);
			// Logger.d("INFO : " + info.TRAFINFO);
			viewHolder.TRAFTYPE.setText(SeoulUtils.getTrafficInfo(info.TRAFTYPE));
			viewHolder.TRAFINFO.setText(info.TRAFINFO);

			return view;
		}

		/**
		 * 
		 * <h3><b>ViewHolder</b></h3></br>
		 * 
		 * @author aincc@barusoft.com
		 * @version 1.3.0
		 * @since 1.3.0
		 */
		class ViewHolder
		{
			LinearLayout cellSelector;
			TextView TRAFTYPE;
			TextView TRAFINFO;
		}
	}

	/**
	 * 
	 * <h3><b>TrafficComparator</b></h3></br>
	 * 
	 * @author aincc@barusoft.com
	 * @version 1.3.0
	 * @since 1.3.0
	 */
	class TrafficComparator implements Comparator<FacilTrafficInfo>
	{

		@Override
		public int compare(FacilTrafficInfo lhs, FacilTrafficInfo rhs)
		{
			// 내림차순 ('T' 타입 최우선)
			return rhs.TRAFTYPE.compareTo(lhs.TRAFTYPE);
		}

	}
}
