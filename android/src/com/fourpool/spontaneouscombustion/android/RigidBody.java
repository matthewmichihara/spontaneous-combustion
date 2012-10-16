package com.fourpool.spontaneouscombustion.android;

import android.graphics.PointF;

public interface RigidBody {
	public void update();

	public void setShouldDelete(boolean b);

	public boolean shouldDelete();

	public void draw();

	public PointF getPoint();

	public float getRadius();

	public boolean intersects(RigidBody rigidBody);
}
