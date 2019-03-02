package com.shinetvbox.vod.db;

public abstract class BaseQuery {
	public abstract void reset();
	public abstract BaseQuery clone();
	public abstract String toString();
	public abstract boolean equals(Object query);
}
