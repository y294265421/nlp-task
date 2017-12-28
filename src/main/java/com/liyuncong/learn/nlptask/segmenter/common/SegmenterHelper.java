package com.liyuncong.learn.nlptask.segmenter.common;

import java.util.LinkedList;
import java.util.List;

public class SegmenterHelper {
	private SegmenterHelper(){}
	
	/**
	 * 
	 * @param word 词
	 * @return word的类别表示，比如 美丽 -> BE
	 */
	public static String tranferWordToClassRepresentation(String word) {
		if (word == null) {
			throw new IllegalArgumentException("转化为类别表示形式的词不能为null");
		}
		
		StringBuilder stringBuilder = new StringBuilder();
		int len = word.length();
		
		if (len == 0) {
			throw new IllegalArgumentException("转化为类别表示的词的长度不能为0");
		}
		
		if (len == 1) {
			stringBuilder.append(HiddenStates.SINGLE.getAlias());
		} else {
			stringBuilder.append(HiddenStates.BEGIN.getAlias());
			for(int i = 1; i <= len - 2; i++) {
				stringBuilder.append(HiddenStates.MIDDLE.getAlias());
			}
			stringBuilder.append(HiddenStates.END.getAlias());
		}
		return stringBuilder.toString();
	}
	
	/**
	 * 
	 * @param sentence 句子
	 * @return 句子的类别表示 比如 我 喜欢 她 -> SBES
	 * @throws NullPointerException if sentence is null
	 */
	public static String transferSentenceToClassRepresentation(String sentence) {
		if (sentence == null) {
			throw new NullPointerException();
		}
		List<String> words = TextTools.findAllChineseWord(sentence);
		StringBuilder sentenceClassRepresentation = new StringBuilder();
		for (String word : words) {
			sentenceClassRepresentation.append(tranferWordToClassRepresentation(word));
		}
		return sentenceClassRepresentation.toString();
	}
	
	/**
	 * 
	 * @param sentences 句子集合
	 * @return 句子集合中所有句子的类别表示的集合
	 * @throws NullPointerException if sentences is null
	 */
	public static List<String> transferAllSentenceToClassRepresentation(List<String> sentences) {
		if (sentences == null) {
			throw new NullPointerException();
		}
		List<String> sentenceClassRepresentations = new LinkedList<String>();
		for (String sentence : sentences) {
			sentenceClassRepresentations.add(transferSentenceToClassRepresentation(sentence));
		}
		return sentenceClassRepresentations;
	}
	
	/**
	 * 
	 * @param sentence 句子
	 * @param hiddenStates 句子对应的隐藏状态
	 * @return 句子中的词用斜线分隔开 如 我/喜欢/你/
	 */
	public static String segment(String sentence, String[] hiddenStates) {
		if (sentence == null || hiddenStates == null) {
			throw new NullPointerException();
		}
		int len = sentence.length();
		int len1 = hiddenStates.length;
		if (len != len1) {
			throw new IllegalArgumentException("sentence 的长度和hiddenStates的长度不一致");
		}
		StringBuilder stringBuilder = new StringBuilder();
		for(int i = 0; i < len; i++) {
			stringBuilder.append(sentence.charAt(i));
			// 用斜线标识一个词的结束
			if (HiddenStates.SINGLE.getAlias().equals(hiddenStates[i])|| 
					HiddenStates.END.getAlias().equals(hiddenStates[i])) {
				stringBuilder.append("/");
			}
		}
		return stringBuilder.toString();
	}
}
