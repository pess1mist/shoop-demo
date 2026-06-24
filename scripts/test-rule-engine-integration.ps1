# 审批规则引擎集成测试
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   审批规则引擎集成测试" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

$baseUrl = "http://localhost:8080/api"

Write-Host "【测试场景1】采购审批 - 小额采购（3万元）" -ForegroundColor Yellow
Write-Host "预期结果：匹配'小额采购-部门经理审批'规则，直接到部门经理节点`n" -ForegroundColor White
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/workflow/instance/start-with-data?processKey=PURCHASE_PLAN&businessType=PURCHASE&businessKey=TEST_001&userId=1&userName=张三&businessData=30000" -Method Post
    Write-Host "✓ 流程启动成功，实例ID: $($response.data)" -ForegroundColor Green
} catch {
    Write-Host "✗ 启动失败: $_" -ForegroundColor Red
}

Start-Sleep -Seconds 1

Write-Host "`n【测试场景2】采购审批 - 中额采购（8万元）" -ForegroundColor Yellow
Write-Host "预期结果：匹配'中额采购-财务审批'规则，直接到财务节点`n" -ForegroundColor White
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/workflow/instance/start-with-data?processKey=PURCHASE_PLAN&businessType=PURCHASE&businessKey=TEST_002&userId=1&userName=张三&businessData=80000" -Method Post
    Write-Host "✓ 流程启动成功，实例ID: $($response.data)" -ForegroundColor Green
} catch {
    Write-Host "✗ 启动失败: $_" -ForegroundColor Red
}

Start-Sleep -Seconds 1

Write-Host "`n【测试场景3】采购审批 - 大额采购（60万元）" -ForegroundColor Yellow
Write-Host "预期结果：匹配'大额采购-总经理审批'规则，直接到总经理节点`n" -ForegroundColor White
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/workflow/instance/start-with-data?processKey=PURCHASE_PLAN&businessType=PURCHASE&businessKey=TEST_003&userId=1&userName=张三&businessData=600000" -Method Post
    Write-Host "✓ 流程启动成功，实例ID: $($response.data)" -ForegroundColor Green
} catch {
    Write-Host "✗ 启动失败: $_" -ForegroundColor Red
}

Start-Sleep -Seconds 1

Write-Host "`n【测试场景4】预算调整 - 小额调整（5万元）" -ForegroundColor Yellow
Write-Host "预期结果：匹配'小额预算调整-财务审批'规则，直接到财务节点`n" -ForegroundColor White
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/workflow/instance/start-with-data?processKey=BUDGET_ADJUST&businessType=BUDGET&businessKey=TEST_004&userId=1&userName=李四&businessData=50000" -Method Post
    Write-Host "✓ 流程启动成功，实例ID: $($response.data)" -ForegroundColor Green
} catch {
    Write-Host "✗ 启动失败: $_" -ForegroundColor Red
}

Start-Sleep -Seconds 1

Write-Host "`n【测试场景5】预算调整 - 大额调整（15万元）" -ForegroundColor Yellow
Write-Host "预期结果：匹配'大额预算调整-总经理审批'规则，直接到总经理节点`n" -ForegroundColor White
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/workflow/instance/start-with-data?processKey=BUDGET_ADJUST&businessType=BUDGET&businessKey=TEST_005&userId=1&userName=李四&businessData=150000" -Method Post
    Write-Host "✓ 流程启动成功，实例ID: $($response.data)" -ForegroundColor Green
} catch {
    Write-Host "✗ 启动失败: $_" -ForegroundColor Red
}

Start-Sleep -Seconds 1

Write-Host "`n【对比测试】不使用规则引擎（不提供金额）" -ForegroundColor Yellow
Write-Host "预期结果：使用默认起始节点（第一个APPROVAL节点）`n" -ForegroundColor White
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/workflow/instance/start?processKey=PURCHASE_PLAN&businessType=PURCHASE&businessKey=TEST_006&userId=1&userName=王五" -Method Post
    Write-Host "✓ 流程启动成功，实例ID: $($response.data)" -ForegroundColor Green
} catch {
    Write-Host "✗ 启动失败: $_" -ForegroundColor Red
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "   测试完成！" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "`n说明：" -ForegroundColor Yellow
Write-Host "1. 提供 businessData 参数时，系统会自动匹配审批规则" -ForegroundColor White
Write-Host "2. 根据规则配置，流程会智能跳转到对应的审批节点" -ForegroundColor White
Write-Host "3. 不提供 businessData 时，使用默认的起始节点" -ForegroundColor White
Write-Host "4. 所有操作都会记录到 approval_log 表中" -ForegroundColor White
