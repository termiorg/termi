package org.termi.auth.keycloak;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.Config;
import org.keycloak.exportimport.ExportImportManager;
import org.keycloak.models.KeycloakSession;
import org.keycloak.services.managers.ApplianceBootstrap;
import org.keycloak.services.resources.KeycloakApplication;
import org.keycloak.services.util.JsonConfigProviderFactory;
import org.termi.auth.keycloak.providers.JsonProviderFactory;

import java.util.NoSuchElementException;

@Slf4j
public class App extends KeycloakApplication {

	static ServerProperties properties;

	protected void loadConfig() {
		JsonConfigProviderFactory factory = new JsonProviderFactory();
		Config.init(factory.create().orElseThrow(() -> new NoSuchElementException("No value present")));
	}

	@Override
	protected ExportImportManager bootstrap() {
		final ExportImportManager exportImportManager = super.bootstrap();
		createMasterRealmAdminUser();
		return exportImportManager;
	}

	private void createMasterRealmAdminUser() {
		KeycloakSession session = getSessionFactory().create();
		ApplianceBootstrap applianceBootstrap = new ApplianceBootstrap(session);
		try {
			session.getTransactionManager().begin();
			applianceBootstrap.createMasterRealmUser(properties.username(), properties.password());
			session.getTransactionManager().commit();
		} catch (Exception ex) {
			log.warn("Couldn't create keycloak master admin user: {}", ex.getMessage());
			session.getTransactionManager().rollback();
		}
		session.close();
	}

}
