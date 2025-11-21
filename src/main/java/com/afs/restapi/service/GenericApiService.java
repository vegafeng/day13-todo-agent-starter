package com.afs.restapi.service;

import com.afs.restapi.agent.ApiToolConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 通用API服务
 * 
 * 从JSON配置文件加载API定义，动态执行API调用
 * 只需添加JSON配置即可将任何API转换为可调用的服务
 */
@Service
public class GenericApiService {

    private static final Logger logger = LoggerFactory.getLogger(GenericApiService.class);
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private ApiToolConfig apiToolConfig;

    public GenericApiService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        logger.info("✅ GenericApiService Bean created successfully");
    }

    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource("api-tools-config.json");
            apiToolConfig = objectMapper.readValue(resource.getInputStream(), ApiToolConfig.class);
            logger.info("✅ Loaded {} API tools from configuration", 
                    apiToolConfig.getApiTools().size());
        } catch (IOException e) {
            logger.error("❌ Failed to load API tools configuration", e);
            throw new RuntimeException("Failed to load API tools configuration", e);
        }
    }

    /**
     * 执行API调用
     * 
     * @param toolName 工具名称（对应JSON配置中的name）
     * @param params 参数Map，key为参数名，value为参数值
     * @return API调用结果
     */
    public String executeApiCall(String toolName, Map<String, String> params) {
        try {
            // 查找工具配置
            ApiToolConfig.ApiTool tool = findTool(toolName);
            if (tool == null) {
                return String.format("❌ Tool '%s' not found in configuration", toolName);
            }

            logger.info("Executing API tool: {}", toolName);

            // 构建URL
            String url = buildUrl(tool, params);
            
            // 设置请求头
            HttpHeaders headers = buildHeaders(tool);
            
            // 构建请求体
            Object body = buildRequestBody(tool, params);
            
            // 创建请求实体
            HttpEntity<Object> requestEntity = new HttpEntity<>(body, headers);
            
            // 发送请求
            HttpMethod method = HttpMethod.valueOf(tool.getMethod());
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    method,
                    requestEntity,
                    String.class
            );

            logger.info("API call response: status={}", response.getStatusCode());

            // 处理响应
            if (response.getStatusCode().is2xxSuccessful()) {
                String message = replaceParameters(tool.getSuccessMessage(), params);
                return String.format("%s. Response: %s", message, response.getBody());
            } else {
                String message = replaceParameters(tool.getErrorMessage(), params);
                return String.format("%s. Status: %s, Response: %s",
                        message, response.getStatusCode(), response.getBody());
            }

        } catch (Exception e) {
            logger.error("Error executing API call for tool: {}", toolName, e);
            return String.format("❌ Error executing API call: %s", e.getMessage());
        }
    }

    /**
     * 查找工具配置
     */
    private ApiToolConfig.ApiTool findTool(String toolName) {
        return apiToolConfig.getApiTools().stream()
                .filter(tool -> tool.getName().equals(toolName))
                .findFirst()
                .orElse(null);
    }

    /**
     * 构建URL，替换路径参数和添加查询参数
     */
    private String buildUrl(ApiToolConfig.ApiTool tool, Map<String, String> params) {
        String url = tool.getUrl();
        UriComponentsBuilder builder = null;

        for (ApiToolConfig.Parameter param : tool.getParameters()) {
            String value = params.getOrDefault(param.getName(), param.getDefaultValue());
            
            if (value != null) {
                if ("path".equals(param.getLocation())) {
                    // 替换路径参数
                    url = url.replace("{" + param.getName() + "}", value);
                } else if ("query".equals(param.getLocation())) {
                    // 添加查询参数
                    if (builder == null) {
                        builder = UriComponentsBuilder.fromUriString(url);
                    }
                    builder.queryParam(param.getName(), value);
                }
            }
        }

        return builder != null ? builder.toUriString() : url;
    }

    /**
     * 构建请求头
     */
    private HttpHeaders buildHeaders(ApiToolConfig.ApiTool tool) {
        HttpHeaders headers = new HttpHeaders();
        if (tool.getHeaders() != null) {
            tool.getHeaders().forEach((key, value) -> {
                if ("Content-Type".equalsIgnoreCase(key)) {
                    headers.setContentType(MediaType.parseMediaType(value));
                } else {
                    headers.set(key, value);
                }
            });
        }
        return headers;
    }

    /**
     * 构建请求体
     */
    private Object buildRequestBody(ApiToolConfig.ApiTool tool, Map<String, String> params) {
        if (tool.getRequestBody() == null) {
            return new HashMap<>();
        }
        
        // 如果requestBody是String类型，尝试替换参数
        if (tool.getRequestBody() instanceof String) {
            return replaceParameters((String) tool.getRequestBody(), params);
        }
        
        return tool.getRequestBody();
    }

    /**
     * 替换消息中的参数占位符
     */
    private String replaceParameters(String message, Map<String, String> params) {
        if (message == null) {
            return "";
        }
        
        String result = message;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return result;
    }

    /**
     * 获取所有可用的工具名称
     */
    public String[] getAvailableTools() {
        return apiToolConfig.getApiTools().stream()
                .map(ApiToolConfig.ApiTool::getName)
                .toArray(String[]::new);
    }

    /**
     * 获取工具的描述信息
     */
    public String getToolDescription(String toolName) {
        ApiToolConfig.ApiTool tool = findTool(toolName);
        return tool != null ? tool.getDescription() : "Tool not found";
    }
}
