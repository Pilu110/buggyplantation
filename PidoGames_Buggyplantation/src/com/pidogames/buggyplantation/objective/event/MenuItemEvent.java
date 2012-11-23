package com.pidogames.buggyplantation.objective.event;

import java.util.HashMap;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.pidogames.buggyplantation.IdCounter;
import com.pidogames.buggyplantation.R;
import com.pidogames.buggyplantation.ResIdConverter;
import com.pidogames.buggyplantation.Scene;
import com.pidogames.buggyplantation.SceneView;
import com.pidogames.buggyplantation.entity.Entity;
import com.pidogames.buggyplantation.interfaces.ObjectiveListener;
import com.pidogames.buggyplantation.menu.DialogBox;
import com.pidogames.buggyplantation.menu.EditorMenu;
import com.pidogames.buggyplantation.objective.Objective;

public class MenuItemEvent extends ObjectiveEvent {
	
	private static final String JSON_SET_ENABLE = Entity.registerJSONKey("se", MenuItemEvent.class);
	private static final String JSON_MENU_ITEMS = Entity.registerJSONKey("mi", MenuItemEvent.class);
	
	public static final int [] MENU_ITEM_RESID = {
        R.drawable.menu_delete,
        R.drawable.menu_heal,
        R.drawable.menu_poison,
        R.drawable.menu_sear,
        R.drawable.b_core,
        R.drawable.b_core_mine,
        R.drawable.b_flower,
        R.drawable.b_flower2,
        R.drawable.b_gall_ud,
        R.drawable.b_leaf,
        R.drawable.b_seed,
        R.drawable.b_sprout_d,
        R.drawable.b_shooter,
        R.drawable.b_shooter4,
        R.drawable.b_shooter5,
        R.drawable.b_shooter6,
        R.drawable.b_stem_d,
        R.drawable.menu_roots,
        R.drawable.b_flytrap,
        R.drawable.b_tendrill_choking_sprout_d,
        R.drawable.m_thorn,
        R.drawable.i_dandelion,
        R.drawable.i_seabean,
	};
	
	private boolean set_enable;
	private HashSet<Integer> menu_items;

	public MenuItemEvent(int id, Objective parent) {
		super(id, parent);
		set_enable = false;
		menu_items = null;
	}
	
	public boolean isInEnableMode(){
		return set_enable;
	}
	
	public void setEnableMode(boolean set_enable) {
		this.set_enable = set_enable;
	}
	
	public void addItemToList(int resid){
		if(menu_items==null) menu_items = new HashSet<Integer>();
		menu_items.add(resid);
	}
	
	public void removeItemFromList(int resid){
		if(menu_items!=null){
			menu_items.remove(resid);
			if(menu_items.isEmpty()) menu_items = null;
		}
	}

	@Override
	public int getType() {
		return OBJECTIVE_EVENT_MENU_ITEM;
	}

	@Override
	protected void triggerFunction(ObjectiveListener listener) {
		Scene scene = getParent().getScene();
		if(set_enable) {
			if(menu_items==null) scene.setEnabledMenuItems(null);
			else scene.addToEnabledMenuItems(menu_items);
		}
		else {
			if(menu_items==null) scene.setEnabledMenuItems(new HashSet<Integer>());
			else scene.removeFromEnabledMenuItems(menu_items);
		}
		fulfill();
	}
	
	public JSONArray getRemoveItemForMenu(int resid){
		JSONArray items = new JSONArray();
		try {
			JSONObject o = new JSONObject();
			o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_EVT_REMOVE_MENU_ITEM+"="+getId()+","+resid);
			o.put(DialogBox.JSON_TITLE, SceneView.getString(R.string.remove_menu_item));
			items.put(o);
		}
		catch(JSONException e){
			e.printStackTrace();			
		}
		
		return items;		
	}
	
	public JSONArray getSelectedMenuItemsForMenu(){
		JSONArray items = new JSONArray();
		try {
			JSONObject o = new JSONObject();
			o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_EVT_MENU_ITEM_LIST+"="+getId());
			o.put(DialogBox.JSON_TITLE, SceneView.getString(R.string.add_menu_item));
			o.put(DialogBox.JSON_CHILDREN, true);
			o.put(DialogBox.JSON_PADDING, DialogBox.SMALL_PADDING);
			items.put(o);
			
			if(menu_items==null){
				o = new JSONObject();
				o.put(DialogBox.JSON_CODE, "-");
				o.put(DialogBox.JSON_TITLE, SceneView.getString(R.string.all));
				o.put(DialogBox.JSON_ENABLED, false);
				items.put(o);
			}
			else {
				for(Integer resid : menu_items) {
					o = new JSONObject();
					o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_EVT_MI_ITEM_DETAILS+"="+getId()+","+resid);
					o.put(DialogBox.JSON_TITLE, ResIdConverter.getNameForResid(resid));
					o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
					o.put(DialogBox.JSON_PADDING, DialogBox.SMALL_PADDING);
					o.put(DialogBox.JSON_CHILDREN, true);
					items.put(o);
				}
			}
		}
		catch(JSONException e){
			e.printStackTrace();			
		}
		
		return items;
	}
	
	public JSONArray getAllMenuItemsForMenu(){
		JSONArray items = new JSONArray();
		HashSet<Integer> all_items = new HashSet<Integer>();
		for(int resid : MENU_ITEM_RESID) if(menu_items==null || !menu_items.contains(resid)) all_items.add(resid);
		
		
		try {
			for(int resid : all_items){
				JSONObject o = new JSONObject();
				o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_EVT_ADD_MENU_ITEM+"="+getId()+","+resid);
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
	public void addEventTypeDetailsForMenu(JSONArray items) throws JSONException {
		
		JSONObject o = new JSONObject();
		o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_EVT_MI_SET_ENABLED+"="+getId());
		o.put(DialogBox.JSON_TITLE, SceneView.getString(R.string.mm_obj_evt_set_enabled));
		o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
		o.put(DialogBox.JSON_TYPE, DialogBox.ITEM_TYPE_CHECKBOX);
		o.put(DialogBox.JSON_PADDING, DialogBox.SMALL_PADDING);
		items.put(o);
		
		o = new JSONObject();
		o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_EVT_SELECT_MENU_ITEMS+"="+getId());
		o.put(DialogBox.JSON_TITLE, SceneView.getString(R.string.mm_obj_evt_menu_items));
		o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
		o.put(DialogBox.JSON_CHILDREN, true);
		items.put(o);
	}
	
	@Override
	protected void initJSON(JSONObject o, HashMap<Integer, Entity> tmp_ids, ResIdConverter rc) throws JSONException {
		super.initJSON(o, tmp_ids, rc);
		
		set_enable = o.getBoolean(JSON_SET_ENABLE);
		
		if(!o.isNull(JSON_MENU_ITEMS)){
			menu_items = new HashSet<Integer>();
			JSONArray json_menu_items = o.getJSONArray(JSON_MENU_ITEMS);
			for(int i=0; i<json_menu_items.length(); i++){
				menu_items.add(rc.typeToResId(json_menu_items.getInt(i)));
			}
		}
		else {
			menu_items = null;
		}
	}
	
	@Override
	public JSONObject toJSON(HashMap<Entity,Integer> tmp_ids, IdCounter counter, ResIdConverter rc) throws JSONException {
		JSONObject o = super.toJSON(tmp_ids, counter, rc);
		JSONArray json_menu_items = new JSONArray();
				
		o.put(JSON_SET_ENABLE, set_enable);
		
		if(menu_items!=null) {
			for(Integer resid : menu_items){
				json_menu_items.put(rc.resIdToType(resid));
			}
			o.put(JSON_MENU_ITEMS, json_menu_items);
		}
		
		return o;
	}
	
	
}
