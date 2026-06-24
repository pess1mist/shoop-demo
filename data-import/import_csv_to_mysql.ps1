# Import all CSV data to MySQL database
# This script reads CSV files and generates SQL INSERT statements

$csvPath = "C:\Users\19012\Desktop\2"
$outputFile = "C:\Users\19012\Desktop\1\import_all_csv.sql"

# Clear output file
"" | Out-File -FilePath $outputFile -Encoding UTF8

# Function to escape single quotes in SQL
function Escape-SQL($text) {
    return $text -replace "'", "''"
}

# ========================================
# 1. Import product_bom.csv
# ========================================
Write-Host "Processing product_bom.csv..."
$bomData = Import-Csv "$csvPath\product_bom.csv"
$sql = "-- Import product BOM data`nINSERT INTO `product_bom` (`bom_id`, `product_code`, `product_name`, `material_code`, `material_name`, `quantity_per_unit`, `valid_from`) VALUES`n"
$values = @()
foreach ($row in $bomData) {
    $values += "('$($row.bom_id)', '$(Escape-SQL $row.product_code)', '$(Escape-SQL $row.product_name)', '$(Escape-SQL $row.material_code)', '$(Escape-SQL $row.material_name)', $($row.'quantity_per_unit(吨/吨)'), '$($row.valid_from)')"
}
$sql += ($values -join ",`n") + ";`n`n"
$sql | Out-File -FilePath $outputFile -Encoding UTF8 -Append

# ========================================
# 2. Import budget_execution_base.csv
# ========================================
Write-Host "Processing budget_execution_base.csv..."
$budgetData = Import-Csv "$csvPath\budget_execution_base.csv"
$sql = "-- Import budget execution base data`nINSERT INTO `budget_execution` (`fiscal_year`, `period`, `budget_item`, `budget_amount`, `actual_amount`, `variance`, `variance_rate`) VALUES`n"
$values = @()
foreach ($row in $budgetData) {
    $values += "($($row.fiscal_year), '$(Escape-SQL $row.period)', '$(Escape-SQL $row.budget_item)', $($row.budget_amount), $($row.actual_amount), $($row.variance), $($row.variance_rate))"
}
$sql += ($values -join ",`n") + ";`n`n"
$sql | Out-File -FilePath $outputFile -Encoding UTF8 -Append

# ========================================
# 3. Import manufacturing_cost_detail.csv
# ========================================
Write-Host "Processing manufacturing_cost_detail.csv..."
$costData = Import-Csv "$csvPath\manufacturing_cost_detail.csv"
$sql = "-- Import manufacturing cost detail data`nINSERT INTO `manufacturing_cost_detail` (`cost_date`, `cost_category`, `amount`, `production_line`, `approver`) VALUES`n"
$values = @()
foreach ($row in $costData) {
    $values += "('$($row.cost_date)', '$(Escape-SQL $row.cost_category)', $($row.'amount(元)'), '$(Escape-SQL $row.production_line)', '$(Escape-SQL $row.approver)')"
}
$sql += ($values -join ",`n") + ";`n`n"
$sql | Out-File -FilePath $outputFile -Encoding UTF8 -Append

# ========================================
# 4. Import purchase_price_history.csv
# ========================================
Write-Host "Processing purchase_price_history.csv..."
$priceData = Import-Csv "$csvPath\purchase_price_history.csv"
$sql = "-- Import purchase price history data`nINSERT INTO `purchase_price_history` (`material_code`, `order_date`, `supplier_id`, `unit_price`, `remark`) VALUES`n"
$values = @()
foreach ($row in $priceData) {
    $remark = if ($row.remark) { "'$(Escape-SQL $row.remark)'" } else { "NULL" }
    $values += "('$($row.material_code)', '$($row.order_date)', '$($row.supplier_id)', $($row.'unit_price(元/吨)'), $remark)"
}
$sql += ($values -join ",`n") + ";`n`n"
$sql | Out-File -FilePath $outputFile -Encoding UTF8 -Append

# ========================================
# 5. Import approval_status.csv (as purchase_order)
# ========================================
Write-Host "Processing approval_status.csv..."
$approvalData = Import-Csv "$csvPath\approval_status.csv"
$sql = "-- Import purchase order data from approval_status`nINSERT INTO `purchase_order` (`order_id`, `order_date`, `supplier_id`, `material_code`, `material_name`, `quantity`, `unit_price`, `total_amount`, `approval_status`) VALUES`n"
$values = @()
foreach ($row in $approvalData) {
    $status = if ($row.approval_status -eq "已批") { "approved" } else { "pending" }
    $values += "('$($row.order_id)', '$($row.order_date)', '$($row.supplier_id)', '$($row.material_code)', '$(Escape-SQL $row.material_name)', $($row.'quantity(吨)'), $($row.'unit_price(元/吨)'), $($row.'total_amount(元)'), '$status')"
}
$sql += ($values -join ",`n") + ";`n`n"
$sql | Out-File -FilePath $outputFile -Encoding UTF8 -Append

Write-Host "All CSV data has been processed!"
Write-Host "SQL file created: $outputFile"
Write-Host ""
Write-Host "To import, run:"
Write-Host "Get-Content import_all_csv.sql | docker exec -i mysql80 mysql -uroot -p123456 --default-character-set=utf8mb4 shuju"
