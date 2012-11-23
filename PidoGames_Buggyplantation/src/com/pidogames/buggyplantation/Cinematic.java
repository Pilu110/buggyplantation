package com.pidogames.buggyplantation;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;

public class Cinematic {
	private String name;
	private boolean is_asset;
	private int scene;
	
	private boolean is_ended;
	private boolean is_loaded;
	
	private int text_index;
	private JSONArray text;
	private Bitmap background;	
	
	private Paint text_paint;
	
	public Cinematic(String name, boolean is_asset){
		this.name     = name;
		this.is_asset = is_asset;
		scene         = 1;
		text_index    = 0;
		is_ended	  = false;
		is_loaded     = false;
		
		text_paint = new Paint();
		text_paint.setTextAlign(Align.CENTER);
		text_paint.setTextSize(20*SceneView.scale);
		text_paint.setColor(0xffa62c01);
		text_paint.setShadowLayer(2*SceneView.scale, SceneView.scale, SceneView.scale, 0xff5d1901);
	}
	
	public synchronized void moveToNext(){
		if(is_loaded){
			do {
				if(text!=null && text_index<text.length()-1) text_index++;
				else {
					text_index = 0;
					scene++;
					is_loaded = false;
				}
			} while(is_loaded && text.optString(text_index,null)==null);
		}
	}
	
	public void moveIt(){
		if(!is_loaded) {
			if(background!=null){
				background.recycle();
				background = null;
			}
			
			
			boolean is_loaded = FileManager.getInstance(SceneView.getInstance(null).getContext()).loadCinematicScene(this);
			if(is_loaded) this.is_loaded = true;
			else {
				is_ended = true;
			}
		}
	}
	
	public void drawIt(Canvas canvas){
		canvas.drawColor(0xff000000);
		if(is_loaded){
			int w = canvas.getWidth();
			int h = canvas.getHeight();
			
			Bitmap bg = background;
			if(bg!=null) {
				int bw = bg.getWidth();
				int bh = bg.getHeight();
				float sx = (float)w/(float)bw;
				float sy = (float)h/(float)bh;
				float sr = sx<sy?sx:sy;
				bw = (int)(bw*sr);
				bh = (int)(bh*sr);
				int ox = (w-bw)/2;
				int oy = (h-bh)/2;
				canvas.drawBitmap(background, null, new Rect(ox,oy,bw+ox,bh+oy), null);
			}
			
			try {
				String [] words = text.getString(text_index).split(" ");
				String prev="", act = "";
				int i=0;
				ArrayList<String> sentences = new ArrayList<String>();
				do {
					do {
						prev = act;
						act += (" " + words[i]);
						i++;
					} while(i<words.length && text_paint.measureText(act)<w);
					
					if(i<words.length){
						sentences.add(prev);
						act = words[i-1];
					}
					else {
						if(text_paint.measureText(act)<w)
							sentences.add(act);
						else {
							sentences.add(prev);
							sentences.add(words[i-1]);
						}
					}
				} while(i<words.length);
				
				int line = sentences.size();
				for(String s : sentences){
					canvas.drawText(s, w/2, h-line*text_paint.getTextSize()-20*SceneView.scale, text_paint);
					line--;
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean isAsset(){
		return is_asset;
	}
	
	public boolean isEnded(){
		return is_ended;
	}
	
	public int getScene(){
		return scene;
	}
	
	public String getName(){
		return name;
	}
	
	public void onLoadScene(JSONArray text, Bitmap background){
		this.text = text;
		this.background = background;
	}
}
