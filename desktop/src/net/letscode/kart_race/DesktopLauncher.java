package net.letscode.kart_race;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {

	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

		config.setWindowedMode(180*2, 280*2);

		config.setForegroundFPS(60);
		config.setTitle("Kart-Race");
		new Lwjgl3Application(new net.letscode.kart_race.KartRace(), config);
	}
}
