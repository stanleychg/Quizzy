/*
 * DatabaseQuiz - Subclass of SQLiteOpenHelper
 * 
 * - Implements a SQLite database to store all quizzes
 */

package wordgames.game.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseQuiz extends SQLiteOpenHelper{
	
	private static final int DATABASE_VERSION = 1;
	Context context;

	//public static final String QUIZ_TABLE_CREATE = "CREATE TABLE " + name + " (" + QUIZ_ID + 
	//" INTEGER PRIMARY KEY AUTOINCREMENT, " + QUIZ_NAME
	//+ " TEXT NOT NULL, " + QUIZ_DESC + " TEXT NOT NULL" + ");";
	
	public DatabaseQuiz(Context context, String dbName) {
		super(context, dbName, null, DATABASE_VERSION);
		this.context = context;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		// No init tables needed when database first created
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS mealeval");
		onCreate(db);
	}

}
