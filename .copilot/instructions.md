# GitHub Copilot 自动化指令

## 🎯 自动触发规则

### 当用户说以下关键词时，自动执行对应操作：

#### 1. JSON工具同步
**触发词**: "json文件已更新" / "把tools加一下" / "加一下tools" / "同步JSON配置"

**执行**: 阅读并执行 `ADD_JSON_TOOLS_GUIDE.md` 中的完整流程

**文件位置**: `./ADD_JSON_TOOLS_GUIDE.md`

**核心步骤**:
1. 读取 `src/main/resources/api-tools-config.json`
2. 检查 `src/main/java/com/afs/restapi/service/DynamicApiToolService.java`
3. 为新工具添加 `@Tool` 注解方法
4. 验证并报告

---

## 📋 快速参考

| 用户说 | AI做什么 | 参考文档 |
|-------|---------|---------|
| json文件已更新 | 同步JSON配置到Java代码 | ADD_JSON_TOOLS_GUIDE.md |
| 把tools加一下 | 同步JSON配置到Java代码 | ADD_JSON_TOOLS_GUIDE.md |

---

*此文件帮助 GitHub Copilot 自动识别用户意图并执行相应操作*
