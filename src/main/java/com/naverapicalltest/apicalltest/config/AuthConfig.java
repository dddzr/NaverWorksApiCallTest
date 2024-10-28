package com.naverapicalltest.apicalltest.config;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

@Component
public class AuthConfig {
    @Value("${auth.client_id}")
    private String clientId;

    @Value("${auth.client_secret}")
    private String clientSecret;

    @Value("${auth.redirect_uri}")
    private String redirectUri;

    @Value("${auth.scope}")
    private String scope;

    @Value("${auth.server_account}")
    private String serverAccount;

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public String getScope() {
        return scope;
    }

    public String getServerAccount() {
        return serverAccount;
    }
}