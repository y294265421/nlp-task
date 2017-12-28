package com.liyuncong.learn.nlptask.stanfordcorenlp.crfsegmenter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.util.CoreMap;

/**
 * https://nlp.stanford.edu/software/segmenter-faq.html
 * @author liyuncong
 *
 */
public class TrainCrfSegmenterModel {
	private TrainCrfSegmenterModel() {
	}
	public static void main(String[] args) throws IOException{
		train();
	}
	
	private static void train() throws IOException {
		Properties props = new Properties();
		props.load(new InputStreamReader(new FileInputStream("stanfordcorenlp/crfsegmenter/data/ctb.prop"), "utf-8"));
		String basedir = "stanfordcorenlp/crfsegmenter/data";
		props.setProperty("sighanCorporaDict", basedir);
//		props.setProperty("serDictionary", basedir + "/dict-chris6.ser.gz");
		props.setProperty("trainFile", "sighandata/icwb2-data/training/pku_training2.utf8_small");
		CRFClassifier<CoreMap> classifier = new CRFClassifier<>(props);
		classifier.train();
		classifier.serializeClassifier("stanfordcorenlp/crfsegmenter/crfsegmenter.ser.gz");
	}
}
