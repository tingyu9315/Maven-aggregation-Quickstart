package com.wd.maven.aggregation;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AggregationProjectDialog extends DialogWrapper {

    private JBTextField groupIdField;
    private JBTextField artifactIdField;
    private JBTextField versionField;
    private JBTextField modulesField;
    private JBTextField domainNameField; // 用于单模块DDD架构
    private JComboBox<String> javaVersionComboBox;
    
    // 单模块/多模块选择
    private JRadioButton singleModuleRadioButton;
    private JRadioButton multiModuleRadioButton;
    private ButtonGroup moduleTypeButtonGroup;
    
    // 预定义模块复选框
    private JBCheckBox apiCheckBox;
    private JBCheckBox commonCheckBox;
    private JBCheckBox mapperCheckBox;
    private JBCheckBox pojoCheckBox;
    private JBCheckBox serviceCheckBox;
    private JBCheckBox addDependenciesCheckBox;
    
    // 模块相关面板，用于动态显示/隐藏
    private JPanel modulesPanel;
    private JPanel modulesFieldsPanel;

    public AggregationProjectDialog(Project project) {
        super(project);
        setTitle("创建Maven聚合项目");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);

        // 单模块/多模块选择
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.0;
        panel.add(new JLabel("项目类型:"), c);

        JPanel moduleTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        singleModuleRadioButton = new JRadioButton("单模块DDD架构", false);
        multiModuleRadioButton = new JRadioButton("多模块架构", true);
        moduleTypeButtonGroup = new ButtonGroup();
        moduleTypeButtonGroup.add(singleModuleRadioButton);
        moduleTypeButtonGroup.add(multiModuleRadioButton);
        moduleTypePanel.add(singleModuleRadioButton);
        moduleTypePanel.add(multiModuleRadioButton);

        c.gridx = 1;
        c.weightx = 1.0;
        panel.add(moduleTypePanel, c);

        // 领域名称（用于单模块DDD架构）
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0.0;
        panel.add(new JLabel("领域名称:"), c);

        c.gridx = 1;
        c.weightx = 1.0;
        domainNameField = new JBTextField("order");
        panel.add(domainNameField, c);

        // Group ID
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 0.0;
        panel.add(new JBLabel("Group ID:"), c);

        c.gridx = 1;
        c.weightx = 1.0;
        groupIdField = new JBTextField("com.example");
        panel.add(groupIdField, c);

        // Artifact ID
        c.gridx = 0;
        c.gridy = 3;
        c.weightx = 0.0;
        panel.add(new JBLabel("Artifact ID:"), c);

        c.gridx = 1;
        c.weightx = 1.0;
        artifactIdField = new JBTextField("demo");
        panel.add(artifactIdField, c);

        // Version
        c.gridx = 0;
        c.gridy = 4;
        c.weightx = 0.0;
        panel.add(new JBLabel("Version:"), c);

        c.gridx = 1;
        c.weightx = 1.0;
        versionField = new JBTextField("1.0-SNAPSHOT");
        panel.add(versionField, c);

        // Java Version
        c.gridx = 0;
        c.gridy = 5;
        c.weightx = 0.0;
        panel.add(new JBLabel("Java Version:"), c);

        c.gridx = 1;
        c.weightx = 1.0;
        javaVersionComboBox = new JComboBox<>(new String[]{"8", "11", "17", "21"});
        panel.add(javaVersionComboBox, c);

        // 创建模块相关面板
        this.modulesPanel = new JPanel(new GridBagLayout());
        GridBagConstraints mc = new GridBagConstraints();
        mc.fill = GridBagConstraints.HORIZONTAL;
        mc.insets = new Insets(5, 5, 5, 5);

        // 预定义模块
        mc.gridx = 0;
        mc.gridy = 0;
        mc.weightx = 0.0;
        this.modulesPanel.add(new JBLabel("预定义模块:"), mc);

        // 模块复选框面板
        this.modulesFieldsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        apiCheckBox = new JBCheckBox("api", true);
        commonCheckBox = new JBCheckBox("common", true);
        mapperCheckBox = new JBCheckBox("mapper", true);
        pojoCheckBox = new JBCheckBox("pojo", true);
        serviceCheckBox = new JBCheckBox("service", true);
        
        this.modulesFieldsPanel.add(apiCheckBox);
        this.modulesFieldsPanel.add(commonCheckBox);
        this.modulesFieldsPanel.add(mapperCheckBox);
        this.modulesFieldsPanel.add(pojoCheckBox);
        this.modulesFieldsPanel.add(serviceCheckBox);

        mc.gridx = 1;
        mc.weightx = 1.0;
        this.modulesPanel.add(this.modulesFieldsPanel, mc);

        // 自定义模块
        mc.gridx = 0;
        mc.gridy = 1;
        mc.weightx = 0.0;
        this.modulesPanel.add(new JBLabel("自定义模块 (逗号分隔):"), mc);

        mc.gridx = 1;
        mc.weightx = 1.0;
        modulesField = new JBTextField();
        this.modulesPanel.add(modulesField, mc);

        // 将模块面板添加到主面板
        c.gridx = 0;
        c.gridy = 6;
        c.weightx = 0.0;
        c.gridwidth = 1;
        panel.add(new JBLabel(), c); // 占位

        c.gridx = 1;
        c.weightx = 1.0;
        c.gridwidth = 1;
        panel.add(this.modulesPanel, c);
        
        // 添加常用依赖选项
        c.gridx = 0;
        c.gridy = 7;
        c.weightx = 0.0;
        panel.add(new JBLabel("添加常用依赖:"), c);
        
        c.gridx = 1;
        c.weightx = 1.0;
        addDependenciesCheckBox = new JBCheckBox("常用依赖 (Lombok, Hutool, SLF4J, Logback)");
        addDependenciesCheckBox.setSelected(true); // 默认选中
        panel.add(addDependenciesCheckBox, c);

        // 添加选择监听器
        singleModuleRadioButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                updateUIComponentsVisibility();
            }
        });

        multiModuleRadioButton.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                updateUIComponentsVisibility();
            }
        });

        // 初始更新UI组件可见性
        updateUIComponentsVisibility();

        return panel;
    }

    /**
     * 根据选择的项目类型更新UI组件的可见性
     */
    private void updateUIComponentsVisibility() {
        boolean isMultiModule = multiModuleRadioButton.isSelected();
        domainNameField.setVisible(!isMultiModule); // 单模块时显示领域名称
        modulesPanel.setVisible(isMultiModule); // 多模块时显示模块选择
    }

    @Override
    protected ValidationInfo doValidate() {
        if (groupIdField.getText().trim().isEmpty()) {
            return new ValidationInfo("Group ID不能为空", groupIdField);
        }
        if (artifactIdField.getText().trim().isEmpty()) {
            return new ValidationInfo("Artifact ID不能为空", artifactIdField);
        }
        if (versionField.getText().trim().isEmpty()) {
            return new ValidationInfo("Version不能为空", versionField);
        }
        // 单模块时验证领域名称
        if (singleModuleRadioButton.isSelected() && domainNameField.getText().trim().isEmpty()) {
            return new ValidationInfo("领域名称不能为空", domainNameField);
        }
        return null;
    }

    public String getGroupId() {
        return groupIdField.getText().trim();
    }

    public String getArtifactId() {
        return artifactIdField.getText().trim();
    }

    public String getVersion() {
        return versionField.getText().trim();
    }

    public String getJavaVersion() {
        return (String) javaVersionComboBox.getSelectedItem();
    }

    public String[] getModules() {
        List<String> modulesList = new ArrayList<>();
        
        // 只有在多模块模式下才收集模块
        if (multiModuleRadioButton.isSelected()) {
            // 添加选中的预定义模块
            if (apiCheckBox.isSelected()) modulesList.add("api");
            if (commonCheckBox.isSelected()) modulesList.add("common");
            if (mapperCheckBox.isSelected()) modulesList.add("mapper");
            if (pojoCheckBox.isSelected()) modulesList.add("pojo");
            if (serviceCheckBox.isSelected()) modulesList.add("service");
            
            // 添加自定义模块
            String customModules = modulesField.getText().trim();
            if (!customModules.isEmpty()) {
                modulesList.addAll(Arrays.asList(customModules.split("\\s*,\\s*")));
            }
        }
        
        return modulesList.toArray(new String[0]);
    }
    
    /**
     * 是否为单模块模式
     */
    public boolean isSingleModule() {
        return singleModuleRadioButton.isSelected();
    }
    
    /**
     * 获取领域名称（用于单模块DDD架构）
     */
    public String getDomainName() {
        return domainNameField.getText().trim();
    }
    
    /**
     * 是否添加常用依赖
     */
    public boolean isAddDependencies() {
        return addDependenciesCheckBox.isSelected();
    }
    
    // 重复方法已在原始代码中存在，保留原始实现
    

}