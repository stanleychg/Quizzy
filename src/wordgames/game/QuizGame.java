package wordgames.game;

/*
 * QuizGame - User tests him/herself with quiz
 * 
 * When a user decides to play with a quiz, those words are brought up.
 * A certain amount of words are randomly chosen; one chosen word's definition is displayed.
 * The user must touch the word that correctly matches the definition
 * If the user is correct, the word is replaced and another definition appears
 * If the user is incorrect, the user must continue to guess. 
 * 
 */

import java.util.Random;

import wordgames.game.util.DatabaseQuizManager;
import wordgames.game.util.Quiz;
import wordgames.game.util.WordPair;
import android.app.Activity;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class QuizGame extends Activity {
    /** Called when the activity is first created. */
	 
	final int MAX_WORDS = 4;
	
	//This is the question
	TextView wordDesc;
	 
	//This was the previous question
	TextView descPrevious;
	 
	//Referring to the text "Right"
	TextView right;
	int rightCount = 0;
	 
	//Referring to the text "WRONG"
	TextView wrong;
	int wrongCount = 0;
	
	//The x Selections
	Button[] bank;
	
	//The x WordPair Selections
	WordPair[] wordSelections;
	 
	//WordPair Selected
    WordPair word;
    int index;
	
	//words[] is to set up varied multiple choice in beginning to prevent repeat answers
	//Later set up to be the size equal to amount of pairs
	boolean[] record;
	
	//Keeps count of words used to know when to reset
	int wordsDone = 0;
	
	//Grab quiz
	Quiz QUIZ;
	Quiz wordsLeft;

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_game);
        
        //Initialize QUIZ. Afterwards, QUIZ shall remain unmodified
        QUIZ = new Quiz((this.getIntent().getStringExtra("name"))); 
        
        if(prepareQuiz(QUIZ.getName()) == false){
        	Toast.makeText(this, "There aren't enough words!", Toast.LENGTH_LONG).show();
        	finish();
        	return; //Make sure the Activity stops and returns instead of continuing
        }
        
        wordsLeft = new Quiz(QUIZ.getName());
        wordsLeft.addAll(QUIZ);
        
        //Initialize Views
        wordDesc = (TextView)this.findViewById(R.id.gameCurrentDesc);
      descPrevious = (TextView)findViewById(R.id.gamePreviousDesc);
      right = (TextView)this.findViewById(R.id.gameRightCount);
      wrong = (TextView)this.findViewById(R.id.gameWrongCount);


        //Initialize Word Selections
        bank = new Button[Math.max(2, Math.min(QUIZ.getSize()/3,MAX_WORDS))];
        switch(bank.length){
        case 4:
            bank[3] = (Button)this.findViewById(R.id.gameWord4);
        case 3:
            bank[2] = (Button)this.findViewById(R.id.gameWord3);
        case 2:
            bank[1] = (Button)this.findViewById(R.id.gameWord2);
            bank[0] = (Button)this.findViewById(R.id.gameWord1);
            break;
        }
        
        //Set buttons visible
        for(Button b: bank){
        	b.setVisibility(View.VISIBLE);
        }
       
        //Word Selections
        wordSelections = new WordPair[bank.length];
        for(int x = 0; x < wordSelections.length; x ++){
        	grabAnotherWord(x);
        }
        
        //Select one word
        Random Rand = new Random();
        index = Rand.nextInt(wordSelections.length);
        word = wordSelections[index];
        wordDesc.setText(word.definition);
        
        //Animate Views
	    AnimationSet as = (AnimationSet)AnimationUtils.loadAnimation(this, R.animator.fade_in);
	    (findViewById(R.id.gameLayout)).startAnimation(as);
        
    }
    
    //Set up Action Bar
//	public boolean onCreateOptionsMenu(Menu menu){
//		MenuInflater inflater = getMenuInflater();
//		inflater.inflate(R.menu.quiz_game_action, menu);
//		
//        //Initialize Views
//        descPrevious = (TextView)findViewById(R.id.menu_prev_ans);
//        right = (TextView)this.findViewById(R.id.menu_right);
//        wrong = (TextView)this.findViewById(R.id.menu_wrong);
//		return super.onCreateOptionsMenu(menu);
//	}
	
	//Event Listener for Action Bar
//	public boolean onOptionsItemSelected(MenuItem item){
//		switch(item.getItemId()){
//		case R.id.menu_add:
//			displayAddOptions(QuizMaker.this);
//			return true;
//		default:
//			return super.onOptionsItemSelected(item);
//		}
//	}
	
    //Check if the View/answer selected is correct 
	public void checkAnswer(View choice){
		TextView tv = (TextView)choice;
		if(tv.getText().toString().equals(word.word)){
			descPrevious.setText("Correct! - " + word.word 
					+ " = " + wordDesc.getText().toString());
			
			grabAnotherWord(index);
			
	        //Select one word
	        Random Rand = new Random();
	        index = Rand.nextInt(wordSelections.length);
	        word = wordSelections[index];
	        wordDesc.setText(word.definition);
			
			rightCount ++;
			right.setText(String.valueOf(rightCount));
			
		}
		else{
			wrongCount ++;
			wrong.setText(String.valueOf(wrongCount));
			
		}
	}

	//Take another unique word from wordsLeft and
	//replace the selected index with it.
	public void grabAnotherWord(int bankIndex){
		//Check for remaining words. If none left, reset.
		if(wordsLeft.getSize() == 0)
			wordsLeft.addAll(QUIZ);
		
		//Select random index to grab from quiz
		Random Rand = new Random();
		int random = Rand.nextInt(wordsLeft.getSize());
		
		//Grab WordPair at int random. Add to selection
		wordSelections[bankIndex] = wordsLeft.get(random);
		bank[bankIndex].setText(wordSelections[bankIndex].word);
		
		wordsLeft.remove(random);
	}
	
	//Fill a quiz with its appropriate WordPairs
	public boolean prepareQuiz(String name){
		DatabaseQuizManager data = new DatabaseQuizManager(this,getResources().getString(R.string.database_file));
		data.open(false);
		data.setTable(name);
		
		Cursor a = data.getAllWords();
		this.startManagingCursor(a);
		a.moveToFirst();
		
		if(a.getCount() > 1){
			do{
				QUIZ.add(new WordPair(a.getString(a.getColumnIndexOrThrow("word")),a.getString(a.getColumnIndexOrThrow("definition"))));
			}while(a.moveToNext());
			
			data.close();
			a.close();
			return true;
		}
		
		data.close();
		a.close();
		return false;
		

	}
	
	//Exit screen
	@Override
	public void onBackPressed(){
		finish();
	}


	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		
		//Grab vital info before changing to new layout
		String tempPreviousCorrect = descPrevious.getText().toString();
		
        //Reset Content View for new layout
		setContentView(R.layout.quiz_game);
        
        //Initialize Views
        wordDesc = (TextView)this.findViewById(R.id.gameCurrentDesc);
        descPrevious = (TextView)findViewById(R.id.gamePreviousDesc);
        right = (TextView)this.findViewById(R.id.gameRightCount);
        wrong = (TextView)this.findViewById(R.id.gameWrongCount);


        //Initialize Word Selections
        bank = new Button[Math.max(2, Math.min(QUIZ.getSize()/3,MAX_WORDS))];
        switch(bank.length){
        case 4:
            bank[3] = (Button)this.findViewById(R.id.gameWord4);
        case 3:
            bank[2] = (Button)this.findViewById(R.id.gameWord3);
        case 2:
            bank[1] = (Button)this.findViewById(R.id.gameWord2);
            bank[0] = (Button)this.findViewById(R.id.gameWord1);
            break;
        }
        
        //Set buttons visible
        for(Button b: bank){
        	b.setVisibility(View.VISIBLE);
        }
        
        //Reset definition
        wordDesc.setText(word.definition);
        
        //Reset bank
		for(int x = 0; x < bank.length; x++){
			bank[x].setText(wordSelections[x].word);
		}

        
        //Reset Scores
		right.setText(String.valueOf(rightCount));
		wrong.setText(String.valueOf(wrongCount));
		
		//Reset previous word if one has been answered
		if(rightCount > 0){
			descPrevious.setText(tempPreviousCorrect);
		}

        
	}

	
    
}
