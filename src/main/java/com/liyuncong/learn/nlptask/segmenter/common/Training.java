package com.liyuncong.learn.nlptask.segmenter.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liyuncong.learn.common.Constants;

/**
 * 训练语料
 * @author yuncong
 *
 */
public class Training {
	private Logger logger = LoggerFactory.getLogger(Training.class);
	private List<String> sentences;
	
	private Training() {
		String fileName = "sighandata/icwb2-data/training/pku_training.utf8";
		try(InputStream input = new FileInputStream(fileName);) {
			String fileContent = IOUtils.toString(input, Constants.FROM_CHARSET_NAME);
			sentences = TextTools.findAllChineseSentence(fileContent);
		} catch (IOException e) {
			logger.error("{}", e);
		}
	}
	
	private static class SingletonHolder {
		private static Training training = new Training();
		
		private SingletonHolder(){}
	}
	
	public static Training getInstance() {
		return SingletonHolder.training;
	}

	public List<String> getSentences() {
		return sentences;
	}
	
}
