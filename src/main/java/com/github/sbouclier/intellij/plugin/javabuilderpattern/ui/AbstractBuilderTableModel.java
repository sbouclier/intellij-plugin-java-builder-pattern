package com.github.sbouclier.intellij.plugin.javabuilderpattern.ui;

import com.github.sbouclier.intellij.plugin.javabuilderpattern.model.BuilderParameter;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract table model for parameters to Builder dialog.
 */
public abstract class AbstractBuilderTableModel extends AbstractTableModel {

    protected static final String HEADER_PARAMETER_NAME = "Parameter name";
    protected static final String HEADER_SETTER_NAME = "Setter name";
    protected static final String HEADER_CONSTRUCTOR_NAME = "Constructor";
    protected static final String HEADER_MANDATORY_NAME = "Mandatory";

    protected List<BuilderParameter> parameters = new ArrayList<>();

    /**
     * Table headers.
     * @return headers
     */
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
