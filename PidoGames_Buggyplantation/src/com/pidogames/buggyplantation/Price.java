package com.pidogames.buggyplantation;

import java.util.HashMap;

public class Price extends HashMap<Integer, int []> {
	
	private static Price instance;
	private static final int [] NULL_P = new int[] {0,0,0};

	private Price(){
		super();
		load();
	}
	
	private void load(){					//			water	sugar	nitrogen
		put(R.drawable.b_sprout_d,			new int[]{	5,		0,		5});
		put(R.drawable.b_leaf,				new int[]{	50,		0,		15});
		put(R.drawable.b_core,				new int[]{	95,		95,		95});
		put(R.drawable.i_dandelion,			new int[]{	10,		100,	100});
		put(R.drawable.i_seabean,			new int[]{	50,		50,		100});
		put(R.drawable.b_core_mine,			new int[]{	100,	10,		25});
		put(R.drawable.b_flower,			new int[]{	250,	100,	50});
		put(R.drawable.b_flower2,			new int[]{	25,		50,		10});
		put(R.drawable.b_flytrap,			new int[]{	25,		25,		10});
		put(R.drawable.b_tendrill_sprout_d,	new int[]{	5,		5,		1});
		put(R.drawable.b_shooter,			new int[]{	50,		50,		50});
		put(R.drawable.b_shooter4,			new int[]{	50,		50,		25});
		put(R.drawable.b_shooter5,			new int[]{	60,		50,		50});
		put(R.drawable.b_shooter6,			new int[]{	70,		50,		75});
		put(R.drawable.b_tendrill_sprout_thorn_d, 
											new int[]{	30,		10,		20});
		put(R.drawable.b_stalk2_ud,			new int[]{	30,		10,		10});
		put(R.drawable.b_gall_ud,			new int[]{	200,	300,	250});
		
		put(R.drawable.menu_heal,			new int[]{	5,		5,		5});
		put(R.drawable.menu_poison,			new int[]{	5,		5,		5});
		
		put(R.drawable.i_dandelion,			new int[]{	50,		100,	50});
		put(R.drawable.i_seabean,			new int[]{	200,	50,		100});
		
		put(R.drawable.bug_wasp_eating1,	new int[]{	50,		100,	50});
	}
	
	@Override 
	public int[] get(Object key){
		int[] p = super.get(key);
		if(p==null) p = NULL_P;
		return p;
	}
	
	public static Price getInstance(){
		if(instance==null) instance = new Price();
		return instance;
	}
}
