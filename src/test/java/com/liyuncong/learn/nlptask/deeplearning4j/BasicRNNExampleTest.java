package com.liyuncong.learn.nlptask.deeplearning4j;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nd4j.linalg.api.buffer.DataBuffer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class BasicRNNExampleTest {

	@Test
	public void test() {
		INDArray input = Nd4j.zeros(2, 3, 3);
		for (int i : input.shape()) {
			System.out.println(i);
		}
		input.putScalar(new int[] {1, 1, 1}, 1);
		DataBuffer dataBuffer = input.data();
		for(double d : dataBuffer.asDouble()) {
			System.out.println(d);
		}
	}

}
