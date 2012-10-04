package manakeri.Zombicalypse;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class HighScores extends Menu {
	private Preferences highscores;
	private String[] names;
	private long[] times;

	void addNew() {
		Gdx.input.getTextInput(new TextInputListener() {
			@Override
			public void canceled() {
			}

			@Override
			public void input(String text) {
			}
		}, "New highscore!", "Your name");
	}

	@Override
	public void dispose() {
		super.dispose();
		highscores.flush();
	}

	@Override
	public void hide() {
		super.hide();
		for (int i = 0; i < 10; i++) {
			highscores.putString("name" + i, names[i]);
			highscores.putLong("time" + i, times[i]);
		}
	}

	@Override
	public void init() {
		highscores = Gdx.app.getPreferences("highscores");
		names = new String[10];
		times = new long[10];
		for (int i = 0; i < 10; i++) {
			names[i] = highscores.getString("name" + i, "manakeri");
			times[i] = highscores.getLong("time" + i, i * 100000);
		}

		Label.LabelStyle highscore_style = new Label.LabelStyle(
				new BitmapFont(), Color.WHITE);
		for (int i = 0; i < 10; i++) {
			Label place = new Label(Integer.toString(i + 1) + ".",
					highscore_style);
			Label lname = new Label(names[i], highscore_style);
			Label ltimes = new Label(String.format("%1$TM:%1$TS", times[i]),
					highscore_style);
			table.add(place);
			table.add(lname);
			table.add(ltimes);
			table.row();
		}

		table.row();
		TextButton menuitem = new TextButton("Back", TGame.textbuttonStyle);
		menuitem.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				TGame.game.setScreen(TGame.mainmenu);
			}
		});
		table.add(menuitem).colspan(3);

		ui.addActor(table);
	}

}
