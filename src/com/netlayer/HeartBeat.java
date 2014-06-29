package com.netlayer;

public class HeartBeat extends Thread {
	
	private ThreadManger thm;
	private boolean alive =false;
	
	public HeartBeat(ThreadManger thm){
		this.thm=thm;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(alive){
			thm.tt();
		}
	}
	
	private void setAliveTrue(){
		this.alive=true;
	}
	
	public void setAliveFalse(){
		this.alive=false;
		thm.rr();
	}
	
	@Override
	public synchronized void start() {
		// TODO Auto-generated method stub
		setAliveTrue();
		super.start();
	}
}
