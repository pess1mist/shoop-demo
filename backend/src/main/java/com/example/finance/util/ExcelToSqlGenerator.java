package com.example.finance.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Excel 数据导入工具 - 生成 SQL 脚本
 */
public class ExcelToSqlGenerator {
    
    public static void main(String[] args) {
        String excelPath = "C:/Users/19012/Desktop/2/毕业设计数据_2023_2025.xlsx";
        String outputPath = "C:/Users/19012/Desktop/1/import_bx_data.sql";
        
        try {
            generateSqlFromExcel(excelPath, outputPath);
            System.out.println("SQL 脚本生成成功：" + outputPath);
        } catch (Exception e) {
            System.err.println("生成失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 从 Excel 生成 SQL 脚本
     */
    public static void generateSqlFromExcel(String excelPath, String outputPath) throws Exception {
        File file = new File(excelPath);
        if (!file.exists()) {
            throw new Exception("Excel 文件不存在：" + excelPath);
        }
        
        FileInputStream fis = new FileInputStream(file);
        Workbook workbook = new XSSFWorkbook(fis);
        
        PrintWriter writer = new PrintWriter(new FileWriter(outputPath));
        
        // 写入文件头
        writer.println("-- 数据库 BX 数据导入脚本");
        writer.println("-- 生成时间：" + new Timestamp(System.currentTimeMillis()));
        writer.println("");
        writer.println("CREATE DATABASE IF NOT EXISTS bx DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");
        writer.println("USE bx;");
        writer.println("");
        
        int sheetIndex = 0;
        for (Sheet sheet : workbook) {
            sheetIndex++;
            String sheetName = sheet.getSheetName();
            
            writer.println("-- ============================================");
            writer.println("-- 工作表：" + sheetName);
            writer.println("-- ============================================");
            writer.println("");
            
            // 读取表头
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                continue;
            }
            
            List<String> headers = new ArrayList<>();
            int lastColNum = headerRow.getLastCellNum();
            
            for (int i = 0; i < lastColNum; i++) {
                Cell cell = headerRow.getCell(i);
                String headerName = cell != null ? cell.toString().trim() : "column_" + i;
                headers.add(headerName);
            }
            
            // 创建表
            String tableName = "sheet_" + sheetIndex;
            writer.println("DROP TABLE IF EXISTS `" + tableName + "`;");
            writer.println("CREATE TABLE `" + tableName + "` (");
            writer.println("  `id` BIGINT NOT NULL AUTO_INCREMENT,");
            
            // 添加列定义
            for (int i = 0; i < headers.size(); i++) {
                String header = headers.get(i);
                String columnName = toValidColumnName(header);
                writer.print("  `" + columnName + "` VARCHAR(500) DEFAULT NULL COMMENT '" + escapeComment(header) + "'");
                if (i < headers.size() - 1) {
                    writer.println(",");
                } else {
                    writer.println();
                }
            }
            
            writer.println("  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,");
            writer.println("  PRIMARY KEY (`id`)");
            writer.println(") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='" + escapeComment(sheetName) + "';");
            writer.println("");
            
            // 插入数据
            int rowCount = sheet.getPhysicalNumberOfRows();
            int insertCount = 0;
            
            for (int i = 1; i < rowCount; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                
                // 检查是否为空行
                boolean hasData = false;
                for (int j = 0; j < lastColNum; j++) {
                    Cell cell = row.getCell(j);
                    if (cell != null && !cell.toString().trim().isEmpty()) {
                        hasData = true;
                        break;
                    }
                }
                
                if (!hasData) {
                    continue;
                }
                
                if (insertCount == 0) {
                    writer.print("INSERT INTO `" + tableName + "` (");
                    for (int j = 0; j < headers.size(); j++) {
                        writer.print("`" + toValidColumnName(headers.get(j)) + "`");
                        if (j < headers.size() - 1) {
                            writer.print(", ");
                        }
                    }
                    writer.println(") VALUES ");
                }
                
                writer.print("(");
                for (int j = 0; j < lastColNum; j++) {
                    Cell cell = row.getCell(j);
                    String value = "";
                    
                    if (cell != null) {
                        switch (cell.getCellType()) {
                            case STRING:
                                value = cell.getStringCellValue();
                                break;
                            case NUMERIC:
                                if (DateUtil.isCellDateFormatted(cell)) {
                                    Date date = cell.getDateCellValue();
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    value = sdf.format(date);
                                } else {
                                    double num = cell.getNumericCellValue();
                                    if (num == (long) num) {
                                        value = String.valueOf((long) num);
                                    } else {
                                        value = String.valueOf(num);
                                    }
                                }
                                break;
                            case BOOLEAN:
                                value = String.valueOf(cell.getBooleanCellValue());
                                break;
                            case FORMULA:
                                try {
                                    value = cell.getStringCellValue();
                                } catch (Exception e) {
                                    value = String.valueOf(cell.getNumericCellValue());
                                }
                                break;
                            default:
                                value = "";
                        }
                    }
                    
                    // 转义 SQL 字符串
                    value = value.replace("\\", "\\\\").replace("'", "\\'").replace("\n", "\\n").replace("\r", "\\r");
                    writer.print("'" + value + "'");
                    
                    if (j < lastColNum - 1) {
                        writer.print(", ");
                    }
                }
                writer.print(")");
                
                insertCount++;
                
                // 每 1000 条记录添加一个分号
                if (insertCount % 1000 == 0 || insertCount == rowCount - 1) {
                    writer.println(";");
                    writer.println("");
                    writer.println("-- 已插入 " + insertCount + " 条记录");
                    writer.println("");
                    if (insertCount < rowCount - 1) {
                        writer.print("INSERT INTO `" + tableName + "` (");
                        for (int j = 0; j < headers.size(); j++) {
                            writer.print("`" + toValidColumnName(headers.get(j)) + "`");
                            if (j < headers.size() - 1) {
                                writer.print(", ");
                            }
                        }
                        writer.println(") VALUES ");
                    }
                } else {
                    writer.println(",");
                }
            }
            
            writer.println("-- 工作表 " + sheetName + " 完成，共 " + insertCount + " 条记录");
            writer.println("");
        }
        
        writer.println("-- 数据导入完成");
        writer.println("-- 总工作表数：" + sheetIndex);
        
        writer.close();
        workbook.close();
        fis.close();
    }
    
    /**
     * 将列名转换为有效的 SQL 列名
     */
    private static String toValidColumnName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "column";
        }
        
        // 移除特殊字符，只保留字母、数字、下划线和中文
        StringBuilder sb = new StringBuilder();
        char[] chars = name.toCharArray();
        for (char c : chars) {
            if (Character.isLetterOrDigit(c) || c == '_' || Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) {
                sb.append(c);
            } else {
                sb.append('_');
            }
        }
        
        String result = sb.toString();
        
        // 如果以数字开头，添加前缀
        if (result.length() > 0 && Character.isDigit(result.charAt(0))) {
            result = "col_" + result;
        }
        
        return result.isEmpty() ? "column" : result;
    }
    
    /**
     * 转义注释中的特殊字符
     */
    private static String escapeComment(String comment) {
        if (comment == null) {
            return "";
        }
        return comment.replace("'", "''").replace("--", "").replace("/*", "").replace("*/", "");
    }
}
