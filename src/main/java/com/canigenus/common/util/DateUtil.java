package com.canigenus.common.util;

import java.text.DateFormat;
import java.util.Date;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

@ApplicationScoped
@Named
public class DateUtil {

	public static String getFormattedDate(Date date) {

		return DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL)
				.format(date);

	}

	public long getDateDifference(Date d1, Date d2) {
		return d2.getTime() - d1.getTime();

	}

	public String getInstanceInDaysHoursMinutesSeconds(long diff) {
		long diffSeconds = diff / 1000 % 60;
		long diffMinutes = diff / (60 * 1000) % 60;
		long diffHours = diff / (60 * 60 * 1000) % 24;
		long diffDays = diff / (24 * 60 * 60 * 1000);
		String str = "";
		if (diffDays != 0) {
			str = str + diffDays + " days, ";
		}
		str = str + diffHours + " hours, ";
		str = str + diffMinutes + " minutes, ";
		str = str + diffSeconds + " seconds.";
		return str;
	}

}
