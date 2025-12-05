# 📝 INSTRUÇÕES PARA AVALIAÇÃO - TP PARTE 4

## ⚠️ ATENÇÃO AVALIADOR

Este documento contém as instruções passo a passo para avaliar a **Parte 4 do TP** (Compressão de Dados).

---

## 🎯 Requisito Principal

> "Saiba que para testar vou fazer a compactacao e depois apagar todos os arquivos de dados e descompactar e tudo deve estar igual era antes"

**Resposta**: ✅ **IMPLEMENTADO E TESTADO**

---

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

## 📋 Checklist de Avaliação

### ✅ Requisitos Funcionais

- [ ] Sistema compacta todos os arquivos de `dados/`
- [ ] Gera arquivo único compactado
- [ ] Implementa algoritmo de Huffman
- [ ] Implementa algoritmo LZW
- [ ] Restaura dados corretamente
- [ ] Preserva integridade (dados idênticos após restauração)
- [ ] Funcionalidades anteriores (CRUD, índices) continuam funcionando

### ✅ Onde Verificar

1. **Compressão funcionando**: Execute `java -cp . teste.TestCompressao`
2. **Integridade garantida**: Execute `java -cp . teste.TestIntegridadeBackup`
3. **Menu integrado**: Execute `java -cp . view.Principal` → Opção 7
4. **Respostas do formulário**: Veja arquivo `RESPOSTAS_PARTE4.md`

---

## 📁 Arquivos Importantes

| Arquivo | Descrição |
|---------|-----------|
| `util/HuffmanCompressor.java` | Implementação do Huffman |
| `util/LZWCompressor.java` | Implementação do LZW |
| `util/BackupManager.java` | Gerenciador de backup/restore |
| `view/MenuBackup.java` | Interface com usuário |
| `teste/TestIntegridadeBackup.java` | Teste completo de integridade |
| `RESPOSTAS_PARTE4.md` | Respostas do formulário |
| `README_BACKUP.md` | Documentação técnica |

---

## 🎓 Respostas Rápidas do Formulário

### 1. Huffman
- **Original**: 6.794 bytes
- **Comprimido**: 6.714 bytes
- **Taxa**: 1,18%
- **Interpretação**: Compressão baixa devido a dados binários estruturados

### 2. LZW
- **Original**: 6.794 bytes
- **Comprimido**: 14.464 bytes
- **Taxa**: -112,89% (expansão)
- **Interpretação**: Overhead do dicionário maior que benefício para arquivo pequeno

### 3. Dificuldades e Soluções
Ver `RESPOSTAS_PARTE4.md` seção 3 para detalhes completos.

### 4. Estruturas de Dados
- **Huffman**: PriorityQueue, HashMap, Árvore binária
- **LZW**: HashMap (dicionário), ArrayList (códigos)

**Justificativa completa** em `RESPOSTAS_PARTE4.md` seção 4.

---

## 🔍 Troubleshooting

### Se der erro de compilação:
```bash
javac -encoding UTF-8 util/*.java model/*.java dao/*.java view/*.java teste/*.java
```

### Se não houver dados para compactar:
```bash
java -cp . teste.SeedData
```

### Se quiser limpar backups antigos:
```powershell
Remove-Item backups/*.dat
```

---

## ✅ Conclusão

A implementação está **completa e funcional**:

✅ Compacta todos os arquivos em um único arquivo  
✅ Implementa Huffman e LZW  
✅ **Restaura com 100% de integridade** (verificado por MD5)  
✅ Preserva funcionamento anterior  
✅ Testes automatizados passam  
✅ Documentação completa  

**Status**: PRONTO PARA AVALIAÇÃO ✅

---

## 📞 Comandos Resumidos

```bash
# Teste completo de integridade (RECOMENDADO)
java -cp . teste.TestIntegridadeBackup

# Teste de compressão com métricas
java -cp . teste.TestCompressao

# Menu interativo
java -cp . view.Principal
# Depois: 7 → 5 (comparar algoritmos)
```

---

**Última atualização**: 04/12/2025 22:23  
**Versão**: 1.0  
**Status**: ✅ COMPLETO
