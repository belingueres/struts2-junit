package com.hexaid.struts2.junit;

import static org.junit.Assert.*;

import org.junit.Test;

import com.opensymphony.xwork2.Action;

/**
 * @author Gabriel Belingueres
 *
 */
@Config(file="struts.xml")
public class MyActionTestCaseTest extends StrutsBaseTestCase {

    /**
     * Test that the action name is taken from the method name 
     */
    @Test
    public void myaction() {
        try {
            String result = actionProxy.execute();
            assertEquals(Action.SUCCESS, result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Should not raise any exception!");
        }
    }

    /**
     * Test that the action name is taken from the annotation 
     */
    @Test
    @Config(actionName="myaction")
    public void methodIsNotActionNameTest() {
        try {
            String result = actionProxy.execute();
            assertEquals(Action.SUCCESS, result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Should not raise any exception!");
        }
    }

    /**
     * Test that the action name is taken from the annotation, and it works
     * with any namespace since it has none defined 
     */
    @Test
    @Config(actionName="myaction", namespace="/admin")
    public void actionNotInAnyNamespaceWorksTest() {
        try {
            String result = actionProxy.execute();
            assertEquals(Action.SUCCESS, result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Should not raise any exception!");
        }
    }

    /**
     * Test that the action name is taken from the annotation, and it works
     * with correct namespace 
     */
    @Test
    @Config(actionName="myactionnamespace", namespace="/admin")
    public void actionCorrectNamespaceWorkTest() {
        try {
            String result = actionProxy.execute();
            assertEquals(Action.SUCCESS, result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Should not raise any exception!");
        }
    }

    /**
     * Test that the action name is NOT taken from the annotation, and it execute
     * a specific method
     */
    @Test
    public void mymethod() {
        try {
            String result = actionProxy.execute();
            assertEquals(Action.INPUT, result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Should not raise any exception!");
        }
    }

    /**
     * Test that the action name is taken from the annotation, and it execute
     * a specific method name
     */
    @Test
    @Config(actionName="mymethod", methodName="someMethod")
    public void actionSpecifyingMethodTest() {
        try {
            String result = actionProxy.execute();
            assertEquals(Action.INPUT, result);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Should not raise any exception!");
        }
    }

    /**
     * Test that the action name is taken from the annotation, and it execute
     * a specific method name
     * @throws Exception 
     */
    @Test(expected=NoSuchMethodException.class)
    @Config(actionName="mymethod", methodName="nonExistingMethod")
    public void actionSpecifyingNonExistingMethodTest() throws Exception {
        actionProxy.execute();
    }
}
