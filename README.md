# MCP Service 完整实现指南

> 📖 **文档导航**: 不知道从哪里开始？查看 [INDEX.md](INDEX.md) 获取完整的文档导航和学习路径推荐！

> 🚀 **新功能**: 现在支持通过JSON配置文件将任何API转换为MCP Service！查看 [GENERIC_API_GUIDE.md](GENERIC_API_GUIDE.md) 了解详情。

## 📚 目录
- [什么是 MCP (Model Context Protocol)](#什么是-mcp-model-context-protocol)
- [核心概念](#核心概念)
- [🆕 通用API框架 - 零代码配置](#通用api框架---零代码配置)
- [如何将普通API转换为MCP Service](#如何将普通api转换为mcp-service)
- [实战案例：Shipment POR更新服务](#实战案例shipment-por更新服务)
- [MCP Registry 和 AI Hub](#mcp-registry-和-ai-hub)
- [项目配置与使用](#项目配置与使用)

---

## 通用API框架 - 零代码配置

### 🎯 快速开始

现在您可以通过简单的JSON配置将任何REST API转换为MCP Service工具，**无需编写Java代码**！

#### 1. 编辑配置文件

在 `src/main/resources/api-tools-config.json` 中添加您的API：

```json
{
  "apiTools": [
    {
      "name": "yourToolName",
      "description": "Tool description for AI",
      "method": "GET",
      "url": "https://api.example.com/endpoint/{param}",
      "headers": {
        "Authorization": "Bearer YOUR_TOKEN"
      },
      "parameters": [
        {
          "name": "param",
          "type": "String",
          "location": "path",
          "description": "Parameter description",
          "required": true
        }
      ],
      "successMessage": "✅ Operation successful",
      "errorMessage": "❌ Operation failed"
    }
  ]
}
```

#### 2. 重启应用

工具会自动注册并可供AI使用！

#### 3. 完整文档

- 📘 **完整指南**: [GENERIC_API_GUIDE.md](GENERIC_API_GUIDE.md)
- 📗 **添加示例**: [ADD_NEW_API_EXAMPLE.md](ADD_NEW_API_EXAMPLE.md)

### ✨ 核心优势

- ✅ **零代码配置** - 只需编辑JSON文件
- ✅ **自动工具注册** - 框架自动将API暴露给AI
- ✅ **灵活参数支持** - 支持路径参数、查询参数、请求体等
- ✅ **统一错误处理** - 自动处理HTTP响应和异常
- ✅ **易于维护** - 所有API配置集中管理

---

## 什么是 MCP (Model Context Protocol)

**MCP (Model Context Protocol)** 是一个开放标准协议，旨在让 AI 模型能够安全、高效地与外部工具和数据源进行交互。

### 核心价值
- **统一标准**：为 AI 工具集成提供标准化接口
- **安全可控**：提供明确的权限和调用边界
- **易于扩展**：任何 API 都可以快速转换为 MCP 工具
- **降低成本**：减少人工支持工作，让 AI Agent 自动处理常见任务

### MCP vs 传统 API
| 特性 | 传统 API | MCP Service |
|------|---------|-------------|
| AI 调用 | ❌ 需要人工编写调用逻辑 | ✅ AI 自动理解和调用 |
| 参数说明 | ❌ 需要查阅文档 | ✅ 通过 @Tool 注解自描述 |
| 错误处理 | ❌ 需要编写额外代码 | ✅ 统一的错误处理机制 |
| 工具组合 | ❌ 需要手动编排 | ✅ AI 自动组合多个工具 |

---

## 核心概念

### 1. **@Tool 注解**
Spring AI 提供的核心注解，用于将 Java 方法暴露给 AI：

```java
@Tool(description = "Clear description of what this tool does")
public String myTool(String param) {
    // Implementation
}
```

### 2. **Tool 自动发现**
项目使用 `ToolLoader` 自动扫描所有带有 `@Tool` 注解的方法：

```java
@Component
@Primary
public class ToolLoader {
    // 自动扫描所有 Spring Bean 中的 @Tool 注解
    private void scanForToolBeans() { ... }
}
```

### 3. **SSE (Server-Sent Events)**
MCP 使用 SSE 协议进行实时通信，AI 客户端通过 `/sse` 端点连接到服务器。

---

## 如何将普通API转换为MCP Service

### 步骤 1️⃣：创建 Service 类

```java
@Service
public class MyAPIService {
    private final RestTemplate restTemplate = new RestTemplate();
}
```

### 步骤 2️⃣：添加 @Tool 注解

```java
@Tool(description = "Clear description for AI to understand what this tool does, " +
                    "including parameter requirements and expected outputs")
public String myToolMethod(String param1, String param2) {
    // Your API call logic
}
```

### 步骤 3️⃣：实现业务逻辑

```java
@Tool(description = "Update shipment POR information")
public String updateShipmentPOR(String shipmentNumber, String porId) {
    try {
        // 1. 构建请求 URL
        String url = BASE_URL + "/" + shipmentNumber + "," + porId;
        
        // 2. 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // 3. 发送请求
        ResponseEntity<String> response = restTemplate.exchange(
            url, HttpMethod.PUT, 
            new HttpEntity<>(requestBody, headers), 
            String.class
        );
        
        // 4. 处理响应
        if (response.getStatusCode().is2xxSuccessful()) {
            return "✅ Success: " + response.getBody();
        }
        
    } catch (Exception e) {
        return "❌ Error: " + e.getMessage();
    }
}
```

### 步骤 4️⃣：注册到 Spring Context

由于使用了 `@Service` 注解，Spring 会自动注册该服务，`ToolLoader` 会自动发现并注册所有 `@Tool` 方法。

---

## 实战案例：Shipment POR更新服务

### 业务场景
COSCO 物流系统需要更新货物的 **POR (Port of Receipt)**，传统方式需要：
1. 手动查找 API 文档
2. 构建 cURL 命令
3. 执行请求
4. 验证结果

使用 MCP 后，只需告诉 AI：
> "请将货物 7261242220 的 POR 更新为 738872886233503"

AI 会自动：
1. 调用 `updateShipmentPOR` 工具
2. 调用 `verifyShipment` 工具验证
3. 返回结果

### 原始 cURL 命令

```bash
curl --location --request PUT \
  'http://irisbackendqa5.lines.coscoshipping.com/wls_dom_shp/rest/shipment/updateShpPOR/7261242220,738872886233503' \
  --header 'Accept: application/json' \
  --header 'Content-Type: application/json; charset=UTF-8' \
  --data-raw '{}'
```

### MCP Service 实现

详见 `ShipmentService.java`，核心工具包括：

#### 1. **updateShipmentPOR** - 更新 POR
```java
@Tool(description = "Update the POR (Port of Receipt) for a specific shipment")
public String updateShipmentPOR(String shipmentNumber, String porId)
```

#### 2. **verifyShipment** - 验证更新
```java
@Tool(description = "Verify shipment information and check if the POR update was successful")
public String verifyShipment(String shipmentNumber)
```

#### 3. **updateAndVerifyShipmentPOR** - 组合操作
```java
@Tool(description = "Update shipment POR and verify the result in one operation")
public String updateAndVerifyShipmentPOR(String shipmentNumber, String porId)
```

### 使用效果对比

**传统方式**：
```bash
# 需要记住复杂的 URL 和参数
curl --location --request PUT 'http://..../updateShpPOR/7261242220,738872886233503' ...
# 然后再验证
curl --location 'https://..../viewShp?type=browse&shipmentNumber=7261242220' ...
```

**MCP 方式**：
```
用户：请更新货物 7261242220 的 POR 为 738872886233503，并验证结果

AI：好的，我来帮您完成：
     1️⃣ 正在更新 POR...
     2️⃣ 正在验证更新...
     ✅ 更新成功！详细信息如下...
```

---

## MCP Registry 和 AI Hub

### 什么是 MCP Registry？

**MCP Registry** 是 MCP 工具的中央注册中心，类似于：
- npm registry（Node.js 包）
- Maven Central（Java 包）
- Docker Hub（容器镜像）

### AI Hub 的作用

**AI Hub** 是企业级 MCP 工具管理平台，提供：

1. **工具发现**：浏览和搜索可用的 MCP 工具
2. **版本管理**：管理工具的不同版本
3. **权限控制**：控制哪些团队可以使用哪些工具
4. **监控分析**：追踪工具使用情况和性能

### 如何使用 MCP Registry

#### 1. 注册你的 MCP Service
```json
{
  "name": "shipment-por-service",
  "version": "1.0.0",
  "description": "COSCO Shipment POR Management Service",
  "endpoint": "http://localhost:8080/sse",
  "tools": [
    {
      "name": "updateShipmentPOR",
      "description": "Update shipment POR information"
    }
  ]
}
```

#### 2. 在 AI Hub 中配置
```json
{
  "mcpServers": {
    "cosco-shipment": {
      "url": "http://your-server:8080/sse",
      "metadata": {
        "team": "logistics",
        "environment": "production"
      }
    }
  }
}
```

#### 3. 客户端使用
GitHub Copilot 或其他 AI 客户端在 `mcp.json` 中配置：

```json
{
  "servers": {
    "shipment-service": {
      "url": "http://localhost:8080/sse"
    }
  }
}
```

---

## 项目配置与使用

### 1. 环境要求
- Java 17+
- Spring Boot 3.x
- Spring AI 依赖

### 2. 配置 application.yml
```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat:
        model: gpt-4o
```

### 3. 启动服务
```bash
./gradlew bootRun
```

### 4. 配置 AI 客户端

在 VS Code 的 `settings.json` 或 `mcp.json` 中添加：

```json
{
  "servers": {
    "todo-agent": {
      "url": "http://localhost:8080/sse"
    }
  }
}
```

或者使用 `mcpServers` 格式：

```json
{
  "mcpServers": {
    "todo-agent": {
      "url": "http://localhost:8080/sse"
    }
  }
}
```

### 5. 使用示例

**示例 1：Todo 管理**
```
用户：帮我创建一个待办事项：完成 MCP 文档
AI：✅ 已创建待办事项 #1
```

**示例 2：Shipment POR 更新**
```
用户：更新货物 7261242220 的 POR 为 738872886233503
AI：
=== Step 1: Updating POR ===
✅ Successfully updated POR for shipment 7261242220

=== Step 2: Verifying Changes ===
✅ Shipment verification successful
```

**示例 3：组合操作**
```
用户：查询所有待办事项，然后创建一个新的，标记第一个为完成
AI：（自动调用 findAll、create、updateById 三个工具）
```

---

## 实用价值和应用场景

### 🎯 减少 Support Effort

**传统模式**：
- Support 团队需要手动执行 API 调用
- 需要记住各种命令和参数
- 容易出错，需要多次重试

**MCP 模式**：
- Support 人员用自然语言描述需求
- AI Agent 自动执行正确的 API 调用
- 自动验证结果，减少人为错误

### 📈 其他应用场景

1. **客户服务自动化**
   - 订单查询和修改
   - 退款处理
   - 物流追踪

2. **运维自动化**
   - 服务器状态检查
   - 日志分析
   - 自动故障修复

3. **数据分析**
   - 自动生成报表
   - 数据查询和聚合
   - 趋势分析

4. **开发辅助**
   - 代码审查
   - 测试用例生成
   - 文档自动更新

---

## 🔧 开发最佳实践

### 1. 清晰的 Tool Description
```java
// ❌ 不好
@Tool(description = "Update POR")

// ✅ 好
@Tool(description = "Update the POR (Port of Receipt) for a specific shipment. " +
                    "This tool modifies the port of receipt information. " +
                    "Example: shipmentNumber='7261242220', porId='738872886233503'")
```

### 2. 友好的返回信息
```java
// 使用 emoji 和结构化信息
return String.format("✅ Successfully updated POR for shipment %s", shipmentNumber);
return String.format("❌ Error: %s", error.getMessage());
```

### 3. 错误处理
```java
try {
    // API 调用
} catch (Exception e) {
    logger.error("Error details", e);
    return "❌ Error: " + e.getMessage();
}
```

### 4. 日志记录
```java
logger.info("Updating shipment POR: shipmentNumber={}, porId={}", shipmentNumber, porId);
```

---

## 📖 参考资源

- [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/)
- [Model Context Protocol Specification](https://spec.modelcontextprotocol.io/)
- [OpenAI Function Calling Guide](https://platform.openai.com/docs/guides/function-calling)

---

## 💡 总结

通过 MCP 协议和 Spring AI 的 `@Tool` 注解：

1. ✅ **任何 REST API** 都可以轻松转换为 AI 工具
2. ✅ **AI 自动理解**工具用途和参数
3. ✅ **大幅降低** Support 团队的工作量
4. ✅ **提高效率**，减少人为错误
5. ✅ **易于扩展**，支持复杂的业务场景

**让 AI 成为你的智能助手，而不仅仅是聊天机器人！** 🚀