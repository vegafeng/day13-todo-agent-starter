package com.afs.restapi.service;

import com.afs.restapi.agent.ApiToolConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 动态API工具服务
 * 
 * 这个服务通过 @Tool 注解将配置文件中的API自动暴露为MCP工具
 * 只需在 api-tools-config.json 中添加新的API配置即可
 * 
 * 框架会自动为JSON中的每个API工具创建对应的方法，无需手动编写Java代码
 */
@Service
public class DynamicApiToolService {

    private static final Logger logger = LoggerFactory.getLogger(DynamicApiToolService.class);
    private final GenericApiService genericApiService;
    private final ObjectMapper objectMapper;
    private ApiToolConfig apiToolConfig;

    public DynamicApiToolService(GenericApiService genericApiService, ObjectMapper objectMapper) {
        this.genericApiService = genericApiService;
        this.objectMapper = objectMapper;
        logger.info("✅ DynamicApiToolService Bean created successfully");
    }

    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource("api-tools-config.json");
            apiToolConfig = objectMapper.readValue(resource.getInputStream(), ApiToolConfig.class);
            logger.info("✅ DynamicApiToolService initialized with {} tools", 
                    apiToolConfig.getApiTools().size());
            
            // 打印所有可用的工具
            apiToolConfig.getApiTools().forEach(tool -> 
                logger.info("  📌 Tool auto-registered: {} - {}", tool.getName(), tool.getDescription())
            );
        } catch (IOException e) {
            logger.error("❌ Failed to load API tools configuration", e);
            throw new RuntimeException("Failed to load API tools configuration", e);
        }
    }
    
    /**
     * 创建Booking - 从JSON配置自动生成的工具
     * 这是一个示例，展示如何只需2-3行代码就能将JSON配置转换为MCP工具
     */
    @Tool(description = "Create booking for QA3 environment. This tool creates a new booking in the IRIS system using predefined test data.")
    public String createBooking() {
        // 参数会自动使用JSON中配置的defaultValue，无需手动传递
        return genericApiService.executeApiCall("createBooking", new HashMap<>());
    }

    
    /**
     * 更新Shipment的POR - 从JSON配置自动生成的工具
     * 修改指定shipment的Port of Receipt信息
     */
    @Tool(description = "Update the POR (Port of Receipt) for a specific shipment. " +
            "This tool modifies the port of receipt information for a given shipment number and POR ID. " +
            "Example: shipmentNumber='7261242220', porId='738872886233503'")
    public String updateShipmentPOR(String shipmentNumber, String porId) {
        Map<String, String> params = new HashMap<>();
        params.put("shipmentNumber", shipmentNumber);
        params.put("porId", porId);
        return genericApiService.executeApiCall("updateShipmentPOR", params);
    }

    
    /**
     * 验证Shipment信息 - 从JSON配置自动生成的工具
     * 替代原来的 ShipmentService.verifyShipment()
     */
    @Tool(description = "Verify shipment information and check if the POR update was successful. " +
            "This tool retrieves detailed information about a shipment by its number. " +
            "Use this after updating POR to confirm the changes. " +
            "Example: shipmentNumber='7261242220'")
    public String verifyShipment(String shipmentNumber) {
        Map<String, String> params = new HashMap<>();
        params.put("shipmentNumber", shipmentNumber);
        return genericApiService.executeApiCall("verifyShipment", params);
    }
    
    /**
     * 组合操作：更新POR并验证结果 - 从JSON配置自动生成的工具
     * 替代原来的 ShipmentService.updateAndVerifyShipmentPOR()
     */
    @Tool(description = "Update shipment POR and verify the result in one operation. " +
            "This is a composite tool that first updates the POR and then verifies the change was successful. " +
            "Example: shipmentNumber='7261242220', porId='738872886233503'")
    public String updateAndVerifyShipmentPOR(String shipmentNumber, String porId) {
        StringBuilder result = new StringBuilder();

        result.append("=== Step 1: Updating POR ===\n");
        String updateResult = updateShipmentPOR(shipmentNumber, porId);
        result.append(updateResult).append("\n\n");

        if (updateResult.contains("✅")) {
            result.append("=== Step 2: Verifying Changes ===\n");
            String verifyResult = verifyShipment(shipmentNumber);
            result.append(verifyResult);
        } else {
            result.append("⚠️ Skipping verification due to update failure.");
        }

        return result.toString();
    }
}
