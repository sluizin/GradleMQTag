/**
 * 
 */
package com.maqiao.was.tag.table;

import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Sunjian
 * @version 1.0
 * @since jdk1.7
 */
public class MQTTConst {
	/**
	 * 换行符
	 */
	static final String ACC_Enter = System.getProperty("line.separator", "\n");
	/**
	 * 保存到session关键字头
	 */
	static final String ACC_SaveSessionKey = "tag";
	static final String ACC_SaveSessionKeyHead = ACC_SaveSessionKey + "_";
	@SuppressWarnings("unused")
	public static void main(String[] args) 
	{
		System.out.println("Hello World!");
		// Java 8之后：
		List<String> features = Arrays.asList("Lambdas", "Default Method", "Stream API", "Date and Time API");
		features.forEach(n -> System.out.println(n));
		 
		// 使用Java 8的方法引用更方便，方法引用由::双冒号操作符标示，
		// 看起来像C++的作用域解析运算符
		features.forEach(System.out::println);	
		List<String> languages = Arrays.asList("Java", "Scala", "C++", "Haskell", "Lisp");
		List<Integer> costBeforeTax = Arrays.asList(100, 200, 300, 400, 500);
		costBeforeTax
		.stream()
		.map((cost) -> cost + 0.12 * cost)
		.forEach(System.out::println);
		
		// 新方法：
		List<Integer> costBeforeTax2 = Arrays.asList(100, 200, 300, 400, 500);
		double bill = costBeforeTax2.stream()
				.map((cost) -> cost + .12*cost)
				.reduce((sum, cost) -> sum + cost)
				.get();
		System.out.println("Total : " + bill);
		
		// 将字符串换成大写并用逗号链接起来
		List<String> G7 = Arrays.asList("USA", "Japan", "France", "Germany", "Italy", "U.K.","Canada");
		String G7Countries = G7
				.stream()
				.map(x -> x.toUpperCase())
				.collect(Collectors.joining("| "));
		System.out.println(G7Countries);
		
		// 用所有不同的数字创建一个正方形列表
		List<Integer> numbers = Arrays.asList(9, 10, 3, 4, 7, 3, 4);
		List<Integer> distinct = numbers.stream().map( i -> i*i).distinct().collect(Collectors.toList());
		System.out.printf("Original List : %s,  Square Without duplicates : %s %n", numbers, distinct);
		
		
		//获取数字的个数、最小值、最大值、总和以及平均值
		List<Integer> primes = Arrays.asList(2, 3, 5, 7, 11, 13, 17, 19, 23, 29);
		IntSummaryStatistics stats = primes
				.stream()
				.mapToInt((x) -> x)
				.summaryStatistics();
		System.out.println("Highest prime number in List : " + stats.getMax());
		System.out.println("Lowest prime number in List : " + stats.getMin());
		System.out.println("Sum of all prime numbers : " + stats.getSum());
		System.out.println("Average of all prime numbers : " + stats.getAverage());
		
	    int sumAll = Stream
	    		.of(1, 2, 3, 4)
	    		.reduce(0,(sum, e) -> sum + e);// 给一个0是用来启动，的，若给-1，结果会是9  
	    System.out.println(sumAll);// 10  
		
		
	}
}
