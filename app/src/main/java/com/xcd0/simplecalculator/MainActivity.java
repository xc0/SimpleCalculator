package com.xcd0.simplecalculator;

import android.graphics.Color;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.view.Gravity;
import android.widget.Button;

import android.text.Html;

import android.text.Layout.Alignment;
//import android.graphics.Typeface;
//import android.view.ViewGroup.MarginLayoutParams;

import com.xcd0.simplecalculator.Common;

public class MainActivity extends AppCompatActivity {
	
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
	private String[] pre = {"", ""};
	
	String[] tmp = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ".", "AC", "BS", "=", "+", "-", "×", "÷", "%", "^", "(", ")", "ANS" };
	
	private String bLabel[] =
			{ "AC", "ANS", "BS", "(", ")"
			, "7", "8", "9", "%", "^"
			, "4", "5", "6", "×", "÷"
			, "1", "2", "3", "+", "-"
			, "0", ".", "=" };
	private final int MP = LinearLayout.LayoutParams.MATCH_PARENT;
	private Common common;
	
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		
		makeMainLayout();
		
		for( int i = 0; i < buttonRow.length; i++ ) {
			buttonRow[ i ] = new LinearLayout( this );
			buttonRow[ i ].setOrientation( LinearLayout.HORIZONTAL );
			LinearLayout.LayoutParams br = new LinearLayout.LayoutParams( MP, 0 );
			br.weight = 1;
			buttonRow[ i ].setGravity( Gravity.CENTER_HORIZONTAL );
			buttonRow[ i ].setLayoutParams( br );
			buttonRow[ i ].setPadding( 0, dp1, 0, 0 );
			lowerView.addView( buttonRow[ i ] );
			
			for( int j = 0; j < 5; j++ ) {
				int num = i * 5 + j;
				if( num >= this.bLabel.length )
					break;
				button[ j ] = new Button( this );
				//button[j].setText(String.valueOf(num));
				//button[j].setTag(String.valueOf(num));
				button[ j ].setText( bLabel[ num ] );
				button[ j ].setTag( bLabel[ num ] );
				button[ j ].setTextSize( 10 * dp1 );
				button[ j ].setBackgroundColor( Color.rgb( 255, 255, 255 ) );
				
				LinearLayout.LayoutParams bl = new LinearLayout.LayoutParams( 0, MP );
				bl.weight = 1;
				//if( b0w < 0 && num == 16 )
				if( num == 20 || num == 22 ){
					bl.weight = 2;
				}
				button[ j ].setLayoutParams( bl );
				buttonRow[ i ].addView( button[ j ] );
				//if(false){
				if( num == 22 )
					num = 22;
				if( j != 4 ) {
					LinearLayout empty = new LinearLayout( this );
					empty.setOrientation( LinearLayout.HORIZONTAL );
					LinearLayout.LayoutParams zero = new LinearLayout.LayoutParams( dp1, MP );
					empty.setLayoutParams( zero );
					buttonRow[ i ].addView( empty );
				}
				
				button[ j ].setOnClickListener( new View.OnClickListener() {
					public void onClick( View view ) {
						MainActivity.this.buttonClicked( view.getTag().toString() );
					}
				} );
			}
		}
		
	}
	
	public void buttonClicked( String text ) {
		
		String[] out;
		out = MainActivity.this.common.MainProcess( text );
		makeLine( text, out );
	}
	
	private void makeLine( String text, String[] out ) {
		boolean lineBreakFlag = false;
		// 3回ACタップで履歴消去
		if( text.equals( "AC" ) && this.pre[0].equals( "AC" ) && this.pre[1].equals( "AC" ) ) {
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
		//this.output[ pNum ].setText( out[ 1 ] );
		
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
	}
	
	private void viewMaker( int pNum ) {
		
		float upperFontSize = 11 * dp;
		int fontColor = 0xff000000;
		
		// 1行分の表示をまとめる要素を作成
		this.inputRow[ pNum ] = new LinearLayout( this );
		this.inputRow[ pNum ].setOrientation( LinearLayout.HORIZONTAL );
		LinearLayout.LayoutParams ir = new LinearLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, MP );
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
		
		
		// 入力文字列を表示する要素を作成
		this.inputView[ pNum ] = new TextView( this );
		this.inputView[ pNum ].setGravity( Gravity.LEFT );
		this.inputView[ pNum ].setTextColor( fontColor );
		this.inputView[ pNum ].setTextSize( upperFontSize );
		LinearLayout.LayoutParams io = new LinearLayout.LayoutParams( 0, MP );
		io.weight = 9;
		this.inputView[ pNum ].setLayoutParams( io );
	}
	
	private void viewAdder( int pNum ) {
		// 行番号を表示する要素lineNumを1行分の要素をまとめるinputRowに追加
		//this.inputRowLeft[ pNum ].addView( this.lineNum[ pNum ] );
		//this.inputRowLeft[ pNum ].addView( this.inputView[ pNum ] );
		//this.inputRow[ pNum ].addView( this.inputRowLeft[ pNum ] );
		//this.inputRowRight[ pNum ].addView( this.outFigure );
		//this.inputRowRight[ pNum ].addView( this.output[ pNum ] );
		//this.inputRow[ pNum ].addView( this.inputRowRight[ pNum ] );
		//this.inputRow[ pNum ].addView( this.inputRowRight[ pNum ] );
		
		
		this.inputRow[ pNum ].addView( this.lineNum[ pNum ] );
		this.inputRow[ pNum ].addView( this.inputView[ pNum ] );
		
		// 1行分の表示をまとめる要素inputRowをupperViewのupperScrollViewに追加する
		this.upperScrollView.addView( this.inputRow[ pNum ] );
		
		scrollView.post( new Runnable() {
			public void run() {
				MainActivity.this.scrollView.fullScroll( View.FOCUS_DOWN );
			}
		} );
	}
	
	public String getpreInput() {
		return ( String ) inputView[ ( this.pNum + 99 ) % 100 ].getText();
		
	}
	
	private void viewResetter() {
		
		this.upperScrollView.removeAllViews();
		this.pNum = -1;
	}
	
	private void makeMainLayout(){
		
		this.common = new Common( this );
		
		this.mainView = new LinearLayout( this );
		this.upperView = new LinearLayout( this );
		this.scrollView = new ScrollView( this );
		this.upperScrollView = new LinearLayout( this );
		this.lowerView = new LinearLayout( this );
		
		//setContentView(R.layout.activity_main);
		
		//LinearLayout mainView = new LinearLayout(this);
		mainView.setOrientation( LinearLayout.VERTICAL );
		mainView.setLayoutParams( new LinearLayout.LayoutParams( MP, MP ) );
		mainView.setGravity( Gravity.CENTER );
		setContentView( mainView );
		
		//LinearLayout upperView = new LinearLayout(this);
		upperView.setOrientation( LinearLayout.VERTICAL );
		LinearLayout.LayoutParams uv = new LinearLayout.LayoutParams( MP, 0 );
		uv.weight = 3.0f;
		upperView.setBackgroundColor( Color.rgb( 240, 240, 240 ) );
		upperView.setLayoutParams( uv );
		
		this.dp = getResources().getDisplayMetrics().density;
		this.dp1 = ( int ) dp;
		//ScrollView scrollView = new ScrollView(this);
		upperView.addView( scrollView, new LinearLayout.LayoutParams( MP, MP ) );
		
		upperScrollView.setOrientation( LinearLayout.VERTICAL );
		LinearLayout.LayoutParams usv = new LinearLayout.LayoutParams( MP, 0 );
		usv.weight = 3.0f;
		upperScrollView.setBackgroundColor( Color.rgb( 240, 240, 240 ) );
		upperScrollView.setLayoutParams( usv );
		scrollView.addView( upperScrollView, new LinearLayout.LayoutParams( MP, ViewGroup.LayoutParams.WRAP_CONTENT ) );
		
		
		//LinearLayout lowerView = new LinearLayout(this);
		lowerView.setOrientation( LinearLayout.VERTICAL );
		LinearLayout.LayoutParams lv = new LinearLayout.LayoutParams( MP, 0 );
		lv.weight = 5.0f;
		lowerView.setBackgroundColor( Color.rgb( 240, 240, 240 ) );
		lowerView.setLayoutParams( lv );
		
		
		setContentView( mainView );
		mainView.addView( upperView, uv );
		mainView.addView( lowerView, lv );
		
	}
	
	private void inputKeep( String text ){
		this.pre[1] = this.pre[0];
		this.pre[0] = text;
	}
}
