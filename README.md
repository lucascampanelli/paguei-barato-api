![PagueiBaratoLogo](https://user-images.githubusercontent.com/56810073/176563856-105698e9-6258-463b-96fc-3032660bf308.png)
# PagueiBarato API
made with ❤ in 🇧🇷

<br>

<img src="https://img.shields.io/github/v/release/lucascampanelli/paguei-barato-api"/>
<hr>

<p>O PagueiBarato é uma API RESTful de busca e comparação de preços de produtos em diversos mercados varejistas e atacadistas, desenvolvida com a framework Java Spring Boot. Encontre o melhor preço do produto que desejar, compare preços entre lojas e cadastre ofertas imperdíveis dos estabelecimentos que frequenta.</p>

<br>

<p>A API segue os critérios para o design RESTful, oferecendo um serviço fácil e rápido de ser consumido.</p>

<br>

# Sumário

- [Como usar?](#comoUsar)
    - [Configurando o ambiente](#configurando)
    - [Cadastrando um usuário](#cadastrandoUsuario)
    - [Autenticando](#autenticando)
    - [Sugerindo um preço](#sugerindoPreco)
- [Construída usando](#construidaUsando)

<br>

# Como usar? <a name = "comoUsar"></a>

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
        <strong>/</strong> - Rota inicial onde são obtidas todas as rotas possíveis para realizar a manipulação e busca dos recursos.
    </li>
</ul>

<br>

## Configurando o ambiente <a name = "configurando"></a>
É necessário definir algumas variáveis de ambiente para rodar a API. Para fazer isso corretamente, siga as seguintes etapas:
<ol>
    <li>Acesse o diretório <strong>src > main > resources</strong></li>
    <li>Renomeie o arquivo <strong>application-example.properties</strong> para <strong>application.properties</strong></li>
    <li>Altere as informações do banco de dados para as informações do seu ambiente, como nome do banco, host, usuário e senha.</li>
</ol>

<br>

> <em>OBS: O arquivo SQL para criação da base de dados POSTGRESQL encontra-se <a href="https://github.com/lucascampanelli/paguei-barato-api/blob/master/assets/api-pagueibarato-db.sql">aqui</a>. </em>

<br>

## Cadastrando um usuário <a name = "cadastrandoUsuario"></a>
<p>Para realizar algumas ações, como criação de produtos, mercados e sugestões, é necessário ter um usuário cadastrado e estar autenticado.</p>
<p>Para cadastrar um novo usuário, é necessário realizar uma requisição com o método <strong>POST</strong> à rota <strong>/usuario</strong> enviando no <em>corpo</em> os dados do usuário. </p>
<p>Exemplo de corpo de requisição para criação de um usuário:</p>

<code>
{
    "nome": "John Doe",
    "email": "john.public-doe@email.com",
    "senha": "mYP4s5W0Rd1sV3rYS3CR3t!!!",
    "logradouro": "Rua Brasil",
    "numero": 12,
    "complemento": "Apart. 22",
    "bairro": "Itaquera",
    "cidade": "São Paulo",
    "uf": "SP",
    "cep": "03367-074"
}
</code>

<br>

## Autenticando <a name = "autenticando"></a>
<p>Para criar ou obter alguns recursos, é necessário estar com algum usuário autenticado e enviar em cada requisição o token obtido na autenticação.</p>
<p>Para realizar a Autenticação é necessário fazer uma requisição <strong>POST</strong> na rota <strong>/login</strong> enviando no <em>corpo</em> o <strong>email</strong> e a <strong>senha</strong> do usuário.</p>
<p>Exemplo de corpo de requisição para criação de uma sessão de autenticação:</p>

<code>
{
    "email": "john.public-doe@email.com",
    "senha": "mYP4s5W0Rd1sV3rYS3CR3t!!!"
}
</code>

<br>

<p>Como resposta, você obterá um token que deverá ser enviado no cabeçalho <strong>Authorization</strong> junto com o prefixo "Bearer" em todas as requisições às rotas protegidas. Por exemplo:<>
<p><em><strong>Authorization:</strong> Bearer T0K3Ng3r4d0</em></p>

<br>

## Sugerindo um preço <a name = "sugerindoPreco"></a>
<p>Se você quiser indicar o preço de um produto em um mercado, você deverá criar uma sugestão de preço. Para criar uma sugestão, basta fazer uma requisição com o método <strong>POST</strong> para a rota <strong>/sugestao</strong> enviando no <em>corpo</em> o <strong>preço</strong> - com o valor decimal -, o <strong>id do estoque</strong> e o <strong>id do usuário</strong> que está sugerindo o preço.</p>

<br>

> <em>OBS: É necessário estar autenticado para criar um estoque.</em>

<br>

# Construída usando <a name = "construidaUsando"></a>
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
    <img width=100 src="https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=Spring-Security&logoColor=white"> - Segurança
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
