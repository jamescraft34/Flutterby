package com.craftysoft.flutterbyandmarguerite;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
	
public class NarrationProfileAdapter extends ArrayAdapter<String> {

	private String[] _profiles;
	private Context _context;
	private int _textViewResourceId;
	private int _resource;

	
	public NarrationProfileAdapter(Context context, int resource,
			int textViewResourceId, String[] profiles) 
	{
		super(context, resource, textViewResourceId, profiles);
		_resource = resource;
		_context = context;
		_profiles = profiles;
		_textViewResourceId = textViewResourceId;	
	}
	
	static class ViewHolder
	{
		public TextView textView;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{	
		ViewHolder vh;
		
        if(convertView == null) 
        {        	
            LayoutInflater vi = (LayoutInflater)_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(_resource, null);        
            
            vh = new ViewHolder();
        	vh.textView = (TextView)convertView.findViewById(_textViewResourceId);
        	
        	convertView.setTag(vh);
            
        }
        else
        {
        	vh = (ViewHolder)convertView.getTag();
        }
        
        String profileName = _profiles[position];
        
        if (profileName != "") 
        {
        	vh.textView.setText(profileName);
        }
        
        return convertView;
	}
}
