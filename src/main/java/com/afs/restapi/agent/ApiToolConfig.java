package com.afs.restapi.agent;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 * API工具配置模型
 * 用于从JSON文件加载API配置，自动生成MCP工具
 */
public class ApiToolConfig {
    
    @JsonProperty("apiTools")
    private List<ApiTool> apiTools;

    public List<ApiTool> getApiTools() {
        return apiTools;
    }

    public void setApiTools(List<ApiTool> apiTools) {
        this.apiTools = apiTools;
    }

    /**
     * API工具定义
     */
    public static class ApiTool {
        private String name;
        private String description;
        private String method;
        private String url;
        private Map<String, String> headers;
        private List<Parameter> parameters;
        private Object requestBody;
        private String successMessage;
        private String errorMessage;

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

        public void setHeaders(Map<String, String> headers) {
            this.headers = headers;
        }

        public List<Parameter> getParameters() {
            return parameters;
        }

        public void setParameters(List<Parameter> parameters) {
            this.parameters = parameters;
        }

        public Object getRequestBody() {
            return requestBody;
        }

        public void setRequestBody(Object requestBody) {
            this.requestBody = requestBody;
        }

        public String getSuccessMessage() {
            return successMessage;
        }

        public void setSuccessMessage(String successMessage) {
            this.successMessage = successMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }

    /**
     * 参数定义
     */
    public static class Parameter {
        private String name;
        private String type;
        private String location; // path, query, body, header
        private String description;
        private boolean required;
        private String defaultValue;

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public boolean isRequired() {
            return required;
        }

        public void setRequired(boolean required) {
            this.required = required;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }
    }
}
