package com.wd.maven.aggregation;

public class MavenAggregationSettings {
    private String groupId = "com.example";
    private String artifactId = "demo";
    private String version = "1.0-SNAPSHOT";
    private String javaVersion = "8";
    private String[] modules = {"api", "common", "mapper", "pojo", "service"};
    private boolean addDependencies = true; // 默认添加依赖

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getJavaVersion() {
        return javaVersion;
    }

    public void setJavaVersion(String javaVersion) {
        this.javaVersion = javaVersion;
    }

    public String[] getModules() {
        return modules;
    }

    public void setModules(String[] modules) {
        this.modules = modules;
    }
    
    public boolean isAddDependencies() {
        return addDependencies;
    }
    
    public void setAddDependencies(boolean addDependencies) {
        this.addDependencies = addDependencies;
    }



    public MavenAggregationSettings() { }
}

