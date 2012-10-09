package com.aincc.seoulexcursion.ui.widget;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.aincc.seoulexcursion.R;
import com.aincc.seoulexcursion.ui.Constants;
import com.aincc.seoulexcursion.ui.control.OpenAdapter;
import com.aincc.seoulexcursion.ui.widget.Navibar.NavibarStyle;
import com.aincc.seoulexcursion.util.SeoulFont;

/**
 * 
 * <h3><b>Navisheet</b></h3></br>
 * 
 * Navibar 에서 옵션기능 선택시 표시할 다이얼로그
 * 
 * @author aincc@barusoft.com
 * @version 1.0.0
 * @since 1.0.0
 */
public class Navisheet extends Dialog implements android.view.View.OnClickListener
{
	private ListView optionlistview1;
	private ListView optionlistview2;
	private ImageButton optionOk;
	private ImageButton optionCancel;
	private OptionSelected listener;
	private List<Pair<String, List<String>>> options;
	private int optionSelected1 = Constants.OPTION_INVALID_INDEX;
	private int optionSelected2 = Constants.OPTION_INVALID_INDEX;
	private boolean isCanceled = true;
	private WindowManager.LayoutParams param;
	private NavibarStyle style;

	/**
	 * 
	 * @since 1.0.0
	 * @param context
	 */
	private Navisheet(Context context, NavibarStyle style, List<Pair<String, List<String>>> options, int optionSelected1, int optionSelected2, OptionSelected listener)
	{
		super(context);
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);

		param = new WindowManager.LayoutParams();
		param.copyFrom(getWindow().getAttributes());
		param.width = WindowManager.LayoutParams.MATCH_PARENT;
		param.height = WindowManager.LayoutParams.MATCH_PARENT;
		param.gravity = Gravity.TOP;
		param.windowAnimations = R.style.option_dialog_animation;
		// param.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		getWindow().setAttributes(param);

		View view = getLayoutInflater().inflate(R.layout.widget_navisheet, null);
		optionlistview1 = (ListView) view.findViewById(R.id.sheet_optionlist1);
		optionlistview2 = (ListView) view.findViewById(R.id.sheet_optionlist2);
		optionOk = (ImageButton) view.findViewById(R.id.sheet_ok);
		optionOk.setOnClickListener(this);
		optionCancel = (ImageButton) view.findViewById(R.id.sheet_cancel);
		optionCancel.setOnClickListener(this);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(view, param);

		this.listener = listener;
		this.options = options;
		this.style = style;
		switch (style)
		{
		case OPTION2:
		{
			setOption1(optionSelected1);
			setOption2(optionSelected2);
			optionlistview1.setSelection(optionSelected1);
			optionlistview2.setSelection(optionSelected2);
		}
			break;
		case OPTION1:
		{
			setOption1(optionSelected1);
			optionlistview1.setSelection(optionSelected1);
			optionlistview2.setVisibility(View.GONE);
		}
			break;
		default:
			break;
		}

		setOnDismissListener(new OnDismissListener()
		{
			@Override
			public void onDismiss(DialogInterface dialog)
			{
				if (!isCanceled && null != Navisheet.this.listener)
				{
					Navisheet.this.listener.OnOptionSelected(Navisheet.this.optionSelected1, Navisheet.this.optionSelected2);
				}
			}
		});
	}

	/**
	 * 
	 * @since 1.0.0
	 * @param optionSelected1
	 * @return
	 */
	private OptionAdapter setOption1(int optionSelected1)
	{
		this.optionSelected1 = optionSelected1;
		ArrayList<String> optionlist1 = new ArrayList<String>();
		int size = options.size();
		for (int ii = 0; ii < size; ii++)
		{
			optionlist1.add(options.get(ii).first);
		}
		OptionAdapter adapter = new OptionAdapter(getContext(), optionlist1, 1);
		optionlistview1.setAdapter(adapter);
		return adapter;
	}

	/**
	 * 
	 * @since 1.0.0
	 * @param optionSelected2
	 * @return
	 */
	private OptionAdapter setOption2(int optionSelected2)
	{
		this.optionSelected2 = optionSelected2;
		ArrayList<String> optionlist2 = new ArrayList<String>();
		int size = options.get(optionSelected1).second.size();
		if (0 == size)
		{
			this.optionSelected2 = Constants.OPTION_INVALID_INDEX;
		}
		for (int ii = 0; ii < size; ii++)
		{
			optionlist2.add(options.get(optionSelected1).second.get(ii));
		}
		OptionAdapter adapter = new OptionAdapter(getContext(), optionlist2, 2);
		optionlistview2.setAdapter(adapter);
		return adapter;
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.sheet_ok:
			isCanceled = false;
			dismiss();
			break;
		case R.id.sheet_cancel:
			isCanceled = true;
			cancel();
			break;
		}
	}

	/**
	 * 
	 * @since 1.0.0
	 * @param context
	 * @param style
	 * @param options
	 * @param optionSelected1
	 * @param optionSelected2
	 * @param cancelable
	 * @param cancelListener
	 * @param listener
	 * @return
	 */
	public static Navisheet show(Context context, NavibarStyle style, List<Pair<String, List<String>>> options, int optionSelected1, int optionSelected2, OptionSelected listener)
	{
		Navisheet dialog = new Navisheet(context, style, options, optionSelected1, optionSelected2, listener);

		dialog.setTitle(null);
		dialog.setCancelable(true);
		dialog.show();

		return dialog;
	}

	/**
	 * 
	 * <h3><b>OptionAdapter</b></h3></br>
	 * 
	 * @author aincc@barusoft.com
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	class OptionAdapter extends OpenAdapter<String>
	{
		// 1, 2
		int option;

		protected OptionAdapter(Context context, List<String> listData, int option)
		{
			super(context, listData);
			this.option = option;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent)
		{
			View view = convertView;
			ViewHolder viewHolder = null;
			if (null == convertView)
			{
				LayoutInflater inflater = LayoutInflater.from(context);
				view = inflater.inflate(R.layout.cell_option, parent, false);

				viewHolder = new ViewHolder();
				viewHolder.cellSelector = (LinearLayout) view.findViewById(R.id.cellSelector);
				viewHolder.name = (TextView) view.findViewById(R.id.name);
				viewHolder.name.setTypeface(SeoulFont.getInstance().getSeoulHangang());
				viewHolder.check = (CheckBox) view.findViewById(R.id.selected);
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
					switch (option)
					{
					case 1:
						optionSelected1 = position;
						switch (style)
						{
						case OPTION2:
						{
							setOption2(Constants.OPTION_START_INDEX).notifyDataSetChanged();
						}
							break;
						default:
							break;
						}

						break;
					case 2:
						optionSelected2 = position;
						break;
					}
					notifyDataSetChanged();
				}
			});

			String name = listData.get(position);
			viewHolder.name.setText(name);

			switch (option)
			{
			case 1:
				viewHolder.check.setChecked(position == optionSelected1);
				break;
			case 2:
				viewHolder.check.setChecked(position == optionSelected2);
				break;
			}

			return view;
		}

		/**
		 * 
		 * <h3><b>ViewHolder</b></h3></br>
		 * 
		 * @author aincc@barusoft.com
		 * @version 1.0.0
		 * @since 1.0.0
		 */
		class ViewHolder
		{
			LinearLayout cellSelector;
			TextView name;
			CheckBox check;
		}
	}

	/**
	 * 
	 * <h3><b>OptionSelected</b></h3></br>
	 * 
	 * @author aincc@barusoft.com
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	public interface OptionSelected
	{
		public void OnOptionSelected(int optionSelected1, int optionSelected2);
	}

}
