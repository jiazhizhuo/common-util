package com.jzz.util.string;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 对比两个字符串最短距离, 错误率比对
 * 
 * 获得 T(总错误字数 步长) = I (插入错误字数) + D (删 除错误字数) + S (替换错误字数)
 * @author JZZ
 *
 */
public class DifferenceString {
	
	public static void main(String[] args) {
		diff("ba","caba"); // <ins>c</ins><ins>a</ins>ba
		diff("bacc","caba");// <sub>bc</sub>a<sub>cb</sub><sub>ca</sub>
		diff("ABCABBA","CBABAC");// <sub>AC</sub>B<del>C</del>AB<sub>BA</sub><sub>AC</sub>
		
	}
	
	
	public static String diff(String src, String dist){
		char[] aA = src.toCharArray();
		char[] bA = dist.toCharArray();
		/**
		 * 	参考： https://cjting.me/misc/how-git-generate-diff/
		 * 	转为有向图；
		 * 		a1	a2	a3	a4	a5
		 * 	b1
		 * 	b2
		 * 	b3
		 * 	b4	
		 * 
		 * 	(x,y) == (x+1, y+1) >> value = 0;			// 对角虚线，含义为“相等”，权重为0；
		 * 	(x,y) -> (x+a, y+b) >> value = max(a,b);     // 对角实现，含义为“替换”，权重为横纵向较大值；
		 */
		
		int step = 0;// 步长
		int maxStep = aA.length + bA.length;// 最大步长
		
		List<Trace> cl = new ArrayList<Trace>();// 存储当前步长记录的数据遍历信息
		
		Trace it = new Trace(0,0,0,"",aA, bA);
		cl.add(it);
		for(step=0; step<maxStep; step++){
			List<Trace> nl = new ArrayList<Trace>();
			for(Trace t : cl){
				if(t.isEnd()){//  最后一个元素未遍历到
					System.out.println(t.toString());
					return t.trace;
				}else{
					nl.addAll(Trace.nextStep(t));
				}
			}
			cl = nl;
		}
		return null;
	}
	
	static class Trace{
		int step = 0;// 步长
		char[] aA;// src
		char[] bA;// dist
		int ai = 0;// 列表 a 的游标；
		int bi = 0;// 列表 b 的游标；
		
		String trace;
		
		public Trace(int step, int ai, int bi, String trace, char[] aA, char[] bA) {
			super();
			this.step = step;
			this.ai = ai;
			this.bi = bi;
			this.trace = trace;
			this.aA = aA;
			this.bA = bA;
		}

		/**
		 * 向 右、下、斜线 方向迈1步；
		 * 
		 * 斜线相等，步长为0；
		 * 斜线不等，步长为1；
		 * 右移1，步长为1；
		 * 下移1，步长为1；
		 * @param t
		 * @return
		 */
		public static List<Trace> nextStep(Trace t){
			List<Trace> tl = new ArrayList<Trace>();
			
			trySlide(t);// 滑动
			
			if( t.aA.length >= t.ai+1 && t.bA.length >= t.bi+1 ){// 斜线 1
				tl.add( trySlide(new Trace(t.step+1, t.ai+1, t.bi+1, t.trace+"<sub>"+ t.aA[t.ai] + t.bA[t.bi]+"</sub>", t.aA, t.bA)) );
			}
			if( t.aA.length >= t.ai+1){// 右移 1
				tl.add( trySlide(new Trace(t.step+1, t.ai+1, t.bi, t.trace+"<del>"+t.aA[t.ai]+"</del>", t.aA, t.bA)) );
			}
			if( t.bA.length >= t.bi+1 ){// 下移 1
				tl.add( trySlide(new Trace(t.step+1, t.ai, t.bi+1, t.trace+"<ins>"+ t.bA[t.bi]+"</ins>", t.aA, t.bA)) );
			}
			return tl;
		}
		
		/**
		 * t 尝试权重不变，向终点滑动；
		 * @param t
		 * @return
		 */
		public static Trace trySlide(Trace t){
			for(int i=0; i<t.aA.length+t.bA.length;i++){
				if(t.ai>=t.aA.length || t.bi>=t.bA.length)
					return t;
				if(t.aA[t.ai] == t.bA[t.bi]){
					t.trace = t.trace + t.aA[t.ai];
					t.ai = t.ai+1;
					t.bi = t.bi+1;
				}else{
					return t;
				}
			}
			return t;
		}
		
		public boolean isEnd(){
			if(ai > aA.length-1 && bi > bA.length-1)
				return true;
			return false;
		}

		@Override
		public String toString() {
			return "Trace [step=" + step + ", aA=" + Arrays.toString(aA) + ", bA=" + Arrays.toString(bA) + ", ai=" + ai
					+ ", bi=" + bi + ", trace=" + trace + "]";
		}
	}
}
