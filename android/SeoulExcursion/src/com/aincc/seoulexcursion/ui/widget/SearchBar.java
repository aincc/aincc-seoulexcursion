package com.aincc.seoulexcursion.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.aincc.lib.util.Utils;
import com.aincc.seoulexcursion.R;

/**
 * 
 * <h3><b>SearchBar</b></h3></br>
 * 
 * @author aincc@barusoft.com
 * @version 1.0.0
 * @since 1.0.0
 */
public class SearchBar extends Dialog implements android.view.View.OnClickListener, OnEditorActionListener
{
	private EditText input;
	private ImageButton cancel;
	private SearchRequest listener;
	private boolean isCanceled = true;
	private WindowManager.LayoutParams param;

	/**
	 * 
	 * @since 1.0.0
	 * @param context
	 */
	private SearchBar(Context context, SearchRequest listener)
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

		View view = getLayoutInflater().inflate(R.layout.widget_search, null);
		input = (EditText) view.findViewById(R.id.input);
		input.setOnEditorActionListener(this);
		cancel = (ImageButton) view.findViewById(R.id.cancel);
		cancel.setOnClickListener(this);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(view, param);
		// getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

		this.listener = listener;

		setOnCancelListener(new OnCancelListener()
		{

			@Override
			public void onCancel(DialogInterface dialog)
			{
				isCanceled = true;
				if (null != SearchBar.this.listener)
				{
					SearchBar.this.listener.OnSearchCancel();
				}
			}
		});

		setOnDismissListener(new OnDismissListener()
		{
			@Override
			public void onDismiss(DialogInterface dialog)
			{
				if (!isCanceled && null != SearchBar.this.listener)
				{
					SearchBar.this.listener.OnSearchRequest(input.getText().toString());
				}
			}
		});
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.cancel:
		{
			isCanceled = true;
			cancel();
		}
			break;
		}
	}

	@Override
	public boolean onEditorAction(TextView tv, int actionId, KeyEvent ev)
	{
		searchRequest();
		return false;
	}

	/**
	 * 
	 * @since 1.0.0
	 */
	private void searchRequest()
	{
		if (Utils.isEmpty(input.getText().toString()))
		{
			Toast.makeText(getContext(), "검색어를 입력하세요.", Toast.LENGTH_SHORT).show();
		}
		else
		{
			isCanceled = false;
			dismiss();
		}
	}

	/**
	 * 
	 * @since 1.0.0
	 * @param context
	 * @param listener
	 * @return
	 */
	public static SearchBar show(Context context, SearchRequest listener)
	{
		SearchBar dialog = new SearchBar(context, listener);

		dialog.setTitle(null);
		dialog.setCancelable(true);
		dialog.show();
		return dialog;
	}

	/**
	 * 
	 * <h3><b>SearchRequest</b></h3></br>
	 * 
	 * @author aincc@barusoft.com
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	public interface SearchRequest
	{
		/**
		 * 검색어 입력 후 검색요청이 발생한 경우
		 * 
		 * @since 1.0.0
		 * @param keyword
		 */
		public void OnSearchRequest(String keyword);

		/**
		 * 검색화면을 종료하는 경우
		 * 
		 * @since 1.0.0
		 */
		public void OnSearchCancel();
	}

}
