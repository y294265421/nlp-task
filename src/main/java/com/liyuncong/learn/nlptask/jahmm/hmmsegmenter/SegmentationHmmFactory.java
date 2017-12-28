package com.liyuncong.learn.nlptask.jahmm.hmmsegmenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.liyuncong.learn.nlptask.segmenter.common.HiddenStates;
import com.liyuncong.learn.nlptask.segmenter.common.SegmenterHelper;
import com.liyuncong.learn.nlptask.segmenter.common.TextTools;
import com.liyuncong.learn.nlptask.segmenter.common.Training;

import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationInteger;
import be.ac.ulg.montefiore.run.jahmm.OpdfInteger;
import be.ac.ulg.montefiore.run.jahmm.OpdfIntegerFactory;

public class SegmentationHmmFactory {
	private static Hmm<ObservationInteger> hmm;
	
	private SegmentationHmmFactory() {
		
	}
	
	/**
	 * 
	 * @param sentences 句子（类别）集合
	 * @param pattern 模式 比如 美丽或BE
	 * @return 模式在集合中出现的次数
	 * @throws NullPointerException if sentences or pattern is null
	 */
	public static int count(List<String> sentences, String pattern) {
		if (sentences == null || pattern == null) {
			throw new NullPointerException();
		}
		int count = 0;
		int subWordLen = pattern.length();
		for (String sentence : sentences) {
			int len = sentence.length();
			int upBound = len - subWordLen;
			for(int i = 0; i <= upBound; i++) {
				if (sentence.subSequence(i, i + subWordLen).equals(pattern)) {
					count++;
				}
			}
		}
		return count;
	}
	
	/**
	 * 计算转移矩阵。
	 * @param sentencesClassRepresentation 句子类别表示集合
	 * @return 转移矩阵
	 * @throws NullPointerException if sentencesClassRepresentation is null
	 */
	public static double[][] computeTransitionMatrix(List<String> sentencesClassRepresentation) {
		if (sentencesClassRepresentation == null) {
			throw new NullPointerException();
		}
		int classNum = ClassDictionary.getInstance().size();
		double[][] transitionMatrix = new double[classNum][classNum];
		double bNum = count(sentencesClassRepresentation, HiddenStates.BEGIN.getAlias());
		double mNum = count(sentencesClassRepresentation, HiddenStates.MIDDLE.getAlias());
		double eNum = count(sentencesClassRepresentation, HiddenStates.END.getAlias());
		double sNum = count(sentencesClassRepresentation, HiddenStates.SINGLE.getAlias());
		String[] classs = new String[] {
				HiddenStates.BEGIN.getAlias(), 
				HiddenStates.MIDDLE.getAlias(), 
				HiddenStates.END.getAlias(), 
				HiddenStates.SINGLE.getAlias()};
		double[] counts = new double[] {bNum, mNum, eNum, sNum};
		for(int i = 0; i < classNum; i++) {
			for(int j = 0; j < classNum; j++) {
				String pattern = classs[i] + classs[j];
				double patternNum = count(sentencesClassRepresentation, pattern);
				 // Aij = P(Cj|Ci)  =  P(Ci,Cj) / P(Ci) = Count(Ci,Cj) / Count(Ci)
				transitionMatrix[i][j] = patternNum / counts[i];
			}
		}
		return transitionMatrix;
	}
	
	/**
	 * 计算发射矩阵
	 * @param text 文本 比如 我喜欢你你喜欢我...
	 * @param classRepresentations 文本的列别表示 SBESSBES
	 * @return 发射矩阵
	 * @throws NullPointerException if text or classRepresentations is null
	 */
	public static double[][] computeEmissionMatrix(String text, String classRepresentations) {
		if (text == null || classRepresentations == null) {
			throw new NullPointerException();
		}
		if (text.length() != classRepresentations.length()) {
			throw new IllegalArgumentException("text的长度和classRepresentations的长度不一样");
		}
		int classNum = ClassDictionary.getInstance().size();
		int letterNum = LetterDictionary.getInstance().size();
		double[][] emissionMatrix = new double[classNum][letterNum];
		
		// 统计每个类别出现了多少次
		Map<String, Integer> classCount = new HashMap<String, Integer>();
		List<String> sentences = new LinkedList<String>();
		sentences.add(classRepresentations);
		for(String classRepresentation : ClassDictionary.getInstance().classs()) {
			int num = count(sentences, classRepresentation);
			classCount.put(classRepresentation, num);
		}
		
		// 统计每种字符/类别对出现了多少次
		Map<LetterClassPair, Integer> letterClassPairCount = new HashMap<LetterClassPair, Integer>();
		for(int i = 0; i < text.length(); i++) {
			LetterClassPair letterClassPair = new LetterClassPair
					(text.substring(i, i + 1), classRepresentations.substring(i, i + 1));
			if (letterClassPairCount.keySet().contains(letterClassPair)) {
				letterClassPairCount.put(letterClassPair, letterClassPairCount.get(letterClassPair) + 1);
			} else {
				letterClassPairCount.put(letterClassPair, 1);
			}
		}
		
		for(LetterClassPair letterClassPair : letterClassPairCount.keySet()) {
			int firstIndex = ClassDictionary.getInstance().value(letterClassPair.getClassRepresentation());
			int secondIndex = LetterDictionary.getInstance().value(letterClassPair.getLetter());
			int letterClassPairNum = letterClassPairCount.get(letterClassPair);
			int classNumTemp = classCount.get(letterClassPair.getClassRepresentation());
			// Bij = P(Oj|Ci)  =  P(Oj,Ci) / P(Ci) = Count(Oj,Ci) / Count(Ci)
			// Bij = P(Oj|Ci)  =  (Count(Oj,Ci) + 1)/ Count(Ci)
			double probability = (double) (letterClassPairNum + 1) / classNumTemp;
			emissionMatrix[firstIndex][secondIndex] = probability;
		}
		
		// 处理矩阵中的0
		for(int i = 0; i < classNum; i ++) {
			for(int j = 0; j < letterNum; j++) {
				if (emissionMatrix[i][j] == 0) {
					String classRepresentation = ClassDictionary.getInstance().key(i);
					int classNumTemp = classCount.get(classRepresentation);
					// Bij = P(Oj|Ci)  =  (Count(Oj,Ci) + 1)/ Count(Ci)
					double probability = (double) 1 / classNumTemp;
					emissionMatrix[i][j] = probability;
				}
			}
		}
		
		return emissionMatrix;
	}
	
	public static Hmm<ObservationInteger> hmm() {
		if (hmm != null) {
			return hmm;
		}
		
		int hiddenStatesNum = ClassDictionary.getInstance().size();
		int observedStatesNum = LetterDictionary.getInstance().size();
		Hmm<ObservationInteger> hmm = new Hmm<>(hiddenStatesNum, new OpdfIntegerFactory(observedStatesNum));
		
		hmm.setPi(0, 0.5);
		hmm.setPi(1, 0);
		hmm.setPi(2, 0);
		hmm.setPi(3, 0.5);
		
		// 训练数据
		List<String> sentences = Training.getInstance().getSentences();
		
		// 转移矩阵
		List<String> sentencesClassRepresentation = SegmenterHelper.transferAllSentenceToClassRepresentation(sentences);
		double[][] transitionMatrix = computeTransitionMatrix(sentencesClassRepresentation);
		for(int i = 0; i < transitionMatrix.length; i++) {
			for(int j = 0; j < transitionMatrix[0].length; j++) {
				hmm.setAij(i, j, transitionMatrix[i][j]);
			}
		}
		
		// 发射矩阵
		StringBuilder text = new StringBuilder();
		for (String sentence : sentences) {
			List<String> words = TextTools.findAllChineseWord(sentence);
			for (String word : words) {
				text.append(word);
			}
		}
		StringBuilder classRepresentations = new StringBuilder();
		for (String sentenceClassRepresentation : sentencesClassRepresentation) {
			classRepresentations.append(sentenceClassRepresentation);
		}
		double[][] emissionMatrix = computeEmissionMatrix(text.toString(), classRepresentations.toString());
		for(int i = 0; i < emissionMatrix.length; i++) {
			hmm.setOpdf(i, new OpdfInteger(emissionMatrix[i]));
		}
		
		return hmm;
	}
	
	/**
	 * 
	 * @param sentence 句子
	 * @return 句子中每一个字用对应的序号表示，生成的观察序列
	 * @throws NullPointerException if sentence is null
	 * @throws IllegalArgumentException if sentence.length is 0
	 */
	public static List<ObservationInteger> generateObservationSequence(String sentence) {
		if (sentence == null) {
			throw new NullPointerException();
		}
		int len = sentence.length();
		if (len == 0) {
			throw new IllegalArgumentException();
		}
		
		LetterDictionary letterDictionary = LetterDictionary.getInstance();
		List<ObservationInteger> observationSequence = new ArrayList<>();
		for(int i = 0; i < len; i++) {
			String letter = sentence.substring(i, i + 1);
			Integer serialNum = letterDictionary.value(letter);
			observationSequence.add(new ObservationInteger(serialNum));
		}
		return observationSequence;
	}
	
	/**
	 * 
	 * @param hiddenStatesSeq 隐藏状态序列
	 * @return 把隐藏状态序列中的序列号转化为对应的隐藏状态
	 * @throws NullPointerException if hiddenStatesSeq is null
	 * @throws IllegalArgumentException if hiddenStatesSeq.length is 0
	 */
	public static String[] hiddenStatesSeqToHiddenStates(int[] hiddenStatesSeq) {
		if (hiddenStatesSeq == null) {
			throw new NullPointerException();
		}
		int len = hiddenStatesSeq.length;
		if (len == 0) {
			throw new IllegalArgumentException();
		}
		
		ClassDictionary classDictionary = ClassDictionary.getInstance();
		String[] hiddenStates = new String[len];
		for(int i = 0; i < len; i++) {
			hiddenStates[i] = classDictionary.key(hiddenStatesSeq[i]);
		}
		return hiddenStates;
	}
	
}
