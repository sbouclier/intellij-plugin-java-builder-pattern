package com.github.sbouclier.intellij.plugin.javabuilderpattern.util;

import org.junit.Assert;
import org.junit.Test;

public class StringUtilsTest {

    @Test
    public void should_return_first_uppercase_letter() {
        Assert.assertEquals("MyTest", StringUtils.firstUppercaseLetter("myTest"));
    }
}
