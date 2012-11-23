package com.pidogames.buggyplantation.objective;

import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.Resources;

import com.pidogames.buggyplantation.IdCounter;
import com.pidogames.buggyplantation.R;
import com.pidogames.buggyplantation.ResIdConverter;
import com.pidogames.buggyplantation.Scene;
import com.pidogames.buggyplantation.SceneView;
import com.pidogames.buggyplantation.entity.Entity;
import com.pidogames.buggyplantation.interfaces.DisplayableState;
import com.pidogames.buggyplantation.interfaces.ObjectiveListener;
import com.pidogames.buggyplantation.menu.DialogBox;
import com.pidogames.buggyplantation.menu.EditorMenu;
import com.pidogames.buggyplantation.objective.event.ObjectiveEvent;

public class DelayObjective extends Objective implements DisplayableState {
	
	public static final int MAX_HOURS = 10;
	
	public static final int SECOND = 25;
	public static final int MINUTE = 60*SECOND;
	public static final int HOUR   = 60*MINUTE;
	
	private static final String JSON_DELAY = Entity.registerJSONKey("l",DelayObjective.class);
	private static final String JSON_START_AT = Entity.registerJSONKey("r",DelayObjective.class);;
	
	private long delay;
	private long start_at;
	
	private float hr,mr,sr;

	public DelayObjective(int id, Scene scene, int player, String title, String description, String achieved_description, boolean enabled, ObjectiveListener listener, long delay) {
		super(id, scene, player, title, description, achieved_description, enabled, listener);
		this.delay = delay;
		initRates();
		start_at = -1;
	}

	public DelayObjective(int id){
		super(id);
		delay = 750;
		initRates();
		start_at = -1;
	}
	
	@Override
	public void restart(){
		super.restart();
		start_at = -1;
	}
	
	public float getDelaySecondsRate(){
		return sr;
	}
	
	public void setDelaySecondsRate(float rate){
		sr = rate;
		setDelay();
	}
	
	public float getDelayMinutesRate(){
		return mr;
	}
	
	public void setDelayMinutesRate(float rate){
		mr = rate;
		setDelay();
	}
	
	public float getDelayHoursRate(){
		return hr;
	}
	
	public void setDelayHoursRate(float rate){
		hr = rate;
		setDelay();
	}
	
	public String getHoursTitle(Resources res){
		long hours = (delay / HOUR);
		return hours + " " + res.getString(R.string.hours);
	}
	
	public String getMinutesTitle(Resources res){
		long minutes = (delay % HOUR) / MINUTE;
		return minutes + " " + res.getString(R.string.minutes);
	}
	
	public String getSecondsTitle(Resources res){
		long seconds = (delay % MINUTE) / SECOND;
		return seconds + " " + res.getString(R.string.seconds);
	}
	
	private void initRates(){
		hr = (float)delay / (float)(HOUR*MAX_HOURS);
		mr = (float)(delay % HOUR) / (float)(HOUR);
		sr = (float)(delay % MINUTE) / (float)(MINUTE);
	}
	
	private void setDelay(){
		int hours   = (int)(hr*MAX_HOURS);
		int minutes = (int)(mr*59);
		int seconds  = (int)(sr*59);
		
		delay = hours*HOUR + minutes*MINUTE + seconds*SECOND;
	}
	
	@Override
	protected boolean checkIfAchieved(long tic) {
		if(start_at<0) start_at = tic;
		return (start_at+delay<tic);
	}
	
	public long getRemainedTime(long tic){
		return (start_at<0)?delay:(start_at+delay-tic);
	}

	@Override
	public int getType() {
		return OBJECTIVE_TYPE_COUNTDOWN;
	}

	
	@Override
	public JSONArray getTypeSettingsForMenu(){
		JSONArray items = new JSONArray();
		
		try {
			Resources res = SceneView.getInstance(null).getResources();
			
			JSONObject o = new JSONObject();
			o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_DELAY_HOURS+"="+getId());
			o.put(DialogBox.JSON_TITLE, getHoursTitle(res));
			o.put(DialogBox.JSON_TYPE, DialogBox.ITEM_TYPE_SLIDEBAR);
			o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
			items.put(o);
			
			o = new JSONObject();
			o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_DELAY_MINUTES+"="+getId());
			o.put(DialogBox.JSON_TITLE, getMinutesTitle(res));
			o.put(DialogBox.JSON_TYPE, DialogBox.ITEM_TYPE_SLIDEBAR);
			o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
			items.put(o);
			
			o = new JSONObject();
			o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_DELAY_SECONDS+"="+getId());
			o.put(DialogBox.JSON_TITLE, getSecondsTitle(res));
			o.put(DialogBox.JSON_TYPE, DialogBox.ITEM_TYPE_SLIDEBAR);
			o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
			items.put(o);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return items;
	}

	@Override
	public String getDisplayableState() {
		long remained = getRemainedTime(SceneView.getTic());
		if(remained>=0){
			remained /= 25;
			long s = remained % 60;
			long m = (remained / 60) % 60;
			long h = remained / 3600;
			return (h<10?("0"+h):h)+":"+(m<10?("0"+m):m)+":"+(s<10?("0"+s):s);
		}
		return null;
	}
	
	@Override
	protected void initJSON(JSONObject o, HashMap<Integer, Entity> tmp_ids, ResIdConverter rc) throws JSONException {
		super.initJSON(o,tmp_ids,rc);
		delay = o.getLong(JSON_DELAY);
		start_at = o.getLong(JSON_START_AT);
		initRates();
	}
	
	@Override
	public JSONObject toJSON(HashMap<Entity,Integer> tmp_ids, IdCounter counter, ResIdConverter rc) throws JSONException {
		JSONObject o = super.toJSON(tmp_ids,counter,rc);
		o.put(JSON_DELAY, delay);
		o.put(JSON_START_AT, start_at);
		return o;
	}
	
}
