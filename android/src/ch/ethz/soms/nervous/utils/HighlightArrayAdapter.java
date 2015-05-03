package ch.ethz.soms.nervous.utils;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class HighlightArrayAdapter<String> extends ArrayAdapter<String>{

	public HighlightArrayAdapter(Context context, int resource,
			String[] objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
	}

	private int selectedItem = -1;
	
	
	
	public void setSelectedItem(int position)
	{
		selectedItem = position;
	}
	
	public int getSelectedItem()
	{
		return selectedItem;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position,convertView,parent);
		if(convertView!=null && position==selectedItem)
			convertView.setBackgroundColor(Color.rgb(77, 148, 255));
		else if(convertView!=null)
			convertView.setBackgroundColor(Color.TRANSPARENT);
		return super.getView(position, convertView, parent);
	}

}
