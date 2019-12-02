package com.console.controller;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.console.config.TextInfo;
import com.google.common.base.Joiner;


@RestController
@RequestMapping("readLog")
public class ReadController {
	
	int READNUM = 100; //一次读取100行

	public static void main(String[] args) throws IOException {
		//String pathname = "C:\\Users\\Administrator\\Desktop\\data_1080326282719399898.json";
		//String pathname = "C:\\Users\\Administrator\\Desktop\\机构用户索引.txt";
		String pathname = "C:\\Users\\Administrator\\Desktop\\28\\provider.out";
		TextInfo textInfo = new TextInfo();
		textInfo.setFilePath(pathname);
		textInfo.setLineSize(100);
		textInfo.setCharset("GBK");
		textInfo.setPosition(0);
		textInfo = goRead(textInfo);
		String x = Joiner.on("\n").join(textInfo.getData());
		System.out.println(x);
		System.out.println("===========================");
		textInfo = backRead(textInfo, true);
		x = Joiner.on("\n").join(textInfo.getData());
		System.out.println(x);
	}
	
	@GetMapping("read")
	public Object read(String filepath) {
		
		return null;
	}

	/**
	 * start -> end
	 * @param textInfo
	 * @return
	 */
	public static TextInfo goRead(TextInfo textInfo) {
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
	 * @param goLast
	 * @return
	 */
	public static TextInfo backRead(TextInfo textInfo, boolean goLast) {
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
			if (goLast) {
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
