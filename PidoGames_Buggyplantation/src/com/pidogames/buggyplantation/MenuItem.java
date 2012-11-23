package com.pidogames.buggyplantation;

import java.util.HashMap;
import com.pidogames.buggyplantation.effect.BackgroundBitmapEffect;
import com.pidogames.buggyplantation.effect.ClockTintEffect;
import com.pidogames.buggyplantation.entity.Entity;
import com.pidogames.buggyplantation.entity.block.Block;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;

public class MenuItem implements Comparable<MenuItem> {
	private static HashMap<Integer,Bitmap>  sprite_cache = new HashMap<Integer,Bitmap>();
	private static final int NOT_ACTIVATED = -1;

	public static final int PLACE_NOWHERE  = 0;
	public static final int PLACE_OVER	   = 1;
	public static final int PLACE_NEXT_TO  = 2;
	public static final int PLACE_TENDRILL = 3;
	
	private int icon_resid;
	private int sort_order;
	private Rect click_rect;
	private int top_filter_type;
	private GameMenu parent;
	
	private ClockTintEffect clockEffect;
	private static BackgroundBitmapEffect bg_effect;
	private static Bitmap menu_bg;
	
	public int getPlaceMode(){
		switch(icon_resid){
			case R.drawable.menu_poison:
			case R.drawable.menu_heal:
			case R.drawable.menu_roots:
			case R.drawable.b_core:
			case R.drawable.b_core_mine:
			case R.drawable.b_stalk2_ud:
			case R.drawable.b_shooter4:
			case R.drawable.b_shooter5:
			case R.drawable.b_shooter6:
			case R.drawable.menu_rotate:
			case R.drawable.menu_delete:
			case R.drawable.menu_sear:
			case R.drawable.menu_cm_explode:
			case R.drawable.b_flytrap:
			case R.drawable.b_flytrap_closed:
			case R.drawable.i_dandelion:
			case R.drawable.i_seabean:
			case R.drawable.menu_sprouting_seed:
				return PLACE_NOWHERE;
			case R.drawable.b_sprout_d:
				return PLACE_NEXT_TO;
			case R.drawable.b_tendrill_sprout_d:
				return PLACE_TENDRILL;
			default:
				return PLACE_OVER;
		}
	}
	
	public boolean isEnabled(){
		if(this.getParent()==null) return true;
		
		int place_mode = getPlaceMode();
		Scene scene = SceneView.getScene();
		Entity e = this.getParent().getParent();
		if(e instanceof Block) {
			Block b = (Block)e;
			switch(place_mode){
				case PLACE_OVER: {
					int level = b.getLevel();
					if(level==scene.getLevels()-1) return false;
					if(scene.getBlock(level+1, b.getX(), b.getY())!=null) return false;				
					return true;
				}
				case PLACE_NEXT_TO: {
					int dirs = b.getAllowedDirections(icon_resid);
					return (dirs != 0);
				}
				case PLACE_TENDRILL: {
					int dirs = b.getAllowedDirections(icon_resid);
					int type = b.getType();
					if(dirs!=0){
						if(type==R.drawable.b_tendrill_sprout_d
						|| type==R.drawable.b_tendrill_sprout_u
						|| type==R.drawable.b_tendrill_sprout_l
						|| type==R.drawable.b_tendrill_sprout_r){
							return true;
						}
						else {
							int level = b.getLevel();
							if(level==scene.getLevels()-1) return false;
							if(scene.getBlock(level+1, b.getX(), b.getY())!=null) return false;
							return true;
						}
					}
					else {
						return false;
					}
				}
				case PLACE_NOWHERE: {
					return true;
				}
			}
			
			return false;
		}
		else {
			return true;
		}
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof MenuItem){
			return icon_resid==((MenuItem)o).getIconResid();
		}
		else return super.equals(o);
	}
	
	@Override
	public int hashCode(){
		return icon_resid;
	}
	
	public MenuItem(int icon_resid, int top_filter_type, int sort_order){
		this(null, icon_resid, top_filter_type, sort_order);
	}
	
	public MenuItem(GameMenu parent, int icon_resid, int top_filter_type, int sort_order){
		this.icon_resid = icon_resid;
		this.parent = parent;
		this.top_filter_type = top_filter_type;
		this.sort_order = sort_order;
	}
	
	public int getTopFilterType(){
		return top_filter_type;
	}
	
	public void setClickRect(Rect rect){
		click_rect = rect;
	}
	
	public Rect getClickRect(){
		return click_rect;
	}
	
	public GameMenu getParent(){
		return parent;
	}
	
	public void setParent(GameMenu parent){
		this.parent = parent;
	}
	
	public int [] getPrice(){
		return Price.getInstance().get(icon_resid);
	}
	
	private int getHashKey(boolean enabled){
		return enabled?icon_resid:icon_resid + 5000;
	}	
	
	public Bitmap getIcon(long tic, double building) {
		synchronized(sprite_cache){
			
			if(menu_bg==null) {
				Context context = SceneView.getInstance(null).getContext();
				menu_bg = BitmapFactory.decodeResource(context.getResources(), R.drawable.menu_bg);
		        final float scale = GameMenu.getScale(); //context.getResources().getDisplayMetrics().density;
				if(scale!=1.0) menu_bg = Bitmap.createScaledBitmap(menu_bg, (int)(menu_bg.getWidth()*scale), (int)(menu_bg.getHeight()*scale), false);
				
				bg_effect = new BackgroundBitmapEffect();
				bg_effect.setBackground(menu_bg);
			}
			
			int hash_key = getHashKey(building!=0);
			Bitmap bm = sprite_cache.get(hash_key);
			if(bm==null) {				
				Context context = SceneView.getInstance(null).getContext();
		        //float scale = GameMenu.getScale();
				bm = BitmapFactory.decodeResource(context.getResources(), icon_resid);
				
				int w = bm.getWidth();
				int h = bm.getHeight();
				int bw = menu_bg.getWidth();
				int bh = menu_bg.getHeight();
				
				float w_scale = 1;
				//if(w>bw) 
				w_scale = (float)bw/(float)w;
				float h_scale = 1;
				//if(h>bh) 
				h_scale = (float)bh/(float)h;
				
				//if(w_scale<scale) 
				float scale = w_scale;
				if(h_scale<scale) scale = h_scale;
				
				if(scale!=1.0) {
					//Log.d("LOAD MENU ITEM ","LMI: "+icon_resid+", sw:"+w_scale+",sh:"+h_scale);
					//Log.d("LOAD MENU ITEM ","LMI: "+icon_resid+", scale:"+scale+", w:"+w+",h:"+h+", bw:"+bw+", bh:"+bh);
					bm = Bitmap.createScaledBitmap(bm, (int)(w*scale), (int)(h*scale), false);					
					w = bm.getWidth();
					h = bm.getHeight();
					//Log.d("LOAD MENU ITEM ","LMI: "+icon_resid+", scale:"+scale+", w:"+w+",h:"+h);
				}
				else bm = bm.copy(Config.ARGB_8888, true);
				
				if(w!=bw || h!=bh){
					
					int [] p = new int[w*h];
					bm.getPixels(p, 0, w, 0, 0, w, h);
					bm.recycle();
					
					Bitmap bm2 = Bitmap.createBitmap(bw, bh, Config.ARGB_8888);
					int [] bp = new int[bw*bh];
					int ox = (bw - w)/2;
					int oy = (bh - h)/2;
					for(int y=0; y<h; y++){
						for(int x=0; x<w; x++){
							//Log.e("t", "bh:"+bh+", h:"+h);
							//Log.e("t", "OX:"+ox+", OY:"+oy);
							//Log.e("t", "i2:"+(y+oy)*bw + x + ox);
							//Log.e("t", "im:"+bw*bh);
							bp[(y+oy)*bw + x + ox] = p[y*w + x];
						}
					}
					bm2.setPixels(bp, 0, bw, 0, 0, bw, bh);
					bm = bm2;					
				}
				
				bg_effect.applyEffect(bm);
				
				if(building==0){
					if(clockEffect==null) clockEffect = new ClockTintEffect();
					clockEffect.setAngle(0);
					clockEffect.applyEffect(bm);
				}
				
				sprite_cache.put(hash_key, bm);
			}
			
			if(building>0 && building<1){
				bm = bm.copy(Config.ARGB_8888, true);
				if(clockEffect==null) clockEffect = new ClockTintEffect();
				clockEffect.setAngle(building*2.0*Math.PI);
				clockEffect.applyEffect(bm);				
			}
			
			
			/*
			if(last_activated!=NOT_ACTIVATED){
				if(tic-last_activated<getBuildTime()){
					bm = bm.copy(Config.ARGB_8888, true);
					if(clockEffect==null) clockEffect = new ClockTintEffect();
					clockEffect.setAngle((double)(tic-last_activated)/(double)getBuildTime()*2.0*Math.PI);
					clockEffect.applyEffect(bm);
				}
			}
			*/
			
			return bm;
		}
	}
	
	public int getIconResid() {return icon_resid;}
	
	public static void clearCache(){
		sprite_cache.clear();
	}

	@Override
	public int compareTo(MenuItem another) {
		if(another!=null) return sort_order<another.sort_order?-1:sort_order==another.sort_order?0:1;
		return 0;
	}
	
}
