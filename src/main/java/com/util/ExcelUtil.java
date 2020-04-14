package com.util;


import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * excel导入导出 使用apache poi
 */
public class ExcelUtil {

    /**
     * @param in         excel文件输入流
     * @param datePatten 时间格式
     * @return List(Map)
     */
    public static List<Map<String, Object>> readExcel(InputStream in, String datePatten) {
        final ArrayList<Map<String, Object>> list = new ArrayList();
        try (Workbook wb = WorkbookFactory.create(in)) {
            for (Sheet sheet : wb) {
                for (Row row : sheet) {
                    Map<String, Object> map = new HashMap();
                    for (Cell cell : row) {
                        Object cellValue = getCellValue(cell, datePatten);
                        map.put(getCellValue(sheet.getRow(0).getCell(cell.getColumnIndex()), datePatten).toString(), cellValue);
                    }
                    list.add(map);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }


    public static List<Map<String, Object>> readExcel(InputStream in, String datePatten, String sheetName) {
        final ArrayList<Map<String, Object>> list = new ArrayList();
        try (Workbook wb = WorkbookFactory.create(in)) {
            final Sheet sheet = wb.getSheet(sheetName);
            for (Row row : sheet) {
                Map<String, Object> map = new HashMap();
                for (Cell cell : row) {
                    Object cellValue = getCellValue(cell, datePatten);
                    map.put(getCellValue(sheet.getRow(0).getCell(cell.getColumnIndex()), datePatten).toString(), cellValue);
                }
                list.add(map);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }


    private static Object getCellValue(Cell cell, String datePatten) {
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

    /**
     * excel导出
     *
     * @param data    数据 List(Map)
     * @param key     关键字  Map中的key
     * @param headers 表头
     * @return Workbook 对象
     */
    public Workbook exportExcel(List<Map> data, List<String> key, List<String> headers) {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet();
        for (int i = 0; i < headers.size(); i++) {
            sheet.setColumnWidth(i, headers.get(i).getBytes().length * 256);
        }
        //标题样式
        CellStyle headerCellStyle = wb.createCellStyle();

        //边框
        headerCellStyle.setBorderBottom(BorderStyle.MEDIUM);
        headerCellStyle.setBorderLeft(BorderStyle.MEDIUM);
        headerCellStyle.setBorderTop(BorderStyle.MEDIUM);
        headerCellStyle.setBorderRight(BorderStyle.MEDIUM);

        //插入标题
        Row headerRow = sheet.createRow(0);
        Cell headerCell;
        for (int i = 0; i < headers.size(); i++) {
            headerCell = headerRow.createCell(i);
            headerCell.setCellValue(headers.get(i));
            headerCell.setCellStyle(headerCellStyle);
        }

        //插入数据
        Row row;
        Cell cell;
        for (int i = 0; i < data.size(); i++) {
            row = sheet.createRow(i + 1);
            for (int j = 0; j < key.size(); j++) {
                cell = row.createCell(j);
                if (data.get(i).containsKey(key.get(j))) {
                    cell.setCellValue(data.get(i).get(key.get(j)).toString());
                } else {
                    cell.setCellValue("");
                }
            }
        }
        return wb;
    }


    /**
     * 自定义类型excel导出
     *
     * @param data       List<?>
     * @param properties 属性名称数组
     * @param headers    表格标题
     * @param type       自定义类型 .class
     * @return WorkBook 对象
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public Workbook exportExcel(List<?> data, String[] properties, String[] headers, Class type) throws NoSuchFieldException, IllegalAccessException {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet();

        //标题样式
        CellStyle headerCellStyle = wb.createCellStyle();

        //边框
        headerCellStyle.setBorderBottom(BorderStyle.MEDIUM);
        headerCellStyle.setBorderLeft(BorderStyle.MEDIUM);
        headerCellStyle.setBorderTop(BorderStyle.MEDIUM);
        headerCellStyle.setBorderRight(BorderStyle.MEDIUM);

        //插入标题
        Row headerRow = sheet.createRow(0);
        Cell headerCell;
        for (int i = 0; i < headers.length; i++) {
            headerCell = headerRow.createCell(i);
            headerCell.setCellValue(headers[i]);
            headerCell.setCellStyle(headerCellStyle);
        }

        //插入数据
        Row row;
        Cell cell;
        for (int i = 0; i < data.size(); i++) {
            row = sheet.createRow(i + 1);
            //要导出的属性
            for (int j = 0; j < properties.length; j++) {
                final Field field = type.getDeclaredField(properties[j]);
                field.setAccessible(true);
                cell = row.createCell(j);
                cell.setCellValue(field.get(data.get(i)).toString());
            }
        }
        return wb;
    }

    /**
     * 数据导出
     *
     * @param data    数据源
     * @param key     关键字
     * @param headers 二级表头
     * @param title   一级表头
     * @return
     */
    public Workbook exportExcel(List<Map> data, List<String> key, List<String> headers, String title) {
        Workbook wb = new XSSFWorkbook();

        Sheet sheet = wb.createSheet();
        Row titleRow = sheet.createRow(0);

        // 合并大标题单元格
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, headers.size() - 1);
        sheet.addMergedRegion(region);


        //创建大标题
        ExcelUtil.createTitleCell(wb, titleRow, 0, title, region);


        //创建2级标题
        Row row = sheet.createRow(1);
        for (int i = 0; i < headers.size(); i++) {
            ExcelUtil.createCell(wb, row, i, headers.get(i));
        }

        for (int i = 0; i < data.size(); i++) {
            Row dataRow = sheet.createRow(i + 2);
            for (int j = 0; j < headers.size(); j++) {
                ExcelUtil.createCell(wb, dataRow, j, data.get(i).get(key.get(j)) == null ? "" : data.get(i).get(key.get(j)).toString());
            }
        }

        return wb;
    }

    /**
     * Creates a cell and aligns it a certain way.
     *
     * @param wb     the workbook
     * @param row    the row to create the cell in
     * @param column the column number to create the cell in
     */
    private static void createCell(Workbook wb, Row row, int column, String value) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        CellStyle cellStyle = wb.createCellStyle();

        //对齐
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        //字体
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        cellStyle.setFont(font);

        //边框
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);

        cellStyle.setWrapText(true);

        cell.setCellStyle(cellStyle);

        row.getSheet().setColumnWidth(column, (int) (value.getBytes().length * 1.2d * 256 > 12 * 256 ? value.getBytes().length * 1.2d * 256 : 12 * 256));

    }

    private static void createTitleCell(Workbook wb, Row row, int column, String value, CellRangeAddress region) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);

        CellStyle cellStyle = wb.createCellStyle();

        //字体
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        cellStyle.setFont(font);

        //对齐
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        //背景色
//        cellStyle.setFillBackgroundColor(IndexedColors.LIGHT_GREEN.getIndex());
//        cellStyle.setFillPattern(FillPatternType.THICK_BACKWARD_DIAG);

        cell.setCellStyle(cellStyle);

//        row.getSheet().setColumnWidth(column, value.getBytes().length * 256);
//        row.getSheet().setAutoFilter(region);
//        row.getSheet().autoSizeColumn(column, true);

        //设置合并边框的
        RegionUtil.setBorderBottom(BorderStyle.THIN, region, row.getSheet());
        RegionUtil.setBorderLeft(BorderStyle.THIN, region, row.getSheet());
        RegionUtil.setBorderRight(BorderStyle.THIN, region, row.getSheet());
        RegionUtil.setBorderTop(BorderStyle.THIN, region, row.getSheet());
    }


    public static Workbook exportExcelWithOutStyle(List<Map<String, Object>> data, List<String> key, List<String> headers) {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet();
        for (int i = 0; i < headers.size(); i++) {
            sheet.setColumnWidth(i, headers.get(i).getBytes().length * 256);
        }


        //插入标题
        Row headerRow = sheet.createRow(0);
        Cell headerCell;
        for (int i = 0; i < headers.size(); i++) {
            headerCell = headerRow.createCell(i);
            headerCell.setCellValue(headers.get(i));
        }

        //插入数据
        Row row;
        Cell cell;
        for (int i = 0; i < data.size(); i++) {
            row = sheet.createRow(i + 1);
            for (int j = 0; j < key.size(); j++) {
                cell = row.createCell(j);
                if (data.get(i).containsKey(key.get(j))) {
                    cell.setCellValue(data.get(i).get(key.get(j)) == null ? "" : data.get(i).get(key.get(j)).toString());
                } else {
                    cell.setCellValue("");
                }
            }
        }
        return wb;
    }


    public static SXSSFWorkbook bigDataExport(List<Map<String, Object>> data, List<String> keys, List<String> header) {
        //使用可滑动窗体来解决内存开销大问题（实际就是读一点数据就写一点不占内存）
        //设置窗体大小
        SXSSFWorkbook wb = new SXSSFWorkbook(100);

        Sheet sh = wb.createSheet();

        for (int i = 0; i < data.size(); i++) {
            Row row = sh.createRow(i);
            for (int j = 0; j < keys.size(); j++) {
                Cell cell = row.createCell(j);
                final Object cellValue = data.get(i).containsKey(keys.get(j)) ? data.get(i).get(keys.get(j)) : null;
                cell.setCellValue(cellValue == null ? null : cellValue.toString());
            }
        }

        for (int i = 0; i < 1000; i++) {
            sh.getRow(i);
        }

//        da

        return wb;
    }

    public static void main(String[] args) throws IOException {
        //使用可滑动窗体来解决内存开销大问题（实际就是读一点数据就写一点不占内存）
        //设置窗体大小
        SXSSFWorkbook wb = new SXSSFWorkbook(100000);
        Sheet sh = wb.createSheet();
        for (int rownum = 0; rownum < 300000; rownum++) {
            Row row = sh.createRow(rownum);
            for (int cellnum = 0; cellnum < 10; cellnum++) {
                Cell cell = row.createCell(cellnum);
                String address = new CellReference(cell).formatAsString();
                cell.setCellValue(address);
            }
        }
        FileOutputStream out = new FileOutputStream("sxssf.xlsx");
        wb.write(out);
        out.close();
        // dispose of temporary files backing this workbook on disk
        wb.dispose();
    }

}
