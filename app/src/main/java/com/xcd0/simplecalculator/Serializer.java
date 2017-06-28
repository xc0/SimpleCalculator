package com.xcd0.simplecalculator;

import java.util.LinkedList;
import java.text.StringCharacterIterator;
import java.text.CharacterIterator;

// http://qiita.com/GachiNyanNyan/items/1b6c0d8730fd9ecbad4d

public class Serializer {
	//  String配列をStringに変換する
	//  また、その逆を行う
	//  内部形式はカンマ区切りだが自動的にエスケープされるので使用不可能な文字はない
	//  ネストすることも可能でシリアライズされた文字列は可視文字だけで構成される（バイナリではなくテキスト）
	//  故に、仕組み的にどんなグチャグチャなデータを渡してもシリアライズとデシリアライズの過程で例外が発生することはない
	public static String serialize(String... elements) {
		StringBuilder serialized = new StringBuilder();
		for (String s: elements) {
			if (serialized.length() > 0) {
				serialized.append(',');
			}
			
			//  エスケープ処理（順番は重要）
			StringBuilder value = new StringBuilder(s);
			replace(value, "\\", "\\\\");
			replace(value, ",", "\\,");
			
			serialized.append(value.toString());
		}
		return serialized.toString();
	}
	
	//  デシリアライズします
	public static LinkedList<String> deserializeToList(String serialized) {
		LinkedList<String> elements = new LinkedList<String>();
		
		int bsCombo = 0;    //  バックスラッシュが連続して登場しているカウント
		int start = 0;      //  次に切り出す開始位置インデックス
		
		StringCharacterIterator it = new StringCharacterIterator(serialized);
		for (char c=it.first(); c!=CharacterIterator.DONE; c=it.next()) {
			if (c != ',') {
				if (c == '\\') {
					bsCombo++;
				} else {
					bsCombo = 0;
				}
				continue;
			}
			
			//  カンマがエスケープされていないならここで区切る
			if (bsCombo%2 == 0) {   //  bsComboが0でもtrue
				//  エスケープ解除処理（順番は重要）
				StringBuilder value = new StringBuilder(serialized.substring(start, it.getIndex()));
				replace(value, "\\,", ",");
				replace(value, "\\\\", "\\");
				
				elements.addLast(value.toString());
				start = it.getIndex()+1;
			}
			
			//  今回はカンマだったのでコンボは途切れる
			bsCombo = 0;
		}
		
		//  最後の要素を処理する
		//  エスケープ解除処理（順番は重要）
		StringBuilder value = new StringBuilder(serialized.substring(start, it.getEndIndex()));
		replace(value, "\\,", ",");
		replace(value, "\\\\", "\\");
		elements.addLast(value.toString());
		
		return elements;
	}
	
	//  StringをString配列にデシリアライズします
	//  deserializeToList()　の戻り値を配列に変換して返します
	public static final String[] deserialize(String serialized) {
		LinkedList<String> ret = deserializeToList(serialized);
		return ret.toArray(new String[ret.size()]);
	}
	
	//  StringBuilder版replace
	//  登場するtargetすべてをstrに置き換えます
	//  回帰処理はされません（/ -> //　などの時無限ループになるのでおそらく本家もしていない）
	//  置き換えた数を返す
	public static int replace(StringBuilder sb, String target, String str) {
		int count = 0;
		
		int tokenStart;
		int offset = 0;
		while ((tokenStart=sb.indexOf(target, offset)) != -1) {
			sb.replace(tokenStart, tokenStart+target.length(), str);
			offset = tokenStart+str.length();
			count++;
		}
		
		return count;
	}
}
