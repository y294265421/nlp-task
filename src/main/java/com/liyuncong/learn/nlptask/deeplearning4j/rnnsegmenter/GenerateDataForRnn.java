package com.liyuncong.learn.nlptask.deeplearning4j.rnnsegmenter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liyuncong.learn.nlptask.segmenter.common.SegmenterHelper;
import com.liyuncong.learn.nlptask.segmenter.common.TextTools;
import com.liyuncong.learn.nlptask.segmenter.common.Training;

public class GenerateDataForRnn {
	private static Logger logger = LoggerFactory.getLogger(GenerateDataForRnn.class);
	private GenerateDataForRnn(){}
	
	public static void generateDataForRnn() {
		List<String> sentences =Training.getInstance().getSentences();
		List<String> cleanSentences = new LinkedList<>();
		List<String> cleanSentencesClassRepresentations = new ArrayList<>();
		for(String sentence : sentences) {
			List<String> words = TextTools.findAllChineseWord(sentence);
			StringBuilder cleanSentence = new StringBuilder();
			StringBuilder classRepresentation = new StringBuilder();
			for (String word : words) {
				cleanSentence.append(word);
				classRepresentation.append(SegmenterHelper.tranferWordToClassRepresentation(word));
			}
			if (cleanSentence.length() == 0) {
				logger.warn("empty sentence:{}", sentence);
				continue;
			}
			cleanSentences.add(cleanSentence.toString());
			cleanSentencesClassRepresentations.add(classRepresentation.toString());
		}
	}
}
