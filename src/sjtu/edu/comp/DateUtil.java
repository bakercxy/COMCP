package sjtu.edu.comp;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class DateUtil {
	
	Date Date;
	Calendar calendar;
	
	
	public int getWeek(Date d){
		calendar.setTime(d);
		int week = calendar.get(Calendar.WEEK_OF_YEAR);
		
		if(week == 1 && getMonth(d) == 12)
			week = 53;
		
		return week;
	}
	
	public int getMonth(Date d){
		calendar.setTime(d);
		return calendar.get(Calendar.MONTH) + 1;
	}
	
	public int getYear(Date d){
		calendar.setTime(d);
		return calendar.get(Calendar.YEAR);
	}
	
	public Date nextDate(Date d){
		d.setTime(d.getTime() + 86400000);
		return d;
	}
	
	public Date getDate() {
		return Date;
	}

	public void setDate(Date date) {
		Date = date;
	}
	
	public DateUtil(int year, int startweek){
		String date = "" + year + "-01-01";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.SUNDAY);
		try {
			Date = format.parse(date);
			while(getWeek(Date) != startweek)
				Date = nextDate(Date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		int startYear = 2014, startWeek = 35, endYear = 2016, endWeek = 13;
		List<String> dateList = new ArrayList<String>();
		dateList.add("week, month, prop");
		DateUtil dateUtil = new DateUtil(startYear, startWeek);
		Date date = dateUtil.getDate();
		
		String curWeek = "" + dateUtil.getYear(date) + String.format("%02d", dateUtil.getWeek(date));
		String curMonth = "" + dateUtil.getYear(date) + String.format("%02d", dateUtil.getMonth(date));
		int count = 0;
		
		while((dateUtil.getWeek(date) <= endWeek + 1 && dateUtil.getYear(date) == endYear) || dateUtil.getYear(date) < endYear)
		{
			if(curWeek.equals("" + dateUtil.getYear(date) + String.format("%02d",dateUtil.getWeek(date))) && curMonth.equals("" + dateUtil.getYear(date) + String.format("%02d", dateUtil.getMonth(date))))
				count++;
			else
			{
				dateList.add(curWeek + ", " + curMonth + ", " + (count / 7d));
				count = 1;
				curWeek = "" + dateUtil.getYear(date) + String.format("%02d", dateUtil.getWeek(date));
				curMonth = "" + dateUtil.getYear(date) + String.format("%02d", dateUtil.getMonth(date));
			}
			dateUtil.nextDate(date);
		}
		
		for(String s : dateList)
			System.out.println(s);
	}
	
}
