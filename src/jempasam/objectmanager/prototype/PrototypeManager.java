package jempasam.objectmanager.prototype;

import jempasam.objectmanager.ObjectManager;

public interface PrototypeManager<Y,T extends Prototype<Y>> extends ObjectManager<T>{
	
	default Y create(String name) {
			T prototype=get(name);
			if(prototype==null)return null;
			else return prototype.clone();
	}
		
}
