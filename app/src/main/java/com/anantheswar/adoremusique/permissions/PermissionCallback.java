package com.anantheswar.adoremusique.permissions;

public interface PermissionCallback {
    void permissionGranted();

    void permissionRefused();
}