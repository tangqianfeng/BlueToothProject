package com.useroperate;

import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;

public class ArrayAdapterImp extends ArrayAdapter<String> {

	private List<Integer> value_list;
	
	public ArrayAdapterImp(Context context, int textViewResourceId, List<String> show_list,List<Integer> value_list) {
		super(context, textViewResourceId, show_list);
		this.value_list = value_list;
	}
	
	public int getValue(int pos){
		return value_list.get(pos);
	}

	public int getItemPositionByValue(int value){
		for(int i=0;i<value_list.size();i++){
			if(value_list.get(i)==value){
				return i;
			}
		}
		return 0;
	}
}
