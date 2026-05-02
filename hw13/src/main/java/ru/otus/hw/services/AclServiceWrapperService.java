package ru.otus.hw.services;

import org.springframework.security.acls.model.Permission;

public interface AclServiceWrapperService {

    void createPermission(Object object, Permission permission);
    
    void createPermission(Object object, Permission permission, String username);
    
    void createPermissionForRole(Object object, Permission permission, String role);
    
    void deletePermission(Object object, Permission permission);
    
    void deleteAcl(Object object);
}