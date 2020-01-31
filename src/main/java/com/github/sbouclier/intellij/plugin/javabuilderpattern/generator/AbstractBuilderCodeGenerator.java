package com.github.sbouclier.intellij.plugin.javabuilderpattern.generator;

import com.github.sbouclier.intellij.plugin.javabuilderpattern.model.BuilderParameter;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;

import java.util.List;

/**
 * Abstract Builder code generator.
 */
public abstract class AbstractBuilderCodeGenerator {
    protected final PsiClass targetClass;
    protected final List<BuilderParameter> parameters;
    protected final PsiElementFactory elementFactory;

    public AbstractBuilderCodeGenerator(PsiClass targetClass, List<BuilderParameter> parameters) {
        this.targetClass = targetClass;
        this.parameters = parameters;
        this.elementFactory = JavaPsiFacade.getElementFactory(targetClass.getProject());
    }

    /**
     * Generate Builder pattern.
     */
    public abstract void generate();
}
