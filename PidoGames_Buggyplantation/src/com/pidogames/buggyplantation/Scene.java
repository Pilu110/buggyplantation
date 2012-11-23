package com.pidogames.buggyplantation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.pidogames.buggyplantation.entity.Entity;
import com.pidogames.buggyplantation.entity.block.Block;
import com.pidogames.buggyplantation.entity.block.FlyTrap;
import com.pidogames.buggyplantation.entity.block.PlantBlock;
import com.pidogames.buggyplantation.entity.block.ShooterBlock;
import com.pidogames.buggyplantation.entity.block.Tendrill;
import com.pidogames.buggyplantation.entity.block.WaterBlock;
import com.pidogames.buggyplantation.entity.block.Block.OverlapData;
import com.pidogames.buggyplantation.entity.item.Item;
import com.pidogames.buggyplantation.entity.item.Missile;
import com.pidogames.buggyplantation.entity.item.bug.Bug;
import com.pidogames.buggyplantation.interfaces.Constants;
import com.pidogames.buggyplantation.interfaces.DisplayableState;
import com.pidogames.buggyplantation.interfaces.SceneListener;
import com.pidogames.buggyplantation.menu.DialogBox;
import com.pidogames.buggyplantation.menu.EditorMenu;
import com.pidogames.buggyplantation.menu.MainMenu;
import com.pidogames.buggyplantation.objective.DefeatAreaObjective;
import com.pidogames.buggyplantation.objective.DelayObjective;
import com.pidogames.buggyplantation.objective.Objective;
import com.pidogames.buggyplantation.objective.event.DefeatedEvent;
import com.pidogames.buggyplantation.objective.event.MenuItemEvent;
import com.pidogames.buggyplantation.objective.event.ObjectiveEvent;
import com.pidogames.buggyplantation.objective.event.StarterEvent;
import com.pidogames.buggyplantation.objective.event.VictoryEvent;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

public class Scene implements Constants {
	
	public static final int MAP_TYPE_SOIL  = 0;
	public static final int MAP_TYPE_SAND  = 1;
	public static final int MAP_TYPE_WATER = 2;
	public static final int MAP_TYPE_GRASS = 3;
	
	// titles: R.array.statistics
	public static final int STAT_M_WATER    = 0;
	public static final int STAT_M_SUGAR    = 1;
	public static final int STAT_M_NITROGEN = 2;
	public static final int STAT_S_WATER    = 3;
	public static final int STAT_S_SUGAR    = 4;
	public static final int STAT_S_NITROGEN = 5;
	
	public static final int STAT_PRODUCE    = 6;
	public static final int STAT_LOST_UNIT  = 7;
	public static final int STAT_KILL       = 8;
	
	public static final int STAT_BUILD      = 9;
	public static final int STAT_LOST_BLOCK = 10;
	public static final int STAT_RAZE       = 11;
	
	public static final int STATS_COUNT   = 12;
	
	private static final int STAT_MAX_WIDTH = 270;
	private static final int STAT_MARGIN_WIDTH = 10;
	private static final long [] STAT_MAX_VALUE = {
		1000,
		1000,
		1000,
		1000,
		1000,
		1000,
		10,
		10,
		10,
		10,
		10,
		10,
	};
	
	public static final int STAT_GROUP_BUILDINGS = 0;
	public static final int STAT_GROUP_UNITS = 1;
	public static final int STAT_GROUP_MINED = 2;
	public static final int STAT_GROUP_SPENT = 3;
	
	private static final int [][] STAT_GROUPS = {
		{
			STAT_BUILD,
			STAT_LOST_BLOCK,
			STAT_RAZE
		},
		{
			STAT_PRODUCE,
			STAT_LOST_UNIT,
			STAT_KILL
		},
		{
			STAT_M_WATER,
			STAT_M_SUGAR,
			STAT_M_NITROGEN
		},
		{
			STAT_S_WATER,
			STAT_S_SUGAR,
			STAT_S_NITROGEN
		}
	};
	public static final int STAT_GROUP_COUNT = STAT_GROUPS.length;
	
	public static final int STAT_BTN_PREV = 0;
	public static final int STAT_BTN_NEXT = 1;
	public static final int STAT_BTN_OK   = 2;
	private static RectF [] statBtnRect = new RectF[3];
	
	private static final int DM_RECALCULATE = 10;
	
	private static final String JSON_CURRENT_MAP_FILE = "cm";
	private static final String JSON_NEXT_MAP_FILE    = "nm";
	
	private static final String JSON_IS_ASSET   = "ia";
	private static final String JSON_IS_MISSION = "im";
	
	private static final String JSON_MAP = "s";
	private static final String JSON_MAP_TYPE = "t";
	private static final String JSON_WIDTH  = "w";
	private static final String JSON_HEIGHT = "h";
	private static final String JSON_LEVELS = "l";
	//private static final String JSON_BUGS   = "b";
	private static final String JSON_ITEMS  = "i";
	private static final String JSON_REFERENCIES = "r";
	private static final String JSON_OBJECTIVES = "o";
	
	private static final String JSON_ID = Entity.registerJSONKey("q", Scene.class);
		
	private static Random rnd = new Random(System.currentTimeMillis());
	
	public static class OverlapRule {
		public int a; //overlapper
		public int b; //overlapped
		
		public OverlapRule(int a, int b){
			this.a = a;
			this.b = b;
		}
	}
	
	private static final OverlapRule [] OVERLAP_RULE = {
		new OverlapRule(R.drawable.b_grass, R.drawable.b_soil),
		new OverlapRule(R.drawable.b_grass, R.drawable.b_sand),
		new OverlapRule(R.drawable.b_grass, R.drawable.b_water_ocean_a1),
		new OverlapRule(R.drawable.b_sand, R.drawable.b_soil),
		new OverlapRule(R.drawable.b_mount, R.drawable.b_grass),
		new OverlapRule(R.drawable.b_mount, R.drawable.b_soil),
		new OverlapRule(R.drawable.b_mount, R.drawable.b_sand),
		new OverlapRule(R.drawable.b_mount, R.drawable.b_water_ocean_a1),
		new OverlapRule(R.drawable.b_sand, R.drawable.b_water_ocean_a1),
		new OverlapRule(R.drawable.b_soil, R.drawable.b_water_ocean_a1)
	};	
	
	public static final int NO_OVERLAP = 0;
	public static final int OVERLAP    = 1;
	public static final int OVERLAPPED = 2;	
		
	private HashMap<Integer, Integer> minimap_colors;
	int [][] minimap;
	
	private LinkedList<ObjectiveEvent> triggered_events;
	private ArrayList<Objective> objectives;
	private Block [][][] map;
	private Map<Item, Boolean> [][] item_grid;
	private int map_type;
	
	private Map<Integer, List<Item>> items;
	
	private HashSet<Integer> enabled_menu_items;
	
	private ArrayList<Item> to_remove_items;
	private ArrayList<Item> to_add_items;
	
	private SceneListener sl;
	
	private HashMap<Integer, HashMap<Integer, Long>> statistics;
	
	public ArrayList<Vector> debug_vectors;
	
	private boolean is_asset;
	private boolean is_mission;
	private String current_map_file;
	private String next_map_file;
	private String next_map_name;
	private String map_description;
	
	public Scene(SceneListener sl, int levels, int width, int height, int map_type, String current_map_file, String next_map_file, String map_description, boolean is_asset, boolean is_mission){
		this.sl = sl;
		this.is_asset = is_asset;
		this.is_mission = is_mission;
		this.map_type = map_type;
		this.current_map_file = current_map_file;
		this.next_map_file = next_map_file;
		this.map_description = map_description;
		if(next_map_file!=null){
			FileManager fm = SceneView.getInstance(null).getThread().getFileManager();
			JSONObject header = fm.loadHeader(FileManager.DIR_MAP, next_map_file, is_asset);
			if(header!=null){
				try {
					next_map_name = header.getString(FileManager.JSON_NAME);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		
		map = new Block[levels][width][height];
		item_grid = new ConcurrentHashMap[width][height];
		
		objectives = new ArrayList<Objective>();
		triggered_events = new LinkedList<ObjectiveEvent>();		
		//setDefaultObjectives();
		
		minimap_colors = new HashMap<Integer, Integer>();
		minimap = new int[width][height];
		
		statistics = new HashMap<Integer, HashMap<Integer, Long>>();
		
		enabled_menu_items = null;
		
		//bugs = new ArrayList<Bug>();
		items = new ConcurrentHashMap<Integer, List<Item>>();
		to_remove_items = new ArrayList<Item>();
		to_add_items = new ArrayList<Item>();
		if(DEBUG) debug_vectors = new ArrayList<Vector>();
		fillMap(Entity.PLAYER_NEUTRAL,GROUND_LEVEL, getMapTypeResId());
	}
	
	public Scene(SceneListener sl, JSONObject data, HashMap<Integer, Entity> tmp_ids, ResIdConverter rc, String map_description, boolean is_asset, boolean is_mission) throws JSONException {
		this(sl, data.getInt(JSON_LEVELS),data.getInt(JSON_WIDTH),data.getInt(JSON_HEIGHT),data.getInt(JSON_MAP_TYPE),data.has(JSON_CURRENT_MAP_FILE)?data.getString(JSON_CURRENT_MAP_FILE):null,data.has(JSON_NEXT_MAP_FILE)?data.getString(JSON_NEXT_MAP_FILE):null, map_description, data.has(JSON_IS_ASSET)?data.getBoolean(JSON_IS_ASSET):is_asset, data.has(JSON_IS_MISSION)?data.getBoolean(JSON_IS_MISSION):is_mission);
		
		fillMap(Entity.PLAYER_NEUTRAL,GROUND_LEVEL, getMapTypeResId());
		sl.hookNewSceneReference(this);
		
		if(tmp_ids==null) tmp_ids = new HashMap<Integer,Entity>();
		int tmp_id = 0;
		
		JSONArray json_map = data.getJSONArray(JSON_MAP);
		for(int i=0; i<json_map.length(); i++){
			JSONObject o = json_map.getJSONObject(i);
			
			Block b = Block.getBlockFromJSON(o, rc);
			
			if(!o.isNull(Entity.JSON_MENU)) {
				b.initMenuFromJSON(o.getJSONObject(Entity.JSON_MENU), rc);
			}
			
			int level = o.getInt(Block.JSON_LEVEL);
			map[level][b.getX()][b.getY()] = b;
			tmp_ids.put(tmp_id, b);
			if(b instanceof FlyTrap) {
				Bug trapped = ((FlyTrap)b).getTrapped();
				if(trapped!=null) tmp_ids.put(-tmp_id, trapped);
			}
			tmp_id++;
		}
		
		if(!data.isNull(JSON_ITEMS)){
			JSONArray json_items = data.getJSONArray(JSON_ITEMS);
			for(int i=0; i<json_items.length(); i++){
				JSONObject o = json_items.getJSONObject(i);
				Item item = Item.getItemFromJSON(o, rc);
				
				if(!o.isNull(Entity.JSON_MENU)) {
					item.initMenuFromJSON(o.getJSONObject(Entity.JSON_MENU), rc);
				}
				
				addItem(item, true);
				tmp_ids.put(tmp_id, item);
				tmp_id++;
			}
		}
		
		if(!data.isNull(JSON_REFERENCIES)){
			JSONArray json_r = data.getJSONArray(JSON_REFERENCIES);
			for(int i=0; i<json_r.length(); i++){
				JSONObject o = json_r.getJSONObject(i);
				int id = o.getInt(JSON_ID);
				Entity e = tmp_ids.get(id);
				e.referenciesFromJSON(o, tmp_ids, rc);
			}
		}
		
		JSONArray json_objectives = data.getJSONArray(JSON_OBJECTIVES);
		for(int i=0; i<json_objectives.length(); i++){
			objectives.add(Objective.getObjective(json_objectives.getJSONObject(i), tmp_ids, rc));
		}
		
		for(int i=0; i<json_objectives.length(); i++){
			JSONObject json_o = json_objectives.getJSONObject(i);
			int id = json_o.getInt(Objective.JSON_ID);
			Objective o = getObjectiveById(id);
			if(o!=null){
				o.referenciesFromJSON(json_o, tmp_ids);
			}
			else {
				Log.e(Scene.class.getName(), "OBJECTIVE NOT FOUND FOR ID: "+id);
			}
		}
	}
	
	public SceneListener getSceneListener(){
		return sl;
	}
	
	public int getMapTypeResId(){
		switch(map_type){
			case MAP_TYPE_SAND: return R.drawable.b_sand;
			case MAP_TYPE_WATER: return R.drawable.b_water_ocean_a1;
			case MAP_TYPE_GRASS: return R.drawable.b_grass;
			default: return R.drawable.b_soil;
		}
	}
	
	public String getNextMapFile(){
		return next_map_file;
	}
	
	public String getNextMapName(){
		return next_map_name;
	}
	
	public void setNextMap(String next_map_file, String next_map_name){
		this.next_map_file = next_map_file;
		this.next_map_name = next_map_name;
	}
	
	public void setCurrentMapFile(String current_map_file){
		this.current_map_file = current_map_file;
	}
	
	public String getCurrentMapFile(){
		return current_map_file;
	}
	
	public boolean isFromAsset(){
		return is_asset;
	}
	
	public boolean isMission(){
		return is_mission;
	}
	
	public void setDescription(String map_description){
		this.map_description = map_description;
	}
	
	public String getDescription(){
		return map_description;
	}
	
	public void setMapSize(int width, int height){
		map = new Block[MAP_LEVELS][width][height];
		item_grid = new ConcurrentHashMap[width][height];
		minimap = new int[width][height];
		items = new ConcurrentHashMap<Integer, List<Item>>();
		to_remove_items = new ArrayList<Item>();
		to_add_items = new ArrayList<Item>();
		if(DEBUG) debug_vectors = new ArrayList<Vector>();
		fillMap(Entity.PLAYER_NEUTRAL,GROUND_LEVEL, getMapTypeResId());		
	}
	
	public void setDefaultObjectives(){
		objectives = new ArrayList<Objective>();
		triggered_events = new LinkedList<ObjectiveEvent>();
		
		final int width  = getWidth();
		final int height = getHeight();
		
		Objective o1 = new DefeatAreaObjective(getFreeObjectiveId(), this, Entity.PLAYER_PLANT, null, "Pusztísd el a pályán található össze ellenséget.", "Gratulálok!\nSikeresen megvédted magad a rovarok hada ellen.Újra megjelentek az egerek több Tesco áruházban, a rágcsálók elszaporodását a vásárlók fényképekkel is igazolták, írja a pénteki Magyar Nemzet. A lap egyik olvasója az érdi Tesco zöldségosztályán, egy másik pedig a budaörsi Tesco tejtermékei között fotózta le a rágcsálókat. A lap által közölt egyik fotón például egy tejfölösdoboz tetején üldögél az egér.\nA lap megkeresésére a Tesco nem cáfolta a vásárlók állításait. Szerintük a rágcsálók és egyéb kártevõk általában az áruk között megbújva, raklapon jutnak a raktárakba. Közölték, hogy a Tesco minõségbiztosítási szakemberei tisztában vannak az egerek jelenlétével, ezért fokozottan ellenõrzik, hogy észlelnek-e kártevõkre utaló nyomokat. Amennyiben ilyet találnak, soron kívüli irtást kérnek szerzõdéses partnerüktõl, aki ezt állításuk szerint 24 órás határidõn belül elvégzi. A háromezer négyzetméternél nagyobb áruházakban legalább hetente egyszer ellenõrzik a kártevõmentességet, tették hozzá.",false, sl, new Rect(0,0,width-1,height-1));
		objectives.add(o1);		
		o1.addEvent(new VictoryEvent(getFreeEventId(),o1));
		
		Objective o2 = new DefeatAreaObjective(getFreeObjectiveId(), this, Entity.PLAYER_BUG, null, "A rovarok hadat üzentek.", "A rovarok megsemmisítettek téged.", false, sl, new Rect(0,0,width-1,height-1));
		objectives.add(o2);
		o2.addEvent(new DefeatedEvent(getFreeEventId(),o2));
		
		Objective o3 = new DelayObjective(getFreeObjectiveId(), this, Entity.PLAYER_NEUTRAL, "Rovarok támadása", null, "A csata megkezdõdött.", true, sl, 500);
		objectives.add(o3);
		o3.addEvent(new StarterEvent(getFreeEventId(),o3,o1,StarterEvent.ACTION_START));
		o3.addEvent(new StarterEvent(getFreeEventId(),o3,o2,StarterEvent.ACTION_START));
	}
	
	public static int getOverlap(int a, int b){
		for(OverlapRule r : OVERLAP_RULE){
			if(r.a==a && r.b==b) return OVERLAP;
		}
		for(OverlapRule r : OVERLAP_RULE){
			if(r.a==b && r.b==a) return OVERLAPPED;
		}
		
		return NO_OVERLAP;
	}
	
	public void addDebugVector(Vector v){debug_vectors.add(v);}
	public void clearDebugVectorsByColor(int color){
		ArrayList<Vector> to_remove = new ArrayList<Vector>();
		for(Vector v : debug_vectors){
			if(v.color==color) to_remove.add(v);
		}
		for(Vector v : to_remove) debug_vectors.remove(v);
	}
	public ArrayList<Vector> getDebugVectors(){return debug_vectors; }
	
	public int getLevels(){
		return map.length;
	}
	
	public int getWidth(){
		return map[0].length;
	}
	
	public int getHeight(){
		return map[0][0].length;
	}
	
	public int getMapType(){
		return map_type;
	}
	
	public void setMapType(int map_type){
		this.map_type = map_type;
		fillMap(Entity.PLAYER_NEUTRAL,GROUND_LEVEL, getMapTypeResId());
	}
	
	public void fillMap(int player, int level, int type){
		Block b = type==R.drawable.b_water_ocean_a1 ? new WaterBlock(player, type, 0, 0, level, true) : new Block(player, type, 0, 0, level, true);
		for(int y=0; y<getHeight(); y++){
			for(int x=0; x<getWidth(); x++){
				map[level][x][y] = Block.getBlock(b, x, y, level);
			}
		}
	}
	
	/*
	public int getAllowedDirections(int level, int x, int y, int type){
		Block b = getBlock(level,x,y);
		int dirs = N4_U|N4_R|N4_D|N4_L;
		if(b!=null){
			dirs = b.getAllowedDirections();
		}
		
		
		return dirs;
	}
	*/
	public int getStatNutrient(boolean is_mined, int nutrient){
		if(is_mined){
			switch(nutrient){
				case Block.WATER: return STAT_M_WATER;
				case Block.SUGAR: return STAT_M_SUGAR;
				case Block.NITROGEN: return STAT_M_NITROGEN;
			}
		}
		else {
			switch(nutrient){
				case Block.WATER: return STAT_S_WATER;
				case Block.SUGAR: return STAT_S_SUGAR;
				case Block.NITROGEN: return STAT_S_NITROGEN;
			}
		}
		
		return -1;
	}
	
	public void addStatValue(int player, int stat, long value){
		HashMap<Integer,Long> pstat = statistics.get(player);
		if(pstat==null) {
			pstat = new HashMap<Integer,Long>();
			statistics.put(player, pstat);
		}
		
		Long sum = pstat.get(stat);
		if(sum==null) pstat.put(stat, value);
		else pstat.put(stat, (sum+value));
	}
	
	public long getStatMaxValue(int stat){
		long max = STAT_MAX_VALUE[stat];
		for(Entry<Integer,HashMap<Integer,Long>> e : statistics.entrySet()){
			if(e.getValue()!=null){
				Long v = e.getValue().get(stat);
				if(v!=null && v>max) max = v;
			}
		}
		return max;
	}
	
	public long getStatValue(int player, int stat){
		HashMap<Integer,Long> pstat = statistics.get(player);
		if(pstat==null) return 0;
		Long v = pstat.get(stat);
		return v!=null?v:0;
	}
	
	public Block getHigherBuiltBlock(int x, int y){
		Block b;
		for(int level=getLevels()-1; level>=0; level--){
			if((b = getBlock(level,x,y))!=null) {
				if (b.isBuilded()) return b;
			}
		}
		return null;
	}
	
	public int getHigherBlockLevel(int x, int y){
		for(int level=getLevels()-1; level>=0; level--){
			if(map[level][x][y] != null) return level;
		}
		return GROUND_LEVEL;
	}
	
	public Block getHigherBlock(int x, int y){
		Block b;
		for(int level=getLevels()-1; level>=0; level--){
			if((b = map[level][x][y])!=null) return b;
		}
		return null;
	}
	
	public boolean inBounds(int x, int y){
		return x>=0 && y>=0 && x<getWidth() && y<getHeight();
	}
	
	public Block getBlock(int level, int x, int y){
		return map[level][x][y];
	}
	
	public void setBlock(int level, int x, int y, Block b){
				
		Block prev_b = map[level][x][y];
		if(prev_b!=null) {
			prev_b.cancelBuilding();
			if(prev_b instanceof ShooterBlock) {
				for(Missile m : ((ShooterBlock)prev_b).getMissiles()){
					if(m!=null && m.getState()==Missile.LOADED) removeItem(m);
				}
			}
			
			if(b==null && PlantBlock.isStalkLevelType(prev_b.getType())){
				int shape = prev_b.getShape();
				if((shape & N4_U) != 0 && y>0){
					Block nb = map[level][x][y-1];
					if(nb!=null && nb instanceof PlantBlock) {
						int n_shape = nb.getReducedShape(N4_D);
						nb.setType(Block.getTypeByShape(R.drawable.b_sprout_d, n_shape, true));
					}
				}
				if((shape & N4_D) != 0 && y<getHeight()-1){
					Block nb = map[level][x][y+1];
					if(nb!=null && nb instanceof PlantBlock) {
						int n_shape = nb.getReducedShape(N4_U);
						nb.setType(Block.getTypeByShape(R.drawable.b_sprout_d, n_shape, true));
					}
				}
				if((shape & N4_L) != 0 && x>0){
					Block nb = map[level][x-1][y];
					if(nb!=null && nb instanceof PlantBlock) {
						int n_shape = nb.getReducedShape(N4_R);
						nb.setType(Block.getTypeByShape(R.drawable.b_sprout_d, n_shape, true));
					}
				}
				if((shape & N4_R) != 0 && x<getWidth()-1){
					Block nb = map[level][x+1][y];
					if(nb!=null && nb instanceof PlantBlock) {
						int n_shape = nb.getReducedShape(N4_L);
						nb.setType(Block.getTypeByShape(R.drawable.b_sprout_d, n_shape, true));
					}
				}
			}
		}
		
		map[level][x][y]=b;
		if(b!=null){
			b.setLevel(level);
			//b.setOverlapType(NO_OVERLAP);
			int type = b.getType();
			
			if(level == GROUND_LEVEL){
				int lw = getWidth();
				int lh = getHeight();
				
				int x1 = x-1;
				int y1 = y-1;
				int x2 = x+1;
				int y2 = y+1;
				if(y1<0) y1=0;
				if(x1<0) x1=0;
				if(x2>=getWidth())  x2 = lw-1;
				if(y2>=getHeight()) y2 = lh-1;
				
				int overlap = NO_OVERLAP;
				int overlapper_type = 0;
				Block overlapper_block = null;
				
				//int ovr_cnt = 0;
				//int ovd_cnt = 0;
				boolean is_high = b.isHigh();
				if(is_high) map[level][x][y] = prev_b;
				
				if(is_high && Block.canOverlap(type)) {
					overlap = OVERLAP;
				}
				else {
					for(int j=y1;j<=y2;j++){
						for(int i=x1;i<=x2;i++){
							Block nb = map[level][i][j];
							if(nb!=null) {
								overlapper_type = nb.getType();
								overlapper_block = nb;
								/*
								overlapper_type = Block.getTypeByOverlapType(nb.getOverlapType());
							
								if(overlapper_type>0) {
									if(type==overlapper_type) ovr_cnt++;
									else {
										ovd_cnt++;
										overlapper_block = Block.getBlock(Block.PLAYER_NEUTRAL, overlapper_type, i, j, true);
										overlap = OVERLAPPED;
									}
								}
								*/
								overlap = getOverlap(type, overlapper_type);
								if(overlap!=NO_OVERLAP) break;
							}
						}
						if(overlap!=NO_OVERLAP) break;
					}
				}
								
				/*
				if((ovr_cnt>ovd_cnt) || (overlap==NO_OVERLAP && Block.canOverlap(type))) {
					overlap = OVERLAP;
				}
				*/
				
				if(overlap==OVERLAP) {
					overlapper_type = type;
					overlapper_block = b;
				}
				else if(overlap==OVERLAPPED){
					for(int j=y1;j<=y2;j++){
						for(int i=x1;i<=x2;i++){
							if(i!=x || j!=y){
								Block nb = Block.getBlock(b,i,j,level);
								map[level][i][j] = nb;
							}
						}
					}
				}
				
				if(overlap!=NO_OVERLAP){
					int [] ot = new int[9];
					int oti=0;
					for(int j=y1;j<=y2;j++){
						for(int i=x1;i<=x2;i++){
							if(i!=x || j!=y || is_high){
								Block nb = map[level][i][j];
								if(nb!=null){
									ot[oti]=getOverlapTypeFor(nb,overlapper_type, is_high);
								}
								else {
									ot[oti]=Block.NO_OVERLAP;						
								}
							}
							oti++;
						}
					}
					
					oti=0;
					for(int j=y1;j<=y2;j++){
						for(int i=x1;i<=x2;i++){
							Block nb = map[level][i][j];
							if(i!=x || j!=y || is_high){
								if(nb!=null){
									//Log.d("OVERLAP","OVERL X:"+i+",Y:"+j+", ot:"+ot[oti]);
									if(ot[oti]==Block.FULL_OVERLAP){
										//nb.setType(overlapper_type);
										Block ob = Block.getBlock(overlapper_block,i,j,level);
										ob.setOverlapType(NO_OVERLAP);
										map[level][i][j] = ob;
									}
									else if (!is_high || ot[oti]!=NO_OVERLAP){
										nb.setOverlapType(ot[oti]);
									}
								}
							}
							oti++;
						}
					}
					
					//check changed overlaps at borders
					
					boolean changed;
					do {
						changed = false;
						for(int dir=0; dir<4; dir++){
							int i=0, j=0;
							int z1, z2;
							switch(dir){
								case 0:
									if(y1<=0) continue;
									j = y1-1;
									z1 = x1>0?x1-1:x1;
									z2 = x2<lw-1?x2+1:x2;
								break;
								case 1:
									if(x2>=lw-1) continue;
									i = x2+1;
									z1 = y1;
									z2 = y2;
								break;
								case 2:
									if(y2>=lh-1) continue;
									j = y2+1;
									z1 = x1>0?x1-1:x1;
									z2 = x2<lw-1?x2+1:x2;
								break;
								default:
									if(x1<=0) continue;
									i = x1-1;
									z1 = y1;
									z2 = y2;
								break;
							}
							
							int fot;
							for(int z=z1; z<=z2; z++){
								if(dir%2==0) i = z;
								else         j = z;
								
								fot = map[level][i][j].getOverlapType();
								Log.e("OVERLAP P","X: "+i+", Y:"+j);
								Log.e("OVERLAP P","FOT: "+fot);
								if(fot!=NO_OVERLAP){
									OverlapData od = Block.getOverlapDataByType(fot);
									Log.e("OVERLAP P","OD: "+od);
									if(od!=null){
										Log.e("OVERLAP P","OD T: "+od.type+", S:"+od.shape);
										int as = od.shape;
										if((as & N4_I) == 0) as = (~as & 15);
										else as = 15;
										Log.e("OVERLAP P","AS: "+as);
										
										if(i>0 && (as & N4_L)!=0) {
											int fot2 = map[level][i-1][j].getOverlapType();
											if(fot2!=NO_OVERLAP){
												OverlapData od2 = Block.getOverlapDataByType(fot2);
												if(od2!=null && od.type != od2.type) {
													as = as & ~N4_L;
												}
											}
											else if (map[level][i-1][j].getType() != od.type) as = as & ~N4_L;
										}
										
										if(j>0 && (as & N4_U)!=0) {
											int fot2 = map[level][i][j-1].getOverlapType();
											if(fot2!=NO_OVERLAP){
												OverlapData od2 = Block.getOverlapDataByType(fot2);
												if(od2!=null && od.type != od2.type) {
													as = as & ~N4_U;
												}
											}
											else if (map[level][i][j-1].getType() != od.type) as = as & ~N4_U;
										}
										
										if(i<lw-1 && (as & N4_R)!=0) {
											int fot2 = map[level][i+1][j].getOverlapType();
											if(fot2!=NO_OVERLAP){
												OverlapData od2 = Block.getOverlapDataByType(fot2);
												if(od2!=null && od.type != od2.type) {
													as = as & ~N4_R;
												}
											}
											else if (map[level][i+1][j].getType() != od.type) as = as & ~N4_R;
										}
										
										if(j<lh-1 && (as & N4_D)!=0) {
											int fot2 = map[level][i][j+1].getOverlapType();
											if(fot2!=NO_OVERLAP){
												OverlapData od2 = Block.getOverlapDataByType(fot2);
												if(od2!=null && od.type != od2.type) {
													as = as & ~N4_D;
												}
											}
											else if (map[level][i][j+1].getType() != od.type) as = as & ~N4_D;
										}
										
										as = (~as & 15); 
										if(((od.shape & N4_I) == 0) || as>0) {
											int new_fot = Block.getTypeByOverlap(od.type, as);
											if(fot != new_fot){
												map[level][i][j].setOverlapType(new_fot);
												changed = true;
											}
										}
									}
								}
							}
						}
					}while(changed);
					
				}
			}
		}
		if(sl!=null) sl.onChangeBlock(level, x, y);
		
	}
	
	private int invertShape(int a){
		return ((a & N4_I) == 0)?(~a & 15):a;
	}
		
	private int getOverlapTypeFor(Block b, int overlapper_type, boolean is_high){
		
		int type = b.getType();
		if(type==overlapper_type) return NO_OVERLAP;
		
		int x = b.getX();
		int y = b.getY();
		int level = b.getLevel();
		/*
		int x1 = x-1;
		int y1 = y-1;
		int x2 = x+1;
		int y2 = y+1;
		if(y1<0) y1=0;
		if(x1<0) x1=0;
		if(x2>=getWidth())  x2 = getWidth()-1;
		if(y2>=getHeight()) y2 = getHeight()-1;
		*/
		
		int bitmask = 0;
		for(int j=y-1;j<=y+1;j++){
			for(int i=x-1;i<=x+1;i++){
				if(j!=y || i!=x){
					if(j>=0 && i>=0 && i<getWidth() && j<getHeight()){
						Block nb = map[level][i][j];
						if(nb!=null){
							//if(nb.getType()==R.drawable.b_sand || nb.getOverlapType()!=Block.NO_OVERLAP) bitmask = bitmask | 1;							
							if(is_high){
								if(nb.isHigh() || nb.getType()==R.drawable.b_mount) bitmask = bitmask | 1;
							}
							else {
								if(nb.getType()==overlapper_type && !Block.isOverlapTypeOf(type, nb.getOverlapType())) bitmask = bitmask | 1;
							}
						}
					}
					bitmask = bitmask << 1;
				}
			}
		}
		bitmask = bitmask >> 1;
		Log.d("OVERLAP","BITMASK X:"+x+", Y:"+y+", M:"+bitmask);
		int overlap_type = is_high?b.getHighOverlapTypeFromMask(bitmask, overlapper_type):b.getOverlapTypeFromMask(bitmask, overlapper_type);
		return overlap_type;
	}
	
	/*
	public void damageBlock(int level, int x, int y, int hitpoints, Entity e){
		Block b = getBlock(level,x,y);
		if(b!=null) {
			b.damage(hitpoints, e);
		}
	}
	*/
	
	public void placeBlock(int player, int level, int x, int y, int type, int direction, boolean is_plant, boolean is_builded, Entity mb, MenuItem mi){
		int new_type;
		
		boolean is_tendrill = (type==R.drawable.b_tendrill_sprout_d);
		
		int o_level = level;
		boolean found = false;
		do {
			Block from = getBlock(level, x, y);
			if(from!=null) {
				if(is_tendrill && from instanceof WaterBlock) {
					level++;
					continue;					
				}
				
				new_type = Block.getTypeByShape(type, from.getJoinedShape(direction),false);
				if(new_type==-1) {
					if(level<getLevels()-1) {
						level++;
						continue;
					}
					else {
						return;
					}
				}
				from.setType(new_type);
				found = true;
			}
			else {
				new_type = Block.getTypeByShape(type, direction, false);
				from = is_tendrill? new Tendrill(player, new_type, x, y, level, is_builded) : (is_plant? new PlantBlock(player, new_type, x, y, level, is_builded) : new Block(player, new_type, x, y, level, is_builded));
				setBlock(level, x, y, from);
				found = true;
			}
		} while(!found);
		
		int nx=x, ny=y;
		switch(direction){
			case N4_U: ny--; break;
			case N4_R: nx++; break; 
			case N4_D: ny++; break;
			case N4_L: nx--; break;
		}
		
		Block block;
		if(is_tendrill)		block = new Tendrill(player,type,nx,ny,level,is_builded);
		else if(is_plant)	block = new PlantBlock(player,type,nx,ny,level,is_builded);
		else				block = new Block(player,type,nx,ny,level,is_builded);
		
		Block b = getBlock(o_level,nx,ny);
		if(is_tendrill && b!=null){
			if(!(b instanceof WaterBlock)) block.setType(R.drawable.b_tendrill_choking_sprout_d);
			o_level = LEAF_LEVEL;
		}
		
		new_type = block.getTypeByDirection(getOppDir(direction));
		if(new_type!=-1) block.setType(new_type);		
		
		setBlock(o_level,nx,ny,block);
		
		mb.getMenu().setBuildingProcess(mi, block);
	}
	
	public int getOppDir(int dir){
		switch(dir){
			case N4_U:	return N4_D;
			case N4_R:	return N4_L;
			case N4_D:	return N4_U;
			case N4_L:	return N4_R;
		}
		return -1;
	}
	
		
	public Stack<Entity> getDestPathFor(Entity destination, Entity entity, int [][][] dm){
		if(dm==null) dm = getDistanceMapFrom(entity, 0, dm);
		Stack<Entity> dest_path = new Stack<Entity>();
		Coord ep = destination.getPosition();
		int x = ep.x/Block.getAbsoluteWidth();
		int y = ep.y/Block.getAbsoluteHeight();
		
		int i=0;
		dest_path.push(destination);
		boolean ext=false;
		do {
			if(dm[0][x][y]<=0 || dm[1][x][y]<=0) break;
			switch(dm[1][x][y]){
				case N8_L: x--; break;
				case N8_R: x++; break;
				case N8_U: y--; break;
				case N8_D: y++; break;
				case N8_UL: x--; y--; break;
				case N8_DL: x--; y++; break;
				case N8_UR: x++; y--; break;
				case N8_DR: x++; y++; break;
				default: ext=true; break;
			}
			if(!ext) dest_path.push(getHigherBlock(x,y));
		} while(!ext);
		
		return dest_path;
	}
	
	public int[][][] getDistanceMapFrom(Entity entity, int sqr_range, int [][][] prev_dm){
		
		int bw = Block.getAbsoluteWidth();
		int bh = Block.getAbsoluteHeight();
		
		Coord p = entity.getPosition();
		int sx = (int)p.x/bw;
		int sy = (int)p.y/bh;
		
		if(prev_dm!=null && prev_dm[0][sx][sy]<DM_RECALCULATE) {
			return prev_dm;
		}
		
		boolean changed;
		int w = getWidth();
		int h = getHeight();
		int [][][] dm = new int[2][w][h];
		for(int my=0; my<h; my++){
			for(int mx=0; mx<w; mx++){
				dm[0][mx][my] = Integer.MAX_VALUE;
			}
		}
		
		dm[0][sx][sy] = 0;
		dm[1][sx][sy] = NONE;
		
		Coord p2 = new Coord(sx*bw+bw/2, sy*bh+bh/2);
		
		int i=0;
		int a1,a2,b1,b2,as,bs;
		do {
			changed = false;
			switch(i%4){
				case 0:  a1=0;a2=h-1;as=1;b1=0;b2=w-1;bs=1;break;
				case 1:  a1=0;a2=h-1;as=1;b1=w-1;b2=0;bs=-1;break;
				case 2:  a1=h-1;a2=0;as=-1;b1=0;b2=w-1;bs=1;break;
				default: a1=h-1;a2=0;as=-1;b1=w-1;b2=0;bs=-1;break;
			}
			
			//Log.w("PATH", "A: "+a1+"->"+a2+", "+as);
			//Log.w("PATH", "B: "+b1+"->"+b2+", "+bs);
			
			for(int ai=a1; as>0?ai<=a2:ai>=a2; ai += as){
				for(int bi=b1; bs>0?bi<=b2:bi>=b2; bi += bs){
					Block b = getHigherBlock(bi, ai);
					
					boolean in_range = sqr_range<=0 || b.getPosition().d2(p2)<sqr_range;
					if(in_range){
						int fo = b.getFocusObject(entity);
						
						int weight = 1;
						int shape = Block.FULL_OVERLAP;
						
						
						if(fo==Block.FOCUS_BLOCK){
							Map<Item,Boolean> items = this.getItemsAt(bi, ai);
							if(items!=null){
								int level = b.getLevel();
								for(Item item : items.keySet()){
									if(item.getLevel()>=level && item.getFocusObject(entity)==Block.FOCUS_BRIDGE){
										Coord bp = b.getPosition();
										if(item.isCollision(bp.x, bp.y)){
											fo = Block.FOCUS_NONE;
											weight = 4;
											break;
										}
									}
								}
							}
						}
						
						int mt = entity.getMovingType();
						if(b instanceof WaterBlock && mt==Entity.MOVING_FLOAT){
							shape = b.getShape();
						}
						
						int ot = b.getOverlapType();
						if(ot>0){
							
							//water coast
							if(b instanceof WaterBlock) {
								if(fo==Block.FOCUS_BLOCK){
									shape = Block.getOverlapShapeByType(ot);
									shape = shape ^ 0x00000f; // oppposite
									fo = Block.FOCUS_NONE;
									weight = 2;
								}
								else if(mt==Entity.MOVING_SWIM || mt==Entity.MOVING_FLOAT){
									shape = Block.getOverlapShapeByType(ot);
									weight = 5;
								}
							}
							
							//mount side
							else {
								if(entity.getMovingType()!=Entity.MOVING_FLY && Block.isOverlapTypeOf(R.drawable.b_mount, ot)){
									fo = Block.FOCUS_BLOCK;
								}
							}								
						}
						
						if(fo==Block.FOCUS_NONE || fo==Block.FOCUS_BRIDGE || fo==Block.FOCUS_TUNNEL){
							int d   = dm[0][bi][ai];
							int dir = dm[1][bi][ai];
							
							if(ai-1>=0 && dm[0][bi][ai-1]<Integer.MAX_VALUE && dm[0][bi][ai-1]+2*weight<d && (shape==Block.FULL_OVERLAP || dm[0][bi][ai-1]==0 || ((shape & N4_U) != 0))) {
								d   = dm[0][bi][ai-1]+2*weight;
								dir = N8_U;
								changed = true;
							}
							if(ai+1<h && dm[0][bi][ai+1]<Integer.MAX_VALUE && dm[0][bi][ai+1]+2*weight<d && (shape==Block.FULL_OVERLAP || dm[0][bi][ai+1]==0 || ((shape & N4_D) != 0))) {
								d   = dm[0][bi][ai+1]+2*weight;
								dir = N8_D;
								changed = true;
							}
							if(bi-1>=0 && dm[0][bi-1][ai]<Integer.MAX_VALUE && dm[0][bi-1][ai]+2*weight<d && (shape==Block.FULL_OVERLAP || dm[0][bi-1][ai]==0 || ((shape & N4_L) != 0))) {
								d   = dm[0][bi-1][ai]+2*weight;
								dir = N8_L;
								changed = true;
							}
							if(bi+1<w && dm[0][bi+1][ai]<Integer.MAX_VALUE && dm[0][bi+1][ai]+2*weight<d && (shape==Block.FULL_OVERLAP || dm[0][bi+1][ai]==0 || ((shape & N4_R) != 0))) {
								d   = dm[0][bi+1][ai]+2*weight;
								dir = N8_R;
								changed = true;
							}
							
							if(ai-1>=0 && bi-1>=0 && dm[0][bi-1][ai-1]<Integer.MAX_VALUE && dm[0][bi-1][ai-1]+3*weight<d && (shape==Block.FULL_OVERLAP || (((shape & N4_U) != 0) && ((shape & N4_L) != 0)))) {
								d   = dm[0][bi-1][ai-1]+3*weight;
								dir = N8_UL;
								changed = true;
							}
							if(ai+1<h && bi-1>=0 && dm[0][bi-1][ai+1]<Integer.MAX_VALUE && dm[0][bi-1][ai+1]+3*weight<d && (shape==Block.FULL_OVERLAP || (((shape & N4_D) != 0) && ((shape & N4_L) != 0)))) {
								d   = dm[0][bi-1][ai+1]+3*weight;
								dir = N8_DL;
								changed = true;
							}
							if(ai-1>=0 && bi+1<w && dm[0][bi+1][ai-1]<Integer.MAX_VALUE && dm[0][bi+1][ai-1]+3*weight<d && (shape==Block.FULL_OVERLAP || (((shape & N4_U) != 0) && ((shape & N4_R) != 0)))) {
								d   = dm[0][bi+1][ai-1]+3*weight;
								dir = N8_UR;
								changed = true;
							}
							if(ai+1<h && bi+1<w && dm[0][bi+1][ai+1]<Integer.MAX_VALUE && dm[0][bi+1][ai+1]+3*weight<d && (shape==Block.FULL_OVERLAP || (((shape & N4_D) != 0) && ((shape & N4_R) != 0)))) {
								d   = dm[0][bi+1][ai+1]+3*weight;
								dir = N8_DR;
								changed = true;
							}
							
							dm[0][bi][ai] = d;
							dm[1][bi][ai] = dir;
						}
					}
				}
			}
			
			i++;
		} while(changed);
		
		/*
		Log.w("PATH", "ITERATIONS: "+i);
		for(int my=0; my<h; my++){
			String sl = "";
			for(int mx=0; mx<w; mx++){
				sl += "," + ((dm[0][mx][my]==Integer.MAX_VALUE)?"-":dm[0][mx][my]);
			}
			Log.w("PATH",sl);
		}
		*/
		
		return dm;
	}
	
	public void takeRoot(int x, int y){
		Block b = this.getBlock(STALK_LEVEL, x, y);
		int new_type = -1;
		if(b!=null){
			if(b.getType()==R.drawable.b_sprout_u) new_type = R.drawable.b_stem_u;
			else if(b.getType()==R.drawable.b_sprout_r) new_type = R.drawable.b_stem_r;
			else if(b.getType()==R.drawable.b_sprout_d) new_type = R.drawable.b_stem_d;
			else if(b.getType()==R.drawable.b_sprout_l) new_type = R.drawable.b_stem_l;
			else if(PlantBlock.isTendrillType(b.getType()) && b.getLevel()==STALK_LEVEL){
				b.setType(Block.getTypeByShape(R.drawable.b_tendrill_sprout_d, b.getShape(), false));
				setBlock(LEAF_LEVEL, x, y, b);
				setBlock(STALK_LEVEL, x, y, new PlantBlock(b.getPlayer(), R.drawable.b_stem_remains, x, y, STALK_LEVEL, true));
			}
		}
		if(new_type!=-1){
			b.setType(new_type);
		}
	}

	public void load(Context context, int scene){
		for(int level=0; level<getLevels(); level++){
			for(int y=0; y<getHeight(); y++){
				for(int x=0; x<getWidth(); x++){
					map[level][x][y]=null;
				}
			}
		}
		
		Random rnd = new Random();
		for(int i=0; i<100; i++){
			int x = rnd.nextInt(getWidth()-2) + 1;
			int y = rnd.nextInt(getHeight()-2) + 1;
			setBlock(STALK_LEVEL, x, y, new Block(0,R.drawable.b_rock,x,y,STALK_LEVEL,true));
		}
		/*
		for(int i=0; i<10; i++){
			int x = rnd.nextInt(getWidth()-2) + 1;
			int y = rnd.nextInt(getHeight()-2) + 1;
			setBlock(LEAF_LEVEL, x, y, new Block(R.drawable.b_plant,x,y));
		}
		*/
		
		//for(int i=0; i<30; i++) bugs.add(new Bug(1,0,this));
		//for(int i=0; i<6; i++) bugs.add(new Bug(1,1,this));
		
		SoundManager.getInstance(context).load(R.raw.bug_eat1);
		SoundManager.getInstance(context).load(R.raw.bug_eat2);
		SoundManager.getInstance(context).load(R.raw.bug_squashed);
	}
	
	public void queuedRemoveItem(Item i){
		to_remove_items.add(i);
	}
	
	public void applyQueuedRemove(){
		for(Item i : to_remove_items) {
			removeItem(i);
		}
		to_remove_items.clear();
	}
	
	public void queuedAddBug(int player, int type){
		to_add_items.add(Bug.getInstance(player,type,this));		
	}
	
	public void applyQueuedAdd(){
		for(Item i : to_add_items){
			addItem(i, true);
		}
		to_add_items.clear();
	}
	
	/*
	public void orderDeadBugs(){
		ArrayList<Item> ordered = new ArrayList<Item>();
		for(Bug b : bugs) if(b.getState()==Bug.SQUASHED) ordered.add(b);
		for(Bug b : bugs) if(b.getState()!=Bug.SQUASHED) ordered.add(b);		
		bugs = ordered;
	}
	*/
	
	public void moveIt(long tic){
		if(DEBUG) clearDebugVectorsByColor(0xffffff00);
		
		for(int level=0; level<getLevels(); level++){
			for(int y=0; y<getHeight(); y++){
				for(int x=0; x<getWidth(); x++){
					Block b = map[level][x][y];
					if(b!=null) b.step(tic);
				}
			}
		}
		
		for(Entry<Integer,List<Item>> e : items.entrySet()){
			for(Item item : e.getValue()) item.step(tic);
		}
		
		recalculateGrid();
		
		
		//check objectives
		if(tic%20==0) {
			for(Objective objective : objectives){
				objective.checkAchieved(tic);
			}
		}
		
		//check triggered events
		if(!SceneView.hasMessages()){
			ObjectiveEvent event;
			do {
				event = triggered_events.peek();
				if(event==null) break;
				if(!event.isFulfilled() && !event.isTriggered()) event.trigger();
				if(event.isFulfilled()) triggered_events.removeFirst();
			} while(event.isFulfilled());
		}
	}
	
	public void invalidateMenu(){
		for(int level=0; level<getLevels(); level++){
			for(int y=0; y<getHeight(); y++){
				for(int x=0; x<getWidth(); x++){
					if(map[level][x][y] != null) map[level][x][y].invalidateMenu();
				}
			}
		}
		
		for(Entry<Integer,List<Item>> e : items.entrySet()){
			for(Item item : e.getValue()) item.invalidateMenu();
		}
		
	}
	
	public void onChangeGameMode(int game_mode){
		for(Objective o : objectives){
			for(ObjectiveEvent e : o.getEvents()){
				e.onChangeGameMode(game_mode);
			}
		}
		
		invalidateMenu();
	}
	
	public JSONArray getObjectivesForMenu(){
		JSONArray items = new JSONArray();
		try {
			boolean is_editor = (SceneView.getGameMode()==GAME_MODE_MAP_EDITOR);
			
			if(is_editor){
				JSONObject o = new JSONObject();
				o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_ADD_OBJECTIVE);
				o.put(DialogBox.JSON_CHILDREN, true);
				items.put(o);
			}
			
			for(Objective objective : objectives) {
				if((objective.isEnabled() && !objective.isAchieved()) || is_editor){
					JSONObject o = new JSONObject();
					o.put(DialogBox.JSON_CODE, MainMenu.MENU_CODE_OBJECTIVE + "=" + objective.getId());
					o.put(DialogBox.JSON_TITLE, objective.getTitle());
					o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
					o.put(DialogBox.JSON_PADDING, DialogBox.SMALL_PADDING);
					if(is_editor) o.put(DialogBox.JSON_CHILDREN, true);
					items.put(o);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return items;
	}
	
	public JSONArray getObjectiveTypesForMenu(){
		JSONArray items = new JSONArray();
		Resources res = SceneView.getInstance(null).getResources();
		try {
			int i=0;
			for(String type_name : res.getStringArray(R.array.objective_type)) {
				JSONObject o = new JSONObject();
				o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_TYPE + "=" + i);
				o.put(DialogBox.JSON_TITLE, type_name);
				o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
				o.put(DialogBox.JSON_PADDING, DialogBox.SMALL_PADDING);
				items.put(o);
				i++;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return items;
	}

	public JSONArray getTargetsForMenu(String code, int id, boolean is_empty_item){
		JSONArray items = new JSONArray();
		try {
			
			Resources res = SceneView.getInstance(null).getResources();
			if(is_empty_item){
				JSONObject o = new JSONObject();
				o.put(DialogBox.JSON_CODE, code+"="+id+",-1");
				o.put(DialogBox.JSON_TITLE, res.getString(R.string.none));
				items.put(o);
			}
			
			for(Objective objective : objectives) {
				JSONObject o = new JSONObject();
				o.put(DialogBox.JSON_CODE, code+"="+id+","+objective.getId());
				o.put(DialogBox.JSON_TITLE, objective.getDisplayableTitle());
				o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
				o.put(DialogBox.JSON_PADDING, DialogBox.SMALL_PADDING);
				items.put(o);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return items;
	}
	
	public JSONArray getObjectiveDetailsForMenu(int objective_id){
		JSONArray items = new JSONArray();
		
		try {
			JSONObject o = new JSONObject();
			Resources res = SceneView.getInstance(null).getResources();
			o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_REMOVE_OBJECTIVE+"="+objective_id);
			o.put(DialogBox.JSON_TITLE, res.getString(R.string.mm_remove_objective));
			items.put(o);
			
			o = new JSONObject();
			o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_TITLE+"="+objective_id);
			o.put(DialogBox.JSON_TITLE, res.getString(R.string.mm_obj_title));
			o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
			o.put(DialogBox.JSON_PADDING, DialogBox.SMALL_PADDING);
			items.put(o);
			o = new JSONObject();
			o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_STATUS+"="+objective_id);
			o.put(DialogBox.JSON_TITLE, res.getString(R.string.mm_obj_status));
			o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
			o.put(DialogBox.JSON_PADDING, DialogBox.SMALL_PADDING);
			o.put(DialogBox.JSON_TYPE, DialogBox.ITEM_TYPE_CHECKBOX);
			items.put(o);
			o = new JSONObject();
			o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_PLAYER+"="+objective_id);
			o.put(DialogBox.JSON_TITLE, res.getString(R.string.mm_obj_player));
			o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
			o.put(DialogBox.JSON_PADDING, DialogBox.SMALL_PADDING);
			o.put(DialogBox.JSON_TYPE, DialogBox.ITEM_TYPE_LIST);
			o.put(DialogBox.JSON_MAX_VALUE, Entity.PLAYERS.length);
			items.put(o);
			o = new JSONObject();
			/*
			o.put(DialogBox.JSON_CODE, MainMenu.MENU_CODE_OBJ_TYPE+"="+objective_id);
			o.put(DialogBox.JSON_TITLE, res.getString(R.string.mm_obj_type));
			o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
			o.put(DialogBox.JSON_PADDING, DialogBox.SMALL_PADDING);
			o.put(DialogBox.JSON_TYPE, DialogBox.ITEM_TYPE_LIST);
			o.put(DialogBox.JSON_MAX_VALUE, Objective.OBJECTIVE_CLASSES.length);
			items.put(o);
			*/
			Objective objective = getObjectiveById(objective_id);
			
			o = new JSONObject();
			o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_TYPE_ARGS+"="+objective_id);
			o.put(DialogBox.JSON_TITLE, res.getString(R.string.mm_obj_type_args));
			o.put(DialogBox.JSON_DIALOG_TITLE, res.getStringArray(R.array.objective_type)[objective.getType()]);
			o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
			o.put(DialogBox.JSON_PADDING, DialogBox.SMALL_PADDING);
			
			if(objective.getTypeSettingsForMenu()!=null) o.put(DialogBox.JSON_CHILDREN, true);
			else o.put(DialogBox.JSON_ENABLED, false);
			
			items.put(o);
			
			o = new JSONObject();
			o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_EVENTS+"="+objective_id);
			o.put(DialogBox.JSON_TITLE, res.getString(R.string.mm_obj_events));
			o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
			o.put(DialogBox.JSON_PADDING, DialogBox.SMALL_PADDING);
			o.put(DialogBox.JSON_CHILDREN, true);
			items.put(o);
			o = new JSONObject();
			o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_DESC+"="+objective_id);
			o.put(DialogBox.JSON_TITLE, res.getString(R.string.mm_obj_desc));
			o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
			o.put(DialogBox.JSON_PADDING, DialogBox.SMALL_PADDING);
			items.put(o);
			o = new JSONObject();
			o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_ACHIEVED_DESC+"="+objective_id);
			o.put(DialogBox.JSON_TITLE, res.getString(R.string.mm_obj_achieved_desc));
			o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
			o.put(DialogBox.JSON_PADDING, DialogBox.SMALL_PADDING);
			items.put(o);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return items;
	}
	
	public Objective getObjectiveById(int id){
		for(Objective o : objectives){
			if(o.getId()==id) return o;
		}
		return null;
	}
	
	public ObjectiveEvent getObjectiveEventById(int id){
		for(Objective o : objectives){
			for(ObjectiveEvent e : o.getEvents()){
				if(e.getId()==id) return e;
			}
		}
		return null;		
	}
	
	public int getFreeEventId(){
		HashSet<Integer> ids = new HashSet<Integer>();
		for(Objective o : objectives) {
			if(o.getEvents()!=null){
				for(ObjectiveEvent event : o.getEvents()){
					ids.add(event.getId());
				}
			}
		}
		
		for(int i=1; i<ids.size()+2; i++) {
			if(!ids.contains(i)) return i;
		}
		
		return -1;		
	}
	
	public int getFreeObjectiveId(){
		HashSet<Integer> ids = new HashSet<Integer>();
		for(Objective o : objectives) ids.add(o.getId());
		
		for(int i=1; i<ids.size()+2; i++) {
			if(!ids.contains(i)) return i;
		}
		
		return -1;
	}
	
	public void removeObjective(int id){
		Objective o = getObjectiveById(id);
		if(o!=null) {
			objectives.remove(o);
			refreshObjectiveReferences();
		}
	}
	
	public int addObjective(int type){
		Objective o = Objective.getObjective(type, getFreeObjectiveId());
		if(o!=null){
			objectives.add(o);
			return o.getId();
		}
		
		return -1;
	}
	
	public void renameObjective(int id, String title){
		Objective o = getObjectiveById(id);
		if(o!=null) {
			o.setTitle(title);
		}
	}
	
	public int addObjectiveEvent(int objective_id, int event_type){
		Objective parent = getObjectiveById(objective_id);
		ObjectiveEvent event = ObjectiveEvent.getEvent(event_type, getFreeEventId(), parent);
		return event!=null?event.getId():-1;
	}
	
	public void removeObjectiveEvent(int event_id){
		ObjectiveEvent event = getObjectiveEventById(event_id);
		if(event!=null) event.getParent().removeEvent(event);
	}
	
	/*
	public void changeObjectiveType(int id, int type){
		Objective o = getObjectiveById(id);
		if(o!=null) {
			try {
				Constructor<Objective> co = Objective.OBJECTIVE_CLASSES[type].getConstructor(new Class[]{Objective.class});
				Objective o2 = co.newInstance(o);
				objectives.remove(o);
				objectives.add(o2);				
				refreshObjectiveReferences();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void changeObjectiveEventType(int id, int type){
		ObjectiveEvent e = getObjectiveEventById(id);
		if(e!=null) {
			try {
				Constructor<ObjectiveEvent> co = ObjectiveEvent.OBJECTIVE_EVENT_CLASSES[type].getConstructor(new Class[]{ObjectiveEvent.class});
				ObjectiveEvent e2 = co.newInstance(e);
				Objective parent = e.getParent();
				parent.removeEvent(e);
				parent.addEvent(e2);
				refreshObjectiveReferences();
			} catch (SecurityException ex) {
				ex.printStackTrace();
			} catch (NoSuchMethodException ex) {
				ex.printStackTrace();
			} catch (IllegalArgumentException ex) {
				ex.printStackTrace();
			} catch (InstantiationException ex) {
				ex.printStackTrace();
			} catch (IllegalAccessException ex) {
				ex.printStackTrace();
			} catch (InvocationTargetException ex) {
				ex.printStackTrace();
			}
		}
	}
	*/
	
	private void refreshObjectiveReferences(){
		for(Objective o : objectives){
			o.refreshReferences(this);
		}
	}
	
	void addEventToTriggeredList(ObjectiveEvent event){
		triggered_events.add(event);
	}
	
	public void poisonBugs(int player, int x, int y){
		List<Item> bugs = items.get(BUG_LEVEL);
		if(bugs!=null){
			for(Item i : bugs){
				if(i instanceof Bug) {
					Bug bug = (Bug)i;
					if(bug.getState()==Bug.EATING){
						int bx = (int)bug.getX()/Block.getAbsoluteWidth();
						int by = (int)bug.getY()/Block.getAbsoluteHeight();
						if(bx==x && by==y){
							bug.poison(player, 5, 50+rnd.nextInt(100), 20);
						}
					}
				}
			}
		}
	}
	
	public boolean changedLevel(int old_level, Item i){
		List<Item> li = items.get(old_level);
		if(li!=null && li.remove(i)) {
			addItem(i,false);
			return true;
		}
		else return false;
	}
	
	public boolean putLast(Item i){
		List<Item> li = items.get(i.getLevel());
		if(li!=null){
			int idx = li.indexOf(i);
			if(idx >= 0 && idx < li.size()-1 ){
				li.remove(i);
				li.add(li.size(), i);
				return true;
			}
		}
		return false;
	}
	
	public boolean putFirst(Item i){
		List<Item> li = items.get(i.getLevel());
		if(li!=null){
			if(li.indexOf(i) > 0){
				li.remove(i);
				li.add(0, i);
				return true;
			}
		}
		return false;
	}
	
	public void removeItem(Item i){
		List<Item> li = items.get(i.getLevel());
		if(li!=null) li.remove(i);
	}
	
	public void addItem(Item i, boolean concurrent){
		List<Item> li = items.get(i.getLevel());
		if(li==null) {
			li = new CopyOnWriteArrayList<Item>();
			items.put(i.getLevel(), li);
		}
		
		if(i instanceof Bug){
			Bug b = (Bug)i;
			int mt = b.getMovingType();
			if(mt==Bug.MOVING_FLY) li.add(li.size(),i);
			else if (mt==Bug.MOVING_SWIM) li.add(0,i);
			else {
				int index=0;
				for(Item ii : li) {
					if(ii instanceof Bug){
						Bug bb = (Bug)ii;
						if(bb.getMovingType()!=Bug.MOVING_SWIM){
							break;
						}
					}
					index++;
				}
				li.add(index,i);
			}
			
			addStatValue(i.getPlayer(), Scene.STAT_PRODUCE, 1);
		}
		else {
			li.add(i);
		}
		
		itemToGrid(i,true,concurrent);
	}
	
	/*
	public void addItems(Item [] items, boolean concurrent){
		for(Item i : items) addItem(i, concurrent); //this.items.add(i);
	}
	*/
	
	public Map<Integer, List<Item>> getItems(){
		return items;
	}
	
	public List<Item> getItems(int level){
		return items.get(level);
	}
	
	public Map<Item,Boolean> getItemsAt(int x, int y){
		return item_grid[x][y];
	}
	
	public void recalculateGrid(){
		for(int y=0; y<getHeight(); y++){
			for(int x=0; x<getWidth(); x++){
				item_grid[x][y] = null;
			}
		}
		
		for(Entry<Integer,List<Item>> e : items.entrySet()){
			for(Item item : e.getValue()){
				itemToGrid(item,true,true);
			}
		}
	}
	
	public void itemToGrid(Item item, boolean is_add, boolean concurrent){
		//if(!SceneView.isCollision() && item instanceof Bug) return;
		final int r = (int)(Math.sqrt(item.getMaxBitmapD2())/Entity.getZoom()/2);
		final int ix = (int)item.getX();
		final int iy = (int)item.getY();
		final int bw = Block.getAbsoluteWidth();
		final int bh = Block.getAbsoluteHeight();
		final int h = getHeight();
		final int w = getWidth();
		int x1 = (ix-r)/bw;
		int x2 = (ix+r)/bw;
		int y1 = (iy-r)/bh;
		int y2 = (iy+r)/bh;
		
		if(x1<0) x1=0;
		else if (x1>=w) x1 = w-1;
		if(y1<0) y1=0;
		else if (y1>=h) y1 = h-1;
		if(x2<0) x2=0;
		else if (x2>=w) x2 = w-1;
		if(y2<0) y2=0;
		else if (y2>=h) y2 = h-1;
		
		for(int j=y1; j<=y2; j++){
			for(int i=x1; i<=x2; i++){
				if(is_add){
					//Collections.newSetFromMap(new ConcurrentHashMap<Object,Boolean>())
					//if(concurrent){
						if(item_grid[i][j]==null) item_grid[i][j] = new ConcurrentHashMap<Item, Boolean>();
						item_grid[i][j].put(item, true);
					/*}
					else {
						HashSet<Item> items = item_grid[i][j];
						if(items!=null) items = new HashSet<Item>(items); //(HashSet<Item>)(items.clone());
						else items = new HashSet<Item>();
						items.add(item);
						item_grid[i][j] = items;
					}
					*/
				}
				else {
					//if(concurrent){
						if(item_grid[i][j]!=null) {
							item_grid[i][j].remove(item);
							if(item_grid[i][j].isEmpty()) item_grid[i][j] = null;
						}
					/*}
					else {
						HashSet<Item> items = item_grid[i][j];
						if(items!=null) {
							items = new HashSet<Item>(items);
							items.remove(item);
							if(items.isEmpty()) items = null;
							item_grid[i][j] = items;
						}
					}
					*/
				}
			}
		}
	}
	
	public void calculateMiniMap(boolean transparency){
		
		for(int y=0; y<getHeight();y++)
			for(int x=0; x<getWidth();x++)
				minimap[x][y] = 0;
				
		for(int level=getLevels()-1; level>=0; level--){
			for(int y=0; y<getHeight(); y++){
				for(int x=0; x<getWidth(); x++){
					if(minimap[x][y]==0){
						int color = 0x80000000;
						Block b = map[level][x][y];
						if(b!=null){
							if(b.getPlayer()==Entity.PLAYER_NEUTRAL){
								int type = b.getOverlapType();
								if(type==R.drawable.b_nitrogen || type==R.drawable.b_nitrogen2 || type==R.drawable.b_nitrogen3){
									color = Block.getNutrientColor(Block.NITROGEN);
								}
								else {
									if(type == Block.NO_OVERLAP) type = b.getType();
									
									Integer cc = minimap_colors.get(type);
									if(cc==null){
										cc = b.getColor();
										minimap_colors.put(type, cc);
									}
									
									color = cc;
									if(transparency){
										color = color & 0x00ffffff;
										color = color | 0x80000000;
									}
								}
							}
							else {
								color = b.getPlayerColor();
							}
						}
						else if(level>0) continue;
						
						minimap[x][y] = color;
					}
				}
			}		
		}
	}
	
	public float drawObjectiveStates(Canvas canvas, float sx, float sy, long tic){
		Paint paint = new Paint();
		paint.setTextSize(15*SceneView.scale);
		paint.setColor(0xffffffff);
		sy += paint.getTextSize()/2;
		
		for(Objective o : objectives){
			if(o.isEnabled() && !o.isAchieved()){
				if(o instanceof DisplayableState){
					DisplayableState ds = (DisplayableState)o;
					String state = ds.getDisplayableState();
					if(state!=null){
						canvas.drawText(o.getDisplayableTitle() + ":", sx, sy, paint);
						sy += (paint.getTextSize());
						canvas.drawText(state, sx, sy, paint);
						sy += (paint.getTextSize() + 5*SceneView.scale);						
					}
				}
			}
		}
		
		return sy;
	}
	
	
	public RectF [] getStatBtnRect(){
		return statBtnRect;
	}
	
	public void closeFinalStatistics(){
		for(int i=0; i<statBtnRect.length; i++){
			statBtnRect[i] = null;
		}
	}
	
	public void drawFinalStatistics(Canvas canvas, int player, int group_id, long anim_phase){
		float scale = SceneView.scale;
		
		int w = (int)(STAT_MAX_WIDTH*SceneView.scale);
		if(w>canvas.getWidth()) w = canvas.getWidth();
		w -= (2 * STAT_MARGIN_WIDTH);
		int h = (int)(15 * scale);
		int m = (int)(20 * scale);
		int ox = canvas.getWidth()/2;
		int oy = canvas.getHeight()/2;
				
		Paint vBgPaint = new Paint();
		vBgPaint.setStyle(Paint.Style.FILL);
		vBgPaint.setColor(0xff505000);
		
		Paint vInnerPaint = new Paint();
		vInnerPaint.setStyle(Paint.Style.FILL);
		vInnerPaint.setColor(0xffffff00);
		
		Paint fPaint = new Paint();
		fPaint.setColor(0xffccffcc);
		fPaint.setStyle(Paint.Style.STROKE);
		fPaint.setStrokeWidth(scale);
		
		Paint fInnerPaint = new Paint();
		fInnerPaint.setStyle(Paint.Style.FILL);
		fInnerPaint.setColor(0x80ccffcc);
		
		Paint tPaint = new Paint();
		tPaint.setColor(0xffccffcc);
		tPaint.setTextSize(20 * scale);
		tPaint.setShadowLayer(scale, scale, scale, 0xff000000);
		tPaint.setTextAlign(Align.CENTER);
		
		int ts = (int)tPaint.getTextSize()+1;
		
		int [] group = STAT_GROUPS[group_id];
		int count = group.length;
		
		RectF rect = new RectF();
		float r  = 5*SceneView.scale;
		float br = 10*SceneView.scale;
		rect.left   = ox - w/2;
		rect.right  = ox + w/2;
		rect.top    = oy - ((h+ts+m)*count/2) + m;
		rect.bottom = oy + h - ((h+ts+m)*count/2) + m;
		String titles [] = SceneView.getStringArray(R.array.statistics_full);

		
		int fm = (int)(10 * scale);
		RectF fRect = new RectF(rect.left - fm, rect.top - ts - fm, rect.right + fm, rect.top + (h+ts+m)*count - m + fm - ts/2);
		
		//buttons
		int bw = (int)(80*scale);
		int bh = (int)(50*scale);
		int bm = (int)(10*scale);
		RectF b1 = new RectF(fRect.right-bw,fRect.bottom+bm, fRect.right, fRect.bottom+bh+bm);
		
		canvas.drawRoundRect(fRect, r, r, fInnerPaint);
		canvas.drawRoundRect(fRect, r, r, fPaint);
		canvas.drawRoundRect(b1, br, br, fInnerPaint);
		canvas.drawRoundRect(b1, br, br, fPaint);
		
		canvas.drawText("Ok", b1.left+bw/2, b1.top+bh/2+ts/2, tPaint);
		statBtnRect[STAT_BTN_OK] = b1;
		
		//header
		int tw = bh;
		
		RectF t1 = new RectF(fRect.left,fRect.top-bh-bm, fRect.left + tw, fRect.top-bm);
		canvas.drawRoundRect(t1, br, br, fInnerPaint);
		canvas.drawRoundRect(t1, br, br, fPaint);
		canvas.drawText("<", t1.left+tw/2, t1.top+bh/2+ts/2, tPaint);
		statBtnRect[STAT_BTN_PREV] = t1;
		
		RectF t2 = new RectF(fRect.right-tw,t1.top,fRect.right,t1.bottom);
		canvas.drawRoundRect(t2, br, br, fInnerPaint);
		canvas.drawRoundRect(t2, br, br, fPaint);
		canvas.drawText(">", t2.left+tw/2, t2.top+bh/2+ts/2, tPaint);
		statBtnRect[STAT_BTN_NEXT] = t2;
		
		RectF t3 = new RectF(fRect.left + tw + bm, t1.top, fRect.right - tw - bm, t1.bottom);
		canvas.drawRoundRect(t3, br, br, fInnerPaint);
		canvas.drawRoundRect(t3, br, br, fPaint);		
		String [] groupTitles = SceneView.getStringArray(R.array.stat_group_title);
		tw = (int)(t3.right - t3.left);
		canvas.drawText(groupTitles[group_id], t3.left+tw/2, t3.top+bh/2+ts/2, tPaint);

		tPaint.setTextAlign(Align.LEFT);
		for(int i=0; i<count; i++){
			int stat = group[i];
			long value = getStatValue(player, stat);			
			long max   = getStatMaxValue(stat);
			
			int vw = (int)(w*((double)value/(double)max));
			if(vw>anim_phase) {
				vw = (int)anim_phase;
				value = (long)((double)vw/(double)w * max);
			}
			
			tPaint.setTextAlign(Align.RIGHT);
			canvas.drawText(value+"/"+max, rect.right, rect.top, tPaint);
			
			tPaint.setTextAlign(Align.LEFT);
			canvas.drawText(titles[stat], rect.left, rect.top, tPaint);			
			
			rect.top    += ts/2;
			rect.bottom += ts/2;
			
			canvas.drawRoundRect(rect, r, r, vBgPaint);			

			if(vw>0){
				if(vw>r/2) {
					rect.right = rect.left + 2*r;
					canvas.drawRoundRect(rect, r, r, vInnerPaint);
				}
				//canvas.drawCircle(rect.left, rect.top + h/2, r, vPaint);
				if(vw>r){
					if(vw>w-r) rect.right = rect.left + w - r;
					else rect.right  = rect.left + vw;
					rect.left   += r;
					canvas.drawRect(rect, vInnerPaint);
					rect.left   -= r;
				}
				
				rect.right  = rect.left + w;
				
				if(vw>w-r + r/2) {
					rect.left += (w - 2*r);					
					canvas.drawRoundRect(rect, r, r, vInnerPaint);
					rect.left -= (w - 2*r);					
				}
				
			}
			
			canvas.drawRoundRect(rect, r, r, fPaint);
			rect.top    += (h + m + ts/2);
			rect.bottom += (h + m + ts/2);
		}
	}
	
	public void drawMiniMap(Canvas canvas, int sx, int sy, int w){
		Paint paint = new Paint();
			
		int bw = Block.getAbsoluteWidth();
		int bh = Block.getAbsoluteHeight();
		
		for(int y=0; y<getHeight(); y++){
			for(int x=0; x<getWidth(); x++){
				int color = minimap[x][y];
				
				Map<Item,Boolean> items = item_grid[x][y];
				if(items!=null && !items.isEmpty()) {
					//color = items.iterator().next().getPlayerColor();
					for(Item item : items.keySet()){
						if(item.isSelectable() && (int)(item.getX()/bw)==x && (int)(item.getY()/bh)==y) {
							color = item.getPlayerColor();
							break;
						}
					}
				}
				
				paint.setColor(color);
				if(w<2) canvas.drawPoint(sx+x, sy+y, paint);
				else {
					canvas.drawRect(sx+w*x, sy+w*y, sx+w*(x+1), sy+w*(y+1), paint);
				}
			}
		}
		
		paint.setStyle(Style.STROKE);
		paint.setColor(0xff808080);
		canvas.drawRect(sx, sy, sx+w*getWidth(), sy+w*getHeight(), paint);
		paint.setColor(0xff000000);
		canvas.drawRect(sx-1, sy-1, sx+w*getWidth()+1, sy+w*getHeight()+1, paint);
	}
	
	public JSONObject toJSON(HashMap<Entity,Integer> tmp_ids, ResIdConverter rc) throws JSONException{
		JSONObject o = new JSONObject();
		o.put(JSON_WIDTH, getWidth());
		o.put(JSON_HEIGHT, getHeight());
		o.put(JSON_LEVELS, getLevels());
		o.put(JSON_MAP_TYPE, map_type);
		o.put(JSON_CURRENT_MAP_FILE, current_map_file);
		o.put(JSON_NEXT_MAP_FILE, next_map_file);
		
		if(is_asset) o.put(JSON_IS_ASSET, is_asset);
		if(is_mission) o.put(JSON_IS_MISSION, is_mission);
		
		int tmp_id = 0;
		if(tmp_ids==null) tmp_ids = new HashMap<Entity,Integer>();
		
		int map_type_resid = getMapTypeResId();
		
		for(int phase=0; phase<2; phase++){
		
			JSONArray json_map = new JSONArray();
			JSONArray json_r = new JSONArray();
			
			for(int level=0; level<getLevels(); level++){
				for(int y=0; y<getHeight(); y++){
					for(int x=0; x<getWidth(); x++){
						Block b = map[level][x][y];
						if(b!=null && (b.getType()!=map_type_resid || b.getOverlapType()>0 || b.isHigh())) {
							if(phase==0){
								json_map.put(b.toJSON(rc));
								tmp_ids.put(b, tmp_id);
								tmp_id++;
							}
							else {
								JSONObject io = b.referenciesToJSON(tmp_ids);
								if(io!=null) {
									io.put(JSON_ID, tmp_ids.get(b));
									json_r.put(io);
								}
							}
						}
					}
				}
			}
			
			if(phase==0) o.put(JSON_MAP, json_map);
			
			/*
			if(bugs!=null && bugs.size()>0){
				JSONArray json_bugs = new JSONArray();
				for(Bug b : bugs){
					json_bugs.put(b.toJSON());
				}
				o.put(JSON_BUGS, json_bugs);
			}
			*/
			
			if(items!=null && items.size()>0){
				JSONArray json_items = new JSONArray();
				for(Entry<Integer, List<Item>> e : items.entrySet()){
					for(Item item : e.getValue()){
						if(!(item instanceof Missile) || (((Missile)item).getState()==Missile.FIRED)) {
							if(phase==0){
								json_items.put(item.toJSON(rc));
								tmp_ids.put(item, tmp_id);
								tmp_id++;
							}
							else {
								JSONObject io = item.referenciesToJSON(tmp_ids);
								if(io!=null) {
									io.put(JSON_ID, tmp_ids.get(item));
									json_r.put(io);
								}
							}
						}
					}
				}
				
				if(phase==0) o.put(JSON_ITEMS, json_items);
			}
			
			if(phase==1) o.put(JSON_REFERENCIES, json_r);
		}
		
		IdCounter counter = new IdCounter(tmp_id);
		JSONArray json_objectives = new JSONArray();
		
		for(Objective objective : objectives){
			json_objectives.put(objective.toJSON(tmp_ids, counter, rc));
		}
		o.put(JSON_OBJECTIVES, json_objectives);
		
		return o;
	}
	
	public void addMenuItemTo(Collection<MenuItem> dst, int resid, int top_filter_type, int sort_order){
		if(enabled_menu_items==null || enabled_menu_items.contains(resid)) dst.add(new MenuItem(resid, top_filter_type, sort_order));
	}
	
	public void addMenuItemsTo(Collection<MenuItem> dst, int[] items, int top_filter_type, int sort_order){
		for(int resid : items){
			if(enabled_menu_items==null || enabled_menu_items.contains(resid)) dst.add(new MenuItem(null, resid, top_filter_type, sort_order));
			sort_order++;
		}
	}
	
	private HashSet<Integer> getAllMenuItems(){
		HashSet<Integer> items = new HashSet<Integer>();
		for(int resid : MenuItemEvent.MENU_ITEM_RESID) items.add(resid);
		return items;
	}
	
	public void addToEnabledMenuItems(HashSet<Integer> items){
		if(enabled_menu_items != null) { 
			enabled_menu_items.addAll(items);
			invalidateMenu();
		}
	}
	
	public void removeFromEnabledMenuItems(HashSet<Integer> items){
		if(enabled_menu_items==null) enabled_menu_items = getAllMenuItems();
		enabled_menu_items.removeAll(items);
		invalidateMenu();
	}
	
	public void setEnabledMenuItems(HashSet<Integer> items){
		enabled_menu_items = items;
		invalidateMenu();
	}
	
}
