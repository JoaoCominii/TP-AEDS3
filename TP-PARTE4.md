# FORMULARIO - TP PARTE 4: Criptografia RSA e Compressão de dados

1) Qual foi a taxa de compressão obtida com o algoritmo de Huffman?
   
  a) Tamanho do arquivo original
**6.794 bytes** (0,01 MB)

  b) Tamanho do arquivo comprimido
**6.714 bytes** (0,01 MB)

  c) Cálculo da taxa
```
Taxa de Compressão = ((Tamanho Original - Tamanho Comprimido) / Tamanho Original) × 100
Taxa de Compressão = ((6.794 - 6.714) / 6.794) × 100
Taxa de Compressão = (80 / 6.794) × 100
Taxa de Compressão = 1,18%
```

  d) Interpretação do resultado
O algoritmo de Huffman obteve uma **compressão baixa de 1,18%**, reduzindo apenas 80 bytes do tamanho original. Esta taxa modesta se deve à natureza dos dados:

- **Dados binários estruturados**: Os arquivos contêm registros serializados com campos de tamanho fixo e variável
- **Alta entropia**: Os dados têm distribuição relativamente uniforme de bytes, limitando a eficácia da codificação por frequência
- **Overhead da árvore**: A tabela de frequências e a estrutura da árvore de Huffman ocupam espaço no arquivo comprimido

**Huffman é mais eficaz em:** textos com alta repetição de caracteres, onde a codificação variável pode representar caracteres frequentes com menos bits.

---

2) Qual foi a taxa de compressão obtida com o algoritmo de LZW?

  a) Tamanho do arquivo original
**6.794 bytes** (0,01 MB)

  b) Tamanho do arquivo comprimido
**14.464 bytes** (0,01 MB)

  c) Cálculo da taxa
```
Taxa de Compressão = ((Tamanho Original - Tamanho Comprimido) / Tamanho Original) × 100
Taxa de Compressão = ((6.794 - 14.464) / 6.794) × 100
Taxa de Compressão = (-7.670 / 6.794) × 100
Taxa de Compressão = -112,89%
```

  d) Interpretação do resultado
O algoritmo LZW resultou em **expansão de 112,89%**, mais que dobrando o tamanho do arquivo. Isso ocorre porque:

- **Dicionário grande**: Cada código LZW é armazenado como um inteiro de 4 bytes (32 bits)
- **Dados pequenos**: Com apenas 6.794 bytes, não há padrões repetidos suficientes para compensar o overhead do dicionário
- **Natureza binária**: Dados binários com pouca repetição de sequências não se beneficiam do LZW
- **Overhead de metadados**: É necessário armazenar o tamanho original e todos os códigos

**LZW é mais eficaz em:** arquivos grandes com muitos padrões repetidos (texto, imagens), onde o dicionário se paga rapidamente.

---

3) Quais dificuldades surgiram ao implementar Huffman e LZW e como você resolveu?

### Huffman

**Dificuldade 1: Serialização da Árvore**
- **Problema**: Como armazenar a árvore de Huffman no arquivo comprimido para permitir descompressão?
- **Solução**: Em vez de serializar a árvore completa, armazenamos apenas a tabela de frequências. Na descompressão, reconstruímos a árvore a partir das frequências, garantindo a mesma estrutura.

**Dificuldade 2: Manipulação de Bits**
- **Problema**: Java trabalha nativamente com bytes (8 bits), mas os códigos de Huffman têm tamanhos variáveis.
- **Solução**: Implementamos conversão de string binária para array de bytes, armazenando quantos bits são válidos no último byte para não perder informação.

**Dificuldade 3: Caso de Árvore Única**
- **Problema**: Quando todos os bytes são iguais, a árvore tem apenas um nó, causando loop infinito na decodificação.
- **Solução**: Detectamos este caso especial e preenchemos diretamente o array de resultado sem percorrer a árvore.

### LZW

**Dificuldade 1: Tamanho do Dicionário**
- **Problema**: Dicionário ilimitado pode causar uso excessivo de memória.
- **Solução**: Limitamos o dicionário a 65.536 entradas (2^16), que é um bom equilíbrio entre compressão e memória.

**Dificuldade 2: Caso Especial do Código Inexistente**
- **Problema**: LZW pode gerar um código que ainda não está no dicionário (código = tamanho atual).
- **Solução**: Implementamos tratamento especial: quando encontramos este código, sabemos que a sequência é `string_atual + primeiro_char(string_atual)`.

**Dificuldade 3: Conversão de Tipos**
- **Problema**: LZW trabalha com strings internamente, mas precisamos processar bytes.
- **Solução**: Usamos máscaras (`b & 0xFF`) para garantir que bytes sejam tratados como valores de 0-255, e criamos função auxiliar para converter strings em bytes.

---

4) Justifique a escolha da estrutura de dados usada para armazenar as tabelas, dicionários e
árvores utilizados pelos algoritmos.

### Huffman

**PriorityQueue<Node> (Fila de Prioridade)**
- **Uso**: Construção da árvore de Huffman
- **Justificativa**: Mantém os nós ordenados por frequência automaticamente, permitindo extrair os dois nós de menor frequência em O(log n). Essencial para o algoritmo guloso de Huffman que sempre combina os nós de menor frequência.
- **Complexidade**: Inserção e remoção em O(log n), ideal para construir a árvore eficientemente.

**HashMap<Byte, Integer> (Tabela de Frequências)**
- **Uso**: Contar frequências de cada byte
- **Justificativa**: Acesso O(1) para incrementar contadores. Como temos no máximo 256 valores possíveis (bytes), o HashMap é perfeito para armazenar e recuperar rapidamente as frequências.

**HashMap<Byte, String> (Tabela de Códigos)**
- **Uso**: Mapear cada byte para seu código binário de Huffman
- **Justificativa**: Durante a compressão, precisamos acesso O(1) ao código de cada byte. Um HashMap fornece essa busca rápida, essencial quando processamos grandes volumes de dados.

**Árvore Binária (Classe Node)**
- **Uso**: Representar a árvore de Huffman para decodificação
- **Justificativa**: A estrutura natural do algoritmo de Huffman é uma árvore binária onde folhas representam bytes e o caminho da raiz à folha define o código. Percorrer a árvore durante decodificação é O(altura), muito eficiente.

### LZW

**HashMap<String, Integer> (Dicionário de Compressão)**
- **Uso**: Mapear sequências de bytes para códigos durante compressão
- **Justificativa**: Precisamos verificar rapidamente se uma sequência já está no dicionário. HashMap oferece busca O(1), crucial para o desempenho do LZW que faz milhares de consultas.
- **Capacidade**: Limitado a 65.536 entradas para evitar uso excessivo de memória.

**HashMap<Integer, String> (Dicionário de Descompressão)**
- **Uso**: Mapear códigos de volta para sequências durante descompressão
- **Justificativa**: Durante descompressão, recebemos códigos e precisamos recuperar as sequências. O HashMap inverso (Integer → String) oferece acesso O(1), simétrico ao processo de compressão.

**ArrayList<Integer> (Lista de Códigos)**
- **Uso**: Armazenar sequência de códigos comprimidos
- **Justificativa**: A saída do LZW é uma sequência ordenada de códigos. ArrayList oferece:
  - Inserção O(1) amortizada no final
  - Acesso sequencial O(1) durante serialização
  - Tamanho dinâmico, pois não sabemos antecipadamente quantos códigos serão gerados

### Backup Manager

**List<FileData> (Lista de Arquivos)**
- **Uso**: Armazenar informações de todos os arquivos antes de compactar
- **Justificativa**: Precisamos manter a ordem dos arquivos e suas metadados (caminho + conteúdo). Uma lista é perfeita para processamento sequencial e serialização.

**ByteArrayOutputStream/DataOutputStream**
- **Uso**: Serialização de dados
- **Justificativa**: Permite escrever tipos primitivos (int, byte[], String) de forma portável e eficiente, essencial para criar um formato de arquivo estruturado.

5) Qual campo foi escolhido para criptografia? Por quê?

   - Campo escolhido: `Cliente.senha` (atributo `senha` da classe `model/Cliente`).
   - Justificativa: é o dado mais sensível do domínio (credencial de usuário). O requisito pede criptografar pelo menos um campo — proteger senhas protege a privacidade e reduz risco caso os arquivos sejam lidos sem autorização.

6) Descreva como o RSA foi implementado no projeto.

   a) Estrutura das chaves pública e privada

   - Chaves geradas como par RSA (public/private). A chave pública foi armazenada no formato X.509 (usada por `X509EncodedKeySpec`) e a chave privada em PKCS#8 (usada por `PKCS8EncodedKeySpec`).
   - O utilitário (`util/RSAUtil.java`) reconstrói `PublicKey` e `PrivateKey` usando `KeyFactory` a partir desses bytes.

   b) Como e onde foram armazenadas

   - Diretório: `dados/keys/`.
   - Arquivos: `dados/keys/public.key` (bytes da chave pública) e `dados/keys/private.key` (bytes da chave privada).
   - Comportamento: na primeira execução, se os arquivos não existirem, o utilitário gera um par de chaves (2048 bits) e grava os arquivos; em execuções seguintes, as chaves são carregadas desses arquivos.

   c) Como foram carregadas pelo sistema

   - `RSAUtil.ensureKeys()` verifica se as chaves estão carregadas; se não estiverem, tenta ler os arquivos; se ausentes, gera um novo par e grava.
   - As funções `encryptString` e `decryptString` chamam `ensureKeys()` antes de operar.

   d) Tamanho das chaves escolhidas e justificativa

   - Tamanho: **2048 bits**.
   - Justificativa: 2048 bits é atualmente considerado seguro e é uma escolha equilibrada entre segurança e desempenho. Para o caso de senhas (pequeno volume de dados) 2048 bits com OAEP-SHA256 fornece proteção adequada.

   e) Em qual momento a criptografia do(s) campo(s) ocorre (no CRUD)

   - Create (inserção): a criptografia ocorre antes da escrita no arquivo `.db`. Implementação: `model/Cliente.toByteArray()` chama `RSAUtil.encryptString(...)` e escreve o texto cifrado (Base64) no registro. Assim o `.db` contém a senha já cifrada.
   - Update: mesma lógica que o create — ao atualizar o objeto, `toByteArray()` é chamado e a senha é cifrada antes de sobrescrever o registro.

   f) Em qual momento ocorre a descriptografia

   - A descriptografia ocorre em `model/Cliente.fromByteArray()` imediatamente após a leitura dos bytes do registro do `.db`. O valor lido (string Base64) é passado a `RSAUtil.decryptString(...)` e o texto claro é atribuído ao campo `senha` no objeto em memória.

   g) Conversões realizadas (ex.: string → bytes → blocos)

   - Fluxo de gravação:
     1. `String senha` → codificação UTF-8 → `byte[]`.
     2. `Cipher.doFinal(...)` com `RSA/ECB/OAEPWithSHA-256AndMGF1Padding` e chave pública → `byte[]` cifrado.
     3. `byte[]` cifrado → Base64 → escrita por `DataOutputStream.writeUTF(...)` no `.db`.

   - Fluxo de leitura:
     1. Lê string (Base64) via `readUTF()` → `String enc`.
     2. `Base64.decode(enc)` → `byte[]` cifrado.
     3. `Cipher.doFinal(...)` com chave privada → bytes decifrados.
     4. bytes → `String` via UTF-8 → `Cliente.senha` em memória.

   - Observação: o utilitário de descriptografia foi tornado tolerante a valores legados em texto claro: se a operação de decodificação/decifração falhar, ele retorna o valor original lido (compatibilidade com dados anteriores em texto claro).

Arquivo(s) modificados / adicionados nesta fase (referência):

- `util/RSAUtil.java` (novo) — utilitário de geração/carregamento e encrypt/decrypt.
- `model/Cliente.java` — `toByteArray()` e `fromByteArray()` atualizados para criptografar antes de gravar e descriptografar ao ler.
 
---

Breve explicação — diferença entre chave pública e chave privada

- **Chave pública (public key)**: pode ser divulgada. É usada para *criptografar* dados destinados ao dono da chave privada ou para *verificar* assinaturas geradas pela chave privada. No caso de confidencialidade, qualquer pessoa usa a chave pública para cifrar; somente a chave privada correspondente pode decifrar.
- **Chave privada (private key)**: deve ser mantida em segredo. É usada para *descriptografar* dados cifrados com a chave pública, e também pode ser usada para *assinar* dados (assinatura digital). A assinatura gerada com a chave privada pode ser verificada por qualquer pessoa que possua a chave pública.

Em resumo: para confidencialidade, usa-se `public encrypt -> private decrypt`; para autenticação/assinatura, usa-se `private sign -> public verify`.

### [Sistema de backup instruções](README_BACKUP.md)

