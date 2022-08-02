package com.db.awmd.challenge.domain;

import java.math.BigDecimal;

public class Transfer {
	private  String fromId;
	private  String toId;
	private  BigDecimal amount;

	public Transfer() {
	}
	
	public Transfer(String fromId, String toId, BigDecimal amount) {
		super();
		this.fromId = fromId;
		this.toId = toId;
		this.amount = amount;
	}

	public String getFromId() {
		return fromId;
	}

	public String getToId() {
		return toId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setFromId(String fromId) {
		this.fromId = fromId;
	}

	public void setToId(String toId) {
		this.toId = toId;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	

}
