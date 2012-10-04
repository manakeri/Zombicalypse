package manakeri.Zombicalypse;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class GameOver extends Menu {
	@Override
	public void init() {
		/*
		 * TextButtonStyle buttonStyle = new TextButtonStyle(); buttonStyle.font
		 * = TGame.font; buttonStyle.fontColor = Color.WHITE;
		 * buttonStyle.downFontColor = Color.RED;
		 * 
		 * LabelStyle labelStyle = new LabelStyle(new BitmapFont(),
		 * Color.WHITE);
		 */

		// table.row();
		table.add(new Label("GAME OVER", TGame.labelStyle));
		table.row();
		TextButton menuitem = new TextButton("Mainmenu", TGame.textbuttonStyle);
		menuitem.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				TGame.game.setScreen(TGame.gameloop);
			}
		});
		table.add(menuitem);

	}
}
