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
        String customAttr1 = context.getAuthenticatorConfig().getConfig()
                .getOrDefault("customAttribute1", "ContractNumber");
        String customAttr2 = context.getAuthenticatorConfig().getConfig()
                .getOrDefault("customAttribute2", "RegistrationFormNumber");

        logger.info("Using Custom Attributes: " + customAttr1 + ", " + customAttr2);

        Response challenge = createFormResponse(context, customAttr1, customAttr2, null);
        context.challenge(challenge);
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();

        String customAttr1 = context.getAuthenticatorConfig().getConfig()
                .getOrDefault("customAttribute1", "ContractNumber");
        String customAttr2 = context.getAuthenticatorConfig().getConfig()
                .getOrDefault("customAttribute2", "RegistrationFormNumber");

        String attr1Value = formData.getFirst("attribute1");
        String attr2Value = formData.getFirst("attribute2");

        if (Validation.isBlank(attr1Value) || Validation.isBlank(attr2Value)) {
            Response challenge = createFormResponse(context, customAttr1, customAttr2,
                    new FormMessage("error",
                            context.form().getMessage("error.bothFieldsRequired")));
            context.failureChallenge(AuthenticationFlowError.INVALID_USER, challenge);
            return;
        }

        Map<String, String> searchParams = new HashMap<>();
        searchParams.put(customAttr1, attr1Value);
        searchParams.put(customAttr2, attr2Value);

        List<UserModel> users = context.getSession().users()
                .searchForUserStream(context.getRealm(), searchParams)
                .filter(user -> attr1Value.equals(user.getFirstAttribute(customAttr1)) &&
                        attr2Value.equals(user.getFirstAttribute(customAttr2)))
                .collect(Collectors.toList());

        var user = handleUsers(context, users, customAttr1, customAttr2);
        if (user == null) {
            Response challenge = createFormResponse(context, customAttr1, customAttr2,
                    new FormMessage("error",
                            context.form().getMessage("error.oneOrMoreAttributesIncorrect")));
            context.failureChallenge(AuthenticationFlowError.INVALID_USER, challenge);
            return;
        }
        logger.info("Inactive supplier found: " + user.getEmail());

        if (user.getRequiredActionsStream()
                .anyMatch(action -> action.equals(UserModel.RequiredAction.UPDATE_PASSWORD.name()))) {
            logger.info("user has update pass required action: " + user.getEmail());
            context.setUser(user);
            context.success();
        } else {
            Response challenge = createFormResponse(context, customAttr1, customAttr2,
                    new FormMessage("error",
                            context.form().getMessage("error.supplierAlreadyActivated")));
            context.failureChallenge(AuthenticationFlowError.INVALID_USER, challenge);
        }

    }

    private Response createFormResponse(AuthenticationFlowContext context, String customAttr1, String customAttr2,
            FormMessage errorMessage) {
        var formBuilder = context.form()
                .setAttribute("attribute1Label", customAttr1)
                .setAttribute("attribute2Label", customAttr2);
        if (errorMessage != null) {
            formBuilder.setError(errorMessage.getMessage());
        }
        return formBuilder.createForm("custom-attribute-form.ftl");
    }

    private UserModel handleUsers(AuthenticationFlowContext context, List<UserModel> users, String customAttr1,
            String customAttr2) {
        UserModel user = null;
        switch (users.size()) {
            case 1:
                user = users.get(0);
                getLogs(user);
                break;
            case 2:
                var selectedUser = validateUsers(context, users);
                logger.info("selected user from 2 is the delegate with mail: " + selectedUser.getEmail());
                user = selectedUser;
                getLogs(user);
                break;
            default:
                user = null;
        }
        return user;
    }

    private UserModel validateUsers(AuthenticationFlowContext context, List<UserModel> users) {
        return users.stream()
                .filter(user -> "Delegate".equals(user.getFirstAttribute("UserType")))
                .findFirst()
                .orElse(null);
    }

    private void getLogs(UserModel user) {
        if (user != null) {
            logger.info("handle user: " + user.getEmail());
        } else {
            logger.info("user is null");
        }
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