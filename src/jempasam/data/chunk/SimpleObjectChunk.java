package jempasam.data.chunk;

import java.util.ArrayList;
import java.util.List;

import jempasam.samstream.collectors.SamCollectors;

public class SimpleObjectChunk extends AbstractDataChunk implements ObjectChunk {
	
	
	
	private List<DataChunk> datas;
	
	
	
	public SimpleObjectChunk(String name) {
		super(name);
		datas=new ArrayList<>();
	}
	
	
	
	@Override
	public void add(int index, DataChunk e) {
		datas.add(index, e);
	}
	
	@Override
	public void set(int index, DataChunk e) {
		datas.set(index, e);
	}
	
	@Override
	public DataChunk get(int index) throws IndexOutOfBoundsException {
		return datas.get(index);
	}
	
	@Override
	public DataChunk get(String name) {
		return stream().filter(datachunk->datachunk.getName().equals(name)).next().orElseGet(()->null);
	}
	
	@Override
	public int find(DataChunk dc) {
		int i=0;
		for(DataChunk datachunk : this) {
			if(datachunk==dc)return i;
			i++;
		}
		return -1;
	}

	@Override
	public boolean remove(DataChunk e) {
		return datas.remove(e);
	}

	@Override
	public int size() {
		return datas.size();
	}
	
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		sb.append("\"").append(getName()).append("\":(").append(childStream().map(Object::toString).collect(SamCollectors.concatenate(","))).append(")");
		return sb.toString();
	}
	
	@Override
	public SimpleObjectChunk clone() throws CloneNotSupportedException {
		SimpleObjectChunk ret=(SimpleObjectChunk)super.clone();
		ret.datas=new ArrayList<>();
		for(DataChunk data : this) {
			ret.add(data.clone());
		}
		return ret;
	}
}
