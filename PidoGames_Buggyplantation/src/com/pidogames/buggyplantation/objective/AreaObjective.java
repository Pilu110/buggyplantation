package com.pidogames.buggyplantation.objective;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.Resources;
import android.graphics.Rect;

import com.pidogames.buggyplantation.IdCounter;
import com.pidogames.buggyplantation.R;
import com.pidogames.buggyplantation.ResIdConverter;
import com.pidogames.buggyplantation.Scene;
import com.pidogames.buggyplantation.SceneView;
import com.pidogames.buggyplantation.Util;
import com.pidogames.buggyplantation.entity.Entity;
import com.pidogames.buggyplantation.entity.item.Item;
import com.pidogames.buggyplantation.interfaces.CountSlider;
import com.pidogames.buggyplantation.interfaces.ObjectiveListener;
import com.pidogames.buggyplantation.menu.DialogBox;
import com.pidogames.buggyplantation.menu.EditorMenu;
import com.pidogames.buggyplantation.entity.item.bug.Bug;
import com.pidogames.buggyplantation.entity.block.PlantBlock;
import com.pidogames.buggyplantation.entity.block.ShooterBlock;

public abstract class AreaObjective extends Objective implements CountSlider {
	
	public static final int NITROGEN_TYPE = -2;
	public static final int ALL_TYPE = -1;
		
	public static final int [] TYPE_ITEM_RESID = {

        R.drawable.b_core,
        R.drawable.b_core_mine,
        R.drawable.b_flower,
        R.drawable.b_flower2,
        R.drawable.b_flytrap,
                
        R.drawable.b_leaf,
        R.drawable.b_plant,
        R.drawable.b_rock,
        R.drawable.b_seed,
        R.drawable.b_shooter,
        
        R.drawable.b_nitrogen,
        
        R.drawable.b_gall_ud,
        R.drawable.b_sprout_d,
        R.drawable.b_stalk_ud,
        R.drawable.b_stem_d,
        R.drawable.b_tendrill_sprout_d,
        R.drawable.b_tendrill_ud,
        R.drawable.b_tendrill_stem_d,
        
        R.drawable.bug1_moving1,
        R.drawable.bug3_moving1,
        R.drawable.bug_ant_moving1,
        R.drawable.bug_ant_queen_moving1,
        R.drawable.bug_ant_soldier_moving1,
        R.drawable.bug_ant_winged_moving1,
        R.drawable.bug_wasp_moving1,
        
        R.drawable.i_ant_egg,
        R.drawable.i_ant_hole1,
        
        R.drawable.i_branch,
        R.drawable.i_branch2,
        R.drawable.i_branch3,
        
        R.drawable.i_bug_hole,
        
        R.drawable.i_dandelion,
        R.drawable.i_dried_leaf,
        R.drawable.i_dried_leaf2,
        R.drawable.i_dried_leaf3,
        
        R.drawable.i_leaf_chip1,
        
        R.drawable.i_scarab_lair,
        
        R.drawable.i_seabean,
        R.drawable.i_seabean_open
	};
	
	private static final String JSON_AREA  = Entity.registerJSONKey("r",AreaObjective.class);
	private static final String JSON_TYPE  = Entity.registerJSONKey("y",AreaObjective.class);
	private static final String JSON_COUNT = Entity.registerJSONKey("o",AreaObjective.class);

	private static final String JSON_ONLY_POISONIUS = Entity.registerJSONKey("op",AreaObjective.class);
	private static final String JSON_ONLY_SPIKY     = Entity.registerJSONKey("os",AreaObjective.class);
	private static final String JSON_ONLY_MISSILES_ABOVE = Entity.registerJSONKey("om",AreaObjective.class);	
	
	protected Rect area;
	protected int typeResId;
	
	protected Integer specialType;
	protected Integer bugType;
	
	private boolean onlyPoisonous;
	private boolean onlySpiky;
	private int     onlyMissilesAbove;	
	
	private boolean positive;
	private boolean has_type_select;
	
	protected HashSet<Item> item_counted;
	protected int count;
	protected int sum;
	private float cr;
	
	public AreaObjective(int id, Scene scene, int player, String title, String description, String achieved_description, boolean enabled, ObjectiveListener listener, Rect area, boolean positive, boolean has_type_select) {
		super(id, scene, player, title, description, achieved_description, enabled, listener);
		this.area = area;
		this.positive = positive;
		this.has_type_select  = has_type_select;
		this.typeResId = ALL_TYPE;
		this.item_counted = new HashSet<Item>();
		this.bugType = null;
		this.specialType = null;
		
		this.onlyPoisonous = false;
		this.onlySpiky      = false;
		this.onlyMissilesAbove = 3;
		
		setCount(1);
	}	
	
	public AreaObjective(int id, boolean positive, boolean has_type_select){
		super(id);
		area = new Rect(0,0,scene.getWidth()-1,scene.getHeight()-1);
		this.positive = positive;
		this.has_type_select = has_type_select;
		this.typeResId = ALL_TYPE;
		this.item_counted = new HashSet<Item>();
		this.bugType = null;
		this.specialType = null;
		
		this.onlyPoisonous		= false;
		this.onlySpiky			= false;
		this.onlyMissilesAbove	= 3;
		
		setCount(1);
	}
	
	public boolean getOnlySpiky(){
		return onlySpiky;
	}
	
	public boolean getOnlyPoisonous(){
		return onlyPoisonous;
	}
	
	public int getOnlyMissilesAbove(){
		return onlyMissilesAbove;
	}
	
	public void setOnlySpiky(boolean onlySpiky){
		this.onlySpiky = onlySpiky;
	}
	
	public void setOnlyPoisonous(boolean onlyPoisonous){
		this.onlyPoisonous = onlyPoisonous;
	}
	
	public void setOnlyMissilesAbove(int onlyMissilesAbove){
		this.onlyMissilesAbove = onlyMissilesAbove;
	}
	
	public Rect getArea(){
		return area;
	}
	
	public void setArea(Rect area){
		this.area = area;
	}
	
	public void setType(int type){
		if(type == ALL_TYPE) setCount(1);
		
		Integer bugType = Bug.BUG_TYPE_FROM_RESID.get(type);
		if(bugType!=null) {
			this.bugType = bugType;
		}
		else {
			specialType = PlantBlock.getSpecialType(type);
		}
		
		this.typeResId = type;
	}
	
	public int getMaxCount() {
		return 50;
	}
	
	public void setCount(int count){
		if(count<1) count = 1;
		this.count = count;
		this.cr = (float)count/getMaxCount();
	}
	
	@Override
	public int getCount(){
		return count;
	}
	
	@Override
	public float getCountRate(){
		return cr;
	}
	
	@Override
	public void setCountRate(float cr){
		this.cr = cr;
		int count = (int)(cr*getMaxCount());
		if(count<1) count = 1;
		this.count = count;
	}
	
	@Override
	public String getCountSliderTitle(){
		return count+"";
	}
	
	public abstract int achievedAt(int x, int y);
	
	@Override
	protected boolean checkIfAchieved(long tic) {
		if(positive){
			int sum=0;
			item_counted.clear();
			for(int y=area.top; y<=area.bottom; y++){
				for(int x=area.left; x<=area.right; x++){
					sum += achievedAt(x,y);
					if(sum>=count) return true;
				}
			}
			this.sum = sum;
			return false;
		}
		else {
			for(int y=area.top; y<=area.bottom; y++){
				for(int x=area.left; x<=area.right; x++){
					if(achievedAt(x,y)==0) return false;
				}
			}
			return true;
		}
	}
	
	
	protected boolean hasMatchingType(Entity e){		
		if(bugType!=null){
			if(e instanceof Bug){
				return e.getType()==bugType;
			}
			else {
				return false;
			}
		}
		else {
			
			if(onlySpiky && e instanceof PlantBlock){
				if(!((PlantBlock)e).isSpiky()) return false;
			}
			
			if(onlyPoisonous && e instanceof PlantBlock){
				if(((PlantBlock)e).getPoison()==0) return false;
			}
			
			if(onlyMissilesAbove>3 && e instanceof ShooterBlock) {
				if(((ShooterBlock)e).getMissiles().length<onlyMissilesAbove) return false;				
			}
			
			if(specialType!=null){
				Integer st = PlantBlock.getSpecialType(e.getType());
				return (st!=null && st.equals(specialType));
			}
			else {
				return e.getType()==typeResId;
			}
		}
	}
	
	@Override
	public JSONArray getTypeSettingsForMenu(){
		JSONArray items = new JSONArray();
		
		try {
			
			String range;
			Resources res = SceneView.getInstance(null).getResources();
			
			boolean is_global = false;
			if(area.left==0 && area.top==0 && area.right==scene.getWidth()-1 && area.bottom == scene.getHeight()-1){
				range = res.getString(R.string.global);
				is_global = true;
			}
			else {
				range = "["+area.left+":"+area.top+"] - ["+area.right+":"+area.bottom+"]"; 
			}
			
			JSONObject o = new JSONObject();
			o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_SELECTED_AREA+"="+getId());
			o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
			o.put(DialogBox.JSON_ENABLED, false);
			items.put(o);
			
			o = new JSONObject();
			o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_SELECTED_AREA+"="+getId());
			o.put(DialogBox.JSON_TITLE, range);
			o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
			o.put(DialogBox.JSON_PADDING, DialogBox.SMALL_PADDING);
			items.put(o);
			
			if(!is_global){
				o = new JSONObject();
				o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_SELECT_GLOBAL_AREA+"="+getId());
				o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
				o.put(DialogBox.JSON_PADDING, DialogBox.SMALL_PADDING);
				items.put(o);
			}
			
			if(has_type_select){
				o = new JSONObject();
				o.put(DialogBox.JSON_CODE, "-"); //EditorMenu.MENU_CODE_OBJ_SELECTED_TYPE+"="+getId());
				o.put(DialogBox.JSON_TITLE, res.getString(R.string.mm_obj_selected_type));
				o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
				o.put(DialogBox.JSON_ENABLED, false);
				items.put(o);
				
				o = new JSONObject();
				o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_SELECTED_TYPE+"="+getId());
				o.put(DialogBox.JSON_TITLE, (typeResId!=ALL_TYPE)?((typeResId!=NITROGEN_TYPE)?ResIdConverter.getNameForResid(typeResId):res.getString(R.string.n_nitrogen)):res.getString(R.string.all));
				o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
				o.put(DialogBox.JSON_PADDING, DialogBox.SMALL_PADDING);
				o.put(DialogBox.JSON_CHILDREN, true);
				o.put(DialogBox.JSON_DIALOG_TITLE, res.getString(R.string.mm_obj_selected_type));
				items.put(o);
				
				if(typeResId!=ALL_TYPE){
					
					if(PlantBlock.isPoisonousPlant(typeResId)) {
						o = new JSONObject();
						o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_TOGGLE_POISONOUS+"="+getId());
						o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
						o.put(DialogBox.JSON_TYPE, DialogBox.ITEM_TYPE_CHECKBOX);
						o.put(DialogBox.JSON_PADDING, DialogBox.SMALL_PADDING);
						items.put(o);						
					}
					
					if(PlantBlock.isSpikyPlant(typeResId)) {
						o = new JSONObject();
						o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_TOGGLE_SPIKY+"="+getId());
						o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
						o.put(DialogBox.JSON_TYPE, DialogBox.ITEM_TYPE_CHECKBOX);
						o.put(DialogBox.JSON_PADDING, DialogBox.SMALL_PADDING);
						items.put(o);
					}
					
					if(typeResId==R.drawable.b_shooter){
						o = new JSONObject();
						o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_TOGGLE_MISSILES_ABOVE+"="+getId());
						o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
						o.put(DialogBox.JSON_TYPE, DialogBox.ITEM_TYPE_LIST);
						o.put(DialogBox.JSON_MIN_VALUE, 3);
						o.put(DialogBox.JSON_MAX_VALUE, 7);
						o.put(DialogBox.JSON_PADDING, DialogBox.SMALL_PADDING);
						items.put(o);
					}
					
					if(positive){
						o = new JSONObject();
						o.put(DialogBox.JSON_CODE, "-");
						o.put(DialogBox.JSON_TITLE, SceneView.getInstance(null).getResources().getString(R.string.mm_obj_selected_number));
						o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
						o.put(DialogBox.JSON_ENABLED, false);
						items.put(o);
						
						o = new JSONObject();
						o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_SELECTED_NUMBER+"="+getId());
						o.put(DialogBox.JSON_TITLE, count);
						o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
						o.put(DialogBox.JSON_TYPE, DialogBox.ITEM_TYPE_SLIDEBAR);
						items.put(o);				
					}
					
					o = new JSONObject();
					o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_SELECT_ALL_TYPE+"="+getId());
					o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
					o.put(DialogBox.JSON_PADDING, DialogBox.SMALL_PADDING);
					items.put(o);
				}
			}
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return items;
	}
	
	public JSONArray getAllTypesForMenu(){
		JSONArray items = new JSONArray();
		
		try {
			JSONObject o = new JSONObject();
			o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_SELECTED_TYPE_RESID+"="+getId()+","+NITROGEN_TYPE);
			o.put(DialogBox.JSON_TITLE, SceneView.getString(R.string.n_nitrogen));
			o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
			o.put(DialogBox.JSON_PADDING, DialogBox.SMALL_PADDING);
			items.put(o);
			
			for(int resid : TYPE_ITEM_RESID){
				o = new JSONObject();
				o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_SELECTED_TYPE_RESID+"="+getId()+","+resid);
				o.put(DialogBox.JSON_TITLE, ResIdConverter.getNameForResid(resid));
				o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
				o.put(DialogBox.JSON_PADDING, DialogBox.SMALL_PADDING);
				items.put(o);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return items;
	}	

	@Override
	protected void initJSON(JSONObject o, HashMap<Integer, Entity> tmp_ids, ResIdConverter rc) throws JSONException {
		super.initJSON(o,tmp_ids,rc);
		count = o.getInt(JSON_COUNT);
		setCount(count);
		area  = Util.JSON2Rect(o.getJSONObject(JSON_AREA));
		if(has_type_select && !o.isNull(JSON_TYPE)) {
			int typeResId = o.getInt(JSON_TYPE);
			if(typeResId>=0) typeResId = rc.typeToResId(typeResId);
			
			setType(typeResId);
		}
		else {
			setType(ALL_TYPE);
		}
		
		if(!o.isNull(JSON_ONLY_POISONIUS)) onlyPoisonous = o.getBoolean(JSON_ONLY_POISONIUS);
		else onlyPoisonous = false;
		
		if(!o.isNull(JSON_ONLY_SPIKY)) onlySpiky = o.getBoolean(JSON_ONLY_SPIKY);
		else onlySpiky = false;
		
		if(!o.isNull(JSON_ONLY_MISSILES_ABOVE)) onlyMissilesAbove = o.getInt(JSON_ONLY_MISSILES_ABOVE);
		else onlyMissilesAbove = 3;
		
	}
	
	@Override
	public JSONObject toJSON(HashMap<Entity,Integer> tmp_ids, IdCounter counter, ResIdConverter rc) throws JSONException {
		JSONObject o = super.toJSON(tmp_ids,counter,rc);
		
		o.put(JSON_COUNT, count);
		o.put(JSON_AREA, Util.rect2JSON(area));
		if(has_type_select && typeResId!=ALL_TYPE) o.put(JSON_TYPE, typeResId<0 ? typeResId : rc.resIdToType(typeResId));
		
		if(onlyPoisonous) o.put(JSON_ONLY_POISONIUS, true);
		if(onlySpiky) o.put(JSON_ONLY_SPIKY, true);
		if(onlyMissilesAbove>3) o.put(JSON_ONLY_MISSILES_ABOVE, onlyMissilesAbove);
		
		return o;
	}

}
