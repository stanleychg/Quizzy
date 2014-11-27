/*
 * Subclass of QuizListAdapter<T> - Provides custom implementation of getView()
 */

package wordgames.game.util;

import java.util.ArrayList;

import wordgames.game.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class QuizListAdapter extends ArrayAdapter<Quiz>{

	int quizLayout; //Stores resource id of layout
	int selectedQuiz; //Stores last quiz clicked
	
	public QuizListAdapter(Context context, int textViewResourceId, ArrayList<Quiz> qm) {
		super(context, textViewResourceId, qm);
		quizLayout = textViewResourceId;
		selectedQuiz = (qm.size() == 1? 0: -1);
	}

	public void setItemLastClicked(int pos){
		//Allow unselection
		selectedQuiz = (selectedQuiz == pos? -1: pos);
	}
	
	public int getItemLastClicked(){
		return selectedQuiz;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){	
		View v = convertView;
		
		//Temp fix
		if(v == null)
		{
			LayoutInflater li = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = li.inflate(quizLayout, null);
		}
		
		System.out.println(position);
		
		Quiz q = getItem(position);
		TextView name = (TextView)v.findViewById(R.id.quizName);
		TextView wordCount = (TextView)v.findViewById(R.id.quizNumWords);
		//TextView description = (TextView)v.findViewById(R.id.quizDesc);
		TextView sample = (TextView)v.findViewById(R.id.quizSample);
		Button edit = (Button)v.findViewById(R.id.quizEdit);
		edit.setOnClickListener((OnClickListener) getContext());
		Button play = (Button)v.findViewById(R.id.quizPlay);
		play.setOnClickListener((OnClickListener) getContext());
		Button delete = (Button)v.findViewById(R.id.quizDelete);
		delete.setOnClickListener((OnClickListener) getContext());
		
		name.setText(q.getName());
		wordCount.setText("# Words: ");
		//description.setText("Description: ");
		sample.setText("Sample: ");
		
		//Check if current item is selected item
		if(position == selectedQuiz){
			//Item is selected quiz. Display more info
			wordCount.setVisibility(View.VISIBLE);
			//description.setVisibility(View.VISIBLE);
			sample.setVisibility(View.VISIBLE);
			edit.setVisibility(View.VISIBLE);
			play.setVisibility(View.VISIBLE);
			delete.setVisibility(View.VISIBLE);

		} else {
			//Item not selected quiz
			wordCount.setVisibility(View.GONE);
			//description.setVisibility(View.GONE);
			sample.setVisibility(View.GONE);
			edit.setVisibility(View.GONE);
			play.setVisibility(View.GONE);
			delete.setVisibility(View.GONE);
		}
		return v;
	}
}
