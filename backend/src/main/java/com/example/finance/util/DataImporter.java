package com.example.finance.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * Excel 数据导入数据库工具类
 */
public class DataImporter {
    
    private final DataSource dataSource;
    private final String excelPath;
    
    public DataImporter(DataSource dataSource, String excelPath) {
        this.dataSource = dataSource;
        this.excelPath = excelPath;
    }
    
    /**
     * 执行数据导入
     */
    public void importData() {
        try (FileInputStream fis = new FileInputStream(excelPath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            System.out.println("========== 开始导入数据 ==========");
            
            // 按依赖顺序导入：先导入基础数据，再导入业务数据
            importSupplier(workbook);
            importProduct(workbook);
            importPurchaseOrder(workbook);
            importPurchasePriceHistory(workbook);
            importProductBom(workbook);
            importProductionOrder(workbook);
            importManufacturingCostDetail(workbook);
            importBudgetExecution(workbook);
            importInternalControlLog(workbook);
            
            System.out.println("\n========== 数据导入完成 ==========");
            
        } catch (Exception e) {
            System.err.println("导入数据失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void importSupplier(Workbook workbook) throws SQLException {
        Sheet sheet = workbook.getSheet("supplier");
        if (sheet == null) return;
        
        String sql = "REPLACE INTO supplier (supplier_id, supplier_name, credit_grade) VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            int count = 0;
            for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null) continue;
                
                pstmt.setString(1, getStringValue(row.getCell(0)));
                pstmt.setString(2, getStringValue(row.getCell(1)));
                pstmt.setString(3, getStringValue(row.getCell(2)));
                pstmt.addBatch();
                count++;
            }
            
            pstmt.executeBatch();
            System.out.println("✓ 供应商信息导入完成，共 " + count + " 条");
        }
    }
    
    private void importProduct(Workbook workbook) throws SQLException {
        Sheet sheet = workbook.getSheet("product");
        if (sheet == null) return;
        
        String sql = "REPLACE INTO product (product_code, product_name, unit) VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            int count = 0;
            for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null) continue;
                
                pstmt.setString(1, getStringValue(row.getCell(0)));
                pstmt.setString(2, getStringValue(row.getCell(1)));
                pstmt.setString(3, getStringValue(row.getCell(2)));
                pstmt.addBatch();
                count++;
            }
            
            pstmt.executeBatch();
            System.out.println("✓ 产品信息导入完成，共 " + count + " 条");
        }
    }
    
    private void importPurchaseOrder(Workbook workbook) throws SQLException {
        Sheet sheet = workbook.getSheet("purchase_order");
        if (sheet == null) return;
        
        String sql = "REPLACE INTO purchase_order (order_id, order_date, supplier_id, material_code, material_name, quantity, unit_price, total_amount, approval_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            int count = 0;
            for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null) continue;
                
                pstmt.setString(1, getStringValue(row.getCell(0)));
                pstmt.setDate(2, getDateValue(row.getCell(1)));
                pstmt.setString(3, getStringValue(row.getCell(2)));
                pstmt.setString(4, getStringValue(row.getCell(3)));
                pstmt.setString(5, getStringValue(row.getCell(4)));
                pstmt.setBigDecimal(6, getBigDecimalValue(row.getCell(5)));
                pstmt.setBigDecimal(7, getBigDecimalValue(row.getCell(6)));
                pstmt.setBigDecimal(8, getBigDecimalValue(row.getCell(7)));
                pstmt.setString(9, getStringValue(row.getCell(8)));
                pstmt.addBatch();
                count++;
            }
            
            pstmt.executeBatch();
            System.out.println("✓ 采购订单导入完成，共 " + count + " 条");
        }
    }
    
    private void importPurchasePriceHistory(Workbook workbook) throws SQLException {
        Sheet sheet = workbook.getSheet("purchase_price_history");
        if (sheet == null) return;
        
        String sql = "REPLACE INTO purchase_price_history (material_code, order_date, supplier_id, unit_price, remark) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            int count = 0;
            for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null) continue;
                
                pstmt.setString(1, getStringValue(row.getCell(1)));
                pstmt.setDate(2, getDateValue(row.getCell(2)));
                pstmt.setString(3, getStringValue(row.getCell(3)));
                pstmt.setBigDecimal(4, getBigDecimalValue(row.getCell(4)));
                pstmt.setString(5, getStringValue(row.getCell(5)));
                pstmt.addBatch();
                count++;
            }
            
            pstmt.executeBatch();
            System.out.println("✓ 采购价格历史导入完成，共 " + count + " 条");
        }
    }
    
    private void importProductBom(Workbook workbook) throws SQLException {
        Sheet sheet = workbook.getSheet("product_bom");
        if (sheet == null) return;
        
        String sql = "REPLACE INTO product_bom (bom_id, product_code, product_name, material_code, material_name, quantity_per_unit, valid_from) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            int count = 0;
            for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null) continue;
                
                pstmt.setString(1, getStringValue(row.getCell(0)));
                pstmt.setString(2, getStringValue(row.getCell(1)));
                pstmt.setString(3, getStringValue(row.getCell(2)));
                pstmt.setString(4, getStringValue(row.getCell(3)));
                pstmt.setString(5, getStringValue(row.getCell(4)));
                pstmt.setBigDecimal(6, getBigDecimalValue(row.getCell(5)));
                pstmt.setDate(7, getDateValue(row.getCell(6)));
                pstmt.addBatch();
                count++;
            }
            
            pstmt.executeBatch();
            System.out.println("✓ 产品 BOM 导入完成，共 " + count + " 条");
        }
    }
    
    private void importProductionOrder(Workbook workbook) throws SQLException {
        Sheet sheet = workbook.getSheet("production_order");
        if (sheet == null) return;
        
        String sql = "REPLACE INTO production_order (prod_order_id, product_code, plan_quantity, actual_quantity, start_date, end_date, material_cost, labor_cost, manu_cost, total_cost) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            int count = 0;
            for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null) continue;
                
                pstmt.setString(1, getStringValue(row.getCell(0)));
                pstmt.setString(2, getStringValue(row.getCell(1)));
                pstmt.setBigDecimal(3, getBigDecimalValue(row.getCell(2)));
                pstmt.setBigDecimal(4, getBigDecimalValue(row.getCell(3)));
                pstmt.setDate(5, getDateValue(row.getCell(4)));
                pstmt.setDate(6, getDateValue(row.getCell(5)));
                pstmt.setBigDecimal(7, getBigDecimalValue(row.getCell(6)));
                pstmt.setBigDecimal(8, getBigDecimalValue(row.getCell(7)));
                pstmt.setBigDecimal(9, getBigDecimalValue(row.getCell(8)));
                pstmt.setBigDecimal(10, getBigDecimalValue(row.getCell(9)));
                pstmt.addBatch();
                count++;
            }
            
            pstmt.executeBatch();
            System.out.println("✓ 生产订单导入完成，共 " + count + " 条");
        }
    }
    
    private void importManufacturingCostDetail(Workbook workbook) throws SQLException {
        Sheet sheet = workbook.getSheet("manufacturing_cost_detail");
        if (sheet == null) return;
        
        String sql = "REPLACE INTO manufacturing_cost_detail (cost_date, cost_category, amount, production_line, approver) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            int count = 0;
            for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null) continue;
                
                pstmt.setDate(1, getDateValue(row.getCell(1)));
                pstmt.setString(2, getStringValue(row.getCell(2)));
                pstmt.setBigDecimal(3, getBigDecimalValue(row.getCell(3)));
                pstmt.setString(4, getStringValue(row.getCell(4)));
                pstmt.setString(5, getStringValue(row.getCell(5)));
                pstmt.addBatch();
                count++;
            }
            
            pstmt.executeBatch();
            System.out.println("✓ 制造费用明细导入完成，共 " + count + " 条");
        }
    }
    
    private void importBudgetExecution(Workbook workbook) throws SQLException {
        Sheet sheet = workbook.getSheet("budget_execution");
        if (sheet == null) return;
        
        String sql = "REPLACE INTO budget_execution (fiscal_year, period, budget_item, budget_amount, actual_amount, variance, variance_rate) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            int count = 0;
            for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null) continue;
                
                pstmt.setInt(1, getIntValue(row.getCell(0)));
                pstmt.setString(2, getStringValue(row.getCell(1)));
                pstmt.setString(3, getStringValue(row.getCell(2)));
                pstmt.setBigDecimal(4, getBigDecimalValue(row.getCell(3)));
                pstmt.setBigDecimal(5, getBigDecimalValue(row.getCell(4)));
                pstmt.setBigDecimal(6, getBigDecimalValue(row.getCell(5)));
                pstmt.setBigDecimal(7, getBigDecimalValue(row.getCell(6)));
                pstmt.addBatch();
                count++;
            }
            
            pstmt.executeBatch();
            System.out.println("✓ 预算执行数据导入完成，共 " + count + " 条");
        }
    }
    
    private void importInternalControlLog(Workbook workbook) throws SQLException {
        Sheet sheet = workbook.getSheet("internal_control_log");
        if (sheet == null) return;
        
        String sql = "REPLACE INTO internal_control_log (alert_type, related_doc_no, alert_time, alert_content, handle_status, handler) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            int count = 0;
            for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null) continue;
                
                pstmt.setString(1, getStringValue(row.getCell(1)));
                pstmt.setString(2, getStringValue(row.getCell(2)));
                pstmt.setTimestamp(3, getTimestampValue(row.getCell(3)));
                pstmt.setString(4, getStringValue(row.getCell(4)));
                pstmt.setString(5, getStringValue(row.getCell(5)));
                pstmt.setString(6, getStringValue(row.getCell(6)));
                pstmt.addBatch();
                count++;
            }
            
            pstmt.executeBatch();
            System.out.println("✓ 内控预警日志导入完成，共 " + count + " 条");
        }
    }
    
    // 辅助方法
    private String getStringValue(Cell cell) {
        return cell != null ? cell.toString() : "";
    }
    
    private BigDecimal getBigDecimalValue(Cell cell) {
        if (cell == null) return BigDecimal.ZERO;
        try {
            switch (cell.getCellType()) {
                case NUMERIC:
                    return BigDecimal.valueOf(cell.getNumericCellValue());
                case STRING:
                    String str = cell.getStringCellValue().trim();
                    // 如果是空字符串或非数字字符，返回 0
                    if (str.isEmpty() || !str.matches("^-?\\d+(\\.\\d+)?$")) {
                        return BigDecimal.ZERO;
                    }
                    return new BigDecimal(str);
                default:
                    return BigDecimal.ZERO;
            }
        } catch (Exception e) {
            System.err.println("转换 BigDecimal 失败：" + cell.toString() + " - " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }
    
    private java.sql.Date getDateValue(Cell cell) {
        if (cell == null) return null;
        
        // 尝试从 NUMERIC 类型读取日期
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            java.util.Date date = cell.getDateCellValue();
            return new java.sql.Date(date.getTime());
        } 
        // 尝试从 STRING 类型读取日期
        else if (cell.getCellType() == CellType.STRING) {
            String dateStr = cell.getStringCellValue().trim();
            if (!dateStr.isEmpty()) {
                // 尝试多种日期格式
                try {
                    // 格式：yyyy-MM-dd (标准格式)
                    if (dateStr.contains("-") && dateStr.split("-").length == 3) {
                        String[] parts = dateStr.split("-");
                        int year = Integer.parseInt(parts[0]);
                        int month = Integer.parseInt(parts[1]);
                        int day = Integer.parseInt(parts[2]);
                        LocalDate localDate = LocalDate.of(year, month, day);
                        return java.sql.Date.valueOf(localDate);
                    }
                    // 格式：yyyy/M/d (如 2023/1/4)
                    else if (dateStr.contains("/")) {
                        String[] parts = dateStr.split("/");
                        if (parts.length == 3) {
                            int year = Integer.parseInt(parts[0]);
                            int month = Integer.parseInt(parts[1]);
                            int day = Integer.parseInt(parts[2]);
                            LocalDate localDate = LocalDate.of(year, month, day);
                            return java.sql.Date.valueOf(localDate);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("解析日期失败：" + dateStr + " - " + e.getMessage());
                }
            }
        }
        // 尝试从数字格式读取 (Excel 内部存储为数字)
        else if (cell.getCellType() == CellType.NUMERIC) {
            double numericValue = cell.getNumericCellValue();
            if (!Double.isNaN(numericValue)) {
                try {
                    java.util.Date date = org.apache.poi.ss.usermodel.DateUtil.getJavaDate(numericValue);
                    return new java.sql.Date(date.getTime());
                } catch (Exception e) {
                    System.err.println("转换数字日期失败：" + numericValue);
                }
            }
        }
        return null;
    }
    
    private Timestamp getTimestampValue(Cell cell) {
        if (cell == null) return null;
        
        // 尝试从 NUMERIC 类型读取日期时间
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            java.util.Date date = cell.getDateCellValue();
            return new Timestamp(date.getTime());
        }
        // 尝试从 STRING 类型读取
        else if (cell.getCellType() == CellType.STRING) {
            String dateTimeStr = cell.getStringCellValue().trim();
            if (!dateTimeStr.isEmpty()) {
                try {
                    // 尝试解析多种格式
                    // 格式：yyyy-MM-dd HH:mm
                    if (dateTimeStr.contains("-") && dateTimeStr.contains(":")) {
                        // 先分割日期和时间
                        String[] parts = dateTimeStr.split("\\s+");
                        String datePart = parts[0];
                        String timePart = parts.length > 1 ? parts[1] : "00:00:00";
                        
                        // 解析日期部分 (处理 2023-01-4 这种格式)
                        String[] dateComponents = datePart.split("-");
                        int year = Integer.parseInt(dateComponents[0]);
                        int month = Integer.parseInt(dateComponents[1]);
                        int day = Integer.parseInt(dateComponents[2]);
                        
                        // 解析时间部分
                        String[] timeComponents = timePart.split(":");
                        int hour = Integer.parseInt(timeComponents[0]);
                        int minute = Integer.parseInt(timeComponents[1]);
                        int second = timeComponents.length > 2 ? Integer.parseInt(timeComponents[2]) : 0;
                        
                        java.time.LocalDateTime ldt = java.time.LocalDateTime.of(year, month, day, hour, minute, second);
                        return Timestamp.valueOf(ldt);
                    }
                    // 格式：yyyy-MM-dd (只有日期)
                    else if (dateTimeStr.contains("-")) {
                        String[] parts = dateTimeStr.split("-");
                        if (parts.length == 3) {
                            int year = Integer.parseInt(parts[0]);
                            int month = Integer.parseInt(parts[1]);
                            int day = Integer.parseInt(parts[2].split("\\s+")[0]);
                            java.time.LocalDate ld = java.time.LocalDate.of(year, month, day);
                            return Timestamp.valueOf(ld.atStartOfDay());
                        }
                    }
                } catch (Exception e) {
                    System.err.println("解析时间戳失败：" + dateTimeStr + " - " + e.getMessage());
                }
            }
        }
        // 尝试从数字格式读取
        else if (cell.getCellType() == CellType.NUMERIC) {
            double numericValue = cell.getNumericCellValue();
            if (!Double.isNaN(numericValue)) {
                try {
                    java.util.Date date = org.apache.poi.ss.usermodel.DateUtil.getJavaDate(numericValue);
                    return new Timestamp(date.getTime());
                } catch (Exception e) {
                    System.err.println("转换数字时间戳失败：" + numericValue);
                }
            }
        }
        return null;
    }
    
    private Integer getIntValue(Cell cell) {
        if (cell == null) return 0;
        if (cell.getCellType() == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        }
        return 0;
    }
}
