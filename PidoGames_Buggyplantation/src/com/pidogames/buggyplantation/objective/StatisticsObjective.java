package com.pidogames.buggyplantation.objective;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.pidogames.buggyplantation.IdCounter;
import com.pidogames.buggyplantation.R;
import com.pidogames.buggyplantation.ResIdConverter;
import com.pidogames.buggyplantation.Scene;
import com.pidogames.buggyplantation.SceneView;
import com.pidogames.buggyplantation.entity.Entity;
import com.pidogames.buggyplantation.interfaces.AmountSlider;
import com.pidogames.buggyplantation.interfaces.DisplayableState;
import com.pidogames.buggyplantation.menu.DialogBox;
import com.pidogames.buggyplantation.menu.EditorMenu;

public class StatisticsObjective extends Objective implements AmountSlider, DisplayableState {

	public static final int MAX_THOUSANDS = 50;
	
	private int stat_id;
	private int count;
	
	private boolean count_from_start;
	private long start_count;
	
	private float hr, tr;
	
	private static final String JSON_STAT_ID  = Entity.registerJSONKey("si",StatisticsObjective.class);
	private static final String JSON_COUNT    = Entity.registerJSONKey("co",StatisticsObjective.class);
	private static final String JSON_COUNT_FS = Entity.registerJSONKey("cf",StatisticsObjective.class);
	private static final String JSON_START_COUNT = Entity.registerJSONKey("sc",StatisticsObjective.class);
	
	public StatisticsObjective(int id){
		super(id);
		stat_id = Scene.STAT_M_WATER;
		count   = 1000;
		start_count = -1;
		count_from_start = false;
		initRates();
	}
	
	public void setCountFromStart(boolean count_from_start){
		this.count_from_start = count_from_start;
	}
	
	public void toggleCountFromStart(){
		count_from_start = !count_from_start;
	}
	
	public boolean isCountFromStart(){
		return count_from_start;
	}
	
	public void setStatId(int stat_id){
		this.stat_id = stat_id;
	}
	
	public int getStatId(){
		return stat_id;
	}
	
	@Override
	public float getAmountHundredsRate(){
		return hr;
	}
	
	@Override
	public void setAmountHundredsRate(float rate){
		hr = rate;
		setAmount();
	}
	
	@Override
	public float getAmountThousandsRate(){
		return tr;
	}
	
	@Override
	public void setAmountThousandsRate(float rate){
		tr = rate;
		setAmount();
	}
	
	private void initRates(){
		tr = (float)count / (float)(1000*MAX_THOUSANDS);
		hr = (float)(count % 1000) / 1000.0f;
		setAmount();
	}
	
	private void setAmount(){
		count = (int)(tr*MAX_THOUSANDS)*1000 + (int)(hr*9)*100;
	}
	
	public int getCount(){
		return count;
	}
		
	public String getCountSliderTitle(){
		return count+"";
	}
	
	@Override
	public int getType() {
		return OBJECTIVE_TYPE_STATISTICS;
	}

	@Override
	protected boolean checkIfAchieved(long tic) {
		long value = scene.getStatValue(player, stat_id);
		if(start_count<0) start_count = count_from_start?value:0;
		return value-start_count > count;
	}

	@Override
	public JSONArray getTypeSettingsForMenu(){
		JSONArray items = new JSONArray();
		
		try {
			
			JSONObject o = new JSONObject();
			o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_STAT_TYPE+"="+getId());
			o.put(DialogBox.JSON_TITLE, SceneView.getString(R.string.statistics));
			o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
			o.put(DialogBox.JSON_PADDING, DialogBox.SMALL_PADDING);
			o.put(DialogBox.JSON_TYPE, DialogBox.ITEM_TYPE_LIST);
			o.put(DialogBox.JSON_MAX_VALUE, Scene.STATS_COUNT);
			items.put(o);
			
			o = new JSONObject();
			o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_COUNT_FROM_START+"="+getId());
			o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
			o.put(DialogBox.JSON_PADDING, DialogBox.SMALL_PADDING);
			o.put(DialogBox.JSON_TYPE, DialogBox.ITEM_TYPE_CHECKBOX);
			items.put(o);
			
			o = new JSONObject();
			o.put(DialogBox.JSON_CODE, "-");
			o.put(DialogBox.JSON_TITLE, SceneView.getString(R.string.reached_amount));
			o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
			o.put(DialogBox.JSON_ENABLED, false);
			items.put(o);
			
			o = new JSONObject();
			o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_AM_TITLE);
			o.put(DialogBox.JSON_TITLE, getCountSliderTitle());
			o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
			o.put(DialogBox.JSON_PADDING, DialogBox.SMALL_PADDING);
			o.put(DialogBox.JSON_ENABLED, false);
			items.put(o);
			
			o = new JSONObject();
			o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_AM_THOUSANDS+"="+getId());
			o.put(DialogBox.JSON_TITLE, SceneView.getString(R.string.thousands));
			o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
			o.put(DialogBox.JSON_TYPE, DialogBox.ITEM_TYPE_SLIDEBAR);
			items.put(o);			
			
			o = new JSONObject();
			o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_AM_HUNDREDS+"="+getId());
			o.put(DialogBox.JSON_TITLE, SceneView.getString(R.string.hundreds));
			o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
			o.put(DialogBox.JSON_TYPE, DialogBox.ITEM_TYPE_SLIDEBAR);
			items.put(o);			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return items;
	}
	
	@Override
	public String getDisplayableState() {
		return (start_count<0?0:(scene.getStatValue(player, stat_id)-start_count))+" / "+count;
	}

	@Override
	protected void initJSON(JSONObject o, HashMap<Integer, Entity> tmp_ids, ResIdConverter rc) throws JSONException {
		super.initJSON(o,tmp_ids,rc);
		
		stat_id = o.getInt(JSON_STAT_ID);
		count   = o.getInt(JSON_COUNT);
		count_from_start = o.getBoolean(JSON_COUNT_FS);
		start_count = o.getLong(JSON_START_COUNT);
	}
	
	@Override
	public JSONObject toJSON(HashMap<Entity,Integer> tmp_ids, IdCounter counter, ResIdConverter rc) throws JSONException {
		JSONObject o = super.toJSON(tmp_ids,counter,rc);
		
		o.put(JSON_STAT_ID, stat_id);
		o.put(JSON_COUNT, count);
		o.put(JSON_COUNT_FS, count_from_start);
		o.put(JSON_START_COUNT, start_count);
		
		return o;
	}
	
}
