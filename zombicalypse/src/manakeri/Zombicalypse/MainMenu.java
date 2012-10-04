package manakeri.Zombicalypse;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MainMenu extends Menu {
	@Override
	public void init() {
		TextButton menuitem;

		table.row();
		menuitem = new TextButton("Start", TGame.textbuttonStyle);
		menuitem.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				TGame.previous_screen = TGame.mainmenu;
				TGame.game.setScreen(TGame.gameloop);
			}
		});
		table.add(menuitem).colspan(3);

		table.row();
		menuitem = new TextButton("Options", TGame.textbuttonStyle);
		menuitem.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				TGame.previous_screen = TGame.mainmenu;
				TGame.game.setScreen(TGame.options);
			}
		});
		table.add(menuitem).colspan(3);

		table.row();
		menuitem = new TextButton("Highscores", TGame.textbuttonStyle);
		menuitem.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				TGame.previous_screen = TGame.mainmenu;
				TGame.game.setScreen(TGame.highscores);
			}
		});
		table.add(menuitem).colspan(3);

		table.row();
		menuitem = new TextButton("Exit", TGame.textbuttonStyle);
		menuitem.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
			}
		});
		table.add(menuitem).colspan(3);
	}

}
