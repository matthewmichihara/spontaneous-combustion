package com.fourpool.spontaneouscombustion;

import android.graphics.PointF;

public interface RigidBody {
	public void update();

	public void setShouldDelete(boolean b);

	public boolean shouldDelete();

	public void draw();

	public PointF getPoint();

	public int getRadius();

	public boolean intersects(RigidBody rigidBody);
}
