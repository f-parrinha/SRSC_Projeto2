package com.api.common.tls;

import com.api.common.shell.Shell;
import org.springframework.boot.web.server.Ssl;

import java.util.Arrays;


/**
 * Class  TLSServerConfig  configures an SSL Context with custom configs for a Tomcat servlet, used by Spring
 *
 * @author Martin Magdalinchev  58172
 * @author Francisco Parrinha   58360
 */
public class TLSServerConfig extends AbstractTLSConfig implements TLSConfig<Ssl> {

    /** Variables */
    private String keyAlias;
    private String keyPass;
    private Ssl sslContext;


    @Override
    public Ssl getSslContext() {
        return sslContext;
    }
    @Override
    public void setSslContext(Ssl context) { sslContext = context; }
    public void setKeyAlias(String keyAlias) {
        this.keyAlias = keyAlias;
    }
    public void setKeyPass(String keyPass) {
        this.keyPass = keyPass;
    }

    @Override
    public Ssl buildSslContext() {
        Ssl ssl = new Ssl();
        ssl.setEnabled(true);
        ssl.setKeyStoreType(STORE_TYPE);
        ssl.setKeyStore(keyStorePath);
        ssl.setKeyStorePassword(keyStorePass);
        ssl.setKeyAlias(keyAlias);
        ssl.setKeyPassword(keyPass);
        ssl.setTrustStoreType(STORE_TYPE);
        ssl.setTrustStore(trustStorePath);
        ssl.setTrustStorePassword(trustStorePass);
        ssl.setClientAuth(auth == AuthType.SERV ? Ssl.ClientAuth.NONE : Ssl.ClientAuth.WANT);
        ssl.setProtocol(TLS_PROTOCOL);
        ssl.setEnabledProtocols(protocols);
        if (ciphers.length != 0 ) ssl.setCiphers(ciphers);

        return ssl;
    }

    @Override
    public void printSSLConfigs() {
        Shell.printDebug("SSL Configurations:");
        Shell.printDebug(" - Protocol : " + Arrays.toString(sslContext.getEnabledProtocols()));
        Shell.printDebug(" - Ciphers  : " + Arrays.toString(sslContext.getCiphers()));
    }
}
