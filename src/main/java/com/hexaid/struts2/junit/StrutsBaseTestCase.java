package com.hexaid.struts2.junit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsJUnit4TestCase;
import org.apache.struts2.config.StrutsXmlConfigurationProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.springframework.mock.web.MockHttpSession;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ActionProxyFactory;
import com.opensymphony.xwork2.config.Configuration;

/**
 * @author Gabriel Belingueres
 * @version 1.0
 * @since 0.1
 */
public class StrutsBaseTestCase extends StrutsJUnit4TestCase<Action> {
	
	protected MockHttpSession session;
	protected ActionProxy actionProxy;
    private DefaultConfiguration annotationConfiguration;
	
	@Rule public TestName testName = new TestName();
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		
		session = new MockHttpSession();
		request.setSession(session);
		
		annotationConfiguration.createActionProxy();
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
		session = null;
		actionProxy = null;
	}
	
	@Override
    protected void setupBeforeInitDispatcher() throws Exception {
        annotationConfiguration = new DefaultConfiguration(this); 
  
        setUpResolvingAnnotations(annotationConfiguration);
    }

    @Override
    protected String getConfigPath() {
	    return StringUtils.join(annotationConfiguration.configFile, ',');
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
                                            final String actionName,
                                            final String methodName) {
//        request.setRequestURI(uri);
//        ActionMapping mapping = getActionMapping(request);
//        String namespace = mapping.getNamespace();
//        String name = mapping.getName();
//        String method = mapping.getMethod();
    
        Configuration config = configurationManager.getConfiguration();
        ActionProxy proxy = config.getContainer().getInstance(ActionProxyFactory.class)
            .createActionProxy(namespace, actionName, methodName, new HashMap<String, Object>(), true, false);
    
        ActionContext invocationContext = proxy.getInvocation().getInvocationContext();
        invocationContext.setParameters(new HashMap<String, Object>(request.getParameterMap()));
        // set the action context to the one used by the proxy
        ActionContext.setContext(invocationContext);
    
        // this is normaly done in onSetUp(), but we are using Struts internal
        // objects (proxy and action invocation)
        // so we have to hack around so it works
        ServletActionContext.setServletContext(servletContext);
        ServletActionContext.setRequest(request);
        ServletActionContext.setResponse(response);
        
        actionProxy = proxy;
    
        return proxy;
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

		final StrutsXmlConfigurationProvider[] providersArray = 
				providers.toArray(new StrutsXmlConfigurationProvider[providers.size()]);
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
		
		public abstract ActionProxy createActionProxy();

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
		public ActionProxy createActionProxy() {
			return testObject.createActionProxy(namespace, actionName, methodName);
		}

	}

}
