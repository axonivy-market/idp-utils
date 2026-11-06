package com.axonivy.utils.idp.test;

import static com.axonivy.utils.e2etest.enums.E2EEnvironment.REAL_SERVER;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.axonivy.utils.idp.test.constants.IdpTestConstants;
import com.axonivy.utils.e2etest.utils.E2ETestUtils;

import ch.ivyteam.ivy.environment.AppFixture;

public abstract class BaseSetup {

    protected static final String CLIENT_ID = "IDP";
    protected boolean isRealTest;

    @BeforeEach
    void beforeEach(ExtensionContext context, AppFixture fixture) {
        isRealTest = context.getDisplayName().equals(REAL_SERVER.getDisplayName());
        E2ETestUtils.determineConfigForContext(context.getDisplayName(), runRealEnv(fixture), runMockEnv(fixture));
    }

    private Runnable runRealEnv(AppFixture fixture) {
        return () -> {
            String apiKeySecret = System.getProperty(IdpTestConstants.API_KEY_SECRET);
            fixture.var("idpUtils.apiKeySecret", apiKeySecret);
        };
    }

    private Runnable runMockEnv(AppFixture fixture) {
        return () -> {
            fixture.var("idpUtils.apiUrl", "TESTHOSTURL");
            fixture.var("idpUtils.apiKeySecret", "TESTKEY");
            fixture.var("idpUtils.waitFor", "120");
            fixture.config("RestClients." + CLIENT_ID + ".Url",
                    "http://{ivy.engine.host}:{ivy.engine.http.port}/{ivy.request.application}/api/idpMock");
        };
    }
}
