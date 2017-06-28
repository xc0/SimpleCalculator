package com.xcd0.simplecalculator;

import android.graphics.Color;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
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
	
	private int b0w = -1;
	private int pNum = -1;
	private int dp1 = 0;
	private float dp = 0;
	
	private LinearLayout mainView;
	
	private LinearLayout upperView;
	private ScrollView scrollView;
	private LinearLayout upperScrollView;
	private LinearLayout[] inputRow = new LinearLayout[ 100 ];
	private LinearLayout[] inputRowLeft = new LinearLayout[ 100 ];
	private LinearLayout[] inputRowRight = new LinearLayout[ 100 ];
	private TextView[] lineNum = new TextView[ 100 ];
	private TextView[] inputView = new TextView[ 100 ];
	private TextView[] output = new TextView[ 100 ];
	
	
	private LinearLayout lowerView;
	private LinearLayout[] buttonRow = new LinearLayout[ 5 ];
	private Button[] button = new Button[ buttonRow.length * 4 ];
	
	private String bLabel[] = { "AC", "±", "%", "÷", "7", "8", "9", "×", "4", "5", "6", "-", "1", "2", "3", "+", "0", ".", "=" };
	
	private final int MP = LinearLayout.LayoutParams.MATCH_PARENT;
	private Common common;
	
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
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
		
		
		for( int i = 0; i < buttonRow.length; i++ ) {
			buttonRow[ i ] = new LinearLayout( this );
			buttonRow[ i ].setOrientation( LinearLayout.HORIZONTAL );
			LinearLayout.LayoutParams br = new LinearLayout.LayoutParams( MP, 0 );
			br.weight = 1;
			buttonRow[ i ].setLayoutParams( br );
			buttonRow[ i ].setPadding( 0, dp1, 0, 0 );
			lowerView.addView( buttonRow[ i ] );
			
			for( int j = 0; j < 4; j++ ) {
				int num = i * 4 + j;
				if( num == 19 )
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
				if( b0w < 0 && num == 16 )
					bl.weight = 2;
				button[ j ].setLayoutParams( bl );
				buttonRow[ i ].addView( button[ j ] );
				//if(false){
				if( j != 3 ) {
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
		/*
			表示したい情報
			行番号 入力文字列   出力値
			out[7]
			[0] break line 1:0
			[1] input   flag
			[2] input value
			[3] output  flag
			[4] output value
			[5] nextState
		*/
		
		makeLine( text, out );
		
		
	}
	
	private void makeLine( String text, String[] out ) {
		boolean lineBreakFlag = false;
		/*
			表示したい情報
			行番号 入力文字列   出力値
			out[7]
			[0] break line 1:0
			[1] input   flag
			[2] input line
			[3] output  flag
			[4] output line
			[5] acFlag
		*/
		if( this.pNum == 99 ) {
			// 行番号をリセット
			viewResetter();
		}
		if( this.pNum == -1  || out[0] == "1" || out[5] == "AC" ) {
			lineBreakFlag = true;
			if( out[5] == "AC" && pNum != -1 ) {
				String tmp;
				tmp = this.inputView[ this.pNum ].getText() + " \\";
				this.inputView[ pNum ].setText( tmp );
			}
			this.pNum++;
			viewMaker( pNum );
			this.lineNum[ pNum ].setText( Integer.toString( this.pNum ) + ": " );
		}
		if( out[1] == "1" ){
			this.inputView[ pNum ].setText( Html.fromHtml( out[ 2 ] ) );
		}
		if( out[3] == "1" ){
			this.output[ pNum ].setText( out[ 4 ] );
		}else{
			this.output[ pNum ].setText( "" );
		}
		if( lineBreakFlag ) {
			viewAdder( this.pNum );
		}
	}
	
	private void viewMaker( int pNum ) {
		
		float upperFontSize = 10 * dp;
		int fontColor = 0xff000000;
		
		// 1行分の表示をまとめる要素を作成
		this.inputRow[ pNum ] = new LinearLayout( this );
		this.inputRow[ pNum ].setOrientation( LinearLayout.HORIZONTAL );
		LinearLayout.LayoutParams ir = new LinearLayout.LayoutParams( MP, 0 );
		this.inputRow[ pNum ].setPadding( 0, dp1, 0, 0 );
		ir.weight = 1;
		this.inputRow[ pNum ].setLayoutParams( ir );
		
		this.inputRowLeft[ pNum ] = new LinearLayout( this );
		this.inputRowLeft[ pNum ].setOrientation( LinearLayout.HORIZONTAL );
		this.inputRowLeft[ pNum ].setGravity( Gravity.LEFT );
		this.inputRowLeft[ pNum ].setPadding( 0, dp1, 0, 0 );
		LinearLayout.LayoutParams irl = new LinearLayout.LayoutParams( 0, MP );
		irl.weight = 1;
		this.inputRowLeft[ pNum ].setLayoutParams( irl );
		
		this.inputRowRight[ pNum ] = new LinearLayout( this );
		this.inputRowRight[ pNum ].setOrientation( LinearLayout.HORIZONTAL );
		this.inputRowRight[ pNum ].setGravity( Gravity.BOTTOM );
		this.inputRowRight[ pNum ].setPadding( 0, dp1, 0, 0 );
		LinearLayout.LayoutParams irr = new LinearLayout.LayoutParams( 0, MP );
		irr.weight = 1;
		this.inputRowRight[ pNum ].setLayoutParams( irr );
		
		
		// 1行分の要素の先頭に行番号を表示する
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
		LinearLayout.LayoutParams io = new LinearLayout.LayoutParams( MP, MP );
		io.weight = 4;
		this.inputView[ pNum ].setLayoutParams( io );
		
		// 出力値を表示する要素を作成
		this.output[ pNum ] = new TextView( this );
		this.output[ pNum ].setGravity( Gravity.RIGHT );
		//this.output[ pNum ].setGravity( Gravity.BOTTOM );
		this.output[ pNum ].setTextColor( fontColor );
		this.output[ pNum ].setTextSize( upperFontSize );
		LinearLayout.LayoutParams op = new LinearLayout.LayoutParams( MP, MP );
		//LinearLayout.LayoutParams op = new LinearLayout.LayoutParams( 0, MP );
		//op.weight = 10;
		this.output[ pNum ].setLayoutParams( op );
	}
	
	private void viewAdder( int pNum ) {
		// 行番号を表示する要素lineNumを1行分の要素をまとめるinputRowに追加
		this.inputRowLeft[ pNum ].addView( this.lineNum[ pNum ] );
		// 入力された演算子を表示する要素inputOperatorを1行分の要素をまとめるinputRowに追加
		this.inputRowLeft[ pNum ].addView( this.inputView[ pNum ] );
		// 入力された数値を表示する要素inputNumを1行分の要素をまとめるinputRowに追加
		this.inputRowRight[ pNum ].addView( this.output[ pNum ] );
		this.inputRow[ pNum ].addView( this.inputRowLeft[ pNum ] );
		this.inputRow[ pNum ].addView( this.inputRowRight[ pNum ] );
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
		float upperFontSize = 8 * dp;
		
		System.out.println( "99" );
		// 99番目の表示をコピって最初に貼る
		String preInput, preOutput;
		preInput = ( String ) this.inputView[ 99 ].getText();
		preOutput = ( String ) this.output[ 99 ].getText();
		
		
		this.upperScrollView.removeAllViews();
		pNum = 0;
		
		viewMaker( pNum );
		
		
		this.lineNum[ pNum ].setText( Integer.toString( this.pNum ) + ": " );
		this.inputView[ pNum ].setText( preInput );
		this.output[ pNum ].setText( preOutput );
		
		
		viewAdder( this.pNum );
	}
}
