package sjtu.edu.comp;

public class LineEntity implements Comparable<LineEntity>{
	String name;
	double value;
	
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public double getValue() {
		return value;
	}

	public LineEntity(String name, double value) {
		super();
		this.name = name;
		this.value = value;
	}
	
	public LineEntity() {
	}


	public void setValue(double value) {
		this.value = value;
	}




	@Override
	public int compareTo(LineEntity lineEntity) {
		// TODO Auto-generated method stub
		
		if(lineEntity.getValue() > this.value)
			return 1;
		else if(lineEntity.getValue() < this.value)
			return -1;
		else
			return 0;
	}
}
