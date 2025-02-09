package com.example.keycloak.authenticator;

import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.List;

public class CustomAttributeAuthenticatorFactory implements AuthenticatorFactory {

    public static final String PROVIDER_ID = "custom-attribute-authenticator";

    @Override
    public String getDisplayType() {
        return "Custom Attribute Authenticator";
    }

    @Override
    public String getReferenceCategory() {
        return null;
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return new AuthenticationExecutionModel.Requirement[]{
                AuthenticationExecutionModel.Requirement.REQUIRED,
                AuthenticationExecutionModel.Requirement.ALTERNATIVE,
                AuthenticationExecutionModel.Requirement.DISABLED
        };
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public String getHelpText() {
        return "Authenticator that allows login with custom attributes.";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return List.of(
        new ProviderConfigProperty("customAttribute1", "Custom Attribute 1",
                "First custom attribute for authentication", ProviderConfigProperty.STRING_TYPE, ""),
        new ProviderConfigProperty("customAttribute2", "Custom Attribute 2",
                "Second custom attribute for authentication", ProviderConfigProperty.STRING_TYPE, "")
    );
    }

    @Override
    public Authenticator create(KeycloakSession session) {
        return new CustomAttributeAuthenticator();
    }

    @Override
    public void init(org.keycloak.Config.Scope config) {
        // No initialization required
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // No post-initialization required
    }

    @Override
    public void close() {
        // No resources to close
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

}