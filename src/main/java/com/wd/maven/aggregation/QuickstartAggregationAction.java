package com.wd.maven.aggregation;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;

public class QuickstartAggregationAction extends AnAction {

    // 使用构造函数设置图标和文本
    public QuickstartAggregationAction() {
        super("Create Maven Aggregation Project",
                "Creates a new Maven multi-module project template",
                IconLoader.getIcon("/icons/create_maven_aggregation.png", QuickstartAggregationAction.class));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

        // 创建对话框
        AggregationProjectDialog dialog = new AggregationProjectDialog(project);
        if (dialog.showAndGet()) {
            // 用户点击了确定
            String groupId = dialog.getGroupId();
            String artifactId = dialog.getArtifactId();
            String version = dialog.getVersion();
            String[] modules = dialog.getModules();
            String javaVersion = dialog.getJavaVersion();
            ArchitectureType architectureType = dialog.getArchitectureType();
            String domainName = dialog.getDomainName();

            // 生成项目
            AggregationProjectGenerator generator = new AggregationProjectGenerator();
            generator.generateProject(project, groupId, artifactId, version, modules, javaVersion, dialog.isAddDependencies(), architectureType, domainName);

            // 刷新项目视图
            refreshProjectView(project);
        }
    }

    private void refreshProjectView(Project project) {
        // 使用更高效的刷新方式，避免全量刷新
        VirtualFile baseDir = ProjectUtil.guessProjectDir(project);
        if (baseDir != null) {
            // 只刷新根目录，让IDEA自动检测Maven项目
            baseDir.refresh(false, false);
            
            // 延迟触发项目结构刷新，让IDEA有时间检测到新的Maven项目
            com.intellij.openapi.application.ApplicationManager.getApplication()
                .invokeLater(() -> {
                    // 触发项目结构刷新
                    baseDir.refresh(false, true);
                });
        }
    }
}
