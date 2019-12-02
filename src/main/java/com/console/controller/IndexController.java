package com.console.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.hutool.core.util.IdUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

@Controller
public class IndexController {
	
	Log logger = LogFactory.get(this.getClass());
	
	@Autowired
	Environment environment;
	
	@Value("${server.ip}")
	String ip;
	
	@RequestMapping({"/", "index"})
	public String index(HttpServletRequest request, Model model) {
		String cid = IdUtil.fastSimpleUUID();
		String port = environment.getProperty("local.server.port");
		model.addAttribute("ip", ip);
		model.addAttribute("port", port);
		model.addAttribute("cid", cid);
		logger.info("ip:{}, port:{}, cid:{}", ip, port , cid);
		return "index";
	}
	
	
}
