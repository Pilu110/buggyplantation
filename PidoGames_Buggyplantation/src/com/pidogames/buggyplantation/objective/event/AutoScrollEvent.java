package com.pidogames.buggyplantation.objective.event;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.pidogames.buggyplantation.Coord;
import com.pidogames.buggyplantation.IdCounter;
import com.pidogames.buggyplantation.R;
import com.pidogames.buggyplantation.ResIdConverter;
import com.pidogames.buggyplantation.SceneView;
import com.pidogames.buggyplantation.entity.Entity;
import com.pidogames.buggyplantation.interfaces.ObjectiveListener;
import com.pidogames.buggyplantation.menu.DialogBox;
import com.pidogames.buggyplantation.menu.EditorMenu;
import com.pidogames.buggyplantation.objective.Objective;

public class AutoScrollEvent extends ObjectiveEvent {
	
	private static final String JSON_SCROLL = Entity.registerJSONKey("s", AutoScrollEvent.class);
	private static final String JSON_ZOOM   = Entity.registerJSONKey("z", AutoScrollEvent.class);
	
	private Coord scroll;
	private double zoom;

	public AutoScrollEvent(int id, Objective parent) {
		super(id, parent);
		scroll   = new Coord(SceneView.getSX(),SceneView.getSY());
		zoom     = SceneView.getZoom();
	}
	
	public void setZoom(Double zoom){
		this.zoom = zoom;
	}
	
	public void setScroll(int x, int y){
		this.scroll = new Coord(x,y);
	}
	
	public double getZoom(){
		return zoom;
	}
	
	public Coord getScroll(){
		return scroll;
	}

	@Override
	public int getType() {
		return OBJECTIVE_EVENT_AUTO_SCROLL;
	}

	@Override
	public void triggerFunction(ObjectiveListener listener) {
		listener.setEvent(this);
	}

	@Override
	public void addEventTypeDetailsForMenu(JSONArray items) throws JSONException {
		
		JSONObject o = new JSONObject();
		o.put(DialogBox.JSON_CODE, EditorMenu.MENU_CODE_OBJ_EVT_SET_VIEWPOINT+"="+getId());
		o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
		o.put(DialogBox.JSON_PADDING, DialogBox.SMALL_PADDING);
		items.put(o);
		
		o = new JSONObject();
		o.put(DialogBox.JSON_CODE, "-");
		o.put(DialogBox.JSON_TITLE, SceneView.getString(R.string.scroll) + ": ["+scroll.x+","+scroll.y+"]");
		o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
		o.put(DialogBox.JSON_PADDING, DialogBox.SMALL_PADDING);
		o.put(DialogBox.JSON_ENABLED, false);
		items.put(o);
		
		o = new JSONObject();
		o.put(DialogBox.JSON_CODE, "-");
		o.put(DialogBox.JSON_TITLE, SceneView.getString(R.string.zoom) + ": " + zoom);
		o.put(DialogBox.JSON_TEXT_SIZE, DialogBox.SMALL_TEXT_SIZE);
		o.put(DialogBox.JSON_PADDING, DialogBox.SMALL_PADDING);
		o.put(DialogBox.JSON_ENABLED, false);
		items.put(o);
	}

	@Override
	protected void initJSON(JSONObject o, HashMap<Integer, Entity> tmp_ids, ResIdConverter rc) throws JSONException {
		super.initJSON(o, tmp_ids, rc);
		scroll = new Coord(o.getJSONObject(JSON_SCROLL));
		zoom   = o.getDouble(JSON_ZOOM);
	}
	
	@Override
	public JSONObject toJSON(HashMap<Entity,Integer> tmp_ids, IdCounter counter, ResIdConverter rc) throws JSONException {
		JSONObject o = super.toJSON(tmp_ids, counter, rc);
		o.put(JSON_SCROLL, scroll.toJSON());
		o.put(JSON_ZOOM, zoom);
		return o;
	}

}
