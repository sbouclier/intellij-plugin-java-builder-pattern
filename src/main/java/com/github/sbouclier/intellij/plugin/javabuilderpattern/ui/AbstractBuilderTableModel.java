package com.github.sbouclier.intellij.plugin.javabuilderpattern.ui;

import com.github.sbouclier.intellij.plugin.javabuilderpattern.model.BuilderParameter;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract table model for parameters to Builder dialog.
 */
public abstract class AbstractBuilderTableModel extends AbstractTableModel {
    protected List<BuilderParameter> parameters = new ArrayList<>();

    abstract String[] getHeaders();

    @Override
    public int getRowCount() {
        return parameters.size();
    }

    @Override
    public int getColumnCount() {
        return getHeaders().length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return getHeaders()[columnIndex];
    }

    public void setParameters(List<BuilderParameter> params) {
        parameters = params;
    }

    public List<BuilderParameter> getParameters() {
        return parameters;
    }
}
