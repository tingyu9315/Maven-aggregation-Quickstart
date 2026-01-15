package com.wd.maven.aggregation;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DebounceDocumentListener implements DocumentListener {
    private final Timer timer;
    private final Runnable action;
    private final int delay;

    public DebounceDocumentListener(Runnable action, int delay) {
        this.action = action;
        this.delay = delay;
        this.timer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DebounceDocumentListener.this.action.run();
            }
        });
        this.timer.setRepeats(false);
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        timer.restart();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        timer.restart();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        timer.restart();
    }
}