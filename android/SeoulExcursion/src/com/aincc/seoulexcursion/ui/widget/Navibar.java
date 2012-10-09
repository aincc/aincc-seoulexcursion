package com.aincc.seoulexcursion.ui.widget;

import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aincc.seoulexcursion.R;
import com.aincc.seoulexcursion.ui.Constants;
import com.aincc.seoulexcursion.ui.widget.Navisheet.OptionSelected;

/**
 * 
 * <h3><b>Navibar</b></h3></br>
 * 
 * 상단 네비게이션 바
 * 
 * @author aincc@barusoft.com
 * @version 1.0.0
 * @since 1.0.0
 * @see widget_navibar.xml
 */
public class Navibar extends LinearLayout implements OnClickListener, OptionSelected
{
	private Context context;

	/**
	 * 뒤로가기 버튼
	 */
	private ImageButton backBtn;

	/**
	 * 기능 버튼 (기본 검색아이콘)
	 */
	private ImageButton funcBtn;

	/**
	 * 제목
	 */
	private TextView titleView;

	/**
	 * 하단 구분선
	 */
	@SuppressWarnings("unused")
	private View underline;

	/**
	 * 옵션기능 레이아웃
	 */
	private LinearLayout optionLayout;

	/**
	 * 옵션기능1
	 */
	private TextView option1;

	/**
	 * 옵션기능2
	 */
	private TextView option2;

	/**
	 * 분리선
	 */
	private ImageView divider;

	/**
	 * 펼치기/접기 표시
	 */
	private ImageView arrow;

	/**
	 * 스타일
	 */
	private NavibarStyle style;

	/**
	 * 옵션 데이터
	 */
	private List<Pair<String, List<String>>> options;

	/**
	 * 옵션1 선택인덱스
	 */
	private int optionSelected1;

	/**
	 * 옵션2 선택인덱스
	 */
	private int optionSelected2;

	/**
	 * 옵션선택 리스너
	 */
	private OptionSelected listener;

	/**
	 * 
	 * @since 1.0.0
	 * @param context
	 */
	public Navibar(Context context)
	{
		super(context);

		initialize(context, null);
	}

	/**
	 * 
	 * @since 1.0.0
	 * @param context
	 * @param attrs
	 */
	public Navibar(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		initialize(context, attrs);
	}

	/**
	 * 
	 * @since 1.0.0
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public Navibar(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);

		initialize(context, attrs);
	}

	/**
	 * 초기화
	 * 
	 * @since 1.0.0
	 * @param context
	 * @param attrs
	 */
	private void initialize(Context context, AttributeSet attrs)
	{
		this.context = context;

		LayoutInflater.from(context).inflate(R.layout.widget_navibar, this);

		backBtn = (ImageButton) findViewById(R.id.navibar_back);
		funcBtn = (ImageButton) findViewById(R.id.navibar_func);
		titleView = (TextView) findViewById(R.id.navibar_title);

		optionLayout = (LinearLayout) findViewById(R.id.navibar_option);
		optionLayout.setOnClickListener(this);
		option1 = (TextView) findViewById(R.id.navibar_option1);
		option2 = (TextView) findViewById(R.id.navibar_option2);
		divider = (ImageView) findViewById(R.id.navibar_divider);
		arrow = (ImageView) findViewById(R.id.navibar_arrow);

		underline = findViewById(R.id.navibar_underline);

		if (null != attrs)
		{
			TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.Navibar, 0, 0);
			int n = array.getIndexCount();
			for (int i = 0; i < n; i++)
			{
				int attr = array.getIndex(i);

				switch (attr)
				{
				case R.styleable.Navibar_style:
					int style = array.getInt(attr, NavibarStyle.TITLE.getStyle());
					setStyle(style);
					break;
				}
			}
			array.recycle();
		}
		else
		{
			setStyle(NavibarStyle.TITLE.getStyle());
		}
	}

	/**
	 * 
	 * @since 1.0.0
	 * @param tf
	 */
	public void setTypeface(Typeface tf)
	{
		titleView.setTypeface(tf);
		option1.setTypeface(tf);
		option2.setTypeface(tf);
	}

	/**
	 * 뒤로가기 버튼 클릭리스너 등록
	 * 
	 * @since 1.0.0
	 * @param l
	 */
	public void setActionBack(OnClickListener l)
	{
		backBtn.setOnClickListener(l);
	}

	/**
	 * 기능 버튼 클릭리스너 등록
	 * 
	 * @since 1.0.0
	 * @param l
	 */
	public void setActionFunc(OnClickListener l)
	{
		funcBtn.setOnClickListener(l);
	}

	/**
	 * 기능 버튼 이미지 등록
	 * 
	 * @since 1.0.0
	 * @param d
	 */
	public void setActionFuncIcon(Drawable d)
	{
		funcBtn.setImageDrawable(d);
	}

	/**
	 * 네비게이션바 제목 설정
	 * 
	 * @since 1.0.0
	 * @param title
	 */
	public void setTitle(String title)
	{
		titleView.setText(title);
	}

	/**
	 * 뒤로가기 버튼 표시 설정
	 * 
	 * @since 1.0.0
	 * @param visibility
	 */
	public void setActionBackVisible(int visibility)
	{
		backBtn.setVisibility(visibility);
	}

	/**
	 * 기능 버튼 표시 설정
	 * 
	 * @since 1.0.0
	 * @param visibility
	 */
	public void setActionFuncVisible(int visibility)
	{
		funcBtn.setVisibility(visibility);
	}

	/**
	 * 스타일 지정
	 * 
	 * @since 1.0.0
	 * @param style
	 */
	public void setStyle(int style)
	{
		switch (style)
		{
		case 0x01:
			optionLayout.setVisibility(VISIBLE);
			option1.setVisibility(VISIBLE);
			arrow.setVisibility(VISIBLE);
			option2.setVisibility(GONE);
			divider.setVisibility(GONE);
			titleView.setVisibility(GONE);
			this.style = NavibarStyle.OPTION1;
			break;
		case 0x02:
			optionLayout.setVisibility(VISIBLE);
			option1.setVisibility(VISIBLE);
			arrow.setVisibility(VISIBLE);
			option2.setVisibility(VISIBLE);
			divider.setVisibility(VISIBLE);
			titleView.setVisibility(GONE);
			this.style = NavibarStyle.OPTION2;
			break;

		default:
		case 0x00:
			optionLayout.setVisibility(GONE);
			titleView.setVisibility(VISIBLE);
			this.style = NavibarStyle.TITLE;
			break;
		}
	}

	/**
	 * 
	 * @since 1.0.0
	 * @param listener
	 */
	public void setOptionListener(OptionSelected listener)
	{
		this.listener = listener;
	}

	/**
	 * 옵션기능 문구 설정
	 * 
	 * @since 1.0.0
	 * @param optionStr1
	 * @param optionStr2
	 */
	public void setOptionText(String optionStr1, String optionStr2)
	{
		if (null != optionStr1)
		{
			option1.setText(optionStr1);
		}

		if (null != optionStr2)
		{
			option2.setText(optionStr2);
		}
	}

	/**
	 * 옵션기능 문구 설정
	 * 
	 * @since 1.0.0
	 * @param optionSelected1
	 * @param optionSelected2
	 */
	public void setOptionText(int optionSelected1, int optionSelected2)
	{
		setOptionText(Constants.OPTION_INVALID_INDEX < optionSelected1 ? options.get(optionSelected1).first : "", Constants.OPTION_INVALID_INDEX < optionSelected2 ? options.get(optionSelected1).second.get(optionSelected2) : "");
	}

	/**
	 * 옵션 데이터 설정
	 * 
	 * @since 1.0.0
	 * @param options
	 * @param optionSelected1
	 * @param optionSelected2
	 */
	public void setOptionData(List<Pair<String, List<String>>> options, int optionSelected1, int optionSelected2)
	{
		this.options = options;
		this.optionSelected1 = optionSelected1;
		this.optionSelected2 = optionSelected2;

		setOptionText(Constants.OPTION_INVALID_INDEX < optionSelected1 ? options.get(optionSelected1).first : "", Constants.OPTION_INVALID_INDEX < optionSelected2 ? options.get(optionSelected1).second.get(optionSelected2) : "");
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.navibar_option:
			Navisheet.show(context, style, options, optionSelected1, NavibarStyle.OPTION1 == style ? Constants.OPTION_INVALID_INDEX : optionSelected2, this);
			break;
		}
	}

	@Override
	public void OnOptionSelected(int optionSelected1, int optionSelected2)
	{
		this.optionSelected1 = optionSelected1;
		this.optionSelected2 = optionSelected2;
		setOptionText(Constants.OPTION_INVALID_INDEX < optionSelected1 ? options.get(optionSelected1).first : "", Constants.OPTION_INVALID_INDEX < optionSelected2 ? options.get(optionSelected1).second.get(optionSelected2) : "");
		if (null != listener)
		{
			listener.OnOptionSelected(optionSelected1, optionSelected2);
		}
	}

	/**
	 * 
	 * <h3><b>NavibarStyle</b></h3></br>
	 * 
	 * @author aincc@barusoft.com
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	public enum NavibarStyle
	{
		/**
		 * 제목만..
		 */
		TITLE(0x00),

		/**
		 * 옵션기능1
		 */
		OPTION1(0x01),

		/**
		 * 옵션기능2
		 */
		OPTION2(0x02);

		private int style;

		NavibarStyle(int style)
		{
			this.style = style;
		}

		public int getStyle()
		{
			return style;
		}
	}

}
