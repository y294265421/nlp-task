package com.liyuncong.learn.nlptask.commontask;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.CRF.CRFSegment;
import com.hankcs.hanlp.seg.common.Term;
import com.liyuncong.learn.common.FileTools;
import com.liyuncong.learn.nlptask.segmenter.common.TextTools;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class KeyWordStatistic {
	private static Set<String> stopWords = new HashSet<>();
	
	static {
		try {
			stopWords.addAll(FileTools.readAllLines("D:/program/nlp/organization/hanlp/data/data-for-1.3.2/data/dictionary/stopwords.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private KeyWordStatistic(){}
	
	public static void main(String[] args) throws IOException {
		List<String> chineseSentences = preprocess();
		Map<String, Integer> wordAndCount = stanfordNlpSegment(chineseSentences);

		WordAndCount[] wordAndCounts = new WordAndCount[wordAndCount.size()];
		int index = 0;
		for (Entry<String, Integer> entry : wordAndCount.entrySet()) {
			wordAndCounts[index] = new WordAndCount(entry.getKey(), entry.getValue());
			index++;
		}
		Arrays.sort(wordAndCounts);
		for(int i = 0; i < 300; i++) {
			System.out.println(wordAndCounts[i]);
		}
	}
	
	public static Map<String, Integer> hanlpSegment(List<String> chineseSentences) {
		Map<String, Integer> wordAndCount = new HashMap<>();
		Segment segment = new CRFSegment().enableAllNamedEntityRecognize(true);
		for(String sentence : chineseSentences) {
			List<Term> terms = segment.seg(sentence);
			for (Term term : terms) {
				if (term != null && term.nature != null && (term.nature.startsWith('n') 
						|| term.nature.startsWith('s') || term.nature.startsWith('v')
						|| term.nature.startsWith('a')) && !stopWords.contains(term.word)
						&& term.word.length() > 1) {
					Integer count = wordAndCount.get(term.word);
					if (count == null) {
						count = 0;
					}
					wordAndCount.put(term.word, count + 1);
				}
			}
		}
		return wordAndCount;
	}
	
	private static Map<String, Integer> stanfordNlpSegment(List<String> chineseSentences) {
		Map<String, Integer> wordAndCount = new HashMap<>();
		StanfordCoreNLP corenlp = new StanfordCoreNLP("StanfordCoreNLP-chinese-for-keywords.properties");
		for (String chineseSentence : chineseSentences) {
			Annotation document = new Annotation(chineseSentence);
			corenlp.annotate(document);
			List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
			for (CoreMap sentence : sentences) {
				System.out.println(sentence.get(CoreAnnotations.TextAnnotation.class));
				// traversing the words in the current sentence
				// a CoreLabel is a CoreMap with additional token-specific
				// methods
				for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
					// this is the text of the token
					String word = token.get(CoreAnnotations.TextAnnotation.class);
					if (StringUtils.isBlank(word) || word.length() < 2 || stopWords.contains(word)) {
						continue;
					}
					// this is the POS tag of the token
					Set<String> neededPos = new HashSet<>();
					neededPos.add("NN");
					neededPos.add("NR");
					neededPos.add("VV");
					String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
					if (neededPos.contains(pos)) {
						Integer count = wordAndCount.get(word);
						if (count == null) {
							count = 0;
						}
						wordAndCount.put(word, count + 1);
					}
				}
			}
		}
		return wordAndCount;
	}
	
	private static class WordAndCount implements Comparable<WordAndCount>{
		private String word;
		private Integer count;
		public WordAndCount(String word, Integer count) {
			super();
			this.word = word;
			this.count = count;
		}
		public String getWord() {
			return word;
		}
		public void setWord(String word) {
			this.word = word;
		}
		public Integer getCount() {
			return count;
		}
		public void setCount(Integer count) {
			this.count = count;
		}
		@Override
		public int compareTo(WordAndCount o) {
			return count.compareTo(o.count) * -1;
		}
		@Override
		public String toString() {
			return "word=" + word + ", count=" + count;
		}
		
	}
	
	private static List<String> preprocess() throws IOException {
		List<String> lines = FileTools.readAllLines("keywords\\爱玩钱.txt");
		List<String> chineseSentences = new LinkedList<>();
		for(String line : lines) {
			if (StringUtils.isBlank(line)) {
				continue;
			}
			String cleanLine = line.replaceAll("<[^>]+>", " ");
			if (StringUtils.isBlank(cleanLine)) {
				continue;
			}
			List<String> temp = TextTools.findAllChineseSentence(cleanLine);
			for (String sentence : temp) {
				if (StringUtils.isBlank(sentence)) {
					continue;
				}
				chineseSentences.add(sentence);
			}
		}
		return chineseSentences;
	}
}
