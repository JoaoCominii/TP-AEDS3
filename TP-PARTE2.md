
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


1) Criar cliente seed (gera ID 1):

```powershell
java -cp . teste.SeedData
```

2) Rodar testes individuais (ordem sugerida):

```powershell
java -cp . teste.TestBuscar
java -cp . teste.TestAlterar
java -cp . teste.TestBiblioteca
java -cp . teste.TestJogo
java -cp . teste.TestCompra
```

Notas:
- Os arquivos de dados ficam em `dados/<entidade>/<entidade>.db`. 
- Mudanças no layout binário podem quebrar compatibilidade; o projeto inclui fallbacks em alguns `fromByteArray` para manter compatibilidade com registros antigos.

## Executando a interface principal (view.Principal)

Após compilar o projeto, você pode iniciar a interface de console principal que agrega todos os menus (clientes, bibliotecas, jogos, compras):

```powershell
# a partir da raiz do projeto
java -cp . view.Principal
```

O que aparece e o que fazer:

- Menu principal (opções):
	1 - Clientes
	2 - Bibliotecas
	3 - Jogos
	4 - Compras
	0 - Sair

- Como navegar:
	- Digite o número da opção e pressione Enter.
	- Em cada sub-menu use as opções mostradas (Buscar, Incluir, Alterar, Excluir, Listar). Normalmente 0 volta ao menu anterior.

- Fluxo de uso recomendado (exemplo prático):
	1) Clientes → Incluir: crie um cliente para obter um `clienteId` válido (necessário para associar bibliotecas e compras).
	2) Jogos → Incluir: cadastre jogos que poderão ser adicionados a compras/bibliotecas.
	3) Compras → Incluir: informe o `clienteId` do cliente criado e os dados da compra (o DAO valida o cliente).
	4) Bibliotecas → Incluir: associe uma biblioteca a um `clienteId` se desejar.

- Dicas:
	- Em operações de alteração, deixar o campo vazio (apertar Enter) normalmente mantém o valor atual.
	- Veja mensagens de sucesso/erro exibidas no console após cada operação.
