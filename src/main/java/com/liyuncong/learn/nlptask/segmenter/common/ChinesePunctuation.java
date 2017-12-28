package com.liyuncong.learn.nlptask.segmenter.common;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author liyuncong
 *
 */
public class ChinesePunctuation {
	private static Set<String> punctuations = new HashSet<>();
	static {
		punctuations.add("，");
		punctuations.add("。");
		punctuations.add("；");
		punctuations.add("：");
		punctuations.add("‘");
		punctuations.add("’");
		punctuations.add("“");
		punctuations.add("”");
		punctuations.add("？");
		punctuations.add("《");
		punctuations.add("》");
		punctuations.add("（");
		punctuations.add("）");
	}
	
	private ChinesePunctuation(){}
	
	public static boolean isPunctuation(String candidate) {
		return punctuations.contains(candidate);
	}
}
