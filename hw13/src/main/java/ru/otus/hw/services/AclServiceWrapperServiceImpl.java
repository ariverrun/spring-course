package ru.otus.hw.services;

import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
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
        var sid = new PrincipalSid(authentication);
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

    @Override
    @Transactional
    public void deleteAcl(Object object) {
        mutableAclService.deleteAcl(new ObjectIdentityImpl(object), false);
    }
}