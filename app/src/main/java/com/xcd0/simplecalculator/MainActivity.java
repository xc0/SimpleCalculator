package com.xcd0.simplecalculator;

import android.support.v7.app.AppCompatActivity;

import android.os.StrictMode;
import android.os.Bundle;

import android.util.Log;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Button;

import android.text.Html;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Gravity;

public class MainActivity extends AppCompatActivity {
	
	private final int MP = LinearLayout.LayoutParams.MATCH_PARENT;
	private final int WC = LinearLayout.LayoutParams.WRAP_CONTENT;
	
	private int pNum = -1;
	private int dp1 = 0;
	private float dp = 0;
	private LinearLayout mainView;
	private LinearLayout upperView;
	private ScrollView scrollView;
	private LinearLayout upperScrollView;
	private LinearLayout[] inputRow = new LinearLayout[ 200 ];
	private TextView[] lineNum = new TextView[ 200 ];
	private TextView[] inputView = new TextView[ 200 ];
	private LinearLayout lowerView;
	private LinearLayout[] buttonRow = new LinearLayout[ 5 ];
	private Button[] button = new Button[ buttonRow.length * 5 ];
	
	private String[] pre = {"", "", ""};
	private StringCalculator SC = new StringCalculator();
	
	private LinearLayout.LayoutParams[] bl = new LinearLayout.LayoutParams[25];
	
	private Point currentDisplaySize;
	
	String[] tmp = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ".", "AC", "BS", "=", "+", "-", "×", "÷", "%", "^", "(", ")", "ANS" };
	
	private String bLabel[] =
			{ "AC", "ANS", "BS", "(", ")"
			, "7", "8", "9", "%", "^"
			, "4", "5", "6", "×", "÷"
			, "1", "2", "3", "+", "-"
			, "0", ".", "=", "", "" };
	
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		getWindow().addFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN );
		
		for( int i = 0; i < 5; i++ ){
			this.buttonRow[i] = new LinearLayout( this );
			
			for( int j = 0; j < 5; j++ ){
				this.button[i*5+j] = new Button( this );
			}
		}
		
		makeMainLayout();
		for( int i = 0; i < buttonRow.length; i++ ) {
			this.buttonRow[ i ].setOrientation( LinearLayout.HORIZONTAL );
			LinearLayout.LayoutParams br = new LinearLayout.LayoutParams( MP, 0 );
			br.weight = 1;
			this.buttonRow[ i ].setGravity( Gravity.CENTER_HORIZONTAL );
			this.buttonRow[ i ].setLayoutParams( br );
			this.buttonRow[ i ].setPadding( 0, dp1, 0, 0 );
			lowerView.addView( buttonRow[ i ] );
			
			for( int j = 0; j < 5; j++ ) {
				int num = i * 5 + j;
				if( num >= 23 )
					break;
				this.button[ num ].setText( bLabel[ num ] );
				this.button[ num ].setTag( bLabel[ num ] );
				this.button[ num ].setTextSize( 10 * dp1 );
				button[ num ].setBackgroundColor( Color.rgb( 255, 255, 255 ) );
				
				bl[ num ] = new LinearLayout.LayoutParams( 0, MP );
				this.bl[ num ].weight = 1;
				//if( b0w < 0 && num == 16 )
				if( num == 20 || num == 22 ) {
					bl[ num ].weight = 2;
				}
				button[ num ].setLayoutParams( bl[ num ] );
				buttonRow[ i ].addView( button[ num ] );
				
				if( num == 20 || num == 21 ) {
					LinearLayout empty = new LinearLayout( this );
					empty.setOrientation( LinearLayout.HORIZONTAL );
					LinearLayout.LayoutParams zero = new LinearLayout.LayoutParams( dp1, MP );
					empty.setLayoutParams( zero );
					buttonRow[ i ].addView( empty );
				}else {
					if( j != 4 || num != 22 ) {
						LinearLayout empty = new LinearLayout( this );
						empty.setOrientation( LinearLayout.HORIZONTAL );
						LinearLayout.LayoutParams zero = new LinearLayout.LayoutParams( dp1, MP );
						empty.setLayoutParams( zero );
						buttonRow[ i ].addView( empty );
					}
				}
				
				button[ num ].setOnClickListener( new View.OnClickListener() {
					public void onClick( View view ) {
						MainActivity.this.buttonClicked( view.getTag().toString() );
					}
				} );
			}
			
		}
		VTO:
		{
			ViewTreeObserver oMV = mainView.getViewTreeObserver();
			oMV.addOnGlobalLayoutListener( new ViewTreeObserver.OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					Log.d( "MainActivity : ", "mainView width  = " + mainView.getWidth() );
					Log.d( "MainActivity : ", "mainView height = " + mainView.getHeight() );
				}
			} );
			ViewTreeObserver ob0 = button[ 0 ].getViewTreeObserver();
			ob0.addOnGlobalLayoutListener( new ViewTreeObserver.OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					Log.d( "MainActivity : ", "button[0] width  = " + button[ 0 ].getWidth() );
					Log.d( "MainActivity : ", "button[0] height = " + button[ 0 ].getHeight() );
				}
			} );
			ViewTreeObserver ob1 = button[ 1 ].getViewTreeObserver();
			ob0.addOnGlobalLayoutListener( new ViewTreeObserver.OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					Log.d( "MainActivity : ", "button[1] width  = " + button[ 1 ].getWidth() );
					Log.d( "MainActivity : ", "button[1] height = " + button[ 1 ].getHeight() );
				}
			} );
			ViewTreeObserver ob19 = button[ 4 ].getViewTreeObserver();
			ob0.addOnGlobalLayoutListener( new ViewTreeObserver.OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					Log.d( "MainActivity : ", "button[19] width  = " + button[ 4 ].getWidth() );
					Log.d( "MainActivity : ", "button[19] height = " + button[ 4 ].getHeight() );
				}
			} );
			
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		switch( newConfig.orientation ) {
			case Configuration.ORIENTATION_PORTRAIT:    // 縦
				this.mainView.setOrientation( LinearLayout.VERTICAL );
				Log.d("oCCp : ", "mainView width = " + mainView.getWidth());
				Log.d("oCCp : ", "mainView height = " + mainView.getHeight());
				Log.d("oCCp : ", "button[0] width = " + button[0].getWidth());
				Log.d("oCCp : ", "button[0] height = " + button[0].getHeight());
				this.currentDisplaySize = getViewSize( this.mainView );
				break;
			case Configuration.ORIENTATION_LANDSCAPE:   // 横
				this.mainView.setOrientation( LinearLayout.HORIZONTAL );
				Log.d("oCCh : ", "mainView width = " + mainView.getWidth());
				Log.d("oCCh : ", "mainView height = " + mainView.getHeight());
				Log.d("oCCh : ", "button[0] width = " + button[0].getWidth());
				Log.d("oCCh : ", "button[0] height = " + button[0].getHeight());
				//this.mainView.setLayoutParams( new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT ) );
				this.currentDisplaySize = getViewSize( this.mainView );
				break;
			default:
				break;
		}
		super.onConfigurationChanged( newConfig );
	}
	
	public void buttonClicked( String text ) {
		String[] out;
		
		this.currentDisplaySize = getViewSize( this.mainView );
		
		out = mainProcess( text );
		
		makeLine( text, out );
	}
	
	private String[] mainProcess( String text ){
		String[] output = new String[3];
		
		if( this.pNum < 1 && text.equals( "ANS" )){
			if( this.pre[2].equals( "" ) ){
				output[1] = "";
				output[0] = SC.getInputString();
				output[2] = "0";
				return output;
			}else{
				SC.setPreAns( this.pre[2] );
			}
		}
		output[1] = SC.inputOneCharaString( text );
		output[0] = SC.getInputString();
		output[2] = Integer.toString( SC.getStatus() );
		switch( SC.getStatus() ){
			case -1:
				break;
			case 0:
				output[1] = "";
				break;
			case 1:
				break;
		}
		return output;
	}
	
	private void makeLine( String text, String[] out ) {
		boolean lineBreakFlag = false;
		// 3回ACタップで履歴消去
		if( text.equals( "AC" ) && this.pre[0].equals( "AC" ) && this.pre[1].equals( "AC" ) ) {
			this.pre[2] = SC.getOutputString();
			viewResetter();
		}
		inputKeep(text);
		if( this.pNum == 198 ) {
			// 行番号をリセット
			// 99番目の表示をコピって最初に貼る
			String preInput, preOutput;
			preInput = ( String ) this.inputView[ 198 ].getText();
			preOutput = ( String ) this.inputView[ 199 ].getText();
			viewResetter();
			this.inputView[ pNum+1 ].setText( preOutput );
			
		}
		
		// 0 input
		// 1 output
		// 2 statusCode
		if( this.pNum == -1 ){
			this.pNum++;
			viewMaker( this.pNum );
			viewMaker( this.pNum+1 );
			StringBuffer bf = new StringBuffer();
			bf.append( " " );
			bf.append( Integer.toString( this.pNum / 2 ) );
			bf.append( ": " );
			this.lineNum[ pNum ].setText( bf.toString() );
			this.lineNum[ pNum+1 ].setText( "      " );
			viewAdder( this.pNum );
			viewAdder( this.pNum + 1 );
		}
		
		this.inputView[ pNum ].setText( Html.fromHtml( out[ 0 ] ) );
		this.inputView[ pNum+1 ].setText( Html.fromHtml( out[ 1 ] ) );
		
		if( out[2].equals( "1" ) ) {
			//viewAdder( this.pNum+1 );
			// =で結果を表示した後改行
			
			this.lineNum[ pNum+1 ].setText( "   >> " );
			
			this.pNum += 2;
			viewMaker( this.pNum );
			viewMaker( this.pNum+1 );
			StringBuffer bf = new StringBuffer();
			bf.append( Integer.toString( this.pNum / 2 ) );
			bf.append( ": "  );
			this.lineNum[ pNum ].setText( bf.toString() );
			this.lineNum[ pNum+1 ].setText( "      " );
			viewAdder( this.pNum );
			viewAdder( this.pNum+1 );
		}
		
		scrollView.post( new Runnable() {
			public void run() {
				MainActivity.this.scrollView.fullScroll( View.FOCUS_DOWN );
			}
		} );
	}
	
	private void viewMaker( int pNum ) {
		
		float upperFontSize = 11 * dp;
		int fontColor = 0xff000000;
		
		// 行の表示をまとめる要素を作成
		this.inputRow[ pNum ] = new LinearLayout( this );
		this.inputRow[ pNum ].setOrientation( LinearLayout.HORIZONTAL );
		LinearLayout.LayoutParams ir = new LinearLayout.LayoutParams( WC, MP );
		this.inputRow[ pNum ].setPadding( 0, dp1, 0, 0 );
		this.inputRow[ pNum ].setLayoutParams( ir );
		
		// 行番号を表示する要素を作成
		this.lineNum[ pNum ] = new TextView( this );
		this.lineNum[ pNum ].setGravity( Gravity.RIGHT );
		this.lineNum[ pNum ].setTextColor( fontColor );
		this.lineNum[ pNum ].setTextSize( upperFontSize );
		LinearLayout.LayoutParams ln = new LinearLayout.LayoutParams( 0, MP );
		ln.weight = 1;
		this.lineNum[ pNum ].setLayoutParams( ln );
		
		// 文字列を表示する要素を作成
		this.inputView[ pNum ] = new TextView( this );
		this.inputView[ pNum ].setGravity( Gravity.LEFT );
		this.inputView[ pNum ].setTextColor( fontColor );
		this.inputView[ pNum ].setTextSize( upperFontSize );
		LinearLayout.LayoutParams io = new LinearLayout.LayoutParams( 0, MP );
		io.weight = 9;
		this.inputView[ pNum ].setLayoutParams( io );
	}
	
	private void viewAdder( int pNum ) {
		this.inputRow[ pNum ].addView( this.lineNum[ pNum ] );
		this.inputRow[ pNum ].addView( this.inputView[ pNum ] );
		
		this.upperScrollView.addView( this.inputRow[ pNum ] );
		//
	}
	
	private void viewResetter() {
		
		this.upperScrollView.removeAllViews();
		this.pNum = -1;
	}
	
	private void makeMainLayout(){
		
		this.mainView = new LinearLayout( this );
		this.upperView = new LinearLayout( this );
		this.scrollView = new ScrollView( this );
		this.upperScrollView = new LinearLayout( this );
		this.lowerView = new LinearLayout( this );
		
		this.mainView.setOrientation( LinearLayout.VERTICAL );
		LinearLayout.LayoutParams mv = new LinearLayout.LayoutParams( MP, MP );
		this.mainView.setLayoutParams( mv );
		this.mainView.setGravity( Gravity.CENTER );
		setContentView( this.mainView );
		
		this.upperView.setOrientation( LinearLayout.VERTICAL );
		LinearLayout.LayoutParams uv = new LinearLayout.LayoutParams( MP, 0 );
		uv.weight = 3;
		this.upperView.setBackgroundColor( Color.rgb( 240, 240, 240 ) );
		this.upperView.setLayoutParams( uv );
		
		this.dp = getResources().getDisplayMetrics().density;
		this.dp1 = ( int ) dp;
		upperView.addView( scrollView, new LinearLayout.LayoutParams( MP, MP ) );
		
		this.upperScrollView.setOrientation( LinearLayout.VERTICAL );
		LinearLayout.LayoutParams usv = new LinearLayout.LayoutParams( MP, 0 );
		usv.weight = 3;
		this.upperScrollView.setBackgroundColor( Color.rgb( 240, 240, 240 ) );
		this.upperScrollView.setLayoutParams( usv );
		scrollView.addView( this.upperScrollView, new LinearLayout.LayoutParams( MP, WC) );
		
		
		this.lowerView.setOrientation( LinearLayout.VERTICAL );
		LinearLayout.LayoutParams lv = new LinearLayout.LayoutParams( MP, 0 );
		lv.weight = 5;
		this.lowerView.setBackgroundColor( Color.rgb( 240, 240, 240 ) );
		this.lowerView.setLayoutParams( lv );
		
		
		setContentView( this.mainView );
		this.mainView.addView( this.upperView, uv );
		this.mainView.addView( this.lowerView, lv );
		
	}
	
	private void inputKeep( String text ){
		this.pre[1] = this.pre[0];
		this.pre[0] = text;
	}
	
	public static Point getViewSize(View View){
		Point point = new Point(0, 0);
		point.set(View.getWidth(), View.getHeight());
		
		return point;
	}
	
	public void viewSizeChecker(){
		
	}
}
