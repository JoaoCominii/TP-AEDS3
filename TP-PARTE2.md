
# Formulário - TP PARTE 2

a) Qual a estrutura usada para representar os registros?

- Persistência: arquivo binário por entidade em `dados/<entidade>/<entidade>.db`.
- Cabeçalho do arquivo: 4 bytes (int) para o último ID usado + 8 bytes (long) para o ponteiro para a lista de espaços deletados (free-list). Total = 12 bytes.
- Layout de cada registro no arquivo:
	- 1 byte: lápide (' ' espaço = ativo, '*' = excluído)
	- 2 bytes: tamanho do bloco de dados (short)
	- N bytes: payload = byte[] retornado por `toByteArray()` da entidade
- Representação em memória/serialização: cada modelo implementa `Registro` com os métodos `toByteArray()` e `fromByteArray(byte[])`. Esses métodos controlam exatamente a ordem e o tipo dos campos gravados (por exemplo: id, clienteId, campos UTF, longs para datas etc.).

b) Como atributos multivalorados do tipo string foram tratados?

- Estratégia: gravamos um inteiro com a quantidade (count) seguido de cada string com `writeUTF`.
- Exemplo: no modelo `Jogo` o atributo `generos` é uma lista de strings. A serialização faz: `dos.writeInt(generos.size())` e então, para cada gênero, `dos.writeUTF(genero)`; na leitura faz `int n = dis.readInt();` e itera n vezes lendo `dis.readUTF()` e populando a lista.
- Vantagem: leitura determinística e simples, sem delimitadores ambíguos.

c) Como foi implementada a exclusão lógica?

- Cada registro tem uma "lápide" no arquivo: um byte imediatamente antes do tamanho. Valores usados:
	- ' ' (espaço) = registro ativo
	- '*' = registro excluído
- Ao excluir um registro, o código altera a lápide para '*' e adiciona o espaço à free-list (uma lista encadeada de blocos livres armazenada dentro do próprio arquivo). A implementação fica em `dao.Arquivo`:
	- `delete(id)` marca a lápide e chama `addDeleted(tamanho, endereco)`.
	- `addDeleted` atualiza o ponteiro da lista de deletados no cabeçalho (offset 4) e grava, no início do bloco livre, um ponteiro long para o próximo bloco livre.
	- `getDeleted(tamanhoNecessario)` percorre a free-list em busca de um bloco livre com espaço suficiente e remove (ou atualiza) o ponteiro quando reutilizado.
- Reutilização de espaço:
	- Ao criar (`create`) ou ao atualizar (`update`) com um novo payload maior, o código tenta recuperar um bloco da free-list com `getDeleted` e reutilizá-lo (se couber); caso contrário, anexa no final do arquivo.

d) Além das PKs, quais outras chaves foram utilizadas nesta etapa?

- Foram utilizadas chaves Primárias (PKs) para garantir unicidade dos registros nas entidades principais:
	- `Jogo.id`
	- `Cliente.id`
	- `Biblioteca.id`
	- `CompraJogo.id`
	- `BibliotecaJogo.id`
- Além das PKs, outras chaves foram utilizadas com finalidade de indexação e busca eficiente, principalmente para relacionamentos e consultas específicas:
	- `Jogo.preco` — utilizado como chave de busca para consultas por valor exato ou por faixa de preço
	- `Biblioteca.clienteId` — chave estrangeira (FK) usada como critério de busca no relacionamento 1:N (Cliente → Biblioteca)
	- `BibliotecaJogo.bibliotecaId` — FK utilizada como chave de pesquisa para obter todos os jogos vinculados a uma biblioteca (relacionamento 1:N)
	- `BibliotecaJogo.jogoId` — FK utilizada como chave de busca para consultas inversas (quais bibliotecas possuem determinado jogo)

e) Quais tipos de estruturas B+Tree e Hash foram utilizadas para cada chave de pesquisa?

- Para otimizar as operações de busca, foram implementadas as seguintes estruturas de índice:
	- **Hash Extensível** para o relacionamento `Cliente -> Bibliotecas` (chave `Biblioteca.clienteId`):
		- Implementação: `dao.HashExtensivel`
		- Tipo: Hash dinâmico com chaves `int` (ID do cliente) e valores `List<Integer>` (lista de IDs de bibliotecas).
		- Finalidade: localizar rapidamente todas as bibliotecas de um cliente específico.
		- Exemplo de uso: `buscarPorCliente(1)` no `MenuBiblioteca`.
	- **Árvore B+** para a busca de `Jogos` por `preço`:
		- Implementação: `dao.ArvoreBMaisPreco`
		- Tipo: Árvore B+ com chaves `double`.
		- Finalidade: busca por preço exato e, principalmente, por faixas de valores.
		- Exemplo de uso: `buscarPorFaixaPreco(30.0, 80.0)`.
	- **Árvore B+** para o relacionamento `Biblioteca -> Jogos` (chave `BibliotecaJogo.bibliotecaId`):
		- Implementação: `dao.ArvoreBMaisBibliotecaJogo`
		- Tipo: Árvore B+ com chaves `int`.
		- Finalidade: recuperar todos os jogos associados a uma biblioteca.
		- Exemplo de uso: `listarJogosDaBiblioteca(1)` no sub-menu de gerenciamento de jogos da biblioteca.

f) Como foi implementado o relacionamento 1:N (ex: Cliente → Bibliotecas)?

- **Modelo de Dados:** O relacionamento é representado por uma chave estrangeira. A classe `Biblioteca` possui um atributo `clienteId` que armazena o ID do `Cliente` ao qual pertence.
- **Estrutura de Índice:** Para otimizar a busca de bibliotecas por cliente, foi implementado um índice de **Hash Extensível** (`dao.HashExtensivel`).
- **Funcionamento:**
	1. O `HashExtensivel` mapeia o `clienteId` (chave) a uma lista de IDs de bibliotecas (`List<Integer>`).
	2. A classe `IndiceClienteHash` gerencia o ciclo de vida deste índice, incluindo seu carregamento e salvamento.
	3. Quando uma biblioteca é criada, alterada ou excluída, o `BibliotecaDAO` atualiza o índice de hash para refletir a mudança na associação.
- **Busca:** Para encontrar todas as bibliotecas de um cliente, o `BibliotecaDAO` consulta o índice de hash com o `clienteId`. Isso retorna diretamente a lista de IDs das bibliotecas correspondentes, que são então carregadas do arquivo principal. Este método é muito mais eficiente do que uma varredura sequencial.
- **Validação:** A validação de integridade referencial (garantir que um `clienteId` em uma `Biblioteca` aponta para um `Cliente` existente) continua sendo feita manualmente no código da aplicação, por exemplo, no método `BibliotecaDAO.incluirComValidacao`.

g) Como os índices são persistidos em disco?

- **Estratégia de Persistência:** Todos os índices (`HashExtensivel`, `ArvoreBMaisPreco`, `ArvoreBMaisBibliotecaJogo`) são persistidos em disco através da **serialização de objetos Java**. A estrutura de dados completa (seja a tabela de hash com seus buckets ou a árvore B+ com seus nós) é gravada em um arquivo binário.
- **Arquivos de Índice:**
	- `dados/cliente_bibliotecas_hash.db`: Arquivo do índice de Hash Extensível para `Cliente -> Bibliotecas`.
	- `dados/jogos/preco_idx.db`: Arquivo do índice de Árvore B+ para `Jogo.preco`.
	- `dados/bibliotecas/biblioteca_jogo_idx.db`: Arquivo do índice de Árvore B+ para `Biblioteca -> Jogos`.
- **Carregamento (Load):**
	1. Ao iniciar a aplicação, os DAOs correspondentes instanciam suas classes de gerenciamento de índice (`IndiceClienteHash`, `IndicePrecoJogo`, etc.).
	2. O construtor da classe de índice tenta carregar o objeto serializado a partir do arquivo `.db` correspondente usando `ObjectInputStream`.
	3. Se o arquivo não existir (primeira execução ou após uma limpeza), uma nova estrutura de índice vazia é criada em memória.
	4. Para as Árvores B+, após o carregamento, os ponteiros `transient` (pai, irmão anterior/próximo) dos nós são reconstruídos para restaurar a funcionalidade completa da árvore em memória. O Hash Extensível não necessita dessa etapa de restauração pós-carregamento.
- **Salvamento (Save):**
	1. Após qualquer operação de Criação, Alteração ou Exclusão (CUD) que afete os dados indexados, o DAO correspondente invoca o método de salvamento do seu gerenciador de índice (ex: `indiceCliente.salvarIndice()`).
	2. Este método utiliza `ObjectOutputStream` para serializar e sobrescrever o objeto de índice completo no arquivo `.db`. Isso garante que o índice no disco esteja sempre sincronizado com os dados.
- **Manutenção:** Foram adicionadas opções nos menus para "Reconstruir índice", que apagam o índice atual e o recriam a partir do zero, lendo todos os dados do arquivo principal. Isso serve como uma ferramenta de recuperação para casos de corrupção do arquivo de índice.

h) Como está estruturado o projeto no GitHub (pastas, módulos, arquitetura)?

- Arquitetura geral: padrão MVC combinado com DAOs para persistência.
- Pastas principais no repositório:
	- `model/` — classes de domínio (por exemplo `Cliente`, `Biblioteca`, `Jogo`, `Compra`) e a interface `Registro`. Cada modelo implementa `toByteArray`/`fromByteArray`.
	- `dao/` — implementação genérica `Arquivo<T>` (RandomAccessFile + free-list + gerência de lápides) e DAOs específicos (`ClienteDAO`, `BibliotecaDAO`, `JogoDAO`, `CompraDAO`) que usam `Arquivo`.
	- `view/` — menus e componentes de UI em console (`MenuClientes`, `MenuBiblioteca`, `MenuJogos`, `MenuCompras`, `Principal`, `Listar*`).
	- `util/` — utilitários como `OutputFormatter` (formatação de datas, preços e representação de objetos para impressão).
	- `teste/` — classes de teste / scans manuais (`TestBuscar`, `TestAlterar`, `TestBiblioteca`, `TestJogo`, `TestCompra`, `SeedData`).
	- `dados/` — diretório criado em tempo de execução que contém subpastas por entidade e os arquivos `.db` binários (normalmente não versionados).
- Padrões e decisões importantes:
	- Persistência: armazenamento binário customizado por entidade (sem DBMS), controle manual de IDs e free-list.
	- Serialização: responsabilidade das classes de modelo (deterministicidade do layout para compatibilidade/versões).
	- Validações de integridade (FK): feitas na camada DAO (por exemplo, `BibliotecaDAO.incluirComValidacao` verifica existência de `Cliente` quando `clienteId > 0`).
	- Convenções: data formatada como `dd-MM-yyyy`, preços com duas casas decimais, `nota` dos jogos limitada a 0–5 pelo modelo.

---

## Diagrama de bytes (layout do arquivo .db)

Cada arquivo de entidade (por exemplo `dados/clientes/clientes.db`) tem o seguinte formato geral:

- Cabeçalho (offset 0):
	- bytes [0..3]   : int  -> último ID usado (4 bytes)
	- bytes [4..11]  : long -> ponteiro para primeiro bloco da free-list (-1 se vazio) (8 bytes)

- Registros (começando em offset 12): repetição de blocos do tipo:
	- byte  [offset]     : lápide (1 byte) -> ' ' (espaço) = ativo, '*' = excluído
	- short [offset+1]   : tamanho do payload em bytes (2 bytes)
	- byte[] [offset+3]  : payload (N bytes) = valor retornado por toByteArray()

Exemplo visual (offsets relativos):

	0       4       12
	|-------|-------|--------------------------------------------
	| lastID| free  | lapide | size(short) | payload (toByteArray)
	| 4b   | 8b    | 1b     | 2b          | N bytes

Detalhe do payload (exemplo genérico para `Compra` com clienteId):

	payload bytes (ordem em toByteArray):
	- int id (4 bytes)
	- int clienteId (4 bytes)
	- UTF status (2B length + chars)
	- double valor (8 bytes)
	- long dataEpochDay (8 bytes)

Exemplo de payload para `Jogo` com multivalorados (`generos`):
	- int id
	- UTF nome
	- UTF descricao
	- UTF tamanho
	- short nota
	- UTF plataforma
	- double preco
	- int countGeneros
		- for i in 0..countGeneros-1: UTF genero_i
	- UTF classificacaoEtaria

Observações:
- `UTF` no DataOutputStream grava primeiro um short com o comprimento em bytes e depois os bytes do texto; por isso o payload tem partes de tamanho variável.
- Ao ler (`fromByteArray`) as classes devem respeitar exatamente a mesma ordem de campos.
- Para compatibilidade com versões antigas (quando um campo foi adicionado), o projeto implementa fallback nos `fromByteArray` (tenta o formato novo e, em caso de EOF/erro, tenta o formato antigo e preenche valores padrão como `clienteId = -1`).

## Como compilar e rodar os testes (instruções rápidas)

Requisitos: JDK 11+ e PowerShell (as instruções abaixo assumem PowerShell no Windows).

1) Limpar dados antigos (opcional, mas recomendado para um teste limpo):
```powershell
Remove-Item -Recurse -Force dados
```

2) Compilar todo o projeto:
```powershell
javac -encoding UTF-8 -cp . dao/*.java model/*.java view/*.java util/*.java teste/*.java
```

3) Rodar testes individuais (ordem sugerida):

```powershell
# Cria dados iniciais (cliente, jogos)
java -cp . teste.SeedData

# Testa CRUD de Jogos e índice de preço
java -cp . teste.TestJogo

# Testa CRUD de Bibliotecas e índice Cliente->Biblioteca
java -cp . teste.TestBiblioteca

# Testa CRUD de Compras
java -cp . teste.TestCompra

# Testa interativamente o índice de preços
java -cp . teste.TestBuscarPreco
```

Notas:
- Os arquivos de dados ficam em `dados/<entidade>/<entidade>.db`. 
- Os arquivos de índice ficam em `dados/<entidade>/<indice>.db`.
- Mudanças no layout binário podem quebrar compatibilidade; o projeto inclui fallbacks em alguns `fromByteArray` para manter compatibilidade com registros antigos.

## Executando a interface principal e testando os índices

Após compilar o projeto, você pode iniciar a interface de console principal que agrega todos os menus:

```powershell
# a partir da raiz do projeto
java -cp . view.Principal
```

### Tutorial Rápido para Testar os Índices

**1. Testando o Índice de Preços (Jogo)**

- No menu principal, escolha a opção **`5 - Busca por Preço (Índice)`**.
- Você verá um menu com várias opções de busca baseadas em preço (exato, faixa, premium, etc.).
- Use essas opções para verificar se os jogos são encontrados corretamente.
- Para testar a persistência: adicione um novo jogo no menu `3 - Jogos`, feche e reabra a aplicação, e verifique se a busca por preço encontra o novo jogo.

**2. Testando o Índice Cliente → Bibliotecas**

- Primeiro, crie um cliente no menu `1 - Clientes` e anote o ID dele.
- Em seguida, vá para o menu `2 - Bibliotecas` e inclua algumas bibliotecas, associando-as ao `clienteId` que você criou.
- No menu de Bibliotecas, use a opção **`7 - Buscar por Cliente`** e informe o ID do cliente. A busca deve retornar apenas as bibliotecas associadas a ele.
- Para testar a persistência: feche e reabra a aplicação e realize a busca por cliente novamente. O resultado deve ser o mesmo.
- Se algo der errado, use a opção **`8 - Reconstruir Índice Cliente->Biblioteca`**.

**3. Testando o Índice Biblioteca → Jogos**

- No menu `2 - Bibliotecas`, escolha a opção **`6 - Gerenciar Jogos da Biblioteca`**.
- Informe o ID de uma biblioteca existente.
- Você entrará em um sub-menu. Use a opção **`1 - Adicionar Jogo`** e informe o ID de um jogo existente para criar a associação.
- Use a opção **`2 - Listar Jogos`** para ver todos os jogos associados àquela biblioteca. A busca é feita pelo índice.
- Para testar a persistência: feche e reabra a aplicação, volte a este menu e liste os jogos da biblioteca novamente. A associação deve permanecer.
- Se algo der errado, use a opção **`4 - Reconstruir Índice Biblioteca->Jogos`**.

- Dicas:
	- Em operações de alteração, deixar o campo vazio (apertar Enter) normalmente mantém o valor atual.
	- Veja mensagens de sucesso/erro exibidas no console após cada operação.
