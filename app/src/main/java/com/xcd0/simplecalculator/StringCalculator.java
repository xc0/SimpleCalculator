package com.xcd0.simplecalculator;

/*
* 文字列を解釈して計算する電卓
* 入力は一字づつ
* 出力はString[] output[2]で
* output[0]に入力文字列
* output[1]に計算結果文字列
*
* 入力は
* AC,BS,(),=,0~9,+-×÷%^
*/
class stringCalculator {
	private String text;
	private String output;
	private String[] inputString;
	private String[] CHARA;
	private int inputCounter;
	private double preAns;
	private int statusCode;
	private String[] inputArray;
	
	
	stringCalculator() {
		this.text = "";
		this.output = "";
		this.inputString = new String[ 100 ];
		this.inputCounter = 0;
		this.preAns = 0;
		this.statusCode = 0;
		
		String[] tmp = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ".", "AC", "BS", "=", "+", "-", "×", "÷", "%", "^", "(", ")", "ANS" };
		this.CHARA = tmp;
		this.inputArray = new String[ 200 ];
		
	}
	
	// 1ボタンプッシュ毎に出力する計算機、といっても=でない時はエラー表示のみ
	// output[0]に入力文字列
	// output[1]に計算結果文字列
	public String inputOneCharaString( String text ) {
		this.text = text;
		this.output = inputChecker();
		if( this.output.equals( "OK" ) ) {
			this.output = calcStringArray( this.inputArray );
			this.statusCode = 1;
		} else if( this.output.equals( "_0div" ) ) {
			this.output = "ERROR! Can not divide Zero!";
			this.statusCode = -1;
		} else if( this.output.equals( "_1stack" ) ) {
			this.output = "ERROR! Num of operand is bad.";
			this.statusCode = -1;
		} else if( this.output.equals( "_2p" ) ) {
			this.output = "ERROR! Near the parenthesis is bad.";
			this.statusCode = -1;
		} else if( this.output.equals( "_3+=" ) ) {
			this.output = "ERROR! One before Equal must be number.";
			this.statusCode = -1;
		} else if( this.output.equals( "_4pnum" ) ) {
			this.output = "ERROR! num of '(' and ')' is wrong";
			this.statusCode = -1;
		} else if( this.output.equals( "NONE" ) ) {
			this.output = "";
			this.statusCode = 0;
		}
		return this.output;
	}
	
	// -1 error
	// 0  unresolvable
	// 1  resolve
	public int getStatus() {
		return this.statusCode;
	}
	
	public String getInputString() {
		if( this.inputString[ 0 ] == null ) {
			return "";
		}
		String out;
		StringBuilder bf = new StringBuilder();
		for( String tmp : this.inputString ) {
			if( tmp != null ) {
				bf.append( tmp );
			}
		}
		out = bf.toString();
		return out;
	}
	
	/*
	// 文字列を正しく計算できる場合(=で終わるなど)のみ計算する
	public String calcString( String input ) {
		String[] in;
		in = new String[ input.length() ];
		// ちぎって
		for( int i = 0; i < input.length(); i++ ) {
			in[ i ] = String.valueOf( input.charAt( i ) );
		}
		// 呼ぶ
		return calcStringArray( inputNumReader( in ) );
	}
	*/
	
	// いい感じにString[]の配列になってる入力を計算
	// エラー処理なし
	public String calcStringArray( String input[] ) {
		return rpnCalculator( rpnizer( input ) );
	}
	
	// 後置記法の演算器
	public String rpnCalculator( String[] input ) {
		//StringQueue inputQueue = new StringQueue( input.length );
		StringStack inputStack = new StringStack( input.length );
		StringStack oprandStack = new StringStack( input.length );
		String f, s, o;
		for( String tmp : input ) {
			if( tmp.equals( "=" )) break;
			inputStack.push( tmp );
			if( inputTypeChecker( tmp ).equals( "OPE" ) ) {
				o = inputStack.pop();
				s = inputStack.pop();
				f = inputStack.pop();
				if( Double.valueOf( s ) == 0 && tmp.equals( "÷" ) ) {
					return "_0div";
				}
				
				String out = Double.toString( calc( s, f, o ) );
				inputStack.push( out );
			}
		}
		if( inputStack.size() == 1 ) {
			o = inputStack.pop();
		} else {
			o = "_1stack";
		}
		return o;
	}
	
	private double calc( String f, String s, String o ) {
		double first = Double.valueOf( f );
		double second = Double.valueOf( s );
		if( o.equals( "+" ) ) {
			return first + second;
		}
		if( o.equals( "-" ) ) {
			return first - second;
		}
		if( o.equals( "×" ) ) {
			return first * second;
		}
		if( o.equals( "÷" ) ) {
			if( second == 0 ) {
				return 0d;
			}
			return first / second;
		}
		if( o.equals( "%" ) ) {
			return first % second;
		}
		if( o.equals( "^" ) ) {
			return Math.pow( first, second );
		}
		return 0d;
	}
	
	// this.textとthis.inputStringをチェック
	// 良ければ追加
	// "ERROR" or "NONE" or "OK" しか返さない
	public String inputChecker() {
		String output;
		
		// 入力自体がエラーなら追加せず返す
		if( !inputCharaChecker( this.text ) ) {
			output = "_2p";
			return output;
		} else if( this.text.equals( "ANS" ) ) {
			this.inputString[ inputCounter ] = Double.toString( this.preAns );
			this.inputCounter++;
			output = "NONE";
			return output;
		} else if( this.text.equals( "AC" ) ) {
			for( int i = this.inputCounter - 1; i >= 0; i-- ){
				this.inputString[i] = null;
			}
			//this.inputString[ inputCounter ] = Double.toString( this.preAns );
			this.inputCounter = 0;
			output = "NONE";
			return output;
		} else if( this.text.equals( "=" ) ) {
			if( inputCounter > 0 && ( inputString[ inputCounter - 1 ] ).equals( "OPE" ) ) {
				// =の直前が演算子ならエラー 1+=
				output = "_3+=";
				return output;
			}
			if( !( inputParenthesesChecker( inputString ) ) ) {
				// 括弧の数が合ってない
				// 括弧の内側に演算子ときエラー 1+(+
				output = "_4pnum";
				return output;
			} else {
				this.inputString[ inputCounter ] = this.text;
				this.inputCounter++;
				this.inputArray = inputNumReader( this.inputString );
				output = "OK";
				return output;
			}
		} else if( this.text.equals( "BS" ) ) {
			this.inputString[ this.inputCounter ] = null;
			this.inputCounter--;
			output = "NONE";
			return output;
		} else {
			// =やACやBSでない時は適当に返す
			this.inputString[ inputCounter ] = this.text;
			this.inputCounter++;
			output = "NONE";
			return output;
		}
	}
	
	private boolean inputCharaChecker( String text ) {
		boolean A = false;
		/* 多分for-eachにできるっぽいのでこれでいい
		for( int i = 0; i < this.CHARA.length; i++ ) {
			if( text.equals( this.CHARA[ i ] ) ) {
				return true;
			}
		}*/
		for( String tmp : this.CHARA ) {
			if( text.equals( tmp ) ) {
				A = true;
				break;
			}
		}
		// 括弧の直前、直後のエラー
		if( A ) {
			String tmp = inputTypeChecker( text );
			if( tmp.equals( "NUM" ) ) {    // )1
				if( this.inputString[ this.inputCounter ] == null ) {
					return true;
				}
				if( this.inputString[ this.inputCounter ].equals( ")" ) ) {
					A = false;
				}
			}
			// 最初の入力が数字でないならエラー
			if( this.inputString[ 0 ] == null ) {
				return false;
			}
			if( tmp.equals( "OPE" ) ) {    // (+
				if( this.inputString[ this.inputCounter - 1 ].equals( "(" ) ) {
					A = false;
				}
			}
			if( text.equals( ")" ) ) {  // +)
				if( inputTypeChecker( this.inputString[ this.inputCounter - 1 ] ).equals( "OPE" ) ) {
					A = false;
				}
			}
			if( text.equals( "(" ) ) {  // 1(
				if( !( inputTypeChecker( this.inputString[ this.inputCounter - 1 ] ).equals( "OPE" ) ) ) {
					// 直前が演算子でないならエラー
					A = false;
				}
			}
		}
		return A;
	}
	
	private boolean inputParenthesesChecker( String[] text ) {
		char tmp;
		int cSTR = 0, cEND = 0;
		for( int i = 0; i < this.inputCounter; i++ ) {
			if( inputTypeChecker( text[ i ] ).equals( "STR" ) ) {
				cSTR++;
			}
			if( inputTypeChecker( text[ i ] ).equals( "END" ) ) {
				cEND++;
			}
		}
		if( cSTR == cEND )
			return true;
		return false;
	}
	
	// 0に個数、1~は位取りした数または記号を返す
	// 入力が=で終わってなかったらエラー
	private String[] inputNumReader( String[] text ) {
		int length = inputCounter;
		
		if( !( text[ length - 1 ].equals( "=" ) ) ) {
			String[] error = new String[ 2 ];
			error[ 0 ] = "";
			error[ 1 ] = "input do not end Equal";
			return error;
		}
		String[] out = new String[ length ];
		for( int i = 0; i < length; i++ ) {
			out[ i ] = "";
		}
		String preS = "", tmpS;
		char tmp;
		int cNumArray = 0, cNum = 0, i;
		for( i = 0; i < this.inputCounter; i++ ) {
			tmpS = inputTypeChecker( text[ i ] );
			if( tmpS.equals( "NUM" ) || tmpS.equals( "DOT" ) ) {
				out[ cNumArray ] = out[ cNumArray ] + text[ i ];
			} else {
				if( preS.equals( "NUM" ) ) {
					// 数の後に数以外なら
					cNumArray++;
					out[ cNumArray ] = text[ i ];
					cNumArray++;
				}
				
			}
			preS = tmpS;
		}/*
		if( out[ cNumArray ].equals( "=" ) ) {
			out[ cNumArray ] = out[ cNumArray ];
		}*/
		String[] output = new String[ cNumArray ];
		
		for( i = 0; i < cNumArray; i++ ) {
			output[ i ] = out[ i ];
		}
		return output;
	}
	// { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
	// , ".", "AC", "BS", "=", "+", "-", "×", "÷", "%", "^"
	// , "(", ")", "ANS" };
	
	private String inputTypeChecker( String text ) {
		String output = null;
		for( int i = 0; i < this.CHARA.length; i++ ) {
			if( text.equals( this.CHARA[ i ] ) ) {
				if( i < 10 ) {       // 0-9
					output = "NUM";
					break;
				} else if( i == 10 ) {     // .
					output = "DOT";
					break;
				} else if( i == 11 ) {     // AC
					output = "AC";
					break;
				} else if( i == 12 ) {       // BS
					output = "BS";
					break;
				} else if( i == 13 ) {      // =
					output = "EQU";
					break;
				} else if( i <= 19 ) {      // +-×÷%^
					output = "OPE";
					break;
				} else if( i == 20 ) {      // (
					output = "STR";
					break;
				} else if( i == 21 ) {      // )
					output = "END";
					break;
				} else if( i == 22 ) {      //
					output = "ANS";
					break;
				}
			}
		}
		if( output == null ) {
			output = "OTH";
		}
		return output;
	}
	
	public String[] rpnizer( String[] input ) {
		String tmp, tmp2;
		StringStack opeStack = new StringStack( 100 );
		StringQueue outQueue = new StringQueue( 100 );
		int length = input.length;
		
		for( int i = 0; i < length; i++ ) {
			tmp = inputTypeChecker( input[ i ] );
			if( tmp.equals( "OTH" ) ){
				// 数値
				outQueue.enqueue( input[ i ] );
				continue;
			}else if( tmp.equals( "END" ) ) {
				// (までの演算子をoutにpush
				for( int j = 0; j < opeStack.size(); j++ ) {
					tmp2 = opeStack.pop();
					if( tmp2.equals( "(" ) )
						break;
					outQueue.enqueue( tmp2 );
				}
				continue;
			}else if( tmp.equals( "STR" ) ) {
				opeStack.push( input[ i ] );
				continue;
			}else if( tmp.equals( "OPE" ) ) {
				while( true ) {
					// ループ
					if( opeStack.size() == 0 ) {
						// opeStackが空なら
						// input[i]をopeStackにpushしてcontinue
						opeStack.push( input[ i ] );
						break;
					} else {
						// opestackが空でないなら
						String topOpe = opeStack.pop();
						// opeStackの最後に突っ込んだのとinput[i]の優先順位を比較
						if( opeRanker( topOpe ) <= opeRanker( input[ i ] ) ) {
							// opeStackに戻す
							opeStack.push( topOpe );
							// input[i]をopeStackにpushしてcontinue
							opeStack.push( input[ i ] );
							break;
						} else {
							outQueue.enqueue( topOpe );
							// ここからループの最初に戻る
						}
					}
				}
				// input[i]が演算子かつinput[i]をopeStackにpushしたらcontinue
				continue;
				//}
			}else if( tmp.equals( "EQU" ) ) {
				while( opeStack.size() != 0 ) {
					outQueue.enqueue( opeStack.pop() );
				}
				outQueue.enqueue( input[ i ] );
				break;
			}
			//ここには来ないはず
		}
		length = outQueue.size();
		String[] out;
		out = new String[ length ];
		for( int i = 0; i < length; i++ ) {
			out[ i ] = outQueue.dequeue();
		}
		return out;
	}
	
	private int opeRanker( String input ) {
		
		if( input.equals( "×" ) || input.equals( "÷" ) )
			return 4;
		if( input.equals( "^" ) )
			return 5;
		if( input.equals( "+" ) || input.equals( "-" ) )
			return 6;
		if( input.equals( "%" ) )
			return 7;
		return 99;
	}
	
	private void clear(){
		
		this.output = "";
		this.inputString = new String[ 100 ];
		this.inputCounter = 0;
		this.statusCode = 0;
		this.inputArray = new String[ 200 ];
	}
}

class StringStack {
	private int stackSize;
	private int stackPointer;
	private String[] stack;
	
	public StringStack( int size ) {
		this.stackPointer = 0;
		this.stackSize = size;
		
		stack = new String[ stackSize ];
	}
	
	public void push( String tmp ) {
		if( stackPointer >= stackSize ) {
			return;
		}
		stack[ stackPointer++ ] = tmp;
	}
	
	public String pop() {
		if( stackPointer <= 0 ) {
			return "";
		}
		return stack[ --stackPointer ];
	}
	
	public int size() {
		return this.stackPointer;
	}
}


class StringQueue {
	private int queueSize;
	private int str, end;
	private String[] queue;
	
	public StringQueue( int size ) {
		this.queueSize = size;
		this.str = 0;
		this.end = 0;
		this.queue = new String[ this.queueSize ];
	}
	
	public void enqueue( String in ) {
		
		if( this.str + this.queueSize  == ( this.end + 1 + this.queueSize ) % this.queueSize ) {
			return;
		}
		this.end++;
		this.end = this.end % this.queueSize;
		this.queue[ this.end - 1 ] = in;
	}
	
	public String dequeue() {
		if( this.str == this.end ) {
			return "";
		}
		this.str++;
		// strがmax超えてたら減らす
		this.str = this.str % this.queueSize;
		return this.queue[ this.str - 1 ];
		
	}
	
	public int size() {
		return ( end - str + queueSize ) % queueSize;
	}
	
	public String checkFirst() {
		return this.queue[ this.str ];
	}
}
