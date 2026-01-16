package com.wd.maven.aggregation;

import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MavenAggregationSettingsPanel {
    private JPanel mainPanel;
    private JBTextField groupIdField;
    private JBTextField artifactIdField;
    private JBTextField versionField;
    
    // 架构选择
    private JRadioButton multiModuleRadioButton;
    private JRadioButton singleDddRadioButton;
    private JRadioButton singleMvcRadioButton;
    
    // 领域名称
    private JBTextField domainNameField;
    private JBLabel domainNameLabel;
    
    // 模块选择相关组件（用于显示/隐藏）
    private JBLabel predefinedModulesLabel;
    private JPanel predefinedModulesPanel;
    private JBLabel customModulesLabel;
    private JBTextField modulesField; // 自定义模块输入框

    // 预定义模块复选框
    private JBCheckBox apiCheckBox;
    private JBCheckBox commonCheckBox;
    private JBCheckBox mapperCheckBox;
    private JBCheckBox pojoCheckBox;
    private JBCheckBox serviceCheckBox;
    
    private JBCheckBox addDependenciesCheckBox;
    private JComboBox<String> javaVersionComboBox;
    
    private final MavenAggregationSettings settings;
    
    public MavenAggregationSettingsPanel(MavenAggregationSettings settings) {
        this.settings = settings;
        createUI();
        initValues();
    }
    
    private void createUI() {
        mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);

        // 1. Group ID
        c.gridx = 0; c.gridy = 0;
        mainPanel.add(new JBLabel("Group ID:"), c);
        c.gridx = 1; c.weightx = 1.0;
        groupIdField = new JBTextField();
        mainPanel.add(groupIdField, c);

        // 2. Artifact ID
        c.gridx = 0; c.gridy = 1; c.weightx = 0.0;
        mainPanel.add(new JBLabel("Artifact ID:"), c);
        c.gridx = 1; c.weightx = 1.0;
        artifactIdField = new JBTextField();
        mainPanel.add(artifactIdField, c);

        // 3. Version
        c.gridx = 0; c.gridy = 2; c.weightx = 0.0;
        mainPanel.add(new JBLabel("Version:"), c);
        c.gridx = 1; c.weightx = 1.0;
        versionField = new JBTextField();
        mainPanel.add(versionField, c);

        // 4. Java Version
        c.gridx = 0; c.gridy = 3; c.weightx = 0.0;
        mainPanel.add(new JBLabel("Java Version:"), c);
        c.gridx = 1; c.weightx = 1.0;
        javaVersionComboBox = new JComboBox<>(new String[]{"8", "11", "17", "21"});
        mainPanel.add(javaVersionComboBox, c);
        
        // 5. 架构类型选择
        c.gridx = 0; c.gridy = 4; c.weightx = 0.0;
        mainPanel.add(new JBLabel("架构类型:"), c);
        
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        ButtonGroup group = new ButtonGroup();
        multiModuleRadioButton = new JRadioButton("多模块聚合", true);
        singleDddRadioButton = new JRadioButton("单模块DDD", false);
        singleMvcRadioButton = new JRadioButton("单模块MVC", false);
        
        group.add(multiModuleRadioButton);
        group.add(singleDddRadioButton);
        group.add(singleMvcRadioButton);
        
        typePanel.add(multiModuleRadioButton);
        typePanel.add(singleDddRadioButton);
        typePanel.add(singleMvcRadioButton);
        
        c.gridx = 1; c.weightx = 1.0;
        mainPanel.add(typePanel, c);

        // 6. 领域名称 (DDD专用)
        c.gridx = 0; c.gridy = 5; c.weightx = 0.0;
        domainNameLabel = new JBLabel("领域名称:");
        mainPanel.add(domainNameLabel, c);
        
        c.gridx = 1; c.weightx = 1.0;
        domainNameField = new JBTextField();
        mainPanel.add(domainNameField, c);

        // 7. 预定义模块 (多模块专用)
        c.gridx = 0; c.gridy = 6; c.weightx = 0.0;
        predefinedModulesLabel = new JBLabel("预定义模块:");
        mainPanel.add(predefinedModulesLabel, c);
        
        predefinedModulesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        apiCheckBox = new JBCheckBox("api", true);
        commonCheckBox = new JBCheckBox("common", true);
        mapperCheckBox = new JBCheckBox("mapper", true);
        pojoCheckBox = new JBCheckBox("pojo", true);
        serviceCheckBox = new JBCheckBox("service", true);
        
        predefinedModulesPanel.add(apiCheckBox);
        predefinedModulesPanel.add(commonCheckBox);
        predefinedModulesPanel.add(mapperCheckBox);
        predefinedModulesPanel.add(pojoCheckBox);
        predefinedModulesPanel.add(serviceCheckBox);
        
        c.gridx = 1; c.weightx = 1.0;
        mainPanel.add(predefinedModulesPanel, c);

        // 8. 自定义模块 (多模块专用)
        c.gridx = 0; c.gridy = 7; c.weightx = 0.0;
        customModulesLabel = new JBLabel("自定义模块 (逗号分隔):");
        mainPanel.add(customModulesLabel, c);
        
        c.gridx = 1; c.weightx = 1.0;
        modulesField = new JBTextField();
        mainPanel.add(modulesField, c);
        
        // 9. 添加常用依赖选项
        c.gridx = 0; c.gridy = 8; c.weightx = 0.0;
        mainPanel.add(new JBLabel("添加常用依赖:"), c);
        
        c.gridx = 1; c.weightx = 1.0;
        addDependenciesCheckBox = new JBCheckBox("是");
        mainPanel.add(addDependenciesCheckBox, c);
        
        // 添加基础监听器
        setupBasicListeners();
        
        // 添加架构切换监听器
        ItemListener typeListener = e -> updateVisibility();
        multiModuleRadioButton.addItemListener(typeListener);
        singleDddRadioButton.addItemListener(typeListener);
        singleMvcRadioButton.addItemListener(typeListener);
        
        // 添加领域名称监听器
        domainNameField.getDocument().addDocumentListener(new DebounceDocumentListener(() -> {
            settings.setDomainName(domainNameField.getText().trim());
        }, 300));
    }
    
    private void setupBasicListeners() {
        groupIdField.getDocument().addDocumentListener(new DebounceDocumentListener(() -> {
            settings.setGroupId(groupIdField.getText().trim());
        }, 300));

        artifactIdField.getDocument().addDocumentListener(new DebounceDocumentListener(() -> {
            settings.setArtifactId(artifactIdField.getText().trim());
        }, 300));

        versionField.getDocument().addDocumentListener(new DebounceDocumentListener(() -> {
            settings.setVersion(versionField.getText().trim());
        }, 300));

        javaVersionComboBox.addActionListener(e -> {
            String selectedVersion = (String) javaVersionComboBox.getSelectedItem();
            if (isValidJavaVersion(selectedVersion)) {
                settings.setJavaVersion(selectedVersion);
            } else {
                settings.setJavaVersion("17");
                UIUtil.invokeLaterIfNeeded(() -> {
                    javaVersionComboBox.setSelectedItem("17");
                    JOptionPane.showMessageDialog(mainPanel,
                            "不支持的 Java 版本：" + selectedVersion + "，已自动切换为 Java 17。",
                            "警告",
                            JOptionPane.WARNING_MESSAGE);
                });
            }
        });

        modulesField.getDocument().addDocumentListener(new DebounceDocumentListener(this::updateModules, 300));
        addDependenciesCheckBox.addActionListener(e -> settings.setAddDependencies(addDependenciesCheckBox.isSelected()));

        ItemListener moduleListener = e -> updateModules();
        apiCheckBox.addItemListener(moduleListener);
        commonCheckBox.addItemListener(moduleListener);
        mapperCheckBox.addItemListener(moduleListener);
        pojoCheckBox.addItemListener(moduleListener);
        serviceCheckBox.addItemListener(moduleListener);
    }
    
    private void updateVisibility() {
        ArchitectureType type;
        if (singleDddRadioButton.isSelected()) {
            type = ArchitectureType.SINGLE_MODULE_DDD;
        } else if (singleMvcRadioButton.isSelected()) {
            type = ArchitectureType.SINGLE_MODULE_MVC;
        } else {
            type = ArchitectureType.MULTI_MODULE;
        }
        settings.setArchitectureType(type);
        
        boolean isMulti = type == ArchitectureType.MULTI_MODULE;
        boolean isDdd = type == ArchitectureType.SINGLE_MODULE_DDD;
        
        // 控制领域名称显示
        domainNameLabel.setVisible(isDdd);
        domainNameField.setVisible(isDdd);
        
        // 控制模块选择显示
        predefinedModulesLabel.setVisible(isMulti);
        predefinedModulesPanel.setVisible(isMulti);
        customModulesLabel.setVisible(isMulti);
        modulesField.setVisible(isMulti);
    }

    private boolean isValidJavaVersion(String version) {
        return Arrays.asList("8", "11", "17", "21").contains(version);
    }
    
    private void initValues() {
        groupIdField.setText(settings.getGroupId());
        artifactIdField.setText(settings.getArtifactId());
        versionField.setText(settings.getVersion());
        javaVersionComboBox.setSelectedItem(settings.getJavaVersion());
        addDependenciesCheckBox.setSelected(settings.isAddDependencies());
        domainNameField.setText(settings.getDomainName());
        
        // 架构类型回显
        switch (settings.getArchitectureType()) {
            case SINGLE_MODULE_DDD:
                singleDddRadioButton.setSelected(true);
                break;
            case SINGLE_MODULE_MVC:
                singleMvcRadioButton.setSelected(true);
                break;
            default:
                multiModuleRadioButton.setSelected(true);
                break;
        }
        
        // 触发一次可见性更新
        updateVisibility();
        
        // 设置模块选择状态
        String[] modules = settings.getModules();
        for (String module : modules) {
            switch (module) {
                case "api": apiCheckBox.setSelected(true); break;
                case "common": commonCheckBox.setSelected(true); break;
                case "mapper": mapperCheckBox.setSelected(true); break;
                case "pojo": pojoCheckBox.setSelected(true); break;
                case "service": serviceCheckBox.setSelected(true); break;
                default:
                    if (modulesField.getText().isEmpty()) {
                        modulesField.setText(module);
                    } else {
                        modulesField.setText(modulesField.getText() + ", " + module);
                    }
            }
        }
    }
    
    private void updateModules() {
        List<String> modulesList = new ArrayList<>();
        
        if (apiCheckBox.isSelected()) modulesList.add("api");
        if (commonCheckBox.isSelected()) modulesList.add("common");
        if (mapperCheckBox.isSelected()) modulesList.add("mapper");
        if (pojoCheckBox.isSelected()) modulesList.add("pojo");
        if (serviceCheckBox.isSelected()) modulesList.add("service");
        
        String customModules = modulesField.getText().trim();
        if (!customModules.isEmpty()) {
            modulesList.addAll(Arrays.asList(customModules.split("\\s*,\\s*")));
        }
        
        settings.setModules(modulesList.toArray(new String[0]));
    }
    
    public JPanel getPanel() {
        return mainPanel;
    }
}
