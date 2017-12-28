package com.liyuncong.learn.nlptask.hanlp;

import java.util.List;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.CRF.CRFSegment;
import com.hankcs.hanlp.seg.common.Term;

public class HanlpNerDemo {
	public static void main(String[] args) {
		String[] testCase = new String[]{
		        "平安产险获评“2017中国保险风云榜”履行社会责任优秀险企",
		        "体育易实力斩获中国SaaS产业大会“年度最佳智慧体育SaaS服务商”",
		        "京东无人机配送体系建好了配送员要失业？刘强东：不会让京东员工丢饭碗",
		        "王先生您已购买8月27日k4588次16车2张无座",
		        "腾讯荣获“全国预算绩效管理工作先进单位”",
		        "帝国主义把我们的地瓜分了"
		};
//		Segment segment = HanLP.newSegment()
//				.enablePlaceRecognize(true);
		HanLP.Config.IOAdapter = null;
		Segment segment = new CRFSegment().enableAllNamedEntityRecognize(true);
		for (String sentence : testCase) {
		    List<Term> termList = segment.seg(sentence);
		    System.out.println(termList);
		}
	}
}
