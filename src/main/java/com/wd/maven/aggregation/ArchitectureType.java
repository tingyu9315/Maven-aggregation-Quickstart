package com.wd.maven.aggregation;

public enum ArchitectureType {
    MULTI_MODULE("多模块聚合架构"),
    SINGLE_MODULE_DDD("单模块DDD架构"),
    SINGLE_MODULE_MVC("单模块MVC架构");

    private final String description;

    ArchitectureType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
