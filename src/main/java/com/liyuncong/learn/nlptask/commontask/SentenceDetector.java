package com.liyuncong.learn.nlptask.commontask;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.BreakIterator;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.sentdetect.SentenceSample;
import opennlp.tools.sentdetect.SentenceSampleStream;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

public class SentenceDetector {
	public static void main(String[] args) throws IOException {
//		trainOpennlpSentenceModel();
		String text = "我在知乎上搜了一个公司相关话题！发现这个话题下面有该公司HR的账号？我就私信了一下他，当天，过了一两个小时，他就回复我，给我一个邮箱地址，让我把简历发送过去，合适联系。这处理速度，我建议以后知乎可以做知乎招聘了  。";
//		String text = "Pierre Vinken, 61 years old, will join the board as a nonexecutive director Nov. 29. Mr. Vinken is chairman of Elsevier N.V., the Dutch publishing group. Rudolph Agnew, 55 years old and former chairman of Consolidated Gold Fields PLC, was named a director of this British industrial conglomerate.";
		List<String> sentences = opennlpDetectSentence(text);
		for (String sentence : sentences) {
			System.out.println(sentence);
		}
	}
	
	public static List<String> detectSentence(String text) {
		BreakIterator sentenceIterator = BreakIterator.getSentenceInstance(Locale.CHINESE);
		sentenceIterator.setText(text);
		int start = sentenceIterator.first();
		int end;
		List<String> sentences = new LinkedList<>();
		while ((end = sentenceIterator.next()) != BreakIterator.DONE) {
			String sentence = text.substring(start, end);
			start = end;
			sentences.add(sentence);
		}
		return sentences;
	}
	
	public static List<String> opennlpDetectSentence(String text) {
		InputStream modelIn = null;
		SentenceModel model = null;
		try {
			modelIn = new FileInputStream("opennlp/model/en-sent.bin");
			model = new SentenceModel(modelIn);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (IOException e) {
				}
			}
		}
		SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
		String sentences[] = sentenceDetector.sentDetect(text);
		return Arrays.asList(sentences);
	}
	
	public static void trainOpennlpSentenceModel() throws IOException {
		File trainFile = new File("opennlp/model/cn-sent.train");
		InputStreamFactory inputStreamFactory = new MarkableFileInputStreamFactory(trainFile);
		ObjectStream<String> lineStream = new PlainTextByLineStream(inputStreamFactory,
				StandardCharsets.UTF_8);
		SentenceModel model;
		try (ObjectStream<SentenceSample> sampleStream = new SentenceSampleStream(lineStream)) {
			model = SentenceDetectorME.train("cn", sampleStream, true, null, TrainingParameters.defaultParams());
		}

		try (OutputStream modelOut = new BufferedOutputStream(new FileOutputStream("opennlp/model/cn-sent.bin"))) {
			model.serialize(modelOut);
		}
	}
}
