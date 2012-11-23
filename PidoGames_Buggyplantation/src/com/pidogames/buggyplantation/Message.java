package com.pidogames.buggyplantation;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;

public class Message {
	
	private static final int IN_ANIM_STEPS  = 20;
	private static final int OUT_ANIM_STEPS = 20;
	
	private Paint paint;

	private String rawText;
	private int cw;
	
	private int scroll_y;
	
	private String text;
	private int duration;
	private float textSize;
	private int alpha;
	private int alive;
	private boolean hideOnTap;
	
	private int topPosition;
	private int bottomPosition;
	
	public Message(String text,  int duration, boolean hideOnTap, Paint paint){
		if(paint==null){
			paint = new Paint();
			paint.setColor(0xffff5500);
			paint.setTextSize(30*SceneView.scale);
			paint.setTypeface(Typeface.SANS_SERIF);
			paint.setColorFilter(new LightingColorFilter(0xffffffff, 0xff101010));
			//paint.setShadowLayer(2*SceneView.scale, SceneView.scale, SceneView.scale, 0xff000000);
		}
		paint.setTextAlign(Paint.Align.CENTER);
		
		textSize = paint.getTextSize();
		alpha    = paint.getAlpha();
		
		topPosition	   = 0;
		bottomPosition = 0;
		
		this.paint = paint;
		this.duration = duration;
		this.hideOnTap = hideOnTap;
		setText(text);
		this.scroll_y = 0;
		paint.setTextSize(0);
		paint.setAlpha(0);
		
		alive = 0;
	}
	
	public boolean onTap(){
		if(hideOnTap){
			hideOnTap = false;
			return true;
		}
		return false;
	}
	
	public boolean isExpired(){
		return (alive - OUT_ANIM_STEPS - IN_ANIM_STEPS - duration > 0);
	}
	
	public boolean isScrollable(){
		return hideOnTap && paint.getTextSize()==textSize;
	}
	
	public int getScrollY(){
		return scroll_y;
	}
	
	public void setScrollY(int scroll_y){
		this.scroll_y = scroll_y;
	}
	
	public void setText(String text){
		float prev_size = paint.getTextSize();
		paint.setTextSize(textSize);
		this.rawText = text;
		this.cw = SceneView.getCanvasWidth();
		StringBuilder sb = new StringBuilder();
		String [] lines = text.split("\n");
		for(String ll : lines){
			String [] words = ll.split(" ");
			String line=null;
			String prev_line=null;
			for(String word : words){
				line = line==null?word:(line+" "+word);
				if(paint.measureText(line)>=cw) {
					if(prev_line!=null){
						sb.append(prev_line);
						sb.append('\n');
						line = word;
						prev_line = null;
					}
					else {
						sb.append(line);
						sb.append('\n');
						line = null;
					}
				}
				prev_line = line;
			}
			sb.append(line);
			sb.append('\n');
		}
		
		this.text = sb.toString();
		paint.setTextSize(prev_size);
	}
	
	public String getText(){
		return text;
	}
	
	public int getTopPosition(){
		return topPosition;
	}
	
	public int getBottomPosition(){
		return bottomPosition;
	}
	
	public void draw(Canvas canvas){
		if(alive <= IN_ANIM_STEPS){
			int a = alive*this.alpha/IN_ANIM_STEPS;
			paint.setTextSize(alive*this.textSize/IN_ANIM_STEPS);
			//paint.setShadowLayer(5*SceneView.scale, SceneView.scale, SceneView.scale, 0xff0000 | (a << 24));
			paint.setAlpha(a);
			alive++;
		}
		else if(!hideOnTap && alive-IN_ANIM_STEPS>duration){
			int i = OUT_ANIM_STEPS - alive + IN_ANIM_STEPS + duration;
			if(i>0){
				int a = i*this.alpha/OUT_ANIM_STEPS;
				paint.setTextSize(this.textSize + (OUT_ANIM_STEPS-i)*this.textSize/OUT_ANIM_STEPS);
				//paint.setShadowLayer(5*SceneView.scale, SceneView.scale, SceneView.scale, 0xff0000 |(a << 24));
				paint.setAlpha(a);
			}
			alive++;
		}
		else if(alive-IN_ANIM_STEPS<=duration){
			alive++;
		}
		
		//recalculate on orientation change
		if(cw!=SceneView.getCanvasWidth()) setText(rawText);
		
		String [] lines = text.split("\n");
		int i=-lines.length/2;
		float ts = paint.getTextSize();
		float tc = (lines.length%2!=0)?(ts/2):0f;
		Coord position = SceneView.getInstance(null).getThread().getScreenCenter();
		if(paint.getAlpha()>0){
			topPosition = (int)(position.y+ts*(i-1)-tc-scroll_y*((float)ts/(float)textSize));
			for(String l : lines) {
				canvas.drawText(l, position.x, position.y+ts*i-tc-scroll_y*((float)ts/(float)textSize) , paint);
				i++;
			}
			bottomPosition = (int)(position.y+ts*i-tc-scroll_y*((float)ts/(float)textSize));
		}
	}
}
