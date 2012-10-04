/**
 * 
 */
package manakeri.Zombicalypse;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

/**
 * @author manakeri
 * 
 */
abstract public class Menu implements Screen {
	// protected TGame game;
	protected Stage ui;
	protected Table table;

	public Menu() {
		ui = new Stage(TGame.VIRTUAL_WIDTH, TGame.VIRTUAL_HEIGHT, false);
		table = new Table();
		table.setBackground(TGame.ninepatch);
		if (TGame.DEBUG) {
			table.debug();
			// table.defaults().padBottom(5).padTop(5);
		}

		table.add(new Image(TGame.atlas.findRegion("logo")))
				.align(Align.center).colspan(3);
		table.row();

		init();

		table.pack();
		table.setX(TGame.VIRTUAL_WIDTH / 2 - table.getWidth() / 2);
		table.setY(TGame.VIRTUAL_HEIGHT / 2 - table.getHeight() / 2);
		ui.addActor(table);

	}

	@Override
	public void dispose() {
		if (TGame.DEBUG) {
			Gdx.app.log(TGame.TAG, "menu.dispose()");
		}
		ui.dispose();
	}

	@Override
	public void hide() {
		if (TGame.DEBUG) {
			Gdx.app.log(TGame.TAG, "mainmenu.hide()");
		}
	}

	abstract public void init();

	@Override
	public void pause() {
		if (TGame.DEBUG) {
			Gdx.app.log(TGame.TAG, "mainmenu.pause()");
		}
	}

	@Override
	public void render(float delta) {
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl20.glViewport(0, 0, Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());

		if (TGame.DEBUG) {
			Table.drawDebug(ui);
		}
		ui.act(Math.min(Gdx.graphics.getDeltaTime(), 1.0f / 30.0f));
		ui.draw();
	}

	@Override
	public void resize(int width, int height) {
		if (TGame.DEBUG) {
			Gdx.app.log(TGame.TAG, "mainmenu.resize, w:" + width + ", h:"
					+ height);
		}
		ui.setViewport(TGame.VIRTUAL_WIDTH, TGame.VIRTUAL_HEIGHT, true);
		ui.getCamera().position.set(TGame.VIRTUAL_WIDTH / 2,
				TGame.VIRTUAL_HEIGHT / 2, 0);
	}

	@Override
	public void resume() {
		if (TGame.DEBUG) {
			Gdx.app.log(TGame.TAG, "mainmenu.resume()");
		}
	}

	@Override
	public void show() {
		// Gdx.gl10.glDisable(GL10.GL_DEPTH_TEST);
		// Gdx.gl10.glDisable(GL10.GL_CULL_FACE);

		Gdx.input.setInputProcessor(ui);
		if (TGame.prefs.getBoolean("music", true)) {
			TGame.music.play();
		} else {
			TGame.music.stop();
		}
	}
}
