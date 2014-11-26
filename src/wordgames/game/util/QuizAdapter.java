/*
 * Subclass of ArrayAdapter<T> - Provides custom implementation of getView()
 */

package wordgames.game.util;

import wordgames.game.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class QuizAdapter extends ArrayAdapter<WordPair> {
	Context mContext;
	int layoutId;
	
	public QuizAdapter(Context context, int layout, Quiz quiz){
		super(context, layout, quiz);
		mContext = context;
		layoutId = layout;
	}
	
	public View getView(int pos, View convertView, ViewGroup parent){
		View v = convertView;
		WordPair wp = this.getItem(pos);
		
		if(v == null){
			LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = li.inflate(layoutId, null);
		}
		
		TextView term = (TextView)v.findViewById(R.id.pairWord);
		TextView desc = (TextView)v.findViewById(R.id.pairDesc);
		
		term.setText(wp.word);
		desc.setText(wp.definition);
		
		return v;
	}
}
