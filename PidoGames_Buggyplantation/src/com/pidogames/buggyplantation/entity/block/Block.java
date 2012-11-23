package com.pidogames.buggyplantation.entity.block;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
import com.pidogames.buggyplantation.effect.BackgroundBitmapEffect;
import com.pidogames.buggyplantation.effect.LightingEffect;
import com.pidogames.buggyplantation.entity.Entity;
import com.pidogames.buggyplantation.entity.item.bug.Bug;
import com.pidogames.buggyplantation.interfaces.Constants;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.util.Log;

public class Block extends Entity implements Constants {
	
	private static final int SPRITE_W = 64;
	private static final int SPRITE_H = 64;
	
	public static final int FULL_OVERLAP = -1;
	public static final int NO_OVERLAP   = 0;
	
	public static final int WATER = 0;
	public static final int SUGAR = 1;
	public static final int NITROGEN = 2;
	public static final int NUTRIENTS = 3;
	
	public static final int [] NUTRIENT_TITLE_RESID = {
		R.string.n_water,
		R.string.n_sugar,
		R.string.n_nitrogen
	};
	
	public static final int [] NUTRIENT_ICON_RESID = {
		R.drawable.n_water,
		R.drawable.n_sugar,
		R.drawable.b_nitrogen
	};
			
	protected static void addTypes(int [] types, HashMap<Integer,Integer> m, int value){
		for(Integer t : types) m.put(t, value);
	}
	
	protected static void addTypes(int [] types, HashSet<Integer> m){
		for(Integer t : types) m.add(t);
	}
	
	
	protected static final String JSON_TYPE = registerJSONKey("t", Block.class);
	protected static final String JSON_DISPLAYED_TYPE = registerJSONKey("d", Block.class);
	protected static final String JSON_OVERLAP_TYPE   = registerJSONKey("ot", Block.class);
	protected static final String JSON_X = registerJSONKey("x", Block.class);
	protected static final String JSON_Y = registerJSONKey("y", Block.class);
	protected static final String JSON_IS_BUILDED = registerJSONKey("ib", Block.class);
	protected static final String JSON_BUILDING   = registerJSONKey("b", Block.class);
	protected static final String JSON_IS_HIGH   = registerJSONKey("g", Block.class);
	
	protected int displayed_type;
	protected int overlap_type;
	protected int type;
	protected int x;
	protected int y;
	protected int level;
	
	protected GameMenu menu;
	private int building;
	protected boolean is_builded;
	
	private boolean is_high;
	
	private static HashMap<Integer,int[]> displayed_types = new HashMap<Integer,int[]>();
	static {
		displayed_types.put(R.drawable.b_soil, new int[]{R.drawable.b_soil2,R.drawable.b_soil3});
		displayed_types.put(R.drawable.b_grass, new int[]{R.drawable.b_grass2,R.drawable.b_grass3});
		displayed_types.put(R.drawable.b_sand, new int[]{R.drawable.b_sand2,R.drawable.b_sand3,R.drawable.b_sand4});
		displayed_types.put(R.drawable.b_rock, new int[]{R.drawable.b_rock2,R.drawable.b_rock3,R.drawable.b_rock4,R.drawable.b_rock5,R.drawable.b_rock6});
		displayed_types.put(R.drawable.b_mount, new int[]{R.drawable.b_mount2,R.drawable.b_mount3});
	}
	
	
	private static HashMap<Integer,int[]> building_states = new HashMap<Integer,int[]>();
	static {
		building_states.put(R.drawable.b_sprout_u,new int[]{R.drawable.b_sprout_u_b1});
		building_states.put(R.drawable.b_sprout_r,new int[]{R.drawable.b_sprout_r_b1});
		building_states.put(R.drawable.b_sprout_d,new int[]{R.drawable.b_sprout_d_b1});
		building_states.put(R.drawable.b_sprout_l,new int[]{R.drawable.b_sprout_l_b1});
		building_states.put(R.drawable.b_flower,new int[]{R.drawable.b_flower_a2,R.drawable.b_flower_a3,R.drawable.b_flower_a4,R.drawable.b_flower_a5});
	}

	private static HashMap<Integer,int[]> hitpoint_states = new HashMap<Integer,int[]>();
	static {
		hitpoint_states.put(R.drawable.b_leaf, new int[]{R.drawable.b_leaf_d1,R.drawable.b_leaf_d2});
		hitpoint_states.put(R.drawable.b_sprout_u,new int[]{R.drawable.b_sprout_u_d1,R.drawable.b_sprout_u_d2});
		hitpoint_states.put(R.drawable.b_sprout_r,new int[]{R.drawable.b_sprout_r_d1,R.drawable.b_sprout_r_d2});
		hitpoint_states.put(R.drawable.b_sprout_d,new int[]{R.drawable.b_sprout_d_d1,R.drawable.b_sprout_d_d2});
		hitpoint_states.put(R.drawable.b_sprout_l,new int[]{R.drawable.b_sprout_l_d1,R.drawable.b_sprout_l_d2});
		hitpoint_states.put(R.drawable.b_stem_u,new int[]{R.drawable.b_stem_u_d1,R.drawable.b_stem_u_d2});
		hitpoint_states.put(R.drawable.b_stem_r,new int[]{R.drawable.b_stem_r_d1,R.drawable.b_stem_r_d2});
		hitpoint_states.put(R.drawable.b_stem_d,new int[]{R.drawable.b_stem_d_d1,R.drawable.b_stem_d_d2});
		hitpoint_states.put(R.drawable.b_stem_l,new int[]{R.drawable.b_stem_l_d1,R.drawable.b_stem_l_d2});
		hitpoint_states.put(R.drawable.b_stalk_rd,new int[]{R.drawable.b_stalk_rd_d1,R.drawable.b_stalk_rd_d2});
		hitpoint_states.put(R.drawable.b_stalk_rdl,new int[]{R.drawable.b_stalk_rdl_d1,R.drawable.b_stalk_rdl_d2});
		hitpoint_states.put(R.drawable.b_stalk_dl,new int[]{R.drawable.b_stalk_dl_d1,R.drawable.b_stalk_dl_d2});
		hitpoint_states.put(R.drawable.b_stalk_urd,new int[]{R.drawable.b_stalk_urd_d1,R.drawable.b_stalk_urd_d2});
		hitpoint_states.put(R.drawable.b_stalk_urdl,new int[]{R.drawable.b_stalk_urdl_d1,R.drawable.b_stalk_urdl_d2});
		hitpoint_states.put(R.drawable.b_stalk_udl,new int[]{R.drawable.b_stalk_udl_d1,R.drawable.b_stalk_udl_d2});
		hitpoint_states.put(R.drawable.b_stalk_ur,new int[]{R.drawable.b_stalk_ur_d1,R.drawable.b_stalk_ur_d2});
		hitpoint_states.put(R.drawable.b_stalk_url,new int[]{R.drawable.b_stalk_url_d1,R.drawable.b_stalk_url_d2});
		hitpoint_states.put(R.drawable.b_stalk_ul,new int[]{R.drawable.b_stalk_ul_d1,R.drawable.b_stalk_ul_d2});
		hitpoint_states.put(R.drawable.b_stalk_rl,new int[]{R.drawable.b_stalk_rl_d1,R.drawable.b_stalk_rl_d2});
		hitpoint_states.put(R.drawable.b_stalk_ud,new int[]{R.drawable.b_stalk_ud_d1,R.drawable.b_stalk_ud_d2});
		hitpoint_states.put(R.drawable.b_shooter,new int[]{R.drawable.b_shooter_d1,R.drawable.b_shooter_d2});
		hitpoint_states.put(R.drawable.b_plant,new int[]{R.drawable.b_plant_d1,R.drawable.b_plant_d2});
	}	
	
	private static final Map<Integer, HashMap<Integer,Integer>> OVERLAP_TYPES;
	static {
		HashMap<Integer,HashMap<Integer,Integer>> tempMap = new HashMap<Integer,HashMap<Integer,Integer>>();
		
		HashMap<Integer,Integer> ot = new HashMap<Integer,Integer>();
		ot.put(N4_L, R.drawable.b_sand_l);
		ot.put(N4_R, R.drawable.b_sand_r);
		ot.put(N4_U, R.drawable.b_sand_u);
		ot.put(N4_D, R.drawable.b_sand_d);
		ot.put(N4_L | N4_U, R.drawable.b_sand_lu);
		ot.put(N4_L | N4_D, R.drawable.b_sand_ld);
		ot.put(N4_R | N4_U, R.drawable.b_sand_ru);
		ot.put(N4_R | N4_D, R.drawable.b_sand_rd);
		ot.put(N4_I | N4_L | N4_U, R.drawable.b_sand_ilu);
		ot.put(N4_I | N4_L | N4_D, R.drawable.b_sand_ild);
		ot.put(N4_I | N4_R | N4_U, R.drawable.b_sand_iru);
		ot.put(N4_I | N4_R | N4_D, R.drawable.b_sand_ird);
		tempMap.put(R.drawable.b_sand, ot);
				
		ot = new HashMap<Integer,Integer>();
		ot.put(N4_L, R.drawable.b_soil_l);
		ot.put(N4_R, R.drawable.b_soil_r);
		ot.put(N4_U, R.drawable.b_soil_u);
		ot.put(N4_D, R.drawable.b_soil_d);
		ot.put(N4_L | N4_U, R.drawable.b_soil_lu);
		ot.put(N4_L | N4_D, R.drawable.b_soil_ld);
		ot.put(N4_R | N4_U, R.drawable.b_soil_ru);
		ot.put(N4_R | N4_D, R.drawable.b_soil_rd);
		ot.put(N4_I | N4_L | N4_U, R.drawable.b_soil_ilu);
		ot.put(N4_I | N4_L | N4_D, R.drawable.b_soil_ild);
		ot.put(N4_I | N4_R | N4_U, R.drawable.b_soil_iru);
		ot.put(N4_I | N4_R | N4_D, R.drawable.b_soil_ird);
		tempMap.put(R.drawable.b_soil, ot);

		ot = new HashMap<Integer,Integer>();
		ot.put(N4_L, R.drawable.b_grass_l);
		ot.put(N4_R, R.drawable.b_grass_r);
		ot.put(N4_U, R.drawable.b_grass_u);
		ot.put(N4_D, R.drawable.b_grass_d);
		ot.put(N4_L | N4_U, R.drawable.b_grass_lu);
		ot.put(N4_L | N4_D, R.drawable.b_grass_ld);
		ot.put(N4_R | N4_U, R.drawable.b_grass_ru);
		ot.put(N4_R | N4_D, R.drawable.b_grass_rd);
		ot.put(N4_I | N4_L | N4_U, R.drawable.b_grass_ilu);
		ot.put(N4_I | N4_L | N4_D, R.drawable.b_grass_ild);
		ot.put(N4_I | N4_R | N4_U, R.drawable.b_grass_iru);
		ot.put(N4_I | N4_R | N4_D, R.drawable.b_grass_ird);
		tempMap.put(R.drawable.b_grass, ot);		
		
		ot = new HashMap<Integer,Integer>();
		ot.put(N4_L, R.drawable.b_mount_l);
		ot.put(N4_R, R.drawable.b_mount_r);
		ot.put(N4_U, R.drawable.b_mount_u);
		ot.put(N4_D, R.drawable.b_mount_d);
		ot.put(N4_L | N4_U, R.drawable.b_mount_lu);
		ot.put(N4_L | N4_D, R.drawable.b_mount_ld);
		ot.put(N4_R | N4_U, R.drawable.b_mount_ru);
		ot.put(N4_R | N4_D, R.drawable.b_mount_rd);
		ot.put(N4_I | N4_L | N4_U, R.drawable.b_mount_ilu);
		ot.put(N4_I | N4_L | N4_D, R.drawable.b_mount_ild);
		ot.put(N4_I | N4_R | N4_U, R.drawable.b_mount_iru);
		ot.put(N4_I | N4_R | N4_D, R.drawable.b_mount_ird);
		tempMap.put(R.drawable.b_mount, ot);
		OVERLAP_TYPES = Collections.unmodifiableMap(tempMap);
	}
	
	public int getAllowedDirections(int place_type){
		int dirs;
		switch(type){
			case R.drawable.b_stem_u: dirs = N4_U; break;
			case R.drawable.b_stem_r: dirs = N4_R; break;
			case R.drawable.b_stem_d: dirs = N4_D; break;
			case R.drawable.b_stem_l: dirs = N4_L; break;
			default: dirs = N4_U|N4_R|N4_D|N4_L; break;
		}

		int level = Block.getPlaceLevel(place_type);
		Scene scene = SceneView.getScene();
		
		if(y<=0) dirs = dirs & ~N4_U; //dirs[UP]=false;
		else if(scene.getBlock(level,x,y-1)!=null) dirs = dirs & ~N4_U;
		else {
			Block gb = scene.getBlock(GROUND_LEVEL,x,y-1);			
			if(gb!=null){
				if (gb instanceof WaterBlock && (!PlantBlock.isTendrillType(gb.getType()) || gb.getType()==R.drawable.b_water_ocean_a1)) dirs = dirs & ~N4_U;
				else if(Block.isOverlapTypeOf(R.drawable.b_mount, gb.getOverlapType())) dirs = dirs & ~N4_U;
			}
		}
			
		if(x<=0) dirs = dirs & ~N4_L; //dirs[LEFT]=false;
		else if(scene.getBlock(level,x-1,y)!=null) dirs = dirs & ~N4_L;
		else {
			Block gb = scene.getBlock(GROUND_LEVEL,x-1,y);			
			if(gb!=null){
				if (gb instanceof WaterBlock && (!PlantBlock.isTendrillType(gb.getType()) || gb.getType()==R.drawable.b_water_ocean_a1)) dirs = dirs & ~N4_L;
				else if(Block.isOverlapTypeOf(R.drawable.b_mount, gb.getOverlapType())) dirs = dirs & ~N4_L;
			}
		}
		
		if(y>=scene.getHeight()-1) dirs = dirs & ~N4_D; //dirs[DOWN]=false;
		else if(scene.getBlock(level,x,y+1)!=null) dirs = dirs & ~N4_D;
		else {
			Block gb = scene.getBlock(GROUND_LEVEL,x,y+1);			
			if(gb!=null){
				if (gb instanceof WaterBlock && (!PlantBlock.isTendrillType(gb.getType()) || gb.getType()==R.drawable.b_water_ocean_a1)) dirs = dirs & ~N4_D;
				else if(Block.isOverlapTypeOf(R.drawable.b_mount, gb.getOverlapType())) dirs = dirs & ~N4_D;
			}
		}
		
		if(x>=scene.getWidth()-1) dirs = dirs & ~N4_R; //dirs[RIGHT]=false;
		else if(scene.getBlock(level,x+1,y)!=null) dirs = dirs & ~N4_R;
		else {
			Block gb = scene.getBlock(GROUND_LEVEL,x+1,y);
			if(gb!=null){
				if (gb instanceof WaterBlock && (!PlantBlock.isTendrillType(gb.getType()) || gb.getType()==R.drawable.b_water_ocean_a1)) dirs = dirs & ~N4_R;
				else if(Block.isOverlapTypeOf(R.drawable.b_mount, gb.getOverlapType())) dirs = dirs & ~N4_R;
			}
		}
		
		if(place_type == R.drawable.b_tendrill_sprout_d) {
			//if(scene.getHigherBlockLevel(x, y)>STALK_LEVEL) return 0;
			
			if(x>0){
				Block pb = scene.getBlock(level,x-1,y);
				if(pb!=null && pb.getAlignment(this)!=ALIGN_ALLY && scene.getHigherBlockLevel(x-1, y)<LEAF_LEVEL) dirs = dirs | N4_L;
			}
			if(x < scene.getWidth()-1){
				Block pb = scene.getBlock(level,x+1,y);
				if(pb!=null && pb.getAlignment(this)!=ALIGN_ALLY && scene.getHigherBlockLevel(x+1, y)<LEAF_LEVEL) dirs = dirs | N4_R;
			}
			if(y>0){
				Block pb = scene.getBlock(level,x,y-1);
				if(pb!=null && pb.getAlignment(this)!=ALIGN_ALLY && scene.getHigherBlockLevel(x, y-1)<LEAF_LEVEL) dirs = dirs | N4_U;
			}
			if(y < scene.getHeight()-1){
				Block pb = scene.getBlock(level,x,y+1);
				if(pb!=null && pb.getAlignment(this)!=ALIGN_ALLY && scene.getHigherBlockLevel(x, y+1)<LEAF_LEVEL) dirs = dirs | N4_D;
			}
		}
		
		return dirs;
	}
	
	@Override
	public String getTypeName(){
		if(isNitrogenBlock()) {
			return SceneView.getInstance(null).getResources().getString(R.string.n_nitrogen);
		}
		else {
			return ResIdConverter.getNameForResid(type);
		}
	}
	
	public static int getPlaceLevel(int type){
		switch(type){
			case R.drawable.b_soil:
			case R.drawable.b_grass:
			case R.drawable.b_sand:
			case R.drawable.b_mount:
			case R.drawable.b_water_ocean_a1:
			case R.drawable.b_high_soil:
			case R.drawable.b_high_sand:
			case R.drawable.b_high_water_ocean_a1:
				return GROUND_LEVEL;
			case R.drawable.b_mount_ramp_u:
			case R.drawable.b_seed:
			case R.drawable.b_water_mouth_a1:
			case R.drawable.b_water_ud_a1:
			case R.drawable.b_water_ul_a1:
			case R.drawable.b_water_ur_a1:
			case R.drawable.b_rock:
			case R.drawable.b_plant:
			case R.drawable.b_stone:
			case R.drawable.b_sprout_d:
			case R.drawable.b_tendrill_sprout_d:
				return STALK_LEVEL;
			case R.drawable.b_leaf:
			case R.drawable.b_flower:
			case R.drawable.b_flower2:
			case R.drawable.b_flytrap:
			case R.drawable.b_shooter:
			case R.drawable.b_core:
			case R.drawable.b_core_mine:
				return LEAF_LEVEL;
			default:
				return NO_LEVEL;
		}
	}
	
	public static Block getBlockFromJSON(JSONObject o, ResIdConverter rc) throws JSONException {
		if(!o.isNull(JSON_INSTANCEOF)){
			int io = o.getInt(JSON_INSTANCEOF);
			switch(io){
				case JSON_CLASS_PLANT:
					return new PlantBlock(o, rc);
				case JSON_CLASS_SHOOTER:
					return new ShooterBlock(o, rc);
				case JSON_CLASS_WATER:
					return new WaterBlock(o, rc);
				case JSON_CLASS_GALL:
					return new Gall(o, rc);
				case JSON_CLASS_BAIT:
					return new Bait(o, rc);
				case JSON_CLASS_FLYTRAP:
					return new FlyTrap(o, rc);
				case JSON_CLASS_CORE:
					return new Core(o, rc);
				case JSON_CLASS_TENDRILL:
					return new Tendrill(o, rc);
				default:
					return new Block(o, rc);
			}
		}
		else return new Block(o, rc);
	}
	
	public static int getRandomDisplayedType(int type){
		int [] types = displayed_types.get(type);
		if(types==null) return 0;
		else {
			int i = rnd.nextInt(types.length + 1);			
			if(i<types.length) return types[i];
			else return 0;
		}
	}
	
	public static boolean canOverlap(int type){
		return OVERLAP_TYPES.get(type)!=null;
	}
	
	public static boolean isOverlapTypeOf(int type, int overlap_type){
		HashMap<Integer,Integer> types = OVERLAP_TYPES.get(type);
		if(types!=null && types.containsValue(overlap_type)) return true;
		else return false;
	}
	
	public static int getTypeByOverlapType(int type){
		for(Entry<Integer, HashMap<Integer,Integer>> e : OVERLAP_TYPES.entrySet()){
			for(Entry<Integer,Integer> e2 : e.getValue().entrySet()){
				if(e2.getValue() == type) return e.getKey();
			}
		}
		return 0;		
	}
	
	public static int getOverlapShapeByType(int type){
		for(Entry<Integer, HashMap<Integer,Integer>> e : OVERLAP_TYPES.entrySet()){
			for(Entry<Integer,Integer> e2 : e.getValue().entrySet()){
				if(e2.getValue() == type) return e2.getKey();
			}
		}
		return NO_OVERLAP;
	}
	
	public static int getTypeByOverlap(int type, int shape){
		HashMap<Integer,Integer> ot = OVERLAP_TYPES.get(type);
		if(ot!=null) {
			Integer ot2 = ot.get(shape);
			if(ot2!=null) return ot2;
			else return NO_OVERLAP;
		}
		else return NO_OVERLAP;
	}
	
	public static class OverlapData {
		public int type;
		public int shape;
		
		public OverlapData(int type, int shape){
			this.type  = type;
			this.shape = shape;
		}
	}
	
	public static OverlapData getOverlapDataByType(int type){
		if(type==NO_OVERLAP) return null;
		for(Entry<Integer, HashMap<Integer,Integer>> e : OVERLAP_TYPES.entrySet()){
			for(Entry<Integer,Integer> e2 : e.getValue().entrySet()){
				if(e2.getValue() == type) return new OverlapData(e.getKey(),e2.getKey());
			}
		}
		return null;
	}

	
	public Block(JSONObject o, ResIdConverter rc) throws JSONException {
		super(o, rc);
		type = rc.typeToResId(o.getInt(JSON_TYPE));
		if(o.isNull(JSON_HITPOINTS)) hitpoints = getMaxHitPoints();
		if(!o.isNull(JSON_DISPLAYED_TYPE)) displayed_type = rc.typeToResId(o.getInt(JSON_DISPLAYED_TYPE));
		if(!o.isNull(JSON_OVERLAP_TYPE))   overlap_type   = rc.typeToResId(o.getInt(JSON_OVERLAP_TYPE));
		x     = o.getInt(JSON_X);
		y     = o.getInt(JSON_Y);
		level = o.getInt(JSON_LEVEL);
		if(!o.isNull(JSON_IS_BUILDED)){
			is_builded = o.getBoolean(JSON_IS_BUILDED);
			building   = o.getInt(JSON_BUILDING);
		}
		else 
			is_builded = true;
		
		if(!o.isNull(JSON_IS_HIGH)) is_high = o.getBoolean(JSON_IS_HIGH);
	}
	
	public static Block getBlock(int player, int type, int x, int y, int level, boolean is_builded){
		if(type == R.drawable.b_water_ocean_a1) {
			return new WaterBlock(player, type, x, y, level, is_builded);
		}
		else {
			return new Block(player, type, x, y, level, is_builded);			
		}
	}
	
	public static Block getBlock(Block b, int x, int y, int level){
		Block nb;
		if(b instanceof WaterBlock) {
			return new WaterBlock(b, x, y, level);
		}
		else {
			return new Block(b, x, y, level);
		}
	}
	
	protected Block(Block b, int x, int y, int level){
		this(b.player, b.type, x, y, level, b.is_builded);
		hitpoints = getMaxHitPoints();
		this.is_high = b.is_high;
	}
	
	public Block(int player, int type, int x, int y, int level, boolean is_builded){
		super(player);
		
		if(SceneView.getGameMode()==GAME_MODE_MAP_EDITOR) is_builded = true;
		
		this.type = type;
		this.displayed_type = getRandomDisplayedType(type);
		this.overlap_type   = NO_OVERLAP;
		this.x = x;
		this.y = y;
		this.level = level;
		this.is_builded = is_builded;
		this.is_high = false;
		if(is_builded) building = getBuildTime();
		else {
			building = 0;
			hitpoints = 1;
		}
	}
	
	public Block getOverlapBlock(){
		return (overlap_type!=NO_OVERLAP)?new Block(player, overlap_type, x, y, level, true):null;
	}
	
	@Override
	public int getType(){
		return type;
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public void setHigh(boolean is_high){
		this.is_high = is_high;
	}
	
	public boolean isHigh(){
		return is_high;
	}
	
	public void setLevel(int level){
		this.level = level;
	}
	
	public boolean isNitrogenBlock(){
		return overlap_type==R.drawable.b_nitrogen || overlap_type==R.drawable.b_nitrogen2 || overlap_type==R.drawable.b_nitrogen3;
	}
	
	@Override
	public int getLevel(){
		/*
		Scene scene = SceneView.getScene();
		for(int level=0; level<scene.getLevels(); level++){
			if(scene.getBlock(level, x, y) == this) return level;
		}
		return -1;
		*/
		return level;
	}
	
	public void setType(int type){
		double hpr = this.getHitPointsRate();
		this.type = type;
		this.displayed_type = getRandomDisplayedType(type);
		this.overlap_type   = NO_OVERLAP;
		this.hitpoints		= (int)(this.getMaxHitPoints()*hpr);
		if(menu!=null) menu.setValid(false);
	}
	
	public void setOverlapType(int overlap_type){
		this.overlap_type = overlap_type;
	}
	
	public int getOverlapType(){
		return overlap_type;
	}
	
	public int getBuilding(){
		return building;
	}
	
	public double getBuildingPercent(){
		return (double)(building)/(double)getBuildTime();
	}
	
	public int getTypeByDirection(int direction){
		switch(type){
			case R.drawable.b_sprout_u:
			case R.drawable.b_sprout_r:
			case R.drawable.b_sprout_d:
			case R.drawable.b_sprout_l:
				switch(direction){
					case N4_U: return R.drawable.b_sprout_u;
					case N4_R: return R.drawable.b_sprout_r;
					case N4_D: return R.drawable.b_sprout_d;
					case N4_L: return R.drawable.b_sprout_l;
				}
			break;
			
			case R.drawable.b_tendrill_choking_sprout_u:
			case R.drawable.b_tendrill_choking_sprout_r:
			case R.drawable.b_tendrill_choking_sprout_d:
			case R.drawable.b_tendrill_choking_sprout_l:
				switch(direction){
					case N4_U: return R.drawable.b_tendrill_choking_sprout_u;
					case N4_R: return R.drawable.b_tendrill_choking_sprout_r;
					case N4_D: return R.drawable.b_tendrill_choking_sprout_d;
					case N4_L: return R.drawable.b_tendrill_choking_sprout_l;
				}
			break;
			
			case R.drawable.b_tendrill_sprout_u:
			case R.drawable.b_tendrill_sprout_r:
			case R.drawable.b_tendrill_sprout_d:
			case R.drawable.b_tendrill_sprout_l:
				switch(direction){
					case N4_U: return R.drawable.b_tendrill_sprout_u;
					case N4_R: return R.drawable.b_tendrill_sprout_r;
					case N4_D: return R.drawable.b_tendrill_sprout_d;
					case N4_L: return R.drawable.b_tendrill_sprout_l;
				}
			break;
			
			case R.drawable.b_stone:
			case R.drawable.b_stone_u:
			case R.drawable.b_stone_r:
			case R.drawable.b_stone_d:
			case R.drawable.b_stone_l:
				switch(direction){
				case N4_U: return R.drawable.b_stone_u;
				case N4_R: return R.drawable.b_stone_r;
				case N4_D: return R.drawable.b_stone_d;
				case N4_L: return R.drawable.b_stone_l;
			}
			break;
		}
		return -1;
	}
	
	public int getJoinedShape(int direction){
		return getShape() | direction;
	}
	
	public int getReducedShape(int direction){
		return getShape() & ~direction;
	}
	
	
	public static int getTypeByShape(int type, int shape, boolean sprout){
		switch(type){
			case R.drawable.b_sprout_d: {
				if(sprout){
					switch(shape){
						case N4_U:		return R.drawable.b_sprout_u;
						case N4_R:		return R.drawable.b_sprout_r;
						case N4_D:		return R.drawable.b_sprout_d;
						case N4_L:		return R.drawable.b_sprout_l;
					}
				}
				else {
					switch(shape){
						case N4_U:		return R.drawable.b_stem_u;
						case N4_R:		return R.drawable.b_stem_r;
						case N4_D:		return R.drawable.b_stem_d;
						case N4_L:		return R.drawable.b_stem_l;
					}
				}
					
				switch(shape){
					case B_E:	return R.drawable.b_stem_remains;
					case N4_U|N4_R:	return R.drawable.b_stalk_ur;
					case N4_R|N4_D:	return R.drawable.b_stalk_rd;
					case N4_D|N4_L:	return R.drawable.b_stalk_dl;
					case N4_L|N4_U:	return R.drawable.b_stalk_ul;
					case N4_U|N4_D:	return R.drawable.b_stalk_ud;
					case N4_R|N4_L:	return R.drawable.b_stalk_rl;
					case N4_U|N4_R|N4_D:	return R.drawable.b_stalk_urd;
					case N4_R|N4_D|N4_L:	return R.drawable.b_stalk_rdl;
					case N4_D|N4_L|N4_U:	return R.drawable.b_stalk_udl;
					case N4_R|N4_L|N4_U:	return R.drawable.b_stalk_url;
					case N4_U|N4_R|N4_D|N4_L:	return R.drawable.b_stalk_urdl;
				}
				break;
			}
		
			case R.drawable.b_tendrill_sprout_d: {
				switch(shape){
					case B_E:		return -1;
					case N4_U:		return R.drawable.b_tendrill_stem_u;
					case N4_R:		return R.drawable.b_tendrill_stem_r;
					case N4_D:		return R.drawable.b_tendrill_stem_d;
					case N4_L:		return R.drawable.b_tendrill_stem_l;
					case N4_U|N4_R:	return R.drawable.b_tendrill_ur;
					case N4_R|N4_D:	return R.drawable.b_tendrill_rd;
					case N4_D|N4_L:	return R.drawable.b_tendrill_dl;
					case N4_L|N4_U:	return R.drawable.b_tendrill_ul;
					case N4_U|N4_D:	return R.drawable.b_tendrill_ud;
					case N4_R|N4_L:	return R.drawable.b_tendrill_rl;
				}
				break;
			}
			
			case R.drawable.b_tendrill_choking_sprout_d: {
				switch(shape){
					case B_E:		return -1;
					case N4_U:		return R.drawable.b_tendrill_sprout_u;
					case N4_R:		return R.drawable.b_tendrill_sprout_r;
					case N4_D:		return R.drawable.b_tendrill_sprout_d;
					case N4_L:		return R.drawable.b_tendrill_sprout_l;
				}
				break;
			}
			
			case R.drawable.b_stone: {
				switch(shape){
					case B_E:		return R.drawable.b_stone;
					case N4_U:		return R.drawable.b_stone_u;
					case N4_R:		return R.drawable.b_stone_r;
					case N4_D:		return R.drawable.b_stone_d;
					case N4_L:		return R.drawable.b_stone_l;
					case N4_U|N4_R:	return R.drawable.b_stone_ur;
					case N4_R|N4_D:	return R.drawable.b_stone_rd;
					case N4_D|N4_L:	return R.drawable.b_stone_dl;
					case N4_L|N4_U:	return R.drawable.b_stone_ul;
					case N4_U|N4_D:	return R.drawable.b_stone_ud;
					case N4_R|N4_L:	return R.drawable.b_stone_rl;
					case N4_U|N4_R|N4_D:	return R.drawable.b_stone_urd;
					case N4_R|N4_D|N4_L:	return R.drawable.b_stone_rdl;
					case N4_D|N4_L|N4_U:	return R.drawable.b_stone_udl;
					case N4_R|N4_L|N4_U:	return R.drawable.b_stone_url;
					case N4_U|N4_R|N4_D|N4_L:	return R.drawable.b_stone_urdl;
				}
				break;
			}
		}
		return -1;
	}
	
	public int getHighOverlapTypeFromMask(int mask, int overlapper_type){
		int ot = NO_OVERLAP;
		
		if ((mask & N8_L)!=0 && (mask & N8_R)!=0 && (mask & N8_U)!=0 && (mask & N8_D)!=0) ot = FULL_OVERLAP;
		if(ot==FULL_OVERLAP) return ot;
		
		if      ((mask & N8_UL)!=0 && (mask & N8_U)!=0 && (mask & N8_R)!=0 && (mask & N8_DR)!=0) ot =  N4_I | N4_L | N4_D;
		else if ((mask & N8_UR)!=0 && (mask & N8_R)!=0 && (mask & N8_D)!=0 && (mask & N8_DL)!=0) ot =  N4_I | N4_L | N4_U;
		else if ((mask & N8_DR)!=0 && (mask & N8_D)!=0 && (mask & N8_L)!=0 && (mask & N8_UL)!=0) ot =  N4_I | N4_R | N4_U;
		else if ((mask & N8_DL)!=0 && (mask & N8_L)!=0 && (mask & N8_U)!=0 && (mask & N8_UR)!=0) ot =  N4_I | N4_R | N4_D;
		
		else if ((mask & N8_L)!=0 && (mask & N8_U)!=0 && (mask & N8_R)!=0) ot =  N4_D;
		else if ((mask & N8_U)!=0 && (mask & N8_R)!=0 && (mask & N8_D)!=0) ot =  N4_L;
		else if ((mask & N8_D)!=0 && (mask & N8_L)!=0 && (mask & N8_R)!=0) ot =  N4_U;
		else if ((mask & N8_U)!=0 && (mask & N8_L)!=0 && (mask & N8_D)!=0) ot =  N4_R;
		
		else if ((mask & N8_L)!=0 && (mask & N8_U)!=0) ot = N4_R | N4_D;
		else if ((mask & N8_L)!=0 && (mask & N8_D)!=0) ot = N4_R | N4_U;
		else if ((mask & N8_R)!=0 && (mask & N8_U)!=0) ot = N4_L | N4_D;
		else if ((mask & N8_R)!=0 && (mask & N8_D)!=0) ot = N4_L | N4_U;
		
		
		Log.d("OVERLAP","OVERLAPPERTYPE:"+overlapper_type+", OT:"+ot);
		if(ot==NO_OVERLAP) return ot;
		else return OVERLAP_TYPES.get(overlapper_type).get(ot);
	}
	
	public int getOverlapTypeFromMask(int mask, int overlapper_type){
		int ot = NO_OVERLAP;
		
		if     ((mask & N8_L)!=0 && (mask & N8_R)!=0) ot = FULL_OVERLAP;
		else if((mask & N8_U)!=0 && (mask & N8_D)!=0) ot = FULL_OVERLAP;
		else if((mask & N8_L)!=0 && (mask & N8_UR)!=0 && (mask & N8_DR)!=0) ot = FULL_OVERLAP;
		else if((mask & N8_R)!=0 && (mask & N8_UL)!=0 && (mask & N8_DL)!=0) ot = FULL_OVERLAP;
		else if((mask & N8_U)!=0 && (mask & N8_DL)!=0 && (mask & N8_DR)!=0) ot = FULL_OVERLAP;
		else if((mask & N8_D)!=0 && (mask & N8_UL)!=0 && (mask & N8_UR)!=0) ot = FULL_OVERLAP;
		
		if(ot==FULL_OVERLAP) return ot;
		
		if     (((mask & N8_L)!=0) && ((mask & N8_U)!=0 || (mask & N8_UR)!=0)) ot = N4_I | N4_R | N4_D;
		else if(((mask & N8_U)!=0) && ((mask & N8_L)!=0 || (mask & N8_DL)!=0)) ot = N4_I | N4_R | N4_D;
		else if(((mask & N8_R)!=0) && ((mask & N8_U)!=0 || (mask & N8_UL)!=0)) ot = N4_I | N4_L | N4_D;
		else if(((mask & N8_U)!=0) && ((mask & N8_R)!=0 || (mask & N8_DR)!=0)) ot = N4_I | N4_L | N4_D;
		else if(((mask & N8_L)!=0) && ((mask & N8_D)!=0 || (mask & N8_DR)!=0)) ot = N4_I | N4_R | N4_U;
		else if(((mask & N8_D)!=0) && ((mask & N8_L)!=0 || (mask & N8_UL)!=0)) ot = N4_I | N4_R | N4_U;
		else if(((mask & N8_R)!=0) && ((mask & N8_D)!=0 || (mask & N8_DL)!=0)) ot = N4_I | N4_L | N4_U;
		else if(((mask & N8_D)!=0) && ((mask & N8_R)!=0 || (mask & N8_UR)!=0)) ot = N4_I | N4_L | N4_U;
		
		else if((mask & N8_U)!=0 || ((mask & N8_UL)!=0 && (mask & N8_UR)!=0)) ot = N4_D;
		else if((mask & N8_R)!=0 || ((mask & N8_UR)!=0 && (mask & N8_DR)!=0)) ot = N4_L;
		else if((mask & N8_L)!=0 || ((mask & N8_UL)!=0 && (mask & N8_DL)!=0)) ot = N4_R;
		else if((mask & N8_D)!=0 || ((mask & N8_DL)!=0 && (mask & N8_DR)!=0)) ot = N4_U;
		
		else if((mask & N8_UL)!=0) ot = N4_R | N4_D;
		else if((mask & N8_UR)!=0) ot = N4_L | N4_D;
		else if((mask & N8_DL)!=0) ot = N4_R | N4_U;
		else if((mask & N8_DR)!=0) ot = N4_L | N4_U;
		
		Log.d("OVERLAP","OVERLAPPERTYPE:"+overlapper_type+", OT:"+ot);
		if(ot==NO_OVERLAP) return ot;
		else return OVERLAP_TYPES.get(overlapper_type).get(ot);
	}
	
	public int getShape(){
		switch(type){
			case R.drawable.b_sprout_u:
			case R.drawable.b_stem_u:
			case R.drawable.b_tendrill_sprout_u:
			case R.drawable.b_tendrill_choking_sprout_u:
			case R.drawable.b_tendrill_stem_u:
			case R.drawable.b_stone_u:
				return N4_U;
			case R.drawable.b_sprout_r:
			case R.drawable.b_stem_r:
			case R.drawable.b_tendrill_sprout_r:
			case R.drawable.b_tendrill_choking_sprout_r:
			case R.drawable.b_tendrill_stem_r:
			case R.drawable.b_stone_r:
				return N4_R;
			case R.drawable.b_sprout_d:
			case R.drawable.b_stem_d:
			case R.drawable.b_tendrill_sprout_d:
			case R.drawable.b_tendrill_choking_sprout_d:
			case R.drawable.b_tendrill_stem_d:
			case R.drawable.b_stone_d:
				return N4_D;
			case R.drawable.b_sprout_l:
			case R.drawable.b_stem_l:
			case R.drawable.b_tendrill_sprout_l:
			case R.drawable.b_tendrill_choking_sprout_l:
			case R.drawable.b_tendrill_stem_l:
			case R.drawable.b_stone_l:
				return N4_L;
			case R.drawable.b_stalk_rd:
			case R.drawable.b_tendrill_rd:
			case R.drawable.b_stone_rd:
				return N4_R | N4_D;
			case R.drawable.b_stalk_dl:
			case R.drawable.b_tendrill_dl:
			case R.drawable.b_stone_dl:
				return N4_D | N4_L;
			case R.drawable.b_stalk_ul:
			case R.drawable.b_tendrill_ul:
			case R.drawable.b_stone_ul:
				return N4_U | N4_L;
			case R.drawable.b_stalk_rl:
			case R.drawable.b_tendrill_rl:
			case R.drawable.b_gall_rl:
			case R.drawable.b_stone_rl:
				return N4_R | N4_L;
			case R.drawable.b_stalk_ud:
			case R.drawable.b_tendrill_ud:
			case R.drawable.b_gall_ud:
			case R.drawable.b_stone_ud:
				return N4_U | N4_D;
			case R.drawable.b_stalk_ur:
			case R.drawable.b_tendrill_ur:
			case R.drawable.b_stone_ur:
				return N4_U | N4_R;
			case R.drawable.b_stalk_rdl:
			case R.drawable.b_stone_rdl:
				return N4_R | N4_D | N4_L;
			case R.drawable.b_stalk_urd:
			case R.drawable.b_stone_urd:
				return N4_U | N4_R | N4_D;
			case R.drawable.b_stalk_udl:
			case R.drawable.b_stone_udl:
				return N4_D | N4_L | N4_U;
			case R.drawable.b_stalk_url:
			case R.drawable.b_stone_url:
				return N4_U | N4_R | N4_L;
			case R.drawable.b_stalk_urdl:
			case R.drawable.b_stone_urdl:
				return N4_U | N4_R | N4_D | N4_L;
		}
		return B_E;
	}
	
	@Override
	public int getFocusObject(Entity e, boolean attacked){
		int align = getAlignment(e);
		int mt    = e.getMovingType();
		
		if(mt==MOVING_SWIM || mt==MOVING_FLOAT) return FOCUS_BLOCK;
		
		switch(type){
			case R.drawable.b_mount_ramp_u:
				return mt==MOVING_FLY?FOCUS_NONE:FOCUS_BRIDGE;
			case R.drawable.b_rock:
			case R.drawable.b_stone:
			case R.drawable.b_stone_u:
			case R.drawable.b_stone_r:
			case R.drawable.b_stone_d:
			case R.drawable.b_stone_l:
			case R.drawable.b_stone_rl:
			case R.drawable.b_stone_ud:
			case R.drawable.b_stone_rd:
			case R.drawable.b_stone_rdl:
			case R.drawable.b_stone_dl:
			case R.drawable.b_stone_urd:
			case R.drawable.b_stone_urdl:
			case R.drawable.b_stone_udl:
			case R.drawable.b_stone_ur:
			case R.drawable.b_stone_url:
			case R.drawable.b_stone_ul:
			case R.drawable.b_tendrill_choking_sprout_d:
			case R.drawable.b_tendrill_choking_sprout_l:
			case R.drawable.b_tendrill_choking_sprout_r:
			case R.drawable.b_tendrill_choking_sprout_u:
				return mt==MOVING_FLY?FOCUS_NONE:FOCUS_BLOCK;
				
			case R.drawable.b_seed:
			case R.drawable.b_stem_remains:
			case R.drawable.b_sprout_u:
			case R.drawable.b_sprout_r:
			case R.drawable.b_sprout_d:
			case R.drawable.b_sprout_l:
			case R.drawable.b_stem_u:
			case R.drawable.b_stem_r:
			case R.drawable.b_stem_d:
			case R.drawable.b_stem_l:
			case R.drawable.b_tendrill_stem_d:
			case R.drawable.b_tendrill_stem_r:
			case R.drawable.b_tendrill_stem_l:
			case R.drawable.b_tendrill_stem_u:
			case R.drawable.b_stalk_rl:
			case R.drawable.b_stalk_ud:
			case R.drawable.b_stalk_rd:
			case R.drawable.b_stalk_rdl:
			case R.drawable.b_stalk_dl:
			case R.drawable.b_stalk_urd:
			case R.drawable.b_stalk_urdl:
			case R.drawable.b_stalk_udl:
			case R.drawable.b_stalk_ur:
			case R.drawable.b_stalk_url:
			case R.drawable.b_stalk_ul:
			case R.drawable.b_leaf:
			case R.drawable.b_shooter:
			case R.drawable.b_plant:
			case R.drawable.b_core:
			case R.drawable.b_core_mine:
			case R.drawable.b_flower:
			case R.drawable.b_flower2:
			case R.drawable.b_gall_rl:
			case R.drawable.b_gall2_rl:
			case R.drawable.b_gall_ud:
			case R.drawable.b_gall2_ud:
				attacked = attacked || (e instanceof Bug && ((Bug)e).getAttackDestination()==this);
				return attacked? FOCUS_FOOD : (align==ALIGN_ENEMY?FOCUS_FOOD:(mt==MOVING_FLY?FOCUS_NONE:FOCUS_BLOCK));
		}
		
		return FOCUS_NONE;
	}
	
	public int getBuildTime(){
		return 100;
	}
	
	public boolean isBuilded(){
		return is_builded; //building>=getBuildTime();
	}
	
	public void cancelBuilding(){
		is_builded = true;
	}
	
	protected HashSet<MenuItem> getMenuItems(){
		HashSet<MenuItem> items = new HashSet<MenuItem>();
		Scene scene = SceneView.getScene();
		
		scene.addMenuItemTo(items, R.drawable.b_soil,GameMenu.MENU_TF_BLOCK,0);
		scene.addMenuItemTo(items, R.drawable.b_grass,GameMenu.MENU_TF_BLOCK,1);
		scene.addMenuItemTo(items, R.drawable.b_mount,GameMenu.MENU_TF_BLOCK,2);
		scene.addMenuItemTo(items, R.drawable.b_mount_ramp_u,GameMenu.MENU_TF_BLOCK,3);
		
		scene.addMenuItemsTo(items, new int []{
			//R.drawable.i_dandelion,
			//R.drawable.i_coconut,			
			R.drawable.i_ant_hole1,
			R.drawable.i_bug_hole,
			R.drawable.i_branch,
			R.drawable.i_branch2,
			R.drawable.i_branch3,
			R.drawable.i_dried_leaf,
			R.drawable.i_dried_leaf2,
			R.drawable.i_dried_leaf3,
			R.drawable.i_scarab_lair,
			R.drawable.i_water_mouth_a1,
			R.drawable.i_leaf_chip1,
			R.drawable.i_ant_egg
		}, GameMenu.MENU_TF_ITEM, 4);
		if(SceneView.getGameMode()==GAME_MODE_MAP_EDITOR) scene.addMenuItemTo(items, R.drawable.menu_delete,GameMenu.MENU_TF_ACTION,15);
		
		if(is_builded){
			switch(type){
				
				case R.drawable.b_sand:
				case R.drawable.b_mount:
				case R.drawable.b_soil:
				case R.drawable.b_grass:
					scene.addMenuItemTo(items, R.drawable.b_nitrogen,GameMenu.MENU_TF_BLOCK, 17);
					scene.addMenuItemTo(items, R.drawable.b_nitrogen2,GameMenu.MENU_TF_BLOCK, 18);
					scene.addMenuItemTo(items, R.drawable.b_nitrogen3,GameMenu.MENU_TF_BLOCK, 19);
					if(SceneView.getGameMode()==SceneView.GAME_MODE_MAP_EDITOR) scene.addMenuItemTo(items, R.drawable.b_seed,GameMenu.MENU_TF_BUILD, 20);
				case R.drawable.b_water_ocean_a1:
					scene.addMenuItemsTo(items, new int []{							
						R.drawable.b_high_soil,
						R.drawable.b_high_sand,
						R.drawable.b_high_water_ocean_a1,
					}, GameMenu.MENU_TF_BLOCK, -10);
					scene.addMenuItemsTo(items, new int []{							
						R.drawable.b_sand,
						R.drawable.b_water_ocean_a1,
						R.drawable.b_water_mouth_a1,
						R.drawable.b_water_ud_a1,
						R.drawable.b_water_ul_a1,
						R.drawable.b_water_ur_a1,
						R.drawable.b_rock,
						R.drawable.b_stone,
						R.drawable.b_plant
					}, GameMenu.MENU_TF_BLOCK, 21);					
					
				break;
				
				case R.drawable.b_rock:
					scene.addMenuItemTo(items, R.drawable.b_rock,GameMenu.MENU_TF_BLOCK, 30);
				break;
					
				case R.drawable.b_stone:
				case R.drawable.b_stone_u:
				case R.drawable.b_stone_r:
				case R.drawable.b_stone_d:
				case R.drawable.b_stone_l:
				case R.drawable.b_stone_rl:
				case R.drawable.b_stone_ud:
				case R.drawable.b_stone_rd:
				case R.drawable.b_stone_rdl:
				case R.drawable.b_stone_dl:
				case R.drawable.b_stone_urd:
				case R.drawable.b_stone_udl:
				case R.drawable.b_stone_ur:
				case R.drawable.b_stone_url:
				case R.drawable.b_stone_ul:
					scene.addMenuItemTo(items, R.drawable.b_stone,GameMenu.MENU_TF_BLOCK, 31);
				break;
				
			}
		}
		else {
			scene.addMenuItemTo(items, R.drawable.menu_cancel,GameMenu.MENU_TF_ACTION, 32);
		}
		
		return items;
	}
		
		
	public static int getWidth() {
		return (int)(SPRITE_W*getZoom());
	}
	
	public static int getHeight() {
		return (int)(SPRITE_H*getZoom());
	}
	
	public static int getAbsoluteWidth() {
		return SPRITE_W;
	}
	
	public static int getAbsoluteHeight() {
		return SPRITE_H;
	}
	
	@Override
	public int getMaxHitPoints(){
		return 1000;
		//if(isNitrogenBlock()) return 1000;
		//else return 1000;
	}
	
	public int getMaxNutrient(int nutrient){
		return 100;
	}
	
	public static int getNutrientColor(int nutrient){
		switch(nutrient){
			case WATER: return 0xff0000ff;
			case SUGAR: return 0xffb0b000;
			case NITROGEN: return 0xff8c13b4;
		}
		return 0xffffffff;
	}
	
	public static int getNutrientBgColor(int nutrient){
		switch(nutrient){
			case WATER: return 0xff000040;
			case SUGAR: return 0xff303000;
			case NITROGEN: return 0xff640e80;
		}
		return 0xff000000;
	}
	
	public void setDisplayedType(int displayed_type){
		this.displayed_type = displayed_type;
	}
	
	public int getDisplayedType(){
		int [] hpt;
		int mhp;
		int hp;
		
		if(displayed_type!=0) return displayed_type;
		
		if(is_builded){
			hpt = hitpoint_states.get(type);
			mhp = getMaxHitPoints();
			hp  = (int)hitpoints;
		}
		else{
			hpt = building_states.get(type);
			mhp = getBuildTime();
			hp  = building;
		}
		
		if(hpt!=null) {
			int phase = mhp/(hpt.length+1);
			if(hp <= phase*hpt.length) {
				int i = (mhp-hp)/phase - 1;
				if(i>=hpt.length) i = hpt.length-1;
				return hpt[i];
			}
		}
		return type;
	}

	public void invalidateMenu(){
		if(menu!=null) menu.setValid(false);
	}
	
	@Override
	public boolean hasMenu(){
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
	
	@Override
	protected String getBaseHashKey() {
		return "B_"+getDisplayedType()+"_0"+(overlap_type!=NO_OVERLAP?("_"+overlap_type):"")+"_"+(is_high?'1':'0');
	}

	@Override
	protected String getHashKey() {
		int scaled_angle = (int)(2.0*angle) / 45;
		return "B_"+getDisplayedType()+"_"+scaled_angle+(overlap_type!=NO_OVERLAP?("_"+overlap_type):"")+"_"+(is_high?'1':'0');
	}

	@Override
	protected Bitmap loadBitmap() {
		Bitmap bm = loadBitmapResource(getDisplayedType());
		if(overlap_type != NO_OVERLAP){
			BackgroundBitmapEffect bg_effect = new BackgroundBitmapEffect();
			bg_effect.setBackground(bm.copy(Config.ARGB_8888, true));
			bm = loadBitmapResource(overlap_type).copy(Config.ARGB_8888, true);
			if(type==R.drawable.b_mount){
				//AHSVEffect ahsv_effect = new AHSVEffect(1,1,1,1,null);
				//ahsv_effect.applyEffect(bm);
				bg_effect.setAlphaRate(0.8f);
			}
			bg_effect.applyEffect(bm);
		}
		
		if(is_high && type!=R.drawable.b_mount) {
			if(!bm.isMutable()) {
				Bitmap bm2 = bm.copy(Config.ARGB_8888, true);
				bm.recycle();
				bm = bm2;
			}
			//AHSVEffect ahsv_effect = new AHSVEffect(1,1,1,1,null);
			//ahsv_effect.applyEffect(bm);
			
			Bitmap bm_bg = loadBitmapResource(R.drawable.b_mount);
			BackgroundBitmapEffect bg_effect = new BackgroundBitmapEffect();
			bg_effect.setBackground(bm_bg.copy(Config.ARGB_8888, true));
			bg_effect.setAlphaRate(0.8f);
			bg_effect.applyEffect(bm);
		}
		
		return bm;
	}
	
	@Override
	public boolean isRotationCached(){
		return false;
	}

	@Override
	public void step(long tic) {
		if(hitpoints<=0) SceneView.getScene().setBlock(getLevel(),x,y,null);
		else {
			if(!is_builded) {
				int bt = getBuildTime();
				building++;
				if(building>=bt) {
					is_builded = true;
					if(menu!=null) menu.setValid(false);
					this.addEffect(new LightingEffect(1,30));
					SceneView.getInstance(null).onChangeBlock(getLevel(), x, y);
					SceneView.getScene().addStatValue(player, Scene.STAT_BUILD, 1);
				}
				int mhp = getMaxHitPoints();
				hitpoints += mhp/bt;
				if(hitpoints>mhp) hitpoints = mhp;
			}
			if(menu!=null){
				menu.step(tic);
			}
		}
	}
	
	@Override
	public JSONObject toJSON(ResIdConverter rc) throws JSONException {
		JSONObject o = super.toJSON(rc);
		o.put(JSON_TYPE, rc.resIdToType(type));
		if(displayed_type!=0) o.put(JSON_DISPLAYED_TYPE, rc.resIdToType(displayed_type));
		if(overlap_type!=0)   o.put(JSON_OVERLAP_TYPE,   rc.resIdToType(overlap_type));
		o.put(JSON_LEVEL, level);
		o.put(JSON_X, (int)x);
		o.put(JSON_Y, (int)y);
		if(!is_builded){
			o.put(JSON_IS_BUILDED, is_builded);
			o.put(JSON_BUILDING, building);
		}
		
		if(is_high) o.put(JSON_IS_HIGH, is_high);
		
		return o;
	}
	
	@Override
	public Coord getRandomPosition(){
		int bw = Block.getAbsoluteWidth();
		int bh = Block.getAbsoluteHeight();
		return new Coord(bw*x+rnd.nextInt(bw),bh*y+rnd.nextInt(bh));		
	}
	
	@Override
	public Coord getPosition(){
		int bw = Block.getAbsoluteWidth();
		int bh = Block.getAbsoluteHeight();
		return new Coord(bw*x+bw/2,bh*y+bh/2);
	}
		
}
