package com.liyuncong.learn.nlptask.opennlp.maxentsegmenter;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liyuncong.learn.common.Constants;
import com.liyuncong.learn.nlptask.segmenter.common.HiddenStates;
import com.liyuncong.learn.nlptask.segmenter.common.SegmenterHelper;

import opennlp.tools.ml.maxent.GISTrainer;
import opennlp.tools.ml.maxent.io.BinaryGISModelReader;
import opennlp.tools.ml.maxent.io.BinaryGISModelWriter;
import opennlp.tools.ml.maxent.io.GISModelReader;
import opennlp.tools.ml.maxent.io.GISModelWriter;
import opennlp.tools.ml.model.AbstractModel;
import opennlp.tools.ml.model.Event;
import opennlp.tools.ml.model.FileEventStream;
import opennlp.tools.ml.model.MaxentModel;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.TrainingParameters;

public class MaxentSegmenter {
	private static Logger logger = LoggerFactory.getLogger(MaxentModel.class);
	private static MaxentModel maxentModel;
	
	static {
		try {
			File modelFile = new File("opennlp/maxentsegmenter/maxent_segmenter"+".bin");
			if (modelFile.exists()) {
				InputStream inputStream = new FileInputStream(modelFile);
				DataInputStream dataInputStream = new DataInputStream(inputStream);
				maxentModel = new BinaryGISModelReader(dataInputStream).getModel();
			} else {
				GISTrainer gisTrainer = new GISTrainer();
				gisTrainer.init(new TrainingParameters(), new HashMap<>());
				ObjectStream<Event> events = new FileEventStream("opennlp/maxentsegmenter/data.dat", Constants.FROM_CHARSET_NAME);
				maxentModel = gisTrainer.trainModel(events);
				GISModelWriter writer = new BinaryGISModelWriter((AbstractModel) maxentModel, modelFile);
				writer.persist(); 
			}
		} catch (IOException e) {
			logger.error("{}", e);
		}
	}
	
	private MaxentSegmenter(){}
	
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
		String[] hiddenStates = new String[sentence.length()];
		List<TagAndFeatures> tagAndFeatures = TagAndFeatures.generateTagAndFeatures(sentence, null);
		for(int i = 0; i < hiddenStates.length; i++) {
			double[] evalResult = maxentModel.eval(tagAndFeatures.get(i).toString().split("\\s"));
			String best = maxentModel.getBestOutcome(evalResult);
			hiddenStates[i] = best;
		}
		return SegmenterHelper.segment(sentence, hiddenStates);
	}
	
}
