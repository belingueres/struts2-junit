package com.hexaid.struts2.junit;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.model.FrameworkMethod;

import com.hexaid.struts2.junit.StrutsBaseTestCase.AbstractStrutsTestConfiguration;
import com.opensymphony.xwork2.ActionProxy;

public class StrutsBaseTestCaseTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testsetUpResolvingAnnotationsWithOutAnnotations() {
		try {
			Test1 test = new Test1();
			
			Method testMethod = 
				test.getClass().getMethod("theMethodNameIsTheActionNameTest");
			test.testName.starting(new FrameworkMethod(testMethod));
			
			AbstractStrutsTestConfiguration config = new MockStrutsTestConfiguration(test);
			test.setUpResolvingAnnotations(config);
			
			assertEquals("struts.xml", config.configFile);
			assertEquals(null, config.namespace);
			assertEquals("theMethodNameIsTheActionNameTest", config.actionName);
			assertEquals(null, config.methodName);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testsetUpResolvingAnnotationsWithAnnotationInTestMethod() {
		try {
			Test1 test = new Test1();
			
			Method testMethod = 
				test.getClass().getMethod("aMethodWithAnnotationTest");
			test.testName.starting(new FrameworkMethod(testMethod));
			
			AbstractStrutsTestConfiguration config = new MockStrutsTestConfiguration(test);
			test.setUpResolvingAnnotations(config);
			
			assertEquals("struts.xml", config.configFile);
			assertEquals("/", config.namespace);
			assertEquals("differentActionName", config.actionName);
			assertEquals(null, config.methodName);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testsetUpResolvingAnnotationsWithAnnotationInTestClass() {
		try {
			Test2 test = new Test2();
			
			Method testMethod = 
				test.getClass().getMethod("theMethodNameIsTheActionNameTest");
			test.testName.starting(new FrameworkMethod(testMethod));
			
			AbstractStrutsTestConfiguration config = new MockStrutsTestConfiguration(test);
			test.setUpResolvingAnnotations(config);
			
			assertEquals("another-struts.xml", config.configFile);
			assertEquals("/somename", config.namespace);
			assertEquals("theMethodNameIsTheActionNameTest", config.actionName);
			assertEquals(null, config.methodName);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testsetUpResolvingAnnotationsWithAnnotationInTestClassAndMethod() {
		try {
			Test2 test = new Test2();
			
			Method testMethod = 
				test.getClass().getMethod("aMethodWithAnnotationTest");
			test.testName.starting(new FrameworkMethod(testMethod));
			
			AbstractStrutsTestConfiguration config = new MockStrutsTestConfiguration(test);
			test.setUpResolvingAnnotations(config);
			
			assertEquals("another-struts.xml", config.configFile);
			assertEquals("/", config.namespace);
			assertEquals("differentActionName", config.actionName);
			assertEquals("show", config.methodName);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	public static class Test1 extends StrutsBaseTestCase {
		@Test
		public void theMethodNameIsTheActionNameTest() {
		}
		
		@Test
		@Config(actionName="differentActionName", namespace="/")
		public void aMethodWithAnnotationTest() {
		}
	}

	@Config(file="another-struts.xml", namespace="/somename")
	public static class Test2 extends StrutsBaseTestCase {
		@Test
		public void theMethodNameIsTheActionNameTest() {
		}
		
		@Test
		@Config(actionName="differentActionName", namespace="/", methodName="show")
		public void aMethodWithAnnotationTest() {
		}
	}
	
	private static class MockStrutsTestConfiguration extends StrutsBaseTestCase.AbstractStrutsTestConfiguration {
		public MockStrutsTestConfiguration(StrutsBaseTestCase testObject) {
			super(testObject);
		}

		@Override
		public void loadConfiguration(String configFile) {
			// do nothing
		}

		@Override
		public ActionProxy createActionProxy(String namespace, String actionName,
				String methodName) {
			// do nothing
			return null;
		}
		
	}

}
