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
	private int inputsecondOperandNum;
	private String pretext;
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
		this.inputsecondOperandNum = 0;
		this.pretext = "";
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
		this.nextState = 0;

		/*
		入力をCボタンなのか、=ボタンなのか、演算子なのか、数値なのか判定
		inputの値は
		C        0
		=        1
		演算子   2
		数値     3		// 後述の追加機能では BS      4
		*/
		this.input = judgeInput( text );

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

		/*
		状態について
		数値入力(演算子なし)   0
		数値入力(演算子あり)   1
		演算子選択             2
		演算処理               3
		エラー判定             4
		結果描画               5
		エラー描画             6
		終了                   7

		実行中の各タイミングでのstateの値の遷移を示す
		終了する時は次の入力での初期状態をセットする
		書式は-	>> input	: 遷移前 -> 遷移後 (次の初期状態)
		this.nextStateはfinalState()のみから参照される
		次行は入力例で.の前が既存の入力で後ろが新規の入力である。

		< state = 0 > 数値入力(演算子なし)
		入力がC					>> 0	: 0 -> 7 (0)	: リセットして正常終了
			*.C
		入力が=					>> 1	: 0 -> 5 (0)	: 表示値を変更せず正常終了
			C.=, C1.=
		入力が演算子			>> 2	: 0 -> 5 (2)	: 演算子を設定、次状態を演算子選択にして正常終了
			C.+, C1.+, C11.+
		入力が数値				>> 3	: 0 -> 5 (0)	: 桁上げ処理して正常終了
			C.1, C1.1
		入力が数値で桁あふれ	>> 3	: 0 -> 6 (0)	: inputNumの最下位桁を上書きして正常終了
			C1111111111.2

		< state = 1 > 数値入力(演算子あり)
		入力がC					>> 0	: 1 -> 7 (0)	: リセットして正常終了
			C1+2.C, C1+22.C
		入力が=					>> 1	: 1 -> 3 (1)		: 演算して演算正常終了
			C1+2.=
		入力が演算子			>> 2	: 1 -> 5 (1)	: 既存の演算子で演算、次状態を演算子選択にして正常終了
			C1+2.+, C1+2+3.+
		入力が数値				>> 3	: 1 -> 5 (1)	: 桁上げ処理をして正常終了
			C1+2.2, C1+2+3.3
		入力が数値で桁あふれ	>> 3	: 1 -> 6 (1)	: inputNumの最下位桁を上書きして正常終了
			C1+2222222222.3, C1+2+3333333333.4

		< state = 2 > 演算子選択	既存の入力例 C+, C+-, C1+, C1+-, C1+2+, C1+2+-
		入力がC					>> 0	: 2 -> 7 (0)	: リセットして正常終了
			C+.C, C1+.C, C1+-.C
		入力が=					>> 1	: 2 -> 3 (1)	: 表示値を被演算子として演算して正常終了
			C+.=, C1++.=, C1+2+.=
		入力が演算子			>> 2	: 2 -> 5 (1)	: 演算子を再設定して正常終了
			C+.-, C1+.-, C1+2+.-
		入力が数値				>> 3	: 2 -> 5 (1)	: 数値を設定して正常終了
			C1+.2, C1+2+-.3

		< state = 3 > 演算処理
		任意					>> -	: 3 -> 4 ( )	: 演算処理してエラー判定
			C1+2, C

		< state = 4 > エラー判定
		演算結果が不正			>> -	: 4 -> 6 (6)	: エラー描画
		演算結果が正常			>> -	: 4 -> 5 ( )	: 結果描画

		< state = 5 > 結果描画状態
		任意					>> -	: 5 -> 7 ( )	: 描画して終了

		< state = 6 > エラー描画状態
		入力がC					>> 0	: 6 -> 5 (0)	: リセットして終了
		入力がCでない			>> != 0	: 6 -> 7 (6)	: エラー表示を描画して終了

		< state = 7 > 終了状態
		任意					>> -	: 6 -> prestate : ()の値にしてflag=false
		*/

		this.flag = true;
		// 描画させるまで無限ループ
		// 各状態では必ずstateが変更される
		// 描画はerrorStateとsuccessfulCompletionStateで行われる

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
				//checkErrorState();
				break;
			case 5:
				successfulCompletionState();
				break;
			case 6:
				errorState();
				break;
			case 7:
				finalState();
			/*
			// 後述の追加機能の実装ではここでカウントデクリメント処理、カウント判定をする
			if( this.count > 0 ){
				this.connt--;
				break;
			}
			*/
				flag = false;
				break;
		}
		this.output[ 2 ] = Integer.toString( this.nextState );
		this.output[ 3 ] = Long.toString( this.firstOperand );
		this.output[ 4 ] = this.inputOperator;
		this.output[ 5 ] = Long.toString( this.secondOperand );
		this.pretext = this.text;
		return flag;
	}


	/*	< state = 0 > 数値入力(演算子なし)
		入力がC					>> 0	: 0 -> 7 (0)	: リセットして正常終了
			*.C
		入力が=					>> 1	: 0 -> 5 (1)	: 表示値を変更せず正常終了
			C.=, C1.=
		入力が演算子			>> 2	: 0 -> 5 (2)	: 演算子を設定、次状態を演算子選択にして正常終了
			C.+, C1.+, C11.+
		入力が数値				>> 3	: 0 -> 5 (0)	: 桁上げ処理して正常終了
			C.1, C1.1
		入力が数値で桁あふれ	>> 3	: 0 -> 5 (0)	: inputNumの最下位桁を上書きして正常終了
			C1111111111.2
	*/
	private void inputStateWithoutOperator() {
		if( this.input == 0 ) { // C
			clear();
			this.state = 7;
		} else if( this.input == 1 ) {  // =
			this.firstOperand = this.inputNum;
			this.inputNum = 0;
			this.inputsecondOperandNum = 0;
			this.state = 3;
			this.nextState = 0;
		} else if( this.input == 2 ) {     // ope
			this.firstOperand = this.inputNum;
			this.inputOperator = this.text;
			this.inputsecondOperandNum = 0;
			this.inputNum = 0;
			this.state = 7;
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
		入力がC					>> 0	: 1 -> 7 (0)	: リセットして正常終了
			C1+2.C, C1+22.C
		入力が=					>> 1	: 1 -> 3 (1)		: 演算して演算正常終了
			C1+2.=
		入力が演算子			>> 2	: 1 -> 3 (1)	: 既存の演算子で演算、次状態を演算子選択にして正常終了
			C1+2.+, C1+2+3.+
		入力が数値				>> 3	: 1 -> 5 (1)	: 桁上げ処理をして正常終了
			C1+2.2, C1+2+3.3
		入力が数値で桁あふれ	>> 3	: 1 -> 5 (1)	: inputNumの最下位桁を上書きして正常終了
			C1+2222222222.3, C1+2+3333333333.4
	*/
	private void inputState() {

		if( this.input == 0 ) { // C
			clear();
			this.state = 7;
		} else if( this.input == 1 ) {  // =
			if( this.pretext == "=" ) {
				this.secondOperand = this.secondOperand;
				this.firstOperand = this.dispNum;
			} else {
				// c6+3.=
				this.secondOperand = this.inputNum;
			}
			this.inputsecondOperandNum = 0;
			this.inputNum = 0;
			this.state = 3;
			this.nextState = 1;
		} else if( this.input == 2 ) {   // ope
			//this.secondOperand = this.inputNum;
			this.inputOperator = this.text;
			this.inputNum = 0;
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
			this.state = 7;
			this.nextState = 1;
			inputsecondOperandNum++;
		}
		return;
	}

	/*	< state = 2 > 演算子選択	既存の入力例 C+, C+-, C1+, C1+-, C1+2+, C1+2+-
		入力がC					>> 0	: 2 -> 7 (0)	: リセットして正常終了
			C+.C, C1+.C, C1+-.C
		入力が=					>> 1	: 2 -> 3 (1)	: 表示値を被演算子として演算して正常終了
			C+.=, C1++.=, C1+2+.=
		入力が演算子			>> 2	: 2 -> 5 (1)	: 演算子を再設定して正常終了
			C+.-, C1+.-, C1+2+.-
		入力が数値				>> 3	: 2 -> 5 (1)	: 数値を設定して正常終了
			C1+.2, C1+2+-.3
	*/
	private void choiceOperatorState() {
		if( this.input == 0 ) {     // C
			clear();
			this.state = 7;
			this.nextState = 0;
		} else if( this.input == 1 ) {      // =
			//this.firstOperand = this.dispNum;
			//this.secondOperand = this.dispNum;
			this.state = 3;
			this.nextState = 1;
		} else if( this.input == 2 ) {      // ope
			this.inputOperator = this.text;
			this.state = 7;
			this.nextState = 2;
		} else if( this.input == 3 ) {      // num
			this.inputNum = Integer.parseInt( this.text );
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
		// 0割が発生したときoperateがthis.stateを0にしている。
		if( this.state == -1 ) {
			return;
		}
		// 桁あふれ判定
		int MAX = 9;
		if( MAX < String.valueOf( this.ans ).length() ) {
			this.ans = 0;
			this.state = 6;
			this.nextState = 6;
			return;
		}
		this.state = 5;
		this.dispNum = this.ans;
		this.firstOperand = this.ans;
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
				this.state = 6;
				this.nextState = 6;
				return ( long ) 0;
			} else {
				return ( long ) ( num1 / num2 );
			}
		} else {
			return ( long ) 0;
		}
	}


	/*	< state = 5 > 結果描画状態
			任意					>> -	: 5 -> 7 ( )	: 描画して終了
	*/
	private void successfulCompletionState() {
		// 演算結果を描画
		// ここは実装済みの関数を呼び出す
		setOutput( this.inputOperator, this.dispNum );

		this.state = 7;
	}


	/*	< state = 6 > エラー描画状態
		入力がC					>> 0	: 6 -> 7 (0)	: リセットして終了
		入力がCでない			>> != 0	: 6 -> 7 (6)	: エラー表示を描画して終了
	*/
	private void errorState() {
		if( this.input == 0 ) {
			clear();
			this.nextState = 0;
		} else {
			// エラーを描画
			// ここは実装済みの関数を呼び出す
			this.state = 7;
			this.nextState = 6;
			setOutput( "ERROR!!", 0 );
		}
		return;
	}


	/*	< state = 7 > 終了状態
		任意					>> -	: 6 -> nextState : ()の値にしてflag=false
	*/
	private void finalState() {
		this.state = this.nextState;
		return;
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
		this.state = 1;

		// 表示をクリア
		setOutput( "", 0 );
	}

	private void setOutput( String inputOperator, long displayNum ) {
		String num = String.valueOf( displayNum );
		this.output[ 0 ] = inputOperator;
		this.output[ 1 ] = num;
		return;
	}
}

