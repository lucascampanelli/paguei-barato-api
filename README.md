![PagueiBaratoLogo](https://user-images.githubusercontent.com/56810073/176563856-105698e9-6258-463b-96fc-3032660bf308.png)
# PagueiBarato API
made with ❤ in 🇧🇷

<br>

<img src="https://img.shields.io/github/v/release/lucascampanelli/paguei-barato-api"/>
<hr>

<p>O PagueiBarato é uma API RESTful de busca e comparação de preços de produtos em diversos mercados varejistas e atacadistas, desenvolvida com a framework Java Spring Boot. Encontre o melhor preço do produto que desejar, compare preços entre lojas e cadastre ofertas imperdíveis dos estabelecimentos que frequenta.</p>

<br>

<p>A API segue os critérios para o design RESTful, oferecendo um serviço fácil e rápido de ser consumido.</p>

# Como usar?

<p>Com a API PagueiBarato você pode informar quanto cada produto está custando em um determinado mercado, ou seja, você pode <strong>sugerir um preço</strong> para o produto.</p>

<p>Além disso, é possível verificar em quais mercados um produto pode ser encontrado.</p>

<br>

<p>Existem <strong>9 rotas</strong> nas quais pode-se interagir com a API:</p>

<ul>
    <li>
        <strong>/categoria</strong> - Refere-se às ações realizáveis sobre o recurso da categoria do produto;
    </li>
    <li>
        <strong>/estoque</strong> - Concerne às ações realizáveis sobre o estoque de um mercado, isto é, a relação entre as chaves primárias de um <em>Mercado</em> e de um <em>Produto</em>;
    </li>
    <li>
        <strong>/mercado</strong> - Rota referente às ações realizáveis sobre o recurso do mercado;
    </li>
    <li>
        <strong>/produto</strong> - Rota referente às ações realizáveis sobre o recurso do produto;
    </li>
    <li>
        <strong>/ramo</strong> - Refere-se às ações realizáveis sobre o recurso do ramo de um mercado;
    </li>
    <li>
        <strong>/sugestao</strong> - Referente às ações realizáveis sobre o recurso da sugestão. A sugestão é uma <em>indicação de preço</em> feita por um usuário acerca de um produto em um determinado mercado;
    </li>
    <li>
        <strong>/usuario</strong> - Refere-se às ações realizáveis sobre o recurso do usuário;
    </li>
    </li>
        <strong>/login</strong> - Concerne à rota para criação de uma sessão, ou seja, para realização de login pelo usuário, de modo que seja possível obter o token de acesso às rotas protegidas;
    </li>
    <li>
        <strong>/</strong> - Rota inicial onde é obtida todas as rotas possíveis para realizar a manipulação e busca dos recursos.
    </li>
</ul>

<br>

## Cadastrando um usuário
<p>Para realizar algumas ações, como criação de produtos, mercados e sugestões, é necessário ter um usuário cadastrado e estar autenticado.</p>
<p>Para cadastrar um novo usuário, é necessário realizar uma requisição com o método <strong>POST</strong> à rota <strong>/usuario</strong> enviando no <em>corpo</em> os dados do usuário. </p>
<p>Exemplo de corpo de requisição para criação de um usuário:</p>

<code>
{<br>
    "nome": "John Doe",<br>
    "email": "john.public-doe@email.com",<br>
    "senha": "mYP4s5W0Rd1sV3rYS3CR3t!!!",<br>
    "logradouro": "Rua Brasil",<br>
    "numero": 12,<br>
    "complemento": "Apart. 22",<br>
    "bairro": "Itaquera",<br>
    "cidade": "São Paulo",<br>
    "uf": "SP",<br>
    "cep": "03367-074"<br>
}
</code>

<br>

## Autenticando
<p>Para criar ou obter alguns recursos, é necessário estar com algum usuário autenticado e enviar em cada requisição o token obtido na autenticação.</p>
<p>Para realizar a Autenticação é necessário fazer uma requisição <strong>POST</strong> na rota <strong>/login</strong> enviando no <em>corpo</em> o <strong>email</strong> e a <strong>senha</strong> do usuário.</p>
<p>Exemplo de corpo de requisição para criação de uma sessão de autenticação:</p>

<code>
{<br>
    "email": "root@email.com",<br>
    "senha": "R0ot!!!123"<br>
}
</code>

<br>

<p>Como resposta, você obterá um token que deverá ser enviado no cabeçalho <strong>Authorization</strong> junto com o prefixo "Bearer". Por exemplo:<p>
<p><em><strong>Authorization:</strong> Bearer T0K3Ng3r4d0</em></p>

<br>

## Sugerindo um preço
<p>Se você quiser indicar o preço de um produto em um mercado, você deverá criar uma sugestão de preço. Para criar uma sugestão, basta fazer uma requisição com o método <strong>POST</strong> para a rota <strong>/sugestao</strong> enviando no <em>corpo</em> o <strong>preço</strong> - com o valor decimal -, o <strong>id do estoque</strong> e o <strong>id do usuário</strong> que está sugerindo o preço.</p>

> <em>OBS: Para criar um estoque é necessário estar autenticado.</em>

<br>

# Construída usando
<ul>
<li>
    <img width=100 src="https://img.shields.io/badge/Spring_Boot-F2F4F9?style=for-the-badge&logo=spring-boot"> - Framework
</li>
<li>
    <img width=100 src="https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white"> - Banco de dados
</li>
<li>
    <img width=100 src="https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=Hibernate&logoColor=white"> - ORM
</li>
<li>
    <img width=100 src="https://img.shields.io/badge/apache_maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white"> - Gestão de dependências
</li>
<li>
    <img width=100 src="https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=JSON%20web%20tokens&logoColor=white"> - Implementação do JWT para o Java
</li>
<li>
    <img width=100 src="https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=Postman&logoColor=white"> - Cliente teste da API e Documentação
</li>
<li>
    <img width=100 src="https://img.shields.io/badge/Heroku-430098?style=for-the-badge&logo=heroku&logoColor=whitee"> - Deploy para desenv. e homologação
</li>
<li>
    <img width=100 src="https://img.shields.io/badge/VSCode-0078D4?style=for-the-badge&logo=visual%20studio%20code&logoColor=white"> - Editor de código / IDE
</li>
<li>
    <img width=100 src="https://img.shields.io/badge/GIT-E44C30?style=for-the-badge&logo=git&logoColor=white"> - Controle de versões
</li>
<li>
    <img width=100 src="https://img.shields.io/badge/Inkscape-000000?style=for-the-badge&logo=Inkscape&logoColor=white"> - Ilustrações
</li>
</ul>

<br><br><br>
🏗🚧 EM CONSTRUÇÃO 🚧🏗
