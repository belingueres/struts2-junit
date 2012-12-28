package com.hexaid.struts2.junit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsJUnit4TestCase;
import org.apache.struts2.config.StrutsXmlConfigurationProvider;
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
 * @version 0.2.2
 * @since 0.1
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
		
		final DefaultConfiguration config = new DefaultConfiguration(this); 

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
	 * Loads the XML configuration providers given by the strutsFilenames array.
	 * @param strutsFilenames the XML configuration files names used for this test.
	 */
	protected void loadConfiguration(final String[] strutsFilenames) {
		final List<StrutsXmlConfigurationProvider> providers = new ArrayList<StrutsXmlConfigurationProvider>();
		
		final StrutsXmlConfigurationProvider providerStd = 
				new StrutsXmlConfigurationProvider("struts-default.xml", true, servletContext);
		
		providers.add(providerStd);

		for(final String filename : strutsFilenames) {
			final StrutsXmlConfigurationProvider provider = new StrutsXmlConfigurationProvider(filename, true, servletContext);
			provider.init(configuration);
			
			providers.add(provider);
		}

		final XmlConfigurationProvider[] providersArray = 
				providers.toArray(new XmlConfigurationProvider[providers.size()]);
		loadConfigurationProviders(providersArray);
	}

	protected void setUpResolvingAnnotations(final AbstractStrutsTestConfiguration config) throws NoSuchMethodException {
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
		
		protected String[] configFile = {};
		protected String namespace = "";
		protected String actionName = "";
		protected String methodName = "";
		
		public AbstractStrutsTestConfiguration(final StrutsBaseTestCase testObject) {
			this.testObject = testObject;
		}
		
		public abstract void loadConfiguration(String[] configFile);
		public abstract ActionProxy createActionProxy(String namespace, String actionName, String methodName);

		public void setUpFixture() {
			// prepare parameters
			//
			if (configFile.length == 0) {
				// none specified
				configFile = new String[] { "struts.xml" };
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

		public void updateIfNotEmpty(final Config annotation) {
			configFile = updateArrayIfNotEmpty(configFile, annotation.file());
			namespace = updateIfNotEmpty(namespace, annotation.namespace());
			actionName = updateIfNotEmpty(actionName, annotation.actionName());
			methodName = updateIfNotEmpty(methodName, annotation.methodName());
		}

		private String[] updateArrayIfNotEmpty(final String[] current, final String[] newValue) {
			return newValue.length == 0 ? current : newValue;
		}

		protected String updateIfNotEmpty(final String current, final String newValue) {
			return newValue.isEmpty() ? current : newValue;
		}
	}

	
	static class DefaultConfiguration extends AbstractStrutsTestConfiguration {
		
		public DefaultConfiguration(final StrutsBaseTestCase testObject) {
			super(testObject);
		}

		@Override
		public void loadConfiguration(final String[] configFile) {
			testObject.loadConfiguration(configFile);
		}

		@Override
		public ActionProxy createActionProxy(final String namespace, final String actionName, final String methodName) {
			return testObject.createActionProxy(namespace, actionName, methodName);
		}

	}

}
