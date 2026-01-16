package com.wd.maven.aggregation;

import com.intellij.ide.util.projectWizard.WebProjectTemplate;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.wd.maven.aggregation.AggregationProjectGenerator;
import com.wd.maven.aggregation.ArchitectureType;
import com.wd.maven.aggregation.MavenAggregationSettings;
import com.wd.maven.aggregation.MavenAggregationSettingsPanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class MavenAggregationProjectGenerator extends WebProjectTemplate<MavenAggregationSettings> {

    @NotNull
    @Override
    public String getName() {
        return "Maven Aggregation Project";
    }

    @Override
    public String getDescription() {
        return "创建标准的Maven多模块项目结构，包含父POM和子模块";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return null; // 可以返回一个自定义图标
    }

    @NotNull
    public MavenAggregationSettings createSettings() {
        return new MavenAggregationSettings();
    }

    @Nullable
    public JComponent getSettingsPanel(MavenAggregationSettings settings) {
        return new MavenAggregationSettingsPanel(settings).getPanel();
    }

    @Override
    public void generateProject(@NotNull Project project, @NotNull VirtualFile baseDir, @NotNull MavenAggregationSettings settings, @NotNull Module module) {
        AggregationProjectGenerator generator = new AggregationProjectGenerator();
        generator.generateProject(
                project,
                settings.getGroupId(),
                settings.getArtifactId(),
                settings.getVersion(),
                settings.getModules(),
                settings.getJavaVersion(),
                settings.isAddDependencies(),
                settings.getArchitectureType(),
                settings.getDomainName()
        );
    }
    
    /**
     * 生成项目，支持单模块/多模块切换
     * @param project 项目对象
     * @param groupId 组ID
     * @param artifactId 构件ID
     * @param version 版本号
     * @param modules 模块列表
     * @param javaVersion Java版本
     * @param addDependencies 是否添加依赖
     * @param architectureType 架构类型
     * @param domainName 领域名称（单模块时使用）
     */
    public void generateProject(@NotNull Project project, @NotNull String groupId, @NotNull String artifactId, @NotNull String version, @NotNull String[] modules, 
                              @NotNull String javaVersion, boolean addDependencies, ArchitectureType architectureType, String domainName) {
        // 调用核心生成器，支持单模块/多模块模式
        AggregationProjectGenerator generator = new AggregationProjectGenerator();
        generator.generateProject(project, groupId, artifactId, version, modules, javaVersion, addDependencies, architectureType, domainName);
    }
}
