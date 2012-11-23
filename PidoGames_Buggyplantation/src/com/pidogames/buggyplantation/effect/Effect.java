package com.pidogames.buggyplantation.effect;


import android.graphics.Bitmap;

public abstract class Effect {
	EffectListener el;
	boolean enabled;
	
	public Effect(){
		enabled = true;		
	}
	
	public void setEnabled(boolean enabled){
		this.enabled = enabled;
	}
	
	public void invertEnabled(){
		enabled = !enabled;
	}
	
	public void setEffectListener(EffectListener el){
		this.el = el;
	}
	
	public abstract void applyEffect(Bitmap dst);
}
