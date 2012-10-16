package com.fourpool.spontaneouscombustion;

import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

public class ShootingBlob implements RigidBody {
	private static final Random mRandom = new Random();

	private final Canvas mCanvas;
	private final float mSpeed = 15;
	private int mDirectionAngleDegrees;
	private boolean mShouldDelete;

	private PointF mPoint;
	private Paint mPaint = new Paint();
	private float mRadius = 5;

	private final float mMinX;
	private final float mMinY;
	private final float mMaxX;
	private final float mMaxY;

	public ShootingBlob(Canvas canvas, PointF point, int directionAngleDegrees) {
		mCanvas = canvas;
		mPoint = new PointF(point.x, point.y);
		mDirectionAngleDegrees = directionAngleDegrees;

		mMinX = 0 - (2 * mRadius);
		mMinY = 0 - (2 * mRadius);
		mMaxX = mCanvas.getWidth();
		mMaxY = mCanvas.getHeight();

		mPaint.setColor(Color.GREEN);
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

		mRadius += 0.02f;
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
	public float getRadius() {
		return mRadius;
	}

	@Override
	public boolean intersects(RigidBody rigidBody) {
		float r1 = getRadius();
		float r2 = rigidBody.getRadius();

		float x1 = getPoint().x;
		float x2 = rigidBody.getPoint().x;

		float y1 = getPoint().y;
		float y2 = rigidBody.getPoint().y;

		double distance = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));

		return distance < (r1 + r2);
	}
}
