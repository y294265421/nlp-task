package com.liyuncong.learn.nlptask.segmenter.common;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextTools {
	private TextTools(){}
	
	/**
	 * 
	 * @param text 文本
	 * @return 文件中的所有中文句子的集合
	 * @throws NullPointerException if text is null
	 */
	public static List<String> findAllChineseSentence(String text) {
		if (text == null) {
			throw new NullPointerException();
		}
		List<String> sentences = new LinkedList<String>();
		// 中文汉字、空格和中文0-9
		Pattern pattern = Pattern.compile("([\u4e00-\u9fa5]|\\s|[\uff10-\uff19])+");
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			String sentence = matcher.group();
			sentences.add(sentence);
		}
		return sentences;
	}

	/**
	 * 
	 * @param sentence 中文句子，每个词用空格分开
	 * @return 句子中所有词的集合
	 * @throws NullPointerException if sentence is null
	 */
	public static List<String> findAllChineseWord(String sentence) {
		if (sentence == null) {
			throw new NullPointerException();
		}
		List<String> words = new LinkedList<String>();
		// 中文汉字和中文0-9
		Pattern pattern = Pattern.compile("([\u4e00-\u9fa5]|[\uff10-\uff19])+");
		Matcher matcher = pattern.matcher(sentence);
		while (matcher.find()) {
			String word = matcher.group();
			words.add(word);
		}
		return words;
	}
}
