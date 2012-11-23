package com.pidogames.buggyplantation.effect;

import java.util.Random;

import com.pidogames.buggyplantation.Coord;
import com.pidogames.buggyplantation.entity.Entity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

public class HealingEffect extends Effect {
	
	private static final int COLOR = 0xff404080;
	private static final int DELAY = 2;
	private static final byte [][][] MASK = {
		//STEP1
		{{1}},
		
		//STEP2
		{
			{0,1,0},
			{1,2,1},
			{0,1,0}
		},
		
		//STEP3
		{
			{0,0,1,0,0},
			{0,1,2,1,0},
			{1,2,3,2,1},
			{0,1,2,1,0},			
			{0,0,1,0,0}
		},
		
		//STEP4
		{
			{0,0,0,1,0,0,0},
			{0,0,0,2,0,0,0},
			{0,0,1,2,1,0,0},
			{1,2,2,3,2,2,1},
			{0,0,1,2,1,0,0},			
			{0,0,0,2,0,0,0},
			{0,0,0,1,0,0,0}
		}
	};
	
	private static final int MW = 7;
	private static final int MH = 7;
	
	
	private static Random rnd;
	
	private Coord [] stars;
	private int [] phase;
	private boolean [] dir;
	private int duration;
	private boolean init;
	public HealingEffect(int n, int duration){
		super();
		stars = new Coord[n];
		for(int i=0; i<stars.length; i++) {
			stars[i] = new Coord(0,0);
		}
		phase = new int[n];
		dir   = new boolean[n];
		if(rnd==null) rnd = new Random(System.currentTimeMillis());
		this.duration = duration;
		init = false;
	}
	
	@Override
	public void applyEffect(Bitmap dst) {
		if(!enabled || duration<=0 || (dst==null)) return;
		int w = dst.getWidth();
		int h = dst.getHeight();
		int [] pixels = new int[w*h];
		dst.getPixels(pixels, 0, w, 0, 0, w, h);
		
		for(int i=0; i<stars.length; i++){
			if(stars[i].x==0 && stars[i].y==0){
				if(!init){
					phase[i]=rnd.nextInt(MASK.length);
					dir[i]=rnd.nextBoolean();
				}
				else {
					phase[i]=0;
					dir[i]=true;
				}
				if(w>MW) stars[i].x=rnd.nextInt(w)-MW+1;
				if(h>MH) stars[i].y=rnd.nextInt(h)-MH+1;
			}
			
			int ox = stars[i].x; // - MASK[phase[i]][0].length/2;
			int oy = stars[i].y; //- MASK[phase[i]].length/2;
			int z = (int)Entity.getZoom();
			if(z<1) z=1;
			for(int y=0; y<z*MASK[phase[i]].length;y++){
				for(int x=0; x<z*MASK[phase[i]][0].length;x++){
					byte m = MASK[phase[i]][y/z][x/z];
					if(m>0){
						int o = (oy+y)*w+ox+x;
						if(o>=0 && o<pixels.length){
							int r = Color.red(pixels[o]) + Color.red(COLOR)*m;
							if(r>255) r=255;
							int g = Color.green(pixels[o]) + Color.green(COLOR)*m;
							if(g>255) g=255;
							int b = Color.blue(pixels[o]) + Color.blue(COLOR)*m;
							if(b>255) b=255;
							pixels[o] = Color.argb(255, r, g, b);
						}
					}
				}
			}
			
			if(duration%DELAY==0){
				if(dir[i]) {
					phase[i]++;
					if(phase[i]>=MASK.length){
						phase[i]--;
						dir[i] = false;
					}
				}
				else {
					phase[i]--;
					if(phase[i]<0) {
						phase[i]++;
						stars[i].x=0;
						stars[i].y=0;
						//dir[i] = true;				
					}
				}
			}
		}
		
		dst.setPixels(pixels, 0, w, 0, 0, w, h);
		
		init = true;
		duration--;
		if(duration<=0 && el!=null) el.OnEffectEnd(this);			
	}

}
