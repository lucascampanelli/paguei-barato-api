package com.pagueibaratoapi.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.pagueibaratoapi.models.Produto;
import com.pagueibaratoapi.repository.ProdutoRepository;

@RestController
@RequestMapping("/produto")
public class ProdutoController {
    
    private final ProdutoRepository produtoRepository;

    public ProdutoController(ProdutoRepository produtoRepository){
        this.produtoRepository = produtoRepository;
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public Produto criar(@RequestBody Produto produto){
        // Criando uma nova instância do produto para tratar o nome dele e criá-lo no banco
        Produto produtoTratado = produto;

        // Pegando cada palavra do nome do produto separado por espaço e em minúsculas
        String[] nomeProduto = produto.getNome().toLowerCase().split(" ");

        // Percorrendo cada palavra do nome do produto
        for(int i = 0; i < nomeProduto.length; i++){
            // Transformando a palavra atual em uma array
            char[] palavraArr = nomeProduto[i].toCharArray();

            // Transformando a primeira letra da palavra em maiúscula
            palavraArr[0] = nomeProduto[i].toUpperCase().charAt(0);

            // Reescreve a palavra atual com a primeira letra tratada
            nomeProduto[i] = String.valueOf(palavraArr);

            // Se for a primeira palavra sendo tratada, substitui o nome do produto pelo nome tratado
            if(i < 1)
                produtoTratado.setNome(nomeProduto[i]);
            // Se não, concatena a palavra atual ao nome do produto
            else
                produtoTratado.setNome(produtoTratado.getNome() + " " + nomeProduto[i]);
        }

        return produtoRepository.save(produtoTratado);
    }

    @GetMapping(produces = "application/json")
    public List<Produto> listar(){
        return produtoRepository.findAll();
    }

    @GetMapping("/{id}")
    public Produto ler(@PathVariable(value = "id") Integer id){
        return produtoRepository.findById(id).get();
    }

}
