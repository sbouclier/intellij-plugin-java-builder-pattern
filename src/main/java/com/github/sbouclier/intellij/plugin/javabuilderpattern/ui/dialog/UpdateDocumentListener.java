package com.github.sbouclier.intellij.plugin.javabuilderpattern.ui.dialog;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Update document listener to simplify update.
 */
@FunctionalInterface
public interface UpdateDocumentListener extends DocumentListener {
    void update(DocumentEvent e);

    @Override
    default void insertUpdate(DocumentEvent e) {
        update(e);
    }

    @Override
    default void removeUpdate(DocumentEvent e) {
        update(e);
    }

    @Override
    default void changedUpdate(DocumentEvent e) {
        update(e);
    }
}
