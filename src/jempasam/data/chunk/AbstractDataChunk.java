package jempasam.data.chunk;

public abstract class AbstractDataChunk implements DataChunk{
	
	
	
	private String name;
	
	
	
	public AbstractDataChunk(String name) {
		super();
		this.name = name;
	}
	
	
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name=name;
	}
	
	@Override
	public AbstractDataChunk clone() throws CloneNotSupportedException {
		AbstractDataChunk clone=(AbstractDataChunk)super.clone();
		clone.setName(name);
		return clone;
	}

}
