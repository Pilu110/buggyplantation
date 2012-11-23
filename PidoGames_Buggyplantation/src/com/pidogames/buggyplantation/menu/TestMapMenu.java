package com.pidogames.buggyplantation.menu;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.pidogames.buggyplantation.R;

import android.content.Context;

public class TestMapMenu extends MainMenu {

	private static final Map<String, Integer> ARRAY_RESID;
	static {
		HashMap<String, Integer> tempMap = new HashMap<String, Integer>();
		ARRAY_RESID = Collections.unmodifiableMap(tempMap);
	}
	
	private static final Map<String, Integer> TITLE_RESID;
	static {
		HashMap<String, Integer> tempMap = new HashMap<String, Integer>();
		tempMap.put(MENU_CODE_MAP_EDITOR, R.string.mm_stop_testing);
		TITLE_RESID = Collections.unmodifiableMap(tempMap);
	}

	private static TestMapMenu instance;
	
	protected TestMapMenu(Context context) {
		super(context, "test_map");
	}

	public static TestMapMenu getInstance(Context context){
		if(instance==null) instance = new TestMapMenu(context);
		return instance;
	}

	@Override
	public Integer getTitleResidByCode(String code){
		int ci = code.indexOf('=');
		if (ci>-1) code = code.substring(0, ci);
		Integer resid = TITLE_RESID.get(code);
		if(resid!=null) return resid;
		
		return super.getTitleResidByCode(code);
	}

	@Override
	public Integer getArrayResidByCode(String code){
		int ci = code.indexOf('=');
		if (ci>-1) code = code.substring(0, ci);
		Integer resid = ARRAY_RESID.get(code);
		if(resid!=null) return resid;
		
		return super.getArrayResidByCode(code);
	}

}
