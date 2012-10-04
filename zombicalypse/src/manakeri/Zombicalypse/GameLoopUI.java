package manakeri.Zombicalypse;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class GameLoopUI {
	private final Stage ui;
	private final Label fps;
	private final Label target_time;
	private final Label ltime;
	private final Label lzombies_left;
	private Label vertices;

	public GameLoopUI() {
		LabelStyle labelStyle = new LabelStyle(new BitmapFont(), Color.WHITE);

		ui = new Stage(TGame.VIRTUAL_WIDTH, TGame.VIRTUAL_HEIGHT, false);

		Table stats = new Table();
		stats.setBackground(TGame.ninepatch);
		stats.defaults().pad(5).left();

		Label label;

		stats.row();
		label = new Label("Time: ", labelStyle);
		stats.add(label);
		ltime = new Label("00:00", labelStyle);
		stats.add(ltime).expand(true, true).fill(true, true);

		stats.row();
		label = new Label("Zombies: ", labelStyle);
		stats.add(label);
		lzombies_left = new Label("000", labelStyle);
		stats.add(lzombies_left).expand(true, true).fill(true, true);

		stats.row();
		label = new Label("FPS: ", labelStyle);
		stats.add(label);
		fps = new Label("00", labelStyle);
		stats.add(fps);

		if (TGame.DEBUG) {
			stats.row();
			label = new Label("Vertices: ", labelStyle);
			stats.add(label);
			vertices = new Label("000000", labelStyle);
			stats.add(vertices);
		}

		stats.row();
		label = new Label("Target time: ", labelStyle);
		stats.add(label);
		target_time = new Label("0", labelStyle);
		stats.add(target_time);

		stats.pack();
		stats.setPosition(0, ui.getHeight() - stats.getHeight());

		Table weapons = new Table();
		weapons.setBackground(TGame.ninepatch);
		weapons.defaults().pad(5).right();

		ui.addActor(stats);
	}

	void dispose() {
		ui.dispose();
	}

	public void draw(GL20 gl, float delta, int zombies_left,
			String s_start_time, String s_target_start) {
		/*
		 * gl.glMatrixMode(GL10.GL_PROJECTION); gl.glLoadIdentity();
		 * gl.glMatrixMode(GL10.GL_MODELVIEW); gl.glLoadIdentity();
		 * gl.glDisable(GL10.GL_DEPTH_TEST); gl.glDisable(GL10.GL_CULL_FACE);
		 */
		ltime.setText(s_start_time);
		lzombies_left.setText(Integer.toString(zombies_left));
		fps.setText(Integer.toString(Gdx.graphics.getFramesPerSecond()));
		target_time.setText(s_target_start);

		if (TGame.DEBUG) {
			vertices.setText(Integer.toString(City.getnumVertices()));
		}

		ui.act(delta);
		ui.draw();
	}

	public void resize(int width, int height) {
		ui.setViewport(TGame.VIRTUAL_WIDTH, TGame.VIRTUAL_HEIGHT, false);
		ui.getCamera().position.set(TGame.VIRTUAL_WIDTH / 2,
				TGame.VIRTUAL_HEIGHT / 2, 0);
	}
}
