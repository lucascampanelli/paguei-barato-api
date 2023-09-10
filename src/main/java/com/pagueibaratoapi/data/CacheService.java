package com.pagueibaratoapi.data;

import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.pagueibaratoapi.models.enumerators.CacheEnum;

/**
 * Serviço responsável por limpar o cache da aplicação.
 */
@Service
public class CacheService {
    
    private final CacheManager cacheManager;

    public CacheService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void limparTodoCache() {
        cacheManager.getCacheNames().forEach(cacheName -> cacheManager.getCache(cacheName).invalidate());
        System.out.println(cacheManager.getCacheNames().size());
    }

    public void limparCache(CacheEnum cacheName) {
        cacheManager.getCache(cacheName.name()).invalidate();
    }
}
