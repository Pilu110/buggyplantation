package com.pidogames.buggyplantation;

import java.util.ArrayList;
import java.util.Random;

import com.pidogames.buggyplantation.entity.Entity;
import com.pidogames.buggyplantation.entity.FlowerImage;
import com.pidogames.buggyplantation.entity.block.PlantBlock;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

public class FlowerField {
	
	private static final int MAX_FLOWERS = 1000;
	private static final int MAX_LIFE    = 100;
	private static final int DELAY_AFTER = 0;
	private static final int FADE_OUT    = 50;
	
	private static Random rnd = new Random(System.currentTimeMillis());
	
	private class Flower {
		Coord pos;
		int angle;
		long created_at;
		
		Flower(Coord pos, long created_at){
			this.pos = pos;
			this.created_at = created_at;
			this.angle = rnd.nextInt(360);
		}
		
	}
	
	private FlowerImage flowerImage;
	private ArrayList<Flower> flowers;
	private long last_added;
	private long tic;
	private int width;
	
	private double max_d2;
	
	public FlowerField(){
		flowerImage = new FlowerImage();
		flowers = new ArrayList<Flower>(MAX_FLOWERS/4);
		tic = 0;
		last_added = 0;
		width = 0;
		max_d2 = 0;
	}
	
	public void moveIt(){
		if(Entity.getZoom()!=1.0) Entity.setZoom(1.0);
		if(width>0){
			if(flowers.size()<MAX_FLOWERS && tic-last_added>DELAY_AFTER){
				do {
					Coord c = new Coord(rnd.nextInt(width),rnd.nextInt(width));
					if(max_d2>0){
						boolean found = false;
						for(Flower flower : flowers){
							if(flower.pos.d2(c)<max_d2) {
								found = true;
								break;
							}
						}
						if(found) continue;
					}
					flowers.add(new Flower(c, tic));
				} while(false);
			}
		}
		ArrayList<Flower> to_remove = new ArrayList<Flower>();
		for(Flower flower : flowers){
			if(tic-flower.created_at>MAX_LIFE+FADE_OUT) to_remove.add(flower);
		}
		flowers.removeAll(to_remove);
		
		tic++;
	}
	
	public void drawIt(Canvas canvas){
		if(width<=0){
			int width = canvas.getWidth();
			if(canvas.getHeight()>width) width = canvas.getHeight();
			this.width = width;
		}
		
		canvas.drawColor(0xff4a7723);
		
		Paint paint = new Paint();
		paint.setColor(0xffff0000);
		int i=0;
		for(Flower flower : flowers){
			i++;
			long life = tic-flower.created_at;
			if(life>MAX_LIFE) {
				int alpha = (int)((FADE_OUT + MAX_LIFE - life)/(float)FADE_OUT*255);
				if(alpha<0) alpha = 0;
				paint.setAlpha(alpha);
			}
			else paint.setAlpha(255);
			if(life>MAX_LIFE) life = MAX_LIFE;
			
			//canvas.drawCircle(flower.pos.x, flower.pos.y, (life/(float)MAX_LIFE)*20, paint);
			flowerImage.setAngle(flower.angle);
			flowerImage.setAnimPhase((int)(life/2));
			Bitmap bm = flowerImage.getBitmap(Entity.BITMAP_NOEFFECTS);
			if(max_d2==0) max_d2 = flowerImage.getMaxBitmapD2();
			canvas.drawBitmap(bm, flower.pos.x, flower.pos.y, paint);
		}
	}
}
