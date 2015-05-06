# Introduction #

This library is based on the `StrutsJUnit4TestCase` class that comes with the Struts 2 distribution.

# Current Release: 0.2.2 #

This is a bug fix release that allow the tests to execute without the need to download the Struts DTD files from the Internet **every time** the test executes, thereby greatly improving the test execution times. No functionality changed from version 0.2.

To use the library, include it as a test dependency in your Maven projects:

```
<dependency>
  <groupId>com.hexaid.struts2.junit</groupId>
  <artifactId>struts2-junit</artifactId>
  <version>0.2.2</version>
  <scope>test</scope>
</dependency>
```

# Previous Releases #

0.2.1: This is just a formal repackaging to comply with Maven Central Repository rules. No functionality changed from version 0.2.

Now you can use the library by adding it as a dependency in your Maven projects:

```
<dependency>
  <groupId>com.hexaid.struts2.junit</groupId>
  <artifactId>struts2-junit</artifactId>
  <version>0.2.1</version>
  <scope>test</scope>
</dependency>
```

0.2: The last and stable release is the struts2-junit-0.2-bin.zip, which you may download from this site. See the [ReleaseNotes](ReleaseNotes.md).

# Usage #

This library is primarily intended for integration tests, meaning you can test your actions and interceptors against a complete struts interceptor stack, and more generally, against a custom made `struts.xml` file.

Actually the library is only 2 classes:

  * `StrutsBaseTestCase`: A base class from which your test cases would need to extend.

  * `@Config`: An annotation specifying which action to execute in the test. This annotation is versatile enough to let you configure test class commonalities only one time at class definition, and introduce exceptions at test method level.

# An example #

Let's see an example test using the following configuration `struts.xml` file which intents to test that the `BijectionInterceptor` (using à la JBoss Seam's `@In` and `@Out` annotations) is working properly injecting and outjecting values in the conversation scope:

```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE struts PUBLIC "-//Apache Software Foundation//DTD Struts Configuration 2.1//EN"
  "http://struts.apache.org/dtds/struts-2.1.dtd">
<struts>
  <package name="testpkg" extends="struts-default">
    <interceptors>
      <interceptor class="com.hexaid.struts2.interceptor.BijectionInterceptor" name="bijection"/>
      <interceptor-stack name="testStack">
        <interceptor-ref name="defaultStack" />
        <interceptor-ref name="bijection" />
      </interceptor-stack>
    </interceptors>
    <default-interceptor-ref name="testStack" />

    <action
        name="testInterceptAtInAtOutAtConversationScopeConversation"
        class="com.hexaid.struts2.interceptor.TestInOutAction">
      <result>/dummy.jsp</result>
    </action>
  </package>
</struts>
```

The Test class:

```
public class TestBijectionInterceptorComplete extends StrutsBaseTestCase {

  @Test
  public void testInterceptAtInAtOutAtConversationScopeConversation() throws Exception {
    // prepare a new empty conversation
    String conversationId = "1234";
    Conversation conversation = new Conversation(conversationId, null);
    conversation.getMap().put("msg", "injectedValue");
    Map<String,Conversation> conversationsMap = new HashMap<String, Conversation>();
    conversationsMap.put(conversationId, conversation);
    session.setAttribute(Conversation.CONVERSATIONS_MAP_KEY, conversationsMap);

    request.setParameter(Conversation.CONVERSATION_ID_PARAM, conversationId);

    actionProxy.execute();

    String outjected = (String) conversation.getMap().get("msg");
		
    assertEquals("The outjected value is not correct", "injectedValue-MODIFIED-", outjected);
  }
}
```

And the mock action where to perform the bijection is:

```
public class TestInOutAction extends ActionSupport {

  private static final long serialVersionUID = 1L;
	
  @In @Out
  private String msg;
	
  @Override
  public String execute() throws Exception {
    msg += "-MODIFIED-";
    return SUCCESS;
  }

}
```

# How it works #

The `StrutsBaseTestCase` takes care of loading the struts configuration file and execute the action, using some handy default conventions:

  * It loads the actions from the `struts.xml` file.
  * The action name to execute in each test method, is the test method name by itself.
  * It assumes that the action is called with no namespace.
  * It assumes that the action method to call is the `execute()` method.

When some of those parameters change, you can use the `@Config` annotation at class level or method level, to provide the right configuration for the test executions. The values allowed are these:

  * `file`: The file name of the `struts.xml` configuration file.
  * `actionName`: The action name on the configuration file to call.
  * `namespace`: The namespace on the configuration file to call the right action.
  * `methodName`: The name of the method inside the action class that needs to be executed.

To avoid repeating yourself using nearly identical `@Config` annotations at method level, you can place a more general one for the entire test file at class definition level, and apply special parameters using `@Config` annotations at method level. Por example:

```
@Config(file="test-struts.xml")
public class ExampleTest extends StrutsBaseTestCase {

  @Test
  @Config(actionName="doit")
  // executes the action named 'doit' which is defined inside the 'test-struts.xml' configuration file.
  public void testInterceptAtInAtOutAtConversationScopeConversation() throws Exception {
    ...
  }
}
```