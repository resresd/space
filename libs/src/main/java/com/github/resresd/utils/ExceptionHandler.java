package com.github.resresd.utils;

import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.resresd.games.resresdspace.StaticData;

public class ExceptionHandler implements UncaughtExceptionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandler.class);
	private static final File logFile = new File("exceptions.log");

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		LOGGER.error("Exception %s  ", e);
		try {
			if (!logFile.exists()) {
				LOGGER.info("exceptions log file is created:{}", logFile.createNewFile());
			}

			Files.writeString(logFile.toPath(), e.toString() + '\n', StandardOpenOption.APPEND);
		} catch (IOException e1) {
			StaticData.EXCEPTION_HANDLER.uncaughtException(Thread.currentThread(), e1);
		}
	}

}
