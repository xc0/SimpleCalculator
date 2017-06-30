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
public class StringCalculator {
	
	private String text;
	private String output;
	private String[] inputString;
	private String[] CHARA;
	private int inputCounter;
	private String preAns;
	private String preInput;
	private int statusCode;
	private String[] inputArray;
	
	
	StringCalculator() {
		this.text = "";
		this.output = "";
		this.inputString = new String[ 100 ];
		this.inputCounter = 0;
		this.preAns = "";
		this.statusCode = 0;
		
		String[] tmp = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ".", "AC", "BS", "=", "+", "-", "×", "÷", "%", "^", "(", ")", "ANS" };
		this.CHARA = tmp;
		this.inputArray = new String[ 100 ];
		this.preInput = "";
	}
	
	// 1ボタンプッシュ毎に出力する計算機、といっても=でない時はエラー表示のみ
	// output[0]に入力文字列  output[1]に計算結果文字列
	public String inputOneCharaString( String text ) {
		this.text = text;
		this.output = inputChecker();
		
		if( this.output.equals( "OK" ) ) {
			this.preInput = this.text;
			this.output = calcStringArray( this.inputArray );
			if( this.output.equals( "NaN" ) ){
				this.output = "ERROR!<br>0で剰余は取れません";
			}
			this.statusCode = 1;
		} else if( this.output.equals( "NONE" ) ) {
			// 入力は正常、入力文字列に追加して何も表示しない
			this.preInput = this.text;
			this.output = "";
			this.statusCode = 0;
		}else if( this.output.equals( "_0div" ) ) {
			this.output = "ERROR!<br>0では割れません";
			this.statusCode = -1;
		} else if( this.output.equals( "_1stack" ) ) {
			this.output = "ERROR!<br>被演算子の数が合っていません";
			this.statusCode = -1;
		} else if( this.output.equals( "_2p" ) ) {
			this.output = "ERROR!<br>括弧の付近に誤入力があります";
			this.statusCode = -1;
		} else if( this.output.equals( "_3+=" ) ) {
			this.output = "ERROR!<br>＝の直前は数値か閉じ括弧だけです";
			this.statusCode = -1;
		} else if( this.output.equals( "_4pnum" ) ) {
			this.output = "ERROR!<br>括弧の数があっていません";
			this.statusCode = -1;
		} else {
			// 誤入力なので無視
			this.output = "";
			this.statusCode = -1;
		}
		return this.output;
	}
	
	// いい感じにString[]の配列になってる入力を計算
	// エラー処理なし
	public String calcStringArray( String input[] ) {
		String[] rpned = rpnizer( input );
		return rpnCalculator( rpned );
	}
	
	// 後置記法の演算器
	public String rpnCalculator( String[] input ) {
		StringStack inputStack = new StringStack( input.length );
		String f, s, o;
		for( String tmp : input ) {
			if( tmp.equals( "=" ) )
				break;
			inputStack.push( tmp );
			if( inputTypeChecker( tmp ).equals( "OPE" ) ) {
				o = inputStack.pop();
				s = inputStack.pop();
				f = inputStack.pop();
				if( tmp.equals( "÷" ) && Double.valueOf( s ) == 0 ) {
					return "_0div";
				}
				
				this.preAns = Double.toString( calc( f, s, o ) );
				
				// 最後の桁を無視して0が並んでいたら取り除く
				this.preAns = zeroRemover( this.preAns );
				
				// 最後の桁を無視して9が並んでいたら取り除く
				this.preAns = nineRemover( this.preAns );
				
				int length = this.preAns.length();
				// output[1]の最後に.0が合ったら除く
				if( length > 2
						&& this.preAns.charAt( length - 1 ) == '0'
						&& this.preAns.charAt( length - 2 ) == '.' ){
					char[] tmp2 = new char[length - 2];
					for( int i = 0; i < length - 2; i++ ){
						tmp2[i] = this.preAns.charAt( i );
					}
					StringBuilder bf = new StringBuilder();
					for( int i = 0; i < length - 2; i++ ){
						bf.append( tmp2[i] );
					}
					this.preAns = bf.toString();
				}
				String out = this.preAns;
				inputStack.push( out );
			}
		}
		if( inputStack.size() == 1 ) {
			o = inputStack.pop();
			this.preAns = o;
			// 最後の桁を無視して0が並んでいたら取り除く
			this.preAns = zeroRemover( this.preAns );
			
			// 最後の桁を無視して9が並んでいたら取り除く
			this.preAns = nineRemover( this.preAns );
			
			int length = this.preAns.length();
			// output[1]の最後に.0が合ったら除く
			if( length > 2
					&& this.preAns.charAt( length - 1 ) == '0'
					&& this.preAns.charAt( length - 2 ) == '.' ){
				char[] tmp2 = new char[length - 2];
				for( int i = 0; i < length - 2; i++ ){
					tmp2[i] = this.preAns.charAt( i );
				}
				StringBuilder bf = new StringBuilder();
				for( int i = 0; i < length - 2; i++ ){
					bf.append( tmp2[i] );
				}
				this.preAns = bf.toString();
			}
			o = this.preAns;
		} else {
			o = "_1stack";
		}
		return o;
	}
	
	private String nineRemover(String in){
		int length = in.length();
		String out;
		out = in;
		// 最後の桁を無視して0が並んでいたら取り除く
		if( length > 2 ) {
			int i = length - 2, j = 0;
			while( i > 0 && in.charAt( i ) == '9' ) {
				j++;
				i--;
			}
			// j個 9が並んでいる
			if( j > 0 ){
				char[] tmp2 = {};
				// 最後の文字が9ならそれも追加
				if( in.charAt( length - 1 ) == '9' ) {
					j++;
					// 0の並びの先頭が小数点ならそれも削除
					if( in.charAt( length - j - 1 ) == '.' ) {
						// 0.999 -> l = 5, j = 3, l-j-1 = 1
						int k = 0;
						tmp2 = new char[ length - j - 1 ];
						while( k < length - j - 1  && in.charAt( k ) != '.' ) {
							tmp2[ k ] = in.charAt( k );
							k++;
						}
						tmp2[ length - j - 2 ] = ( String.valueOf( Character.getNumericValue( tmp2[ length - j - 2 ] ) + 1 ) ).charAt( 0 );
					}else{
						//tmp2 = new char[ length - j - 1 ];
						tmp2 = new char[ i + 1 ];
						int k = 0;
						while( k < i + 1 ) {
							tmp2[ k ] = in.charAt( k );
							k++;
						}
						int tmp1 = Character.getNumericValue( tmp2[ i ] ) + 1;
						tmp2[ i ] = ( String.valueOf( tmp1 ) ).charAt( 0 );
					}
				}else if( in.charAt( length - 2 ) == '9' ){
					// 1.9998 -> l = 6, j = 3, l-1 - j-1 = 1
					if( in.charAt( length - j - 2 ) == '.' ) {
						int k = 0;
						tmp2 = new char[ length - j - 2 ];
						while( k < length - j - 2  && in.charAt( k ) != '.' ) {
							tmp2[ k ] = in.charAt( k );
							k++;
						}
						tmp2[ length - j - 2 ] = ( String.valueOf( Character.getNumericValue( tmp2[ length - j - 2 ] ) + 1 ) ).charAt( 0 );
					} else {
						// 1.799998  -> l = 8, j = 4, l-1 - j = 3
						int k = 0;
						tmp2 = new char[ length - j - 1 ];
						while( k < length - j - 1  ) {
							tmp2[ k ] = in.charAt( k );
							k++;
						}
						tmp2[ length - j - 2 ] = ( String.valueOf( Character.getNumericValue( tmp2[ length - j - 2 ] ) + 1 ) ).charAt( 0 );
					}
				}
				if( tmp2.length > 0 ) {
					StringBuilder bf = new StringBuilder();
					for( i = 0; i < tmp2.length; i++ ) {
						bf.append( tmp2[ i ] );
					}
					out = bf.toString();
				}
			}
		}
		return out;
	}
	
	private String zeroRemover(String in){
		int length = in.length();
		String out;
		out = in;
		// 最後の桁を無視して0が並んでいたら取り除く
		if( length > 2 ) {
			int i = length - 2, j = 0;
			while( i > 0 && in.charAt( i ) == '0' ) {
				j++;
				i--;
			}
			// j個 0が並んでいる
			if( j > 0 ){
				char[] tmp2 = {};
				// 最後の文字が0ならそれも追加
				if( in.charAt( length - 1 ) == '0' ) {
					j++;
					// 0の並びの先頭が小数点ならそれも削除
					if( in.charAt( length - j - 1 ) == '.' ) {
						// 3.000 -> l = 5, j = 3, l-j-1 = 1
						int k = 0;
						tmp2 = new char[ length - j - 1 ];
						while( k < length - j - 1  && in.charAt( k ) != '.' ) {
							tmp2[ k ] = in.charAt( k );
							k++;
						}
					}
				}else if( in.charAt( length - 2 ) == '0' ){
					// 3.004 -> l = 5, j = 2, l-1 - j -1 = 1
					if( in.charAt( length - j - 2 ) == '.' ) {
						int k = 0;
						tmp2 = new char[ length - j - 2 ];
						while( k < length - j - 2  && in.charAt( k ) != '.' ) {
							tmp2[ k ] = in.charAt( k );
							k++;
						}
					} else {
						// 3.140002  -> l = 8, j = 3, l-1 - j = 4
						int k = 0;
						tmp2 = new char[ length - j - 1 ];
						while( k < length - j - 1  ) {
							tmp2[ k ] = in.charAt( k );
							k++;
						}
					}
				}
				if( tmp2.length > 0 ) {
					StringBuilder bf = new StringBuilder();
					for( i = 0; i < tmp2.length; i++ ) {
						bf.append( tmp2[ i ] );
					}
					out = bf.toString();
				}
			}
		}
		return out;
	}
	
	public String[] rpnizer( String[] input ) {
		String tmp, tmp2;
		StringStack opeStack = new StringStack( 100 );
		StringQueue outQueue = new StringQueue( 100 );
		int length = input.length;
		
		for( int i = 0; i < length; i++ ) {
			tmp = inputTypeChecker( input[ i ] );
			if( tmp.equals( "OTH" ) || tmp.equals( "NUM" ) ) {
				// 数値
				outQueue.enqueue( input[ i ] );
				continue;
			} else if( tmp.equals( "END" ) ) {
				// (までの演算子をoutにpush
				while( opeStack.size() > 0 ) {
					tmp2 = opeStack.pop();
					if( tmp2.equals( "(" ) )
						break;
					outQueue.enqueue( tmp2 );
				}
				continue;
			} else if( tmp.equals( "STR" ) ) {
				opeStack.push( input[ i ] );
				continue;
			} else if( tmp.equals( "OPE" ) ) {
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
						if( opeRanker( topOpe ) > opeRanker( input[ i ] ) ) {
							// input[i]を最上位に
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
			} else if( tmp.equals( "EQU" ) ) {
				while( opeStack.size() != 0 ) {
					outQueue.enqueue( opeStack.pop() );
				}
				outQueue.enqueue( input[ i ] );
				break;
			}else {
				//ここには来ないはず
				String[] error = new String[ 2 ];
				error[ 0 ] = "";
				error[ 1 ] = "on rpnizer, input analize error.";
				return error;
			}
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
		
		if( input.equals( "^" ) )
			return 4;
		if( input.equals( "%" ) )
			return 6;
		if( input.equals( "×" ) || input.equals( "÷" ) )
			return 6;
		if( input.equals( "+" ) || input.equals( "-" ) )
			return 8;
		return 99;
	}
	
	private double calc( String f, String s, String o ) {
		double first = Double.valueOf( f );
		double second = Double.valueOf( s );
		double out = 0d;
		if( o.equals( "+" ) ) {
			out = first + second;
		} else if( o.equals( "-" ) ) {
			out = first - second;
		} else if( o.equals( "×" ) ) {
			out = first * second;
		} else if( o.equals( "÷" ) ) {
			if( second == 0 ) {
				return 0d;
			}
			out = first / second;
		} else if( o.equals( "%" ) ) {
			out = first % second;
		} else if( o.equals( "^" ) ) {
			out = Math.pow( first, second );
		}
		return out;
	}
	
	public String inputChecker() {
		// this.textとthis.inputStringをチェック
		// 良ければ追加
		String output;
		
		// 入力文字列が90文字を超えるときACとBS、= 以外エラー
		
		if(
		   !( this.text.equals( "BS" ) )
		&& !( this.text.equals( "AC" ) )
		&& !( this.text.equals( "=" ) )
		&& inputCounter > 90 ){
			output = "";
			return output;
		}
		
		if( this.text.equals( "ANS" ) && this.preInput.equals( "ANS" ) ){
			output = "";
			return output;
		}
		
		if( this.inputCounter > 0 && this.inputString[ this.inputCounter - 1 ].equals( "=" ) ) {
			// =の後なら既存のinputを削除
			clear();
		}
		if( this.text.equals( "AC" )){
			clear();
			return "NONE";
		}
		
		boolean A = inputCharaChecker( this.text );
		
		// 入力自体がエラーなら追加せず返す
		if( !A ) {
			output = "";
			return output;
		} else if( this.text.equals( "BS" ) ) {
			this.inputString[ this.inputCounter - 1 ] = null;
			if( this.inputCounter > 0 ) {
				this.inputCounter--;
			}
			output = "NONE";
			return output;
		} else if( this.text.equals( "ANS" ) ) {
			this.inputString[ inputCounter ] = this.preAns;
			this.inputCounter++;
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
		} else {
			// =やACやBSでない時は適当に返す
			if( this.inputCounter > 0
					&& inputTypeChecker( this.text ).equals( "OPE" )
					&& inputTypeChecker( this.inputString[this.inputCounter - 1] ).equals( "OPE" ) ){
				this.inputString[ inputCounter - 1 ] = this.text;
				output = "NONE";
				return output;
			}else
			// 0の後0以外の数値が入力されたら0を上書き
			if( this.inputCounter == 1
					&& inputTypeChecker( this.text ).equals( "NUM" )
					&& this.inputString[ this.inputCounter - 1 ].equals( "0" ) ){
				this.inputString[ inputCounter - 1 ] = this.text;
				output = "NONE";
				return output;
			}else
			// 0の後0以外の数値が入力されたら0を上書き
			if( this.inputCounter > 1
					&& inputTypeChecker( this.text ).equals( "NUM" )
					&& this.inputString[ this.inputCounter - 1 ].equals( "0" )
					&& !( inputTypeChecker( this.inputString[ this.inputCounter - 2 ] ).equals( "NUM" ) )
					&& !( inputTypeChecker( this.inputString[ this.inputCounter - 2 ] ).equals( "DOT" ) ) ) {
				this.inputString[ inputCounter - 1 ] = this.text;
				output = "NONE";
				return output;
			}else {
				this.inputString[ inputCounter ] = this.text;
				this.inputCounter++;
				output = "NONE";
				return output;
			}
		}
	}
	
	private String[] inputNumReader( String[] text ) {
		// 0に個数、1~は位取りした数または記号を返す
		// 入力が=で終わってなかったらエラー
		int length = inputCounter;
		
		if( !( text[ length - 1 ].equals( "=" ) ) ) {
			String[] error = new String[ 2 ];
			error[ 0 ] = "";
			error[ 1 ] = "input must finish with Equal";
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
			
			if( i == 0 && tmpS.equals( "STR" ) ){
				out[ cNumArray ] = text[ i ];
				cNumArray++;
			} else if( tmpS.equals( "NUM" ) || tmpS.equals( "DOT" ) ) {
				out[ cNumArray ] = out[ cNumArray ] + text[ i ];
			} else {
				if( tmpS.equals( "OTH" ) ) {
					// ANSで前の演算結果が文字列で入ってきたとき
					out[ cNumArray ] = text[ i ];
					cNumArray++;
				}else if( preS.equals( "NUM" ) ) {
					// 数の後に数以外なら
					cNumArray++;
					out[ cNumArray ] = text[ i ];
					cNumArray++;
				}else if( preS.equals( "OTH" ) ) {
					// 数の後に数以外なら
					out[ cNumArray ] = text[ i ];
					cNumArray++;
				}else if( ( tmpS.equals( "OPE" ) )
						&& ( preS.equals( "NUM" ) || preS.equals( "OTH" ) || preS.equals( "END" ) ) ) {
						out[ cNumArray ] = text[ i ];
						cNumArray++;
				}else if( tmpS.equals( "STR" ) ) {
						out[ cNumArray ] = text[ i ];
						cNumArray++;
					
				}else if( tmpS.equals( "END" ) ) {
					out[ cNumArray ] = text[ i ];
					cNumArray++;
				}else if( (tmpS.equals( "EQU" ) )
						&& !preS.equals( "OPE" ) ) {
					out[ cNumArray ] = text[ i ];
					cNumArray++;
				}else{
					// ここには来ないはず
					String[] error = new String[ 2 ];
					error[ 0 ] = "";
					error[ 1 ] = "on input Num Reader, input analize error.";
					return error;
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
	
	private boolean inputCharaChecker( String text ) {
		boolean A = false;
		
		for( String tmp : this.CHARA ) {
			if( text.equals( tmp ) ) {
				A = true;
				break;
			}
		}
		if( !A ){
			// ANSの直接入力
		}
		
		if( A ) {
			String tmp = inputTypeChecker( text );
			
			if( tmp.equals( "END" ) ){
				// 直前が数値でないならエラー
				if( !( inputTypeChecker( this.inputString[ this.inputCounter - 1 ] ).equals( "NUM" )
						|| inputTypeChecker( this.inputString[ this.inputCounter - 1 ] ).equals( "OTH" )
						|| inputTypeChecker( this.inputString[ this.inputCounter - 1 ] ).equals( "END" ) ) ) {
					A = false;
					return A;
				}
				// 括弧閉じが入力されたとき括弧始めが存在するかチェック
				int cSTR = 0, cEND = 0;
				for( int i = 0; i < this.inputCounter; i++ ) {
					if( inputTypeChecker( this.inputString[ i ] ).equals( "STR" ) ) {
						cSTR++;
					}
					if( inputTypeChecker( this.inputString[ i ] ).equals( "END" ) ) {
						cEND++;
					}
				}
				if( cSTR > cEND ){
					A = true;
					return A;
				}else{
					A = false;
					return A;
				}
				
			}
			if( tmp.equals( "DOT" ) ) {
				if( this.inputCounter < 1 ){
					A = false;
					return A;
				}
				// 直前が数値でないならエラー
				if( !( inputTypeChecker(this.inputString[ this.inputCounter - 1 ]).equals( "NUM" ) )
				|| inputTypeChecker(this.inputString[ this.inputCounter - 1 ]).equals( "DOT" )
				|| this.preInput.equals( "ANS" ) ){
					A = false;
					return A;
				}
				int c = 0;
				// 直前の数字に小数点がついていないかチェック
				for( ; c < this.inputCounter; c++ ){
					if( inputTypeChecker(this.inputString[ this.inputCounter - 1 - c ]).equals( "NUM" ) ){
						// ok チェック続行
						continue;
					}else if( inputTypeChecker(this.inputString[ this.inputCounter - 1 - c ]).equals( "DOT" ) ){
						// ダメ
						A = false;
						return A;
					}else{
						// ok 正常終了
						A = true;
						return A;
					}
				}
			}
			
			if( tmp.equals( "ANS" ) ) {
				if( this.inputCounter == 0 ) return true;
				if( inputTypeChecker(this.inputString[ this.inputCounter - 1 ]).equals( "NUM" )
				|| inputTypeChecker(this.inputString[ this.inputCounter - 1 ]).equals( "OTH" )
				|| inputTypeChecker(this.inputString[ this.inputCounter - 1 ]).equals( "END" )
				|| inputTypeChecker(this.inputString[ this.inputCounter - 1 ]).equals( "DOT" ) ){
					return false;
				}else{
					return true;
				}
			}else if( tmp.equals( "EQU" ) ) {
				if( this.inputCounter == 0 ) return false;
				
				// 括弧の数をチェック
				if( !( inputParenthesesChecker( inputString ) ) ) {
					A = false;
					return A;
				}
				// 直前の入力が閉じ括弧か数値ならok
				if( this.inputString[ this.inputCounter - 1 ].equals( ")" )
				|| inputTypeChecker(this.inputString[ this.inputCounter - 1 ]).equals( "NUM" )
				|| inputTypeChecker(this.inputString[ this.inputCounter - 1 ]).equals( "OTH" ) ){
					return true;
				}else{
					A = false;
					return A;
				}
			}else if( tmp.equals( "NUM" ) || tmp.equals( "ANS" ) ) {    // )1
				if( this.inputCounter == 0 ) return true;
				if( this.inputCounter > 0 ) {
					// )の後に数値はエラー
					if( this.inputString[ this.inputCounter - 1 ].equals( ")" ) ) {
						A = false;
					}
					// ANSボタンの後に数値はエラー
					if( this.inputString[ this.inputCounter - 1 ].equals( this.preAns ) ) {
						return false;
					}
				}
			}
			if( this.inputString[ 0 ] == null ) {
				// 最初の入力が数字か(でないならエラー
				if( tmp.equals( "STR" ) ) {
					return true;
				}
				return false;
			}
			if( tmp.equals( "OPE" ) ) {    // (+
				if( this.inputString[ this.inputCounter - 1 ].equals( "(" ) ) {
					A = false;
					return A;
				}
			}
			if( text.equals( "(" ) ) {  // 1(
				if( !( inputTypeChecker( this.inputString[ this.inputCounter - 1 ] ).equals( "OPE" ) )
				&&  !( inputTypeChecker( this.inputString[ this.inputCounter - 1 ] ).equals( "STR" ) ) ) {
					// 直前が演算子でないならエラー
					A = false;
					return A;
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
				} else if( i == 22 ) {      // ANS
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
	
	private void clear() {
		
		for( int i = this.inputCounter - 1; i >= 0; i-- ) {
			this.inputString[ i ] = null;
		}
		for( int i = 0; i < this.inputArray.length; i++ ) {
			if( this.inputArray[0] == null )
				break;
			if( this.inputArray[ i ].isEmpty() )
				break;
			this.inputArray[ i ] = null;
		}
		
		this.output = "";
		this.inputCounter = 0;
		this.statusCode = 0;
		
	}
	
	public int getStatus() {
		// -1 error
		// 0  unresolvable
		// 1  resolve
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
	
	public String getOutputString() {
		return this.preAns;
	}
	
	public void setPreAns( String in ) {
		this.preAns = in;
	}
	
}
