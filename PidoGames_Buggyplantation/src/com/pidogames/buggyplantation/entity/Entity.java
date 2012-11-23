package com.pidogames.buggyplantation.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONException;
import org.json.JSONObject;


import com.pidogames.buggyplantation.Coord;
import com.pidogames.buggyplantation.GameMenu;
import com.pidogames.buggyplantation.R;
import com.pidogames.buggyplantation.ResIdConverter;
import com.pidogames.buggyplantation.Scene;
import com.pidogames.buggyplantation.SceneView;
import com.pidogames.buggyplantation.effect.Effect;
import com.pidogames.buggyplantation.effect.EffectListener;
import com.pidogames.buggyplantation.effect.LightMultiplyEffect;
import com.pidogames.buggyplantation.entity.block.Block;
import com.pidogames.buggyplantation.entity.item.AnimatingItem;
import com.pidogames.buggyplantation.entity.item.Item;
import com.pidogames.buggyplantation.entity.item.bug.Bug;
import com.pidogames.buggyplantation.interfaces.Constants;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Bitmap.Config;
import android.util.Log;
import android.widget.Toast;

public abstract class Entity implements EffectListener, Constants {
	
	public static final int PLAYER_PLANT   = 0;
	public static final int PLAYER_BUG     = 1;
	public static final int PLAYER_NEUTRAL = 2;
	
	public static final int [] PLAYERS = {
		PLAYER_PLANT,
		PLAYER_BUG,
		PLAYER_NEUTRAL,		
	};
	
	public static final int ALIGN_NEUTRAL = 0;
	public static final int ALIGN_ENEMY   = 1;
	public static final int ALIGN_ALLY    = 2;	

	public static final int FOCUS_NONE    = 0;
	public static final int FOCUS_BLOCK   = 1;
	public static final int FOCUS_FOOD    = 2;
	public static final int FOCUS_BRIDGE  = 3;
	public static final int FOCUS_TUNNEL  = 4;
	
	public static final int MOVING_SWIM  = 0;
	public static final int MOVING_FLOAT = 1;
	public static final int MOVING_MOVE  = 2;
	public static final int MOVING_FLY   = 3;
	
	
	public static final char FLAG_EFFECTS  = 1;
	
	public static final char BITMAP_NORMAL    = 0xffff;
	public static final char BITMAP_NOEFFECTS = BITMAP_NORMAL & ~FLAG_EFFECTS;
	
	public static final int MAX_BITMAP_RADIUS = 200;
	
	protected static Random rnd = new Random(System.currentTimeMillis());
	
	//private static Map<String,SoftReference<Bitmap>> sprite_cache = Collections.synchronizedMap(new HashMap<String,SoftReference<Bitmap>>());
	//private static ReferenceQueue<Bitmap> ref_queue = new ReferenceQueue<Bitmap>();
	private static ImageCache sprite_cache = new ImageCache(4 * 1024 * 1024);
	
	private static HashMap<String, HashSet<Class>> registered_json_key = new HashMap<String, HashSet<Class>>();
	protected static double zoom = 1.0;
	
	public static LightMultiplyEffect light_effect = new LightMultiplyEffect(1);
	
	public  static final String JSON_ID = registerJSONKey("id", Entity.class);
	public  static final String JSON_IS_BLOCK = registerJSONKey("isb", Entity.class);
	
	public  static final String JSON_INSTANCEOF = registerJSONKey("i",  Entity.class);
	public  static final String JSON_LEVEL      = registerJSONKey("l",  Entity.class);
	public  static final String JSON_HITPOINTS  = registerJSONKey("h",  Entity.class);
	private static final String JSON_ANGLE      = registerJSONKey("a",  Entity.class);
	private static final String JSON_PLAYER     = registerJSONKey("p",  Entity.class);
	public  static final String JSON_MENU		= registerJSONKey("mn", Entity.class);
	
	public  static final int JSON_CLASS_PLANT    = 1;
	public  static final int JSON_CLASS_SHOOTER  = 2;
	public  static final int JSON_CLASS_WATER    = 3;
	public  static final int JSON_CLASS_GALL     = 4;
	public  static final int JSON_CLASS_BAIT     = 5;
	public  static final int JSON_CLASS_FLYTRAP  = 6;
	public  static final int JSON_CLASS_CORE     = 7;
	public  static final int JSON_CLASS_TENDRILL = 8;
	
	public  static final int JSON_CLASS_NEST    = 11;
	public  static final int JSON_CLASS_MISSILE = 12;
	public  static final int JSON_CLASS_BUG     = 13;
	public  static final int JSON_CLASS_SEABEAN = 14;
	public  static final int JSON_CLASS_DANDELION = 15;
	public  static final int JSON_CLASS_SEED    = 16;
	public  static final int JSON_CLASS_SHRAPNEL = 17;
	public  static final int JSON_CLASS_BOUNCER = 18;
	public  static final int JSON_CLASS_ANIMATING = 19;
	public  static final int JSON_CLASS_ANT     = 20;
	public  static final int JSON_CLASS_ANT_WORKER = 21;
	public  static final int JSON_CLASS_ANT_QUEEN  = 22;
	public  static final int JSON_CLASS_EGG     = 23;
	public  static final int JSON_CLASS_DISPLAYED_TYPE_ITEM = 24;
	
	private ConcurrentHashMap<Class,Effect> effect = new ConcurrentHashMap<Class,Effect>();	
	protected float hitpoints;
	protected double angle;	
	
	protected int player;
	
	protected Entity(JSONObject o, ResIdConverter rc) throws JSONException {
		if(!o.isNull(JSON_HITPOINTS)) hitpoints = (float)o.getDouble(JSON_HITPOINTS);
		else hitpoints = getMaxHitPoints();
		
		if(!o.isNull(JSON_ANGLE)) angle = o.getDouble(JSON_ANGLE);
		else angle = 0;
		
		if(!o.isNull(JSON_PLAYER)) player = o.getInt(JSON_PLAYER);
		else player = PLAYER_NEUTRAL;
	}
	
	protected Entity(int player){
		hitpoints = getMaxHitPoints();
		this.player = player;
	}
	
	// only for Block and Item subclasses
	public static Entity getEntityFromJSON(JSONObject o, ResIdConverter rc) throws JSONException {
		if(!o.isNull(JSON_IS_BLOCK) && o.getBoolean(JSON_IS_BLOCK)){
			return Block.getBlockFromJSON(o, rc);
		}
		else return Item.getItemFromJSON(o, rc);
	}
	
	public static final String registerJSONKey(String json, Class c) {
		if(DEBUG){
			Log.d("registerJSONKey", json+", "+c);
			if(registered_json_key.containsKey(json)){
				HashSet<Class> s = registered_json_key.get(json);
				String str_c = null;
				boolean conflict = false;
				
				for(Class cl : s) {
					str_c = ((str_c==null)? cl.getName() : (str_c + ',' + cl.getName()));
					if (cl.isAssignableFrom(c) || c.isAssignableFrom(cl)){
						conflict = true;
					}
				}
				
				s.add(c);
				if(conflict){
					String msg = json+", "+c+" : JSON key already used by "+str_c+" class.";
					Log.e("registerJSONKey", msg);
					SceneView sw = SceneView.getInstance(null);
					if(sw!=null) Toast.makeText(sw.getContext(), msg, Toast.LENGTH_LONG).show();
				}
			}
			else {
				HashSet<Class> s = new HashSet<Class>();
				s.add(c);
				registered_json_key.put(json, s);
			}
		}
		return json;
	}
	
	public boolean isPoisonous(){
		return false;
	}
	
	public int getPlayer(){
		return player;
	}
	
	public int getPlayerColor(){
		switch(player){
			case PLAYER_NEUTRAL: return Color.WHITE;
			case PLAYER_PLANT: return Color.GREEN;
			case PLAYER_BUG: return Color.RED;
		}
		return Color.BLACK;
	}
	
	public abstract int getMaxHitPoints();
	public abstract boolean hasMenu();
	public abstract GameMenu getMenu();
	public abstract void initMenuFromJSON(JSONObject o, ResIdConverter rc) throws JSONException;
	public abstract int getLevel();
	public abstract int getType();
	public abstract String getTypeName();
	
	protected boolean hasDamageEffect(){
		return true;
	}
	
	public void damage(int hitpoints, Entity e, int player){
		this.hitpoints -= hitpoints;
		
		if(e!=null && e.hasDamageEffect()){
			Coord c = getPosition();
			int rd = rnd.nextInt(30);
			double rad = 2.0*Math.PI/360.0 * rnd.nextInt(360);
			c.x += (rd*Math.sin(rad));
			c.y -= (rd*Math.cos(rad));
			SceneView.getScene().addItem(new AnimatingItem(PLAYER_NEUTRAL, R.drawable.i_damage1, AIR_LEVEL, c.x, c.y, true), false);
		}
		
		//killed
		if(this.hitpoints<=0 && this.hitpoints+hitpoints>0){
			Scene scene = SceneView.getScene();
			if(this instanceof Bug){
				scene.addStatValue(this.player, Scene.STAT_LOST_UNIT, 1);
				scene.addStatValue(player, Scene.STAT_KILL, 1);
			}
			else {
				scene.addStatValue(this.player, Scene.STAT_LOST_BLOCK, 1);
				scene.addStatValue(player, Scene.STAT_RAZE, 1);
			}
		}
	}
	
	public void setHitpoints(int hitpoints){
		this.hitpoints = hitpoints;
	}
	
	public int getHitPoints(){
		return (int)hitpoints;
	}
	
	public float getHitPointsRate(){
		return hitpoints / getMaxHitPoints();
	}
	
	public void setAngle(double angle){
		while(angle>=360) angle -= 360;
		while(angle<0) angle += 360;
		this.angle = angle;
	}
	
	public double getAngle(){
		return angle;
	}
		
	@Override
	public void OnEffectEnd(Effect e) {
		clearEffect(e.getClass());
	}
	
	public void addEffect(Effect e){
		e.setEffectListener(this);
		effect.put(e.getClass(),e);
	}
	
	public boolean hasEffect(Class e){
		return effect.containsKey(e);
	}
	
	public void clearEffect(Class ec){
		effect.remove(ec);
	}
	
	public void invertEffect(Class ec){
		Effect e = effect.get(ec);
		if(e!=null) e.invertEnabled();
	}
	
	public void clearEffects(){
		effect.clear();
	}
			
	private Bitmap applyEffect(Bitmap src){
		Bitmap dst=null;
		dst = src.copy(Config.ARGB_8888, true);
		for(Effect e : effect.values()) e.applyEffect(dst);
		return dst;
	}
	
	public static void setBrightness(float brightness){
		light_effect.setFactor(brightness*2.0f);
	}

	public static void setZoom(double zoom){
		Entity.zoom = zoom;
		clearCache();
	}
	
	public static double getZoom(){
		return zoom;
	}
	
	public static void clearCache() {
		sprite_cache.evictAll();
	}	
	
	/*
	public static void clearCacheWithPrefix(String prefix) {
		ArrayList<String> to_remove = new ArrayList<String>(); 
		synchronized(sprite_cache){
			for(String key : sprite_cache.keySet()){
				if(key.startsWith(prefix)){
					to_remove.add(key);
				}
			}
			for(String r : to_remove) sprite_cache.remove(r);
		}
	}
	*/	
		
	public static int getAlignment(int p1, int p2){
		if(p2==PLAYER_NEUTRAL) return ALIGN_NEUTRAL;
		else if(p1 == p2) return ALIGN_ALLY;
		else return ALIGN_ENEMY;		
	}
	
	public int getAlignment(Entity e){
		return getAlignment(player,e.player);
	}

	
	protected abstract String getBaseHashKey();
	protected abstract String getHashKey();
	protected abstract Bitmap loadBitmap();
	public abstract boolean isRotationCached();
	public abstract void step(long tic);
	
	protected Bitmap loadBitmapResource(int resId) {
		return BitmapFactory.decodeResource(SceneView.getInstance(null).getResources(), resId);
		/*
		Bitmap bm = sprite_cache.get("RES_"+resId);
		if(bm==null || bm.isRecycled()){
			bm = BitmapFactory.decodeResource(SceneView.getInstance(null).getResources(), resId);
			sprite_cache.put("RES_"+resId, bm);
		}
		return bm;
		*/
	}
	
	public double getMaxBitmapD2(){
		String base_hash_key = getBaseHashKey();
		Bitmap dst = sprite_cache.get(base_hash_key);
		if(dst==null) dst = getBitmap(BITMAP_NOEFFECTS);
		
		double w = dst.getWidth();
		double h = dst.getHeight();
		return (w*w + h*h);
	}
	
	public int getFocusObject(Entity e){
		return getFocusObject(e, false);
	}
	
	public int getFocusObject(Entity e, boolean attacked){
		return FOCUS_NONE;
	}
	
	public int getMovingType() {
		return MOVING_MOVE;
	}
	
	public int getCrosshairColor(int player){
		int color;
		switch(Entity.getAlignment(player, this.player)){
			case ALIGN_ALLY:  color=0x80008000; break;
			case ALIGN_ENEMY: color=0x80800000; break;
			default:		  color=0x80e6ae16; break;
		}
		return color;
	}
	
	public int getColor(){
		Bitmap bm = getBitmap(BITMAP_NOEFFECTS);
		int w = bm.getWidth();
		int h = bm.getHeight();
		
		int [] pixels = new int[w*h];
		bm.getPixels(pixels, 0, w, 0, 0, w, h);
		
		long r = 0;
		long g = 0;
		long b = 0;
		long n = 0;
		for(int i=0; i<pixels.length; i++){
			if(Color.alpha(pixels[i])==255){
				r += Color.red(pixels[i]);
				g += Color.green(pixels[i]);
				b += Color.blue(pixels[i]);
				n++;
			}
		}
		
		return (n>0)?Color.rgb((int)(r/n), (int)(g/n), (int)(b/n)):0;
	}
		
	public Bitmap getBitmap(char flag){
		boolean rc = isRotationCached();
		Bitmap dst;
		synchronized(sprite_cache){
			String hash_key = getHashKey();
			dst = sprite_cache.get(hash_key);
			
			if(dst==null || (!rc && angle!=0.0)){
				String base_hash_key = getBaseHashKey();
				dst = sprite_cache.get(base_hash_key);
				
				if(dst==null){
					dst = loadBitmap();
					
					if(light_effect.getFactor()!=1.0f){
						dst = dst.copy(Config.ARGB_8888, true);
						light_effect.applyEffect(dst);
					}
					
					if(zoom!=1.0 /*|| grayscale_mode*/) {
						int dw = (int)(dst.getWidth()*zoom);
						int dh = (int)(dst.getHeight()*zoom);
						if(dw<1) dw = 1;
						if(dh<1) dh = 1;
						dst = Bitmap.createScaledBitmap(dst, dw, dh, false);
					}
					//if (grayscale_mode) grayscale.applyEffect(dst);
					
					//check if base hash key is changed
					String valid_bhk = getBaseHashKey();
					if(base_hash_key.equals(valid_bhk)) {
						sprite_cache.put(base_hash_key, dst);
					}
					else {
						rc = false;
					}
				}
				
				if(angle!=0.0 && dst.getWidth()>0 && dst.getHeight()>0){
			        Matrix matrix = new Matrix();
			        //matrix.postScale((float)zoom, (float)zoom);
			        
			        if(rc){
						int offset_x = (int)(2.0*angle) / 45;
				        matrix.postRotate((float)(offset_x*22.5));
			        }
			        else
				        matrix.postRotate((float)angle);
			        
			        dst = Bitmap.createBitmap(dst, 0, 0, dst.getWidth(), dst.getHeight(), matrix, true);
				}
				//else if(zoom!=1.0) dst = Bitmap.createScaledBitmap(dst, (int)(dst.getWidth()*zoom), (int)(dst.getHeight()*zoom), false);
				if(rc) {
					sprite_cache.put(hash_key, dst);
				}
			}

		}
		
		if(!effect.isEmpty() && ((flag & FLAG_EFFECTS)!=0)) dst = applyEffect(dst);
		return dst;		
	}
	
	public Coord getRandomPosition(){
		return getPosition();
	}
	
	public Coord getPosition(){
		return null;
	}
	
	public boolean isCollision(int x, int y){
		Bitmap bm = getBitmap(BITMAP_NOEFFECTS);
		Coord p = getPosition();
		int dx = (int)((p.x - x)*zoom);
		int dy = (int)((p.y - y)*zoom);
		int w = bm.getWidth();
		int h = bm.getHeight();
		int px = w/2 - dx;
		int py = h/2 - dy;
		if(px>=0 && py>=0 && px<w && py<h){
			int color = bm.getPixel(px, py);
			return (color & 0xff000000)==0xff000000;
		}
		else return false;
	}
	
	public JSONObject toJSON(ResIdConverter rc) throws JSONException {
		JSONObject o = new JSONObject();
		if(hitpoints!=getMaxHitPoints()) o.put(JSON_HITPOINTS, hitpoints);
		if(angle!=0) o.put(JSON_ANGLE, (int)angle);
		if(player!=PLAYER_NEUTRAL) o.put(JSON_PLAYER, player);
		if(hasMenu()) o.put(JSON_MENU, getMenu().toJSON(rc));
		return o;
	}
	
	
	public void payBackPrice(int [] prices){
	}
	
	public int payPrice(int [] prices){
		for(int i=0; i<prices.length; i++){
			if(prices[i]>0) return i;
		}
		return -1;
	}
	
	public void referenciesFromJSON(JSONObject o, HashMap<Integer, Entity> tmp_ids, ResIdConverter rc) throws JSONException {
		if(hasMenu()) getMenu().referenciesFromJSON(o, tmp_ids, rc);
	}
	
	public JSONObject referenciesToJSON(HashMap<Entity, Integer> tmp_ids) throws JSONException{
		if(hasMenu())	return getMenu().referenciesToJSON(tmp_ids);
		else			return null;
	}
		
}
