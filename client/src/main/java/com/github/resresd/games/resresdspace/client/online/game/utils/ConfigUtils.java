package com.github.resresd.games.resresdspace.client.online.game.utils;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ServiceConfigurationError;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.resresd.games.resresdspace.client.online.game.configs.ClientConfig;
import com.github.resresd.games.resresdspace.client.online.game.header.GameHeader;

public class ConfigUtils {

	public static void initConfig() throws IOException, NoSuchAlgorithmException {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		if (!GameHeader.getConfigFile().exists()) {
			if (!GameHeader.getConfigFile().getParentFile().exists()) {
				GameHeader.getConfigFile().getParentFile().mkdirs();
			}
			if (GameHeader.getConfigFile().createNewFile()) {

			}
			GameHeader.clientConfig.getPlayer().genNew();
			mapper.writeValue(GameHeader.getConfigFile(), GameHeader.clientConfig);
			throw new ServiceConfigurationError("please edit " + GameHeader.getConfigFile().getAbsolutePath());
		}
		GameHeader.setClientConfig(mapper.readValue(GameHeader.getConfigFile(), ClientConfig.class));
		if (!GameHeader.getClientConfig().isPrepare()) {
			throw new ServiceConfigurationError("please edit " + GameHeader.getConfigFile().getAbsolutePath());
		}

	}

}
