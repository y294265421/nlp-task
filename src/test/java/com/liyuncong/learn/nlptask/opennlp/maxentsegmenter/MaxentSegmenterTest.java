package com.liyuncong.learn.nlptask.opennlp.maxentsegmenter;

import static org.junit.Assert.*;

import org.junit.Test;

public class MaxentSegmenterTest {

	@Test
	public void testSegment() {
		System.out.println(MaxentSegmenter.segment("李云聪毕业于云南大学"));
		System.out.println(MaxentSegmenter.segment("迈向充满希望的新世纪"));
	}

}
