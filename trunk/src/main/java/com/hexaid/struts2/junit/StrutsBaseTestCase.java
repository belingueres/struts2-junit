package com.hexaid.struts2.junit;

import java.util.Map;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsJUnit4TestCase;
import org.apache.struts2.dispatcher.Dispatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;

import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;

/**
 * @author Gabriel Belingueres
 *
 */
public class StrutsBaseTestCase extends StrutsJUnit4TestCase {
	
	protected MockServletContext servletContext;
	protected MockHttpServletRequest request;
	protected MockHttpSession session;
	protected MockHttpServletResponse response;
	protected ActionProxy actionProxy;
	
	@Rule public TestName testName = new TestName();
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		
		servletContext = new MockServletContext();
		request = new MockHttpServletRequest();
		session = new MockHttpSession();
		request.setSession(session);
		response = new MockHttpServletResponse();
		ServletActionContext.setRequest(request);
		ServletActionContext.setResponse(response);
		ServletActionContext.setServletContext(servletContext);
		
		DefaultConfiguration config = new DefaultConfiguration(this); 

		setUpResolvingAnnotations(config);
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
		response = null;
		session = null;
		request.setSession(null);
		request = null;
		servletContext = null;
		actionProxy = null;
	}

	/**
	 * Create an ActionProxy, which is used to call an action through the entire interceptor stack.
	 * NOTE: Before creating ActionProxies, you must loadConfiguration();
	 * @param namespace the namespace of the action to execute (it may be null).
	 * @param actionName the action name to execute.
	 * @param methodName the method inside the action to execute (it may be null).
	 * @return the newly created ActionProxy. 
	 */
	protected ActionProxy createActionProxy(final String namespace,
			final String actionName, final String methodName) {
		final Dispatcher dispatcher = Dispatcher.getInstance();
		final Map<String, Object> contextMap = dispatcher.createContextMap(
				request, response, null, servletContext);

		actionProxy = 
				this.actionProxyFactory.createActionProxy(namespace, actionName, methodName, contextMap);
		return actionProxy;
	}

	/**
	 * Loads the struts.xml configuration biven by the strutsFilename.
	 * @param strutsFilename the struts.xml file name used for this test.
	 */
	protected void loadConfiguration(final String strutsFilename) {
		XmlConfigurationProvider providerStd = 
				new XmlConfigurationProvider("struts-default.xml");

		XmlConfigurationProvider provider = 
				new XmlConfigurationProvider(strutsFilename);

		provider.init(configuration);
		loadConfigurationProviders(providerStd, provider);
	}

	protected void setUpResolvingAnnotations(AbstractStrutsTestConfiguration config) throws NoSuchMethodException {
		// type level annotation
		Config annotation = this.getClass().getAnnotation(Config.class);
		if (annotation != null) {
			// initialize variables with type level values
			config.updateIfNotEmpty(annotation);
		}

		// get method level annotation
		annotation = this.getClass().getMethod(testName.getMethodName()).getAnnotation(Config.class);
		if (annotation != null) {
			// update variables if something is configured at method level
			config.updateIfNotEmpty(annotation);
		}
		
		config.setUpFixture();
	}
	
	static abstract class AbstractStrutsTestConfiguration {
		protected StrutsBaseTestCase testObject;
		
		protected String configFile = "";
		protected String namespace = "";
		protected String actionName = "";
		protected String methodName = "";
		
		public AbstractStrutsTestConfiguration(StrutsBaseTestCase testObject) {
			this.testObject = testObject;
		}
		
		public abstract void setUpFixture();
		public abstract void loadConfiguration(String configFile);
		public abstract ActionProxy createActionProxy(String namespace, String actionName, String methodName);

		public void updateIfNotEmpty(Config annotation) {
			configFile = updateIfNotEmpty(configFile, annotation.file());
			namespace = updateIfNotEmpty(namespace, annotation.namespace());
			actionName = updateIfNotEmpty(actionName, annotation.actionName());
			methodName = updateIfNotEmpty(methodName, annotation.methodName());
		}

		protected String updateIfNotEmpty(String current, String newValue) {
			return newValue.isEmpty() ? current : newValue;
		}
	}

	
	static class DefaultConfiguration extends AbstractStrutsTestConfiguration {
		
		public DefaultConfiguration(StrutsBaseTestCase testObject) {
			super(testObject);
		}

		@Override
		public void loadConfiguration(String configFile) {
			testObject.loadConfiguration(configFile);
		}

		@Override
		public ActionProxy createActionProxy(String namespace, String actionName, String methodName) {
			return testObject.createActionProxy(namespace, actionName, methodName);
		}

		@Override
		public void setUpFixture() {
			// prepare parameters
			//
			if (configFile.isEmpty()) {
				// defaults to some default xml file
				configFile = "struts.xml";
			}
			
			if (actionName.isEmpty()) {
				// defaults to the method name
				actionName = testObject.testName.getMethodName();
			}

			if (namespace.isEmpty()) {
				// defaults to no namespace
				namespace = null;
			}
			
			if (methodName.isEmpty()) {
				// defaults to no method specified
				methodName = null;
			}

			// do Struts the initialization stuff
			//
			loadConfiguration(configFile);
			createActionProxy(namespace, actionName, methodName);
		}

	}

}
