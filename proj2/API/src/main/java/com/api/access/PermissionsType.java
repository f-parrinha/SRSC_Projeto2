package com.api.access;

/**
 *
 */
public enum PermissionsType {
    ALLOW_READ_WRITE("allow read write"),
    ALLOW_READ("allow read"),
    DENY("deny");

    private final String value;

    PermissionsType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PermissionsType fromString(String value) {
        for (PermissionsType accessLevel : PermissionsType.values()) {
            if (accessLevel.getValue().equalsIgnoreCase(value.trim())) {
                return accessLevel;
            }
        }
        throw new IllegalArgumentException("Invalid access level: " + value);
    }

    /**
     *
     * @return
     */
    public boolean canRead() {
        return this == ALLOW_READ || this == ALLOW_READ_WRITE;
    }

    /**
     *
     * @return
     */
    public boolean canWrite() {
        return this == ALLOW_READ_WRITE;
    }
}