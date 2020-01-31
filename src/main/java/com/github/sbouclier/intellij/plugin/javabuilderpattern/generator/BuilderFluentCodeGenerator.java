package com.github.sbouclier.intellij.plugin.javabuilderpattern.generator;

import com.github.sbouclier.intellij.plugin.javabuilderpattern.model.BuilderParameter;
import com.github.sbouclier.intellij.plugin.javabuilderpattern.util.StringUtils;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Code generator for Builder with interfaces.
 */
public class BuilderFluentCodeGenerator extends AbstractBuilderCodeGenerator {

    private static final String BUILD_INTERFACE_NAME = "IBuild";
    private static final String BUILDER_CLASS_NAME = "Builder";

    private List<BuilderParameter> mandatoryParameters = new ArrayList<>();
    private List<BuilderParameter> mandatoryParametersWithConstructor = new ArrayList<>();
    private List<BuilderParameter> mandatoryParametersWithoutConstructor = new ArrayList<>();
    private List<BuilderParameter> optionalParameters = new ArrayList<>();

    public BuilderFluentCodeGenerator(PsiClass targetClass, List<BuilderParameter> parameters) {
        super(targetClass, parameters);
    }

    @Override
    public void generate() {
        extractParameters();
        WriteCommandAction
                .writeCommandAction(targetClass.getProject())
                .run(this::createBuilder);
    }

    private void extractParameters() {
        this.parameters.forEach(param -> {
            if (param.isMandatory()) {
                mandatoryParameters.add(param);
                if (param.isConstructor()) {
                    mandatoryParametersWithConstructor.add(param);
                } else {
                    mandatoryParametersWithoutConstructor.add(param);
                }
            } else {
                optionalParameters.add(param);
            }
        });
    }

    private void createBuilder() {
        targetClass.add(createPrivateConstructorInnerClass());
        mandatoryParametersWithoutConstructor.forEach(param -> targetClass.add(createMandatoryInterfaceInnerClass(param)));

        targetClass.add(createStaticBuilderInnerClass());
        targetClass.add(createOptionalsInterfaceBuildInnerClass());
        targetClass.add(createInnerBuilderClass());
    }

    private PsiMethod createPrivateConstructorInnerClass() {
        PsiMethod constructor = elementFactory.createConstructor();
        constructor.getModifierList().setModifierProperty(PsiModifier.PRIVATE, true);
        return constructor;
    }

    private PsiMethod createStaticBuilderInnerClass() {
        final String firstInterfaceName;
        if(mandatoryParametersWithoutConstructor.size() > 0) {
            firstInterfaceName = generateInterfaceName(mandatoryParametersWithoutConstructor.get(0).getParameterName());
        } else {
            firstInterfaceName = BUILD_INTERFACE_NAME;
        }

        List<String> listParamNameWithType = new ArrayList<>();
        List<String> listParamNameWithoutType = new ArrayList<>();
        mandatoryParametersWithConstructor.forEach(param -> {
            listParamNameWithType.add(param.getParameterType() + " " + param.getParameterName());
            listParamNameWithoutType.add(param.getParameterName());
        });

        StringBuilder sbMethod = new StringBuilder("public static ")
                .append(firstInterfaceName)
                .append(" builder(")
                .append(String.join(",", listParamNameWithType))
                .append(") {")
                .append("return new ").append(BUILDER_CLASS_NAME).append("(")
                .append(String.join(",", listParamNameWithoutType))
                .append(");").append("}");
        return elementFactory.createMethodFromText(sbMethod.toString(), null);
    }

    private PsiClass createMandatoryInterfaceInnerClass(BuilderParameter parameter) {
        final String paramName = parameter.getParameterName();
        final String name = generateInterfaceName(paramName);
        final String nextInterface = nextInterfaceName(parameter).orElse(BUILD_INTERFACE_NAME);

        PsiClass parameterInterface = elementFactory.createInterface(name);

        StringBuilder sbMethod = new StringBuilder(nextInterface)
                .append(" ").append(parameter.getSetterName()).append("(")
                .append(parameter.getParameterType())
                .append(" ").append(paramName).append(");");
        PsiMethod method = elementFactory.createMethodFromText(sbMethod.toString(), null);
        parameterInterface.add(method);

        return parameterInterface;
    }

    private PsiClass createOptionalsInterfaceBuildInnerClass() {
        PsiClass buildInterface = elementFactory.createInterface(BUILD_INTERFACE_NAME);

        optionalParameters.forEach(param -> {
            StringBuilder sbMethod = new StringBuilder(BUILD_INTERFACE_NAME)
                    .append(" ").append(param.getSetterName()).append("(")
                    .append(param.getParameterType())
                    .append(" ").append(param.getParameterName()).append(");");
            PsiMethod method = elementFactory.createMethodFromText(sbMethod.toString(), null);

            buildInterface.add(method);
        });

        StringBuilder sbMethod = new StringBuilder(targetClass.getName()).append(" build();");
        PsiMethod buildMethod = elementFactory.createMethodFromText(sbMethod.toString(), null);

        buildInterface.add(buildMethod);

        return buildInterface;
    }

    private PsiClass createInnerBuilderClass() {
        PsiClass innerBuilderClass = targetClass.findInnerClassByName(BUILDER_CLASS_NAME, true);
        if (innerBuilderClass == null) {
            // class
            final PsiClass finalInnerBuilderClass = elementFactory.createClass(BUILDER_CLASS_NAME);
            finalInnerBuilderClass.getModifierList().setModifierProperty(PsiModifier.STATIC, true);
            finalInnerBuilderClass.getModifierList().setModifierProperty(PsiModifier.FINAL, true);

            // class implements interfaces
            mandatoryParametersWithoutConstructor.forEach(param ->
                    finalInnerBuilderClass
                            .getImplementsList()
                            .add(elementFactory.createReferenceFromText(
                                    generateInterfaceName(param.getParameterName()), null)));
            finalInnerBuilderClass.getImplementsList().add(elementFactory.createReferenceFromText(BUILD_INTERFACE_NAME, null));

            // instance
            PsiClassType targetClassType = elementFactory.createType(targetClass);
            PsiField instance = elementFactory.createField("instance", targetClassType);
            instance.getModifierList().setModifierProperty(PsiModifier.FINAL, true);
            instance.setInitializer(
                    elementFactory.createExpressionFromText("new " + targetClassType.getName() + "()", null)
            );
            finalInnerBuilderClass.add(instance);

            // constructor
            finalInnerBuilderClass.add(createConstructorInnerBuilderClass());

            // parameter methods
            mandatoryParametersWithoutConstructor.forEach(param -> finalInnerBuilderClass.add(createMandatoryMethodInnerBuilderClass(param)));
            optionalParameters.forEach(param -> finalInnerBuilderClass.add(createOptionalsMethodInnerBuilderClass(param)));

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

            StringBuilder sbStatement = new StringBuilder("instance.")
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

    private PsiMethod createMandatoryMethodInnerBuilderClass(BuilderParameter parameter) {
        final String paramName = parameter.getParameterName();
        final String name = generateInterfaceName(paramName);
        final String nextInterface = nextInterfaceName(parameter).orElse(BUILD_INTERFACE_NAME);

        StringBuilder sbMethod = new StringBuilder("@Override\n")
                .append("public ").append(nextInterface)
                .append(" ").append(parameter.getSetterName()).append("(")
                .append(parameter.getParameterType())
                .append(" ").append(paramName).append(") {")
                .append("instance.").append(paramName).append(" = ").append(paramName).append(";\n")
                .append("return this;")
                .append("}");

        return elementFactory.createMethodFromText(sbMethod.toString(), null);
    }

    private PsiMethod createOptionalsMethodInnerBuilderClass(BuilderParameter parameter) {
        final String paramName = parameter.getParameterName();

        StringBuilder sbMethod = new StringBuilder("@Override\n")
                .append("public ").append(BUILD_INTERFACE_NAME)
                .append(" ").append(parameter.getSetterName()).append("(")
                .append(parameter.getParameterType())
                .append(" ").append(paramName).append(") {")
                .append("instance.").append(paramName).append(" = ").append(paramName).append(";\n")
                .append("return this;")
                .append("}");

        return elementFactory.createMethodFromText(sbMethod.toString(), null);
    }

    private PsiMethod createBuildMethodInnerBuilderClass() {
        StringBuilder sbMethod = new StringBuilder("@Override\n")
                .append("public ").append(targetClass.getName()).append(" build() {")
                .append("return instance;")
                .append("}");

        return elementFactory.createMethodFromText(sbMethod.toString(), null);
    }

    private String generateInterfaceName(String name) {
        return new StringBuilder("I")
                .append(StringUtils.firstUppercaseLetter(name))
                .toString();
    }

    private Optional<String> nextInterfaceName(BuilderParameter param) {
        int idx = mandatoryParametersWithoutConstructor.indexOf(param);
        if (idx < 0 || idx + 1 == mandatoryParametersWithoutConstructor.size()) {
            return Optional.empty();
        }
        return Optional.of(generateInterfaceName(mandatoryParametersWithoutConstructor.get(idx + 1).getParameterName()));
    }
}
