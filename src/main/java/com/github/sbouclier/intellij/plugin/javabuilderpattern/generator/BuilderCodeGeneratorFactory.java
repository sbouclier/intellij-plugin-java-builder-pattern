package com.github.sbouclier.intellij.plugin.javabuilderpattern.generator;

import com.github.sbouclier.intellij.plugin.javabuilderpattern.model.BuilderParameter;
import com.github.sbouclier.intellij.plugin.javabuilderpattern.model.BuilderType;
import com.intellij.psi.PsiClass;

import java.util.List;

/**
 * Factory to get Builder generator.
 */
public class BuilderCodeGeneratorFactory {
    public static AbstractBuilderCodeGenerator get(BuilderType builderType, PsiClass targetClass, List<BuilderParameter> parameters) {
        if(builderType == BuilderType.CLASSIC) {
            return new BuilderClassicCodeGenerator(targetClass, parameters);
        } else {
            return new BuilderFluentCodeGenerator(targetClass, parameters);
        }
    }
}
