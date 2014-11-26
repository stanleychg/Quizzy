/*
 * CommonFunctions - Stores static functions
 */

package wordgames.game.util;

public class CommonFunctions{

	//Filter out phrases that contains these characters
	//Used to prevent SQLite Database from crashing due to unusual characters
	public static boolean checkChar(String input1){
		boolean isClear = true;

		if(input1.contains("(") || 
				input1.contains(")") ||
				input1.contains("/") ||
				input1.contains("{") ||
				input1.contains("[") ||
				input1.contains("}") ||
				input1.contains("]") ||
				input1.contains("*") ||
				input1.contains("\"") ||
				input1.contains("\\") ||
				input1.contains("?") ||
				input1.contains(":") ||
				input1.contains("<") ||
				input1.contains(">") ||
				input1.contains("|") ||
				input1.contains("\'") 
				){
			isClear = false;	
		}

		return isClear;
	}
}
	
