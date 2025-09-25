package org.shopme.admin.service;

import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class CacheService {
    private final CacheManager manager;

    public CacheService(CacheManager manager) {
        this.manager = manager;
    }

    public void clearCache(String name) {
        var cache = manager.getCache(name);
        if (cache != null) {
            cache.clear();
        }
    }

    public void clearAllCache() {
        manager.getCacheNames().forEach(name -> {
            var cache = manager.getCache(name);
            if (cache != null) {
                cache.clear();
            }
        });
    }
}
