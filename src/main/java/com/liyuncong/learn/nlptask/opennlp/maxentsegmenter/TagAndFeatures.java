package com.liyuncong.learn.nlptask.opennlp.maxentsegmenter;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liyuncong.learn.common.CommonTools;

/**
 * 
 * @author liyuncong
 *
 */
public class TagAndFeatures {
	private static Logger logger = LoggerFactory.getLogger(TagAndFeatures.class);
	private String tag;
	private String[] context;

	public TagAndFeatures(String tag, String[] context) {
		super();
		this.tag = tag;
		this.context = context;
	}

	@Override
	public String toString() {
		List<String> result = new LinkedList<>();
		if (tag != null) {
			result.add(tag);
		}
		
		// Cn(n = −2,−1, 0, 1, 2)
		for(int i = 0; i < context.length; i++) {
			if (context[i] != null) {
				result.add("c" + (i - 2) + "=" + context[i]);
			}
		}
		
		// CnCn+1(n = −2,−1, 0, 1)
		for(int i = 0; i < context.length - 1; i++) {
			if (context[i] != null && context[i + 1] != null) {
				result.add("c" + (i - 2) + "c" + (i - 2 + 1) + "=" + context[i] + context[i + 1]);
			}
		}
		
		// C−1C1
		if (context[1] != null && context[3] != null) {
			result.add("c-1c1=" + context[1] + context[3]);
		}
		
		// Pu(C0)
//		if (ChinesePunctuation.isPunctuation(context[2])) {
//			result.add("punctuation=true");
//		} else {
//			result.add("punctuation=false");
//		}
		
		// T(C−2)T(C−1)T(C0)T(C1)T(C2)
		
		return CommonTools.join(" ", result.toArray(new String[0]));
	}
	
	/**
	 * 
	 * @param sentence 句子
	 * @return
	 * @throws NullPointerException if sentence is null
	 * @throws IllegalArgumentException if sentence.length is 0 or 
	 * sentence.length != sentenceClassRepresentation.length
	 */
	public static List<TagAndFeatures> generateTagAndFeatures(String sentence, 
			String sentenceClassRepresentation) {
		if (sentence == null) {
			throw new NullPointerException();
		}
		if ((sentence.length() == 0) || (sentenceClassRepresentation != null && sentence.length() != sentenceClassRepresentation.length())) {
			logger.error("sentence:{}, sentenceClassRepresentation:{}", sentence, sentenceClassRepresentation);
			throw new IllegalArgumentException();
		}
		List<TagAndFeatures> result = new LinkedList<>();
		for(int i = 0; i < sentence.length(); i++) {
			String[] context = new String[5];
			int lowBound = i - 2;
			int upBound = i + 2;
			int contextIndex = 0;
			for(int j = lowBound; j <= upBound; j++) {
				if (j >= 0 && j < sentence.length()) {
					context[contextIndex] = sentence.substring(j, j + 1);
				}
				contextIndex++;
			}
			String tag = null;
			if (sentenceClassRepresentation != null) {
				tag = sentenceClassRepresentation.substring(i, i + 1);
			}
			TagAndFeatures tagAndFeatures = new TagAndFeatures(tag, context);
			result.add(tagAndFeatures);
		}
		return result;
	}
}
