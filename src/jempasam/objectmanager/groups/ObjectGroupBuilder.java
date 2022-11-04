package jempasam.objectmanager.groups;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jempasam.data.loader.tags.LoadableParameter;
import jempasam.map.MultiMap;
import jempasam.samstream.SamStreams;



public class ObjectGroupBuilder<T> {
	
	
	
	protected GroupObjectManager<T> manager;
	protected MultiMap<String, T> bufferMap;
	@LoadableParameter(name = "inCategory")
	protected String categName="";
	
	
	
	public ObjectGroupBuilder(GroupObjectManager<T> manager) {
		super();
		this.manager = manager;
		this.bufferMap = new MultiMap<>(new HashMap<>(), ArrayList::new);
	}
	
	
	
	// GET FROM INPUT
	@LoadableParameter
	public void get(String name) {
		bufferMap.add(categName, manager.get(name));
	}
	
	@LoadableParameter
	public void getAllFromNames(String separator) {
		for(Map.Entry<String, T> entry : manager.stream().filter(e->!e.getKey().startsWith("_"))) {
			String[] categories=entry.getKey().split("\\.");
			for(String c : categories)bufferMap.add(c, entry.getValue());
		}
	}
	
	// FROM
	@LoadableParameter
	public void fromCategory(String name) {
		for(T elem : manager.ofCategories(name))bufferMap.add(name, elem);
	}
	
	@LoadableParameter
	public void fromAllCategories(boolean IGNORED) {
		for(Map.Entry<String,Collection<T>> entry : manager.categories()) for(T elem : entry.getValue()) bufferMap.add(entry.getKey(), elem);
	}
	
	// TO
	@LoadableParameter
	public void toCategory(String name) {
		for(Collection<T> list : bufferMap.values()) for(T elem : list) manager.addToCategory(elem, name);
	}
	
	@LoadableParameter
	public void toAllCategories(boolean IGNORED) {
		for(Map.Entry<String,Collection<T>> entry : bufferMap.entrySet())
			for(T elem : entry.getValue())
					manager.addToCategory(elem,entry.getKey());
	}
	
	// REMOVE
	@LoadableParameter
	public void removeSizeLessThan(int min) {
		List<Map.Entry<String, Collection<T>>> old=new ArrayList<>(bufferMap.entrySet());
		old.stream().filter(e->e.getValue().size()<min).forEach(e->bufferMap.remove(e.getKey()));
	}
	
	@LoadableParameter
	public void remove(String name) {
		bufferMap.remove(name);
	}
	
	@LoadableParameter
	public void empty(boolean IGNORED) {
		bufferMap.clear();
	}
	
	// MIXED
	@LoadableParameter
	public void createMixeds(int maxmix) {
		String[] categories=bufferMap.keySet().toArray(new String[0]);
		List<List<String>> mixed_categories=new ArrayList<>();
		makeMix2(categories, mixed_categories, maxmix);
		mixed_categories.removeIf(e->e.size()<=1);
		for(List<String> categ : mixed_categories) {
			List<T> capacity=new ArrayList<>(bufferMap.get(categ.get(0)));
			int sizemin=capacity.size();
			for(int i=1; i<categ.size(); i++) {
				Collection<T> retained=bufferMap.get(categ.get(i));
				capacity.retainAll(retained);
				sizemin=Math.min(sizemin, retained.size());
			}
			if(capacity.size()>0 && capacity.size()<sizemin) {
				StringBuilder sb=new StringBuilder();
				for(String str : categ)sb.append(str).append("-");
				if(sb.length()>0)sb.setLength(sb.length()-1);
				bufferMap.put(sb.toString(), capacity);
			}
		}
	}
	
	
	
	private static void makeMix2(String[] words, List<List<String>> target, int maxmix){
		if(maxmix==1) SamStreams.create(words).forEach(str->target.add(Collections.singletonList(str)));
		else makeMixOfSize(words, 0, target, new ArrayList<>(), maxmix);
	}
	
	private static void makeMixOfSize(String[] words, int start, List<List<String>> target, List<String> elem, int mixsize){
		for(int i=start; i<words.length; i++) {
			List<String> a=new ArrayList<>(elem);
			a.add(words[i]);
			target.add(new ArrayList<>(a));
			if(mixsize>1)makeMixOfSize(words, i+1, target, a, Math.min(words.length-i, mixsize-1));
		}
	}
	
}
