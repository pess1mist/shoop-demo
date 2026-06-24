# 审批规则引擎测试脚本
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   审批规则引擎功能测试" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

$baseUrl = "http://localhost:8080/api/workflow/rule"

Write-Host "1. 查询所有启用的审批规则..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/list" -Method Get
    Write-Host "✓ 查询成功，共 $($response.data.Count) 条规则" -ForegroundColor Green
    $response.data | Format-Table id, ruleName, ruleType, processKey, thresholdAmount, priority -AutoSize
} catch {
    Write-Host "✗ 查询失败: $_" -ForegroundColor Red
}

Write-Host "`n2. 查询采购审批流程的规则..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/list/PURCHASE_PLAN" -Method Get
    Write-Host "✓ 查询成功，共 $($response.data.Count) 条规则" -ForegroundColor Green
    $response.data | Format-Table id, ruleName, thresholdAmount, targetNodeKey, priority -AutoSize
} catch {
    Write-Host "✗ 查询失败: $_" -ForegroundColor Red
}

Write-Host "`n3. 测试规则匹配 - 小额采购 (3万元)..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/match?processKey=PURCHASE_PLAN&businessData=30000" -Method Post
    if ($response.code -eq 200) {
        Write-Host "✓ 匹配成功" -ForegroundColor Green
        Write-Host "  规则名称: $($response.data.ruleName)" -ForegroundColor White
        Write-Host "  金额阈值: $($response.data.thresholdAmount)" -ForegroundColor White
        Write-Host "  目标节点: $($response.data.targetNodeKey)" -ForegroundColor White
    } else {
        Write-Host "✗ 匹配失败: $($response.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ 请求失败: $_" -ForegroundColor Red
}

Write-Host "`n4. 测试规则匹配 - 中额采购 (8万元)..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/match?processKey=PURCHASE_PLAN&businessData=80000" -Method Post
    if ($response.code -eq 200) {
        Write-Host "✓ 匹配成功" -ForegroundColor Green
        Write-Host "  规则名称: $($response.data.ruleName)" -ForegroundColor White
        Write-Host "  金额阈值: $($response.data.thresholdAmount)" -ForegroundColor White
        Write-Host "  目标节点: $($response.data.targetNodeKey)" -ForegroundColor White
    } else {
        Write-Host "✗ 匹配失败: $($response.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ 请求失败: $_" -ForegroundColor Red
}

Write-Host "`n5. 测试规则匹配 - 大额采购 (60万元)..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/match?processKey=PURCHASE_PLAN&businessData=600000" -Method Post
    if ($response.code -eq 200) {
        Write-Host "✓ 匹配成功" -ForegroundColor Green
        Write-Host "  规则名称: $($response.data.ruleName)" -ForegroundColor White
        Write-Host "  金额阈值: $($response.data.thresholdAmount)" -ForegroundColor White
        Write-Host "  目标节点: $($response.data.targetNodeKey)" -ForegroundColor White
    } else {
        Write-Host "✗ 匹配失败: $($response.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ 请求失败: $_" -ForegroundColor Red
}

Write-Host "`n6. 测试规则匹配 - 预算调整 (15万元)..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/match?processKey=BUDGET_ADJUST&businessData=150000" -Method Post
    if ($response.code -eq 200) {
        Write-Host "✓ 匹配成功" -ForegroundColor Green
        Write-Host "  规则名称: $($response.data.ruleName)" -ForegroundColor White
        Write-Host "  金额阈值: $($response.data.thresholdAmount)" -ForegroundColor White
        Write-Host "  目标节点: $($response.data.targetNodeKey)" -ForegroundColor White
    } else {
        Write-Host "✗ 匹配失败: $($response.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ 请求失败: $_" -ForegroundColor Red
}

Write-Host "`n7. 测试单个规则 - ID=2, 金额=6万..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/test?ruleId=2&businessData=60000" -Method Post
    if ($response.code -eq 200) {
        Write-Host "✓ 测试结果: $($response.data)" -ForegroundColor Green
    } else {
        Write-Host "✗ 测试失败: $($response.message)" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ 请求失败: $_" -ForegroundColor Red
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "   测试完成！" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
