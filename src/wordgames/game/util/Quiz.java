/*
 * Quiz - An ArrayList of WordPairs
 */

package wordgames.game.util;

import java.util.ArrayList;

public class Quiz extends ArrayList<WordPair>{
	
	private static final long serialVersionUID = 1L;

	public enum Filter{
		NAME_ONLY,
		DESC_ONLY,
		NAME_AND_DESC
	};
	
	//Quiz Name
	private String name;
	
	//Quiz Description
	private String desc;
	private int id;
	
	public Quiz(String name){
		this.name = name;
	}
	
	public Quiz (String name, String desc, int id){
		this.name = name;
		this.desc = desc;
		this.id = id;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;  
	}
	
	public String getDesc(){
		return desc;
	}
	
	public void setDesc(String desc){
		this.desc = desc;  
	}
	
	public int getSize(){
		return size();
	}
	
	public int getId(){
		return id;
	}
	
	public WordPair getWordPair(int pos){
		return get(pos);
	}
	
	public void addWordPair(String term, String desc){
		add(new WordPair(term,desc));
	}
	
	public void addWordPair(WordPair wp){
		add(wp);
	}
	
	//Either term or definition can be null
	public WordPair getWordPair(String term, String desc, Filter type){
		for(int x = 0; x < size(); x ++){
			WordPair wp = get(x);
			switch(type){
			case NAME_ONLY:
				if(wp.word.equals(term))
					return get(x);
				break;
				
			case DESC_ONLY:
				if(wp.definition.equals(desc))
					return get(x);
				break;
				
			case NAME_AND_DESC:
				if(wp.word.equals(term) && wp.definition.equals(desc))
					return get(x);
				break;
				
			}

		}
		
		return null;
	}
	
	public boolean removeWordPair(WordPair wp){
		if(remove(wp))
			return true;
		else
			return false;
	}
	
	
	
}
