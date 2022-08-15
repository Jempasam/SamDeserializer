package jempasam.data.modifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import jempasam.data.chunk.DataChunk;
import jempasam.data.chunk.ObjectChunk;
import jempasam.data.chunk.stream.RecursiveChunkStream;
import jempasam.data.chunk.value.StringChunk;
import jempasam.logger.SLogger;

public class TemplateDataModifier implements DataModifier{
	
	
	
	
	private SLogger logger;
	private String keyword;
	private String separator;
	private String separator2;
	private String argcharacter;
	private String argcharacter2;
	private Map<String, String> variables;

	
	
	public TemplateDataModifier(SLogger logger, String keyword, String separator, String separator2, String argcharacter) {
		super();
		this.logger=logger;
		this.keyword=keyword;
		this.separator=Pattern.quote(separator);
		this.separator2=Pattern.quote(separator2);
		this.argcharacter=Pattern.quote(argcharacter);
		this.argcharacter2=argcharacter;
	}
	
	public TemplateDataModifier(SLogger logger) {
		this(logger, "@@", "-","..", "@");
	}
	
	
	
	public void setVariableHolder(Map<String,String> vars) {
		variables=vars;
	}
	
	private HashMap<String, List<DataChunk>> templates;
	
	private List<DataChunk> getTemplateList(String name) {
		List<DataChunk> chunklist=templates.get(name);
		if(chunklist==null) {
			chunklist=new ArrayList<>();
			templates.put(name, chunklist);
		}
		return chunklist;
	}
	
	private void registerTemplate(String name, String nname, DataChunk chunk) {
		List<DataChunk> chunklist=getTemplateList(name);
		chunk.setName(nname);
		chunklist.add(chunk);
	}
	
	private void addTemplate(ObjectChunk parent, String name, String parameters[]) {
		for(DataChunk ndata : useTemplate(name, parameters)) {
			parent.add(ndata);
		}
	}
	
	private List<DataChunk> useTemplate(String name, String parameters[]) {
		List<DataChunk> templates=getTemplateList(name);
		if(templates.size()==0) {
			logger.info("Template \""+name+"\" don't exist.");
			return List.of();
		}
		else {
			List<DataChunk> ret=new ArrayList<>();
			for(DataChunk temp : templates) {
				DataChunk ndata;
				try {
					ndata = temp.clone();
					setParamsInChunk(ndata, parameters);
					ret.add(ndata);
				} catch (CloneNotSupportedException e) {}
			}
			return ret;
		}
	}
	
	private void setParamsInChunk(DataChunk chunk, String params[]) {
		if(chunk instanceof StringChunk) {
			setParamsInValueChunk((StringChunk)chunk, params);
		}
		else {
			setParamsInObjectChunk((ObjectChunk)chunk, params);
		}
	}
	
	private void setParamsInValueChunk(StringChunk chunk, String params[]) {
		chunk.setName(replaceParameters(chunk.getName(), params));
		chunk.setValue(replaceParameters(chunk.getValue(), params));
	}
	
	private void setParamsInObjectChunk(ObjectChunk chunk, String params[]) {
		chunk.setName(replaceParameters(chunk.getName(), params));
		for(DataChunk c : chunk) {
			setParamsInChunk(c, params);
		}
	}
	
	private String replaceParameters(String text, String params[]) {
		for(int i=0; i<params.length; i++) {
			text=text.replaceAll(argcharacter+i+argcharacter, params[i]);
		}
		if(variables!=null)
		for(Map.Entry<String,String> entry : variables.entrySet()) {
			text=text.replaceAll(argcharacter+Pattern.quote(entry.getKey())+argcharacter, entry.getValue());
		}
		return text;
	}
	
	@Override
	public void applyOn(ObjectChunk data) {
		List<Runnable> actions=new ArrayList<>();
		templates=new HashMap<>();
		
		data.recursiveStream().forEach((stream,dc)->{
			ObjectChunk parent=((RecursiveChunkStream)stream).actualParent();
			String value=dc instanceof StringChunk ? ((StringChunk)dc).getValue() : "";
			if(value.startsWith(keyword)) {
				actions.add(()->{
					String params[]=value.substring(keyword.length()).split(separator);
					if(params[0].equals("for")) {
						parent.remove(dc);
						for(int y=2; y<params.length; y++) {
							String parameters[]=params[y].split(separator2);
							addTemplate(parent, params[1], parameters);
						}
					}
					else {
						parent.remove(dc);
						String templatename=params[0];
						params[0]=dc.getName();
						addTemplate(parent, templatename, params);
					}
				});
			}
			if(dc.getName().startsWith(keyword)) {
				actions.add(()->{
					String templateName=dc.getName().substring(keyword.length());
					String splited[]=templateName.split(separator);
					
					registerTemplate(splited[0], (splited.length>1 ? splited[1] : argcharacter2+"0"+argcharacter2), dc);
					parent.remove(dc);
				});
			}
		});
		
		// Do actions
		for(Runnable act : actions)act.run();
	}

}
