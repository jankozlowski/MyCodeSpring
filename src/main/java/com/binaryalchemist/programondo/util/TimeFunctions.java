package com.binaryalchemist.programondo.util;

public class TimeFunctions {

	public static String addTimeString(String time1, String time2) {

		String[] timeParts1 = time1.split(":");
		String[] timeParts2 = time2.split(":");

		String result = (Integer.valueOf(timeParts1[0]) + Integer.valueOf(timeParts2[0])) + ":"
				+ (Integer.valueOf(timeParts1[1]) + Integer.valueOf(timeParts2[1])) + ":"
				+ (Integer.valueOf(timeParts1[2]) + Integer.valueOf(timeParts2[2])) + ":"
				+ (Integer.valueOf(timeParts1[3]) + Integer.valueOf(timeParts2[3]));
		return result;
	}

	public static long timeToSeconds(String time) {

		String[] timeParts = time.split(":");

		long sumSeconds = 0;

		sumSeconds = (int) Math.round(Double.valueOf(timeParts[3]) / 1000);
		sumSeconds += Integer.valueOf(timeParts[2]);
		sumSeconds += Integer.valueOf(timeParts[1]) * 60;
		sumSeconds += Integer.valueOf(timeParts[0]) * 3600;

		return sumSeconds;
	}

	public static String secondsToTime(long seconds2) {

		long days = (int) Math.floor(seconds2 / 86400);
		long hours = (seconds2 % 86400) / 3600;
		long minutes = (seconds2 % 3600) / 60;
		long seconds = seconds2 % 60;

		String result = days + ":" + String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":"
				+ String.format("%02d", seconds);
		return result;

	}

}
