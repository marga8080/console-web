package com.console.util;

import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.io.IoUtil;

public class CmdUtil {

	/**
	 * StartCmdList
	 * @return
	 */
	public static List<String> getStartCmdList() {
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
	public static boolean isWindows() {
		String osName = System.getProperties().getProperty("os.name");
		return osName.toLowerCase().startsWith("windows");
	}
	
	/**
	 * 获取当前路径
	 * @return
	 */
	public static String getPwd() {
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
			e.printStackTrace();
			return null;
		} 
	}
}
