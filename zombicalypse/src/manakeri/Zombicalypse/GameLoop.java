package manakeri.Zombicalypse;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * @author manakeri
 */

public class GameLoop implements Screen, InputProcessor {
	private static int WIDTH = 21;
	private static int HEIGHT = 22;

	private static final int THRESHOLD = 250;
	private static final int TARGET_TIME = 5500;
	private static final int Z_START = 50;

	private PerspectiveCamera cam;
	private City city;
	private final GameLoopUI UI;
	private final SpriteBatch batch;
	private GL20 gl;

	private long counter_start = System.currentTimeMillis();
	private long target_start = System.currentTimeMillis();
	private long start_time;
	private long ct;
	private boolean targeting = false;

	private final static Plane xzPlane = new Plane(new Vector3(0, 1, 0), 0);
	private final Vector3 intersection = new Vector3();
	private final Vector3 curr = new Vector3();
	private final Vector3 last = new Vector3(-1, -1, -1);
	private final Vector3 delta = new Vector3();
	private final Vector2 target = new Vector2();

	private Boolean running = true;

	//FPSLogger fps = new FPSLogger();
	private Zombies zombies;
	private final float[] x = new float[360];
	private final float[] z = new float[360];
	private final float[] vertices = new float[360 * 3];
	private final Mesh points = new Mesh(false, 360 * 3, 0,
			new VertexAttribute(Usage.Position, 3, "a_position"));

	private float explosion_delta;
	private boolean exploding = false;
	private String s_start_time;
	private String s_target_start;

	public GameLoop() {
		UI = new GameLoopUI();
		batch = new SpriteBatch();
		for (int i = 0; i < 360; i++) {
			x[i] = (float) Math.sin(i * 0.01745f) * 2.5f;
			z[i] = (float) Math.cos(i * 0.01745f) * 2.5f;
		}
	}

	private void checkTargeting(float delta2) {
		if (targeting && ct - target_start >= TARGET_TIME) {
			explosion_delta = 0;
			exploding = true;
			if (TGame.prefs.getBoolean("effects")) {
				int expsnd = TGame.rand.nextInt(4);
				if (TGame.DEBUG) {
					Gdx.app.log(TGame.TAG, "expsnd #: " + expsnd);
				}
				TGame.snd_explosion[expsnd]
						.play(TGame.prefs.getFloat("volume"));
				Gdx.input.vibrate(250);
			}
			if (TGame.DEBUG) {
				Gdx.app.log(TGame.TAG, "shot at: " + target);
			}
			targeting = false;
		}
		if (exploding) {
			int vi = 0;
			explosion_delta += delta2;
			for (int i = 0; i < 360; i++) {
				vertices[i] = 0;
			}
			// Gdx.app.log(TGame.TAG, "total_delta: " + explosion_delta);
			if (explosion_delta < 0.75f) {
				for (int i = 0; i < 360; i += 2) {
					float ox = target.x + 0.5f + explosion_delta * x[i];
					float oz = target.y + 0.5f + explosion_delta * z[i];
					if (city.isRoad(ox / city.getWidth(), oz / city.getHeight())) {
						zombies.checkHit(city, (int) ox, (int) oz);
						vertices[vi++] = ox;
						vertices[vi++] = 0.1f;
						vertices[vi++] = oz;
					} else {
						vertices[vi++] = 0;
						vertices[vi++] = 0;
						vertices[vi++] = 0;
					}

				}
				points.setVertices(vertices);
			} else {
				exploding = false;
			}
		}
	};

	@Override
	public void dispose() {
		if (TGame.DEBUG) {
			Gdx.app.log(TGame.TAG, "gameloop.dispose()");
		}
		batch.dispose();
		UI.dispose();

	};

	private void drawUI(float delta2) {
		s_start_time = String.format("%1$TM:%1$TS", ct - start_time);
		s_target_start = "0";
		if (targeting) {
			s_target_start = Long
					.toString((TARGET_TIME - (ct - target_start)) / 1000);
		}
		UI.draw(gl, delta2, zombies.amount(), s_start_time, s_target_start);
	}

	@Override
	public void hide() {
		if (TGame.DEBUG) {
			Gdx.app.log(TGame.TAG, "gameloop.hide()");
		}
	}

	private void init_city() {
		city = new City(WIDTH, HEIGHT);
		zombies = new Zombies(city, Z_START);
		zombies.init(cam);
	}

	@Override
	public boolean keyDown(int keycode) {
		switch (keycode) {
		case Keys.P:
			running = !running;
			break;
		case Keys.BACK:
		case Keys.ESCAPE:
			TGame.game.setScreen(TGame.mainmenu);
			break;
		case Keys.F1:
			if (TGame.DEBUG) {
				Gdx.app.log(
						TGame.TAG,
						"fov: " + cam.fieldOfView + ", x: " + cam.position.x
								+ ", y: " + cam.position.y + ", z: "
								+ cam.position.z + ", fps: "
								+ Gdx.graphics.getFramesPerSecond());
			}
			break;
		case Keys.O:
		case Keys.MENU:
			/*
			 * if (TGame.DEBUG) { WIDTH++; HEIGHT++; init_city(); } else {
			 */
			TGame.previous_screen = TGame.gameloop;
			TGame.game.setScreen(TGame.options);
			// }
			break;
		}
		return true;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean mouseMoved(int x, int y) {
		return false;
	}

	@Override
	public void pause() {
		if (TGame.DEBUG) {
			Gdx.app.log(TGame.TAG, "gameloop.pause()");
		}
		TGame.prefs.flush();
		running = false;
	}

	@Override
	public void render(float delta) {
		// fps.log();

		if (running) {
			gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

			cam.update();
			city.draw(cam);
			zombies.draw(cam, delta);

			ct = System.currentTimeMillis();

			checkTargeting(delta);
			if (exploding) {
				TGame.colshader.begin();
				TGame.colshader.setUniformMatrix("u_worldView", cam.combined);
				Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
				points.render(TGame.colshader, GL20.GL_POINTS);
				Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
				TGame.colshader.end();
			}
			drawUI(delta);

			if (TGame.prefs.getBoolean("effects")
					&& ((ct - start_time) / 1000) % 25 == 0
					&& TGame.rand.nextInt(20) == 10 - 1) {
				TGame.snd_zombie[TGame.rand.nextInt(4)].play(TGame.prefs
						.getFloat("volume") * 0.25f);
			}
		} else {
			// TODO: DRAW "PAUSED"
		}
	}

	@Override
	public void resize(int width, int height) {
		if (TGame.DEBUG) {
			Gdx.app.log(TGame.TAG, "gameloop.resize()");
		}

		gl.glViewport(0, 0, width, height);

		float unitsOnX = (float) Math.sqrt(2) * width / (width * 0.15f);
		float pixelsOnX = width / unitsOnX;
		float unitsOnY = height / pixelsOnX;
		if (TGame.DEBUG) {
			Gdx.app.log(TGame.TAG, unitsOnX + ", " + unitsOnY);
		}

		cam = new PerspectiveCamera(45, unitsOnX, unitsOnY);
		cam.position.set(0, 15, 0);
		cam.near = 1;
		cam.far = 100;
		cam.lookAt(WIDTH / 2, 0, HEIGHT / 2);
		cam.update();

		UI.resize(width, height);

		// if (!_initialized) {
		init_city();
		// }
		start_time = System.currentTimeMillis();
	}

	@Override
	public void resume() {
		if (TGame.DEBUG) {
			Gdx.app.log(TGame.TAG, "gameloop.resume()");
		}
		running = true;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	@Override
	public void show() {
		if (TGame.DEBUG) {
			Gdx.app.log(TGame.TAG, "gameloop.show()");
		}
		gl = Gdx.graphics.getGL20();

		Gdx.input.setInputProcessor(this);

		gl.glClearDepthf(1.0f);
		gl.glDepthFunc(GL10.GL_LEQUAL);

		switch ((int) TGame.prefs.getFloat("quality")) {
		case 1:
			gl.glDisable(GL10.GL_DITHER);
			// gl.glHint(GL20.GL_POLYGON_SMOOTH_HINT, GL10.GL_FASTEST);
			// gl.glHint(GL20.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
			// gl.glShadeModel(GL10.GL_FLAT);
			break;
		case 5:
			// gl.glHint(GL20.GL_POLYGON_SMOOTH_HINT, GL10.GL_NICEST);
			// gl.glHint(GL20.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
			// gl.glShadeModel(GL10.GL_SMOOTH);
			// gl.glEnable(GL10.GL_LIGHTING);
			// gl.glEnable(GL10.GL_LIGHT0);
			// gl.glEnable(GL10.GL_COLOR_MATERIAL);
			break;
		}
	}

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		counter_start = System.currentTimeMillis();
		return false;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		Intersector.intersectRayPlane(cam.getPickRay(x, y), xzPlane, curr);
		if (!(last.x == -1 && last.y == -1 && last.z == -1)) {
			Intersector.intersectRayPlane(cam.getPickRay(last.x, last.y),
					xzPlane, delta);
			delta.sub(curr);
			cam.position.add(delta.x, 0, delta.z);
			cam.lookAt(WIDTH / 2, 0, HEIGHT / 2);
		}
		if (cam.position.x < -8.5f) {
			cam.position.x = -8.5f;
		}
		if (cam.position.z < -8.5f) {
			cam.position.z = -8.5f;
		}

		if (cam.position.x > 30) {
			cam.position.x = 30;
		}
		if (cam.position.z > 30) {
			cam.position.z = 30;
		}
		cam.update();
		last.set(x, y, 0);
		return true;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		last.set(-1, -1, -1);
		if (System.currentTimeMillis() - counter_start < THRESHOLD) {
			Intersector.intersectRayPlane(
					cam.getPickRay(Gdx.input.getX(), Gdx.input.getY()),
					xzPlane, intersection);
			int ix = (int) intersection.x;
			int iy = (int) intersection.y;
			int iz = (int) intersection.z;

			if (ix >= 0 && ix < WIDTH && iz >= 0 && iz < HEIGHT && iy == 0
					&& !exploding && !targeting && city.getTile(ix, iz) >= 128) {
				target_start = System.currentTimeMillis();
				targeting = true;
				target.x = ix;
				target.y = iz;
				if (TGame.DEBUG) {
					Gdx.app.log(TGame.TAG, "target set at x: " + ix + ", z: "
							+ iz);
				}
			}
		}
		counter_start = System.currentTimeMillis();
		return true;
	}

}
