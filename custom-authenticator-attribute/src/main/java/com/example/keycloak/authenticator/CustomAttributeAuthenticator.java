package com.example.keycloak.authenticator;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AbstractFormAuthenticator;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.services.validation.Validation;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomAttributeAuthenticator extends AbstractFormAuthenticator {

    private static final Logger logger = Logger.getLogger(CustomAttributeAuthenticator.class);

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        String customAttr1 = context.getAuthenticatorConfig().getConfig().getOrDefault("customAttribute1", "ContractNumber");
        String customAttr2 = context.getAuthenticatorConfig().getConfig().getOrDefault("customAttribute2", "RegistrationFormNumber");
    
        logger.info("Using Custom Attributes: " + customAttr1 + ", " + customAttr2);
    
        Response challenge = context.form()
            .setAttribute("attribute1Label", customAttr1)
            .setAttribute("attribute2Label", customAttr2)
            .createForm("custom-attribute-form.ftl");
    
        context.challenge(challenge);
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
    
        String customAttr1 = context.getAuthenticatorConfig().getConfig().getOrDefault("customAttribute1", "ContractNumber");
        String customAttr2 = context.getAuthenticatorConfig().getConfig().getOrDefault("customAttribute2", "RegistrationFormNumber");
    
        String attr1Value = formData.getFirst("attribute1");
        String attr2Value = formData.getFirst("attribute2");
    
        if (Validation.isBlank(attr1Value) || Validation.isBlank(attr2Value)) {
            Response challenge = context.form()
                .setAttribute("attribute1Label", customAttr1)
                .setAttribute("attribute2Label", customAttr2)
                .addError(new FormMessage("error", "Both fields are required."))
                .createForm("custom-attribute-form.ftl");
    
            context.failureChallenge(AuthenticationFlowError.INVALID_USER, challenge);
            return;
        }
    
        Map<String, String> searchParams = new HashMap<>();
        searchParams.put(customAttr1, attr1Value);
    
        List<UserModel> users = context.getSession().users()
            .searchForUserStream(context.getRealm(), searchParams)
            .filter(user -> attr2Value.equals(user.getFirstAttribute(customAttr2)))
            .collect(Collectors.toList());
    
        if (users.isEmpty()) {
            Response challenge = context.form()
                .setAttribute("attribute1Label", customAttr1)
                .setAttribute("attribute2Label", customAttr2)
                .addError(new FormMessage("error", "User not found."))
                .createForm("custom-attribute-form.ftl");
    
            context.failureChallenge(AuthenticationFlowError.INVALID_USER, challenge);
            return;
        }
    
        context.setUser(users.get(0));
        context.success();
    }

    @Override
    public boolean requiresUser() {
        return false;
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