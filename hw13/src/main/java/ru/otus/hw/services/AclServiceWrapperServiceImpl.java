package ru.otus.hw.services;

import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AclServiceWrapperServiceImpl implements AclServiceWrapperService {

    private final MutableAclService mutableAclService;

    @Override
    @Transactional
    public void createPermission(Object object, Permission permission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        createPermissionInternal(object, permission, new PrincipalSid(authentication));
    }

    @Override
    @Transactional
    public void createPermission(Object object, Permission permission, String username) {
        createPermissionInternal(object, permission, new PrincipalSid(username));
    }

    @Override
    @Transactional
    public void createPermissionForRole(Object object, Permission permission, String role) {
        createPermissionInternal(object, permission, new GrantedAuthoritySid(role));
    }

    @Override
    @Transactional
    public void deletePermission(Object object, Permission permission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        deletePermissionInternal(object, permission, new PrincipalSid(authentication));
    }

    @Override
    @Transactional
    public void deleteAcl(Object object) {
        mutableAclService.deleteAcl(new ObjectIdentityImpl(object), false);
    }

    private void createPermissionInternal(Object object, Permission permission, Sid sid) {
        ObjectIdentity oid = new ObjectIdentityImpl(object);
        
        MutableAcl acl;
        try {
            acl = (MutableAcl) mutableAclService.readAclById(oid);
        } catch (Exception e) {
            acl = mutableAclService.createAcl(oid);
        }
        
        acl.insertAce(acl.getEntries().size(), permission, sid, true);
        mutableAclService.updateAcl(acl);
    }

    private void deletePermissionInternal(Object object, Permission permission, Sid sid) {
        ObjectIdentity oid = new ObjectIdentityImpl(object);
        MutableAcl acl = (MutableAcl) mutableAclService.readAclById(oid);
        
        acl.getEntries().stream()
            .filter(ace -> ace.getPermission().equals(permission) && ace.getSid().equals(sid))
            .findFirst()
            .ifPresent(ace -> acl.deleteAce(acl.getEntries().indexOf(ace)));
        
        mutableAclService.updateAcl(acl);
    }
}