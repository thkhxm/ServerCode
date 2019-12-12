package com.lkh.tool.excel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author tim.huang
 * 2018年5月28日
 */
public class ExcelConfigBean {
	private String key;
	
	private Map<String,List<RowData>> sheetMap;
	
	public ExcelConfigBean (String key) {
		this.key = key;
		this.sheetMap = new HashMap<>();
	}
	
	public String getKey() {
		return key;
	}

	public List<RowData> getRowList(String sheetName) {
		return sheetMap.get(sheetName);
	}

	public ExcelConfigBean addRow(String sheet,RowData row) {
		sheetMap.computeIfAbsent(sheet, key->new ArrayList<>()).add(row);
		return this;
	}
	
	public static class RowData{
		private Map<String,Object> valueMap;
		
		public RowData () {
			valueMap = new HashMap<String, Object>(20);
		}
		
		@SuppressWarnings("unchecked")
		public <T> T getValue(String key){
			Object obj = valueMap.getOrDefault(key, null);
			if(!Optional.ofNullable(obj).isPresent()) {
//				System.err.println("配置文件中，有空数据");
				return null;
			}
			return (T) obj;
		}
		
		public void addValue(String key,Object obj) {
			this.valueMap.put(key, obj);
		}

		@Override
		public String toString() {
			return "RowData [valueMap=" + valueMap + "]\n";
		}
	}

	@Override
	public String toString() {
		return "ExcelConfigBean [key=" + key + ", sheetMap=" + sheetMap + "]";
	}
	
}
