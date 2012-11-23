package com.pidogames.buggyplantation.entity.item.bug;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Stack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.pidogames.buggyplantation.Coord;
import com.pidogames.buggyplantation.GameMenu;
import com.pidogames.buggyplantation.Poison;
import com.pidogames.buggyplantation.R;
import com.pidogames.buggyplantation.ResIdConverter;
import com.pidogames.buggyplantation.Scene;
import com.pidogames.buggyplantation.SceneView;
import com.pidogames.buggyplantation.SoundManager;
import com.pidogames.buggyplantation.Vector;
import com.pidogames.buggyplantation.R.drawable;
import com.pidogames.buggyplantation.effect.DisappearEffect;
import com.pidogames.buggyplantation.effect.Effect;
import com.pidogames.buggyplantation.effect.LightingEffect;
import com.pidogames.buggyplantation.effect.PoisonEffect;
import com.pidogames.buggyplantation.entity.Entity;
import com.pidogames.buggyplantation.entity.block.Block;
import com.pidogames.buggyplantation.entity.block.WaterBlock;
import com.pidogames.buggyplantation.entity.item.Item;
import com.pidogames.buggyplantation.entity.item.Shrapnel;
import com.pidogames.buggyplantation.entity.item.TurningItem;
import com.pidogames.buggyplantation.interfaces.Constants;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class Bug extends TurningItem implements Constants {
	
	public static final int MAX_BITMAP_RADIUS  = 20;	
	public static final int AUTO_ATTACK_RANGE  = 200;	
	public static final int AUTO_CHASE_RANGE   = 200;
	//public static final int MIN_DEST_REACH_D2 = Block.getAbsoluteWidth()*Block.getAbsoluteWidth();
	
	public static final int MOVING   = 0;
	public static final int EATING   = 1;
	public static final int SQUASHED = 2;
	public static final int STANDING = 3;
	public static final int GIVE_BIRTH = 4;
	
	public static final int REPEAT   = 1;
	public static final int PLAYBACK = 2;	
	
	public static final int TYPE_BUG    = 0;
	public static final int TYPE_ANT_WORKER = 1;
	public static final int TYPE_SCARAB = 2;
	public static final int TYPE_WASP   = 3;
	public static final int TYPE_ANT_QUEEN = 4;
	public static final int TYPE_ANT_SOLDIER = 5;
	public static final int TYPE_ANT_WINGED = 6;
	
	public static final Map<Integer, Integer> BUG_TYPE_FROM_RESID;
	static {
		HashMap<Integer, Integer> tempMap = new HashMap<Integer, Integer>();
		tempMap.put(R.drawable.bug1_moving1, TYPE_BUG);
		tempMap.put(R.drawable.bug_ant_moving1, TYPE_ANT_WORKER);
		tempMap.put(R.drawable.bug3_moving1, TYPE_SCARAB);
		tempMap.put(R.drawable.bug_wasp_moving1, TYPE_WASP);        
		tempMap.put(R.drawable.bug_ant_queen_moving1, TYPE_ANT_QUEEN);
		tempMap.put(R.drawable.bug_ant_soldier_moving1, TYPE_ANT_SOLDIER);
		tempMap.put(R.drawable.bug_ant_winged_moving1, TYPE_ANT_WINGED);
		BUG_TYPE_FROM_RESID = Collections.unmodifiableMap(tempMap);
	};	
	
	private static final int [][][] PHASES_RESID = {
		//bug
		{
			{
				R.drawable.bug1_moving1,
				R.drawable.bug1_moving2,
				R.drawable.bug1_moving3
			},
			{
				R.drawable.bug1_eating1,
				R.drawable.bug1_eating2
			},
			{
				R.drawable.bug1_squashed1
			},
			{
				R.drawable.bug1_moving2				
			},
		},
		
		//ant
		{
			{
				R.drawable.bug_ant_moving1,
				R.drawable.bug_ant_moving2,
				R.drawable.bug_ant_moving3
			},
			{
				R.drawable.bug_ant_moving1,
			},
			{
				R.drawable.bug_ant_squashed1,
			},
			{
				R.drawable.bug_ant_moving2				
			},
		},
		
		//scarab
		{
			{
				R.drawable.bug3_moving1,
				R.drawable.bug3_moving2,
				R.drawable.bug3_moving3
			},
			{
				R.drawable.bug3_moving2,
				R.drawable.bug3_eating2
			},
			{
				R.drawable.bug3_squashed1
			},
			{
				R.drawable.bug3_moving2
			},
		},
		
		//gall wasp
		{
			{
				R.drawable.bug_wasp_moving1,
				R.drawable.bug_wasp_moving2
			},
			{
				R.drawable.bug_wasp_moving1,
				R.drawable.bug_wasp_moving1,
				R.drawable.bug_wasp_moving2,
				R.drawable.bug_wasp_moving2
				//R.drawable.bug_wasp_eating1
			},
			{
				R.drawable.bug_wasp_squashed1
			},
			{
				R.drawable.bug_wasp_moving1,
				R.drawable.bug_wasp_moving1,
				R.drawable.bug_wasp_moving1,
				R.drawable.bug_wasp_moving2,
				R.drawable.bug_wasp_moving2,
				R.drawable.bug_wasp_moving2
				//R.drawable.bug_wasp_eating1
			}
		},
		
		//ant queen
		{
			{
				R.drawable.bug_ant_queen_moving1,
				R.drawable.bug_ant_queen_moving2,
				R.drawable.bug_ant_queen_moving3,
			},
			{
				R.drawable.bug_ant_queen_moving3,
				R.drawable.bug_ant_queen_eating2,
			},
			{
				R.drawable.bug_ant_queen_moving2
			},
			{
				R.drawable.bug_ant_queen_moving2
			},
			{ //give birth
				R.drawable.bug_ant_queen_moving3,
				R.drawable.bug_ant_queen_eating2,
			}
		},
		
		//ant soldier
		{
			{
				R.drawable.bug_ant_soldier_moving1,
				R.drawable.bug_ant_soldier_moving2,
			},
			{
				R.drawable.bug_ant_soldier_moving1,
			},
			{
				R.drawable.bug_ant_soldier_moving1,
			},
			{
				R.drawable.bug_ant_soldier_moving1,
			},
		},
		
		//ant winged
		{
			{
				R.drawable.bug_ant_winged_moving1,
				R.drawable.bug_ant_winged_moving2,
			},
			{
				R.drawable.bug_ant_winged_moving1,
				R.drawable.bug_ant_winged_moving1,
				R.drawable.bug_ant_winged_moving2,
				R.drawable.bug_ant_winged_moving2,
			},
			{
				R.drawable.bug_ant_winged_moving1,
			},
			{
				R.drawable.bug_ant_winged_moving1,
				R.drawable.bug_ant_winged_moving1,
				R.drawable.bug_ant_winged_moving1,
				R.drawable.bug_ant_winged_moving2,
				R.drawable.bug_ant_winged_moving2,
				R.drawable.bug_ant_winged_moving2,
			},
		}
	};
	
	private static final int [][] PHASE_REPEAT = {
		{PLAYBACK, REPEAT, REPEAT, REPEAT},
		{PLAYBACK, REPEAT, REPEAT, REPEAT},
		{PLAYBACK, REPEAT, REPEAT, REPEAT},
		{REPEAT, REPEAT, REPEAT, REPEAT},
		{PLAYBACK, REPEAT, REPEAT, REPEAT, REPEAT},
		{REPEAT, REPEAT, REPEAT, REPEAT},
		{REPEAT, REPEAT, REPEAT, REPEAT}
	};
	
	private static final int [] NAME_RESID = {R.string.bug_footer, R.string.bug_ant, R.string.bug_scarab, R.string.bug_wasp, R.string.bug_ant_queen,  R.string.bug_ant_soldier, R.string.bug_ant_winged};
	protected static final int [] SPEED = {5, 6, 1, 7, 2, 4, 6};
	protected static final int [] ATTACK_DAMAGE = {2,1,5,4,10,10, 2};
	private static final int [] ATTACK_PERIOD = {5,10,10,20,5,10, 10};
	private static final int [] ATTACK_RANGE  = {20,20,20,100,30,80, 100};
	private static final boolean [] IS_RANGER = {false,false,false,true,false,true,true};
	private static final int []    SPIT_RESID = {0,0,0,R.drawable.i_firespot_effect_a1,0,R.drawable.i_acid_effect_a1,R.drawable.i_acid_effect_a1};
	private static final int [] SIGHT_DISTANCE  = {10,15,10,10,10,10,15};
	private static final int [] SIGHT_ANGLE     = {30,30,30,30,30,30,30};
	private static final int [] MIN_DIRCHANGE   = {10,5,5,20,5,5,5};
	private static final int [] SMELL_DISTANCE  = {1000,1000,1000,1000,100,1000,1000};
	private static final int [] SELECTION_RADIUS = {25,15,45,20,45,25,20};
	private static final int [] SMELL_MULTIPLIER = {5, 10, 5, 2, 1, 10, 1};
	private static final int [] MOVING_TYPE = {MOVING_MOVE, MOVING_MOVE, MOVING_MOVE, MOVING_FLY, MOVING_MOVE, MOVING_MOVE, MOVING_FLY};
	
	private static final int MIN_SIGHT_STEP = 5;
	
	private static final String JSON_STATE  = registerJSONKey("s",Bug.class);
	private static final String JSON_ATTACK_DEST = registerJSONKey("ad",Bug.class);
	private static final String JSON_AUTO_COMMAND = registerJSONKey("ac",Bug.class);
	private static final String JSON_POISON = registerJSONKey("o",Bug.class);
	private static final String JSON_FOOD_MEMORY = registerJSONKey("m",Bug.class);
	private static final String JSON_DESTINATION = registerJSONKey("d",Bug.class);
	private static final String JSON_MARCH_INDEX = registerJSONKey("mi",Bug.class);
	
	protected int state;
	private boolean is_blocked;
	
	private Coord prev_coord;
	private int phase;	//animation phase	
	private int phase_dir; //animation direction
	
	private ArrayList<Poison> poison;
	
	private ArrayList<Coord> eye_sight_pts;
	public ArrayList<Coord> getEyeSightPts(){return eye_sight_pts; }
	
	private long eating_timer;

	private long dirchange_timer;
	private ArrayList<Entity> food_memory;
	private Entity last_turned_to;
	private Entity destination;
	private Stack<Entity> dest_path;
	private int dest_range_d2;
	private int march_index;
	private boolean attack_dest;
	private boolean auto_command;
		
	private static final int LEFT   = 0;
	private static final int MIDDLE = 1;
	private static final int RIGHT  = 2;	
	
	protected static Random rnd = new Random(System.currentTimeMillis());
	
	// the eyes of the bug
	private class Eye {

		private double angle;
		private int focus_object;
		private Entity focus_source;
		private double focus_distance;
		
		private Eye(double angle){
			this.angle = angle;
			focus_object = FOCUS_NONE;
			focus_source = null;
			focus_distance = 0.0;
		}
		
		public double getAngle(){
			return angle;
		}
		
		public int getFocusObject(){
			return focus_object;
		}
		
		public Entity getFocusSource(){
			return focus_source;
		}
		
		public double getFocusDistance(){
			return focus_distance;
		}
		
		private void see(){
			int bw = Block.getAbsoluteWidth();
			int bh = Block.getAbsoluteHeight();
			Scene scene = SceneView.getScene();
			double distance = 0.0;
			double ex, ey;
			boolean prev_coast = false;
			focus_object = FOCUS_NONE;
			Coord prev_eb = new Coord(-100,-100);
			int speed = SPEED[type];
			if(speed<MIN_SIGHT_STEP) speed = MIN_SIGHT_STEP;
			
			for(int i=0; i<SIGHT_DISTANCE[type]; i++){

				double rad = 2.0*Math.PI/360.0 * (Bug.this.angle + angle);
				ex = x + (distance*Math.sin(rad));
				ey = y - (distance*Math.cos(rad));
				if(DEBUG) eye_sight_pts.add(new Coord((int)ex,(int)ey));
				//if(ex>=0 && ey>=0)
				//scene.setBlock(STALK_LEVEL, (int)(ex/Block.getWidth()), (int)(ey/Block.getHeight()), new Block(R.drawable.b_rock));
				int ebx = (int)ex/bw;
				int eby = (int)ey/bh;
				if(ex<0 || ey<0 || ex>=scene.getWidth()*bw || ey>scene.getHeight()*bh) {
					focus_object = FOCUS_BLOCK;
					focus_source = null;
					focus_distance = distance;
				}
				else {
					if(prev_coast || prev_eb.x!=ebx || prev_eb.y!=eby) {
						prev_coast = false;
						if(ebx>=0 && ebx<scene.getWidth() && eby>=0 && eby<scene.getHeight()){
							Block b = scene.getHigherBlock(ebx, eby);
							if(b!=null){
								focus_object = b.getFocusObject(Bug.this);
								focus_source = b;
								focus_distance = distance;
								if(focus_object!=FOCUS_FOOD) removeFoodMemory(ebx,eby);
								//if(focus_object!=FOCUS_NONE) break;
								
								//water coast
								if(b instanceof WaterBlock && focus_object==FOCUS_BLOCK){
									Block ob = b.getOverlapBlock();
									if(ob!=null){
										if(ob.isCollision((int)ex, (int)ey)) {
											focus_object = FOCUS_NONE;
											focus_source = null;
										}
										prev_coast = true;
									}
								}
								//mount side
								else {
									int ot = b.getOverlapType();
									if(getMovingType()!=MOVING_FLY && ot>0 && Block.isOverlapTypeOf(R.drawable.b_mount, ot)){
										Block ob = b.getOverlapBlock();
										if(ob.isCollision((int)ex, (int)ey)) {
											focus_object = FOCUS_BLOCK;
											focus_source = null;
										}
										//prev_coast = true;
									}
								}								
							}
							else removeFoodMemory(ebx,eby);
						}
						else{
							focus_object = FOCUS_BLOCK;
							focus_source = null;
							focus_distance = distance;
						}
						
					}
					
					//ITEMS
					Map<Item,Boolean> items = scene.getItemsAt(ebx, eby);
					if(items!=null){
						Coord c = new Coord((int)ex,(int)ey);
						for(Item item : items.keySet()){
							if(item != Bug.this){
								int fo = item.getFocusObject(Bug.this);
								if(fo != FOCUS_NONE) {
									if(item.isCollision((int)ex, (int)ey)){
										if((fo!=FOCUS_BRIDGE) || (focus_source==null) || (focus_source.getLevel()<=item.getLevel())){
											focus_object   = fo;
											focus_source   = item;
											focus_distance = distance;
										}
									}
								}
								
							}
						}
					}
					
					
					if(focus_object==FOCUS_BRIDGE) focus_object = FOCUS_NONE;
					
					if(focus_object==FOCUS_NONE){
						prev_eb.x = ebx;
						prev_eb.y = eby;
					}
					
				}
				
				if(focus_object != FOCUS_NONE) break;
				distance += speed;
			}
		}
	}
	private Eye [] eye;

	public Bug(JSONObject o, ResIdConverter rc) throws JSONException {
		super(o, rc);
		if(!o.isNull(JSON_STATE)) state = o.getInt(JSON_STATE);
		else state = MOVING;
		
		if(!o.isNull(JSON_MARCH_INDEX)) march_index = o.getInt(JSON_MARCH_INDEX);
		else march_index = 0;
		
		if(!o.isNull(JSON_ATTACK_DEST)) attack_dest = o.getBoolean(JSON_ATTACK_DEST);
		else attack_dest = false;
		
		if(!o.isNull(JSON_AUTO_COMMAND)) auto_command = o.getBoolean(JSON_AUTO_COMMAND);
		else auto_command = false;
		
		poison = new ArrayList<Poison>();
		if(!o.isNull(JSON_POISON)){
			JSONArray json_poison = o.getJSONArray(JSON_POISON);
			for(int i=0; i<json_poison.length(); i++){
				poison.add(new Poison(json_poison.getJSONObject(i)));
			}
		}
		
		food_memory = new ArrayList<Entity>();
		
		init();
	}
	
	protected Bug(int player, int type, int x, int y, double angle){
		super(player, type, BUG_LEVEL, x, y);
		food_memory = new ArrayList<Entity>();
		poison = new ArrayList<Poison>();
		if(SceneView.getPlayer()==player) state = STANDING;
		else state = MOVING;
		destination = null;
		attack_dest = false;
		auto_command = false;
		march_index = 0;
		setAngle(angle);
		init();
	}
	
	private void init(){
		phase = 0;
		phase_dir = 0;
		prev_coord = new Coord((int)x,(int)y);
		last_turned_to = null;
		dirchange_timer = 0;
		eating_timer = 0;
		eye = new Eye[3]; // left, middle, right eye
		double eye_angle = -SIGHT_ANGLE[type];
		for(int i=0; i<eye.length; i++){
			eye[i] = new Eye(eye_angle);
			eye_angle += SIGHT_ANGLE[type];
		}		
	}
	
	public static Bug getInstance(int player, int type, int x, int y, double angle){
		switch(type){
			case TYPE_ANT_WORKER: {
				return new AntWorker(player, type,  x, y, angle);
			}
			case TYPE_ANT_SOLDIER: {
				return new Ant(player, type,  x, y, angle);				
			}
			case TYPE_ANT_QUEEN: {
				return new AntQueen(player, type,  x, y, angle);
			}
			default: {
				return new Bug(player, type,  x, y, angle);
			}
		}
	}
	
	public static Bug getInstance(int player, int type, Scene scene){
		Bug b = getInstance(player, type, 0, 0, rnd.nextInt(360));		
		boolean found = true;
		int x,y;
		do {
			found = true;
			x = rnd.nextInt(scene.getWidth());
			y = rnd.nextInt(scene.getHeight());			
			for(int level=0; level<scene.getLevels(); level++) if(scene.getBlock(level, x, y)!=null) {found=false; break;}
		} while(!found);
		
		b.prev_coord = new Coord(x,y);
		int bw = Block.getAbsoluteWidth();
		int bh = Block.getAbsoluteHeight();
		b.setLocation(bw*x+bw/2,bh*y+bh/2);
		
		return b;
	}
	
	public boolean isRanger(){
		return IS_RANGER[type];
	}
	
	public void setState(int state){
		if(this.state!=state){
			phase = 0;
			this.state = state;		
			phase = 0;
		}
	}
	
	public int getState(){
		return state;
	}	
	
	@Override
	public int getMaxHitPoints() {
		return 100;
	}	
	
	@Override
	public String getTypeName(){
		return SceneView.getInstance(null).getResources().getString(NAME_RESID[type]);
	}
	
	//public int getX(){return (int)x;}
	//public int getY(){return (int)y;}
	
	public Coord getCoord(){
		return new Coord((int)x,(int)y);
	}
		
	public int getSmellDistance(){
		return SMELL_DISTANCE[type];
	}

	@Override
	public int getSelectionRadius(){
		return SELECTION_RADIUS[type];
	}
	
	public void setLocation(double x, double y){this.x=x;this.y=y;}
	
	public void poison(int player, int damage, int duration, int period){
		poison.add(new Poison(player, damage,duration,period));
	}
		
	
	public void onDie(){
		addEffect(new DisappearEffect(100));		
		SoundManager.playIt(R.raw.bug_squashed, new Coord((int)x, (int)y));
	}
	
	@Override
	public void step(long tic) {
		if(state==SQUASHED) return;
		poisonDamage(tic);
		if(hitpoints<=0) {
			setState(SQUASHED);
			clearEffects();			
			onDie();
		}
		else {
			
			if (state==MOVING) {
				if(DEBUG) eye_sight_pts = new ArrayList<Coord>();
				smell();
				see();
				think();
				if(!is_blocked) forward();
			}
			
			if(destination==null || !(destination instanceof Bug)) turnAdd(rnd.nextInt(7)-3);	//bug shaking
			super.step(tic); //turn();
			
			if(state==STANDING || state==GIVE_BIRTH) nextPhase();
			
			if (state==EATING) eat(tic);
			else if(destination==null) {
				Scene scene = SceneView.getScene();
				int range = AUTO_ATTACK_RANGE < ATTACK_RANGE[type] ? ATTACK_RANGE[type] : AUTO_ATTACK_RANGE;
				int x1 = ((int)x - range) / Block.getAbsoluteWidth();
				int y1 = ((int)y - range) / Block.getAbsoluteHeight();
				int x2 = ((int)x + range) / Block.getAbsoluteWidth();
				int y2 = ((int)y + range) / Block.getAbsoluteHeight();
				if(x1<0) x1 = 0;
				if(y1<0) y1 = 0;
				if(x2>=scene.getWidth())  x2 = scene.getWidth()-1;
				if(y2>=scene.getHeight()) y2 = scene.getHeight()-1;
				
				Entity min_e  = null;
				long   min_d2 = 0;
				Coord c = getPosition();
				for(int i=0; i<2; i++){
					for(int ay=y1; ay<=y2; ay++){
						for(int ax=x1; ax<=x2; ax++){
							if(i==0){
								Map<Item,Boolean> items = scene.getItemsAt(ax, ay);
								if(items!=null){
									for(Item item : items.keySet()){
										if(getAlignment(item) == ALIGN_ENEMY && item.getFocusObject(this) == FOCUS_FOOD && item.getHitPoints()>0){
											long d2 = c.d2(item.getPosition());
											if(d2<=range*range){
												if(min_e==null || d2<min_d2){
													min_e  = item;
													min_d2 = d2;
												}
											}
										}
									}
								}
							}
							else {
								Block b = scene.getHigherBlock(ax, ay);
								if(b!=null && getAlignment(b)==ALIGN_ENEMY && b.getFocusObject(this) == FOCUS_FOOD && b.getHitPoints()>0){
									long d2 = c.d2(b.getPosition());
									if(d2<=range*range){
										if(min_e==null || d2<min_d2){
											min_e  = b;
											min_d2 = d2;
										}
									}									
								}
							}
						}
					}
					if(min_e!=null) break;
				}
				
				if(min_e!=null && canEat()) this.setDestination(min_e, false, Block.getAbsoluteWidth()*Block.getAbsoluteWidth(), 0, true, true, null);
			}
		}
	}
	
	private void see(){
		for(int i=0; i<eye.length; i++){
			eye[i].see();
		}
	}
	
	private void smell(){
		int bw = Block.getAbsoluteWidth();
		int bh = Block.getAbsoluteHeight();
		double ex, ey;
		int ebx, eby;
		int focus_object;
		for(int i=0; i<SMELL_MULTIPLIER[type]; i++){
			int angle = rnd.nextInt(360);
			int distance = rnd.nextInt(SMELL_DISTANCE[type]);
			
			double rad = 2.0*Math.PI/360.0 * (Bug.this.angle + angle);
			ex = x + (distance*Math.sin(rad));
			ey = y - (distance*Math.cos(rad));
			ebx = (int)(ex/bw);
			eby = (int)(ey/bh);
			
			focus_object = FOCUS_NONE;
			Scene scene = SceneView.getScene();
			if(ebx>=0 && ebx<scene.getWidth() && eby>=0 && eby<scene.getHeight()){
				Block b = scene.getHigherBlock(ebx, eby);
				if(b!=null) {
					focus_object = b.getFocusObject(Bug.this);
					if(focus_object==FOCUS_FOOD) addFoodMemory(b);
				}
				
				//todo: moving items
				Map<Item,Boolean> items = scene.getItemsAt(ebx, eby);
				if(items!=null){
					for(Item item : items.keySet()){
						if((focus_object=item.getFocusObject(this)) == FOCUS_FOOD) {
							addFoodMemory(item);
						}
					}
				}
				
				if(DEBUG) eye_sight_pts.add(new Coord((int)ex,(int)ey));				
			}
		}
	}
	
	@Override
	public int getFocusObject(Entity e){
		if(hitpoints>0){
			if(e.getAlignment(this)==ALIGN_ENEMY){
				return getMovingType()==MOVING_FLY ?
				((e instanceof Bug && ((Bug)e).isRanger()) ? FOCUS_FOOD : FOCUS_NONE):
				FOCUS_FOOD;
			}
			else {
				return FOCUS_NONE; //e.getMovingType()==MOVING_MOVE ? FOCUS_BLOCK : FOCUS_NONE;
			}
		}
		else return FOCUS_NONE;
	}
	
	private void cleanDeadFromFoodMemory(){
		ArrayList<Entity> to_remove = new ArrayList<Entity>();
		for(Entity e : food_memory){
			if(e.getHitPoints()<=0) to_remove.add(e);
		}
		for(Entity e : to_remove) food_memory.remove(e);
	}
	
	private void removeFoodMemory(int bx, int by){
		ArrayList<Entity> to_remove = new ArrayList<Entity>();
		for(Entity e : food_memory){
			if(e instanceof Block){
				Block b = (Block)e;
				if(bx==b.getX() && by==b.getY()){
					to_remove.add(e);
				}
			}
		}
		for(Entity e : to_remove) food_memory.remove(e);
		
		//food_memory.remove(new Coord(bx,by));
	}
	
	public void addFoodMemory(Entity e){
		if(!food_memory.contains(e)) {
			Log.d("TURNTO", "ADD BUG ("+this+") MEM: "+e);
			food_memory.add(e);
		}
	}
	
	public Stack<Entity> getDestPath(){
		return dest_path;
	}
	
	public ArrayList<Entity> getFoodMemory(){
		return food_memory;
	}
	
	public boolean isPoisoned() {
		return poison.size()>0;
	}
	
	@Override
	public int getMovingType() {
		return MOVING_TYPE[type];
	}
	
	private void poisonDamage(long tic){
		if(poison.size()>0){
			if(tic % 7 == 0) {
				if(hasEffect(PoisonEffect.class)) invertEffect(PoisonEffect.class);
				else addEffect(new PoisonEffect());
			}
			ArrayList<Poison> to_remove = new ArrayList<Poison>();
			for(Poison p : poison){
				if(tic % p.period == 0) {
					damage(p.damage, null, p.player);
				}
				p.duration--;
				if(p.duration<1) {
					to_remove.add(p);
					clearEffect(PoisonEffect.class);
				}
			}
			for(Poison p : to_remove) poison.remove(p);
		}
	}
	
	public Entity getAttackDestination(){
		if(attack_dest){
			Entity d = destination;
			if(dest_path!=null && !dest_path.isEmpty()) d = dest_path.firstElement();
			return d;
		}
		else {
			return null;
		}
	}
		
	public void setDestination(Entity destination, boolean only_for_closer, int dest_range_d2, int march_index, boolean attack_dest, boolean auto_attack, int [][][] dm){
		this.auto_command = auto_attack;
		this.attack_dest = attack_dest;
		this.march_index = march_index;
		
		if(destination==null){
			dest_path = null;
			this.destination = null;
		}
		else if(only_for_closer && (this.destination!=null)){
			Coord c = getPosition();
			if(destination.getPosition().d2(c)<this.destination.getPosition().d2(c)) {
				dest_path = SceneView.getScene().getDestPathFor(destination, this, dm);
				this.destination = dest_path.pop();
				this.dest_range_d2 = dest_range_d2;
				this.attack_dest = attack_dest;
				turnTo(this.destination);
				setState(MOVING);
			}
		}
		else {
			dest_path = SceneView.getScene().getDestPathFor(destination, this, dm);
			this.destination = dest_path.pop();
			this.dest_range_d2 = dest_range_d2;
			setState(MOVING);
			turnTo(this.destination);
		}
	}
	
	protected boolean canEat(){
		return !isPoisoned();
	}
	
	private void think(){
		
		int mfo = eye[MIDDLE].getFocusObject();
		int lfo = eye[LEFT].getFocusObject();
		int rfo = eye[RIGHT].getFocusObject();
		double mfd = eye[MIDDLE].getFocusDistance();
		double lfd = eye[LEFT].getFocusDistance();
		double rfd = eye[RIGHT].getFocusDistance();
		
		
		// a bug won't bother with food when it can't eat
		boolean can_eat = canEat();
		if(!can_eat){
			if(mfo==FOCUS_FOOD) mfo=FOCUS_BLOCK;
			if(lfo==FOCUS_FOOD) lfo=FOCUS_BLOCK;
			if(rfo==FOCUS_FOOD) rfo=FOCUS_BLOCK;
		}
		
		//don't stop at other food when heading to a destination
		if(destination!=null){
			if(lfo==FOCUS_FOOD && eye[LEFT].getFocusSource()!=destination) lfo = (eye[LEFT].getFocusSource() instanceof Bug) ? FOCUS_NONE : FOCUS_BLOCK;
			if(mfo==FOCUS_FOOD && eye[MIDDLE].getFocusSource()!=destination) mfo = (eye[MIDDLE].getFocusSource() instanceof Bug) ? FOCUS_NONE : FOCUS_BLOCK;
			if(rfo==FOCUS_FOOD && eye[RIGHT].getFocusSource()!=destination) rfo = (eye[RIGHT].getFocusSource() instanceof Bug) ? FOCUS_NONE : FOCUS_BLOCK;
		}
		
		// change state
		is_blocked = false;
		if(mfo==FOCUS_BLOCK && mfd<2) {
			is_blocked=true;
			if(mfd<1){
				//bugs can't stick in blocks
				if((int)x != prev_coord.x || (int)y != prev_coord.y){
					x = prev_coord.x;
					y = prev_coord.y;
				}
				else {
					//do {
						//set free method
						setAngle(angle+180);
						forward();
						forward();
						setAngle(rnd.nextInt(360));
						//eye[MIDDLE].see();
						//mfo = eye[MIDDLE].getFocusObject();
						//mfd = eye[MIDDLE].getFocusDistance();
					//} while(mfo==FOCUS_BLOCK && mfd<2);
				}
				//setState(SQUASHED);
				//return;
			}
		}
		else if (mfo==FOCUS_FOOD && mfd<1) {
			if(state!=EATING){
				setState(EATING);
				Entity fs = eye[MIDDLE].getFocusSource();
				if(fs!=null) {
					turnTo(fs);
				}
			}
		}
		else setState(MOVING);
		
		if(state!=EATING){
			
			int turn_direction = MIDDLE;
			int steps = 1;
			
			// look
			if(lfo==FOCUS_FOOD || mfo==FOCUS_FOOD || rfo==FOCUS_FOOD){
				int min=MIDDLE;
				double mind=-1;
				if(lfo==FOCUS_FOOD){ min = LEFT; mind = lfd; }
				if(rfo==FOCUS_FOOD && (rfd<lfd || lfo!=FOCUS_FOOD)) { min = RIGHT; mind = rfd; }
				else if(rfo==FOCUS_FOOD && lfo==FOCUS_FOOD && rfd==lfd && rnd.nextBoolean()) { min = RIGHT; mind = rfd; }
				
				if(mfo==FOCUS_FOOD && (mfd<=mind || mind==-1)) { min = MIDDLE; mind = mfd; }
				
				if(mind!=-1) {
					steps = (int)(mind / SPEED[type] / 2);
					if(steps<1) steps = 1;
					
					turn_direction = min;
				}
			}
			else if(mfo==FOCUS_BLOCK){
				
				steps = (int)(mfd / SPEED[type] / 2);
				if(steps<1) steps = 1;
				
				if(lfo == FOCUS_NONE && rfo == FOCUS_NONE){
					if(rnd.nextBoolean()) turn_direction = LEFT;
					else turn_direction = RIGHT;
				}
				else if(lfo == FOCUS_NONE && rfo == FOCUS_BLOCK) turn_direction = LEFT;
				else if(rfo == FOCUS_NONE && lfo == FOCUS_BLOCK) turn_direction = RIGHT;
				else if(lfo == FOCUS_BLOCK && rfo == FOCUS_BLOCK) {
					if(lfd<rfd) turn_direction = RIGHT;
					else if(lfd>rfd) turn_direction = LEFT;
					else {
						if(rnd.nextBoolean()) turn_direction = LEFT;
						else turn_direction = RIGHT;
					}
				}
			}
			
			/*
			if(rfo==FOCUS_NONE && last_turnto_direction==RIGHT) {				
				double ta = getTurnToAngle(last_turned_to);
				if(ta>-30 && ta<30) last_turnto_direction = MIDDLE;
				else turn_direction = RIGHT;
			}
			else if(lfo==FOCUS_NONE && last_turnto_direction==LEFT) {
				double ta = getTurnToAngle(last_turned_to);
				if(ta>-30 && ta<30) last_turnto_direction = MIDDLE;
				else turn_direction = LEFT;

			}
			*/
			
			
			
			
			if(destination != null){
				Entity fd = this.getFinalDestination();				
				Coord p = getPosition();
				long d2 = p.d2(fd.getPosition());
				if(d2<ATTACK_RANGE[type]*ATTACK_RANGE[type]) onReachDestination(true);
				else if (destination!=fd && p.d2(getMarchPosition(destination))<dest_range_d2) onReachDestination(false);
				else if (auto_command && d2>AUTO_CHASE_RANGE*AUTO_CHASE_RANGE){
					setDestination(null, false, 0, 0, false, true, null);
				}
			}
			
			if(turn_direction != MIDDLE) turnTo(eye[turn_direction].getAngle(),steps);
			
			// remember
			if(!isTurning()){
				if(MIN_DIRCHANGE[type]>0 && dirchange_timer>MIN_DIRCHANGE[type]){
					
					if(destination!=null){
						int aw = Block.getAbsoluteWidth();
						int ah = Block.getAbsoluteHeight();
						Entity e = destination;
						if(e instanceof Block){
							Block b = (Block)e;
							if((int)(x/aw)==b.getX() && (int)(y/ah)==b.getY()) {
								onReachDestination(false);
							}
							else turnTo(b);
						}
						else if( e instanceof Item){
							Item i = (Item)e;
							if(i.getHitPoints()<=0) {
								onReachDestination(false);
							}
							else turnTo(i);
						}
					}
					else if(can_eat){
						cleanDeadFromFoodMemory();
						int size = food_memory.size();
						if(size>0){
							Entity e = food_memory.get(size-1);							
							if(e!=last_turned_to){
								if(e instanceof Block){
									Block b = (Block)e;
									turnTo(b);
								}
								else if (e instanceof Item){
									Item i = (Item)e;									
									turnTo(i);
								}
							}
						}
						else {
							int angle = rnd.nextInt(180)-90;
							steps = rnd.nextInt(10)+1;
							if(steps < angle/45) steps = angle/45;
							turnTo(angle,steps);
						}
					}
					dirchange_timer = 0;
				}
				else dirchange_timer++;
			}
			else dirchange_timer = 0;
			
		}		
	}
	
	public void onReachDestination(boolean is_final){
		if(is_final || dest_path==null || dest_path.isEmpty()){
			Entity destination = getFinalDestination();
			march_index = 0;
			if(destination==null || !attack_dest || destination.getHitPoints()<=0){
				this.destination = null;
				dest_path = null;
				if(SceneView.getPlayer()==player) setState(STANDING);
				else setState(MOVING);
			}
			else {
				this.destination = destination;
				dest_path = null;
				setState(EATING);
				turnTo(destination);
			}
		}
		else {
			destination = dest_path.pop();
			turnTo(this.destination);
			setState(MOVING);
		}
	}
	
	
	public Coord getMarchPosition(Entity e){
		Coord c = e.getPosition();
		if(march_index>0){
			if(dest_path!=null && !dest_path.isEmpty()){
				int ei;
				if(e==destination) ei = dest_path.size()-1;
				else {
					ei = dest_path.indexOf(e) - 1;
				}
				
				if(ei>=0 && ei<dest_path.size()){
					Coord next = dest_path.elementAt(ei).getPosition();
					
					int mi = march_index;
					boolean right = (mi % 2 == 0);
					if(!right) mi++;
					boolean first_row = (mi % 4 != 0);
					if(first_row) {
						mi = mi / 4 + 1;
					}
					else {
						mi = mi / 4;
					}
					
					double na = next.angle(c);
					double nd = Math.sqrt(next.d2(c));
					double u = Math.PI / 25;
					if(!first_row){
						nd += 32;
						if(right) na -= u;
						else na += u;
					}
					
					if(right) na += mi*2.0*u;
					else na -= mi*2.0*u;
					
					
					next.x += (nd*Math.sin(na));
					next.y -= (nd*Math.cos(na));
					
					return next;
				}
			}
		}		
		return c;
	}
	
	public double getTurnToAngle(Entity e){
		Coord c = getMarchPosition(e);
		double angle = getPosition().angle(c);
		angle = 180.0/Math.PI * angle;
		
		angle -= this.angle;		
		if(angle>180.0) angle -= 360.0;
		if(angle<-180.0) angle += 360.0;
		
		return angle;
	}
	
	public void turnTo(Entity e){
		double angle = getTurnToAngle(e);		
		turnTo(angle,3);
		last_turned_to = e;
	}
	
	private void forward(){
		prev_coord.x = (int)x;
		prev_coord.y = (int)y;
		double rad = 2.0*Math.PI/360.0 * angle;
		double speed = SPEED[type];
		
		//slow down on grass
		try {
			if(getMovingType()==MOVING_MOVE && SceneView.getScene().getBlock(GROUND_LEVEL, (int)x/Block.getAbsoluteWidth(), (int)y/Block.getAbsoluteHeight()).getType()==R.drawable.b_grass){
				speed /= 1.5;
			}
		}
		catch(IndexOutOfBoundsException e){
		}
		
		x += (speed*Math.sin(rad));
		y -= (speed*Math.cos(rad));
		nextPhase();
	}
	
	public Entity getDestination(){
		return destination;
	}
	
	public Entity getFinalDestination(){
		if(destination!=null){
			Entity destination = this.destination;
			if(dest_path!=null && !dest_path.isEmpty()) destination = dest_path.firstElement();		
			return destination;
		}
		else return null;
	}
	
	@Override
	public void damage(int hitpoints, Entity e, int player){
		super.damage(hitpoints, e, player);
		if(e!=null && e instanceof Bug){
			Bug b = (Bug)e;
			Entity de = this.getFinalDestination();
			if(de==null || (de!=e && auto_command && (!(de instanceof Bug) || state!=EATING))){
				setDestination(e, false, Block.getAbsoluteWidth()*Block.getAbsoluteWidth(), 0, true, true, null);
			}
		}
	}
	
	protected void onAttack(Entity e){
		setAngle(getPosition().angle(e.getPosition())/Math.PI*180.0);
		if(IS_RANGER[type]){
			Scene scene = SceneView.getScene();
			
			double d = Math.sqrt(getPosition().d2(e.getPosition()))-10;
			if(d<0) d = 0;
			
			double v = (Math.sqrt(2.0*d+0.125) - 1 / (2*Math.sqrt(2)))/Math.sqrt(2);
			v += rnd.nextDouble()%1.0 - 0.5;

			Shrapnel acid_spit = new Shrapnel(player, SPIT_RESID[type], AIR_LEVEL, (int)x, (int)y, (int)angle - 1 + rnd.nextInt(3), (float)v, 0.5f, e, ATTACK_DAMAGE[type], false);
			acid_spit.forward(10);
			scene.addItem(acid_spit, true);
		}
		else {
			e.damage(ATTACK_DAMAGE[type], this, player);
			forward(-SPEED[type]/2);
			
			if(eating_timer%9==0) {
				SoundManager.playIt(rnd.nextBoolean()?R.raw.bug_eat1:R.raw.bug_eat2, new Coord((int)x, (int)y));
			}
		}
		
	}
	
	public boolean isDestinationReached(Entity d){
		Entity destination = this.getFinalDestination();
		if(destination==null || (d!=null && destination!=d)) return true;
		return getPosition().d2(destination.getPosition())<=dest_range_d2;
	}
	
	private void eat(long tic){
		if((eating_timer % ATTACK_PERIOD[type]) == 0) {
			Scene scene = SceneView.getScene();
			int bx = (int)x/Block.getAbsoluteWidth();
			int by = (int)y/Block.getAbsoluteHeight();
			
			
			Map<Item,Boolean> items = scene.getItemsAt(bx, by);
			boolean eaten = false;
			
			Entity destination = this.getFinalDestination();			
			
			if(destination!=null && attack_dest && destination.getHitPoints()>0 && getPosition().d2(destination.getPosition())<ATTACK_RANGE[type]*ATTACK_RANGE[type]){
				onAttack(destination);
				eaten=true;
			}
			else if(destination == null){
				if(items!=null){
					for(Item item : items.keySet()){
						if(item.getFocusObject(this)==FOCUS_FOOD){
							onAttack(item);
							eaten=true;
							break;
						}
					}
				}
			}
			
			if(!eaten){
				for(int level=scene.getLevels()-1; level>=0; level--){
					Block b = scene.getBlock(level, bx, by);
					if(b!=null && b.getFocusObject(Bug.this)==FOCUS_FOOD){
						onAttack(b);
						eaten=true;
						break;
					}
				}
			}
			
			if(!eaten) {
				setState(MOVING);
				return;
			}
		}
		nextPhase();
		eating_timer++;
	}
	
	private void nextPhase(){
		if(PHASE_REPEAT[type][state]==PLAYBACK){
			if(phase_dir==0){
				phase++;
				final int phases = PHASES_RESID[type][state].length;
				if(phase>=phases){
					phase_dir=1;
					phase=phases-2;
				}
			}
			else {
				phase--;
				if(phase<0){
					phase_dir=0;
					phase=1;
				}			
			}
		}
		else {
			phase_dir=0;
			phase++;
			if(phase>=PHASES_RESID[type][state].length) phase = 0;
		}
		//phase = (++phase)%SPRITE_PHASES[type];
	}
	
	@Override
	protected Bitmap loadBitmap(){
		return loadBitmapResource(PHASES_RESID[type][state][phase]);
	}
	
	@Override
	public boolean isRotationCached(){
		return true;
	}
	
	@Override
	protected String getHashKey(){
		int scaled_angle = (int)(2.0*angle) / 45;
		return "M_"+PHASES_RESID[type][state][phase]+"_"+scaled_angle;
	}
	
	@Override
	protected String getBaseHashKey(){
		return "M_"+PHASES_RESID[type][state][phase]+"_0";
	}
	
	@Override
	public void OnEffectEnd(Effect e) {
		super.OnEffectEnd(e);
		if(e instanceof DisappearEffect){
			Scene scene = SceneView.getScene();
			scene.queuedRemoveItem(this);
			//scene.queuedAddBug(type);
		}
	}
		
	@Override
	public JSONObject toJSON(ResIdConverter rc) throws JSONException {
		JSONObject o = super.toJSON(rc);
		o.put(JSON_INSTANCEOF, JSON_CLASS_BUG);
		if(state!=MOVING) o.put(JSON_STATE, state);
		if(march_index!=0) o.put(JSON_MARCH_INDEX, march_index);
		if(attack_dest) o.put(JSON_ATTACK_DEST, attack_dest);
		if(auto_command) o.put(JSON_AUTO_COMMAND, auto_command);
		
		if(poison!=null && poison.size()>0){
			JSONArray json_poision = new JSONArray();
			for(Poison p : poison) json_poision.put(p.toJSON());
			o.put(JSON_POISON, json_poision);
		}
			
		return o;
	}

	@Override
	public void referenciesFromJSON(JSONObject o, HashMap<Integer, Entity> tmp_ids, ResIdConverter rc) throws JSONException {
		super.referenciesFromJSON(o, tmp_ids, rc);
		
		if(!o.isNull(JSON_DESTINATION)){
			int id = o.optInt(JSON_DESTINATION, -1);
			if(id!=-1){
				this.setDestination(tmp_ids.get(id),false,Block.getAbsoluteWidth()*Block.getAbsoluteWidth(), march_index, attack_dest, auto_command, null);
			}
			else {
				Coord c = new Coord(o.getJSONObject(JSON_DESTINATION));
				Block b = SceneView.getScene().getHigherBlock(c.x/Block.getAbsoluteWidth(), c.y/Block.getAbsoluteHeight());
				this.setDestination(b,false,Block.getAbsoluteWidth()*Block.getAbsoluteWidth(), march_index, attack_dest, auto_command, null);
			}
		}
		
		food_memory = new ArrayList<Entity>();
		if(!o.isNull(JSON_FOOD_MEMORY)){
			JSONArray json_fm = o.getJSONArray(JSON_FOOD_MEMORY);
			for(int i=0; i<json_fm.length(); i++){
				food_memory.add(tmp_ids.get(json_fm.getInt(i)));
			}
		}
	}
	
	@Override
	public JSONObject referenciesToJSON(HashMap<Entity, Integer> tmp_ids) throws JSONException{
		JSONObject o = super.referenciesToJSON(tmp_ids);
		
		if(food_memory!=null && food_memory.size()>0){
			if(o==null) o = new JSONObject();
			
			JSONArray json_food_memory = new JSONArray();
			for(Entity e : food_memory) {
				Integer id = tmp_ids.get(e);
				if(id!=null) json_food_memory.put(id);
			}			
			o.put(JSON_FOOD_MEMORY, json_food_memory);
		}
		
		if(destination!=null) {
			if(o==null) o = new JSONObject();
			
			Entity d = destination;
			if(dest_path!=null && !dest_path.isEmpty()) d = dest_path.firstElement();
			
			Integer id = tmp_ids.get(d);
			if(id==null){
				o.put(JSON_DESTINATION, d.getPosition().toJSON());
			}
			else {
				o.put(JSON_DESTINATION, id);
			}
		}
		
		return o;
	}
	
	@Override
	public int getLevel() {
		return BUG_LEVEL;
	}
}
