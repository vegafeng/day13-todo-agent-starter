# MCP Service 使用演示

## 🚀 快速开始

### 1. 启动服务

```bash
# 在项目根目录执行
./gradlew bootRun

# Windows 用户使用
gradlew.bat bootRun
```

服务启动后，SSE 端点可用：`http://localhost:8080/sse`

### 2. 配置 AI 客户端

在 VS Code 的 MCP 配置文件中添加（位置：`C:\Users\<YourName>\AppData\Roaming\Code\User\mcp.json`）：

```json
{
  "servers": {
    "todo-agent": {
      "url": "http://localhost:8080/sse"
    }
  }
}
```

或使用 `mcpServers` 格式：

```json
{
  "mcpServers": {
    "todo-agent": {
      "url": "http://localhost:8080/sse"
    }
  }
}
```

---

## 📋 使用示例

### 示例 1：Todo 管理（基础功能）

#### 对话 1：创建待办事项
```
👤 用户：帮我创建一个待办事项：完成 MCP 文档编写

🤖 AI：
好的，我已经为您创建了待办事项：
✅ Todo #1: 完成 MCP 文档编写
状态：未完成
```

#### 对话 2：查看所有待办事项
```
👤 用户：显示所有待办事项

🤖 AI：
您当前有以下待办事项：
1. [❌] 完成 MCP 文档编写
2. [❌] 测试 Shipment Service
3. [✅] 阅读 Spring AI 文档
```

#### 对话 3：标记完成
```
👤 用户：把第一个待办事项标记为完成

🤖 AI：
✅ 已将 Todo #1 标记为完成
更新后的待办事项：
[✅] 完成 MCP 文档编写
```

---

### 示例 2：Shipment POR 更新（实战案例）

#### 场景：更新货物的 POR (Port of Receipt)

**原始 API 调用方式**（需要记住复杂命令）：
```bash
curl --location --request PUT \
  'http://irisbackendqa5.lines.coscoshipping.com/wls_dom_shp/rest/shipment/updateShpPOR/7261242220,738872886233503' \
  --header 'Accept: application/json' \
  --header 'Content-Type: application/json; charset=UTF-8' \
  --data-raw '{}'
```

**MCP 方式**（自然语言交互）：

#### 对话 1：简单更新
```
👤 用户：请将货物 7261242220 的 POR 更新为 738872886233503

🤖 AI：
正在更新货物 POR...
✅ Successfully updated POR for shipment 7261242220 with POR ID 738872886233503.
Response: {"success": true, "message": "POR updated successfully"}
```

#### 对话 2：更新并验证
```
👤 用户：更新货物 7261242220 的 POR 为 738872886233503，并验证结果

🤖 AI：
=== Step 1: Updating POR ===
✅ Successfully updated POR for shipment 7261242220 with POR ID 738872886233503.

=== Step 2: Verifying Changes ===
✅ Shipment verification successful for 7261242220. 
Details: {
  "shipmentNumber": "7261242220",
  "portOfReceipt": "738872886233503",
  "status": "updated",
  "lastModified": "2025-01-15T10:30:00Z"
}
```

#### 对话 3：批量操作
```
👤 用户：我需要更新以下货物的 POR：
- 7261242220 -> 738872886233503
- 7261242221 -> 738872886233504
请分别更新并验证

🤖 AI：
好的，我来依次处理这些更新：

📦 货物 1: 7261242220
=== Updating POR ===
✅ Successfully updated POR to 738872886233503
=== Verifying ===
✅ Verification successful

📦 货物 2: 7261242221
=== Updating POR ===
✅ Successfully updated POR to 738872886233504
=== Verifying ===
✅ Verification successful

✨ 所有更新已完成！
```

---

### 示例 3：复杂组合操作

#### 场景：支持团队日常工作流程

```
👤 用户：我是 support，需要处理一个客户请求：
1. 查询货物 7261242220 的当前状态
2. 如果 POR 不是 738872886233503，更新它
3. 更新后创建一个待办事项记录这个操作
4. 把这个待办标记为完成

🤖 AI：
好的，我来帮您完成这个工作流程：

📋 Step 1: 查询货物状态
✅ Shipment 7261242220 current POR: 738872886233500

📋 Step 2: 检测到 POR 不同，执行更新
✅ Successfully updated POR to 738872886233503

📋 Step 3: 创建待办事项
✅ Todo #5 created: "Updated POR for shipment 7261242220 to 738872886233503"

📋 Step 4: 标记为完成
✅ Todo #5 marked as done

✨ 工作流程完成！所有步骤已成功执行。
```

---

## 🎯 实际应用价值

### 传统方式 vs MCP 方式对比

| 任务 | 传统方式 | MCP 方式 | 节省时间 |
|------|---------|---------|----------|
| 查找 API 文档 | 5 分钟 | 0 分钟 | ⏱️ 100% |
| 构建请求命令 | 3 分钟 | 0 分钟 | ⏱️ 100% |
| 执行验证 | 2 分钟 | 0 分钟 | ⏱️ 100% |
| 记录操作日志 | 5 分钟 | 0 分钟 | ⏱️ 100% |
| **总计** | **15 分钟** | **1 分钟** | **⏱️ 93%** |

### Support 团队的典型场景

#### 场景 1：批量处理客户请求
```
原有方式：
1. 收到 Excel 表格，包含 50 个货物号需要更新 POR
2. 手动一个个执行 cURL 命令
3. 记录每个操作结果
4. 整理成报告发给客户
⏱️ 需要 2-3 小时

MCP 方式：
1. 告诉 AI："请根据这个列表更新所有货物的 POR 并生成报告"
2. AI 自动执行所有操作
3. 自动生成完整报告
⏱️ 需要 5-10 分钟
```

#### 场景 2：故障排查
```
Support：有个客户报告货物信息不对

原有方式：
1. 登录系统查询
2. 检查各个字段
3. 对比历史记录
4. 找出问题
5. 手动修复
⏱️ 需要 30 分钟

MCP 方式：
Support：查询货物 7261242220 的完整信息，对比应该的 POR，如果不对就修复它
AI：（自动完成所有步骤）
⏱️ 需要 2 分钟
```

---

## 🔧 开发者指南：创建你自己的 MCP Tool

### Step 1: 创建 Service 类

```java
@Service
public class MyCustomService {
    
    private final RestTemplate restTemplate;
    
    public MyCustomService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
```

### Step 2: 添加工具方法

```java
@Tool(description = "Your tool description here. " +
                    "Explain what it does, parameters needed, and expected output. " +
                    "Example: param1='value1', param2='value2'")
public String myCustomTool(String param1, String param2) {
    try {
        // 1. 构建请求
        String url = "https://api.example.com/endpoint";
        
        // 2. 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // 3. 创建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("param1", param1);
        requestBody.put("param2", param2);
        
        // 4. 发送请求
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(
            url, HttpMethod.POST, entity, String.class
        );
        
        // 5. 处理响应
        if (response.getStatusCode().is2xxSuccessful()) {
            return "✅ Success: " + response.getBody();
        } else {
            return "❌ Failed: " + response.getStatusCode();
        }
        
    } catch (Exception e) {
        logger.error("Error calling API", e);
        return "❌ Error: " + e.getMessage();
    }
}
```

### Step 3: 测试你的工具

启动服务后，在 AI 对话中测试：
```
👤 用户：使用 myCustomTool，参数 param1=test1, param2=test2

🤖 AI：（自动调用你的工具并返回结果）
```

---

## 📊 监控和日志

### 查看工具调用日志

服务会自动记录所有工具调用：

```log
2025-01-15 10:30:00 INFO  ShipmentService - Updating shipment POR: shipmentNumber=7261242220, porId=738872886233503
2025-01-15 10:30:01 INFO  ShipmentService - Update POR response: status=200 OK, body={"success":true}
2025-01-15 10:30:02 INFO  ShipmentService - Verifying shipment: shipmentNumber=7261242220
```

### 可用工具列表

启动时会显示所有注册的工具：

```log
2025-01-15 10:00:00 INFO  ToolLoader - Tool scanning completed: 2 beans with 7 @Tool methods found
2025-01-15 10:00:00 INFO  ToolLoader - Registered tool bean: todoService (TodoService)
2025-01-15 10:00:00 INFO  ToolLoader - Registered tool bean: shipmentService (ShipmentService)
```

---

## 🌟 最佳实践

### 1. 编写清晰的 Tool Description

```java
// ❌ 不好：描述太简单
@Tool(description = "Update POR")

// ✅ 好：详细说明功能、参数和示例
@Tool(description = "Update the POR (Port of Receipt) for a specific shipment. " +
                    "This modifies the origin port where the cargo is received. " +
                    "Parameters: shipmentNumber (e.g., '7261242220'), " +
                    "porId (e.g., '738872886233503'). " +
                    "Returns success/failure status with details.")
```

### 2. 提供友好的返回信息

```java
// ❌ 不好：返回原始数据
return response.getBody();

// ✅ 好：格式化的、带 emoji 的信息
return String.format("✅ Successfully updated POR for shipment %s. " +
                     "New POR ID: %s. Response: %s",
                     shipmentNumber, porId, response.getBody());
```

### 3. 完善的错误处理

```java
try {
    // API 调用
} catch (HttpClientErrorException e) {
    logger.error("Client error: {}", e.getMessage());
    return "❌ Invalid request: " + e.getMessage();
} catch (HttpServerErrorException e) {
    logger.error("Server error: {}", e.getMessage());
    return "❌ Server error: " + e.getMessage();
} catch (Exception e) {
    logger.error("Unexpected error", e);
    return "❌ Unexpected error: " + e.getMessage();
}
```

### 4. 组合工具提供高级功能

```java
@Tool(description = "Update and verify in one operation")
public String updateAndVerify(String shipmentNumber, String porId) {
    // 调用基础工具
    String updateResult = updateShipmentPOR(shipmentNumber, porId);
    
    if (updateResult.contains("✅")) {
        String verifyResult = verifyShipment(shipmentNumber);
        return updateResult + "\n\n" + verifyResult;
    }
    
    return updateResult + "\n⚠️ Skipped verification due to update failure";
}
```

---

## 🚀 下一步

1. **扩展更多工具**：将你的其他 API 转换为 MCP 工具
2. **配置权限**：添加认证和授权机制
3. **部署到生产**：配置生产环境的端点
4. **监控告警**：添加监控和告警机制
5. **团队培训**：培训 Support 团队使用 AI Agent

---

## 💡 常见问题

### Q1: 工具没有被 AI 发现？
**A:** 检查：
1. 方法是否有 `@Tool` 注解
2. 类是否有 `@Service` 注解
3. 查看启动日志确认工具已注册

### Q2: API 调用失败？
**A:** 检查：
1. URL 是否正确
2. 网络连接是否正常
3. 查看日志获取详细错误信息

### Q3: AI 不知道如何使用工具？
**A:** 改进：
1. 完善 `@Tool` 的 description
2. 在描述中添加参数示例
3. 提供使用场景说明

### Q4: 如何调试工具？
**A:** 方法：
1. 查看控制台日志
2. 添加更多日志输出
3. 直接测试 REST API
4. 使用断点调试

---

**让 AI 成为你的得力助手！** 🎉
