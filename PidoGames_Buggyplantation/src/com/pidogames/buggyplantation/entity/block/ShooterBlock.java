package com.pidogames.buggyplantation.entity.block;

import java.util.HashSet;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.pidogames.buggyplantation.GameMenu;
import com.pidogames.buggyplantation.MenuItem;
import com.pidogames.buggyplantation.R;
import com.pidogames.buggyplantation.ResIdConverter;
import com.pidogames.buggyplantation.Scene;
import com.pidogames.buggyplantation.SceneView;
import com.pidogames.buggyplantation.entity.item.Item;
import com.pidogames.buggyplantation.entity.item.Missile;
import com.pidogames.buggyplantation.entity.item.bug.Bug;


public class ShooterBlock extends PlantBlock {

	private static final String JSON_MISSILES = registerJSONKey("m",ShooterBlock.class);
	
	private Missile [] missiles;
	private int ox;
	private int oy;
	
	private double dest_angle; // the destination angle after the turn
	private double turn_angle; // if it != 0, the bug is turning
	private double turn_speed; // how many degrees should the bug turns in one step
	
	private Bug target;
	
	public ShooterBlock(JSONObject o, ResIdConverter rc) throws JSONException {
		super(o, rc);
		this.ox = getAbsoluteWidth()*x + getAbsoluteWidth()/2;
		this.oy = getAbsoluteHeight()*y + getAbsoluteHeight()/2;
		missiles = new Missile[o.getInt(JSON_MISSILES)];
		if(o.isNull(JSON_HITPOINTS)) hitpoints = getMaxHitPoints();
		setAngle(angle);
		target = null;
	}
	
	public ShooterBlock(int player, int type, int x, int y, int level, boolean is_builded) {
		super(player,type,x,y,level,is_builded);
		this.ox = getAbsoluteWidth()*x + getAbsoluteWidth()/2;
		this.oy = getAbsoluteHeight()*y + getAbsoluteHeight()/2;
		switch(type){
			case R.drawable.b_shooter:
				missiles = new Missile[3];
				hitpoints = getMaxHitPoints();
				//for(int i=0; i<missiles.length; i++) missiles[i] = new Missile(this,R.drawable.m_thorn,ox,oy);
				//SceneView.getScene().addMissiles(missiles);
			break;
		}
		setAngle(0);
		target = null;
	}
	
	public Missile[] getMissiles(){
		return missiles;
	}
	
	public void setMissiles(int n){
		
		int md = 0;
		
		if(missiles!=null){
			md = n - missiles.length;
			Scene scene = SceneView.getScene();
			for(Missile m : missiles){
				if(m!=null && m.getState()==Missile.LOADED) scene.removeItem(m); //items.remove(m);
			}
		}
		
		missiles = new Missile[n];		
		hitpoints += md * super.getMaxHitPoints();
	}
	
	@Override
	public int getMaxHitPoints(){
		if(missiles!=null)	return super.getMaxHitPoints() * missiles.length;
		else				return super.getMaxHitPoints();
	}
	
	@Override
	protected HashSet<MenuItem> getMenuItems(){
		HashSet<MenuItem> items = super.getMenuItems();		
		if(is_builded){
			Scene scene = SceneView.getScene();
			switch(type){
				case R.drawable.b_shooter:
					switch(missiles.length){
						case 3: scene.addMenuItemTo(items, R.drawable.b_shooter4, GameMenu.MENU_TF_BUILD, 200); break;
						case 4: scene.addMenuItemTo(items, R.drawable.b_shooter5, GameMenu.MENU_TF_BUILD, 201); break;
						case 5: scene.addMenuItemTo(items, R.drawable.b_shooter6, GameMenu.MENU_TF_BUILD, 202); break;
					}
				break;
			}
		}
		return items;
	}

	
	@Override
	public int getDisplayedType(){
		int type = super.getDisplayedType();
		switch(missiles.length){
			case 4 : 
				switch(type){
					case R.drawable.b_shooter_d1: type = R.drawable.b_shooter4_d1; break;
					case R.drawable.b_shooter_d2: type = R.drawable.b_shooter4_d2; break;
					default: type = R.drawable.b_shooter4; break;
				}
			break;
			case 5 : 
				switch(type){
				case R.drawable.b_shooter_d1: type = R.drawable.b_shooter5_d1; break;
				case R.drawable.b_shooter_d2: type = R.drawable.b_shooter5_d2; break;
				default: type = R.drawable.b_shooter5; break;
			}
			break;
			case 6 : 
				switch(type){
				case R.drawable.b_shooter_d1: type = R.drawable.b_shooter6_d1; break;
				case R.drawable.b_shooter_d2: type = R.drawable.b_shooter6_d2; break;
				default: type = R.drawable.b_shooter6; break;
			}
			break;
		}
		return type;
	}
	
	private void reloadMissile(int i){
		Missile m = new Missile(player, R.drawable.m_thorn, STALK_LEVEL, ox, oy, this.getRange());
		double a = 360.0/missiles.length;
		m.setAngle(angle + i*a);
		m.setXY(ox, oy);
		m.forward(20);
		missiles[i] = m;
		SceneView.getScene().addItem(missiles[i], true);		
	}
	
	public int getRange(){
//		switch(type){
//			case R.drawable.b_shooter: return 150;
//		}
		return 300;
	}
	
	public long getReloadTime(){
//		switch(type){
//			case R.drawable.b_shooter: return 50;
//		}
		return 25;
	}
	
	@Override
	public void step(long tic) {
		super.step(tic);
		if(!isBuilded()) return;
		
		for(int i=0; i<missiles.length; i++) {
			Missile m = missiles[i];
			if((m==null) || ((m.getState()!=Missile.LOADED) && (m.getFiredAt()+getReloadTime()<tic))) {
				reloadMissile(i);
			}
		}
		
		int sqr_range = getRange();
		sqr_range = sqr_range*sqr_range;
		if(target==null){
			List<Item> bugs = SceneView.getScene().getItems(BUG_LEVEL);
			if(bugs!=null) {
				for(Item item : bugs){
					if(item instanceof Bug){
						Bug b = (Bug)item;
						if(getAlignment(b)== ALIGN_ENEMY) {
							if(b.getState()!=Bug.SQUASHED){
								int x = (int)b.getX();
								int y = (int)b.getY();
								int dx = ox - x;
								int dy = oy - y;
								if(dx*dx + dy*dy <= sqr_range){
									turnToCoord(x,y, 1);
									target = b;
									break;
								}
							}
						}
					}
				}
			}
		}
		else {
			if(target.getState()!=Bug.SQUASHED){
				int x = (int)target.getX();
				int y = (int)target.getY();
				int dx = ox - x;
				int dy = oy - y;
				if(dx*dx + dy*dy <= sqr_range){
					turnToCoord(x,y, 1);
				}			
				else {
					target = null;
				}
			}
			else {
				target = null;
				turn_angle = 0;
			}
		}
		
		if(target!=null){
			//if(target!=null) Log.d("TURN","target x:"+target.getX()+",y:"+target.getY()+", state:"+target.getState());
			double n = 360.0/missiles.length;
			if(turn_angle<0) turn_angle += 360;
			int nm = (int)(turn_angle / n);
			if(nm>=missiles.length) nm = 0;
			
			int sm = -1;
			for(int i=nm; i<missiles.length; i++){
				if(missiles[i].getState()==Missile.LOADED) {
					sm = i;
					break;
				}
			}
			for(int i=nm; i>=0; i--){
				if((sm==-1) || (nm-i<sm-nm)) {
					if(missiles[i].getState()==Missile.LOADED) {					
						sm = i;
						break;
					}
				}
				else break;
			}
			if(sm==-1) sm = 0;
			double diff_angle = sm * n;
			turn_angle -= diff_angle;
			if(turn_angle>180) turn_angle -= 360;
			if(turn_angle<-180) turn_angle += 360;
			if((target!= null) && (turn_angle<5) && (turn_angle>-5) && (missiles[sm].getState()==Missile.LOADED)){
				
				//Log.d("FIRE",this+",tic:"+tic+", sm:"+sm+",angle:"+angle+", diff:"+diff_angle+", turn:"+turn_angle+", target x:"+target.getX()+",target y:"+target.getY()+", state:"+target.getState());
				missiles[sm].fire(tic);
			}
			else {
				//Log.d("FIRE","TURN "+this+", sm:"+sm+", angle:"+angle+", turnto:"+turn_angle);
				turnToWithSpeed(turn_angle,5);
				turn();
			}
		}
	}
	
	public void turnToCoord(double x, double y, int steps){
		
		double dx = this.ox - x;
		double dy = this.oy - y;		
		
		double adx = Math.abs(dx);
		double ady = Math.abs(dy);
		
		double angle = 0.0;
		if(dx<0 && dy>0){
			angle = Math.PI/2 - Math.atan(ady/adx);			
		}
		else if(dx<0 && dy==0){
			angle = Math.PI/2;
		}
		else if(dx<0 && dy<0){
			angle = Math.PI/2 + Math.atan(ady/adx);			
		}
		else if(dx==0 && dy<0){
			angle = Math.PI;
		}
		else if(dx>0 && dy<0){
			angle = 3*Math.PI/2 - Math.atan(ady/adx);			
		}
		else if(dx>0 && dy==0){
			angle = 3*Math.PI/2;
		}
		else if(dx>0 && dy>0){
			angle = 3*Math.PI/2 + Math.atan(ady/adx); 
		}
		
		angle = 180.0/Math.PI * angle;		
		
		angle -= this.angle;
		if(angle>180.0) angle -= 360.0;
		if(angle<-180.0) angle += 360.0;
		
		if(steps<1) steps = 1;
		turnTo(angle,steps);
	}
	
	public void turnToWithSpeed(double turn_angle, double turn_speed){
		this.turn_angle = turn_angle;
		this.turn_speed = turn_angle>0?turn_speed:-turn_speed;
		dest_angle = angle + turn_angle;
	}
	
	public void turnTo(double turn_angle, int steps){
		this.turn_angle = turn_angle;
		turn_speed = turn_angle / (double)steps;
		dest_angle = angle + turn_angle;
	}
	
	private void turn(){
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
		
		setAngle(angle);
	}
	
	@Override
	public void setAngle(double angle){
		super.setAngle(angle);
		double a = 360.0/missiles.length;
		for(int i=0; i<missiles.length; i++){
			Missile m = missiles[i];		
			if(m!=null && m.getState()==Missile.LOADED){
				m.setAngle(angle);
				m.setXY(ox, oy);
				m.forward(20);
			}
			angle += a;
		}
	}

	@Override
	public JSONObject toJSON(ResIdConverter rc) throws JSONException {
		JSONObject o = super.toJSON(rc);
		o.put(JSON_INSTANCEOF, JSON_CLASS_SHOOTER);
		o.put(JSON_MISSILES, missiles.length);
		return o;
	}
	
}
