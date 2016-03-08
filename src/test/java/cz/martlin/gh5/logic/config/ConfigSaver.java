package cz.martlin.gh5.logic.config;

import java.io.File;

import cz.martlin.hg5.HomeGuardApp;
import cz.martlin.hg5.logic.config.ConfigLoader;
import cz.martlin.hg5.logic.config.Configuration;

public class ConfigSaver {

	public static void main(String[] args) {
		final Configuration config = new Configuration();
		final File file = HomeGuardApp.getConfigFile();	//TODO ...

		System.out.println("Config");
		System.out.println(config);
		System.out.println("saving into");
		System.out.println(file);

		ConfigLoader loader = new ConfigLoader(file);
		loader.save(config);
		
		System.out.println("Config saved.");
	}

}
