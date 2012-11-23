package com.pidogames.buggyplantation.objective;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.pidogames.buggyplantation.IdCounter;
import com.pidogames.buggyplantation.R;
import com.pidogames.buggyplantation.ResIdConverter;
import com.pidogames.buggyplantation.SceneView;
import com.pidogames.buggyplantation.entity.Entity;
import com.pidogames.buggyplantation.entity.block.Block;
import com.pidogames.buggyplantation.entity.block.PlantBlock;
import com.pidogames.buggyplantation.interfaces.AmountSlider;
import com.pidogames.buggyplantation.interfaces.DisplayableState;
import com.pidogames.buggyplantation.menu.DialogBox;
import com.pidogames.buggyplantation.menu.EditorMenu;

public class ResourceObjective extends AreaObjective implements DisplayableState, AmountSlider {

	public static final int MAX_THOUSANDS = 50;
	
	private float hr, tr;
	private int resource;
	private int stored_amount;
	
	private static final String JSON_RESOURCE = Entity.registerJSONKey("re",ResourceObjective.class);
	private static final String JSON_STORED_AMOUNT = Entity.registerJSONKey("sa",ResourceObjective.class);
	
	public ResourceObjective(int id) {
		super(id, true, false);
		resource = Block.NITROGEN;
		stored_amount = 0;
		initRates();
	}
	
	public int getResource(){
		return resource;
	}
	
	public void setResource(int resource){
		this.resource = resource;
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
	
	/*
	public int getStoredAmount(){
		int sum=0;
		for(int y=area.top; y<=area.bottom; y++){
			for(int x=area.left; x<=area.right; x++){
				sum += achievedAt(x,y);
				if(sum>=count) return sum;
			}
		}
		return sum;
	}
	*/
	
	public int getStoredAmount(){
		return stored_amount;
	}
	
	@Override
	protected boolean checkIfAchieved(long tic) {
		int sum=0;
		for(int y=area.top; y<=area.bottom; y++){
			for(int x=area.left; x<=area.right; x++){
				sum += achievedAt(x,y);
				if(sum>=count) {
					stored_amount = count;
					return true;
				}
			}
		}
		
		stored_amount = sum;
		return false;
	}
	
	@Override
	public int achievedAt(int x, int y) {
		int sum=0;
		for(int level=Block.STALK_LEVEL; level<=Block.LEAF_LEVEL; level++){
			Block b = scene.getBlock(level, x, y);			
			if(b!=null && b.getPlayer()==player && b instanceof PlantBlock){
				sum += ((PlantBlock)b).getNutrient(resource);
			}
		}
		return sum;
	}

	@Override
	public int getType() {
		return OBJECTIVE_TYPE_RESOURCE;
	}
	
	@Override
	public JSONArray getTypeSettingsForMenu(){
		JSONArray items = super.getTypeSettingsForMenu();
		
		try {
			JSONObject o = new JSONObject();
			o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_RESOURCE_TYPE+"="+getId());
			o.put(DialogBox.JSON_TITLE, SceneView.getString(R.string.resource));
			o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
			o.put(DialogBox.JSON_PADDING, DialogBox.SMALL_PADDING);
			o.put(DialogBox.JSON_TYPE, DialogBox.ITEM_TYPE_LIST);
			o.put(DialogBox.JSON_MAX_VALUE, Block.NUTRIENTS);
			items.put(o);
			
			o = new JSONObject();
			o.put(DialogBox.JSON_CODE, "-");
			o.put(DialogBox.JSON_TITLE, SceneView.getString(R.string.stored_amount));
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
		return stored_amount+" / "+count;
	}
	
	@Override
	protected void initJSON(JSONObject o, HashMap<Integer, Entity> tmp_ids, ResIdConverter rc) throws JSONException {
		super.initJSON(o,tmp_ids,rc);
		
		resource = o.getInt(JSON_RESOURCE);
		stored_amount = o.getInt(JSON_STORED_AMOUNT);
	}
	
	@Override
	public JSONObject toJSON(HashMap<Entity,Integer> tmp_ids, IdCounter counter, ResIdConverter rc) throws JSONException {
		JSONObject o = super.toJSON(tmp_ids,counter,rc);
		
		o.put(JSON_RESOURCE, resource);
		o.put(JSON_STORED_AMOUNT, stored_amount);
		
		return o;
	}
	
}
