package com.demod.fbsr.app;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.Iterator;

import org.json.JSONObject;

import com.demod.factorio.Config;
import com.demod.fbsr.app.PluginFinder.Plugin;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager;
import com.google.common.util.concurrent.ServiceManager.Listener;
import com.google.common.util.concurrent.MoreExecutors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.spi.IIORegistry;

public class StartAllServices {

	private static final Logger LOGGER = LoggerFactory.getLogger(StartAllServices.class);

	private static void addServiceIfEnabled(List<Service> services, String configKey,
			Supplier<? extends Service> factory) {
		JSONObject configJson = Config.get();
		if (configJson.has(configKey) && configJson.getJSONObject(configKey).optBoolean("enabled", true)) {
			services.add(factory.get());
		}
	}

	public static void main(String[] args) {
		// Initialize ImageIO plugins - this will discover all available plugins
		try {
			LOGGER.info("Checking TwelveMonkeys ImageIO plugins availability...");
			
			// Try to load some TwelveMonkeys plugin classes to verify they're working
			try {
				Class<?> jpegReaderClass = Class.forName("com.twelvemonkeys.imageio.plugins.jpeg.JPEGImageReader");
				LOGGER.info("Successfully loaded TwelveMonkeys JPEG plugin: {}", jpegReaderClass.getName());
			} catch (ClassNotFoundException e) {
				LOGGER.warn("TwelveMonkeys JPEG plugin not found: {}", e.getMessage());
			}
			
			// Force plugin registration
			LOGGER.info("Scanning for ImageIO plugins...");
			ImageIO.scanForPlugins();
			LOGGER.info("ImageIO plugin scan completed");

			// Debug: List all available image writers
			String[] writerFormats = ImageIO.getWriterFormatNames();
			LOGGER.info("Available ImageIO writer formats: {}", String.join(", ", writerFormats));

			// Verify PNG support (which should always be available)
			Iterator<ImageWriter> pngWriters = ImageIO.getImageWritersByFormatName("png");
			if (pngWriters.hasNext()) {
				ImageWriter pngWriter = pngWriters.next();
				LOGGER.info("PNG writer confirmed: {}", pngWriter.getClass().getName());
				pngWriter.dispose();
			} else {
				LOGGER.error("PNG writer not found - this is unexpected!");
			}
			
			LOGGER.info("ImageIO setup complete - using PNG for atlas generation");

		} catch (Exception e) {
			LOGGER.warn("Failed to scan for ImageIO plugins: {}", e.getMessage(), e);
		}

		List<Service> services = new ArrayList<>();
//		addServiceIfEnabled(services, "discord", BlueprintBotDiscordService::new);
//		addServiceIfEnabled(services, "reddit", BlueprintBotRedditService::new);
		addServiceIfEnabled(services, "webapi", WebAPIService::new);
//		addServiceIfEnabled(services, "watchdog", WatchdogService::new);
		addServiceIfEnabled(services, "logging", LoggingService::new);

		ServiceManager manager = new ServiceManager(services);
		manager.addListener(new Listener() {
			@Override
			public void failure(Service service) {
				LOGGER.info("SERVICE FAILURE: {}", service.getClass().getSimpleName());
				service.failureCause().printStackTrace();
			}

			@Override
			public void healthy() {
				LOGGER.info("ALL SERVICES ARE HEALTHY!");
			}

			@Override
			public void stopped() {
				LOGGER.info("ALL SERVICES HAVE STOPPED!");
			}
		}, MoreExecutors.directExecutor());

		manager.startAsync().awaitHealthy();
		Runtime.getRuntime().addShutdownHook(new Thread(() -> manager.stopAsync().awaitStopped()));

		PluginFinder.loadPlugins().forEach(Plugin::run);
	}

}
