package com.pidogames.buggyplantation.entity.item;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;


import com.pidogames.buggyplantation.Coord;
import com.pidogames.buggyplantation.GameMenu;
import com.pidogames.buggyplantation.MenuItem;
import com.pidogames.buggyplantation.R;
import com.pidogames.buggyplantation.ResIdConverter;
import com.pidogames.buggyplantation.Scene;
import com.pidogames.buggyplantation.SceneView;
import com.pidogames.buggyplantation.effect.DisappearEffect;
import com.pidogames.buggyplantation.effect.Effect;
import com.pidogames.buggyplantation.entity.Entity;
import com.pidogames.buggyplantation.entity.item.bug.Ant;
import com.pidogames.buggyplantation.entity.item.bug.AntQueen;
import com.pidogames.buggyplantation.entity.item.bug.AntWorker;
import com.pidogames.buggyplantation.entity.item.bug.Bug;
import com.pidogames.buggyplantation.interfaces.Constants;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

public class Item extends Entity implements Constants {
		
	private static final String JSON_TYPE  = registerJSONKey("t", Item.class);
	private static final String JSON_X = registerJSONKey("x", Item.class);
	private static final String JSON_Y = registerJSONKey("y", Item.class);
	private static final String JSON_SELECTABLE = registerJSONKey("sl", Item.class);
	
	private GameMenu menu;
	protected int type;
	protected double x;
	protected double y;
	private int level;
	private boolean selectable;
	
	/*
	public static void clearItemsFromCache() {
		clearCacheWithPrefix("I_");
	}
	*/
	
	public static Item getItemFromJSON(JSONObject o, ResIdConverter rc) throws JSONException {
		if(!o.isNull(JSON_INSTANCEOF)){
			int io = o.getInt(JSON_INSTANCEOF);
			switch(io){
				case JSON_CLASS_NEST:
					return new Nest(o, rc);
				case JSON_CLASS_MISSILE:
					return new Missile(o, rc);
				case JSON_CLASS_BUG:
					return new Bug(o, rc);
				case JSON_CLASS_ANT:
					return new Ant(o, rc);
				case JSON_CLASS_ANT_WORKER:
					return new AntWorker(o, rc);
				case JSON_CLASS_ANT_QUEEN:
					return new AntQueen(o, rc);
				case JSON_CLASS_EGG:
					return new Egg(o, rc);
				case JSON_CLASS_WATER:
					return new WaterItem(o, rc);
				case JSON_CLASS_SEABEAN:
					return new Seabean(o, rc);
				case JSON_CLASS_DANDELION:
					return new Dandelion(o, rc);
				case JSON_CLASS_SEED:
					return new Seed(o, rc);
				case JSON_CLASS_SHRAPNEL:
					return new Shrapnel(o, rc);
				case JSON_CLASS_BOUNCER:
					return new Bouncer(o, rc);
				case JSON_CLASS_ANIMATING:
					return new AnimatingItem(o, rc);
				case JSON_CLASS_DISPLAYED_TYPE_ITEM:
					return new DisplayedTypeItem(o, rc);
				default:
					return new Item(o, rc);
			}
		}
		else return new Item(o, rc);		
	}
	
	public Item(int player, int type, int level, int x, int y){
		super(player);
		this.type = type;
		this.x = x;
		this.y = y;
		this.level = level;
		this.selectable = true;
	}
	
	public Item(JSONObject o, ResIdConverter rc) throws JSONException{
		super(o, rc);
		type  = (this instanceof Bug)?o.getInt(JSON_TYPE):rc.typeToResId(o.getInt(JSON_TYPE));
		x	  = o.getInt(JSON_X);
		y	  = o.getInt(JSON_Y);
		level = o.getInt(JSON_LEVEL);
		if(!o.isNull(JSON_SELECTABLE)) selectable = o.getBoolean(JSON_SELECTABLE);
		else selectable = true;
	}
	
	public void setSelectable(boolean selectable){
		this.selectable = selectable;
	}
	
	public boolean isSelectable(){
		return selectable;
	}

	public double getX(){
		return x;
	}
	
	public double getY(){
		return y;
	}
		
	public void setXY(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	public int getSelectionRadius(){
		return (int)(Math.sqrt(getMaxBitmapD2())/4/zoom);
	}
	
	@Override
	public int getLevel(){
		return level;
	}
	
	public void setLevel(int level){
		int old_level = this.level;
		this.level = level;
		SceneView.getScene().changedLevel(old_level, this);
	}
	
	@Override
	public int getMaxHitPoints() {
		return 1000;
	}
	
	@Override
	public int getType(){
		return type;
	}
	
	@Override
	public String getTypeName(){
		return ResIdConverter.getNameForResid(type);
	}
	
	public int getDisplayedType(){
		return type;
	}	

	@Override
	protected String getBaseHashKey() {
		return "I_"+getDisplayedType()+"_0";
	}

	@Override
	protected String getHashKey() {
		//return "I_"+type+"_"+angle;
		int scaled_angle = (int)(2.0*angle) / 45;
		return "I_"+getDisplayedType()+"_"+scaled_angle;
	}

	@Override
	protected Bitmap loadBitmap() {
		return loadBitmapResource(getDisplayedType());
	}

	@Override
	public boolean isRotationCached() {
		return true;
	}

	@Override
	public void step(long tic) {
		if(hitpoints<=0 && getMaxHitPoints()>0) {
			SceneView.getScene().removeItem(this);
		}
	}

	@Override
	public int getFocusObject(Entity e, boolean attacked){
		if(hitpoints>0) {
			if(e.getAlignment(this)==ALIGN_ENEMY){
				return getMovingType()==MOVING_FLY ?
					((e instanceof Bug && ((Bug)e).isRanger()) ? FOCUS_FOOD : FOCUS_NONE):
					FOCUS_FOOD;
			}
			else {
				return FOCUS_BRIDGE;
			}
		}
		else return FOCUS_NONE;
	}
	
	@Override
	public JSONObject toJSON(ResIdConverter rc) throws JSONException {
		JSONObject o = super.toJSON(rc);
		o.put(JSON_TYPE, (this instanceof Bug)?type:rc.resIdToType(type));
		o.put(JSON_X, (int)x);
		o.put(JSON_Y, (int)y);
		o.put(JSON_LEVEL, level);
		if(!selectable) o.put(JSON_SELECTABLE, selectable);
		return o;
	}

	protected HashSet<MenuItem> getMenuItems(){
		HashSet<MenuItem> items = new HashSet<MenuItem>();		
		
		if(SceneView.getGameMode()==GAME_MODE_MAP_EDITOR) {
			SceneView.getScene().addMenuItemsTo(items, new int[]{
				R.drawable.menu_minus,
				R.drawable.menu_plus,
				R.drawable.menu_delete
			}, GameMenu.MENU_TF_ACTION, 0);
		}
		else {
			SceneView.getScene().addMenuItemsTo(items, new int []{
					R.drawable.menu_select_items,
					R.drawable.menu_move,
					R.drawable.menu_attack,
					R.drawable.menu_create_group
			}, GameMenu.MENU_TF_COMMAND, 0);			
		}
		
		return items;
	}
	
	@Override
	public boolean hasMenu() {
		return menu!=null;
	}
	
	@Override
	public GameMenu getMenu() {
		if(menu==null) menu = new GameMenu(this);
		if(!menu.isValid()) {
			menu.setItems(getMenuItems());
			menu.setValid(true);
		}
		return menu;
	}
	
	@Override
	public void initMenuFromJSON(JSONObject o, ResIdConverter rc) throws JSONException {
		getMenu().initMenuFromJSON(o, rc);
	}
	
	public void invalidateMenu(){
		if(menu!=null) menu.setValid(false);
	}
	
	@Override
	public Coord getPosition(){
		return new Coord((int)x,(int)y);
	}
	
	@Override
	public void OnEffectEnd(Effect e) {
		super.OnEffectEnd(e);
		if(e instanceof DisappearEffect){
			Scene scene = SceneView.getScene();
			scene.removeItem(this);
		}
	}

	public void forward(int d){
		double rad = 2.0*Math.PI/360.0 * angle;
		x += (d*Math.sin(rad));
		y -= (d*Math.cos(rad));
	}
}
