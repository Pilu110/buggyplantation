package com.pidogames.buggyplantation.entity.block;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.pidogames.buggyplantation.GameMenu;
import com.pidogames.buggyplantation.Healing;
import com.pidogames.buggyplantation.MenuItem;
import com.pidogames.buggyplantation.R;
import com.pidogames.buggyplantation.ResIdConverter;
import com.pidogames.buggyplantation.Scene;
import com.pidogames.buggyplantation.SceneView;
import com.pidogames.buggyplantation.effect.AHSVEffect;
import com.pidogames.buggyplantation.effect.HealingEffect;
import com.pidogames.buggyplantation.entity.Entity;
import com.pidogames.buggyplantation.entity.item.Shrapnel;
import com.pidogames.buggyplantation.entity.item.bug.Bug;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;

public class PlantBlock extends Block {

	public static final int MAX_POISON = 10;
	public static final int POISON_STAT_COLOR    = 0xff628d20;
	public static final int POISON_STAT_BG_COLOR = 0xff324810;
	
	private static final String JSON_IS_SPIKY   = registerJSONKey("is",PlantBlock.class);
	private static final String JSON_IS_SEARING = registerJSONKey("ie",PlantBlock.class);
	private static final String JSON_NUTRIENTS  = registerJSONKey("n",PlantBlock.class);
	private static final String JSON_POISON     = registerJSONKey("ps",PlantBlock.class);
	
	private static int READ  = 0;
	private static int WRITE = 1;
	
	public static final int SPECIAL_TYPE_STALK	= 1;
	public static final int SPECIAL_TYPE_STEM	= 2;
	public static final int SPECIAL_TYPE_SPROUT= 3;
	public static final int SPECIAL_TYPE_TENDRILL_STALK = 4;
	public static final int SPECIAL_TYPE_TENDRILL_STEM  = 5;
	public static final int SPECIAL_TYPE_TENDRILL_SPROUT= 6;
	public static final int SPECIAL_TYPE_GALL	= 7;
	
	private static final float SPIKY_SKIN = 1.1f;
	
	private Healing healing;
	
	private float [][] nutrients;
	private boolean [] is_nutrient_leaking;
	private boolean is_spiky;
	private int poison;
	
	private boolean is_searing;
	
	private static AHSVEffect poisonousEffect;
	
	private static final Set<Integer> NEED_SUGAR;
	static {
		HashSet<Integer> tempSet = new HashSet<Integer>();
		tempSet.add(R.drawable.b_shooter);
		tempSet.add(R.drawable.b_flower);
		tempSet.add(R.drawable.b_flower2);
		tempSet.add(R.drawable.b_gall_ud);
		tempSet.add(R.drawable.b_gall_rl);
		NEED_SUGAR = Collections.unmodifiableSet(tempSet);
	};

	
	private static final Set<Integer> POISONOUS_PLANTS;
	static {
		HashSet<Integer> tempSet = new HashSet<Integer>();
		addTypes(PlantBlock.getStemTypes(), tempSet);
		addTypes(PlantBlock.getStalkTypes(), tempSet);
		addTypes(PlantBlock.getSproutTypes(), tempSet);
		tempSet.add(R.drawable.b_leaf);
		tempSet.add(R.drawable.b_stem_remains);
		POISONOUS_PLANTS = Collections.unmodifiableSet(tempSet);
	};
	
	private static final Set<Integer> SPIKY_PLANTS;
	static {
		HashSet<Integer> tempSet = new HashSet<Integer>();
		addTypes(PlantBlock.getStalkTypes(), tempSet);
		tempSet.add(R.drawable.b_leaf);
		addTypes(PlantBlock.getTendrillStalkTypes(), tempSet);
		addTypes(PlantBlock.getTendrillSproutTypes(), tempSet);
		addTypes(PlantBlock.getGallTypes(), tempSet);
		SPIKY_PLANTS = Collections.unmodifiableSet(tempSet);
	};
	
	public static final Map<Integer,Integer> MAX_PLANT_HITPOINT;
	static {
		HashMap<Integer,Integer> tempMap = new HashMap<Integer,Integer>();
		addTypes(PlantBlock.getStemTypes(), tempMap, 800);
		addTypes(PlantBlock.getStalkTypes(), tempMap, 300);
		addTypes(PlantBlock.getSproutTypes(), tempMap, 300);
		addTypes(PlantBlock.getTendrillStemTypes(), tempMap, 30);
		addTypes(PlantBlock.getTendrillStalkTypes(), tempMap, 30);
		addTypes(PlantBlock.getTendrillSproutTypes(), tempMap, 30);
		tempMap.put(R.drawable.b_stem_remains, 500);
		tempMap.put(R.drawable.b_seed,	1000);
		tempMap.put(R.drawable.b_shooter, 300);
		tempMap.put(R.drawable.b_leaf, 500);
		tempMap.put(R.drawable.b_flower, 100);
		tempMap.put(R.drawable.b_flower2, 100);
		tempMap.put(R.drawable.b_flytrap, 300);
		tempMap.put(R.drawable.b_gall_ud, 500);
		tempMap.put(R.drawable.b_gall_rl, 500);
		tempMap.put(R.drawable.b_core, 500);
		tempMap.put(R.drawable.b_core_mine, 200);
		MAX_PLANT_HITPOINT = Collections.unmodifiableMap(tempMap);
	};
	
	public static final Map<Integer,Integer> BUILD_TIME;
	static {
		HashMap<Integer,Integer> tempMap = new HashMap<Integer,Integer>();
		addTypes(PlantBlock.getSproutTypes(), tempMap, 100);
		addTypes(PlantBlock.getTendrillStemTypes(), tempMap, 100);
		//addTypes(PlantBlock.getTendrillStalkTypes(), tempMap, 500);
		addTypes(PlantBlock.getTendrillSproutTypes(), tempMap, 100);
		tempMap.put(R.drawable.b_shooter, 1000);
		tempMap.put(R.drawable.b_leaf, 500);
		tempMap.put(R.drawable.b_flower, 5000);
		tempMap.put(R.drawable.b_flower2, 1000);
		tempMap.put(R.drawable.b_flytrap, 1000);
		tempMap.put(R.drawable.b_core_mine, 1500);
		BUILD_TIME = Collections.unmodifiableMap(tempMap);
	};
	
	private static final Set<Integer> STALK_LEVEL_TYPE;
	static {
		HashSet<Integer> tempSet = new HashSet<Integer>();
		addTypes(PlantBlock.getStemTypes(), tempSet);
		addTypes(PlantBlock.getSproutTypes(), tempSet);
		addTypes(PlantBlock.getStalkTypes(), tempSet);
		addTypes(PlantBlock.getGallTypes(), tempSet);
		STALK_LEVEL_TYPE = Collections.unmodifiableSet(tempSet);
	}
	
	private static final Set<Integer> TENDRILL_TYPE;
	static {
		HashSet<Integer> tempSet = new HashSet<Integer>();
		addTypes(PlantBlock.getTendrillStemTypes(), tempSet);
		addTypes(PlantBlock.getTendrillStalkTypes(), tempSet);
		addTypes(PlantBlock.getTendrillSproutTypes(), tempSet);
		TENDRILL_TYPE = Collections.unmodifiableSet(tempSet);
	}
	
	private static final Map<Integer,Integer> SPECIAL_TYPE;
	static {
		HashMap<Integer,Integer> tempMap = new HashMap<Integer,Integer>();
		addTypes(PlantBlock.getStemTypes(), tempMap, SPECIAL_TYPE_STEM);
		addTypes(PlantBlock.getStalkTypes(), tempMap, SPECIAL_TYPE_STALK);
		addTypes(PlantBlock.getSproutTypes(), tempMap, SPECIAL_TYPE_SPROUT);
		addTypes(PlantBlock.getTendrillStemTypes(), tempMap, SPECIAL_TYPE_TENDRILL_STEM);
		addTypes(PlantBlock.getTendrillStalkTypes(), tempMap, SPECIAL_TYPE_TENDRILL_STALK);
		addTypes(PlantBlock.getTendrillSproutTypes(), tempMap, SPECIAL_TYPE_TENDRILL_SPROUT);
		addTypes(PlantBlock.getGallTypes(), tempMap, SPECIAL_TYPE_GALL);
		SPECIAL_TYPE = Collections.unmodifiableMap(tempMap);
	}
	
	public static final int[] getStalkTypes(){
		return new int [] {
			R.drawable.b_stalk_rl,
			R.drawable.b_stalk_ud,
			R.drawable.b_stalk_rd,
			R.drawable.b_stalk_rdl,
			R.drawable.b_stalk_dl,
			R.drawable.b_stalk_urd,
			R.drawable.b_stalk_urdl,
			R.drawable.b_stalk_udl,
			R.drawable.b_stalk_ur,
			R.drawable.b_stalk_url,
			R.drawable.b_stalk_ul
		};
	}
	
	public static final int[] getSproutTypes(){
		return new int [] {
			R.drawable.b_sprout_u,
			R.drawable.b_sprout_r,
			R.drawable.b_sprout_d,
			R.drawable.b_sprout_l
		};
	}
	
	public static final int[] getStemTypes(){
		return new int [] {
			R.drawable.b_stem_u,
			R.drawable.b_stem_r,
			R.drawable.b_stem_d,
			R.drawable.b_stem_l
		};
	}
	
	public static final int[] getTendrillStalkTypes(){
		return new int [] {
			R.drawable.b_tendrill_rl,
			R.drawable.b_tendrill_ud,
			R.drawable.b_tendrill_rd,
			R.drawable.b_tendrill_dl,
			R.drawable.b_tendrill_ur,
			R.drawable.b_tendrill_ul
		};
	}
	
	public static final int[] getTendrillSproutTypes(){
		return new int [] {
			R.drawable.b_tendrill_sprout_u,
			R.drawable.b_tendrill_sprout_r,
			R.drawable.b_tendrill_sprout_d,
			R.drawable.b_tendrill_sprout_l
		};
	}
	
	public static final int[] getTendrillStemTypes(){
		return new int [] {
			R.drawable.b_tendrill_stem_u,
			R.drawable.b_tendrill_stem_r,
			R.drawable.b_tendrill_stem_d,
			R.drawable.b_tendrill_stem_l
		};
	}
	
	public static final int[] getGallTypes(){
		return new int [] {
			R.drawable.b_gall_ud,
			R.drawable.b_gall_rl
		};
	}
	
	public static boolean isTendrillType(int type){
		return TENDRILL_TYPE.contains(type);
	}
	
	public static boolean isStalkLevelType(int type){
		return STALK_LEVEL_TYPE.contains(type);
	}
	
	public static boolean isPoisonousPlant(int type){
		return POISONOUS_PLANTS.contains(type);
	}	
	
	public static boolean isSpikyPlant(int type){
		return SPIKY_PLANTS.contains(type);
	}	
	
	public static Integer getSpecialType(int type){
		return SPECIAL_TYPE.get(type);
	}
	
	public PlantBlock(JSONObject o, ResIdConverter rc) throws JSONException {
		super(o, rc);
		if(o.isNull(JSON_HITPOINTS)) hitpoints = getMaxHitPoints();
		
		nutrients = new float[2][NUTRIENTS];
		is_nutrient_leaking = new boolean[NUTRIENTS];
		JSONArray n = o.getJSONArray(JSON_NUTRIENTS);
		for(int i=0; i<n.length(); i++){
			nutrients[READ][i]	= n.getInt(i);
			nutrients[WRITE][i]	= 0;
		}
		if(n.length()<NUTRIENTS) {
			for(int i=n.length(); i<NUTRIENTS; i++){
				nutrients[READ][i]	= 1;
				nutrients[WRITE][i]	= 0;
				is_nutrient_leaking[i] = false;
			}			
		}
		if(!o.isNull(JSON_IS_SPIKY)) is_spiky = o.getBoolean(JSON_IS_SPIKY);
		else is_spiky = false;
		
		if(!o.isNull(JSON_IS_SEARING)) is_searing = o.getBoolean(JSON_IS_SEARING);
		else is_searing = false;
		
		if(!o.isNull(JSON_POISON)) poison = o.getInt(JSON_POISON);
		else poison = 0;
		
	}
	
	public PlantBlock(int player, int type, int x, int y, int level, boolean is_builded) {
		super(player, type, x, y, level, is_builded);
		hitpoints = getMaxHitPoints();
		nutrients = new float[2][NUTRIENTS];
		is_nutrient_leaking = new boolean[NUTRIENTS];
		
		//int [] price = Price.getInstance().get(type);
		for(int i=0; i<NUTRIENTS; i++){
			if(type==R.drawable.b_seed) {
				nutrients[READ][i] = 50;
			}
			else {
				nutrients[READ][i] = 0;
			}
			
			nutrients[WRITE][i]	= 0;
			is_nutrient_leaking[i] = false;
		}
		is_spiky   = false;
		is_searing = false;
	}
	
	private static void loadPoisonousEffect(Resources res){
		Bitmap mask = BitmapFactory.decodeResource(res, R.drawable.poisonous_mask);
		poisonousEffect = new AHSVEffect(1,1.5f,1,1.5f,mask);
	}

	public boolean isTendrillType(){
		return TENDRILL_TYPE.contains(type);
	}
	
	@Override
	protected String getBaseHashKey() {
		return super.getBaseHashKey() + (poison>0?"_p":"");
	}

	@Override
	protected String getHashKey() {
		return super.getHashKey() + (poison>0?"_p":"");
	}
	
	@Override
	protected Bitmap loadBitmap() {
		Bitmap bm = super.loadBitmap();
		if(poison>0){
			if(!bm.isMutable()) {
				Bitmap bm2 = bm.copy(Config.ARGB_8888, true);
				bm.recycle();
				bm = bm2;
			}
			if(poisonousEffect==null) loadPoisonousEffect(SceneView.getInstance(null).getResources());
			poisonousEffect.applyEffect(bm);
		}
		return bm;
	}
	
	@Override
	public void damage(int hitpoints, Entity e, int player){
		boolean invalidMenu = false;
		if(this.hitpoints==getMaxHitPoints()) invalidMenu = true;
		super.damage(hitpoints, e, player);
		if(poison>0 && e instanceof Bug) {
			((Bug)e).poison(this.player, 1, 50+rnd.nextInt(100), 20);
			poison--;
			if(poison<=0) invalidMenu = true;
		}
		
		if(invalidMenu) invalidateMenu();
	}
	
	public int getPoison(){
		return poison;
	}
	
	public void addPoison(int poison){
		this.poison += poison;
		invalidateMenu();
	}
	
	public void setSpiky(boolean is_spiky){
		if(!this.is_spiky && is_spiky) hitpoints *= SPIKY_SKIN;
		this.is_spiky = is_spiky;
	}
	
	public void setSearing(boolean is_searing){
		this.is_searing = is_searing;
	}
	
	public void toggleSearing(){
		is_searing = !is_searing;
	}
	
	public boolean isSpiky(){
		return is_spiky;
	}
	
	public boolean isSearing(){
		return is_searing;
	}
	
	public boolean isNutrientLeaking(int nutrient){
		return is_nutrient_leaking[nutrient];
	}
	
	@Override
	public int getBuildTime(){
		Integer bt = BUILD_TIME.get(type);
		return bt!=null ? bt : super.getBuildTime();
	}

	@Override
	public int getMaxHitPoints(){
		Integer mhp = MAX_PLANT_HITPOINT.get(type);
		if(mhp==null) mhp = 1000;
		mhp = is_spiky ? (int)(mhp * SPIKY_SKIN) : mhp;
		return mhp;
	}
	
	@Override
	public boolean isPoisonous(){
		return POISONOUS_PLANTS.contains(type);
	}
	
	public boolean needSugar(){
		return NEED_SUGAR.contains(type);
	}
	
	@Override
	protected boolean hasDamageEffect(){
		return false;
	}
	
	@Override
	public void payBackPrice(int [] prices){
		Scene scene = SceneView.getScene();
		for(int i=0; i<prices.length; i++){
			addNutrient(i, prices[i]);
			scene.addStatValue(player, scene.getStatNutrient(false, i), -prices[i]);
		}
	}
	
	@Override
	public int payPrice(int [] prices){
		for(int i=0; i<prices.length; i++){
			if(prices[i]>0 && prices[i]>nutrients[READ][i]) return i;
		}
		Scene scene = SceneView.getScene();
		for(int i=0; i<prices.length; i++){
			addNutrient(i, -prices[i]);
			scene.addStatValue(player, scene.getStatNutrient(false, i), prices[i]);
		}
		return -1;
	}
	
	@Override
	public int getDisplayedType(){
		int type = super.getDisplayedType();
		if(is_spiky) {
			switch(type){
				case R.drawable.b_stalk_rl:	return R.drawable.b_stalk2_rl;
				case R.drawable.b_stalk_ud:	return R.drawable.b_stalk2_ud;
				case R.drawable.b_stalk_rd:	return R.drawable.b_stalk2_rd;
				case R.drawable.b_stalk_rdl:return R.drawable.b_stalk2_rdl;
				case R.drawable.b_stalk_dl:	return R.drawable.b_stalk2_dl;
				case R.drawable.b_stalk_urd:return R.drawable.b_stalk2_urd;
				case R.drawable.b_stalk_urdl:return R.drawable.b_stalk2_urdl;
				case R.drawable.b_stalk_udl:return R.drawable.b_stalk2_udl;
				case R.drawable.b_stalk_ur:	return R.drawable.b_stalk2_ur;
				case R.drawable.b_stalk_url:return R.drawable.b_stalk2_url;
				case R.drawable.b_stalk_ul:	return R.drawable.b_stalk2_ul;
				case R.drawable.b_leaf:	return R.drawable.b_leaf2;
				case R.drawable.b_gall_ud : return R.drawable.b_gall2_ud;
				case R.drawable.b_gall_rl : return R.drawable.b_gall2_rl;
				
				case R.drawable.b_tendrill_sprout_d: return R.drawable.b_tendrill_sprout_thorn_d;
				case R.drawable.b_tendrill_sprout_u: return R.drawable.b_tendrill_sprout_thorn_u;
				case R.drawable.b_tendrill_sprout_l: return R.drawable.b_tendrill_sprout_thorn_l;
				case R.drawable.b_tendrill_sprout_r: return R.drawable.b_tendrill_sprout_thorn_r;
				case R.drawable.b_tendrill_ud: return R.drawable.b_tendrill_thorn_ud;
				case R.drawable.b_tendrill_rl: return R.drawable.b_tendrill_thorn_rl;				
				case R.drawable.b_tendrill_ur: return R.drawable.b_tendrill_thorn_ur;
				case R.drawable.b_tendrill_rd: return R.drawable.b_tendrill_thorn_rd;
				case R.drawable.b_tendrill_dl: return R.drawable.b_tendrill_thorn_dl;
				case R.drawable.b_tendrill_ul: return R.drawable.b_tendrill_thorn_ul;
			}
		}
		return type;
	}
	
	public void heal(int hitpoints, int duration){
		HealingEffect he = new HealingEffect(6,duration);
		he.setEffectListener(this);
		this.addEffect(he);
		
		healing = new Healing(hitpoints, duration);
	}
	
	@Override
	protected HashSet<MenuItem> getMenuItems(){
		HashSet<MenuItem> items = super.getMenuItems();
		Scene scene = SceneView.getScene();
		if(is_builded){
			if(isPoisonous() && poison<=0) scene.addMenuItemTo(items, R.drawable.menu_poison,GameMenu.MENU_TF_ACTION, 100);
			if(hitpoints<getMaxHitPoints() && healing==null) scene.addMenuItemTo(items, R.drawable.menu_heal,GameMenu.MENU_TF_ACTION, 101);
			
			scene.addMenuItemTo(items, R.drawable.menu_sear,GameMenu.MENU_TF_ACTION, 102);
			
			switch(type){
				case R.drawable.b_leaf:
					if(!is_spiky) scene.addMenuItemTo(items, R.drawable.b_stalk2_ud,GameMenu.MENU_TF_BUILD, 103);
				break;
				
				case R.drawable.b_gall_rl:
				case R.drawable.b_gall_ud:
					if(!is_spiky) scene.addMenuItemTo(items, R.drawable.b_stalk2_ud,GameMenu.MENU_TF_BUILD, 104);
					scene.addMenuItemTo(items, R.drawable.bug_wasp_eating1,GameMenu.MENU_TF_ACTION, 105);
				break;
				
				case R.drawable.b_flower:
					scene.addMenuItemTo(items, R.drawable.b_core,GameMenu.MENU_TF_BUILD, 106);
				break;
				
				case R.drawable.b_flower2:
					scene.addMenuItemTo(items, R.drawable.b_core_mine,GameMenu.MENU_TF_BUILD, 107);
				break;
				
				case R.drawable.b_core_mine:
					scene.addMenuItemTo(items, R.drawable.menu_cm_explode,GameMenu.MENU_TF_ACTION, 108);
				break;
				
				case R.drawable.b_seed:
				case R.drawable.b_stem_remains:
					scene.addMenuItemTo(items, R.drawable.b_sprout_d,GameMenu.MENU_TF_BUILD, 109);
				break;
				
				
				/*
				case R.drawable.b_core:
					items.add(new MenuItem(R.drawable.i_dandelion,GameMenu.MENU_TF_BUILD, 107));
				break;
				*/
	
				case R.drawable.b_stalk_rl:
				case R.drawable.b_stalk_ud:
					scene.addMenuItemTo(items, R.drawable.b_gall_ud,GameMenu.MENU_TF_BUILD, 110);
				case R.drawable.b_stalk_rd:
				case R.drawable.b_stalk_rdl:
				case R.drawable.b_stalk_dl:
				case R.drawable.b_stalk_urd:
				case R.drawable.b_stalk_udl:
				case R.drawable.b_stalk_ur:
				case R.drawable.b_stalk_url:
				case R.drawable.b_stalk_ul:
				case R.drawable.b_stalk_urdl:
					scene.addMenuItemsTo(items, new int[]{
						R.drawable.b_tendrill_sprout_d,
						R.drawable.b_leaf,
						R.drawable.b_flower,
						R.drawable.b_shooter,
						R.drawable.b_sprout_d
					}, GameMenu.MENU_TF_BUILD, 110);
					if(!is_spiky) scene.addMenuItemTo(items, R.drawable.b_stalk2_ud,GameMenu.MENU_TF_BUILD, 120);
				break;
				
				case R.drawable.b_stem_u:
				case R.drawable.b_stem_r:
				case R.drawable.b_stem_d:
				case R.drawable.b_stem_l:
					scene.addMenuItemTo(items, R.drawable.b_sprout_d,GameMenu.MENU_TF_BUILD, 121);
				break;
				
				case R.drawable.b_sprout_u:
				case R.drawable.b_sprout_r:
				case R.drawable.b_sprout_d:
				case R.drawable.b_sprout_l:
					scene.addMenuItemTo(items, R.drawable.b_sprout_d,GameMenu.MENU_TF_BUILD, 122);
					scene.addMenuItemTo(items, R.drawable.menu_roots,GameMenu.MENU_TF_BUILD, 123);
				break;			
				
				case R.drawable.b_tendrill_rl:
				case R.drawable.b_tendrill_ud:
				case R.drawable.b_tendrill_rd:
				case R.drawable.b_tendrill_dl:
				case R.drawable.b_tendrill_ur:
				case R.drawable.b_tendrill_ul: {
					if(level==STALK_LEVEL){
						scene.addMenuItemsTo(items, new int[]{
							R.drawable.b_flower2,
							R.drawable.b_flytrap
						}, GameMenu.MENU_TF_BUILD, 124);
					}
					
					if(!is_spiky) scene.addMenuItemTo(items, R.drawable.b_tendrill_sprout_thorn_d,GameMenu.MENU_TF_BUILD, 126);
					
					break;
				}
					
				case R.drawable.b_tendrill_sprout_u:
				case R.drawable.b_tendrill_sprout_r:
				case R.drawable.b_tendrill_sprout_d:
				case R.drawable.b_tendrill_sprout_l: {
					scene.addMenuItemTo(items, R.drawable.b_tendrill_sprout_d,GameMenu.MENU_TF_BUILD, 126);
					if(!is_spiky) scene.addMenuItemTo(items, R.drawable.b_tendrill_sprout_thorn_d,GameMenu.MENU_TF_BUILD, 127);
					Block b = scene.getBlock(STALK_LEVEL, x, y);
					if(b==null || (b==this)) scene.addMenuItemTo(items, R.drawable.menu_roots,GameMenu.MENU_TF_BUILD, 128);
					break;
				}
			}
		}
		return items;
	}
	
	public float getNutrient(int nutrient){
		return nutrients[READ][nutrient];
	}
	
	public void addNutrient(int nutrient, float amount){
		nutrients[READ][nutrient] += amount;
	}
	
	@Override
	public int getMaxNutrient(int nutrient){
		if(TENDRILL_TYPE.contains(type)){
			return 10;
		}
		else return super.getMaxNutrient(nutrient);
	}
	
	@Override
	public void step(long tic) {
		super.step(tic);
		if(!isBuilded()) return;
		
		if(healing!=null){
			
			hitpoints += healing.hp_per_turn;
			healing.duration--;
			
			if(healing.duration<=0){
				healing = null;
				clearEffect(HealingEffect.class);
				invalidateMenu();
			}
			
			final int mhp = getMaxHitPoints();
			if(hitpoints >= mhp) {
				hitpoints = mhp;
				healing = null;
				clearEffect(HealingEffect.class);
				invalidateMenu();
			}			
		}
		
		Scene scene = SceneView.getScene();
		
		if(type==R.drawable.b_tendrill_choking_sprout_d || type==R.drawable.b_tendrill_choking_sprout_l || type==R.drawable.b_tendrill_choking_sprout_r || type==R.drawable.b_tendrill_choking_sprout_u){
			Block b = scene.getBlock(STALK_LEVEL, x, y);
			if(b!=null && b.getHitPoints()>0){
				b.damage(2, this, player);
				if(b instanceof PlantBlock) {
					addNutrient(WATER, 5);
					addNutrient(SUGAR, 5);
					addNutrient(NITROGEN, 5);
				}
				
				int r = (int)(b.getHitPointsRate()*100);
				if(r<4) r = 4;
				
				if(type==R.drawable.b_tendrill_choking_sprout_d){
					displayed_type = (tic%r<r/2)?R.drawable.b_tendrill_choking_sprout_d_a2:0;
				}
				else if(type==R.drawable.b_tendrill_choking_sprout_u){
					displayed_type = (tic%r<r/2)?R.drawable.b_tendrill_choking_sprout_u_a2:0;
				}
				else if(type==R.drawable.b_tendrill_choking_sprout_l){
					displayed_type = (tic%r<r/2)?R.drawable.b_tendrill_choking_sprout_l_a2:0;
				}
				else if(type==R.drawable.b_tendrill_choking_sprout_r){
					displayed_type = (tic%r<r/2)?R.drawable.b_tendrill_choking_sprout_r_a2:0;
				}
				
			}
			else {
				setType(Block.getTypeByShape(R.drawable.b_tendrill_choking_sprout_d, getShape(), false));
				scene.setBlock(STALK_LEVEL, x, y, this);
				scene.setBlock(LEAF_LEVEL, x, y, null);
				int bw = Block.getAbsoluteWidth();
				int bh = Block.getAbsoluteHeight();
				for(int i=0; i<10; i++){
					int sx = x*bw + rnd.nextInt(bw);
					int sy = y*bh + rnd.nextInt(bh);
					int st;
					switch(rnd.nextInt(3)){
						case 0:  st = R.drawable.i_rock_chip1; break;
						case 1:  st = R.drawable.i_rock_chip2; break;
						default: st = R.drawable.i_rock_chip3; break;
					}
					Shrapnel shrapnel = new Shrapnel(PLAYER_NEUTRAL, st, AIR_LEVEL, sx, sy);
					shrapnel.setAngle(rnd.nextInt(360));
					shrapnel.setVelocity(rnd.nextInt(20));
					shrapnel.setResistance(1.5f);
					scene.addItem(shrapnel, true);
				}
			}
		}
		
		if(tic%10==0){
			int step = (int)((tic/1)%3);
			for(int nutrient=0; nutrient<NUTRIENTS;nutrient++){
				switch(step){
					case 0:
						is_nutrient_leaking[nutrient] = false;
						switch(type){
							case R.drawable.b_stem_remains:
							case R.drawable.b_stem_d:
							case R.drawable.b_stem_l:
							case R.drawable.b_stem_u:
							case R.drawable.b_stem_r:
								if(nutrient!=SUGAR){
									Block ground = scene.getBlock(GROUND_LEVEL, x, y);
									if(ground!=null){
										if(nutrient==NITROGEN){
											if(ground.isNitrogenBlock()) {
												if(nutrients[READ][nutrient]>=getMaxNutrient(nutrient)) nutrients[READ][nutrient] = getMaxNutrient(nutrient);
												else {
													nutrients[READ][nutrient] += 1;
													scene.addStatValue(player, Scene.STAT_M_NITROGEN, 1);
													ground.damage(1, this, player);
													int hp = ground.getHitPoints();
													if(hp<=0) {
														ground.setOverlapType(NO_OVERLAP);
														ground.setHitpoints(ground.getMaxHitPoints());
													}
													else if(hp<=100) ground.setOverlapType(R.drawable.b_nitrogen3);
													else if(hp<=500) ground.setOverlapType(R.drawable.b_nitrogen2);
												}
											}
										}
										else if (ground.getType() != R.drawable.b_sand) {
											int max = getMaxNutrient(nutrient);
											if(nutrients[READ][nutrient]+6>max) {
												scene.addStatValue(player, Scene.STAT_M_WATER, max-(int)nutrients[READ][nutrient]);
												nutrients[READ][nutrient] = max;
											}
											else {
												nutrients[READ][nutrient] += 6;
												scene.addStatValue(player, Scene.STAT_M_WATER, 6);
											}
										}
									}
								}
							break;
							case R.drawable.b_seed:
							case R.drawable.b_plant:
							break;
							default:
								if(nutrient==WATER || (nutrient==SUGAR && needSugar())){
									if(nutrients[READ][nutrient]>0) {
										if(nutrient==SUGAR) nutrients[READ][nutrient]-= 2;
										else {
											if(TENDRILL_TYPE.contains(type)) {
												if(tic%50==0) nutrients[READ][nutrient]--;
											}
											else {
												nutrients[READ][nutrient]--;												
											}
										}
									}
									else if(level+1>=scene.getLevels() || scene.getBlock(level+1, x, y) == null) { 
										damage(1, this, player);
									}
									
									if(nutrients[READ][nutrient]<getMaxNutrient(nutrient)/7+1) {
										is_nutrient_leaking[nutrient] = true;
									}
								}
							break;
						}
						
						if (nutrient==SUGAR && type == R.drawable.b_leaf) {
							float sa = getMaxNutrient(SUGAR)-nutrients[READ][SUGAR];
							if(sa>3) sa = 3;
							if(nutrients[READ][WATER]<sa) sa = nutrients[READ][WATER]; 
							nutrients[READ][WATER] -= sa;
							nutrients[READ][SUGAR] += sa;
							scene.addStatValue(player, Scene.STAT_M_SUGAR, (int)sa);
						}
						
					break;
					case 1:
						int level = getLevel();
						int shape = getShape();
						
						int x2,y2,level2;
						double diff = nutrients[READ][nutrient];
						LinkedList<PlantBlock> neighbor = new LinkedList<PlantBlock>();
						for(int dir=0; dir<6; dir++){
							x2=x;y2=y;level2=level;
							switch(dir){
								case LEFT:	if((x<=0) || ((shape&N4_L)==0)) continue; x2--; break;
								case RIGHT:	if((x>=scene.getWidth()-1) || ((shape&N4_R)==0))continue; x2++; break;
								case UP:	if((y<=0) || ((shape&N4_U)==0)) continue; y2--; break;
								case DOWN:	if((y>=scene.getHeight()-1) || ((shape&N4_D)==0)) continue; y2++; break;
								case UNDER:	if(level2<=0) continue; level2--; break;
								case OVER:	if(level2>=scene.getLevels()-1) continue; level2++; break;
							}
							
							
							Block b;
							try {
								b = scene.getBlock(level2, x2, y2);
								if((b==null || !(b instanceof PlantBlock)) && level2>0) {
									level2--;
									b = scene.getBlock(level2, x2, y2);
									if ((b==null || !(b instanceof PlantBlock)) && level2+1<LEAF_LEVEL){
										level2 += 2;
										b = scene.getBlock(level2, x2, y2);									
									}
								}
							} catch(NullPointerException e){
								b = null;							
							} catch(ArrayIndexOutOfBoundsException e){
								b = null;
							}
							
							if(b!=null && b instanceof PlantBlock && b.isBuilded() && !((PlantBlock)b).isSearing()){
								PlantBlock pb = (PlantBlock)b;
								if(pb.nutrients[READ][nutrient] < pb.getMaxNutrient(nutrient) && pb.nutrients[READ][nutrient] < nutrients[READ][nutrient]){
									diff += pb.nutrients[READ][nutrient];
									neighbor.add(pb);
								}
							}
						}
						
						if(neighbor.size()>0){
							if(!is_searing){
								float avg = (float)(diff / (neighbor.size()+1));
								for(PlantBlock b : neighbor){
									float ad = (avg - b.nutrients[READ][nutrient]);
									if(ad>5) ad = 5;
									b.nutrients[WRITE][nutrient] += ad;
									nutrients[WRITE][nutrient] -= ad;
								}
							}
							else {
								float ad = nutrients[READ][nutrient] / neighbor.size();
								if(ad>5) ad = 5;
								for(PlantBlock b : neighbor){
									b.nutrients[WRITE][nutrient] += ad;
									nutrients[WRITE][nutrient] -= ad;
								}
							}
						}
						
					break;
					case 2:
						//is_nutrient_leaking[nutrient] = is_nutrient_leaking[nutrient] | (nutrients[WRITE][nutrient]<0);
						nutrients[READ][nutrient] += nutrients[WRITE][nutrient];
						nutrients[WRITE][nutrient] = 0;
					break;
				}
			}
		}
	}
	
	@Override
	public JSONObject toJSON(ResIdConverter rc) throws JSONException {
		JSONObject o = super.toJSON(rc);
		o.put(JSON_INSTANCEOF, JSON_CLASS_PLANT);
		if(is_spiky) o.put(JSON_IS_SPIKY, is_spiky);
		
		if(is_searing) o.put(JSON_IS_SEARING, is_searing);
		
		if(poison>0) o.put(JSON_POISON, poison);
		
		//nutrients
		JSONArray n = new JSONArray();		
		float na[] = nutrients[READ];
		for(int i=0; i<na.length; i++){
			n.put((int)na[i]);
		}		
		o.put(JSON_NUTRIENTS, n);
		
		return o;
	}

}
