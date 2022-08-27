package jempasam.data.modifier;

import jempasam.data.chunk.ObjectChunk;
import jempasam.data.chunk.value.StringChunk;

public class ExtractFromNameModifier implements DataModifier{
	
	
	
	private String parameters[];
	private String separator;
	
	
	public ExtractFromNameModifier(String separator, String[] parameters) {
		super();
		this.parameters = parameters;
		this.separator = separator;
	}
	
	
	
	@Override
	public void applyOn(ObjectChunk data) {
		data.recursiveStream().objects().forEach(chunk->{
			String splited[]=chunk.getName().split(separator);
			if(splited.length>1) {
				chunk.setName(splited[0]);
				int max=Math.min(splited.length-1, parameters.length);
				for(int i=0; i<max; i++)chunk.add(new StringChunk(parameters[i], splited[i+1]));
			}
		});
	}

}
