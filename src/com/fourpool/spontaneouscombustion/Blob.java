package com.fourpool.spontaneouscombustion;

import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

public class Blob implements RigidBody {
	private static final Random mRandom = new Random();

	private final Canvas mCanvas;
	private final float mSpeed;
	private final float mDirectionAngleDegrees;

	private PointF mPoint;
	private Paint mPaint;
	private int mRadius;
	private boolean mShouldDelete;

	private final int mMinX;
	private final int mMinY;
	private final int mMaxX;
	private final int mMaxY;

	public Blob(Canvas canvas, float speed) {
		mCanvas = canvas;
		mSpeed = speed;

		mMinX = 0 - (2 * mRadius);
		mMinY = 0 - (2 * mRadius);
		mMaxX = mCanvas.getWidth();
		mMaxY = mCanvas.getHeight();

		mPoint = getRandomStartingPoint();
		mPaint = new Paint();
		mPaint.setColor(Color.RED);
		mRadius = 50;
		mDirectionAngleDegrees = getRandomDirectionAngleDegrees();
	}

	@Override
	public void update() {
		if ((mPoint.x > mMaxX) || (mPoint.x <= mMinX) || (mPoint.y > mMaxY) || (mPoint.y <= mMinY)) {
			mShouldDelete = true;
		}

		double offsetX = Math.cos(Math.toRadians(mDirectionAngleDegrees)) * mSpeed;
		double offsetY = Math.sin(Math.toRadians(mDirectionAngleDegrees)) * mSpeed;

		mPoint.x += offsetX;
		mPoint.y += offsetY;
	}

	@Override
	public void setShouldDelete(boolean b) {
		mShouldDelete = b;
	}

	@Override
	public boolean shouldDelete() {
		return mShouldDelete;
	}

	@Override
	public void draw() {
		mCanvas.drawCircle(mPoint.x, mPoint.y, mRadius, mPaint);
	}

	@Override
	public PointF getPoint() {
		return mPoint;
	}

	@Override
	public int getRadius() {
		return mRadius;
	}

	@Override
	public boolean intersects(RigidBody rigidBody) {
		int r1 = getRadius();
		int r2 = rigidBody.getRadius();

		float x1 = getPoint().x;
		float x2 = rigidBody.getPoint().x;

		float y1 = getPoint().y;
		float y2 = rigidBody.getPoint().y;

		double distance = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));

		return distance < (r1 + r2);
	}

	private PointF getRandomStartingPoint() {
		float randomX = mRandom.nextFloat() * mMaxX;
		float randomY = mRandom.nextFloat() * mMaxY;

		return new PointF(randomX, randomY);
	}

	private float getRandomDirectionAngleDegrees() {
		return mRandom.nextInt(360);
	}

}
