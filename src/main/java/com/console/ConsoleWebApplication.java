package com.console;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class ConsoleWebApplication {

	public static final Log LOG = LogFactory.get();
	
	public static void main(String[] args) {
		SpringApplication.run(ConsoleWebApplication.class, args);
		LOG.info("·······console-web······启动成功");
	}
}
