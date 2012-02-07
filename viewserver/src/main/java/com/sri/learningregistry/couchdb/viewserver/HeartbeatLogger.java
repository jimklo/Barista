package com.sri.learningregistry.couchdb.viewserver;

import java.util.LinkedList;
import java.util.Queue;

public class HeartbeatLogger extends Thread {

	protected final Queue<String> messages = new LinkedList<String>();
	protected boolean done = false;
	
	@Override
	public void run() {
		super.run();
		while (!done && messages.peek() != null) {
			
			String msg = messages.poll();
			if (msg != null) {
				ViewServer.log(msg);
			} else {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					setDone();
				}
			}
		}
	}
	
	public void addMessage(String message) {
		messages.add(message);
	}
	
	public void setDone() {
		done = true;
	}
}
