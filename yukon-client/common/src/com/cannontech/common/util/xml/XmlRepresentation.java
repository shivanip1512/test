package com.cannontech.common.util.xml;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The XmlRepresenation annotation is an annotation used to give enum values a more user friendly look in xml files.
 * This annotation is only need on values that do not meet the usual convention.  Ex.: ABC_DEF_GHI.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface XmlRepresentation {
    public String value();
}