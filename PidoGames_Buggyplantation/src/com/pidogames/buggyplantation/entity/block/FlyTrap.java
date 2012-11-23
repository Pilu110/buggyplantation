package com.pidogames.buggyplantation.entity.block;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.pidogames.buggyplantation.GameMenu;
import com.pidogames.buggyplantation.MenuItem;
import com.pidogames.buggyplantation.R;
import com.pidogames.buggyplantation.ResIdConverter;
import com.pidogames.buggyplantation.Scene;
import com.pidogames.buggyplantation.SceneView;
import com.pidogames.buggyplantation.entity.Entity;
import com.pidogames.buggyplantation.entity.item.Item;
import com.pidogames.buggyplantation.entity.item.bug.Bug;

public class FlyTrap extends Bait {
	
	private boolean is_closed;
	private int sleep;
	private Bug trapped;

	protected static final String JSON_TRAPPED   = registerJSONKey("tp", FlyTrap.class);	
	protected static final String JSON_IS_CLOSED = registerJSONKey("ic", FlyTrap.class);	
	protected static final String JSON_SLEEP     = registerJSONKey("s", FlyTrap.class);	
	
	public FlyTrap(JSONObject o, ResIdConverter rc) throws JSONException {
		super(o, rc);
		if(!o.isNull(JSON_TRAPPED)){
			trapped = (Bug)Item.getItemFromJSON(o.getJSONObject(JSON_TRAPPED), rc); //new Bug(o.getJSONObject(JSON_TRAPPED));
		}
		else trapped = null;
		
		if(!o.isNull(JSON_IS_CLOSED)){
			is_closed = o.getBoolean(JSON_IS_CLOSED);
		}
		else 
			is_closed = false;
			
		if(!o.isNull(JSON_SLEEP)){
			sleep = o.getInt(JSON_SLEEP);
		}
		else
			sleep = 0;
	}
	
	public void setClosed(boolean is_closed){
		this.is_closed = is_closed;
		setActive(!is_closed);
		if(menu!=null) menu.setValid(false);
		Bug b = trapped;
		if(!is_closed && b!=null){
			trapped = null;
			Scene scene = SceneView.getScene();
			b.poison(getPlayer(), 1, 100, 5);
			b.setDestination(null, false, 0, 0, false, true, null);
			b.setState(Bug.MOVING);
			scene.addItem(b, false);
			sleep = 100;
		}
	}
	
	public Bug getTrapped(){
		return trapped;
	}
	
	public boolean isClosed(){
		return is_closed;
	}

	public FlyTrap(int player, int type, int x, int y, int level, boolean is_builded) {
		super(player, type, x, y, level, is_builded);
		setClosed(false);
	}
	
	@Override
	public int getDisplayedType(){
		return is_closed ? R.drawable.b_flytrap_closed : R.drawable.b_flytrap;
	}
	
	@Override
	protected HashSet<MenuItem> getMenuItems(){
		HashSet<MenuItem> items = super.getMenuItems();
		Scene scene = SceneView.getScene();
		if(is_builded){
			scene.addMenuItemTo(items, is_closed?R.drawable.b_flytrap:R.drawable.b_flytrap_closed,GameMenu.MENU_TF_ACTION, 1000);
		}
		
		return items;
	}
	
	@Override
	public int getFocusObject(Entity e){
		int align = getAlignment(e);
		int mt    = e.getMovingType();
		if(mt == MOVING_MOVE){
			if(align == Entity.ALIGN_ALLY){
				return FOCUS_BLOCK;
			}
			else {
				if(is_closed){
					return FOCUS_FOOD;
				}
				else {
					return FOCUS_NONE;
				}
			}
		}
		else return FOCUS_NONE;
	}
	
	@Override
	public void step(long tic){
		setActive(sleep==0);
		super.step(tic);
		if(is_builded){
			if(!is_closed){
				if(sleep==0 && tic % 10 == 0){
					Scene scene = SceneView.getScene();
					Map<Item, Boolean> items = scene.getItemsAt(x,y);
					if(items!=null){
						for(Item item : items.keySet()){
							if(item instanceof Bug && getAlignment(item)!=ALIGN_ALLY){
								if(position.d2(item.getPosition())<1000){
									Bug b = (Bug)item;
									if(b.getMovingType()==Bug.MOVING_MOVE){
										scene.removeItem(b);
										scene.itemToGrid(b, false, true);
										b.setState(Bug.SQUASHED);
										trapped = b;
										setClosed(true);
										break;
									}
								}
							}
						}
					}
				}
			}
			else if(trapped != null && tic%4==0){
				trapped.damage(1, this, player);
				addNutrient(WATER, 4);
				addNutrient(SUGAR, 4);
				addNutrient(NITROGEN, 2);
				if(trapped.getHitPoints()<=0){
					trapped = null;
					setClosed(false);
				}
			}
			
			if(sleep>0) sleep--;
		}
	}
	
	@Override
	public JSONObject toJSON(ResIdConverter rc) throws JSONException {
		JSONObject o = super.toJSON(rc);
		if (trapped!=null) o.put(JSON_TRAPPED, trapped.toJSON(rc));
		o.put(JSON_INSTANCEOF, JSON_CLASS_FLYTRAP);
		if(is_closed) o.put(JSON_IS_CLOSED, is_closed);
		if(sleep!=0)  o.put(JSON_SLEEP, sleep);
		return o;
	}
	
	@Override
	public JSONObject referenciesToJSON(HashMap<Entity, Integer> tmp_ids) throws JSONException{
		JSONObject o = super.referenciesToJSON(tmp_ids);
		
		if(trapped!=null) {
			Integer id = tmp_ids.get(trapped);
			if(id==null) {
				id = tmp_ids.get(this);
				if(id!=null) tmp_ids.put(trapped, -id);
			}
		}
		
		return o;
	}	
}
