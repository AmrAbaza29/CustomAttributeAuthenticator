package com.example.keycloak.authenticator;

import org.keycloak.authentication.AbstractFormAuthenticator;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import jakarta.ws.rs.core.Response;

public class UserDataDisplayAuthenticator extends AbstractFormAuthenticator {


    @Override
    public void authenticate(AuthenticationFlowContext context) {
        UserModel user = context.getUser();
        if (user == null) {
            context.failure(AuthenticationFlowError.UNKNOWN_USER);
            return;
        }

        String email = user.getEmail();
        String customAttr1 = user.getFirstAttribute("ContractNumber");
        String customAttr2 = user.getFirstAttribute("RegistrationFormNumber");

        Response challenge = context.form()
            .setAttribute("email", email)
            .setAttribute("customAttr1", customAttr1)
            .setAttribute("customAttr2", customAttr2)
            .createForm("user-data-display.ftl");

        context.challenge(challenge);
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        context.success();
    }

    @Override
    public boolean requiresUser() {
        return true;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
        // No required actions for this authenticator.
    }

    @Override
    public void close() {
        // No resources to close.
    }
}