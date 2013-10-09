package com.fai.minhasfinancas.entity;

public class Entry {
	private int id;
	private String name;
	private float value;
	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	private int type;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
