package com.datasupply;

import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FileIntoDatabase {

    public void readExcel(String path) {

        //读文件
        try {
            InputStream is = new FileInputStream(path);
            readExcel(is,"yyyyMMdd HH:mm:ss");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param in         excel文件输入流
     * @param datePatten 时间格式
     * @return List(Map)
     */
    public List<Map<Object, Object>> readExcel(InputStream in, String datePatten) {
        final ArrayList<Map<Object, Object>> list = new ArrayList();
        try (Workbook wb = WorkbookFactory.create(in)) {
            for (Sheet sheet : wb) {
                for (Row row : sheet) {
                    Map<Object, Object> map = new HashMap();
                    for (Cell cell : row) {
                        Object cellValue = getCellValue(cell, datePatten);
                        map.put(getCellValue(sheet.getRow(0).getCell(cell.getColumnIndex()), datePatten), cellValue);
                    }
                    list.add(map);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        list.remove(0);
        return list;
    }


    private Object getCellValue(Cell cell, String datePatten) {
        Object cellValue;
        SimpleDateFormat formatter = new SimpleDateFormat(datePatten);
        switch (cell.getCellType()) {
            case STRING:
                cellValue = cell.getStringCellValue().trim();
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    cellValue = formatter.format(cell.getDateCellValue());
                } else {
                    DecimalFormat df = new DecimalFormat("0");
                    cellValue = df.format(cell.getNumericCellValue()).trim();
                }
                break;
            case BOOLEAN:
                cellValue = cell.getBooleanCellValue();
                break;
            case FORMULA:
                cellValue = cell.getCellFormula();
                break;
            default:
                cellValue = null;
        }
        return cellValue;
    }

}
