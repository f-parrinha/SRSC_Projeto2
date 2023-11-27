package com.server;

import com.api.common.shell.StorePasswords;

import java.io.InputStream;

public record ServerConfigs(int port, InputStream configFile, String keyStorePath, String keyAlias, String trustStorePath, StorePasswords passwords) {
}
