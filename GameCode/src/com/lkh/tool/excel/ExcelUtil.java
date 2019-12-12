package com.lkh.tool.excel;

import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.lkh.tool.excel.ExcelConfigBean.RowData;

public class ExcelUtil {
	
	public static ExcelConfigBean readSheet(Sheet sheet, String fileName,ExcelConfigBean config) throws Exception {
		String sheetName = sheet.getSheetName();
		int maxRow = sheet.getLastRowNum();
		Row firstRow = sheet.getRow(0);
		// 过滤非配置表
		if(sheetName.startsWith("#") || maxRow == 0 || firstRow == null) {
			return null;
		}
		int maxCel = firstRow.getLastCellNum();
		//
//		ExcelConfigBean config = new ExcelConfigBean(fileName);
		// 解析
		Row row = null;
		// 空白行，连续空白三行，不往后读，空行直接跳过读取
		int blankRowNum = 0;
		
		List<String> fieldNameList = new ArrayList<>();
		List<String> typeNameList = new ArrayList<>();
		List<String> desNameList = new ArrayList<>();
		
		for (int i = 0; i <= maxRow; i++) {
			row = sheet.getRow(i);
			RowData data =new RowData();
			if(row == null) {
//				System.err.println("配置数据出现空行" + sheet.getSheetName() + "row=" + (i+1));
				if(++blankRowNum > 2) break;
				else continue;
				//throw new  Exception("配置数据出现空行" + config.getSheetName() + "row=" + i);
			}
			if (i < 3) {
				try {
					for (int c = 0; c < maxCel; c++) {
						Cell cel = row.getCell(c);
						if(cel == null || "".equals(cel.getStringCellValue())){
							maxCel = c;
							break;
						}
						if(i == 0) {
							fieldNameList.add(cel.getStringCellValue());
						}else if(i == 1) {
							typeNameList.add(cel.getStringCellValue());
						}else if(i == 2) {
							desNameList.add(cel.getStringCellValue());
						}
					}
				} catch(Exception e) {
					e.printStackTrace();
					throw new  Exception("配置文件读取异常");
				}
			} else {//开始读取数据
				boolean add = true;
				for (int c = 0; c < maxCel; c++) {
					// 读到空行跳过
					if(row == null) {
						add = false;
						break;
					}
					// 判断第一列是否是空行，跑出异常
					Cell cel = row.getCell(c);
					// 行数，数据
					if(cel == null) {
						cel = row.createCell(c);
					}
					// 跳过第一列为空的
					if(c == 0 && cel.toString().trim().equals("")) {
						add = false;
						break;
					}
					data.addValue(fieldNameList.get(c), getMsg(cel, typeNameList.get(c)));
					
				}
				if(add)
					config.addRow(sheetName,data);
			}
		}
		return config;
	}
//	private static DateTimeFormatter defaultFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private static Object getMsg(Cell cell,String type) {
        if(cell.getCellTypeEnum()== CellType.STRING) {
        	return getDefaultByType(cell.getRichStringCellValue().getString(),type);
        }else if(cell.getCellTypeEnum()== CellType.NUMERIC) {
        	if (DateUtil.isCellDateFormatted(cell)) {
                return LocalDateTime.ofInstant(Instant.ofEpochMilli(cell.getDateCellValue().getTime()), ZoneId.systemDefault());
            } else {
            	return getDefaultByType(cell.toString(),type);
            }
        }else if(cell.getCellTypeEnum()== CellType.BOOLEAN) {
        	return cell.getBooleanCellValue();
        }else if(cell.getCellTypeEnum()== CellType.FORMULA) {
        	return cell.getCellFormula();
        }else if(cell.getCellTypeEnum()== CellType.BLANK) {
//        	System.err.println("配置中有数据为空");
        	return "";
        }
        return getDefaultByType(cell.toString(),type);
	}
	
	private  static Object getDefaultByType(String value, String type) {
		if("int".equals(type.toLowerCase())) {
			return Float.valueOf(value).intValue();
		}
		if("float".equals(type.toLowerCase())) {
			return Float.valueOf(value).floatValue();
		}
		if("long".equals(type.toLowerCase())) {
			return Float.valueOf(value).longValue();
		}
		return value;
	}
	
	public static ExcelConfigBean getExcelConfigBean(InputStream path,String fileName) {
		
		try(Workbook wb = new XSSFWorkbook(path)){
			ExcelConfigBean bean = new ExcelConfigBean(fileName);
			Iterator<Sheet> it =  wb.sheetIterator();
			while(it.hasNext()) {
				Sheet sheet = it.next();
				ExcelUtil.readSheet(sheet, fileName,bean);
			}
			return bean;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void main(String[] args) throws Exception {
//		Workbook wb = new XSSFWorkbook("E:\\work\\docs\\V1.0.0.0\\商城\\GameShop.xlsx");
//		Sheet sheet = wb.getSheetAt(0);
//		ExcelConfigBean bean = ExcelUtil.readSheet(sheet, "HallAdvertisement");
//		System.err.println(bean);
	}
	
	
}
