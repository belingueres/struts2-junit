package com.hexaid.struts2.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Gabriel Belingueres
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.TYPE, ElementType.METHOD })
public @interface Config {
	String file() default "";
	String namespace() default "";
	String actionName() default "";
	String methodName() default "";
}
