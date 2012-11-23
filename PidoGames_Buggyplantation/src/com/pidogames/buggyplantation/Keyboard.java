package com.pidogames.buggyplantation;

import android.content.Context;
import android.graphics.Canvas;

public class Keyboard {
	
	private static Keyboard instance;
	
	private static final String [][][] KEYS = {
		{
			{"q","w","e","r","t","y","u","i","o","p"},
			{"a","s","d","f","g","h","j","k","l"},
			{"z","x","c","v","b","n","m","12#"}
		},
		{
			{"0","1","2","3","4","5","6","7","9"},
			{"Q","W","E","R","T","Y","U","I","o","p"},
			{"a","s","d","f","g","h","j","k","l"},
			{"z","x","c","v","b","n","m"}
		}
	};
	
	public static Keyboard getInstance(Context context){
		if(instance==null) instance = new Keyboard(context);
		return instance;
	}
	
	private Keyboard(Context context){
		
	}

	public void draw(Canvas canvas){
		
	}
}
