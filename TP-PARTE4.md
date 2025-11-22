# FORMULARIO - TP PARTE 4: Criptografia RSA

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

