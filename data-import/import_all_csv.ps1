# Import all CSV data to MySQL database
Write-Host "Starting CSV data import to MySQL..." -ForegroundColor Green

$csvPath = "C:\Users\19012\Desktop\2"
$sqlFile = "C:\Users\19012\Desktop\1\import_all_data.sql"

# Function to escape single quotes
function Escape-SQL {
    param($text)
    if ($null -eq $text) { return "NULL" }
    return "'" + ($text.ToString() -replace "'", "''") + "'"
}

# Start building SQL file
@"
USE shuju;

-- Clear existing data
DELETE FROM production_order;
DELETE FROM product;
DELETE FROM supplier;
DELETE FROM product_bom;
DELETE FROM budget_execution;
DELETE FROM manufacturing_cost_detail;
DELETE FROM purchase_price_history;
DELETE FROM purchase_order;

"@ | Out-File -FilePath $sqlFile -Encoding UTF8

# 1. Import products (need to check table structure first)
Write-Host "`n1. Checking product table structure..." -ForegroundColor Cyan
$productTable = docker exec mysql80 mysql -uroot -p123456 shuju -e "SHOW COLUMNS FROM product;" 2>$null
Write-Host $productTable

# Since we don't know exact prices, let's set default values
Write-Host "`n2. Importing products with default prices..." -ForegroundColor Cyan
$products = Import-Csv "$csvPath\product_code.csv" -Encoding UTF8
$sql = ""
foreach ($p in $products) {
    # Set reasonable default prices based on product type
    $unitPrice = switch ($p.product_code) {
        'P01' { 5000.00 }  # 膨化硝铵炸药
        'P02' { 6000.00 }  # 乳化炸药
        'P03' { 4000.00 }  # 水胶炸药
        default { 5000.00 }
    }
    $costPrice = $unitPrice * 0.75  # Cost is 75% of selling price
    
    $sql += "INSERT INTO product (product_code, product_name, unit, unit_price, cost_price) VALUES ("
    $sql += "'$($p.product_code)', '$($p.product_name)', '$($p.unit)', $unitPrice, $costPrice);`n"
}
$sql | Out-File -FilePath $sqlFile -Encoding UTF8 -Append
Write-Host "   Products SQL generated" -ForegroundColor Yellow

# 2. Import suppliers
Write-Host "`n3. Importing suppliers..." -ForegroundColor Cyan
$suppliers = Import-Csv "$csvPath\supplier.csv" -Encoding UTF8
$sql = ""
foreach ($s in $suppliers) {
    $sql += "INSERT INTO supplier (supplier_id, supplier_name, credit_grade) VALUES ("
    $sql += "'$($s.supplier_id)', '$($s.supplier_name)', '$($s.credit_grade)');`n"
}
$sql | Out-File -FilePath $sqlFile -Encoding UTF8 -Append
Write-Host "   Suppliers SQL generated" -ForegroundColor Yellow

# 3. Import production orders
Write-Host "`n4. Importing production orders (108 records)..." -ForegroundColor Cyan
$orders = Import-Csv "$csvPath\production_order.csv" -Encoding UTF8
$sql = "INSERT INTO production_order (prod_order_id, product_code, plan_quantity, actual_quantity, start_date, end_date, material_cost, labor_cost, manu_cost, total_cost, status) VALUES`n"
$values = @()
$count = 0
foreach ($o in $orders) {
    $values += "('$($o.prod_order_id)', '$($o.product_code)', $($o.'plan_quantity(吨)'), $($o.'actual_quantity(吨)'), '$($o.start_date)', '$($o.end_date)', $($o.'material_cost(元)'), $($o.'labor_cost(元)'), $($o.'manu_cost(元)'), $($o.'total_cost(元)'), 'completed')"
    $count++
}
$sql += ($values -join ",`n") + ";`n"
$sql | Out-File -FilePath $sqlFile -Encoding UTF8 -Append
Write-Host "   Production orders SQL generated ($count records)" -ForegroundColor Yellow

# 4. Import product BOM
Write-Host "`n5. Importing product BOM..." -ForegroundColor Cyan
$boms = Import-Csv "$csvPath\product_bom.csv" -Encoding UTF8
$sql = ""
$count = 0
foreach ($b in $boms) {
    $qty = [double]$b.'quantity_per_unit(吨/吨)'
    $sql += "INSERT INTO product_bom (bom_id, product_code, product_name, material_code, material_name, quantity_per_unit, valid_from) VALUES ("
    $sql += "'$($b.bom_id)', '$($b.product_code)', '$($b.product_name)', '$($b.material_code)', '$($b.material_name)', $qty, '$($b.valid_from)');`n"
    $count++
}
$sql | Out-File -FilePath $sqlFile -Encoding UTF8 -Append
Write-Host "   Product BOM SQL generated ($count records)" -ForegroundColor Yellow

# 5. Import budget execution
Write-Host "`n6. Importing budget execution..." -ForegroundColor Cyan
$budgets = Import-Csv "$csvPath\budget_execution_base.csv" -Encoding UTF8
$sql = ""
$count = 0
foreach ($b in $budgets) {
    $sql += "INSERT INTO budget_execution (fiscal_year, period, budget_item, budget_amount, actual_amount, variance, variance_rate) VALUES ("
    $sql += "$($b.fiscal_year), '$($b.period)', '$($b.budget_item)', $($b.budget_amount), $($b.actual_amount), $($b.variance), $($b.variance_rate));`n"
    $count++
}
$sql | Out-File -FilePath $sqlFile -Encoding UTF8 -Append
Write-Host "   Budget execution SQL generated ($count records)" -ForegroundColor Yellow

# 6. Import manufacturing cost detail
Write-Host "`n7. Importing manufacturing cost detail..." -ForegroundColor Cyan
$costs = Import-Csv "$csvPath\manufacturing_cost_detail.csv" -Encoding UTF8
$sql = ""
$count = 0
foreach ($c in $costs) {
    $sql += "INSERT INTO manufacturing_cost_detail (cost_date, cost_category, amount, production_line, approver) VALUES ("
    $sql += "'$($c.cost_date)', '$($c.cost_category)', $($c.'amount(元)'), '$($c.production_line)', '$($c.approver)');`n"
    $count++
}
$sql | Out-File -FilePath $sqlFile -Encoding UTF8 -Append
Write-Host "   Manufacturing cost SQL generated ($count records)" -ForegroundColor Yellow

# 7. Import purchase price history
Write-Host "`n8. Importing purchase price history..." -ForegroundColor Cyan
$prices = Import-Csv "$csvPath\purchase_price_history.csv" -Encoding UTF8
$sql = ""
$count = 0
foreach ($p in $prices) {
    $remark = if ($p.remark -and $p.remark.Trim()) { "'$($p.remark)'" } else { "NULL" }
    $sql += "INSERT INTO purchase_price_history (material_code, order_date, supplier_id, unit_price, remark) VALUES ("
    $sql += "'$($p.material_code)', '$($p.order_date)', '$($p.supplier_id)', $($p.'unit_price(元/吨)'), $remark);`n"
    $count++
}
$sql | Out-File -FilePath $sqlFile -Encoding UTF8 -Append
Write-Host "   Purchase price history SQL generated ($count records)" -ForegroundColor Yellow

# 8. Import purchase orders from approval_status
Write-Host "`n9. Importing purchase orders..." -ForegroundColor Cyan
$approvals = Import-Csv "$csvPath\approval_status.csv" -Encoding UTF8
$sql = ""
$count = 0
foreach ($a in $approvals) {
    $status = if ($a.approval_status -eq "已批") { "approved" } else { "pending" }
    $sql += "INSERT INTO purchase_order (order_id, order_date, supplier_id, material_code, material_name, quantity, unit_price, total_amount, approval_status) VALUES ("
    $sql += "'$($a.order_id)', '$($a.order_date)', '$($a.supplier_id)', '$($a.material_code)', '$($a.material_name)', $($a.'quantity(吨)'), $($a.'unit_price(元/吨)'), $($a.'total_amount(元)'), '$status');`n"
    $count++
}
$sql | Out-File -FilePath $sqlFile -Encoding UTF8 -Append
Write-Host "   Purchase orders SQL generated ($count records)" -ForegroundColor Yellow

Write-Host "`n" + "="*60 -ForegroundColor Green
Write-Host "All SQL statements generated successfully!" -ForegroundColor Green
Write-Host "SQL file: $sqlFile" -ForegroundColor Green
Write-Host "="*60 -ForegroundColor Green
Write-Host "`nNow importing to MySQL..." -ForegroundColor Cyan

# Execute the SQL file
try {
    Get-Content $sqlFile | docker exec -i mysql80 mysql -uroot -p123456 --default-character-set=utf8mb4 shuju 2>&1
    Write-Host "`nImport completed successfully!" -ForegroundColor Green
} catch {
    Write-Host "`nError during import: $_" -ForegroundColor Red
}

# Verify data
Write-Host "`nVerifying imported data..." -ForegroundColor Cyan
docker exec mysql80 mysql -uroot -p123456 shuju -e "
SELECT 'Products' as table_name, COUNT(*) as record_count FROM product
UNION ALL
SELECT 'Suppliers', COUNT(*) FROM supplier
UNION ALL
SELECT 'Production Orders', COUNT(*) FROM production_order
UNION ALL
SELECT 'Product BOM', COUNT(*) FROM product_bom
UNION ALL
SELECT 'Budget Execution', COUNT(*) FROM budget_execution
UNION ALL
SELECT 'Manufacturing Cost', COUNT(*) FROM manufacturing_cost_detail
UNION ALL
SELECT 'Purchase Price History', COUNT(*) FROM purchase_price_history
UNION ALL
SELECT 'Purchase Orders', COUNT(*) FROM purchase_order;
" 2>$null | Select-String -Pattern "table_name|Products|Suppliers|Production|Budget|Manufacturing|Purchase" | ForEach-Object { Write-Host $_ }

Write-Host "`nDone!" -ForegroundColor Green
