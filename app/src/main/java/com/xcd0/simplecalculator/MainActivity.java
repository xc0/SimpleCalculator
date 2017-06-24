package com.xcd0.simplecalculator;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.view.Gravity;
import android.widget.Button;
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
	private TextView[] inputOperator = new TextView[ 100 ];
	private TextView[] inputNum = new TextView[ 100 ];
	private TextView[] output = new TextView[ 100 ];


	private LinearLayout lowerView;
	private LinearLayout[] buttonRow = new LinearLayout[ 5 ];
	private Button[] button = new Button[ buttonRow.length * 4 ];

	private String bLabel[] =
			{ "AC", "±", "%", "÷"
					, "7", "8", "9", "×"
					, "4", "5", "6", "-"
					, "1", "2", "3", "+"
					, "0", ".", "=" };

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
				if( num == 19 ) break;
				button[ j ] = new Button( this );
				//button[j].setText(String.valueOf(num));
				//button[j].setTag(String.valueOf(num));
				button[ j ].setText( bLabel[ num ] );
				button[ j ].setTag( bLabel[ num ] );
				button[ j ].setBackgroundColor( Color.rgb( 255, 255, 255 ) );

				LinearLayout.LayoutParams bl = new LinearLayout.LayoutParams( 0, MP );
				bl.weight = 1;
				if( b0w < 0 && num == 16 ) bl.weight = 2;
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

				button[ j ].setOnClickListener(
						new View.OnClickListener() {
							public void onClick( View view ) {
								MainActivity.this.buttonClicked( view.getTag().toString() );
							}
						} );
			}
		}

	}

	public void buttonClicked( String text ) {

		String[] out;
		out = MainActivity.this.common.MainProcess(text);

		// 行番号をインクリメント
		this.pNum++;
		// 行番号をリセット
		if( this.pNum > 99 ) {
			System.out.println("99");
			// 99番目の表示をコピって最初に貼る
			String pretext, preOutput;
			pretext = ( String ) this.inputNum[ pNum - 1 ].getText();
			preOutput = ( String ) this.output[ pNum - 1 ].getText();
			pNum = 0;

			this.inputRow[ pNum ] = new LinearLayout( this );
			this.inputRow[ pNum ].setOrientation( LinearLayout.HORIZONTAL );
			LinearLayout.LayoutParams ir = new LinearLayout.LayoutParams( MP, 0 );
			ir.weight = 1;
			this.inputRow[ pNum ].setLayoutParams( ir );
			this.inputRow[ pNum ].setPadding( 0, dp1, 0, 0 );

			this.inputRowLeft[ pNum ] = new LinearLayout( this );
			this.inputRowLeft[ pNum ].setOrientation( LinearLayout.HORIZONTAL );
			LinearLayout.LayoutParams irl = new LinearLayout.LayoutParams( 0, MP);
			irl.weight = 1;
			this.inputRowLeft[ pNum ].setLayoutParams( irl );
			this.inputRowLeft[ pNum ].setGravity( Gravity.LEFT );
			this.inputRowLeft[ pNum ].setPadding( 0, dp1, 0, 0 );

			this.inputRowRight[ pNum ] = new LinearLayout( this );
			this.inputRowRight[ pNum ].setOrientation( LinearLayout.HORIZONTAL );
			LinearLayout.LayoutParams irr = new LinearLayout.LayoutParams( 0, MP );
			irr.weight = 1;
			this.inputRowRight[ pNum ].setLayoutParams( irr );
			this.inputRowRight[ pNum ].setGravity( Gravity.RIGHT );
			this.inputRowRight[ pNum ].setPadding( 0, dp1, 0, 0 );

			this.lineNum[ pNum ] = new TextView( this );
			this.lineNum[ pNum ].setText( Integer.toString( this.pNum ) + " : " );
			this.lineNum[ pNum ].setGravity( Gravity.RIGHT );
			this.lineNum[ pNum ].setTextColor( 0xff000000 );
			this.lineNum[ pNum ].setTextSize( 10 * dp );

			this.inputOperator[ pNum ] = new TextView( this );
			this.inputOperator[ pNum ].setText( Integer.toString( this.pNum ) + " : " );
			this.inputOperator[ pNum ].setGravity( Gravity.RIGHT );
			this.inputOperator[ pNum ].setTextColor( 0xff000000 );
			this.inputOperator[ pNum ].setTextSize( 10 * dp );

			this.inputNum[ pNum ] = new TextView( this );
			this.inputNum[ pNum ].setText( pretext );
			this.inputNum[ pNum ].setGravity( Gravity.RIGHT );
			this.inputNum[ pNum ].setTextColor( 0xff000000 );
			this.inputNum[ pNum ].setTextSize( 10 * dp );

			this.output[ pNum ] = new TextView( this );
			this.output[ pNum ].setText( preOutput );
			this.output[ pNum ].setGravity( Gravity.RIGHT );
			this.output[ pNum ].setTextColor( 0xff000000 );
			this.output[ pNum ].setTextSize( 10 * dp );

			this.upperScrollView.removeAllViews();

			// 行番号を表示する要素lineNumを1行分の要素をまとめるinputRowRightに追加
			this.inputRowLeft[ pNum ].addView( this.lineNum[ pNum ] );
			// 入力された演算子を表示する要素inputOperatorを1行分の要素をまとめるinputRowに追加
			this.inputRowLeft[ pNum ].addView( this.inputOperator[ pNum ] );
			// 入力された数値を表示する要素inputNumを1行分の要素をまとめるinputRowに追加
			this.inputRowLeft[ pNum ].addView( this.inputNum[ pNum ] );
			// 入力された数値を表示する要素inputNumを1行分の要素をまとめるinputRowに追加
			this.inputRowRight[ pNum ].addView( this.output[ pNum ] );
			this.inputRow[ pNum ].addView( this.inputRowLeft[ pNum ] );
			this.inputRow[ pNum ].addView( this.inputRowRight[ pNum ] );
			// 1行分の表示をまとめる要素inputRowをupperViewのupperScrollViewに追加する
			this.upperScrollView.addView( this.inputRow[ pNum ] );
		}

		// 1行分の表示をまとめる要素を作成
		this.inputRow[ pNum ] = new LinearLayout( this );
		this.inputRow[ pNum ].setOrientation( LinearLayout.HORIZONTAL );
		LinearLayout.LayoutParams ir = new LinearLayout.LayoutParams( MP, 0 );
		ir.weight = 1;
		this.inputRow[ pNum ].setLayoutParams( ir );
		this.inputRow[ pNum ].setPadding( 0, dp1, 0, 0 );

		this.inputRowLeft[ pNum ] = new LinearLayout( this );
		this.inputRowLeft[ pNum ].setOrientation( LinearLayout.HORIZONTAL );
		LinearLayout.LayoutParams irl = new LinearLayout.LayoutParams( 0, MP );
		irl.weight = 1;
		this.inputRowLeft[ pNum ].setLayoutParams( irl );
		this.inputRowLeft[ pNum ].setGravity( Gravity.LEFT );
		this.inputRowLeft[ pNum ].setPadding( 0, dp1, 0, 0 );

		this.inputRowRight[ pNum ] = new LinearLayout( this );
		this.inputRowRight[ pNum ].setOrientation( LinearLayout.HORIZONTAL );
		LinearLayout.LayoutParams irr = new LinearLayout.LayoutParams( 0, MP );
		irr.weight = 1;
		this.inputRowRight[ pNum ].setLayoutParams( irr );
		this.inputRowRight[ pNum ].setGravity( Gravity.RIGHT );
		this.inputRowRight[ pNum ].setPadding( 0, dp1, 0, 0 );


		// 1行分の要素の先頭に行番号を表示する
		// 行番号を表示する要素を作成
		this.lineNum[ pNum ] = new TextView( this );
		this.lineNum[ pNum ].setText( Integer.toString( this.pNum ) + " : " );
		this.lineNum[ pNum ].setGravity( Gravity.RIGHT );
		this.lineNum[ pNum ].setTextColor( 0xff000000 );
		this.lineNum[ pNum ].setTextSize( 10 * dp );

		// 演算子を表示する要素を作成
		this.inputOperator[ pNum ] = new TextView( this );
		this.inputOperator[ pNum ].setText( out[0] + " : " );
		this.inputOperator[ pNum ].setGravity( Gravity.RIGHT );
		this.inputOperator[ pNum ].setTextColor( 0xff000000 );
		this.inputOperator[ pNum ].setTextSize( 10 * dp );

		// 数値を表示する要素を作成
		this.inputNum[ pNum ] = new TextView( this );
		this.inputNum[ pNum ].setText( text );
		this.inputNum[ pNum ].setGravity( Gravity.RIGHT );
		this.inputNum[ pNum ].setTextColor( 0xff000000 );
		this.inputNum[ pNum ].setTextSize( 10 * dp );

		// 数値を表示する要素を作成
		this.output[ pNum ] = new TextView( this );
		this.output[ pNum ].setText( out[1] + ":" +out[2] + ":" + out[3] + out[4] + out[5] );
		this.output[ pNum ].setGravity( Gravity.RIGHT );
		this.output[ pNum ].setTextColor( 0xff000000 );
		this.output[ pNum ].setTextSize( 10 * dp );


		// 行番号を表示する要素lineNumを1行分の要素をまとめるinputRowに追加
		this.inputRowLeft[ pNum ].addView( this.lineNum[ pNum ] );
		// 入力された演算子を表示する要素inputOperatorを1行分の要素をまとめるinputRowに追加
		this.inputRowLeft[ pNum ].addView( this.inputOperator[ pNum ] );
		// 入力された数値を表示する要素inputNumを1行分の要素をまとめるinputRowに追加
		this.inputRowLeft[ pNum ].addView( this.inputNum[ pNum ] );
		// 入力された数値を表示する要素inputNumを1行分の要素をまとめるinputRowに追加
		this.inputRowRight[ pNum ].addView( this.output[ pNum ] );
		this.inputRow[ pNum ].addView( this.inputRowLeft[ pNum ] );
		this.inputRow[ pNum ].addView( this.inputRowRight[ pNum ] );
		// 1行分の表示をまとめる要素inputRowをupperViewのupperScrollViewに追加する
		this.upperScrollView.addView( this.inputRow[ pNum ] );

		scrollView.post(new Runnable() {
			public void run() {
				MainActivity.this.scrollView.fullScroll(View.FOCUS_DOWN);
			}
		});
	}

	public String getpreInput(){
		return (String) inputNum[(this.pNum + 99) % 100].getText();

	}
}
