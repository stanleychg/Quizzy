/*
 * Subclass of QuizListAdapter<T> - Provides custom implementation of getView()
 */

package wordgames.game.util;

import java.util.ArrayList;

import wordgames.game.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class QuizListAdapter extends ArrayAdapter<Quiz>{

	int quizLayout;
	
	public QuizListAdapter(Context context, int textViewResourceId, ArrayList<Quiz> qm) {
		super(context, textViewResourceId, qm);
		quizLayout = textViewResourceId;
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
		
		Quiz q = getItem(position);
		System.out.println("POSITION: " + position);
		TextView name = (TextView)v.findViewById(R.id.layoutName);
		//TextView wordCount = (TextView)v.findViewById(R.id.layoutWordCount);
		//TextView description = (TextView)v.findViewById(R.id.layoutDesc);
		
		name.setText(q.getName());
		//wordCount.setText("");
		//description.setText("");
		
		return v;
	}
}
