package com.xcd0.simplecalculator;

/**
 * Created by xc0 on 2017/07/03.
 */


import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;

public class FontFitButton extends android.support.v7.widget.AppCompatButton{
	
	/** 最小のテキストサイズ */
	private static final float MIN_TEXT_SIZE = 8f;
	
	/**
	 * コンストラクタ
	 * @param context
	 */
	public FontFitButton(Context context) {
		super(context);
	}
	
	/**
	 * コンストラクタ
	 * @param context
	 * @param attrs
	 */
	public FontFitButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		
		resize();
		
	}
	
	/**
	 * テキストサイズ調整
	 */
	public void resize() {
		
		Paint paint = new Paint();
		
		// Viewの幅
		int viewWidth = this.getWidth();
		int viewHeight = this.getHeight();
		
		if( viewHeight == 0 || viewWidth == 0 )
			return;
		
		// テキストサイズ
		float textSize = 100f;
		
		paint.setTextSize(textSize);
		
		
		// テキストの横幅取得
		float textWidth = paint.measureText(this.getText().toString());
				
		Paint.FontMetrics fm = paint.getFontMetrics();
		float textHeight = (float) (Math.abs(fm.top)) + (Math.abs(fm.descent));
		
		
		// Paintにテキストサイズ設定
		paint.setTextSize(textSize);
		
		
		
		while( viewWidth < textWidth + 10 ) {
			
			if (MIN_TEXT_SIZE >= textSize) {
				textSize = MIN_TEXT_SIZE;
				
				setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
				break;
			}
			
			textSize--;
			setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
			paint.setTextSize(textSize);
			textWidth = paint.measureText(this.getText().toString());
			
		}
		
		
		while( viewHeight < textHeight + 15 ) {
			
			if (MIN_TEXT_SIZE >= textSize) {
				textSize = MIN_TEXT_SIZE;
				
				setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
				break;
			}
			
			textSize--;
			setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
			paint.setTextSize(textSize);
			
			fm = paint.getFontMetrics();
			//textHeight = (float) (Math.abs(fm.top)) + (Math.abs(fm.descent));
			textHeight = (float) (Math.abs(fm.top)) + (Math.abs(fm.bottom));
			
		}
		
		setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize-5);
		if ( getText().equals( "ANS" ) )
			setTextSize( TypedValue.COMPLEX_UNIT_PX, textSize );
		if ( getText().equals( "AC" ) || getText().equals( "BS" ) )
			setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize - 17);
		if ( getText().equals( "(" ) || getText().equals( ")" ) )
			setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize - 16);
		return;
	}
	
}