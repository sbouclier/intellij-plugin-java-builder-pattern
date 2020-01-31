package com.github.sbouclier.intellij.plugin.javabuilderpattern.generator;

import com.github.sbouclier.intellij.plugin.javabuilderpattern.model.BuilderParameter;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Code generator for classic Builder.
 */
public class BuilderClassicCodeGenerator extends AbstractBuilderCodeGenerator {

    private static final String BUILDER_CLASS_NAME = "Builder";

    private List<BuilderParameter> mandatoryParametersWithConstructor = new ArrayList<>();
    private List<BuilderParameter> mandatoryParametersWithoutConstructor = new ArrayList<>();

    public BuilderClassicCodeGenerator(PsiClass targetClass, List<BuilderParameter> parameters) {
        super(targetClass, parameters);
    }

    @Override
    public void generate() {
        extractParameters();
        WriteCommandAction
                .writeCommandAction(targetClass.getProject())
                .run(this::createBuilder);
    }

    private void createBuilder() {
        targetClass.add(createPrivateConstructorInnerClass());
        targetClass.add(createStaticBuilderInnerClass());
        targetClass.add(createInnerBuilderClass());
    }

    private void extractParameters() {
        this.parameters.forEach(param -> {
            if (param.isConstructor()) {
                mandatoryParametersWithConstructor.add(param);
            } else {
                mandatoryParametersWithoutConstructor.add(param);
            }
        });
    }

    private PsiMethod createPrivateConstructorInnerClass() {
        final StringBuilder sbMethod = new StringBuilder("private ").append(targetClass.getName())
                .append("(").append(BUILDER_CLASS_NAME).append(" builder) {");

        parameters.forEach(param -> {
            StringBuilder sbStatement = new StringBuilder("this.")
                    .append(param.getParameterName())
                    .append(" = builder.")
                    .append(param.getParameterName())
                    .append(";\n");
            sbMethod.append(sbStatement.toString());
        });

        sbMethod.append("}");
        return elementFactory.createMethodFromText(sbMethod.toString(), null);
    }

    private PsiMethod createStaticBuilderInnerClass() {
        List<String> listParamNameWithType = new ArrayList<>();
        List<String> listParamNameWithoutType = new ArrayList<>();
        mandatoryParametersWithConstructor.forEach(param -> {
            listParamNameWithType.add(param.getParameterType() + " " + param.getParameterName());
            listParamNameWithoutType.add(param.getParameterName());
        });

        StringBuilder sbMethod = new StringBuilder("public static ")
                .append(BUILDER_CLASS_NAME)
                .append(" builder(")
                .append(String.join(",", listParamNameWithType))
                .append(") {")
                .append("return new ").append(BUILDER_CLASS_NAME).append("(")
                .append(String.join(",", listParamNameWithoutType))
                .append(");").append("}");
        return elementFactory.createMethodFromText(sbMethod.toString(), null);
    }

    private PsiClass createInnerBuilderClass() {
        PsiClass innerBuilderClass = targetClass.findInnerClassByName(BUILDER_CLASS_NAME, true);
        if (innerBuilderClass == null) {
            // class
            final PsiClass finalInnerBuilderClass = elementFactory.createClass(BUILDER_CLASS_NAME);
            finalInnerBuilderClass.getModifierList().setModifierProperty(PsiModifier.STATIC, true);
            finalInnerBuilderClass.getModifierList().setModifierProperty(PsiModifier.FINAL, true);

            // fields
            parameters.forEach(param -> {
                PsiField field = elementFactory.createField(param.getParameterName(), param.getField().getType());
                if (param.isConstructor()) {
                    field.getModifierList().setModifierProperty(PsiModifier.FINAL, true);
                }
                finalInnerBuilderClass.add(field);
            });

            // constructor
            finalInnerBuilderClass.add(createConstructorInnerBuilderClass());

            // parameter methods
            mandatoryParametersWithoutConstructor.forEach(p -> finalInnerBuilderClass.add(createMethodInnerBuilderClass(p)));

            // build method
            finalInnerBuilderClass.add(createBuildMethodInnerBuilderClass());

            return finalInnerBuilderClass;
        }
        return null;
    }

    private PsiMethod createConstructorInnerBuilderClass() {
        List<String> listParamNameWithType = new ArrayList<>();
        List<String> listParamNameWithoutType = new ArrayList<>();
        List<String> constructorStatements = new ArrayList<>();
        mandatoryParametersWithConstructor.forEach(param -> {
            listParamNameWithType.add(param.getParameterType() + " " + param.getParameterName());
            listParamNameWithoutType.add(param.getParameterName());

            StringBuilder sbStatement = new StringBuilder("this.")
                    .append(param.getParameterName())
                    .append(" = ")
                    .append(param.getParameterName())
                    .append(";");
            constructorStatements.add(sbStatement.toString());
        });

        final StringBuilder sbMethod = new StringBuilder("private ")
                .append(BUILDER_CLASS_NAME).append("(")
                .append(String.join(",", listParamNameWithType))
                .append(") {\n");
        constructorStatements.forEach(sbMethod::append);
        sbMethod.append("}");
        return elementFactory.createMethodFromText(sbMethod.toString(), null);
    }

    private PsiMethod createMethodInnerBuilderClass(BuilderParameter parameter) {
        final String paramName = parameter.getParameterName();

        StringBuilder sbMethod = new StringBuilder("public Builder ")
                .append(parameter.getSetterName()).append("(")
                .append(parameter.getParameterType()).append(" ").append(paramName).append(") {")
                .append("this.").append(paramName).append(" = ").append(paramName).append(";\n")
                .append("return this;")
                .append("}");

        return elementFactory.createMethodFromText(sbMethod.toString(), null);
    }

    private PsiMethod createBuildMethodInnerBuilderClass() {
        StringBuilder sbMethod = new StringBuilder("public ")
                .append(targetClass.getName()).append(" build() {")
                .append("return new ").append(targetClass.getName()).append("(this);")
                .append("}");

        return elementFactory.createMethodFromText(sbMethod.toString(), null);
    }
}
