package com.camp.sparkservice.domain;

public class UserEventCount {
	private long count;

	public UserEventCount() {
		
	}
	
	public UserEventCount(long count) {
		this.count = count;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

}
