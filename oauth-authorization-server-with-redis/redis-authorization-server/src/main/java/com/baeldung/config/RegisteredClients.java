package com.baeldung.config;

import java.util.UUID;

import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

public class RegisteredClients {

    public static RegisteredClient messagingClient() {
        return RegisteredClient.withId(UUID.randomUUID()
            .toString())
          .clientId("articles-client")
          .clientSecret("{noop}secret")
          .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
          .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
          .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
          .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
          .authorizationGrantType(AuthorizationGrantType.DEVICE_CODE)
          .redirectUri("http://127.0.0.1:8080/login/oauth2/code/articles-client-oidc")
          .redirectUri("http://127.0.0.1:8080/authorized")
          .postLogoutRedirectUri("http://127.0.0.1:9000/login")
          .scope(OidcScopes.OPENID)
          .scope("articles.read")
          .build();
    }
}
