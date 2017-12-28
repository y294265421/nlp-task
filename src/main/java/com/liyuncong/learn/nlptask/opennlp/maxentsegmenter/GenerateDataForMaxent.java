package com.liyuncong.learn.nlptask.opennlp.maxentsegmenter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liyuncong.learn.common.FileTools;
import com.liyuncong.learn.nlptask.segmenter.common.SegmenterHelper;
import com.liyuncong.learn.nlptask.segmenter.common.TextTools;
import com.liyuncong.learn.nlptask.segmenter.common.Training;

/**
 * 生成训练maxent模型的数据；
 * 特征选择方案见论文：
 * A Maximum Entropy Approach to Chinese Word Segmentation
 * @author liyuncong
 *
 */
public class GenerateDataForMaxent {
	private static Logger logger = LoggerFactory.getLogger(GenerateDataForMaxent.class);
	private GenerateDataForMaxent(){}
	
	public static void generateDataForMaxent() {
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
		
		int cleanSentencesClassRepresentationsIndex = 0;
		List<String> allTagAndFeatures = new LinkedList<>();
		for(String cleanSentence : cleanSentences) {
			String cleanSentencesClassRepresentation = cleanSentencesClassRepresentations
					.get(cleanSentencesClassRepresentationsIndex);
			List<TagAndFeatures> temp = TagAndFeatures.generateTagAndFeatures(cleanSentence, 
					cleanSentencesClassRepresentation);
			for (TagAndFeatures tagAndFeatures : temp) {
				allTagAndFeatures.add(tagAndFeatures.toString());
			}
			cleanSentencesClassRepresentationsIndex++;
		}
		
		FileTools.writeToNewFile(allTagAndFeatures, "opennlp/maxentsegmenter/data.dat");
	}
}
