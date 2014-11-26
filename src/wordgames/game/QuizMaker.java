/*
 * QuizMaker - Handles any modifications to Quiz
 * 
 * -Add, edit, delete words + definitions
 * -Displays word count
 */

package wordgames.game;

import java.util.ArrayList;

import wordgames.game.util.QuizDatabaseManager;
import wordgames.game.util.Quiz;
import wordgames.game.util.QuizAdapter;
import wordgames.game.util.WordPair;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class QuizMaker extends Activity{
	
	//Dialog Text Dimensions
	final float HEADER_SIZE = 25.0f;
	final float TERM_SIZE = 40.0f;
	final float DESC_SIZE = 16.0f;
	
	//UI
	ListView wordList;
	QuizAdapter wpa;
	TextView quizName;
	TextView wordCount;

	int numOfWords;
	
	QuizDatabaseManager data;
	ArrayList<Quiz> qm = new ArrayList<Quiz>();
	Quiz quiz;
	
	//Initialization
	//If there aren't any existing words, bring up tutorial
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.quiz_maker);
	
	    //List of words in quiz
	    wordList = (ListView)findViewById(R.id.makeWordList);
	    
	    //Name of quiz
	    quiz = new Quiz(this.getIntent().getStringExtra("name"));
		//quizName = (TextView)findViewById(R.id.makeQuizTitle);
	    this.getActionBar().setTitle(this.getIntent().getStringExtra("name"));

		//Open database with name <quizName>
    	System.out.println("NAME: " + quiz.getName());
		data = new QuizDatabaseManager(this,getResources().getString(R.string.database_file));
		
		data.open(true);
		
		if(!data.setTable(quiz.getName())){
			data.addTable(quiz.getName());
			data.setTable(quiz.getName());
		}
		
		Cursor c;
		
		c = data.getAllWords();
		c.moveToFirst();
		this.startManagingCursor(c);
		
		if(c.getCount() != 0){
			hideIntro();
			do{	
				quiz.add(new WordPair(c.getString(c.getColumnIndex("word")),c.getString(c.getColumnIndex("definition"))));
			} while (c.moveToNext());
		}

		c.close();

		
		numOfWords = quiz.getSize();
		wpa = new QuizAdapter(this,R.layout.quiz_listview_wordpair, quiz);
		wordList.setAdapter(wpa);
	    wordList.setOnItemClickListener(new OnItemClickListener(){
	
			public void onItemClick(AdapterView<?> arg0, View view, int pos,
					long arg3) {
				// TODO Auto-generated method stub
				displayModifyOptions(view.getContext(), pos);
			}
	    	
	    });
		
		
		wordCount = (TextView)findViewById(R.id.makeWordCount);
		wordCount.setText("");
		wordCount.setText(String.valueOf(numOfWords) + " words");
		
	    //Animate Views
	    AnimationSet as = (AnimationSet)AnimationUtils.loadAnimation(this, R.animator.fade_in);
	    wordList.startAnimation(as);
	    
	    //Visibility of word count. For testing purposes
	    if(wordCount.getVisibility() != View.INVISIBLE)
	    	wordCount.startAnimation(as);
		
		
	}
	
	//Add Action Bar
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.quiz_maker_action, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	//Event Listener for Action Bar
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case R.id.menu_add:
			displayAddOptions(QuizMaker.this);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	//Add word dialog
	public void displayAddOptions(final Context mContext){
        final Dialog dialog = new Dialog(mContext);
        dialog.setTitle("Add Word");

        LinearLayout ll = new LinearLayout(mContext);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        
        LinearLayout llLeft = new LinearLayout(mContext);
        llLeft.setOrientation(LinearLayout.VERTICAL);
        llLeft.setLayoutParams(new TableLayout.LayoutParams(
        		LayoutParams.MATCH_PARENT,
        		LayoutParams.MATCH_PARENT,
        		0.5f));
        
        //Insert name of the word
        TextView nameTitle = new TextView(mContext);
        nameTitle.setText("Term");
        nameTitle.setTextSize(HEADER_SIZE);
        llLeft.addView(nameTitle);
        
        final EditText name = new EditText(mContext);
        name.setWidth(200);
        name.setLayoutParams(new TableLayout.LayoutParams(
        		LayoutParams.MATCH_PARENT,
        		LayoutParams.MATCH_PARENT,
        		1.0f));
        name.setTextSize(TERM_SIZE);
        name.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        llLeft.addView(name);
        
        //Insert definition of word
        TextView descTitle = new TextView(mContext);
        descTitle.setText("Description");
        descTitle.setTextSize(HEADER_SIZE);
        llLeft.addView(descTitle);
        
        final EditText desc = new EditText(mContext);
        desc.setWidth(200);
        desc.setLayoutParams(new TableLayout.LayoutParams(
        		LayoutParams.MATCH_PARENT,
        		LayoutParams.MATCH_PARENT,
        		1.0f));
        desc.setTextSize(DESC_SIZE);
        desc.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        llLeft.addView(desc);
        
        ll.addView(llLeft);
        
        final ImageButton set = new ImageButton(mContext);
        set.setImageResource(R.drawable.green_check);
        set.setScaleType(ScaleType.CENTER_INSIDE);
        set.setAdjustViewBounds(true);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
        		LayoutParams.MATCH_PARENT,
        		LayoutParams.WRAP_CONTENT,
        		1.0f);
        lp.gravity = Gravity.CENTER_VERTICAL;
        set.setLayoutParams(lp);
        set.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	String nameHolder = name.getText().toString();
            	String descHolder = desc.getText().toString();
            	
            	if(!nameHolder.isEmpty() && !descHolder.isEmpty()){
            		hideIntro();
                	quiz.add(new WordPair(nameHolder,descHolder));
                	wpa.notifyDataSetChanged();
                	numOfWords ++;
                	wordCount.setText(String.valueOf(numOfWords) + " words");
                    dialog.dismiss();
            	} else{
            		Toast.makeText(mContext, "Please fill out all fields!", Toast.LENGTH_SHORT).show();
            	}
            	
            }
        });        
        set.setPadding(5, 5, 5, 5);

        ll.addView(set);
        
        dialog.setContentView(ll);        
        dialog.show();        
    }

	//Change layout when orientation changes
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.quiz_maker);
		
	    //List of words in quiz
	    wordList = (ListView)findViewById(R.id.makeWordList);
	    
	    //Set Name of quiz on ActionBar
	    this.getActionBar().setTitle(this.getIntent().getStringExtra("name"));
		
		numOfWords = quiz.getSize();
		wordList.setAdapter(wpa);
	    wordList.setOnItemClickListener(new OnItemClickListener(){
	
			public void onItemClick(AdapterView<?> arg0, View view, int pos,
					long arg3) {
				// TODO Auto-generated method stub
				displayModifyOptions(view.getContext(), pos);
			}
	    	
	    });
		
		
		wordCount = (TextView)findViewById(R.id.makeWordCount);
		wordCount.setText("");
		wordCount.setText(String.valueOf(numOfWords) + " words");
	}

	//Modify word dialog
	public void displayModifyOptions(final Context mContext, final int pos){
        final Dialog dialog = new Dialog(mContext);
        dialog.setTitle("Modify Word");
        final WordPair wp = quiz.get(pos);

        LinearLayout ll = new LinearLayout(mContext);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setLayoutParams(new TableLayout.LayoutParams(
        		LayoutParams.MATCH_PARENT,
        		LayoutParams.MATCH_PARENT));
   
        LinearLayout llTop = new LinearLayout(mContext);
        llTop.setOrientation(LinearLayout.HORIZONTAL);
        llTop.setLayoutParams(new TableLayout.LayoutParams(
        		LayoutParams.MATCH_PARENT,
        		LayoutParams.MATCH_PARENT,
        		1.0f));
        
        LinearLayout llBottom = new LinearLayout(mContext);
        llBottom.setOrientation(LinearLayout.HORIZONTAL);
        llBottom.setLayoutParams(new TableLayout.LayoutParams(
        		LayoutParams.MATCH_PARENT,
        		LayoutParams.MATCH_PARENT,
        		1.0f));
        
        //Term
        TextView nameTitle = new TextView(mContext);
        nameTitle.setText("Term");
        nameTitle.setTextSize(HEADER_SIZE);
        
        final EditText name = new EditText(mContext);
        name.setLayoutParams(new TableLayout.LayoutParams(
        		LayoutParams.MATCH_PARENT,
        		LayoutParams.MATCH_PARENT,
        		0.25f));
        name.setText(wp.word);
        name.setTextSize(TERM_SIZE);
        
        //Desc/Definition
        TextView descTitle = new TextView(mContext);
        descTitle.setText("Description");
        descTitle.setTextSize(HEADER_SIZE);
        
        final EditText desc = new EditText(mContext);
        desc.setLayoutParams(new TableLayout.LayoutParams(
        		LayoutParams.MATCH_PARENT,
        		LayoutParams.MATCH_PARENT,
        		0.25f));
        desc.setText(wp.definition);
        desc.setTextSize(DESC_SIZE);
        
        //Confirm
        ImageButton set = new ImageButton(mContext);
        set.setImageResource(R.drawable.green_check);
        set.setScaleType(ScaleType.CENTER_INSIDE);
        set.setAdjustViewBounds(true);
        set.setLayoutParams(new TableLayout.LayoutParams(
        		LayoutParams.MATCH_PARENT,
        		LayoutParams.WRAP_CONTENT,
        		0.75f));
        set.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	WordPair wp = quiz.get(pos);
            	wp.word = name.getText().toString();
            	wp.definition = desc.getText().toString();
            	wpa.notifyDataSetChanged();
            	dialog.dismiss();
            }
        });        
        set.setPadding(5, 5, 5, 5);


        //Remove
        ImageButton remove = new ImageButton(mContext);
        remove.setImageResource(R.drawable.trash_can_small);
        remove.setScaleType(ScaleType.CENTER_INSIDE);
        remove.setAdjustViewBounds(true);
        remove.setLayoutParams(new TableLayout.LayoutParams(
        		LayoutParams.MATCH_PARENT,
        		LayoutParams.WRAP_CONTENT,
        		0.75f));
        remove.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Toast.makeText(mContext, wp.word + " Removed!", Toast.LENGTH_SHORT).show();
            	quiz.removeWordPair(wp);
            	wpa.notifyDataSetChanged();
            	numOfWords --;
            	wordCount.setText(String.valueOf(numOfWords) + " words");
                dialog.dismiss();
            }
        });        
        remove.setPadding(5, 5, 5, 5);
        
        
        ll.addView(nameTitle);
        llTop.addView(name);
        llTop.addView(set);
        ll.addView(llTop);
        
        ll.addView(descTitle);
        llBottom.addView(desc);
        llBottom.addView(remove);
        ll.addView(llBottom);
        

        
        dialog.setContentView(ll);        
        dialog.show();        
    }
	

	//Exit Screen
//	@Override
//	public void onBackPressed() {
//		saveQuiz();
//		finish();
//	}
	
	//Hide tutorial if there is a word
	private void hideIntro(){
		View v = findViewById(R.id.makeIntro);
		v.setVisibility(View.GONE);
	}
	
	public void saveQuiz(){
		//Save before leaving
		//Quizzes
		data.deleteAllWords();
		for(WordPair wp: quiz){
			data.addWord(wp.word, wp.definition);
		}
		data.close();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 * 
	 * Save quiz before pausing.
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if(data != null)
			saveQuiz();
	}
	
	

	/* (non-Javadoc)
	 * @see android.app.Activity#onRestart()
	 * 
	 * Open up database again if it closed
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		data.open(true);
		
		if(!data.setTable(quiz.getName())){
			data.addTable(quiz.getName());
			data.setTable(quiz.getName());
		}
	}
	
	
	
	
	


	
	
	
		
}