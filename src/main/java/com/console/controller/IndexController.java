package com.console.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
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

import cn.hutool.core.io.IoUtil;
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
		String cid = params.getStr("cid");
		try {
			String command = params.getStr("command");
			if (StringUtils.isBlank(command)) {
				return "命令不能为空";
			}
			command = command.trim();
			// 输出当前命令
			WebSocketServer.sendInfo(getPwd() + " > " + command, cid); 
			logger.info("command:{},  cid:{}", command, cid);
			List<String> cmdList = getStartCmdList();
			Process ps = null;
			if (command.toLowerCase().startsWith("cd") && command.length() > 2) {
				command = command.substring(2).trim();
				if (!command.startsWith("/")) {
					command = getPwd() + "/" + command;
				}
				File dir = new File(command);
				ps = Runtime.getRuntime().exec(cmdList.toArray(new String[cmdList.size()]), null, dir);
			} else {
				cmdList.add(command);
				ps = Runtime.getRuntime().exec(cmdList.toArray(new String[cmdList.size()]));
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream(), isWindows() ? "GBK" : "UTF-8"));
			String line = null;
			while ((line = br.readLine()) != null) {
				//System.out.println(line);
				WebSocketServer.sendInfo(line, cid); 
			}
			int exitVal = ps.waitFor();  
			if (exitVal != 0) {
				WebSocketServer.sendInfo("命令不正确", cid); 
				return "执行失败";
			}
            //System.out.println("Exited with error code " + exitVal);  
			return "执行成功";
		} catch (Exception e) {
			logger.error(e);
			WebSocketServer.sendInfo("执行失败：" + e.getMessage(), cid); 
			return "执行失败：" + e.getMessage();
		}
	}
	
	private List<String> getStartCmdList() {
		List<String> cmdList = new ArrayList<>();
		if (isWindows()) {
			cmdList.add("cmd");
			cmdList.add("/c");
		} else {
			cmdList.add("/bin/sh");
			cmdList.add("-c");
		}
		return cmdList;
	}
	
	/**
	 * 是否windows 系统
	 * @return
	 */
	private boolean isWindows() {
		String osName = System.getProperties().getProperty("os.name");
		return osName.toLowerCase().startsWith("windows");
	}
	
	public String getPwd() {
		List<String> cmdList = getStartCmdList();
		if (isWindows()) {
			cmdList.add("cd");
		} else {
			cmdList.add("pwd");
		}
		try {
			Process ps = Runtime.getRuntime().exec(cmdList.toArray(new String[cmdList.size()]));
			String pwd = IoUtil.read(ps.getInputStream(), isWindows() ? "GBK" : "UTF-8");
			ps.waitFor(); 
			return pwd.trim();
		} catch (Exception e) {
			logger.error(e);
			return null;
		} 
	}
}
