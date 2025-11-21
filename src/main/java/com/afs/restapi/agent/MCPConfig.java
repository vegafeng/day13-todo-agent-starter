package com.afs.restapi.agent;

import com.afs.restapi.service.DynamicApiToolService;
import com.afs.restapi.service.MessageService;
import com.afs.restapi.service.TodoService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MCPConfig {
    /**
     * 注册所有工具服务到 MCP 服务器
     * 包括：
     * - TodoService（4个工具）
     * - MessageService（1个工具）
     * - DynamicApiToolService（包含Shipment工具 + 其他JSON配置的动态工具）⭐ 已迁移
     * 
     * 📝 注意：ShipmentService 已被移除，其功能已迁移到 DynamicApiToolService
     *         现在所有工具都通过 JSON 配置驱动，无需为每个 API 编写 Service 类！
     */
    @Bean
    public ToolCallbackProvider myTools(
            TodoService todoService,
            MessageService messageService,
            DynamicApiToolService dynamicApiToolService) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(todoService, messageService, dynamicApiToolService)
                .build();
    }
}
