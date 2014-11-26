/*
 * DatabaseQuizManager - Handles any modifications to Quiz
 * 
 * - Each table is a quiz
 * - Each table has the following columns:
 * - > id - unique int identifier (int primary key autoincrement)
 * - > word - self explanatory (text)
 * - > definition - self explanatory (text)
 */

package wordgames.game.util;

import java.util.ArrayList;

import wordgames.game.R;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseQuizManager {

		//private class attributes
		private Context myContext;
		private DatabaseQuiz dbQuiz;
		private SQLiteDatabase db;
		private String table;
		private String dbName;
		
		public DatabaseQuizManager (Context context, String dbName)
		{
			myContext = context;
			this.dbName = dbName;
		}
		
		public DatabaseQuizManager open(boolean isWriteable) throws SQLException
		{
			dbQuiz = new DatabaseQuiz(myContext,dbName);
			if (isWriteable)
				db = dbQuiz.getWritableDatabase();
			else
				db = dbQuiz.getReadableDatabase();
			return this;
		}

		//Returns true if table did not previously exist.
		public boolean addTable(String tableName){
			tableName = "[" + tableName + "]";
			try{
				db.query(tableName, new String[]{"word"}, null, null, null, null, null);
				return false;
			} catch (SQLException e){
				db.execSQL("CREATE TABLE " + tableName + " (" + 
						myContext.getString(R.string.quiz_word_id) + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
						"word" + " TEXT NOT NULL, " + 
						"definition" + " TEXT NOT NULL);");
				return true;
			}
			
		}
		public boolean setTable(String tableName){
			tableName = "[" + tableName + "]";
			try{
				db.query(tableName, new String[]{"word"}, null, null, null, null, null);
				table = tableName;
				return true;
			} catch(SQLException e){
				return false;
			}
			
		}
		
		public void close()
		{
			db = null;
			dbQuiz.close();
			dbQuiz = null;
		}
		
		public long addWord(String word, String desc) 
		{
			ContentValues cVals = new ContentValues();
			cVals.put("word", word);
			cVals.put("definition", desc);
			
			long id = db.insert(table, null, cVals);
			
			
			return id;
		}
		
		public boolean updateWord(String word, String desc, long rowid, boolean userowid)
		{
			boolean b;
			ContentValues cVals = new ContentValues();
			cVals.put("word", word);
			cVals.put("definition", desc);
			
			
			if(userowid == true)
			{
				b = db.update(table, cVals, "_id" + "=" + rowid, null) > 0;
			}
			else
			{
				b = db.update(table, cVals, "word=\'"+word+"\'" , null) > 0;
			}
			
			return b;
		}

		public boolean deleteWord(String word, String desc, long rowid, boolean userowid) {
			boolean b;
			
			if(userowid == true)
			{
				b = db.delete(table, "_id" + "=" + rowid, null) > 0;
			}
			else
			{
			    b = db.delete(table, "word=\'" +word+"\'", null) > 0;
			}
			
			return b;
		}

		public boolean deleteAllWords() {

			boolean b = db.delete(table, null, null) > 0;
			return b;
		}
		
		public boolean dropTable(){
			try{
				db.execSQL("DROP TABLE " + table + " ;");	
			} catch (SQLException e){
				e.printStackTrace();
			}
			
			return true;
		}

		public Cursor getWord(String word) {

			return db.query(true, table, new String[] { "_id", "word", "definition"}, 
					"word=\'" +word+"\'", null, null, null, null, null);
		}
		
		public Cursor getAllWords() {

			return db.query(table, new String[] { "_id", "word", "definition"}, 
					null, null, null, null, null);
			
		}
		
		public long getRowId(Cursor a) {
		return a.getLong(a.getColumnIndex("_id"));	
		
		}
		
		public ArrayList<String> getQuizCount(){
			ArrayList<String> a = new ArrayList<String>();
			Cursor c = db.rawQuery("SELECT * FROM " 
			//+ myContext.getResources().getString(R.string.database_file)
			+ "sqlite_master WHERE type='table' "
			+ "AND name !='android_metadata' "
			+ "AND name !='sqlite_sequence';", null);
			
			if(c.moveToFirst() == false)
				return null;
			else{

				do{	
					a.add(c.getString(c.getColumnIndex("tbl_name")));
				} while (c.moveToNext());

			}
			
			c.close();
			return a;
		}
		
	

}
