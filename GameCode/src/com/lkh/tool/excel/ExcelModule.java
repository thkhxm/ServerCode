package com.lkh.tool.excel;

import com.lkh.tool.excel.annon.Config;

public class ExcelModule {
	private Class<?> cla;
	private Config config;
	public ExcelModule(Class<?> cla, Config config) {
		super();
		this.cla = cla;
		this.config = config;
	}
	public Class<?> getCla() {
		return cla;
	}
	public void setCla(Class<?> cla) {
		this.cla = cla;
	}
	public Config getConfig() {
		return config;
	}
	public void setConfig(Config config) {
		this.config = config;
	}
	
}
