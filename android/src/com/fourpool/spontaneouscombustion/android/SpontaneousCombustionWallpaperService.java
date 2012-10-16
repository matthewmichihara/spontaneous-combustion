package com.fourpool.spontaneouscombustion.android;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Pair;
import android.view.SurfaceHolder;

public class SpontaneousCombustionWallpaperService extends WallpaperService {

	private final Handler mHandler = new Handler();

	@Override
	public Engine onCreateEngine() {
		return new CombustionEngine();
	}

	class CombustionEngine extends Engine {

		private final List<RigidBody> mSprites = new ArrayList<RigidBody>();

		private final Runnable mDrawRunnable = new Runnable() {
			@Override
			public void run() {
				draw();
			}
		};

		@Override
		public void onVisibilityChanged(final boolean visible) {
			if (visible) {
				draw();
			} else {
				mHandler.removeCallbacks(mDrawRunnable);
			}
		}

		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {
			super.onCreate(surfaceHolder);

		}

		public void draw() {
			final SurfaceHolder holder = getSurfaceHolder();

			Canvas c = null;
			try {
				c = holder.lockCanvas();
				if (c != null) {
					if (mSprites.size() < 10) {
						mSprites.add(new Blob(c));
					}
					draw(c);
				}
			} finally {
				if (c != null) {
					holder.unlockCanvasAndPost(c);
				}
			}

			mHandler.removeCallbacks(mDrawRunnable);
			mHandler.postDelayed(mDrawRunnable, 5);
		}

		private void draw(final Canvas c) {
			c.drawColor(Color.BLACK);

			List<RigidBody> shootingSpritesToAdd = new ArrayList<RigidBody>();

			// Begin Sweep and Prune collision detection implementation.
			// See: http://jitter-physics.com/wordpress/?tag=sweep-and-prune

			// Step 1, Sort axis list.
			Collections.sort(mSprites, new Comparator<RigidBody>() {
				@Override
				public int compare(RigidBody r1, RigidBody r2) {
					float minX1 = r1.getPoint().x - r1.getRadius();
					float minX2 = r2.getPoint().x - r2.getRadius();

					return Float.compare(minX1, minX2);
				}
			});

			// Step 2, Iterate through axis list, finding possible collisions.
			List<RigidBody> activeList = new CopyOnWriteArrayList<RigidBody>();
			if (mSprites.size() > 0) {
				activeList.add(mSprites.get(0));
			}

			Set<Pair<RigidBody, RigidBody>> possibleCollisions = new HashSet<Pair<RigidBody, RigidBody>>();

			for (RigidBody sprite : mSprites) {

				for (RigidBody activeBody : activeList) {
					if (!(sprite instanceof Blob)) {
						// TODO: Understand why this if statement works.
						continue;
					} else if (sprite == activeBody) {
						// Ignore self.
						continue;
					} else if ((sprite.getPoint().x - sprite.getRadius()) > (activeBody.getPoint().x + activeBody.getRadius())) {
						activeList.remove(activeBody);
					} else {
						possibleCollisions.add(new Pair<RigidBody, RigidBody>(sprite, activeBody));
					}
				}
				activeList.add(sprite);
			}

			// Step 3, Iterate through possible collisions and detect actual
			// collisions.
			for (Pair<RigidBody, RigidBody> pair : possibleCollisions) {
				RigidBody r1 = pair.first;
				RigidBody r2 = pair.second;

				if (r1.intersects(r2)) {
					for (int k = 0; k < 360; k += 10) {
						shootingSpritesToAdd.add(new ShootingBlob(c, r1.getPoint(), k));
					}
					r1.setShouldDelete(true);
					r2.setShouldDelete(true);
				}
			}

			// End Sweep and Prune implementation.

			// Update and redraw sprites.
			Set<RigidBody> markedForDeletionSprites = new HashSet<RigidBody>();
			for (RigidBody sprite : mSprites) {
				sprite.update();
				sprite.draw();
				if (sprite.shouldDelete()) {
					markedForDeletionSprites.add(sprite);
				}
			}

			// Remove any sprites that have been marked for deletion.
			for (RigidBody sprite : markedForDeletionSprites) {
				mSprites.remove(sprite);
			}

			mSprites.addAll(shootingSpritesToAdd);
		}
	}
}
