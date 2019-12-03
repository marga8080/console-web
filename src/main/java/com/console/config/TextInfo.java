package com.console.config;

import java.io.Serializable;
import java.util.List;

public class TextInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<String> data; // 返回的信息
	
	private long length = 0; // 文本总长度
	
	private long position = 0;  // 开始读取位置
	
	private int lineSize = 100;  // 默认读取100行
	
	private String filePath; // 文件路径
	
	private String charset = "UTF-8";
	
	private boolean head;
	
	private boolean last;
	
	public List<String> getData() {
		return data;
	}

	public void setData(List<String> data) {
		this.data = data;
	}

	public long getPosition() {
		return position;
	}

	public void setPosition(long position) {
		this.position = position;
	}

	public int getLineSize() {
		return lineSize;
	}

	public void setLineSize(int lineSize) {
		this.lineSize = lineSize;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public boolean isHead() {
		return head;
	}

	public void setHead(boolean head) {
		this.head = head;
	}

	public boolean isLast() {
		return last;
	}

	public void setLast(boolean last) {
		this.last = last;
	}

}
