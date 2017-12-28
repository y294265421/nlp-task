package com.liyuncong.learn.nlptask.jahmm.hmmsegmenter;

import static org.junit.Assert.*;

import org.junit.Test;

public class HmmSegmenterTest {

	@Test
	public void testSegment() {
		String sentence = "李云聪毕业于云南大学";
		System.out.println(HmmSegmenter.segment(sentence));
	}

}
