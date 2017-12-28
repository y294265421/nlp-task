package com.liyuncong.learn.nlptask.commontask;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;

public class SentenceParser {
	public static void main(String[] args) {
		String sentence = "The quick brown fox jumps over the lazy dog .";
		opennlpParseSentence(sentence);
	}
	
	public static void opennlpParseSentence(String sentence) {
		InputStream modelIn = null;
		try {
			 modelIn = new FileInputStream("opennlp/model/en-parser-chunking.bin");
			ParserModel model = new ParserModel(modelIn);
			Parser parser = ParserFactory.create(model);
			Parse topParses[] = ParserTool.parseLine(sentence, parser, 1);
			for (Parse parse : topParses) {
				parse.show();
			}
		} catch (IOException e) {
		  e.printStackTrace();
		} finally {
		  if (modelIn != null) {
		    try {
		      modelIn.close();
		    }
		    catch (IOException e) {
		    }
		  }
		}
	}
}
