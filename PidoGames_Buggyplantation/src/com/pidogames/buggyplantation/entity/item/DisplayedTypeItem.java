package com.pidogames.buggyplantation.entity.item;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.pidogames.buggyplantation.R;
import com.pidogames.buggyplantation.ResIdConverter;

public class DisplayedTypeItem extends Item {

	protected static final String JSON_DISPLAYED_TYPE = registerJSONKey("d", DisplayedTypeItem.class);
	
	private static HashMap<Integer,int[]> displayed_types = new HashMap<Integer,int[]>();
	static {
		displayed_types.put(R.drawable.i_leaf_chip1, new int[]{R.drawable.i_leaf_chip2,R.drawable.i_leaf_chip3});
	}
	
	private int displayed_type;
	
	public DisplayedTypeItem(JSONObject o, ResIdConverter rc) throws JSONException {
		super(o, rc);
		if(!o.isNull(JSON_DISPLAYED_TYPE)) displayed_type = rc.typeToResId(o.getInt(JSON_DISPLAYED_TYPE));
		else displayed_type = 0;
	}
	
	public DisplayedTypeItem(int player, int type, int level, int x, int y) {
		super(player, type, level, x, y);
		displayed_type = getRandomDisplayedType(type);
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
	
	@Override
	public int getDisplayedType(){
		return (displayed_type>0)?displayed_type:type;
	}
	
	@Override
	public JSONObject toJSON(ResIdConverter rc) throws JSONException {
		JSONObject o = super.toJSON(rc);
		o.put(JSON_INSTANCEOF, JSON_CLASS_DISPLAYED_TYPE_ITEM);
		if(displayed_type!=0) o.put(JSON_DISPLAYED_TYPE, rc.resIdToType(displayed_type));
		return o;
	}
}
