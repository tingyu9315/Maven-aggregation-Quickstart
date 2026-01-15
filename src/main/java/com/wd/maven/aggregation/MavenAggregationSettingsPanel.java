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
    private JBTextField modulesField;
    private JComboBox<String> javaVersionComboBox;
    
    // 预定义模块复选框
    private JBCheckBox apiCheckBox;
    private JBCheckBox commonCheckBox;
    private JBCheckBox mapperCheckBox;
    private JBCheckBox pojoCheckBox;
    private JBCheckBox serviceCheckBox;
    private JBCheckBox addDependenciesCheckBox;
    
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

        // Group ID
        c.gridx = 0;
        c.gridy = 0;
        mainPanel.add(new JBLabel("Group ID:"), c);

        c.gridx = 1;
        c.weightx = 1.0;
        groupIdField = new JBTextField();
        mainPanel.add(groupIdField, c);

        // Artifact ID
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0.0;
        mainPanel.add(new JBLabel("Artifact ID:"), c);

        c.gridx = 1;
        c.weightx = 1.0;
        artifactIdField = new JBTextField();
        mainPanel.add(artifactIdField, c);

        // Version
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 0.0;
        mainPanel.add(new JBLabel("Version:"), c);

        c.gridx = 1;
        c.weightx = 1.0;
        versionField = new JBTextField();
        mainPanel.add(versionField, c);

        // Java Version
        c.gridx = 0;
        c.gridy = 3;
        c.weightx = 0.0;
        mainPanel.add(new JBLabel("Java Version:"), c);

        c.gridx = 1;
        c.weightx = 1.0;
        javaVersionComboBox = new JComboBox<>(new String[]{"8", "11", "17", "21"});
        mainPanel.add(javaVersionComboBox, c);

        // 预定义模块
        c.gridx = 0;
        c.gridy = 4;
        c.weightx = 0.0;
        mainPanel.add(new JBLabel("预定义模块:"), c);

        // 模块复选框面板
        JPanel modulesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        apiCheckBox = new JBCheckBox("api", true);
        commonCheckBox = new JBCheckBox("common", true);
        mapperCheckBox = new JBCheckBox("mapper", true);
        pojoCheckBox = new JBCheckBox("pojo", true);
        serviceCheckBox = new JBCheckBox("service", true);
        
        modulesPanel.add(apiCheckBox);
        modulesPanel.add(commonCheckBox);
        modulesPanel.add(mapperCheckBox);
        modulesPanel.add(pojoCheckBox);
        modulesPanel.add(serviceCheckBox);

        c.gridx = 1;
        c.weightx = 1.0;
        mainPanel.add(modulesPanel, c);

        // 自定义模块
        c.gridx = 0;
        c.gridy = 5;
        c.weightx = 0.0;
        mainPanel.add(new JBLabel("自定义模块 (逗号分隔):"), c);

        c.gridx = 1;
        c.weightx = 1.0;
        modulesField = new JBTextField();
        mainPanel.add(modulesField, c);
        
        // 添加常用依赖选项
        c.gridx = 0;
        c.gridy = 6;
        c.weightx = 0.0;
        mainPanel.add(new JBLabel("添加常用依赖:"), c);
        
        c.gridx = 1;
        c.weightx = 1.0;
        addDependenciesCheckBox = new JBCheckBox("是");
        mainPanel.add(addDependenciesCheckBox, c);
        
        // 添加监听器
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
                // 默认回退到 17
                settings.setJavaVersion("17");
                // 确保在 EDT 中更新 UI
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

        // 模块变更监听器
        ItemListener moduleListener = e -> updateModules();
        apiCheckBox.addItemListener(moduleListener);
        commonCheckBox.addItemListener(moduleListener);
        mapperCheckBox.addItemListener(moduleListener);
        pojoCheckBox.addItemListener(moduleListener);
        serviceCheckBox.addItemListener(moduleListener);
    }

    // 新增辅助方法
    private boolean isValidJavaVersion(String version) {
        return Arrays.asList("8", "11", "17", "21").contains(version);
    }
    
    private void initValues() {
        groupIdField.setText(settings.getGroupId());
        artifactIdField.setText(settings.getArtifactId());
        versionField.setText(settings.getVersion());
        javaVersionComboBox.setSelectedItem(settings.getJavaVersion());
        addDependenciesCheckBox.setSelected(settings.isAddDependencies());
        
        // 设置模块选择状态
        String[] modules = settings.getModules();
        for (String module : modules) {
            switch (module) {
                case "api":
                    apiCheckBox.setSelected(true);
                    break;
                case "common":
                    commonCheckBox.setSelected(true);
                    break;
                case "mapper":
                    mapperCheckBox.setSelected(true);
                    break;
                case "pojo":
                    pojoCheckBox.setSelected(true);
                    break;
                case "service":
                    serviceCheckBox.setSelected(true);
                    break;
                default:
                    // 自定义模块，添加到文本框
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
        
        settings.setModules(modulesList.toArray(new String[0]));
    }
    
    public JPanel getPanel() {
        return mainPanel;
    }
}