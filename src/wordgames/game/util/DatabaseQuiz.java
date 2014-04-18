package wordgames.game.util;

import wordgames.game.R;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseQuiz extends SQLiteOpenHelper{
	
	private static final int DATABASE_VERSION = 1;
	String dbName;
	Context context;

	//public static final String QUIZ_TABLE_CREATE = "CREATE TABLE " + name + " (" + QUIZ_ID + 
	//" INTEGER PRIMARY KEY AUTOINCREMENT, " + QUIZ_NAME
	//+ " TEXT NOT NULL, " + QUIZ_DESC + " TEXT NOT NULL" + ");";
	
	public DatabaseQuiz(Context context, String dbName) {
		super(context, dbName, null, DATABASE_VERSION);
		this.context = context;
		this.dbName = dbName;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		//db.execSQL(QUIZ_TABLE_CREATE);
		
		//db.execSQL("DROP TABLE IF EXISTS name");
		
		//List of quizzes
//		db.execSQL("CREATE TABLE " + dbName + " (" + 
//				context.getString(R.string.quiz_id) + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
//				context.getString(R.string.quiz_name) + " TEXT NOT NULL, " +
//				context.getString(R.string.quiz_desc) + " TEXT, " +
//				context.getString(R.string.quiz_size) + " INTEGER DEFAULT -1);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS mealeval");
		onCreate(db);
	}

}
