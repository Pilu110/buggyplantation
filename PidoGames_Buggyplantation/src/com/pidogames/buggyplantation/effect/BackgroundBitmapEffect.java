package com.pidogames.buggyplantation.effect;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.util.Log;

public class BackgroundBitmapEffect extends Effect {
	
	//Bitmap bg;
	private int [] bg_pixels;
	private float ar;
	
	public BackgroundBitmapEffect(){
		super();
		ar = 1;
	}
	
	public void setAlphaRate(float ar){
		if(ar>1) ar=1;
		else if(ar<0) ar = 0;
		this.ar = ar;
	}
	
	public void setBackground(Bitmap bg){
		//this.bg = bg;
		//bg = bg.copy(Config.ARGB_8888, true);
		int w = bg.getWidth();
		int h = bg.getHeight();
		bg_pixels = new int[w*h];
		bg.getPixels(bg_pixels, 0, w, 0, 0, w, h);		
	}
	
	@Override
	public void applyEffect(Bitmap dst) {
		int w = dst.getWidth();
		int h = dst.getHeight();
		
		int [] pixels = new int[w*h];
		dst.getPixels(pixels, 0, w, 0, 0, w, h);
		
		for(int i=0; i<w*h; i++){
			int alpha = (int)(Color.alpha(pixels[i])*ar);
			int red   = Color.red(pixels[i]);
			int green = Color.green(pixels[i]);
			int blue  = Color.blue(pixels[i]);
			if(alpha<255){
				int bg_alpha = Color.alpha(bg_pixels[i]);
				int bg_red   = Color.red(bg_pixels[i]);
				int bg_green = Color.green(bg_pixels[i]);
				int bg_blue  = Color.blue(bg_pixels[i]);
				if(alpha==0){
					alpha = bg_alpha;
					red   = bg_red;
					green = bg_green;
					blue  = bg_blue;
				}
				else {
					red   = (alpha*red + (255 - alpha)*bg_red) / 255;
					green = (alpha*green + (255 - alpha)*bg_green) / 255;
					blue  = (alpha*blue + (255 - alpha)*bg_blue) / 255;
					alpha = 255;
				}
			}
			pixels[i] = Color.argb(alpha, red, green, blue); //(pixels[i] & 0xffff00ff) | (green << 8);
		}
		
		dst.setPixels(pixels, 0, w, 0, 0, w, h);
	}

}
