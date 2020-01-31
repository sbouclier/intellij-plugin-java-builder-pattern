package com.github.sbouclier.intellij.plugin.javabuilderpattern.model;

import com.intellij.psi.PsiField;

/**
 * Builder parameter which represents a class field with options to build it.
 */
public class BuilderParameter {
    private final PsiField field;
    private String setterName;
    private boolean mandatory = false;
    private boolean constructor = false;

    public BuilderParameter(PsiField field, String setterName, boolean mandatory, boolean constructor) {
        this(field);
        this.setterName = setterName;
        this.mandatory = mandatory;
        this.constructor = constructor;
    }

    public BuilderParameter(PsiField field) {
        this.field = field;
    }

    public PsiField getField() {
        return field;
    }

    public String getParameterName() {
        return this.field.getName();
    }

    public String getParameterType() {
        return this.field.getType().getPresentableText();
    }

    public String getSetterName() {
        return setterName;
    }

    public void setSetterName(String setterName) {
        this.setterName = setterName;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public boolean isConstructor() {
        return constructor;
    }

    public void setConstructor(boolean constructor) {
        this.constructor = constructor;
    }

    @Override
    public String toString() {
        return "BuilderParameter{" +
                "field=" + field +
                ", setterName='" + setterName + '\'' +
                ", mandatory=" + mandatory +
                ", constructor=" + constructor +
                '}';
    }
}
