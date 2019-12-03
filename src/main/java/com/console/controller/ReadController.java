package com.console.controller;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.console.config.TextInfo;


@RestController
@RequestMapping("read")
public class ReadController {
	
//	final static String HEAD = "head"; 
//	final static String LAST = "last"; 
	
	

	/**
	 * start -> end
	 * @param textInfo
	 * @param head
	 * @return
	 */
	@PostMapping("next")
	public TextInfo readNext(@RequestBody TextInfo textInfo, boolean head) {
		textInfo.setHead(head);
		textInfo.setLast(false);
		List<String> result = new ArrayList<String>();
		File file = new File(textInfo.getFilePath());
		if (!file.exists() || file.isDirectory() || !file.canRead()) {
			return textInfo;
		}
		RandomAccessFile fileRead = null;
		try {
			fileRead = new RandomAccessFile(file, "r"); // 用读模式
			long length = fileRead.length();// 获得文件长度
			long pos = textInfo.getPosition();
			if (head) {
				pos = 0;
			}
			if (pos > length - 1 || pos < 0) {
				return textInfo;
			}
			int lineNum = 0;
			if (length == 0L) {// 文件内容为空
				return textInfo;
			} else {
				// 开始位置
				long len = length - 1;
				while (pos < len) {
					fileRead.seek(pos); // 开始读取
					if (pos == 0) {
						String line = new String(fileRead.readLine().getBytes("ISO-8859-1"), textInfo.getCharset());
						result.add(line);
						lineNum++;
					}
					pos++;
					if (fileRead.readByte() == '\n') {// 有换行符，则读取
						String line = new String(fileRead.readLine().getBytes("ISO-8859-1"), textInfo.getCharset());
						result.add(line);
						lineNum++;
					}
					if (lineNum >= textInfo.getLineSize()) {// 满足指定行数 退出。
						break;
					}
				}
			}
			textInfo.setLength(length);
			textInfo.setPosition(pos);
			textInfo.setData(result);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fileRead != null) {
				try {
					// 关闭资源
					fileRead.close();
				} catch (Exception e) {
					
				}
			}
		}
		return textInfo;
	}
	
	/**
	 * end -> start
	 * @param textInfo
	 * @param last
	 * @return
	 */
	@PostMapping("prev")
	public TextInfo readPrev(@RequestBody TextInfo textInfo, boolean last) {
		textInfo.setLast(last);
		textInfo.setHead(false);
		List<String> result = new ArrayList<String>();
		File file = new File(textInfo.getFilePath());
		if (!file.exists() || file.isDirectory() || !file.canRead()) {
			return textInfo;
		}
		RandomAccessFile fileRead = null;
		try {
			fileRead = new RandomAccessFile(file, "r"); // 用读模式
			long length = fileRead.length();// 获得文件长度
			long pos = textInfo.getPosition();
			if (last) {
				pos = length - 1;
			}
			if (pos > length - 1 || pos < 0) {
				return textInfo;
			}
			int lineNum = 0;
			if (length == 0L) {// 文件内容为空
				return textInfo;
			} else {
				// 开始位置
				while (pos > 0) {
					pos--;
					fileRead.seek(pos); // 开始读取
					if (fileRead.readByte() == '\n') {// 有换行符，则读取
						String line = new String(fileRead.readLine().getBytes("ISO-8859-1"), textInfo.getCharset());
						result.add(line);
						lineNum++;
					}
					if (lineNum >= textInfo.getLineSize()) {// 满足指定行数 退出。
						break;
					}
				}
				if (pos == 0) {
					fileRead.seek(0);
					String line = new String(fileRead.readLine().getBytes("ISO-8859-1"), textInfo.getCharset());
					result.add(line);
				}
			}
			textInfo.setLength(length);
			textInfo.setPosition(pos);
			Collections.reverse(result);
			textInfo.setData(result);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fileRead != null) {
				try {
					// 关闭资源
					fileRead.close();
				} catch (Exception e) {
					
				}
			}
		}
		return textInfo;
	}
	/**
	 * 读取文件最后N行
	 * 
	 * @param file
	 * @param numRead
	 * @return List<String>
	 */
	public static List<String> readLastNLine(File file, long numRead) {
		List<String> result = new ArrayList<String>();
		long count = 0;// 定义行数
		if (!file.exists() || file.isDirectory() || !file.canRead()) {
			return result;
		}
		RandomAccessFile fileRead = null;
		try {
			fileRead = new RandomAccessFile(file, "r"); // 用读模式
			long length = fileRead.length();// 获得文件长度
			if (length == 0L) {// 文件内容为空
				return result;
			} else {
				// 开始位置
				long pos = length - 1;
				while (pos > 0) {
					pos--;
					fileRead.seek(pos); // 开始读取
					if (fileRead.readByte() == '\n') {// 有换行符，则读取
						String line = new String(fileRead.readLine().getBytes("ISO-8859-1"),"UTF-8");
						result.add(line);
						count++;
						if (count == numRead) {// 满足指定行数 退出。
							break;
						}
					}
				}

				if (pos == 0) {
					fileRead.seek(0);
					String line = new String(fileRead.readLine().getBytes("ISO-8859-1"),"UTF-8");
					result.add(line);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fileRead != null) {
				try {
					// 关闭资源
					fileRead.close();
				} catch (Exception e) {
					
				}
			}
		}
		Collections.reverse(result);
		return result;
	}

}
