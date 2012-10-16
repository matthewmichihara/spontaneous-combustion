package com.fourpool.spontaneouscombustion;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
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

			// Collision detection
			for (int i = 0; i < mSprites.size(); i++) {
				RigidBody currentSprite = mSprites.get(i);

				for (int j = i + 1; j < mSprites.size(); j++) {
					RigidBody sprite = mSprites.get(j);

					if (currentSprite.intersects(sprite) && ((currentSprite instanceof Blob) || (sprite instanceof Blob))) {
						for (int k = 0; k < 360; k += 10) {
							shootingSpritesToAdd.add(new ShootingBlob(c, currentSprite.getPoint(), k));
						}
						currentSprite.setShouldDelete(true);
						sprite.setShouldDelete(true);
					}
				}
			}

			Set<RigidBody> markedForDeletionSprites = new HashSet<RigidBody>();
			for (RigidBody sprite : mSprites) {
				sprite.update();
				sprite.draw();
				if (sprite.shouldDelete()) {
					markedForDeletionSprites.add(sprite);
				}
			}

			for (RigidBody sprite : markedForDeletionSprites) {
				mSprites.remove(sprite);
			}

			mSprites.addAll(shootingSpritesToAdd);
		}
	}
}
