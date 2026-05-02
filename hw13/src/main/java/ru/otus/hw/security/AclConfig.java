package ru.otus.hw.security;

import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.acls.AclPermissionCacheOptimizer;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.AclAuthorizationStrategyImpl;
import org.springframework.security.acls.domain.ConsoleAuditLogger;
import org.springframework.security.acls.domain.DefaultPermissionGrantingStrategy;
import org.springframework.security.acls.domain.SpringCacheBasedAclCache;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
public class AclConfig {
    private final long ACL_ENTRY_LIFE_TIME_MINUTES = 30;

    private final long ACL_CACHE_MAX_SIZE = 1000;

    @Bean
    @SuppressWarnings("null")
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("aclCache");
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .expireAfterWrite(ACL_ENTRY_LIFE_TIME_MINUTES, TimeUnit.MINUTES)
            .maximumSize(ACL_CACHE_MAX_SIZE)
            .recordStats());
        return cacheManager;
    }

    @Bean
    public SpringCacheBasedAclCache aclCache(
        CacheManager cacheManager,
        PermissionGrantingStrategy permissionGrantingStrategy,
        AclAuthorizationStrategy aclAuthorizationStrategy
    ) {
        return new SpringCacheBasedAclCache(
            cacheManager.getCache("aclCache"),
            permissionGrantingStrategy,
            aclAuthorizationStrategy
        );
    }

    @Bean
    public PermissionGrantingStrategy permissionGrantingStrategy() {
        return new DefaultPermissionGrantingStrategy(new ConsoleAuditLogger());
    }

    @Bean
    public AclAuthorizationStrategy aclAuthorizationStrategy() {
        return new AclAuthorizationStrategyImpl(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    @Bean
    public LookupStrategy lookupStrategy(
        DataSource dataSource,
        AclCache aclCache,
        AclAuthorizationStrategy aclAuthorizationStrategy,
        PermissionGrantingStrategy permissionGrantingStrategy
    ) {
        return new BasicLookupStrategy(
            dataSource,
            aclCache,
            aclAuthorizationStrategy,
            permissionGrantingStrategy
        );
    }

    @Bean
    public MutableAclService aclService(
        DataSource dataSource,
        LookupStrategy lookupStrategy,
        AclCache aclCache
    ) {
        return new JdbcMutableAclService(dataSource, lookupStrategy, aclCache);
    }

    @Bean
    public MethodSecurityExpressionHandler defaultMethodSecurityExpressionHandler(MutableAclService aclService) {
        var expressionHandler = new DefaultMethodSecurityExpressionHandler();
        var permissionEvaluator = new AclPermissionEvaluator(aclService);
        expressionHandler.setPermissionEvaluator(permissionEvaluator);
        expressionHandler.setPermissionCacheOptimizer(new AclPermissionCacheOptimizer(aclService));
        return expressionHandler;
    }
}