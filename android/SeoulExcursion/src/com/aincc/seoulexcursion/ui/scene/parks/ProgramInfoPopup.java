package com.aincc.seoulexcursion.ui.scene.parks;

import org.apache.commons.lang3.StringUtils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.aincc.util.URLImageParser;
import com.aincc.seoulexcursion.R;
import com.aincc.seoulexcursion.util.SeoulFont;
import com.aincc.seoulopenapi.model.ParkProgramInfo;

/**
 * 
 * <h3><b>ProgramInfoPopup</b></h3></br>
 * 
 * 프로그램 팝업
 * 
 * @author aincc@barusoft.com
 * @version 1.0.0
 * @since 1.0.0
 */
public class ProgramInfoPopup extends Dialog implements android.view.View.OnClickListener
{
	private TextView P_NAME;
	private TextView P_EDUDAY;
	private TextView P_PRODAY;
	private TextView P_EDUTIME;
	private TextView P_EDUPERSON;
	private TextView P_EAMAX;
	private TextView P_LIST_CONTENT;
	private ImageButton callphone;
	private ImageButton close;
	private WindowManager.LayoutParams param;
	private Context context;
	private ParkProgramInfo info;

	/**
	 * 
	 * @since 1.0.0
	 * @param context
	 */
	private ProgramInfoPopup(Context context, ParkProgramInfo info)
	{
		super(context);
		this.context = context;
		this.info = info;
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);

		param = new WindowManager.LayoutParams();
		param.copyFrom(getWindow().getAttributes());
		param.width = WindowManager.LayoutParams.MATCH_PARENT;
		param.height = WindowManager.LayoutParams.MATCH_PARENT;
		param.gravity = Gravity.TOP;
		param.windowAnimations = R.style.programinfo_dialog_animation;
		getWindow().setAttributes(param);

		View view = getLayoutInflater().inflate(R.layout.popup_programinfo, null);
		P_NAME = (TextView) view.findViewById(R.id.P_NAME);
		P_EDUDAY = (TextView) view.findViewById(R.id.P_EDUDAY);
		P_PRODAY = (TextView) view.findViewById(R.id.P_PRODAY);
		P_EDUTIME = (TextView) view.findViewById(R.id.P_EDUTIME);
		P_EDUPERSON = (TextView) view.findViewById(R.id.P_EDUPERSON);
		P_EAMAX = (TextView) view.findViewById(R.id.P_EAMAX);
		P_LIST_CONTENT = (TextView) view.findViewById(R.id.P_LIST_CONTENT);

		P_NAME.setTypeface(SeoulFont.getInstance().getSeoulHangang());
		P_EDUDAY.setTypeface(SeoulFont.getInstance().getSeoulHangang());
		P_PRODAY.setTypeface(SeoulFont.getInstance().getSeoulHangang());
		P_EDUTIME.setTypeface(SeoulFont.getInstance().getSeoulHangang());
		P_EDUPERSON.setTypeface(SeoulFont.getInstance().getSeoulHangang());
		P_EAMAX.setTypeface(SeoulFont.getInstance().getSeoulHangang());
		P_LIST_CONTENT.setTypeface(SeoulFont.getInstance().getSeoulHangang());

		P_NAME.setText(info.P_NAME);
		P_EDUDAY.setText(context.getResources().getString(R.string.program_term) + " " + info.P_EDUDAY_S + " ~ " + info.P_EDUDAY_E);
		P_PRODAY.setText(context.getResources().getString(R.string.program_day) + " " + info.P_PRODAY);
		P_EDUTIME.setText(context.getResources().getString(R.string.program_time) + " " + info.P_EDUTIME);
		P_EDUPERSON.setText(context.getResources().getString(R.string.program_person) + " " + info.P_EDUPERSON);
		P_EAMAX.setText(context.getResources().getString(R.string.program_max) + " " + info.P_EAMAX);

		StringBuilder sb = new StringBuilder();
		sb.append("<pre>");
		sb.append(convertHtmlInfo(StringUtils.trim(info.P_CONTENT)));
		sb.append("</pre>");
		P_LIST_CONTENT.setText(Html.fromHtml(sb.toString(), new URLImageParser(P_LIST_CONTENT, context), null));
		callphone = (ImageButton) view.findViewById(R.id.callphone);
		callphone.setOnClickListener(this);

		close = (ImageButton) view.findViewById(R.id.close);
		close.setOnClickListener(this);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(view, param);
	}

	/**
	 * HTML 문자열을 변환한다.<br>
	 * ■,* 특수기호 앞에 개행태그를 추가하고,<br>
	 * 개행문자는 개행태그로 변환한다.
	 * 
	 * @since 1.0.0
	 * @param source
	 * @return the html string
	 */
	private String convertHtmlInfo(String source)
	{
		// String[] findList =
		// { "■", "**", "*", "\n", "-&gt;", "-" };
		// String[] replList =
		// { "<br>■", "※", "<br>●", "<br>", "→", "<br>-" };
		// return StringUtils.replaceEach(source, findList, replList);
		return source.replaceAll(Character.toString((char) 10), "<br>");
	}

	/**
	 * 
	 * @since 1.0.0
	 * @param context
	 * @param info
	 * @return
	 */
	public static ProgramInfoPopup show(Context context, ParkProgramInfo info)
	{
		ProgramInfoPopup dialog = new ProgramInfoPopup(context, info);

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
		case R.id.callphone:
			// 전화 다이얼러 호출
			Intent callIntent = new Intent(Intent.ACTION_DIAL);
			callIntent.setData(Uri.parse("tel:" + info.P_ADMINTEL));
			context.startActivity(callIntent);
			break;
		case R.id.close:
			dismiss();
			break;
		}
	}
}
