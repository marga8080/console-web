package com.console.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.console.websocket.WebSocketServer;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
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
	

	@PostMapping("exec")
	@ResponseBody
	public Object exec(@RequestBody JSONObject params) {
		try {
			String command = params.getStr("command");
			String cid = params.getStr("cid");
			logger.info("command:{},  cid:{}", command, cid);
			String osName = System.getProperties().getProperty("os.name");
			String[] cmd = null;
			String charsetName = "UTF-8";
			if (osName.toLowerCase().startsWith("windows")) {
				cmd = new String[] { "cmd", "/c", command};
				charsetName = "GBK";
			} else {
				cmd = new String[] { "/bin/sh", "-c", command};
			}
			Process ps = Runtime.getRuntime().exec(cmd);
			BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream(), charsetName));
			String line = null;
			while ((line = br.readLine()) != null) {
				//System.out.println(line);
				WebSocketServer.sendInfo(line, cid); 
			}
			ps.waitFor();
			//int exitVal = ps.waitFor();  
            //System.out.println("Exited with error code " + exitVal);  
			return "执行成功";
		} catch (Exception e) {
			logger.error(e);
			return "执行失败：" + e.getMessage();
		}
	}
	
}
