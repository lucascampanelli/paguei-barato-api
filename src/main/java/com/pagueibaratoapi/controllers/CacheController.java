package com.pagueibaratoapi.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pagueibaratoapi.data.CacheService;
import com.pagueibaratoapi.models.enumerators.CacheEnum;
import com.pagueibaratoapi.models.responses.ResponseCache;

/**
 * Classe responsável por fazer o controle sob demanda do cache da aplicação.
 */
@RestController
@RequestMapping("/cache-requisicoes")
public class CacheController {
    
    private final CacheService cacheService;
    
    public CacheController(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    /**
     * Rota para limpar todo o cache da aplicação.
     */
    @GetMapping("/limpar")
    public ResponseCache limparCache() {
        cacheService.limparTodoCache();
        return new ResponseCache("Todos os dados do cache foram limpos.");
    }

    /**
     * Rota para limpar o cache de categorias.
     */
    @GetMapping("/limpar/categorias")
    public ResponseCache limparCacheCategorias() {
        cacheService.limparCache(CacheEnum.Categorias);
        return new ResponseCache("Todos os dados do cache de categorias foram limpos.");
    }

    /**
     * Rota para limpar o cache de estoques.
     */
    @GetMapping("/limpar/estoques")
    public ResponseCache limparCacheEstoques() {
        cacheService.limparCache(CacheEnum.Estoques);
        return new ResponseCache("Todos os dados do cache de estoques foram limpos.");
    }

    /**
     * Rota para limpar o cache de mercados.
     */
    @GetMapping("/limpar/mercados")
    public ResponseCache limparCacheMercados() {
        cacheService.limparCache(CacheEnum.Mercados);
        return new ResponseCache("Todos os dados do cache de mercados foram limpos.");
    }

    /**
     * Rota para limpar o cache dos relacionamentos entre mercados e produtos.
     */
    @GetMapping("/limpar/mercado-produtos")
    public ResponseCache limparCacheMercadoProdutos() {
        cacheService.limparCache(CacheEnum.MercadoProdutos);
        return new ResponseCache("Todos os dados do cache dos relacionamentos entre mercados e produtos foram limpos.");
    }

    /**
     * Rota para limpar o cache dos relacionamentos entre mercados e sugestões.
     */
    @GetMapping("/limpar/mercado-sugestoes")
    public ResponseCache limparCacheMercadoSugestoes() {
        cacheService.limparCache(CacheEnum.MercadoSugestoes);
        return new ResponseCache("Todos os dados do cache dos relacionamentos entre mercados e sugestões foram limpos.");
    }

    /**
     * Rota para limpar o cache de produtos.
     */
    @GetMapping("/limpar/produtos")
    public ResponseCache limparCacheProdutos() {
        cacheService.limparCache(CacheEnum.Produtos);
        return new ResponseCache("Todos os dados do cache de produtos foram limpos.");
    }

    /**
     * Rota para limpar o cache de ramos.
     */
    @GetMapping("/limpar/ramos")
    public ResponseCache limparCacheRamos() {
        cacheService.limparCache(CacheEnum.Ramos);
        return new ResponseCache("Todos os dados do cache de ramos foram limpos.");
    }

    /**
     * Rota para limpar o cache de sugestoes.
     */
    @GetMapping("/limpar/sugestoes")
    public ResponseCache limparCacheSugestoes() {
        cacheService.limparCache(CacheEnum.Sugestoes);
        return new ResponseCache("Todos os dados do cache de sugestoes foram limpos.");
    }

}
