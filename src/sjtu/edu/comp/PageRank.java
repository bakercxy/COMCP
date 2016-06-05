package sjtu.edu.comp;

import java.util.Arrays;
import java.util.Set;

import edu.sjtu.matrix.Matrix;

public class PageRank {
	
	MineFrequence mf;
	
	
	public static void main(String[] args) {
		MineFrequence mf = new MineFrequence("F:\\cdata\\BySortedList\\noURL.csv");
//		MineFrequence mf = new MineFrequence("TopicsToken.csv");
		mf.buildTermIndex();
		mf.buildTransaction();

		System.out.println(mf.getSetList());
		
		double[][] simMatrix = new double[mf.getTermList().size()][mf.getTermList().size()];  //i行j列表示第i个term对第j个term的贡献度  Ni->Nj
		double[][] weightMatrix = new double[1][mf.getTermList().size()];
		
//		初始化相似矩阵（不等权）
		for(int i = 0; i < mf.getTermList().size(); i++)
			for(int j = 0 ; j < mf.getTermList().size(); j++)
			{
				if(i == j)
					continue;
				int count = 0, jcount = 0;
				for(Set<Integer> set : mf.getSetList())
				{
					if(set.contains(i) && set.contains(j))
						count++;
					if(set.contains(j))
						jcount++;
				}
				
				simMatrix[i][j] = count / (double)Math.log(1 + jcount);
			}
		
//		//初始化相似矩阵（等权）
//		for(int i = 0; i < mf.getTermList().size() -1 ; i++)
//				for(int j = i+1 ; j < mf.getTermList().size(); j++)
//				{
//					if(i == j)
//						break;
//					
//					int count = 0;
//					for(Set<Integer> set : mf.getSetList())
//						if(set.contains(i) && set.contains(j))
//							count++;
//						
//					simMatrix[i][j] = count;
//					simMatrix[j][i] = count;
//				}
		
		//初始化权重矩阵
		for(int i = 0; i < mf.getTermList().size(); i++)
			weightMatrix[0][i] = 1d;
		
		Matrix matrix = new Matrix();
		simMatrix = matrix.matrixNormalizationByRow(simMatrix);
//		matrix.print(simMatrix);
		
		double[][] weightMatrixofCopy;
		do
		{
			weightMatrixofCopy = matrix.deepArrayCopy(weightMatrix);
			weightMatrix = matrix.multiplyMatrix(weightMatrixofCopy, simMatrix, false);
		}while((!matrix.isTerminated(weightMatrixofCopy, weightMatrix, 0.01)));
		
		matrix.print(weightMatrix);
	
		LineEntity[] entities = new LineEntity[mf.getTermList().size()];
		for(int i = 0 ; i < mf.getTermList().size(); i ++)
			entities[i] = new LineEntity(mf.getTermList().get(i), weightMatrix[0][i]);
		
		Arrays.sort(entities);
		
		for(LineEntity le : entities)
			System.out.println(le.getName() + " : " + le.getValue());
	}
	
}
