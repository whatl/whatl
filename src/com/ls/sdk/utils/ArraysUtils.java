package com.ls.sdk.utils;

public class ArraysUtils {
	/** * 判断数组中的最大数 * @param args * @return */
	public static int getMaxNum(int args[]) {
		int max = 0;
		for (int i = 0; i < args.length; i++) {
			if (args[i] > args[max])
				max = i;
		}
		return args[max];
	}

	/** * 判断数组中的最小数 * @param args * @return */
	public static int getMinNum(int args[]) {
		int min = 0;
		for (int i = 0; i < args.length; i++) {
			if (args[i] < args[min])
				min = i;
		}
		return args[min];
	}

}
