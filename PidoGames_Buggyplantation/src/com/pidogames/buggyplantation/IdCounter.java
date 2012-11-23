package com.pidogames.buggyplantation;

public class IdCounter {
	private int id;
	
	public IdCounter(int id){
		this.id = id;
	}
	
	public int getId(){
		return id;
	}
	
	public int useId(){
		id++;
		return id-1;
	}
	
	public void inc(){
		id++;
	}
}
