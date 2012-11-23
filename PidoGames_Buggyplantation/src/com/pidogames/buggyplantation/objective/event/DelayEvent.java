package com.pidogames.buggyplantation.objective.event;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.Resources;

import com.pidogames.buggyplantation.IdCounter;
import com.pidogames.buggyplantation.R;
import com.pidogames.buggyplantation.ResIdConverter;
import com.pidogames.buggyplantation.SceneView;
import com.pidogames.buggyplantation.entity.Entity;
import com.pidogames.buggyplantation.interfaces.ObjectiveListener;
import com.pidogames.buggyplantation.menu.DialogBox;
import com.pidogames.buggyplantation.menu.EditorMenu;
import com.pidogames.buggyplantation.objective.Objective;

public class DelayEvent extends ObjectiveEvent {

	private static final String JSON_DELAY    = Entity.registerJSONKey("d", DelayEvent.class);
	private static final String JSON_START_AT = Entity.registerJSONKey("s", DelayEvent.class);
	
	public static final int SECOND = 25;
	public static final int MAX_SECONDS = 60;
	
	private long start_at;
	
	private long delay;
	private float tr;
	private float sr;
	
	public DelayEvent(int id, Objective parent) {
		super(id, parent);
		start_at = -1;
		delay = SECOND*5;
		initRates();
	}

	public float getDelaySecondsRate(){
		return sr;
	}
	
	public void setDelaySecondsRate(float rate){
		sr = rate;
		setDelay();
	}
	
	public float getDelayTicsRate(){
		return tr;
	}
	
	public void setDelayTicsRate(float rate){
		tr = rate;
		setDelay();
	}
		
	public void setDelay(){
		int seconds = (int)(sr*MAX_SECONDS);
		int tics    = (int)(tr*(SECOND-1));
		
		delay = seconds*SECOND + tics;
	}
	
	private void initRates(){
		sr = (float)delay / (float)(SECOND*MAX_SECONDS);
		tr = (float)(delay % SECOND) / (float)(SECOND);
	}
	
	public String getSecondsTitle(Resources res){
		long seconds = delay / SECOND;
		return seconds + " " + res.getString(R.string.seconds);
	}
	
	public String getTicsTitle(Resources res){
		long tics = delay % SECOND;
		return tics + " " + res.getString(R.string.tics);
	}
	
	@Override
	public int getType() {
		return OBJECTIVE_EVENT_DELAY;
	}
	
	public void checkIfFulfilled(){
		if(start_at>0 && SceneView.getTic()-start_at-delay>0) fulfill();
	}

	@Override
	protected void triggerFunction(ObjectiveListener listener) {
		start_at = SceneView.getTic();
		listener.setEvent(this);
	}

	@Override
	public void addEventTypeDetailsForMenu(JSONArray items) throws JSONException {
		Resources res = SceneView.getInstance(null).getResources();
		
		JSONObject o = new JSONObject();
		o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_EVT_DELAY_SECONDS+"="+getId());
		o.put(DialogBox.JSON_TITLE, getSecondsTitle(res));
		o.put(DialogBox.JSON_TYPE, DialogBox.ITEM_TYPE_SLIDEBAR);
		o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
		items.put(o);
			
		o = new JSONObject();
		o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_EVT_DELAY_TICS+"="+getId());
		o.put(DialogBox.JSON_TITLE, getTicsTitle(res));
		o.put(DialogBox.JSON_TYPE, DialogBox.ITEM_TYPE_SLIDEBAR);
		o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
		items.put(o);
	}
	
	@Override
	protected void initJSON(JSONObject o, HashMap<Integer, Entity> tmp_ids, ResIdConverter rc) throws JSONException {
		super.initJSON(o, tmp_ids, rc);
		delay    = o.getLong(JSON_DELAY);
		start_at = o.getLong(JSON_START_AT);
		initRates();
	}		
	
	@Override
	public void referenciesFromJSON(JSONObject o, HashMap<Integer, Entity> tmp_ids) throws JSONException {
		super.referenciesFromJSON(o, tmp_ids);
	}
	
	@Override
	public JSONObject toJSON(HashMap<Entity,Integer> tmp_ids, IdCounter counter, ResIdConverter rc) throws JSONException {
		JSONObject o = super.toJSON(tmp_ids, counter, rc);
		o.put(JSON_DELAY, delay);
		o.put(JSON_START_AT, start_at);
		return o;
	}
}
