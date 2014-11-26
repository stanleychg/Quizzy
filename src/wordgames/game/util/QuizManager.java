

package wordgames.game.util;

import java.util.ArrayList;

public class QuizManager extends ArrayList<Quiz>{

	private static final long serialVersionUID = 1L;

	public QuizManager(){
		
	}
	
	public void addQuiz(String name){
		add(new Quiz(name));
	}
	
	public void addQuiz(String name, String desc, int id){
		add(new Quiz(name, desc, id));
	}
	
	
	public Quiz getQuiz(String name){
		for(int x = 0; x < size(); x ++){
			if(get(x).getName().equals(name))
				return get(x);
		}
		
		return null;
	}
	
	public void saveQuiz(){
		
	}
}
