package sjtu.edu.comp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Lab1.FileUtil;
import au.com.bytecode.opencsv.CSVReader;

public class TransWeekToMonth {
	public void read(String filename,LineHandler lineHandler){
		CSVReader reader = null;
		try {
			reader = new CSVReader(new FileReader(filename));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] nextLine;
		try {
			int i = 1;
			while ((nextLine = reader.readNext()) != null) {
					lineHandler.doText(nextLine, i++);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public interface LineHandler {
		public void doText(String[] line, int no);
		
	}
	
	public static void main(String[] args) {
		
		String input = "input//year.csv";  //输入文件
		String output = "output//year_bymonth.csv";  //输出文件
		
		TransWeekToMonth twMonth = new TransWeekToMonth();
		final List<String> weekList = new ArrayList<String>();
		final List<String> monthList = new ArrayList<String>();
		final List<String> results = new ArrayList<String>();
		final List<String> week2month = new ArrayList<String>();
		final List<Double> prop = new ArrayList<Double>();
		
		//转换映射资源文件
		twMonth.read("week2month.csv" ,new LineHandler() {
			@Override
			public void doText(String[] line, int no) {
				// TODO Auto-generated method stub
				if(no != 1)
				{
					week2month.add(line[0].trim() + "-" + line[1].trim());
					
					if(monthList.size() == 0)
						monthList.add(line[1].trim());
					else if(!monthList.get(monthList.size() - 1).equals(line[1].trim()))
						monthList.add(line[1].trim());
					prop.add(Double.parseDouble(line[2]));
				}
			}
		});
		
		String months = ",total";
		
		for(String month : monthList)
			months += ("," + month);
		
		results.add(months);
		
		twMonth.read(input, new LineHandler() {
			
			@Override
			public void doText(String[] line, int no) {
				// TODO Auto-generated method stub
				if(no == 1)
				{
					for(String l : line)
						weekList.add(l);
				}
				else
				{
					Map<String, Double> result = new HashMap<String, Double>(); 
					String s = "\"" + line[0] + "\"," + line[1];
					
					int p = 0;
					for(int i = 2; i< line.length; i++)
					{
						String week = weekList.get(i);
						double count = Double.parseDouble(line[i]);
						
						while(week2month.get(p).startsWith(week))
						{
							String month = week2month.get(p).split("-")[1];
							if(result.containsKey(month))
								result.put(month, (result.get(month) + count * prop.get(p)));
							else
								result.put(month, (count * prop.get(p)));
							p++;
							if(p == week2month.size())
								break;
						}
					}
					
					for(String month : monthList)
						s += ("," + result.get(month));
					
					results.add(s);
				}
			}
		});
		
		for(String s : results)
			System.out.println(s);
		
		FileUtil fu = new FileUtil();
		fu.writeFile(new File(output), results);
		
	}
}
