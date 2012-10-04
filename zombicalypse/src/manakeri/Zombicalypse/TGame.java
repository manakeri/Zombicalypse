package manakeri.Zombicalypse;

import java.util.Random;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.loaders.g3d.G3dLoader;
import com.badlogic.gdx.graphics.g3d.model.still.StillModel;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class TGame extends Game {
	public static final String TAG = "zombicalypse";
	public static final float VIRTUAL_WIDTH = 800f;
	public static final float VIRTUAL_HEIGHT = 480f;
	public static final boolean DEBUG = false;
	public static TGame game;

	public static StillModel house[];

	public static Music music;
	public static MainMenu mainmenu;
	public static GameLoop gameloop;
	public static GameOver gameover;
	public static Options options;
	public static HighScores highscores;
	public static Random rand = new Random();

	public static LabelStyle labelStyle;
	public static SliderStyle sliderStyle;
	public static TextButtonStyle textbuttonStyle;
	public static CheckBoxStyle checkboxStyle;

	public static BitmapFont font;
	public static NinePatchDrawable ninepatch;

	public static TextureAtlas atlas;
	public static Preferences prefs;
	public static Sound[] snd_explosion;
	public static Sound[] snd_zombie;

	private final String tex_vertexShader = "attribute vec4 a_position;"
			+ "attribute vec4 a_color;" + "attribute vec2 a_texCoord0;"
			+ "uniform mat4 u_worldView;" + "varying vec2 v_texCoords;"
			+ "void main() {" + "v_texCoords = a_texCoord0;"
			+ "gl_Position = u_worldView * a_position; }";
	private final String tex_fragmentShader = "varying vec2 v_texCoords;uniform sampler2D u_texture;"
			+ "void main() { gl_FragColor = texture2D(u_texture, v_texCoords); }";

	private final String col_vertexShader = "attribute vec4 a_position;"
			+ "uniform mat4 u_worldView;"
			+ "void main() { gl_Position = u_worldView * a_position; }";

	private final String col_fragmentShader = "void main() { gl_FragColor = vec4(1,0,0,1); }";

	public static ShaderProgram texshader;
	public static ShaderProgram colshader;
	public static Screen previous_screen;

	@Override
	public void create() {
		if (DEBUG) {
			Gdx.app.log(TAG, "game.create()");
		}

		game = this;
		prefs = Gdx.app.getPreferences("zombicalypse.ini");
		prefs.getBoolean("effects", true);
		prefs.getBoolean("music", true);

		music = Gdx.audio.newMusic(Gdx.files.internal("data/snd/music-01.ogg"));
		music.setLooping(true);

		snd_explosion = new Sound[4];
		snd_explosion[0] = Gdx.audio.newSound(Gdx.files
				.internal("data/snd/explosion-01.ogg"));
		snd_explosion[1] = Gdx.audio.newSound(Gdx.files
				.internal("data/snd/explosion-02.ogg"));
		snd_explosion[2] = Gdx.audio.newSound(Gdx.files
				.internal("data/snd/explosion-03.ogg"));
		snd_explosion[3] = Gdx.audio.newSound(Gdx.files
				.internal("data/snd/explosion-04.ogg"));

		snd_zombie = new Sound[4];
		snd_zombie[0] = Gdx.audio.newSound(Gdx.files
				.internal("data/snd/zombie-01.ogg"));
		snd_zombie[1] = Gdx.audio.newSound(Gdx.files
				.internal("data/snd/zombie-02.ogg"));
		snd_zombie[2] = Gdx.audio.newSound(Gdx.files
				.internal("data/snd/zombie-03.ogg"));
		snd_zombie[3] = Gdx.audio.newSound(Gdx.files
				.internal("data/snd/zombie-04.ogg"));

		atlas = new TextureAtlas("data/img/pack");

		ninepatch = new NinePatchDrawable(new NinePatch(
				atlas.findRegion("menuskin"), 8, 8, 8, 8));

		font = new BitmapFont(new BitmapFontData(
				Gdx.files.internal("data/primo.fnt"), false),
				atlas.findRegion("primo"), false);

		house = new StillModel[3];
		house[0] = G3dLoader.loadStillModel(Gdx.files
				.internal("data/mdl/house1.g3d"));
		house[1] = G3dLoader.loadStillModel(Gdx.files
				.internal("data/mdl/house2.g3d"));
		house[2] = G3dLoader.loadStillModel(Gdx.files
				.internal("data/mdl/house3.g3d"));

		labelStyle = new LabelStyle(font, Color.WHITE);

		sliderStyle = new SliderStyle();
		sliderStyle.background = ninepatch;
		sliderStyle.knob = new TextureRegionDrawable(
				atlas.findRegion("checkboxon"));

		textbuttonStyle = new TextButtonStyle();
		textbuttonStyle.font = font;
		textbuttonStyle.fontColor = Color.WHITE;
		textbuttonStyle.downFontColor = Color.RED;

		checkboxStyle = new CheckBoxStyle();
		checkboxStyle.font = font;
		checkboxStyle.checkboxOn = new TextureRegionDrawable(
				atlas.findRegion("checkboxon"));
		checkboxStyle.checkboxOff = new TextureRegionDrawable(
				atlas.findRegion("checkboxoff"));
		checkboxStyle.checked = new TextureRegionDrawable(
				atlas.findRegion("checkboxon"));

		TGame.highscores = new HighScores();
		TGame.options = new Options();
		TGame.gameover = new GameOver();
		TGame.gameloop = new GameLoop();
		TGame.mainmenu = new MainMenu();

		Gdx.input.setCatchBackKey(true);
		Gdx.input.setCatchMenuKey(true);

		Gdx.app.log(TAG, "isGL20 available: " + Gdx.graphics.isGL20Available());
		// Gdx.input.setCursorCatched(true);
		if (Gdx.graphics.isGL20Available()) {
			texshader = new ShaderProgram(tex_vertexShader, tex_fragmentShader);
			colshader = new ShaderProgram(col_vertexShader, col_fragmentShader);
			if (DEBUG) {
				Gdx.app.log(TAG, texshader.getLog());
				Gdx.app.log(TAG, colshader.getLog());
			}
		}
		TGame.previous_screen = TGame.mainmenu;
		this.setScreen(TGame.mainmenu);
	}

	@Override
	public void dispose() {
		// super.dispose();
		prefs.flush();
		if (music != null) {
			music.dispose();
		}
		if (mainmenu != null) {
			mainmenu.dispose();
		}
		if (gameloop != null) {
			gameloop.dispose();
		}
		if (gameover != null) {
			gameover.dispose();
		}
		if (options != null) {
			options.dispose();
		}

		if (highscores != null) {
			highscores.dispose();
		}
		if (font != null) {
			font.dispose();
		}
		if (atlas != null) {
			atlas.dispose();
		}
		for (Sound element : snd_explosion) {
			element.dispose();
		}
		for (Sound element : snd_zombie) {
			element.dispose();
		}
		for (StillModel element : house) {
			element.dispose();
		}
	}

	@Override
	public void pause() {
		super.pause();
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void resize(int width, int height) {
		if (DEBUG) {
			Gdx.app.log(TAG, "width: " + width + ", height: " + height);
		}
		super.resize(width, height);
	}

	@Override
	public void resume() {
		super.resume();
	}
}
