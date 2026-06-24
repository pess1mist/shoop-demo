# 后端 API 接口文档

## 项目结构

```
src/main/java/com/example/finance/
├── FinanceApplication.java          # 主启动类
├── config/                          # 配置类
│   ├── MybatisPlusConfig.java
│   └── WebSocketConfig.java
├── controller/                      # REST 控制器
│   ├── PurchaseOrderController.java      # 采购订单
│   ├── PurchasePriceController.java      # 采购价格
│   ├── ProductBomController.java         # 产品 BOM
│   ├── ProductionOrderController.java    # 生产订单
│   ├── ManufacturingCostController.java  # 制造费用
│   ├── BudgetExecutionController.java    # 预算执行
│   └── InternalControlController.java    # 内控预警
├── entity/                          # 实体类
│   ├── PurchaseOrder.java
│   ├── PurchasePriceHistory.java
│   ├── ProductBom.java
│   ├── ProductionOrder.java
│   ├── ManufacturingCostDetail.java
│   ├── BudgetExecution.java
│   └── InternalControlLog.java
├── mapper/                          # MyBatis Mapper 接口
│   ├── PurchaseOrderMapper.java
│   ├── PurchasePriceHistoryMapper.java
│   ├── ProductBomMapper.java
│   ├── ProductionOrderMapper.java
│   ├── ManufacturingCostDetailMapper.java
│   ├── BudgetExecutionMapper.java
│   └── InternalControlLogMapper.java
├── service/                         # 服务接口
│   ├── PurchaseOrderService.java
│   ├── PurchasePriceHistoryService.java
│   ├── ProductBomService.java
│   ├── ProductionOrderService.java
│   ├── ManufacturingCostDetailService.java
│   ├── BudgetExecutionService.java
│   └── InternalControlLogService.java
└── service/impl/                    # 服务实现类
    ├── PurchaseOrderServiceImpl.java
    ├── PurchasePriceHistoryServiceImpl.java
    ├── ProductBomServiceImpl.java
    ├── ProductionOrderServiceImpl.java
    ├── ManufacturingCostDetailServiceImpl.java
    ├── BudgetExecutionServiceImpl.java
    └── InternalControlLogServiceImpl.java
```

---

## API 接口列表

### 1. 采购订单管理 `/api/purchase-order`

| 方法 | 路径 | 说明 | 参数 |
|------|------|------|------|
| GET | `/list` | 查询所有采购订单 | 无 |
| GET | `/list/{supplierId}` | 根据供应商查询订单 | supplierId: 供应商 ID |
| GET | `/list/material/{materialCode}` | 根据材料查询订单 | materialCode: 材料编码 |
| POST | `/add` | 新增采购订单 | PurchaseOrder 对象 |
| DELETE | `/delete/{orderId}` | 删除采购订单 | orderId: 订单 ID |
| PUT | `/update` | 更新采购订单 | PurchaseOrder 对象 |

**示例**:
```bash
GET http://localhost:8080/api/purchase-order/list
GET http://localhost:8080/api/purchase-order/list/S01
```

---

### 2. 采购价格历史 `/api/purchase-price`

| 方法 | 路径 | 说明 | 参数 |
|------|------|------|------|
| GET | `/list/{materialCode}` | 查询某材料的价格历史 | materialCode: 材料编码 |
| GET | `/analyze/{materialCode}` | 分析价格走势 | materialCode: 材料编码 |

**示例**:
```bash
GET http://localhost:8080/api/purchase-price/list/M1
GET http://localhost:8080/api/purchase-price/analyze/M1
```

---

### 3. 产品 BOM 管理 `/api/product-bom`

| 方法 | 路径 | 说明 | 参数 |
|------|------|------|------|
| GET | `/list/{productCode}` | 查询某产品的 BOM 清单 | productCode: 产品编码 |
| GET | `/list/material/{materialCode}` | 查询某材料用于哪些产品 | materialCode: 材料编码 |

**示例**:
```bash
GET http://localhost:8080/api/product-bom/list/P01
```

---

### 4. 生产订单管理 `/api/production-order`

| 方法 | 路径 | 说明 | 参数 |
|------|------|------|------|
| GET | `/list` | 查询所有生产订单 | 无 |
| GET | `/list/{productCode}` | 查询某产品的生产订单 | productCode: 产品编码 |
| GET | `/cost-summary/{productCode}` | 统计某产品的成本 | productCode: 产品编码 |

**示例**:
```bash
GET http://localhost:8080/api/production-order/list
GET http://localhost:8080/api/production-order/cost-summary/P01
```

---

### 5. 制造费用管理 `/api/manufacturing-cost`

| 方法 | 路径 | 说明 | 参数 |
|------|------|------|------|
| GET | `/list/{productionLine}` | 按生产线查询费用 | productionLine: 生产线 |
| GET | `/list/category/{costCategory}` | 按费用类别查询 | costCategory: 费用类别 |
| GET | `/total/{productionLine}` | 统计某生产线总费用 | productionLine: 生产线 |

**示例**:
```bash
GET http://localhost:8080/api/manufacturing-cost/list/膨化线
```

---

### 6. 预算执行管理 `/api/budget-execution`

| 方法 | 路径 | 说明 | 参数 |
|------|------|------|------|
| GET | `/list/{period}` | 查询某期间的预算执行 | period: 期间 (yyyy-MM) |
| GET | `/list/item/{budgetItem}` | 查询某预算项目的执行情况 | budgetItem: 预算项目 |
| GET | `/analyze/{budgetItem}` | 分析预算执行差异 | budgetItem: 预算项目 |

**示例**:
```bash
GET http://localhost:8080/api/budget-execution/list/2025-01
GET http://localhost:8080/api/budget-execution/analyze/材料费
```

---

### 7. 内控预警管理 `/api/internal-control`

| 方法 | 路径 | 说明 | 参数 |
|------|------|------|------|
| GET | `/list` | 查询所有预警日志 | 无 |
| GET | `/list/{alertType}` | 按预警类型查询 | alertType: 预警类型 |
| GET | `/list/status/{handleStatus}` | 按处理状态查询 | handleStatus: 处理状态 |
| PUT | `/update-status` | 更新预警处理状态 | logId, handleStatus, handler |

**示例**:
```bash
GET http://localhost:8080/api/internal-control/list
PUT http://localhost:8080/api/internal-control/update-status?logId=1&handleStatus=已处理&handler=张经理
```

---

## 数据实体说明

### PurchaseOrder (采购订单)
- orderId: 订单 ID
- orderDate: 订单日期
- supplierId: 供应商 ID
- materialCode: 材料编码
- materialName: 材料名称
- quantity: 数量（吨）
- unitPrice: 单价（元/吨）
- totalAmount: 总金额（元）
- approvalStatus: 审批状态

### PurchasePriceHistory (采购价格历史)
- id: 自增 ID
- materialCode: 材料编码
- orderDate: 日期
- supplierId: 供应商 ID
- unitPrice: 单价（元/吨）
- remark: 备注

### ProductBom (产品 BOM)
- bomId: BOM ID
- productCode: 产品编码
- productName: 产品名称
- materialCode: 材料编码
- materialName: 材料名称
- quantityPerUnit: 单耗（吨/吨）
- validFrom: 生效日期

### ProductionOrder (生产订单)
- prodOrderId: 生产订单 ID
- productCode: 产品编码
- planQuantity: 计划产量（吨）
- actualQuantity: 实际产量（吨）
- startDate: 开始日期
- endDate: 结束日期
- materialCost: 材料成本（元）
- laborCost: 人工成本（元）
- manuCost: 制造费用（元）
- totalCost: 总成本（元）

### ManufacturingCostDetail (制造费用明细)
- id: 自增 ID
- costDate: 费用日期
- costCategory: 费用类别
- amount: 金额（元）
- productionLine: 生产线
- approver: 审批人

### BudgetExecution (预算执行)
- id: 自增 ID
- fiscalYear: 财年
- period: 期间（yyyy-MM）
- budgetItem: 预算项目
- budgetAmount: 预算金额（元）
- actualAmount: 实际金额（元）
- variance: 差异（元）
- varianceRate: 差异率（%）

### InternalControlLog (内控预警日志)
- logId: 日志 ID
- alertType: 预警类型
- relatedDocNo: 相关单据号
- alertTime: 预警时间
- alertContent: 预警内容
- handleStatus: 处理状态
- handler: 处理人

---

## 技术栈

- **框架**: Spring Boot 3.1.5
- **ORM**: MyBatis Plus 3.5.6
- **数据库**: MySQL 8.0
- **JDK**: Java 17
- **其他**: Lombok, WebSocket, Quartz 定时任务

---

## 启动说明

### 1. 数据库配置
确保 MySQL 服务已启动，并创建 `finance_db` 数据库：
```sql
CREATE DATABASE finance_db DEFAULT CHARACTER SET utf8mb4;
```

### 2. 执行 SQL 脚本
```bash
# 执行表结构创建脚本
SOURCE c:\Users\19012\Desktop\1\create_tables.sql

# 执行数据插入脚本
SOURCE c:\Users\19012\Desktop\1\insert_data.sql
```

### 3. 启动后端
```bash
cd backend
mvn clean spring-boot:run
```

或者直接运行：
```bash
java -jar backend/target/finance-management-1.0.0.jar
```

### 4. 访问接口
启动成功后，访问：
```
http://localhost:8080/api/purchase-order/list
```

---

## 响应格式

所有接口返回统一格式：

**成功示例**:
```json
[
  {
    "orderId": "PO25001",
    "orderDate": "2025-01-10",
    "supplierId": "S01",
    "materialCode": "M1",
    "materialName": "硝酸铵",
    "quantity": 150.00,
    "unitPrice": 2850.00,
    "totalAmount": 427500.00,
    "approvalStatus": "已批"
  }
]
```

**错误示例**:
```json
{
  "timestamp": "2025-03-24 10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Resource not found",
  "path": "/api/purchase-order/xxx"
}
```

---

## 注意事项

1. **跨域配置**: 所有 Controller 已添加 `@CrossOrigin(origins = "*")`，允许跨域访问
2. **日期格式**: 所有日期字段使用 `yyyy-MM-dd` 格式
3. **金额精度**: 所有金额字段使用 `BigDecimal`，精度为 2 位小数
4. **字符集**: 使用 `utf8mb4` 字符集，支持中文
5. **主键生成策略**: 
   - 业务表使用 `ASSIGN_ID` (雪花算法)
   - 历史表使用 `AUTO` (自增)

---

**文档更新时间**: 2025-03-24  
**版本**: v1.0.0
