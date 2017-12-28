package com.liyuncong.learn.nlptask.opennlp.maxentsegmenter;

import java.io.IOException;
import java.util.HashMap;

import com.liyuncong.learn.common.Constants;

import opennlp.tools.ml.maxent.GISTrainer;
import opennlp.tools.ml.model.Event;
import opennlp.tools.ml.model.FileEventStream;
import opennlp.tools.ml.model.MaxentModel;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.TrainingParameters;

public class Main {
	private Main(){}
	
	public static void main(String[] args) throws IOException {
		GISTrainer gisTrainer = new GISTrainer();
		gisTrainer.init(new TrainingParameters(), new HashMap<>());
		ObjectStream<Event> events = new FileEventStream("opennlp\\samples\\sports\\gameLocation.dat", Constants.FROM_CHARSET_NAME);
		MaxentModel maxentModel = gisTrainer.train(events);
		String[] context = new String[] {"Rainy", "Happy"};
		double[] result = maxentModel.eval(context);
		System.out.println(maxentModel.getBestOutcome(result));
		System.out.println(maxentModel.getAllOutcomes(result));
	}
}
