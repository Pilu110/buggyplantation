package com.pidogames.buggyplantation.entity.item;

import org.json.JSONException;
import org.json.JSONObject;

import com.pidogames.buggyplantation.ResIdConverter;


import android.util.Log;

public class TurningItem extends Item {

	private double dest_angle; // the destination angle after the turn
	private double turn_angle; // if it != 0, the bug is turning
	private double turn_speed; // how many degrees should the bug turns in one step
	
	public TurningItem(JSONObject o, ResIdConverter rc) throws JSONException{
		super(o, rc);
		turn_angle = 0.0;
		turn_speed = 0.0;
	}
	
	public TurningItem(int player, int type, int level, int x, int y) {
		super(player, type, level, x, y);
		turn_angle = 0.0;
		turn_speed = 0.0;
	}

	@Override
	public void setAngle(double angle){
		while(angle>=360.0) angle -= 360.0;
		while(angle<0.0) angle += 360.0;
		this.dest_angle = angle;
		this.angle = angle;
	}
		
	public boolean isTurning(){
		return turn_angle!=0;
	}
	
	public void turnTo(double turn_angle, int steps){
		this.turn_angle = turn_angle;
		turn_speed = turn_angle / (double)steps;
		dest_angle = angle + turn_angle;
		//last_turned_to = null;
	}
	
	public void turnAdd(double turn_angle){
		this.turn_angle += turn_angle;
		dest_angle += turn_angle;
	}
	
	@Override
	public void step(long tic) {
		if((turn_angle>turn_speed && turn_speed>0.0) || (turn_angle<turn_speed && turn_speed<0.0)){
			angle += turn_speed;
			turn_angle -= turn_speed;
		}
		else {
			turn_angle = 0.0;
			turn_speed = 0.0;
			angle = dest_angle;
		}
		while(angle>=360.0) angle -= 360.0;
		while(angle<0.0) angle += 360.0;
	}
	
}
