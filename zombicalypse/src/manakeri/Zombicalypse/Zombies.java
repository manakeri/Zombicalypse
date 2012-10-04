package manakeri.Zombicalypse;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Vector3;

/**
 * @author manakeri
 */
public class Zombies {
	class DIRECTION {
		static final short LEFT = 0;
		static final short BACK = 2;
		static final short RIGHT = 4;
		static final short FRONT = 6;

		static final short DEAD = 255;
	}

	class Zombie {
		private final Vector3 position;
		private final Vector3 direction;
		private float timePassed;
		private short state;
		private Decal frame;

		public Zombie(City _city) {
			position = new Vector3();
			direction = new Vector3();
			direction.set(TGame.rand.nextFloat(), 0, TGame.rand.nextFloat());
			do {
				position.set(TGame.rand.nextFloat(), 0, TGame.rand.nextFloat());
			} while (!_city.isRoad(position.x, position.z));
			state = 0;
		}
	}

	private final static short ROWS = 8;
	private final static short COLS = 8;

	private final static short SIZE = 128;

	private final Zombie[] zombies;

	private DecalBatch batch;
	private final int _amount;
	private final City _city;

	private final TextureRegion[] walk = new TextureRegion[ROWS * COLS];

	public Zombies(City city, int amount) {
		_amount = amount;
		_city = city;

		zombies = new Zombie[amount];
		for (int i = 0; i < zombies.length; i++) {
			zombies[i] = new Zombie(city);
		}

		Texture tex = new Texture("data/img/zombie_walk.png");

		for (int y = 0; y < ROWS; y++) {
			for (int x = 0; x < COLS; x++) {
				walk[y * COLS + x] = new TextureRegion(tex, x * SIZE, y * SIZE,
						SIZE, SIZE);
			}
		}
	}

	public int amount() {
		int _amount = 0;
		for (Zombie zomb : zombies) {
			if (zomb.state != DIRECTION.DEAD) {
				_amount++;
			}
		}
		return _amount;
	}

	public void checkHit(City city, int x, int z) {
		for (Zombie zomb : zombies) {
			if (zomb.state != DIRECTION.DEAD) {
				int zx = (int) (zomb.position.x * city.getWidth());
				int zz = (int) (zomb.position.z * city.getHeight());
				if (zx == x && zz == z) {
					zomb.state = DIRECTION.DEAD;
					if (TGame.DEBUG) {
						Gdx.app.log(TGame.TAG, "zx: " + zx + ", zz: " + zz
								+ ", hx: " + x + ", hz: " + z);
					}
				}
			}
		}
	}

	public void draw(Camera cam, float delta) {
		for (Zombie zomb : zombies) {
			if (zomb.state != DIRECTION.DEAD) {
				updatePos(zomb, delta);

				int dir = 0;

				float zdiff = cam.direction.z - zomb.direction.z;
				float xdiff = cam.direction.x - zomb.direction.x;

				// Gdx.app.log(TGame.TAG, "direction x: " + xdiff +
				// ", direction z: " + zdiff);

				if (Math.abs(xdiff) > Math.abs(zdiff)) {
					if (xdiff > 0) {
						dir = DIRECTION.RIGHT;
					} else {
						dir = DIRECTION.LEFT;
					}
				} else {
					if (zdiff > 0) {
						dir = DIRECTION.FRONT;
					} else {
						dir = DIRECTION.BACK;
					}
				}
				/*
				 * if (zomb.direction.x < 0) { dir = DIRECTION.LEFT; } if
				 * (zomb.direction.x > 0) { dir = DIRECTION.RIGHT; }
				 */
				zomb.frame = Decal.newDecal(1, 1,
						walk[dir * COLS + zomb.state], true);
				zomb.frame.setPosition(zomb.position.x * _city.getWidth(),
						zomb.position.y, zomb.position.z * _city.getHeight());
				zomb.frame.lookAt(cam.position, cam.up.nor());
				batch.add(zomb.frame);

				zomb.timePassed += delta;
				if (zomb.timePassed > 0.3f * Math.max(
						Math.abs(zomb.direction.x), Math.abs(zomb.direction.z))) {
					zomb.timePassed = 0;
					if (zomb.state < 8) {
						zomb.state++;
					} else {
						zomb.state = 1;
					}
				}

			}
		}
		batch.flush();
	}

	public void init(Camera cam) {
		batch = new DecalBatch(_amount, new CameraGroupStrategy(cam));
	}

	private void updatePos(Zombie zomb, float delta) {
		zomb.position.add(new Vector3(zomb.direction).mul(delta * 0.01f));
		if (!_city.isRoad(zomb.position.x, zomb.position.z)) {
			zomb.direction.set(-zomb.direction.x, 0, -zomb.direction.z);
			// direction[i].set( TGame.rand.nextFloat(), 0,
			// TGame.rand.nextFloat());
		}
	}

}
