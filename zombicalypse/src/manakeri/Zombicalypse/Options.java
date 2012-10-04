package manakeri.Zombicalypse;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class Options extends Menu {
	private CheckBox music_checkbox;
	private CheckBox effects_checkbox;
	private Slider quality_slider;
	private Slider volume_slider;

	@Override
	public void hide() {
		super.hide();
		TGame.prefs.putBoolean("effects", effects_checkbox.isChecked());
		TGame.prefs.putBoolean("musc", music_checkbox.isChecked());
		TGame.prefs.putFloat("volume", volume_slider.getValue());
		TGame.prefs.putFloat("quality", quality_slider.getValue());
		TGame.prefs.flush();
	}

	@Override
	public void init() {
		Label l_music = new Label("Music", TGame.labelStyle);
		table.add(l_music).colspan(1);
		music_checkbox = new CheckBox("", TGame.checkboxStyle);
		music_checkbox.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				TGame.prefs.putBoolean("music", music_checkbox.isChecked());
				TGame.prefs.flush();
			}
		});
		table.add(music_checkbox).colspan(1);

		table.row();
		Label l_fx = new Label("Effects", TGame.labelStyle);
		table.add(l_fx).colspan(1);
		effects_checkbox = new CheckBox("", TGame.checkboxStyle);
		effects_checkbox.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				TGame.prefs.putBoolean("effects", effects_checkbox.isChecked());
				TGame.prefs.flush();
			}
		});
		table.add(effects_checkbox).colspan(1);

		table.row();
		Label l_volume = new Label("Volume", TGame.labelStyle);
		table.add(l_volume).colspan(1);
		volume_slider = new Slider(0, 1, 0.1f, false, TGame.sliderStyle);
		table.add(volume_slider).colspan(1);

		table.row();
		Label l_quality = new Label("Quality", TGame.labelStyle);
		table.add(l_quality).colspan(1);
		quality_slider = new Slider(1, 5, 1, false, TGame.sliderStyle);
		table.add(quality_slider).colspan(1);

		table.row();
		table.row();
		TextButton menuitem = new TextButton("Back", TGame.textbuttonStyle);
		menuitem.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				TGame.game.setScreen(TGame.previous_screen);
			}
		});
		table.add(menuitem).colspan(3);
	}

	@Override
	public void show() {
		super.show();
		music_checkbox.setChecked(TGame.prefs.getBoolean("music", true));
		effects_checkbox.setChecked(TGame.prefs.getBoolean("effects", true));
		volume_slider.setValue(TGame.prefs.getFloat("volume", 1));
		quality_slider.setValue(TGame.prefs.getFloat("quality", 5));
	}
}
