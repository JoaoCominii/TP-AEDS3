# Formulário - TP PARTE 3
1. Qual foi o relacionamento N:N escolhido e quais tabelas ele conecta?
   > O relacionamento N:N escolhido foi entre as tabelas `Compra` e `Jogo`. Uma compra pode conter vários jogos, e um mesmo jogo pode estar presente em várias compras diferentes. Para conectar as duas, foi criada uma tabela associativa chamada `CompraJogo`.

2. Qual estrutura de índice foi utilizada (B+ ou Hash Extensível)? Justifique a escolha.
   > Foi utilizada a estrutura de **Hash Extensível**. A escolha se justifica porque o principal requisito para a navegação no relacionamento N:N é a busca por chaves específicas (o ID de uma compra ou o ID de um jogo). O Hash Extensível oferece uma complexidade de busca de O(1) em média para essas operações, o que é ideal para encontrar rapidamente todos os registros associados a uma entidade específica. Uma Árvore B+ seria mais apropriada para buscas por faixa de valores, o que não é o caso aqui. Foram criados dois índices hash: um para mapear `idCompra -> List<idJogo>` e outro para `idJogo -> List<idCompra>`, permitindo buscas eficientes em ambas as direções do relacionamento.

3. Como foi implementada a chave composta da tabela intermediária?
   > A tabela intermediária `CompraJogo` utiliza uma **chave primária composta** formada por `(idCompra, idJogo)`. Essa chave não é um campo único no arquivo, mas sim a combinação dos dois atributos. Para gerenciar a persistência, foi criada uma classe `ArquivoCompraJogo.java` não genérica, que lida com a leitura e escrita dos registros `CompraJogo`. Como não há um ID único, operações como `read`, `update` e `delete` precisam iterar pelo arquivo para encontrar o registro que corresponde à combinação `(idCompra, idJogo)`. A unicidade da chave composta é garantida na camada de aplicação antes da inserção.

4. Como é feita a busca eficiente de registros por meio do índice?
   > A busca é feita de forma muito eficiente usando os índices de Hash Extensível. Por exemplo, para encontrar todos os jogos de uma determinada compra:
   > 1. O `idCompra` é usado como chave no primeiro índice hash (`indicePorCompra`).
   > 2. O hash retorna instantaneamente (O(1)) uma `List<Integer>` contendo todos os `idJogo` associados àquela compra.
   > 3. O sistema então itera sobre essa lista de IDs de jogos, buscando os dados completos de cada jogo na `JogoDAO`.
   > O processo é análogo ao buscar todas as compras de um determinado jogo: o `idJogo` é usado no segundo índice (`indicePorJogo`) para obter uma lista de `idCompra`. Essa abordagem é muito mais rápida do que varrer o arquivo `comprajogo.db` inteiro.

5. Como o sistema trata a integridade referencial (remoção/atualização) entre as tabelas?
   > A integridade referencial é garantida na camada de aplicação através de uma implementação de **exclusão em cascata (cascade delete)**.
   > - **Ao excluir uma `Compra`**: O método `excluir` em `CompraDAO` foi modificado. Antes de remover o registro da compra, ele consulta o índice `IndiceCompraJogo` para obter a lista de todos os `idJogo` relacionados àquela compra. Em seguida, ele chama o método `delete(idCompra, idJogo)` da `CompraJogoDAO` para cada par, removendo tanto o registro do arquivo quanto as entradas nos dois índices.
   > - **Ao excluir um `Jogo`**: Da mesma forma, o método `excluir` em `JogoDAO` primeiro consulta o índice para obter a lista de `idCompra` associados, e então remove as entradas correspondentes na tabela `CompraJogo`.
   > Isso assegura que não haverá registros "órfãos" na tabela associativa.

6. Como foi organizada a persistência dos dados dessa nova tabela (mesmo padrão de
cabeçalho e lápide)?
   > A persistência dos dados foi organizada em um novo arquivo binário (`dados/comprajogo.db`), mas com uma abordagem diferente da classe genérica `Arquivo<T>`, já que não há um ID único. A nova classe `ArquivoCompraJogo.java` gerencia este arquivo.
   > - **Cabeçalho**: O cabeçalho deste arquivo não precisa armazenar o "último ID", pois não há um campo de ID auto-incremental.
   > - **Lápide**: O padrão de lápide foi mantido. Cada registro no arquivo é precedido por um byte de "lápide" (`*` para excluído, ` ` para ativo) e um `int` que indica o tamanho do registro. A exclusão ainda é lógica, mas a reutilização de espaço não foi implementada nesta classe específica, focando na simplicidade da operação com chave composta. As operações de busca, atualização e exclusão precisam percorrer o arquivo sequencialmente para encontrar os registros correspondentes.

7. Descreva como o código da tabela intermediária se integra com o CRUD das tabelas
principais.
   > A integração do código da tabela intermediária (`CompraJogoDAO`) com o CRUD das tabelas principais (`CompraDAO` e `JogoDAO`) é focada principalmente na manutenção da integridade referencial durante as operações de exclusão (Delete).
   > - **Create e Update**: As operações de criação e alteração nas tabelas `Compra` e `Jogo` não afetam diretamente a tabela `CompraJogo`. A associação entre um jogo e uma compra é uma operação separada, realizada através do menu de compras.
   > - **Read**: A leitura de um registro de `Compra` ou `Jogo` também não carrega automaticamente os dados da tabela associativa. A busca dos itens relacionados é uma ação explícita do usuário (ex: "Listar jogos da compra").
   > - **Delete (Exclusão em Cascata)**: Esta é a integração mais importante.
   >   - Ao chamar `CompraDAO.excluir(idCompra)`, o método primeiro usa o `IndiceCompraJogo` para encontrar todos os jogos (`idJogo`) associados a essa compra. Em seguida, ele chama `CompraJogoDAO.delete(idCompra, idJogo)` para cada associação, removendo os registros da tabela intermediária e dos índices antes de apagar a compra em si.
   >   - O mesmo processo ocorre em `JogoDAO.excluir(idJogo)`, que remove todas as suas associações com compras antes de se apagar.
   > Essa abordagem, conhecida como exclusão em cascata na camada de aplicação, garante que não haverá registros órfãos na tabela `CompraJogo`.

8. Descreva como está organizada a estrutura de diretórios e módulos no repositório após esta fase.
   > Após a implementação do relacionamento N:N, a estrutura de diretórios e módulos foi expandida para acomodar as novas funcionalidades, mantendo a organização existente:
   > - **`dao/`**: O diretório de acesso a dados foi o que mais sofreu alterações. Agora ele contém:
   >   - `ArquivoCompraJogo.java`: Uma nova classe para persistir a entidade `CompraJogo`, que possui chave primária composta.
   >   - `HashExtensivel.java`: A implementação original do hash, mantida para os relacionamentos 1:N existentes.
   >   - `HashExtensivelNN.java`: Uma nova classe de hash extensível, genérica, criada especificamente para o relacionamento N:N.
   >   - `IndiceCompraJogo.java`: O novo arquivo de índice que gerencia duas instâncias de `HashExtensivelNN` para mapear o relacionamento `Compra <-> Jogo`.
   >   - `CompraJogoDAO.java`: O DAO que orquestra as operações na tabela associativa.
   > - **`model/`**: Contém as classes de domínio. `CompraJogo.java` foi ajustada para refletir a chave composta (sem um `id` próprio).
   > - **`view/`**: As classes de interface com o usuário `MenuCompras.java` e `MenuJogos.java` foram atualizadas com novas opções para criar e listar as associações N:N.
   > - **`dados/`**: O diretório de dados agora contém os novos arquivos de persistência:
   >   - `dados/comprajogo/comprajogo.db`: Arquivo de dados da tabela associativa.
   >   - `dados/compras/indice_compra_jogo.db`: Índice que mapeia compras para jogos.
   >   - `dados/jogos/indice_jogo_compra.db`: Índice que mapeia jogos para compras.
   > Os demais diretórios (`util/`, `teste/`, `img/`) mantiveram sua estrutura original.
