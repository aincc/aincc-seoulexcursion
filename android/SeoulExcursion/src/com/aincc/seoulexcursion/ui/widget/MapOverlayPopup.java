package com.aincc.seoulexcursion.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aincc.seoulexcursion.R;
import com.aincc.seoulexcursion.util.SeoulFont;

/**
 * 
 * <h3><b>MapOverlayPopup</b></h3></br>
 * 
 * @author aincc@barusoft.com
 * @version 1.0.0
 * @since 1.0.0
 */
public class MapOverlayPopup extends LinearLayout implements OnClickListener
{
	private static final String ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android";

	/**
	 * 레이아웃
	 */
	private static final int LAYOUT = R.layout.widget_map_overlay_popup;

	/**
	 * 팝업값
	 */
	private Object value;

	/**
	 * 클릭리스너
	 */
	private OnClickListener l;

	/**
	 * 팝업메시지
	 */
	private TextView textView;

	/**
	 * 
	 * @since 1.0.0
	 * @param context
	 */
	public MapOverlayPopup(Context context)
	{
		super(context);
		applyLayout(context, "", "16sp", LAYOUT);
	}

	/**
	 * 
	 * @since 1.0.0
	 * @param context
	 * @param attrs
	 */
	public MapOverlayPopup(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		int textResId = attrs.getAttributeResourceValue(ANDROID_SCHEMA, "text", 0);
		String text = "";
		if (textResId == 0)
		{
			text = attrs.getAttributeValue(ANDROID_SCHEMA, "text");
		}
		else
		{
			text = context.getString(textResId);
		}
		String textSize = attrs.getAttributeValue(ANDROID_SCHEMA, "textSize");
		applyLayout(context, text, textSize, LAYOUT);
	}

	/**
	 * 
	 * @since 1.0.0
	 * @param text
	 * @param textSize
	 * @param layoutResId
	 */
	private void applyLayout(Context context, CharSequence text, CharSequence textSize, int layoutResId)
	{
		LayoutInflater.from(getContext()).inflate(layoutResId, this);
		textView = (TextView) findViewById(R.id.popup_name);
		textView.setTypeface(SeoulFont.getInstance().getSeoulHangang());
		findViewById(R.id.popup_layout).setOnClickListener(this);
		setText(text);
		setTextSize(textSize);
	}

	/**
	 * 
	 * @since 1.0.0
	 * @return
	 */
	public CharSequence getText()
	{
		return textView.getText();
	}

	/**
	 * 
	 * @since 1.0.0
	 * @param text
	 */
	public void setText(CharSequence text)
	{
		textView.setText(text);
	}

	/**
	 * 
	 * @since 1.0.0
	 * @return
	 */
	public float getTextSize()
	{
		return textView.getTextSize();
	}

	/**
	 * 
	 * @since 1.0.0
	 * @param textSize
	 */
	public void setTextSize(CharSequence textSize)
	{
		float size = 13.0f;
		int unit = TypedValue.COMPLEX_UNIT_SP;

		try
		{
			String value = textSize.toString().toLowerCase();
			if (value.indexOf("sp") >= 0)
			{
				value = value.replace("sp", "").trim();
				size = Float.parseFloat(value);
				unit = TypedValue.COMPLEX_UNIT_SP;
			}
			else if (value.indexOf("dp") >= 0)
			{
				value = value.replace("dp", "").trim();
				size = Float.parseFloat(value);
				unit = TypedValue.COMPLEX_UNIT_DIP;
			}
			else if (value.indexOf("dip") >= 0)
			{
				value = value.replace("dip", "").trim();
				size = Float.parseFloat(value);
				unit = TypedValue.COMPLEX_UNIT_DIP;
			}
			else if (value.indexOf("px") >= 0)
			{
				value = value.replace("px", "").trim();
				size = Float.parseFloat(value);
				unit = TypedValue.COMPLEX_UNIT_PX;
			}
		}
		catch (Exception e)
		{

		}

		textView.setTextSize(unit, size);
	}

	/**
	 * 
	 * @since 1.0.0
	 * @param obj
	 */
	public void setValue(Object obj)
	{
		value = obj;
	}

	/**
	 * 
	 * @since 1.0.0
	 * @return
	 */
	public Object getValue()
	{
		return value;
	}

	@Override
	public void setOnClickListener(OnClickListener l)
	{
		this.l = l;
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.popup_layout:
			if (null != l)
			{
				l.onClick(this);
			}
			break;
		}
	}

}
