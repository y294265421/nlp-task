package com.liyuncong.learn.nlptask.stanfordcorenlp.crfsegmenter;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

public class CrfSegmenter {
	public static void main(String[] args) throws IOException {
		Properties props = new Properties();
		String basedir = "stanfordcorenlp/crfsegmenter/data";
		props.setProperty("sighanCorporaDict", basedir);
	    props.setProperty("inputEncoding", "UTF-8");
	    props.setProperty("sighanPostProcessing", "true");

	    CRFClassifier<CoreLabel> segmenter = new CRFClassifier<>(props);
	    segmenter.loadClassifierNoExceptions("stanfordcorenlp/crfsegmenter/crfsegmenter.ser.gz", props);
	    String sample = "我家住在南山区";
	    List<String> segmented = segmenter.segmentString(sample);
	    System.out.println(segmented);
	}
}
