package io.github.flpmartins88.springthread;

import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
public class Application implements ApplicationRunner {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	private final TaskExecutor applicationTaskExecutor;

	public Application(TaskExecutor applicationTaskExecutor) {
		this.applicationTaskExecutor = applicationTaskExecutor;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		IntStream.range(0, 200)
				.forEach(action -> applicationTaskExecutor.execute(new VeryLongTask()));

		//applicationTaskExecutor.execute(() -> System.exit(0));
	}
}

class VeryLongTask implements Runnable {

	private Logger log = LoggerFactory.getLogger(VeryLongTask.class);

	@Override
	public void run() {
		try {
			log.info("Working");
			Thread.sleep(2 * 1000);
			log.info("Done");
		} catch (InterruptedException e) {
			log.error("Error on thread " + Thread.currentThread().getName(), e);
		}
	}

}

@Configuration
class TaskConfig {

	@Bean
	public TaskExecutor applicationTaskExecutor() {
		return new ThreadPoolTaskExecutor() {{
			setThreadGroupName("simple-group");
			setThreadNamePrefix("simple-");
			setCorePoolSize(10);
			setMaxPoolSize(10);
			setQueueCapacity(200);
			initialize();
		}};
//		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
//		taskExecutor.setThreadGroupName("simple-group");
//		taskExecutor.setThreadNamePrefix("simple");
//		taskExecutor.setCorePoolSize(10);
//		return taskExecutor;
	}

}
