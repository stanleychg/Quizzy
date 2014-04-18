package wordgames.game.util;

import java.util.Vector;

import android.content.Context;
import android.app.Activity;
import android.database.Cursor;



public class CommonFunctions{

	//Filter out phrases that contains these characters
	//Used to prevent SQLITE from crashing
	public static boolean CheckChar(String input1)
	{
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
	
