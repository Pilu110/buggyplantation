package com.pidogames.buggyplantation.effect;

import android.graphics.Bitmap;
import android.graphics.Color;

public class ClockTintEffect extends Effect {
	
	private double angle;
	
	public void setAngle(double angle){
		this.angle = angle;
	}

	@Override
	public void applyEffect(Bitmap dst) {
		int w = dst.getWidth();
		int h = dst.getHeight();
		int ox = w/2;
		int oy = h/2;
		//int mh = h-(int)(angle / 2.0 / Math.PI * h);
		int [] pixels = new int[w*h];
		dst.getPixels(pixels, 0, w, 0, 0, w, h);
		
		
		int alpha;
		int gray;
		int green;
		int i;
		
		if(angle<=0){
			for(int y=0; y<h; y++){
				for(int x=0; x<w; x++){
					i = y*w + x;
					
					alpha = Color.alpha(pixels[i]);
					gray  = (Color.red(pixels[i]) + Color.green(pixels[i]) + Color.blue(pixels[i]))/3;
					green = gray + 64;
					if(green>255) green = 255;
					pixels[i] = Color.argb(alpha, gray, green, gray);
				}
			}
		}
		else if(angle<Math.PI/2){
			for(int y=oy; y<h; y++){
				for(int x=0; x<w; x++){
					i = y*w + x;
					
					alpha = Color.alpha(pixels[i]);
					gray  = (Color.red(pixels[i]) + Color.green(pixels[i]) + Color.blue(pixels[i]))/3;
					green = gray + 64;
					if(green>255) green = 255;
					pixels[i] = Color.argb(alpha, gray, green, gray);
				}
			}
			for(int y=0; y<oy; y++){
				for(int x=0; x<ox; x++){
					i = y*w + x;
					
					alpha = Color.alpha(pixels[i]);
					gray  = (Color.red(pixels[i]) + Color.green(pixels[i]) + Color.blue(pixels[i]))/3;
					green = gray + 64;
					if(green>255) green = 255;
					pixels[i] = Color.argb(alpha, gray, green, gray);
				}
			}
				
			double my = oy;
			double delta = Math.tan(Math.PI/2.0 - angle);
			for(int x=ox; x<w; x++){
				for(int y=oy-1; y>=my; y--){
					i = y*w + x;				
					alpha = Color.alpha(pixels[i]);
					gray  = (Color.red(pixels[i]) + Color.green(pixels[i]) + Color.blue(pixels[i]))/3;
					green = gray + 64;
					if(green>255) green = 255;
					pixels[i] = Color.argb(alpha, gray, green, gray);
				}
				if(my>0){
					my -= delta;
					if(my<0) my = 0;
				}
			}
		}
		else if(angle<Math.PI){
			for(int y=0; y<h; y++){
				for(int x=0; x<ox; x++){
					i = y*w + x;
					
					alpha = Color.alpha(pixels[i]);
					gray  = (Color.red(pixels[i]) + Color.green(pixels[i]) + Color.blue(pixels[i]))/3;
					green = gray + 64;
					if(green>255) green = 255;
					pixels[i] = Color.argb(alpha, gray, green, gray);
				}
			}			
			
			double mx = ox;
			double delta = Math.tan(Math.PI - angle);
			for(int y=oy; y<h; y++){
				for(int x=ox; x<mx; x++){
					i = y*w + x;				
					alpha = Color.alpha(pixels[i]);
					gray  = (Color.red(pixels[i]) + Color.green(pixels[i]) + Color.blue(pixels[i]))/3;
					green = gray + 64;
					if(green>255) green = 255;
					pixels[i] = Color.argb(alpha, gray, green, gray);
				}
				if(mx<w){
					mx += delta;
					if(mx>w) mx = w;
				}
			}
		}
		else if(angle<3.0*Math.PI/2){
			for(int y=0; y<oy; y++){
				for(int x=0; x<ox; x++){
					i = y*w + x;
					
					alpha = Color.alpha(pixels[i]);
					gray  = (Color.red(pixels[i]) + Color.green(pixels[i]) + Color.blue(pixels[i]))/3;
					green = gray + 64;
					if(green>255) green = 255;
					pixels[i] = Color.argb(alpha, gray, green, gray);
				}
			}			
			
			double my = oy;
			double delta = Math.tan(3.0*Math.PI/2.0 - angle);
			for(int x=ox; x>=0; x--){
				for(int y=oy; y<my; y++){
					i = y*w + x;				
					alpha = Color.alpha(pixels[i]);
					gray  = (Color.red(pixels[i]) + Color.green(pixels[i]) + Color.blue(pixels[i]))/3;
					green = gray + 64;
					if(green>255) green = 255;
					pixels[i] = Color.argb(alpha, gray, green, gray);
				}
				if(my<h){
					my += delta;
					if(my>h) my = h;
				}
			}
		}
		else {
			double mx = ox;
			double delta = Math.tan(2.0*Math.PI - angle);
			for(int y=oy; y>=0; y--){
				for(int x=ox-1; x>=mx; x--){
					i = y*w + x;				
					alpha = Color.alpha(pixels[i]);
					gray  = (Color.red(pixels[i]) + Color.green(pixels[i]) + Color.blue(pixels[i]))/3;
					green = gray + 64;
					if(green>255) green = 255;
					pixels[i] = Color.argb(alpha, gray, green, gray);
				}
				if(mx>0){
					mx -= delta;
					if(mx<0) mx = 0;
				}
			}			
		}
		
		/*
		for(int y=0; y<mh; y++){
			for(int x=0; x<w; x++){
				int i = y*w + x;
				
				int alpha = Color.alpha(pixels[i]);
				int gray  = (Color.red(pixels[i]) + Color.green(pixels[i]) + Color.blue(pixels[i]))/6;
				pixels[i] = Color.argb(alpha, gray, gray, gray); //(pixels[i] & 0xffff00ff) | (green << 8);
			}
		}
		*/
		dst.setPixels(pixels, 0, w, 0, 0, w, h);			
	}

}
