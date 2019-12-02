package com.console.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.console.util.CmdUtil;
import com.console.websocket.WebSocketServer;

import cn.hutool.json.JSONObject;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

@RestController
@RequestMapping("cmd")
public class CmdController {
	
	Log logger = LogFactory.get(this.getClass());

	@PostMapping("exec")
	public Object exec(@RequestBody JSONObject params) {
		String cid = params.getStr("cid");
		try {
			String command = params.getStr("command");
			if (StringUtils.isBlank(command)) {
				return "命令不能为空";
			}
			command = command.trim();
			// 输出当前命令
			WebSocketServer.sendInfo(CmdUtil.getPwd() + " > " + command, cid); 
			logger.info("command:{},  cid:{}", command, cid);
			List<String> cmdList = CmdUtil.getStartCmdList();
			Process ps = null;
			if (command.toLowerCase().startsWith("cd") && command.length() > 2) {
				command = command.substring(2).trim();
				if (!command.startsWith("/")) {
					command = CmdUtil.getPwd() + "/" + command;
				}
				File dir = new File(command);
				ps = Runtime.getRuntime().exec(cmdList.toArray(new String[cmdList.size()]), null, dir);
			} else {
				cmdList.add(command);
				ps = Runtime.getRuntime().exec(cmdList.toArray(new String[cmdList.size()]));
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream(), CmdUtil.isWindows() ? "GBK" : "UTF-8"));
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
}
