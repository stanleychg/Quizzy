/*
 * QuizFront - Main Menu
 * 
 * User can do the following on this screen:
 * -Create a new quiz
 * -Modify/Delete a quiz
 * -Play with a quiz
 * -Import/Export quizzes
 */

package wordgames.game;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import wordgames.game.util.CommonFunctions;
import wordgames.game.util.QuizDatabaseManager;
import wordgames.game.util.Quiz;
import wordgames.game.util.QuizListAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class QuizFront extends Activity{

	//Dialog Sizes
	final float HEADER_SIZE = 30.0f;
	final float NAME_SIZE = 30.0f;
	final float TITLE_SIZE = 25.0f;
	
	//Dialog Titles/Descriptions
	final String EDIT_TITLE = "Edit Quiz";
	final String PLAY_TITLE = "Use Quiz";
	final String DELETE_TITLE = "Delete Quiz";
	final String EXPORT_TITLE = "Export Quiz";
	final String IMPORT_TITLE = "Import Quiz";
	final String TOGGLE_TEXT = "Toggle All";
	final String IMPORT_LIST_TEXT = "Select Quizzes to Import";
	final String ADD_QUIZ_TITLE = "Add New Quiz";
	final String ADD_QUIZ_HINT = "Gen Psych 101";
	final String QUIZ_OPTIONS_TITLE = "What would you like to do?";
	
	//UI
	GridView quizList;
	QuizListAdapter qlva;
	
	ArrayList<Quiz> qm;
	QuizDatabaseManager data;
	
	Intent i;

	//Initialization
	public void onCreate(Bundle saved){
		super.onCreate(saved);
		setContentView(R.layout.quiz_front);
		
		//Set Click Listener to GridView of Quizzes
		quizList = (GridView)findViewById(R.id.frontQuizList);
	    quizList.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				// TODO Auto-generated method stub
				System.out.println("POSITION: " + position);
				dialogOptions(view.getContext(),position);
			}
	    	 
	    });
	    
	    //Set Hold Listener to GridView of Quizzes
	    quizList.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				return false;
			}
	    	
	    });
		
	    //List of quizzes updated in onResume
	}
	
	//Updates list of quizzes and animates the views
	//Called when:
	//a) Device sleeps and resumes
	//b) User exits QuizMaker
	protected void onResume() {
		super.onResume();
		updateList();
		
	    //Animate Views
	    AnimationSet as = (AnimationSet)AnimationUtils.loadAnimation(this, R.animator.fade_in);
	    quizList.startAnimation(as);
	}
	
	//Inflate ActionBar
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.quiz_front_action, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	//ActionBar Event Listener
	public boolean onOptionsItemSelected(MenuItem item){
		FileChannel src;
		FileChannel dst;
		
		switch(item.getItemId()){
			case R.id.menu_add:
				dialogCreate(QuizFront.this);
				return true;
			case R.id.menu_import:
				//Import Quizzes
    			try {
    				//Store a temporary copy of the databases in the SD card
					String phoneDbPath = getResources().getString(R.string.internal_directory_folder)
							+ "//"
							+ getResources().getString(R.string.database_file)
							+ "TEMP";
					String sdDBPath = getResources().getString(R.string.sd_directory_folder)
							+ "//"
							+ getResources().getString(R.string.database_file)
							+ ".db";
					File sdDB = new File(Environment.getExternalStorageDirectory(), sdDBPath);
					File phoneDb = new File(Environment.getDataDirectory(),phoneDbPath);
					
    				//Check state of sd
    				if (sdDB.canRead()) {
    					//sd card can write. Set this settings through Android Manifest
    					//<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"></uses-permission>
    					System.out.println("Can Read from SD...");

    					//Open file streams
    					src = new FileInputStream(sdDB).getChannel();
    					dst = new FileOutputStream(phoneDb).getChannel();
    			
    					dst.transferFrom(src, 0, src.size());
    					
    					QuizDatabaseManager temp = new QuizDatabaseManager(
    							QuizFront.this,
    							getResources().getString(R.string.database_file) + "TEMP");
    					temp.open(false);
    					
    					//Tracks quizzes in sd card
    					List<QuizImport> tempList = new ArrayList<QuizImport>();
    					for(String s: temp.getQuizCount()){
        					tempList.add(new QuizImport(s));
        				}
    					temp.close();
    					
    					showImportList(QuizFront.this,tempList);
    					
            			//Ensure that all opened streams are closed
            			src.close(); src = null;
            			dst.close(); dst = null;

        		   }
        		} catch (Exception e) {
        			//Exception hit. Print error message
        			System.out.println("Error:" + e.getMessage());
        			Toast.makeText(QuizFront.this, "ERROR 0: Quizzes failed to import", Toast.LENGTH_LONG).show();
        		} 

				return true;
			case R.id.menu_export:
				//Export Quizzes to SD Card
        		try {
        			//Sets the directories.
        			//sd = sd card directory
        			//data = data directory
        			File sd = Environment.getExternalStorageDirectory();
        			File data = Environment.getDataDirectory();
        		  
        			//Set folder path
        			String folder = getResources().getString(R.string.sd_directory_folder);
        		  
        			//Set up folder directory
        			File dir = new File(sd, folder);
        	      
        	      
        			if(!dir.exists()){
	        				//If it doesn't exist, make it.
	        	    	if(dir.mkdirs()) System.out.println("Folder does not exist. Folder made");
	        	    	else System.out.println("Folder does not exist. Folder failed to materialize");
        			}
        			else System.out.println("Folder exists...");
        	      
        			System.out.println("Attempting Backup...");
        		   
        			//Check state of sd
        			//System.out.println(sd.toString());
        			if (sd.canWrite()) {
        				//sd card can write. Set this settings through Android Manifest
        				//<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"></uses-permission>
        				System.out.println("Can write into SD...");
        			 
        				//Set database path
        				String phoneDBPath;
        				//Set backup database path
        				String sdDBPath;
        			  
        				//Set up directories
        				File phoneDB;
        				File sdDB;

    					//Transfer SQLite databases
    					//Time t = new Time();
    					//t.setToNow();
    					
    					phoneDBPath = getResources().getString(R.string.internal_directory_folder)
    						+ "//" 
    						+ getResources().getString(R.string.database_file);
    					
    					sdDBPath = getResources().getString(R.string.sd_directory_folder) 
    						+ "//" 
    						+ getResources().getString(R.string.database_file)
    					//	+ t.month/10 + t.month%10 + t.monthDay/10 + t.monthDay%10 + t.year
    						+ ".db";
    					
    					phoneDB = new File(data, phoneDBPath);
    					sdDB = new File(sd, sdDBPath);
    					
    					if (phoneDB.exists()) {
    						System.out.println("Database Exists...");
    						src = new FileInputStream(phoneDB).getChannel();
    						dst = new FileOutputStream(sdDB).getChannel();

    						dst.transferFrom(src, 0, src.size());

    						src.close();
    						dst.close();
    						System.out.println("Quizzes backed up!");
    					}  
    					
    					Toast.makeText(QuizFront.this, "Quizzes successfully exported!", Toast.LENGTH_SHORT).show();

        		   }
        		} catch (Exception e) {
        		   // exception
        			System.out.println("Crash");
        			Toast.makeText(QuizFront.this, "ERROR 0: Quizzes failed to export", Toast.LENGTH_LONG).show();
        		}

            
				return true;
			default:
				return super.onOptionsItemSelected(item);	
		}
	}
	
	//Add Quiz Dialog
	private void dialogCreate(final Context mContext) {
        final Dialog dialog = new Dialog(mContext);
        dialog.setTitle(ADD_QUIZ_TITLE);

        //Initialize Dialog
        LinearLayout ll = new LinearLayout(mContext);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setLayoutParams(new LayoutParams(
        	LayoutParams.MATCH_PARENT,
        	LayoutParams.WRAP_CONTENT));
        
        LinearLayout llBot = new LinearLayout(mContext);
        llBot.setOrientation(LinearLayout.HORIZONTAL);
        llBot.setWeightSum(1.0f);
        
        final EditText name = new EditText(mContext);
        name.setTextSize(NAME_SIZE);
        name.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        name.setLayoutParams(new LinearLayout.LayoutParams(
        	LayoutParams.MATCH_PARENT,
        	LayoutParams.WRAP_CONTENT,
        	0.25f));
        
        name.setHint(ADD_QUIZ_HINT);
        llBot.addView(name);
        
        ImageButton set = new ImageButton(mContext);
        set.setImageResource(R.drawable.green_check);
        set.setScaleType(ScaleType.CENTER_INSIDE);
        set.setAdjustViewBounds(true);
        set.setLayoutParams(new LinearLayout.LayoutParams(
        	LayoutParams.MATCH_PARENT,
        	LayoutParams.MATCH_PARENT,
        	0.75f));
        
        //Set event listener to create new quiz
        set.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	String s = name.getText().toString();
            	if(CommonFunctions.checkChar(s) == true && !s.isEmpty()){
            		Intent i = new Intent(mContext, QuizMaker.class);
                	i.putExtra("name", s);
                	
                	mContext.startActivity(i);
                	dialog.dismiss();
                	hideIntro();
            	} else if(!s.isEmpty()){
            		//Invalid characters
            		Toast.makeText(v.getContext(), "Invalid name! Please take out all spaces!", Toast.LENGTH_SHORT).show();
            	} else {
            		//Name is empty
            		Toast.makeText(v.getContext(), "Invalid name! Please insert a name!", Toast.LENGTH_SHORT).show();
            	}
      	
            }
        });    
        
        set.setPadding(5, 5, 5, 5);
        llBot.addView(set);
        
        ll.addView(llBot);
        dialog.setContentView(ll);        
        dialog.show();        
    }

	//Quiz Options Dialog
	private void dialogOptions(final Context mContext, final int pos) {
        final Dialog dialog = new Dialog(mContext);
        dialog.setTitle(QUIZ_OPTIONS_TITLE);
        final Quiz q = qm.get(pos);

        //Initialize Dialog
        LinearLayout ll = new LinearLayout(mContext);
        LinearLayout ll2 = new LinearLayout(mContext);

        ll.setOrientation(LinearLayout.VERTICAL);
        ll2.setOrientation(LinearLayout.HORIZONTAL);
      
        LinearLayout llEdit = new LinearLayout(mContext);
        llEdit.setOrientation(LinearLayout.VERTICAL);
        llEdit.setLayoutParams(new LinearLayout.LayoutParams(
        	LayoutParams.MATCH_PARENT,
        	LayoutParams.MATCH_PARENT,
       		1.0f));
        ImageButton edit = new ImageButton(mContext);
        edit.setImageResource(R.drawable.hammer_small);
        edit.setAdjustViewBounds(true);
        edit.setScaleType(ScaleType.CENTER_INSIDE);
        edit.setLayoutParams(new LinearLayout.LayoutParams(
        	LayoutParams.MATCH_PARENT,
        	LayoutParams.MATCH_PARENT,
       		1.0f));
        
        //Set event listener to edit existing quiz
        edit.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Intent i = new Intent(mContext, QuizMaker.class);
            	i.putExtra("name", q.getName());
                mContext.startActivity(i);
                dialog.dismiss();
            }
        });        
        edit.setPadding(5, 5, 5, 5);
        llEdit.addView(edit);
        TextView editTitle = new TextView(mContext);
        editTitle.setText(EDIT_TITLE);
        editTitle.setGravity(Gravity.CENTER_HORIZONTAL);
        editTitle.setTextSize(TITLE_SIZE);
        llEdit.addView(editTitle);
        ll2.addView(llEdit);

        LinearLayout llPlay = new LinearLayout(mContext);
        llPlay.setOrientation(LinearLayout.VERTICAL);
        llPlay.setLayoutParams(new LinearLayout.LayoutParams(
        	LayoutParams.MATCH_PARENT,
        	LayoutParams.MATCH_PARENT,
       		1.0f));
        
        ImageButton play = new ImageButton(mContext);
        play.setImageResource(R.drawable.puzzle_small);
        play.setScaleType(ScaleType.CENTER_INSIDE);
        play.setAdjustViewBounds(true);
        play.setLayoutParams(new LinearLayout.LayoutParams(
       		LayoutParams.MATCH_PARENT,
       		LayoutParams.MATCH_PARENT,
       		1.0f));
        
        //Set event listener to use quiz
        play.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Intent i = new Intent(mContext, QuizGame.class);
            	i.putExtra("name", q.getName());
                mContext.startActivity(i);
                dialog.dismiss();
            }
        });
        
        play.setPadding(5, 5, 5, 5);
        llPlay.addView(play);
        TextView playTitle = new TextView(mContext);
        playTitle.setText(PLAY_TITLE);
        playTitle.setGravity(Gravity.CENTER_HORIZONTAL);
        playTitle.setTextSize(TITLE_SIZE);
        llPlay.addView(playTitle);
        ll2.addView(llPlay);

        LinearLayout llDelete = new LinearLayout(mContext);
        llDelete.setOrientation(LinearLayout.VERTICAL);
        llDelete.setLayoutParams(new LinearLayout.LayoutParams(
        	LayoutParams.MATCH_PARENT,
        	LayoutParams.MATCH_PARENT,
       		1.0f));
        
        ImageButton delete = new ImageButton(mContext);
        delete.setImageResource(R.drawable.trash_can_small);
        delete.setScaleType(ScaleType.CENTER_INSIDE);
        delete.setAdjustViewBounds(true);
        delete.setLayoutParams(new LinearLayout.LayoutParams(
        	LayoutParams.MATCH_PARENT,
        	LayoutParams.MATCH_PARENT,
       		1.0f));
        //Set event listener to delete quiz
        delete.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	//Remove from list
            	String tableName = qm.get(pos).getName();
                qm.remove(pos);
                qlva.notifyDataSetChanged();
                
                //Delete quiz/table
				data = new QuizDatabaseManager(
					mContext,
					getResources().getString(R.string.database_file));
				data.open(true);
				data.setTable(tableName);
				data.dropTable();
				data.close();
                
                Toast.makeText(mContext, q.getName() + " removed from list", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });  
        
        delete.setPadding(5, 5, 5, 5);
        llDelete.addView(delete);
        TextView deleteTitle = new TextView(mContext);
        deleteTitle.setText(DELETE_TITLE);
        deleteTitle.setGravity(Gravity.CENTER_HORIZONTAL);
        deleteTitle.setTextSize(TITLE_SIZE);
        llDelete.addView(deleteTitle);
        ll2.addView(llDelete);

        ll.addView(ll2);
        dialog.setContentView(ll);        
        dialog.show();        
    }
	
//	//Create dialog to delete quiz
//	private void dialogDelete(final Context mContext, final String quizName){
//		final Dialog dialog = new Dialog(mContext);
//		dialog.setTitle("Delete " + quizName + "?");
//		
//		//Initialize Dialog
//		LinearLayout ll = new LinearLayout(mContext);
//		ll.setLayoutParams(new LinearLayout.LayoutParams(
//				LayoutParams.MATCH_PARENT,
//				LayoutParams.MATCH_PARENT));
//		ll.setOrientation(LinearLayout.HORIZONTAL);
//		
//		ImageButton set = new ImageButton(mContext);
//		set.setLayoutParams(new LinearLayout.LayoutParams(
//				LayoutParams.MATCH_PARENT,
//				LayoutParams.MATCH_PARENT,
//				1.0f));
//		set.setAdjustViewBounds(true);
//		set.setImageResource(R.drawable.green_check);
//		set.setOnClickListener(new OnClickListener(){
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				dialog.dismiss();
//			}
//			
//		});
//		ll.addView(set);
//		
//		ImageButton undo = new ImageButton(mContext);
//		undo.setLayoutParams(new LinearLayout.LayoutParams(
//				LayoutParams.MATCH_PARENT,
//				LayoutParams.MATCH_PARENT,
//				1.0f));
//		undo.setAdjustViewBounds(true);
//		undo.setImageResource(R.drawable.red_x);
//		undo.setOnClickListener(new OnClickListener(){
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				dialog.dismiss();
//			}
//			
//		});
//		ll.addView(undo);
//		
//		dialog.setContentView(ll);
//		dialog.show();
//	}
//	
	//Import Function - Select Quizzes to import
	private void showImportList(final Context mContext, final List<QuizImport> quizzes){
        final Dialog dialog = new Dialog(mContext);
        dialog.setTitle(IMPORT_LIST_TEXT);

        LinearLayout ll = new LinearLayout(mContext);
        LinearLayout llRight = new LinearLayout(mContext);
        llRight.setLayoutParams(new LinearLayout.LayoutParams(
        	LayoutParams.MATCH_PARENT,
        	LayoutParams.MATCH_PARENT,
       		0.65f));

        ll.setOrientation(LinearLayout.HORIZONTAL);
        llRight.setOrientation(LinearLayout.VERTICAL);
      
        ListView list = new ListView(mContext);
        list.setLayoutParams(new LinearLayout.LayoutParams(
        	LayoutParams.MATCH_PARENT,
        	LayoutParams.MATCH_PARENT,
       		0.35f));
        
        final ImportAdapter ia = new ImportAdapter(mContext,R.layout.quiz_listview_import, quizzes);
        list.setAdapter(ia);
        list.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
				long arg3) {
				// TODO Auto-generated method stub
				quizzes.get(pos).toggleImportStatus();
				ia.notifyDataSetChanged();
			}
        	
        });
        ll.addView(list);
        
        
        ImageButton set = new ImageButton(mContext);
        set.setImageResource(R.drawable.green_check);
        set.setAdjustViewBounds(true);
        set.setScaleType(ScaleType.CENTER_INSIDE);
        set.setLayoutParams(new LinearLayout.LayoutParams(
        	LayoutParams.MATCH_PARENT,
        	LayoutParams.MATCH_PARENT,
       		0.3f));
        
        set.setOnClickListener(new OnClickListener() {
        	boolean hasImported = false;
        	
			//Sets the directories.
			//data = data directory
			File data = Environment.getDataDirectory();
            public void onClick(View v) {
            	
            	//Make sure internal directory exists
            	File f = new File(data,getResources().getString(R.string.internal_directory_folder));
				if(f.mkdirs()){
					System.out.println("Internal Directories Created...");
				}
				
            	for(QuizImport q : quizzes){
            		if(q.getImportStatus() == true){
            			
            			QuizDatabaseManager src = new QuizDatabaseManager(
    						mContext,
    						getResources().getString(R.string.database_file) + "TEMP");
    					QuizDatabaseManager dst = new QuizDatabaseManager(
    						mContext,
    						getResources().getString(R.string.database_file));
    					
    					Cursor c = null;
            			try {
        					src.open(false);
        					dst.open(true);
        					src.setTable(q.getQuizName());
        					
        					if(dst.setTable(q.getQuizName())){
        						dst.deleteAllWords();
        					} else{
        						dst.addTable(q.getQuizName());
        						dst.setTable(q.getQuizName());
        					}
        					
        					c = src.getAllWords();
        					c.moveToFirst();
        					startManagingCursor(c);
        					
        					if(c.getCount() != 0){
        						do{	
        							dst.addWord(c.getString(c.getColumnIndex("word")),c.getString(c.getColumnIndex("definition")));
        						} while (c.moveToNext());
        					}
        					
        					hasImported = true;

                		} catch (Exception e) {
                			System.out.println("Crash: " + e.getMessage());
                			Toast.makeText(mContext, "ERROR 1: Quizzes failed to import", Toast.LENGTH_LONG).show();
                		} finally{
                			if(c != null)
                				c.close();
                			dst.close();
                			src.close();
                		}
            			
            		}
            	}
            	
            	if(hasImported){
            		Toast.makeText(mContext, "Quizzes successfully imported!", Toast.LENGTH_SHORT).show();
            		updateList();
            	}
            	
            	dialog.dismiss();
            	hideIntro();
            }
        });        
        
        set.setPadding(5, 5, 5, 5);
        llRight.addView(set);
        
        //Set
        Button toggleAll = new Button(mContext);
        toggleAll.setLayoutParams(new LinearLayout.LayoutParams(
        	LayoutParams.MATCH_PARENT,
        	LayoutParams.MATCH_PARENT,
       		0.7f));
        toggleAll.setText(TOGGLE_TEXT);
        toggleAll.setTextSize(TITLE_SIZE);
        toggleAll.setOnClickListener(new OnClickListener() {
        	
        	boolean toggleStatus = true;

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				for(QuizImport q: quizzes){
					q.setImportStatus(toggleStatus);
				}
				toggleStatus = !toggleStatus;
				ia.notifyDataSetChanged();
			}
        	
        });
        llRight.addView(toggleAll);
        
        ll.addView(llRight);

        dialog.setContentView(ll);        
        dialog.show();        
    }
	
	//Updates list of quizzes available
	private void updateList(){
		qm = new ArrayList<Quiz>();
		data = new QuizDatabaseManager(this, getResources().getString(R.string.database_file));
		data.open(true);
		
		//Grab list of all quizzes
		ArrayList<String> quizDb = data.getQuizCount();
		data.close();
		//If # of quizzes > 0:
		if(quizDb != null && quizDb.size() > 0){
			//# of quizzes > 0. Grab quizzes and display them onto screen
			
			//Hide Tutorial
			hideIntro();
			
			//Take quiz names, remove brackets, and add them to QuizManager
			for(int x = 0; x < quizDb.size(); x++){
				qm.add(new Quiz(quizDb.get(x)));
			}
			
			//Set adapter
			qlva = new QuizListAdapter(this,R.layout.quiz_listview,qm);
			quizList.setAdapter(qlva);
		}
	}
	
	//When device switches from portrait to landscape and vice versa
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		
		setContentView(R.layout.quiz_front);
		//Set Click Listener to GridView of Quizzes
		quizList = (GridView)findViewById(R.id.frontQuizList);
	    quizList.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
				int position, long arg3) {
				// TODO Auto-generated method stub
				System.out.println("POSITION: " + position);
				dialogOptions(view.getContext(),position);
			}
	    	 
	    });
	    
	    //Set Hold Listener to GridView of Quizzes
	    quizList.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
				// TODO Auto-generated method stub
				return false;
			}
	    	
	    });
		
		updateList();

	}

	//Hide Introduction
	private void hideIntro(){
		View v = findViewById(R.id.frontIntro);
		v.setVisibility(View.GONE);
	}
	
	//Internal helper class to import Quizzes
	class QuizImport {
		//Name of Quiz
		private String name;
		
		//Whether to import a quiz or not
		private boolean shouldImport;
		
		public QuizImport(String name){
			this.name = name;
			shouldImport = false;
		}
		
		public void toggleImportStatus(){
			shouldImport = !shouldImport;
		}
		
		public void setImportStatus(boolean shouldImport){
			this.shouldImport = shouldImport;
		}
		
		public String getQuizName(){
			return name;
		}
		
		public boolean getImportStatus(){
			return shouldImport;
		}
	}
	
	//Internal helper class to populate Listview with Quizzes
	class ImportAdapter extends ArrayAdapter<QuizImport>{

		private int layout;
		
		public ImportAdapter(Context context, int layout, List<QuizImport> objects) {
			super(context, layout, objects);
			this.layout = layout;
			// TODO Auto-generated constructor stub
		}

		/* (non-Javadoc)
		 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View v = convertView;
			if(v == null){
				LayoutInflater li = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = li.inflate(layout, null);
			}
			
			TextView quizName = (TextView)v.findViewById(R.id.importQuizName);
			CheckBox check = (CheckBox)v.findViewById(R.id.importCheck);
			
			QuizImport q = getItem(position);
			quizName.setText(q.getQuizName());
			check.setChecked(q.getImportStatus());
				
			return v;
		}
	}
}


