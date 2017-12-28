package com.liyuncong.learn.common;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 常用文件操作
 * @author liyuncong
 *
 */
public class FileTools {
	private static Logger logger = LoggerFactory.getLogger(FileTools.class);
	private FileTools(){}
	
	public static List<String> readAllLines(String pathName) throws IOException {
		Path path = Paths.get(pathName);
		return Files.readAllLines(path, Charset.forName(Constants.FROM_CHARSET_NAME));
	}
	
	/**
	 * 把文本行写进文件中，如果文件存在，则先删除
	 * @param lines 文本行的集合
	 * @param pathName 文件路径
	 */
	public static void writeToNewFile(List<String> lines, String pathName) {
		if (lines == null || lines.isEmpty()) {
			logger.warn("lines is {}", lines);
			return;
		}
		Path path = Paths.get(pathName);
		if (path.toFile().exists() && !path.toFile().delete()) {
			logger.error("{} exists and is not deleted successfully", pathName);
			return;
		}
		try {
			Files.write(path, lines, StandardOpenOption.CREATE);
		} catch (IOException e) {
			logger.error("fail to write lines to {} : {}", pathName, e);
		}
	}
	
	public static void appendToFileLn(String s, String pathName) {
		if (s == null) {
			logger.error("s is null");
			return;
		}
		if (pathName == null || pathName.length() == 0) {
			logger.error("pathName is {}", pathName);
			return;
		}
		appendToFile(s + Constants.LINE_SEPARATOR, pathName);
	}
	
	/**
	 * 把s添加到pathName表示的文件的末尾，如果文件不存在，则先创建
	 * @param s 文本
	 * @param pathName 文件名
	 */
	public static void appendToFile(String s, String pathName) {
		if (s == null) {
			logger.error("s is null");
			return;
		}
		if (pathName == null || pathName.length() == 0) {
			logger.error("pathName is {}", pathName);
			return;
		}
		Path path = Paths.get(pathName);
		try {
			if (!path.toFile().exists()) {
				path.toFile().createNewFile();
			}
			Files.write(path, (s).getBytes(Charset.forName(Constants.TO_CHARSET_NAME)), StandardOpenOption.APPEND);
		} catch (IOException e) {
			logger.error("fail to write {} to {} : {}", s, pathName, e);
		}
	}
}
