package com.xcd0.simplecalculator;

/**
 * Created by xcd0 on 2017/06/24.
 */


public class Common {

	private MainActivity mainActivity;
	private boolean flag;
	private String text;
	private int input;
	private int state;
	private int nextState;
	private long inputNum;
	private String inputOperator;
	private long firstOperand;
	private long secondOperand;
	private long dispNum;
	private long ans;
	private String[] output;
	private String pretext;
	
	private int breakLineFlag;
	private int inputFinishedFlag;
	private int outputFinishedFlag;
	private String inputLine;
	private String outputLine;
	private String inputKeeper; // state=1,2で使用
	private int eqCounter;
	
	/*
	//後述の追加機能の実装で使用するカウンタ
	private int count;
	// 後述の追加機能の実装で使用する入力履歴
	private String[] inputArray;
	*/

	Common( MainActivity mainActivity ) {
		this.mainActivity = mainActivity;
		this.output = new String[ 6 ];
		this.flag = true;
		this.text = "";
		this.input = 0;
		this.state = 0;
		this.nextState = 0;
		this.inputNum = 0;
		this.inputOperator = "";
		this.firstOperand = 0;
		this.secondOperand = 0;
		this.dispNum = 0;
		this.ans = 0;
		this.pretext = "";

		this.breakLineFlag = 0;
		this.inputFinishedFlag = 0;
		this.outputFinishedFlag = 0;
		this.inputLine = "";
		this.outputLine = "";
		this.inputKeeper = "";
		this.eqCounter = 0;
		/*
		// 後述の追加機能の実装で使用するカウンタ
		this.count = 0;
		// 後述の追加機能の実装で使用する入力履歴
		this.inputArray = new String[100];
		for( int i = 0; i < 100; i++ ){
			this.inputArray[i] = "";
		}
		*/
	}


	public String[] MainProcess( String text ) {
		this.text = text;
		//this.nextState = 0;

		/*
		入力をCボタンなのか、=ボタンなのか、演算子なのか、数値なのか判定
		inputの値は
		C       0
		=       1
		演算子     2
		数値      3
		BS      4
		pm      5
		%       6
		.       7
		*/
		this.input = judgeInput( text );

		// 追加機能ならば
		if( this.input > 3 ){
			additionalFunction( this.input );
			return this.output;
		}

		/*
		// 後述の追加機能の実装例
		if( this.input == 4 ){
			int i = 0;
			this.count = 0;
			while( this.inputArray[i] != "" )
				this.count++;
				i++;
			}
		}	*/

		

		this.flag = true;

		while( flag ) {
			// 状態に応じて処理
			flag = mainLoop();
		}
		return this.output;
	}

	private boolean mainLoop() {
		boolean flag = true;
		switch( this.state ) {
		case 0:
			inputStateWithoutOperator();
			break;
		case 1:
			inputState();
			break;
		case 2:
			choiceOperatorState();
			break;
		case 3:
			arithmeticProcessState();
			break;
		case 4:
			setOutput(-1);
			errorState();
			break;
		case 5:
			successfulCompletionState();
			setOutput(0);
			break;
		case 6:
			flag = finalState();
			break;
		}
		return flag;
	}


	/*	< state = 0 > 数値入力(演算子なし)
		入力がC					>> 0	: 0 -> 5 (0)	: リセットして正常終了
			*.C
		入力が=					>> 1	: 0 -> 3 (0)	: 表示値を変更せず正常終了
			C.=, C1.=
		入力が演算子			>> 2	: 0 -> 5 (2)	: 演算子を設定、次状態を演算子選択にして正常終了
			C.+, C1.+, C11.+
		入力が数値				>> 3	: 0 -> 5 (0)	: 桁上げ処理して正常終了
			C.1, C1.1
		入力が数値で桁あふれ	>> 3	: 0 -> 5 (0)	: inputNumの最下位桁を上書きして正常終了
			C1111111111.2
	*/
	private void inputStateWithoutOperator() {
		if( this.input == 0 ) { // AC
			clear();
		} else if( this.input == 1 ) {  // =
			this.firstOperand = this.inputNum;
			this.inputNum = 0;

			// 画面出力内容の設定
			this.breakLineFlag = 1;
			this.inputFinishedFlag = 1;
			this.outputFinishedFlag = 1;
			this.inputLine = Long.toString( this.inputNum ) + this.text;
			this.outputLine = Long.toString( this.inputNum );
			
			this.state = 3;
			this.nextState = 0;
		} else if( this.input == 2 ) {     // ope
			this.firstOperand = this.inputNum;
			this.inputOperator = this.text;

			this.breakLineFlag = 0;
			this.inputFinishedFlag = 1;
			this.outputFinishedFlag = 0;
			this.inputLine = Long.toString(this.inputNum) + this.text;
			this.outputLine = Long.toString(this.inputNum);
			
			this.inputNum = 0;
			this.state = 5;
			this.nextState = 2;
		} else if( this.input == 3 ) {  // num
			// 桁あふれ判定
			int MAX = 9;
			if( MAX < String.valueOf( this.inputNum ).length() ) {
				// 桁あふれしていたら最下位桁を切り捨てる
				this.inputNum /= 10;
			}
			// 入力数値を桁上げして追加
			this.inputNum = this.inputNum * 10 + Integer.parseInt( this.text );
			this.dispNum = this.inputNum;

			// 画面出力内容の設定
			this.breakLineFlag = 0;
			this.inputFinishedFlag = 1;
			this.outputFinishedFlag = 0;
			this.inputLine = Long.toString( this.inputNum );
			this.outputLine = "";
			
			this.state = 5;
			this.nextState = 0;
		} else {
			// 未実装
			this.state = 5;
			this.state = 0;
		}
		return;
	}

	/*	< state = 1 > 数値入力(演算子あり)
		入力がC					>> 0	: 1 -> 5 (0)	: リセットして正常終了
			C1+2.C, C1+22.C
		入力が=					>> 1	: 1 -> 3 (1)		: 演算して演算正常終了
			C1+2.=
		入力が演算子			>> 2	: 1 -> 3 (2)	: 既存の演算子で演算、次状態を演算子選択にして正常終了
			C1+2.+, C1+2+3.+
		入力が数値				>> 3	: 1 -> 5 (1)	: 桁上げ処理をして正常終了
			C1+2.2, C1+2+3.3
		入力が数値で桁あふれ	>> 3	: 1 -> 5 (1)	: inputNumの最下位桁を上書きして正常終了
			C1+2222222222.3, C1+2+3333333333.4
	*/
	private void inputState() {

		if( this.input == 0 ) { // AC
			clear();
		} else if( this.input == 1 ) {  // =
			if( this.pretext == "=" ) {
				this.eqCounter++;
				this.firstOperand = this.dispNum;
				//this.firstOperand = Long.valueOf( this.outputLine );
				this.inputKeeper = this.inputLine + Long.toString( this.dispNum )
								+ ",\n" + Long.toString( this.dispNum ) + inputOperator;
			} else {
				// c6+3.=
				this.secondOperand = this.inputNum;
				this.eqCounter = 0;
			}

			// 画面出力内容の設定
			this.breakLineFlag = 1;
			this.inputFinishedFlag = 1;
			this.outputFinishedFlag = 1;
			// 計算結果を入れる
				//this.inputLine = Long.toString( this.inputNum );
				//this.outputLine = Long.toString( this.inputNum );
			
			
			//this.inputLine = this.inputKeeper + Long.toString( this.inputNum ) + this.text;
			this.inputLine = this.inputKeeper + Long.toString( this.secondOperand ) + this.text;
			
			this.inputNum = 0;
			
			
			this.state = 3;
			this.nextState = 1;
		} else if( this.input == 2 ) {   // ope
			//this.secondOperand = this.inputNum;
			this.inputOperator = this.text;
			this.inputNum = 0;


			// 画面出力内容の設定
			this.breakLineFlag = 0;
			this.inputFinishedFlag = 1;
			this.outputFinishedFlag = 0;
			// 計算結果を入れる
				//this.inputLine = Long.toString( this.inputNum );
				//this.outputLine = Long.toString( this.inputNum );
			
			this.state = 3;
			this.nextState = 2;
		} else if( this.input == 3 ) {  // num
			// 桁あふれ判定
			int MAX = 9;
			if( MAX < String.valueOf( this.inputNum ).length() ) {
				this.inputNum /= 10;
			}
			// 入力数値を桁上げして追加
			this.inputNum = this.inputNum * 10 + Integer.parseInt( this.text );
			this.dispNum = this.inputNum;


			// 画面出力内容の設定
			this.breakLineFlag = 0;
			this.inputFinishedFlag = 1;
			this.outputFinishedFlag = 0;
			this.inputLine = this.inputKeeper + Long.toString( this.inputNum );
			this.outputLine = "";
			
			this.state = 5;
			this.nextState = 1;
		}
		return;
	}

	/*	< state = 2 > 演算子選択	既存の入力例 C+, C+-, C1+, C1+-, C1+2+, C1+2+-
		入力がC					>> 0	: 2 -> 5 (0)	: リセットして正常終了
			C+.C, C1+.C, C1+-.C
		入力が=					>> 1	: 2 -> 3 (1)	: 表示値を被演算子として演算して正常終了
			C+.=, C1++.=, C1+2+.=
		入力が演算子			>> 2	: 2 -> 5 (2)	: 演算子を再設定して正常終了
			C+.-, C1+.-, C1+2+.-
		入力が数値				>> 3	: 2 -> 5 (1)	: 数値を設定して正常終了
			C1+.2, C1+2+-.3
	*/
	private void choiceOperatorState() {
		if( this.input == 0 ) {     // C
			clear();
		} else if( this.input == 1 ) {      // =
			//this.firstOperand = this.dispNum;
			//this.secondOperand = this.dispNum;
			this.state = 3;
			this.nextState = 1;
		} else if( this.input == 2 ) {      // ope
			
			this.inputKeeper = this.inputLine;
			if(this.inputKeeper != "" && this.inputKeeper != null && this.inputKeeper.length() > 0){
				this.inputKeeper = this.inputKeeper.substring(0, this.inputKeeper.length()-1);
			}
			this.inputLine = this.inputKeeper + this.text;
			//this.inputLine = this.inputLine + this.text;
			this.inputOperator = this.text;
			this.state = 5;
			this.nextState = 2;
		} else if( this.input == 3 ) {      // num
			// state=1で入力文字列を保持するために使用
			this.inputKeeper = this.inputLine;
			this.inputNum = Integer.parseInt( this.text );
			this.inputLine = this.inputKeeper + this.inputNum;
			this.firstOperand = this.dispNum;
			this.secondOperand = this.inputNum;
			this.state = 5;
			this.nextState = 1;
		}
		return;
	}


	/*	< state = 3 > 演算処理
		任意					>> -	: 3 -> 4 ( )	: 演算処理してエラー判定
			C1+2, C
	*/
	private void arithmeticProcessState() {
		if( this.inputOperator == "" ) {
			// C1=
			this.ans = this.dispNum;
		} else {
			this.ans = operate( this.firstOperand, this.inputOperator, this.secondOperand );
		}
		// 0割が発生したときoperat()がthis.stateを4にしている。
		if( this.state == 4 ) {
			return;
		}
		// 桁あふれ判定
		int MAX = 9;
		if( MAX < String.valueOf( this.ans ).length() ) {
			this.ans = 0;
			this.state = 4;
			this.nextState = 4;
			return;
		}


		// 画面出力内容の設定
		//this.breakLineFlag = 0;
		//this.inputFinishedFlag = 0;
		this.outputFinishedFlag = 1;
		//this.inputLine = Long.toString( this.inputNum );
		this.outputLine = Long.toString( this.ans );
		
		this.state = 5;
		this.dispNum = this.ans;
		this.firstOperand = this.ans;
		this.state = 5;
		return;
	}

	private long operate( long num1, String operator, long num2 ) {
		if( operator == "+" ) {
			return ( long ) ( num1 + num2 );
		} else if( operator == "-" ) {
			return ( long ) ( num1 - num2 );
		} else if( operator == "×" ) {
			return ( long ) ( num1 * num2 );
		} else if( operator == "÷" ) {
			// 0割
			if( num2 == 0 ) {
				this.state = 4;
				this.nextState = 4;
				return 0l;
			} else {
				return ( long ) ( num1 / num2 );
			}
		} else {
			return 0l;
		}
	}


	/*	< state = 4 > エラー描画状態
		入力がC					>> 0	: 4 -> 6 (0)	: リセットして終了
		入力がCでない			>> != 0	: 4 -> 6 (4)	: エラー表示を描画して終了
	*/
	private void errorState() {
		if( this.input == 0 ) {
			clear();
		} else {
			// もう一度エラーを描画
			this.state = 6;
			this.nextState = 4;
		}
		return;
	}


	/*	< state = 5 > 結果描画状態
			任意					>> -	: 5 -> 6 ( )	: 描画して終了
	*/
	private void successfulCompletionState() {
		this.state = 6;
	}


	/*	< state = 6 > 終了状態
		任意					>> -	: 6 -> nextState : ()の値にしてflag=false
	*/
	private boolean finalState() {
		/*
			setOutput()がするようになった
		 
			/*
				表示したい情報
				行番号 入力文字列   出力値
				out[6]
				[0] break line 1:0
				[1] input   flag
				[2] input line
				[3] output  flag
				[4] output line
				[5] AC flag
			*
	
			this.output[ 0 ] = Integer.toString( this.breakLineFlag );
			this.output[ 1 ] = Integer.toString( this.inputFinishedFlag );
			this.output[ 2 ] = this.inputLine;
			this.output[ 3 ] = Integer.toString( this.outputFinishedFlag );
			this.output[ 4 ] = this.outputLine ;
			this.output[ 5 ] = Integer.toString( this.nextState );
		*/
		this.pretext = this.text;
		this.state = this.nextState;
		return false;
	}

	private int judgeInput( String text ) {
		// ACボタン
		if( text == "AC" ) {
			return 0;
		}
		// =ボタン
		if( text == "=" ) {
			return 1;
		}
		// 演算子
		if( text == "+"
				|| text == "-"
				|| text == "×"
				|| text == "÷" ) {
			return 2;
		} else if( text == "BS" ) {
			return 4;
		} else if( text == "±" ) {
			return 5;
		} else if( text == "%" ) {
			return 6;
		} else if( text == "." ) {
			return 7;
		} else {
			// 数値
			return 3;
		}
	}

	private void clear() {
		this.inputNum = 0;
		this.inputOperator = "";
		this.firstOperand = 0;
		this.secondOperand = 0;
		this.dispNum = 0;
		
		this.state = 5;
		this.nextState = 0;

		this.inputKeeper = "";
		
		this.breakLineFlag = 0;
		this.inputFinishedFlag = 0;
		this.outputFinishedFlag = 0;
		this.inputLine = "";
		this.outputLine = "";
		this.eqCounter = 0;
	}

	/*
	private void setOutput( String inputOperator, long displayNum ) {
		String num = String.valueOf( displayNum );
		this.output[ 0 ] = inputOperator;
		this.output[ 1 ] = num;
		return;
	}*/


	/*  input
	BS      4
	pm      5
	%       6
	.       7
	*/
	private void additionalFunction( int input ) {
		switch( input ) {
			case 4:     // BS
			case 5:     // ±
				this.dispNum *= -1;
				this.inputNum *= -1;
				break;
			case 6:     // %
			case 7:     // dot
		}
	}
	private void setOutput(int error){
		
		/*
			表示したい情報
			行番号 入力文字列   出力値
			out[7]
			[0] break line 1:0
			[1] input   flag
			[2] input line
			[3] output  flag
			[4] output line
			[5] text
		*/
		
		if( error == -1 ){
			this.output[ 0 ] = Integer.toString( breakLineFlag );
			this.output[ 1 ] = Integer.toString( inputFinishedFlag );
			this.output[ 2 ] = "EORROR!!";
			this.output[ 3 ] = Integer.toString( this.outputFinishedFlag );
			this.output[ 4 ] = "Please Tap AC    ";
			this.output[ 5 ] = this.text;
			this.pretext = "ERROR!!";
		}else {
			this.output[ 0 ] = Integer.toString( breakLineFlag );
			this.output[ 1 ] = Integer.toString( inputFinishedFlag );
			this.output[ 2 ] = this.inputLine;
			this.output[ 3 ] = Integer.toString( this.outputFinishedFlag );
			for( int i = eqCounter; i > 0; i-- ){
				this.outputLine = "\n" + this.outputLine;
			}
			this.output[ 4 ] = this.outputLine;
			this.output[ 5 ] = this.text;
			this.pretext = this.text;
		}
	}
}
