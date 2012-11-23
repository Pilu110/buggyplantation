package com.pidogames.buggyplantation;

import com.pidogames.buggyplantation.effect.HealingEffect;

public class Healing {
	public float hp_per_turn;
	public int duration;
	
	public Healing(int hitpoints, int duration){
		this.duration    = duration;
		this.hp_per_turn = (float)hitpoints/(float)duration;
	}
}
