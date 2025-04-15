package ru.kata.spring.boot_security.demo;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

@SpringBootApplication
public class SpringBootSecurityDemoApplication {
	private static final Logger logger = LoggerFactory.getLogger(SpringBootSecurityDemoApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringBootSecurityDemoApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void openBrowserAfterStartup() {
		String url = "http://localhost:8080";
		try {
			if (Desktop.isDesktopSupported()) {
				Desktop desktop = Desktop.getDesktop();
				if (desktop.isSupported(Desktop.Action.BROWSE)) {
					logger.info("Opening browser at: {}", url);
					desktop.browse(URI.create(url));
					return;
				}
			}

			// Альтернативные методы для разных ОС
			String os = System.getProperty("os.name").toLowerCase();
			String[] command;

			if (os.contains("win")) {
				command = new String[]{"cmd", "/c", "start", url};
			} else if (os.contains("mac")) {
				command = new String[]{"open", url};
			} else {
				command = new String[]{"xdg-open", url};
			}

			logger.info("Trying to open browser using command: {}", Arrays.toString(command));
			Runtime.getRuntime().exec("cmd /c start chrome http://localhost:8080");

		} catch (IOException e) {
			logger.error("Failed to open browser for URL: {}", url, e);
		} catch (Exception e) {
			logger.error("Unexpected error while trying to open browser", e);
		}
	}
}