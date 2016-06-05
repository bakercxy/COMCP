package sjtu.edu.comp;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Lab1.FileUtil;
import au.com.bytecode.opencsv.CSVReader;

public class MineFrequence {
	
	public List<String> termList;
	Map<String, Integer> termNametoIndex;
	List<String> transList = new ArrayList<String>();
	List<Set<Integer>> setList = new ArrayList<Set<Integer>>();
	String rawFile;
	DecimalFormat df = new DecimalFormat("#0.00");
	
//	String[] unusedwords = {"big_data","ieee","paper","confer","2015","2016","transact"};
	String[] unusedwords = {};
	Set<String> unusedSet = new HashSet<String>();
	
	public void read(String filename, LineHandler lineHandler){
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
	
	public void initUnusedSet(){
		for(String s : unusedwords)
			unusedSet.add(s);
	}
	
	
	public MineFrequence(String rawFile) {
		termList = new ArrayList<String>();
		termNametoIndex = new HashMap<String, Integer>();
		initUnusedSet();
		this.rawFile = rawFile;
	}
	
	public void buildTermIndex(){
		read(rawFile, new LineHandler() {
			
			@Override
			public void doText(String[] line, int no) {
				// TODO Auto-generated method stub
				
				if(no >=5)
				{
					if(!checkUseful(line[0]))
						return ;
					
					String[] terms = wordSplit(line[0]);
					for(String term : terms){
						if(!termNametoIndex.containsKey(term) && !unusedSet.contains(term))
						{
							termNametoIndex.put(term, termList.size());
							termList.add(term);
						}
					}
				}
			}
		});
	}
	
	public String[] wordSplit(String query){
		String[] terms = query.split("\\s");
		return terms;
	}
	
	public boolean checkUseful(String query){
		boolean useful = false;
		String[] terms = wordSplit(query);
		for(String term : terms)
			if(!unusedSet.contains(term))
			{
				useful = true;
				break;
			}
				
		return useful;
	}
	
	public void buildTransaction(){
		read(rawFile, new LineHandler() {
			@Override
			public void doText(String[] line, int no) {
				if(no >=5)
				{
					if(!checkUseful(line[0]))
						return ;
					String[] terms = wordSplit(line[0]);
					int occur = Integer.parseInt(line[1]);
					
					Set<Integer> termSet = new HashSet<Integer>();
					for(int i = 0 ; i < occur; i ++)
					{
						String trans = "";
						for(String term : terms){
							if(termNametoIndex.containsKey(term))
							{
								trans = trans + termNametoIndex.get(term) + " ";
								termSet.add(termNametoIndex.get(term));
							}
						}
						transList.add(trans.trim());
						setList.add(termSet);
					}
				}
			}
		});
	}
	
	public void saveTransaction(){
		FileUtil fu = new FileUtil();
		fu.writeFile(new File("trans.dat"), transList);
	}
	
	public List<String> getTermList() {
		return termList;
	}

	public void setTermList(List<String> termList) {
		this.termList = termList;
	}

	public Map<String, Integer> getTermNametoIndex() {
		return termNametoIndex;
	}

	public void setTermNametoIndex(Map<String, Integer> termNametoIndex) {
		this.termNametoIndex = termNametoIndex;
	}

	public List<String> getTransList() {
		return transList;
	}

	public void setTransList(List<String> transList) {
		this.transList = transList;
	}

	public List<Set<Integer>> getSetList() {
		return setList;
	}

	public void setSetList(List<Set<Integer>> setList) {
		this.setList = setList;
	}

	public List<String> transToTerm(int[] set){
		List<String> l = new ArrayList<String>();
		for(int i : set)
			l.add(termList.get(i));
		
		return l;
	}
	
	public List<String> getAssociation(Apriori apriori, double conf){
		
		List<String> result = new ArrayList<String>();
		List<LineEntity> lineEntities = new ArrayList<LineEntity>();
		
    	List<int[]> fsets = apriori.getFrequentSet();
    	List<Double> ssets = apriori.getSupport();
    	
    	result.add("###Association Rules:");
    	System.out.println("###Association Rules:");
    	
    	for(int i = 0; i < fsets.size() - 1; i++)
    		for(int j = i + 1; j < fsets.size(); j++)
    		{
    			if(isSubset(fsets.get(i), fsets.get(j)) && ssets.get(j) / ssets.get(i) >= conf)
    			{
    				List<String> ft = transToTerm(fsets.get(j));
    				ft.removeAll(transToTerm(fsets.get(i)));
    				
    				LineEntity lineEntity = new LineEntity(transToTerm(fsets.get(i)) + " -> " + ft, ssets.get(j) / ssets.get(i));
    				lineEntities.add(lineEntity);
    				
    			}
    		}
    	
    	Collections.sort(lineEntities); 
    	
    	for(LineEntity le : lineEntities)
    	{
    		System.out.println(le.getName() + ": " + df.format(le.getValue() * 100d) + "%");
			result.add(le.getName() + ": " + df.format(le.getValue() * 100d) + "%");
    	}
    	
    	return result;
    }
	
	public List<String> getFrequentPattern(Apriori apriori){
		
		List<String> result = new ArrayList<String>();
		List<LineEntity> lineEntities = new ArrayList<LineEntity>();
		
		result.add("###Frequent Patterns:");
		System.out.println("###Frequent Patterns:");
		
		int size = apriori.getFrequentSet().size();
		for(int i = 0; i < size; i ++ )
		{
			LineEntity lineEntity = new LineEntity( ""+transToTerm(apriori.getFrequentSet().get(i)), apriori.getSupport().get(i));
			lineEntities.add(lineEntity);
		}
		
		Collections.sort(lineEntities); 
    	
    	for(LineEntity le : lineEntities)
    	{
    		System.out.println(le.getName() + ": " + df.format(le.getValue() * 100d) + "%");
			result.add(le.getName() + ": " + df.format(le.getValue() * 100d) + "%");
    	}
		
		return result;
	}
    
    //判断set1是不是set2的子集
    public boolean isSubset(int[] set1, int[] set2){
    	List<Integer> list1  = new ArrayList<Integer>(); 
    	List<Integer> list2  = new ArrayList<Integer>();
    	
    	for(int a : set1)
    		list1.add(a);
    	
    	for(int b : set2)
    		list2.add(b);
    	
    	list1.removeAll(list2);
    	if(list1.size() == 0)
    		return true;
    	else
    		return false;
    }
    
	public static void main(String[] args) {
		
		double support = 0.005;
		double confidence = 0.2;
		
		MineFrequence mf = new MineFrequence("F:\\cdata\\BySortedListwithStop\\noURL.csv");
		mf.buildTermIndex();
		mf.saveTransaction();
		
		List<String> l = new ArrayList<String>();
		
		l.add("sup: " + support + " ," + "conf: " + confidence);
		System.out.println("=============================");
		l.add("=============================");
		
		try {
			Apriori apriori = new Apriori(new String[]{"trans.dat", "" + support});
			l.addAll(mf.getFrequentPattern(apriori));
			System.out.println("=============================");
			l.add("=============================");
			l.addAll(mf.getAssociation(apriori, confidence));
			
			FileUtil fu = new FileUtil();
			fu.writeFile(new File("result.dat"), l);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public interface LineHandler {
		public void doText(String[] line, int no);
		
	}
	
}
