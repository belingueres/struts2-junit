package com.hexaid.struts2.junit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to allow the StrutsBaseTestCase to configure the execution
 * environment of the test.
 *  
 * @author Gabriel Belingueres
 * @version 0.2
 * @since 0.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.TYPE, ElementType.METHOD })
public @interface Config {
	/**
	 * The Struts 2 configuration XML files to load as 
	 * configuration. It will automatically load the 
	 * 'struts-default.xml' file as the first configuration provider,
	 * and then will load the specified files in their order of
	 * appearance.
	 */
	String[] file() default {};

	/**
	 * The namespace where the Action resides. If not specified,
	 * the default namespace is applied. 
	 */
	String namespace() default "";

	/**
	 * The action name to execute by the test, as defined inside
	 * the configuration XML file. If not specified, the 
	 * <b>method name</b under test is the action to execute.<br/>
	 * For example, if the test method is:
	 * <pre>
	 * &#064;Config
	 * &#064;Test
	 * public void testSomeBusinessLogic() {...}
	 * </pre>
	 * 
	 * <br/>
	 * then the action name to execute is 'testSomeBusinessLogic'. 
	 */
	String actionName() default "";

	/**
	 * The method name to execute inside the requested action.<br/>
	 * This corresponds to the <pre>method</pre> attribute
	 * of the <pre>action</pre> tag of the configuration xml file.
	 * If not specified then the default method inside the action
	 * object is executed, that is, <pre>execute()</pre>.
	 */
	String methodName() default "";
}
