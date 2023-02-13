package org.termi.auth.keycloak;

import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.keycloak.platform.Platform;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.termi.auth.keycloak.providers.SimplePlatformProvider;

import javax.naming.CompositeName;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingException;
import javax.naming.spi.NamingManager;
import javax.sql.DataSource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@RequiredArgsConstructor
public class Config {

	private final ServerProperties properties;
	private final DataSource dataSource;

	@Bean
	ServletRegistrationBean<HttpServlet30Dispatcher> keycloakJaxRsApplication() throws Exception {
		mockJndiEnvironment();
		App.properties = properties;
		final var servlet = new ServletRegistrationBean<HttpServlet30Dispatcher>(new HttpServlet30Dispatcher());
		servlet.addInitParameter("javax.ws.rs.Application", App.class.getName());
		servlet.addInitParameter(ResteasyContextParameters.RESTEASY_SERVLET_MAPPING_PREFIX,
		        properties.contextPath());
		servlet.addInitParameter(ResteasyContextParameters.RESTEASY_USE_CONTAINER_FORM_PARAMS, "true");
		servlet.addUrlMappings(properties.contextPath() + "/*");
		servlet.setLoadOnStartup(1);
		servlet.setAsyncSupported(true);
		return servlet;
	}

	@Bean
	FilterRegistrationBean<RequestFilter> keycloakSessionManagement() {
		final var filter = new FilterRegistrationBean<RequestFilter>();
		filter.setName("Keycloak Session Management");
		filter.setFilter(new RequestFilter());
		filter.addUrlPatterns(properties.contextPath() + "/*");
		return filter;
	}

	private void mockJndiEnvironment() throws NamingException {
		NamingManager.setInitialContextFactoryBuilder((env) -> (environment) -> new InitialContext() {
			@Override
			public Object lookup(Name name) {
				return lookup(name.toString());
			}

			@Override
			public Object lookup(String name) {
				if ("spring/datasource".equals(name)) {
					return dataSource;
				} else if (name.startsWith("java:jboss/ee/concurrency/executor/")) {
					return fixedThreadPool();
				}
				return null;
			}

			@Override
			public NameParser getNameParser(String name) {
				return CompositeName::new;
			}

			@Override
			public void close() {}
		});
	}

	@Bean("fixedThreadPool")
	ExecutorService fixedThreadPool() {
		return Executors.newFixedThreadPool(5);
	}

	@Bean
	@ConditionalOnMissingBean(name = "springBootPlatform")
	protected SimplePlatformProvider springBootPlatform() {
		return (SimplePlatformProvider) Platform.getPlatform();
	}
}
