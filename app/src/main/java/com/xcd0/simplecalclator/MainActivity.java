package com.xcd0.simplecalclator;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.view.Gravity;
import android.widget.Button;
//import android.graphics.Typeface;
//import android.view.ViewGroup.MarginLayoutParams;

public class MainActivity extends AppCompatActivity {

	private TextView[] tv = new TextView[100];
	private LinearLayout[] buttonRow = new LinearLayout[5];
	private Button[] button = new Button[buttonRow.length * 4];
	private int b0w = -1;
	private int pNum = -1;
	private int dp1 = 0;
	private float dp = 0;

	LinearLayout mainView;
	LinearLayout upperView;
	ScrollView scrollView;
	LinearLayout upperScrollView;
	LinearLayout lowerView;

	private String bLabel[] =
			{ "AC", "±", "%", "÷"
			, "7", "8", "9", "×"
			, "4", "5", "6", "-"
			, "1", "2", "3", "+"
			, "0", ".", "="};

	private final int MP = LinearLayout.LayoutParams.MATCH_PARENT;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		this.mainView = new LinearLayout(this);
		this.upperView = new LinearLayout(this);
		this.scrollView = new ScrollView(this);
		this.upperScrollView = new LinearLayout(this);
		this.lowerView = new LinearLayout(this);

		//setContentView(R.layout.activity_main);

		//LinearLayout mainView = new LinearLayout(this);
		mainView.setOrientation(LinearLayout.VERTICAL);
		mainView.setLayoutParams(new LinearLayout.LayoutParams(MP, MP));
		mainView.setGravity(Gravity.CENTER);
		setContentView(mainView);

		//LinearLayout upperView = new LinearLayout(this);
		upperView.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams uv = new LinearLayout.LayoutParams(MP, 0);
		uv.weight = 3.0f;
		upperView.setBackgroundColor(Color.rgb(240, 240, 240));
		upperView.setLayoutParams(uv);

		this.dp = getResources().getDisplayMetrics().density;
		this.dp1 = (int) dp;
		//ScrollView scrollView = new ScrollView(this);
		upperView.addView(scrollView, new LinearLayout.LayoutParams(MP,MP));

		upperScrollView.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams usv = new LinearLayout.LayoutParams(MP, 0);
		usv.weight = 3.0f;
		upperScrollView.setBackgroundColor(Color.rgb(240, 240, 240));
		upperScrollView.setLayoutParams(usv);
		scrollView.addView(upperScrollView, new LinearLayout.LayoutParams(MP, ViewGroup.LayoutParams.WRAP_CONTENT));

		//LinearLayout lowerView = new LinearLayout(this);
		lowerView.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams lv = new LinearLayout.LayoutParams(MP, 0);
		lv.weight = 5.0f;
		lowerView.setBackgroundColor(Color.rgb(240, 240, 240));
		lowerView.setLayoutParams(lv);


		setContentView(mainView);
		mainView.addView(upperView, uv);
		mainView.addView(lowerView, lv);




		for (int i = 0; i < buttonRow.length; i++) {
			buttonRow[i] = new LinearLayout(this);
			buttonRow[i].setOrientation(LinearLayout.HORIZONTAL);
			LinearLayout.LayoutParams br = new LinearLayout.LayoutParams(MP, 0);
			br.weight = 1;
			buttonRow[i].setLayoutParams(br);
			buttonRow[i].setPadding(0, dp1, 0, 0);
			lowerView.addView(buttonRow[i]);

			for (int j = 0; j < 4; j++) {
				int num = i * 4 + j;
				if (num == 19) break;
				button[j] = new Button(this);
				//button[j].setText(String.valueOf(num));
				//button[j].setTag(String.valueOf(num));
				button[j].setText(bLabel[num]);
				button[j].setTag(bLabel[num]);
				button[j].setBackgroundColor(Color.rgb(255, 255, 255));

				LinearLayout.LayoutParams bl = new LinearLayout.LayoutParams(0, MP);
				bl.weight = 1;
				if (b0w < 0 && num == 16) bl.weight = 2;
				button[j].setLayoutParams(bl);
				buttonRow[i].addView(button[j]);
				//if(false){
				if (j != 3) {
					LinearLayout empty = new LinearLayout(this);
					empty.setOrientation(LinearLayout.HORIZONTAL);
					LinearLayout.LayoutParams zero = new LinearLayout.LayoutParams(dp1, MP);
					empty.setLayoutParams(zero);
					buttonRow[i].addView(empty);
				}

				button[j].setOnClickListener(
						new View.OnClickListener() {
							public void onClick(View view) {
								MainActivity.this.buttonClicked( view.getTag().toString() );
							}
						});
			}
		}

	}
	public void buttonClicked(String text){
		this.pNum++;
		if( this.pNum > 99 ){
			String tmp;
			tmp = this.tv[pNum-1].getText().toString();
			pNum = 0;
			for( int i = 98; i > 0; i-- ){
				this.upperScrollView.removeAllViews();
				this.tv[pNum] = new TextView(this);
				this.tv[pNum].setText( tmp );
				this.tv[pNum].setTextColor(0xff000000);
				this.tv[pNum].setTextSize(10 * dp);
				this.upperScrollView.addView( tv[pNum] );
			}
		}
		this.tv[pNum] = new TextView(this);
		this.tv[pNum].setText( "pNum = " + Integer.toString(this.pNum) + " , text " + text);
		this.tv[pNum].setTextColor(0xff000000);
		this.tv[pNum].setTextSize(10 * dp);
		/*
		if(text == "AC"){

		}else if(text == "±"){

		}else if(text == "%"){

		}else if(text == "÷"){

		}else if(text == "×"){

		}else if(text == "-"){

		}else if(text == "+"){

		}else if(text == "."){

		}else if(text == "0"){

		}else if(text == "1"){

		}else if(text == "2"){

		}else if(text == "3"){

		}else if(text == "4"){

		}else if(text == "5"){

		}else if(text == "6"){

		}else if(text == "7"){

		}else if(text == "8"){

		}else if(text == "9"){

		}else{

		}
		*/
		this.upperScrollView.addView( tv[pNum] );
		this.scrollView.fullScroll( scrollView.FOCUS_DOWN );
	}
}