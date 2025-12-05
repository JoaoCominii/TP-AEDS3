# Sistema de Backup e Compressão - TP Parte 4

## 📋 Visão Geral

Sistema completo de backup e compressão de dados implementando os algoritmos **Huffman** e **LZW**. Compacta todos os arquivos da pasta `dados/` em um único arquivo, permitindo restauração completa com verificação de integridade.

## 🎯 Funcionalidades

- ✅ Compressão com algoritmo de Huffman
- ✅ Compressão com algoritmo LZW
- ✅ Backup completo de todos os arquivos de dados
- ✅ Restauração com verificação de integridade (MD5)
- ✅ Comparação entre algoritmos
- ✅ Interface integrada ao menu principal

## 🚀 COMO TESTAR (Método Rápido)

### Opção 1: Teste Automatizado Completo

Execute o teste que faz exatamente o que você pediu:

```bash
# Compilar (se necessário)
javac -encoding UTF-8 util/*.java model/*.java dao/*.java view/*.java teste/*.java

# Executar teste de integridade
java -cp . teste.TestIntegridadeBackup
```

**O que este teste faz:**
1. ✅ Calcula checksums MD5 de todos os arquivos originais
2. ✅ Cria backup com Huffman
3. ✅ Cria backup com LZW
4. ✅ **APAGA COMPLETAMENTE** a pasta `dados/`
5. ✅ Restaura backup Huffman
6. ✅ Verifica integridade (compara checksums MD5)
7. ✅ Apaga novamente
8. ✅ Restaura backup LZW
9. ✅ Verifica integridade novamente

**Resultado esperado:**
```
✓✓✓ TESTE PASSOU COM SUCESSO! ✓✓✓

Ambos os algoritmos:
  • Compactaram corretamente todos os arquivos
  • Restauraram os dados sem perda de informação
  • Mantiveram a integridade completa dos dados

✓ O sistema está pronto para uso em produção!
```

---

## 🧪 COMO TESTAR (Método Manual)

Se preferir testar manualmente:

### Passo 1: Criar Backup

```bash
java -cp . view.Principal
```

Escolha: `7` → `1` (ou `2` para LZW)

Anote o nome do arquivo gerado (ex: `backup_huffman_20251204_222318.dat`)

### Passo 2: Apagar Todos os Dados

**Windows PowerShell:**
```powershell
Remove-Item -Recurse -Force dados
```

**Verificar que foi apagado:**
```powershell
Test-Path dados
# Deve retornar: False
```

### Passo 3: Restaurar Backup

```bash
java -cp . view.Principal
```

Escolha: `7` → `3` → Selecione o backup criado → Confirme com `S`

### Passo 4: Verificar Integridade

Execute o sistema e teste qualquer funcionalidade:

```bash
java -cp . view.Principal
```

Por exemplo, liste clientes: `1` → `5`

Se os dados aparecerem corretamente, **a restauração funcionou!**

---

## 📊 Verificar Taxas de Compressão

### Via Menu Interativo

```bash
java -cp . view.Principal
```

Escolha: `7` → `5` (Comparar algoritmos)

Você verá:
- Taxa de compressão de cada algoritmo
- Tempo de execução
- Comparação lado a lado

### Via Teste Automatizado

```bash
java -cp . teste.TestCompressao
```

Você verá:
- Métricas detalhadas de ambos os algoritmos
- Respostas formatadas para o formulário
- Interpretação dos resultados

---

## 📁 Estrutura de Arquivos

```
TP-AEDS3/
├── util/
│   ├── HuffmanCompressor.java    # Algoritmo de Huffman
│   ├── LZWCompressor.java         # Algoritmo LZW
│   └── BackupManager.java         # Gerenciador de backup
├── view/
│   └── MenuBackup.java            # Interface do usuário
├── teste/
│   ├── TestCompressao.java        # Teste básico
│   └── TestIntegridadeBackup.java # Teste completo
├── dados/                         # Arquivos de dados (compactados)
└── backups/                       # Arquivos de backup gerados
    ├── backup_huffman_YYYYMMDD_HHMMSS.dat
    └── backup_lzw_YYYYMMDD_HHMMSS.dat
```

## 📊 Resultados Obtidos

### Huffman
- **Tamanho Original**: 6.794 bytes
- **Tamanho Comprimido**: 6.714 bytes
- **Taxa de Compressão**: 1,18%
- **Tempo**: ~90-180 ms

### LZW
- **Tamanho Original**: 6.794 bytes
- **Tamanho Comprimido**: 14.464 bytes
- **Taxa de Compressão**: -112,89% (expansão)
- **Tempo**: ~26-34 ms

### Análise
- **Huffman** obteve compressão positiva, mas modesta devido aos dados binários estruturados
- **LZW** expandiu o arquivo devido ao overhead do dicionário e tamanho pequeno dos dados
- Ambos **preservam 100% da integridade** dos dados (verificado por MD5)

## 🔧 Detalhes Técnicos

### Algoritmo de Huffman

**Estruturas de Dados:**
- `PriorityQueue<Node>`: Construção da árvore (O(n log n))
- `HashMap<Byte, Integer>`: Tabela de frequências (O(1) acesso)
- `HashMap<Byte, String>`: Códigos binários (O(1) lookup)
- Árvore binária para decodificação

**Processo:**
1. Conta frequências de cada byte
2. Constrói árvore de Huffman bottom-up
3. Gera códigos binários de tamanho variável
4. Serializa: frequências + dados codificados + padding info

### Algoritmo LZW

**Estruturas de Dados:**
- `HashMap<String, Integer>`: Dicionário de compressão (máx 65.536 entradas)
- `HashMap<Integer, String>`: Dicionário de descompressão
- `ArrayList<Integer>`: Sequência de códigos gerados

**Processo:**
1. Inicializa dicionário com 256 símbolos ASCII
2. Busca sequências crescentes no dicionário
3. Emite código e adiciona nova sequência
4. Serializa: tamanho + lista de códigos (4 bytes cada)

### Gerenciador de Backup

**Funcionalidades:**
- Coleta recursiva de todos os arquivos em `dados/`
- Serialização de metadados (caminho + tamanho + conteúdo)
- Compressão em algoritmo escolhido
- Nome automático: `backup_{algoritmo}_{timestamp}.dat`

## ✅ Testes de Integridade

Todos os testes passaram com sucesso:

```
✓ 14 arquivos verificados
✓ Todos os checksums MD5 conferem
✓ Nenhum dado foi perdido ou corrompido
✓ Sistema pronto para produção!
```

## 🎓 Respostas do Formulário

Ver arquivo `RESPOSTAS_PARTE4.md` para respostas detalhadas das questões:
1. Taxa de compressão Huffman (cálculo e interpretação)
2. Taxa de compressão LZW (cálculo e interpretação)
3. Dificuldades de implementação e soluções
4. Justificativa das estruturas de dados

## 🔍 Observações Importantes

1. **Dados Pequenos**: Com apenas ~7KB, o overhead dos algoritmos limita a compressão
2. **Dados Binários**: Estruturas binárias têm menos redundância que texto
3. **Huffman vs LZW**: Huffman é melhor para nosso caso de uso
4. **Integridade Garantida**: Ambos os algoritmos preservam 100% dos dados
5. **Pronto para Produção**: Sistema totalmente funcional e testado

## 📝 Compilação

```bash
# Compilar tudo
javac -encoding UTF-8 util/*.java model/*.java dao/*.java view/*.java teste/*.java

# Executar menu principal
java -cp . view.Principal

# Executar testes
java -cp . teste.TestCompressao
java -cp . teste.TestIntegridadeBackup
```

## 🎯 Conclusão

O sistema de backup e compressão foi implementado com sucesso, cumprindo todos os requisitos:
- ✅ Compressão obrigatória sobre todos os arquivos
- ✅ Resultado em arquivo único compactado
- ✅ Funcionamento a nível de arquivo (backup completo)
- ✅ Algoritmos Huffman e LZW implementados
- ✅ Preservação do funcionamento e integridade do CRUD e índices
- ✅ Testes completos de integridade (compactar → apagar → restaurar → verificar)
