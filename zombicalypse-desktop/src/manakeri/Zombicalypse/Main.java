package manakeri.Zombicalypse;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Zombicalypse";
		cfg.resizable = false;
		cfg.useGL20 = true;
		cfg.width = (int) TGame.VIRTUAL_WIDTH;
		cfg.height = (int) TGame.VIRTUAL_HEIGHT;

		new LwjglApplication(new TGame(), cfg);
	}
}
