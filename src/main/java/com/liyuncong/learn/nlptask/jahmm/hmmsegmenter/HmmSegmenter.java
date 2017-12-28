package com.liyuncong.learn.nlptask.jahmm.hmmsegmenter;

import java.util.List;

import com.liyuncong.learn.nlptask.segmenter.common.HiddenStates;
import com.liyuncong.learn.nlptask.segmenter.common.SegmenterHelper;

import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationInteger;

public class HmmSegmenter {
	private static Hmm<ObservationInteger> hmm = SegmentationHmmFactory.hmm();
	
	private HmmSegmenter(){}
	
	/**
	 * 
	 * @param sentence 句子
	 * @return 句子中的词用斜线分隔开 如 我/喜欢/你/
	 * @throws NullPointerException if sentence is null
	 * @throws IllegalArgumentException if sentence.length is 0
	 */
	public static String segment(String sentence) {
		if (sentence == null) {
			throw new NullPointerException();
		}
		if (sentence.length() == 0) {
			throw new IllegalArgumentException();
		}
		
		List<ObservationInteger> observationSequence = SegmentationHmmFactory.generateObservationSequence(sentence);
		// use the Viterbi Algorithm
		int[] hiddenStatesSeq = hmm.mostLikelyStateSequence(observationSequence);
		String[] hiddenStates = SegmentationHmmFactory.hiddenStatesSeqToHiddenStates(hiddenStatesSeq);
		return SegmenterHelper.segment(sentence, hiddenStates);
	}
}
