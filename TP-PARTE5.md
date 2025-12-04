# TP - PARTE 5: Casamento de Padrões (KMP e Boyer–Moore)

1) Qual campo textual foi escolhido para aplicar os algoritmos de casamento de padrões? Por quê?

- Campos escolhidos: `nome` e `descricao` da tabela `Jogo`.
- Justificativa: ambos são campos textuais relevantes para buscas por conteúdos (nome do jogo e descrição do jogo). A pesquisa por padrão faz sentido nesses campos pois o usuário costuma procurar jogos por palavras contidas no título ou descrição. 

2) Explique o funcionamento do KMP implementado

- Implementação: o KMP (Knuth–Morris–Pratt) foi implementado no método `StringSearch.kmpContains(text, pattern)`.
- Pré-processamento (LPS): primeiro é construída a tabela LPS (longest proper prefix which is also suffix) com a função `buildLPS(pattern)`. A tabela `lps[i]` contém o comprimento do maior prefixo próprio de `pattern[0..i]` que também é sufixo.
- Busca: percorremos o `text` comparando `text[i]` com `pattern[j]`. Em caso de igualdade, incrementamos `i` e `j`. Se `j` atingir o comprimento do padrão, encontramos uma ocorrência (método retorna true). Em caso de desigualdade, se `j != 0` usamos `j = lps[j-1]` (não retrocedemos `i`), caso contrário incrementamos `i`.

3) Explique o funcionamento do Boyer–Moore implementado

- Implementação: a versão implementada é Boyer–Moore com a heurística "bad character" (caractere ruim) — implementada em `StringSearch.bmContains(text, pattern)`.
- Pré-processamento (last occurrence): antes de buscar constrói-se a tabela `last` que guarda para cada caractere a última posição (índice) em que ele aparece no padrão.
- Busca: alinha o final do padrão com a posição atual em `text` e compara para trás (do fim para o começo). Ao encontrar um caractere diferente (bad character) na posição `j` do padrão contra `text[i+j]`, consulta-se a tabela `last` para determinar a posição de ocorrência mais à direita desse caractere no padrão; calcula-se o deslocamento `shift = max(1, j - lastOccurrence)` (ou `j+1` se o caractere não existe no padrão). Avança-se `i += shift` e repete.
- Extensão implementada (good-suffix): além da heurística *bad-character*, a versão atual inclui também a heurística *good-suffix* para melhorar os deslocamentos em muitos casos práticos.
- Pré-processamento (good-suffix): a implementação constrói duas estruturas auxiliares:
  - `suffix[k]`: indica o índice de início da substring do padrão que corresponde a um sufixo do padrão de tamanho `k`, ou `-1` se não existir.
  - `prefix[k]`: booleano que indica se existe um sufixo do padrão de tamanho `k` que também é prefixo do padrão.
- Busca com good-suffix: quando há um mismatch em `j`, calculamos dois possíveis deslocamentos — o deslocamento de bad-character (como antes) e o deslocamento sugerido pela good-suffix (função `moveByGoodSuffix`). O algoritmo escolhe o maior deslocamento entre as duas heurísticas, garantindo saltos maiores e menos comparações em muitos textos.
- Implementação: os métodos auxiliares são `buildGoodSuffix(pat, suffix, prefix)` e `moveByGoodSuffix(j, m, suffix, prefix)` em `util/StringSearch.java`.

4) Descreva como integrou os algoritmos ao sistema.

- Local de implementação:
  - `util/StringSearch.java` contém as duas implementações: `kmpContains` e `bmContains`.

- Interface de usuário:
  - Adicionei `view/MenuPesquisaPadrao.java` que implementa o menu "Pesquisar por padrão (KMP / BM)". O fluxo:
    1. Usuário escolhe o algoritmo (1 = KMP, 2 = Boyer–Moore bad-character).
    2. Usuário informa o padrão (string).
    3. Usuário informe se case sensitive ou insensitive.
    4. O sistema percorre todos os jogos obtidos via `JogoDAO.listarTodos()` e aplica o algoritmo ao campo `nome` e ao campo `descricao`. (Se case insenstive o texto é normalizado com toLowerCase() antes da rotina de busca)
    5. Registros cujo `nome` ou `descricao` contenham o padrão são exibidos.

- Integração no menu principal:
  - Atualizei `view/Principal.java` adicionando a opção `6 - Pesquisar por padrão (KMP / BM)` que instancia `MenuPesquisaPadrao`.

5) Quais dificuldades encontrou na implementação dos dois algoritmos?

- Manuseio de casos-limite e entradas nulas: foi necessário garantir que `null` em `nome` ou `descricao` não causasse NullPointerException (substituí por string vazia antes de pesquisar).
- Integração com o fluxo existente: as DAOs não possuem indexação textual; a solução mais simples e segura foi iterar `listAll` e aplicar o algoritmo a cada registro — isto é adequado para o escopo da atividade, mas pode ser lento em bases grandes.
- Testes e validação: testar cenários adversos (padrões repetitivos, padrões maiores que o texto, padrões vazios) exigiu cuidados para evitar exceções e garantir comportamentos coerentes (por exemplo, padrão vazio é tratado como encontrado).

---

Arquivos criados / modificados nesta etapa:

- `util/StringSearch.java` — implementações de KMP e Boyer–Moore (bad character).
- `view/MenuPesquisaPadrao.java` — menu de pesquisa por padrão (interface conforme requisitos).
- `view/Principal.java` — opção 6 adicionada ao menu principal.

---

