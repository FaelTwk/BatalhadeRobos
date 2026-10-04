# Batalha de Robôs

Jogo de batalha por turnos desenvolvido em Java para um projeto da faculdade. O jogador cria um robô, escolhe seu tipo e suas peças e enfrenta um adversário controlado pelo computador pelo terminal.

O projeto utiliza programação orientada a objetos, arrays, matrizes, `ArrayList`, manipulação de strings, tratamento de exceções e escrita em arquivo.

## Como executar

### Requisitos

- JDK 17 ou superior.
- Arquivos com codificação UTF-8 para exibir corretamente os acentos.

### Pela IDE

1. Abra o projeto no IntelliJ IDEA, Eclipse ou outra IDE Java.
2. Configure o JDK do projeto para a versão 17 ou superior.
3. Mantenha os arquivos `.java` na pasta `src`.
4. Execute o método `main` da classe `Main`.

### Pelo terminal

Na pasta raiz do projeto, que contém a pasta `src`, execute:

```bash
javac -encoding UTF-8 -d out src/*.java
java -cp out Main
```

Não são necessárias bibliotecas externas.

## Como jogar

1. Digite um nome de 3 a 20 caracteres para o seu robô. Espaços nas pontas são removidos e espaços repetidos entre palavras são normalizados. Digite `ajuda` para ver as instruções dessa etapa.
2. Escolha entre Tanque, Atirador, Assassino, Hacker e Engenheiro.
3. Escolha uma peça ou nenhuma para cada espaço de equipamento: arma, blindagem e módulo.
4. O jogo sorteia o tipo do adversário e suas peças.
5. A cada rodada, consulte as barras de vida e energia e escolha uma ação.
6. Após as ações, pressione **Enter** para começar a próxima rodada.
7. A partida termina com vitória, derrota ou fuga bem-sucedida. O histórico é salvo em um arquivo de texto.

O jogo executa uma partida por vez. Para começar outra, execute o programa novamente.

## Tipos de robô

Os valores abaixo são os atributos básicos da versão atual, antes dos efeitos das peças.

| Tipo | Vida | Energia | Ataque | Defesa | Velocidade | Adversário |
| --- | ---: | ---: | ---: | ---: | ---: | --- |
| Tanque | 150 | 60 | 18 | 10 | 5 | Titan |
| Atirador | 100 | 100 | 25 | 5 | 15 | Blaster |
| Assassino | 80 | 80 | 30 | 3 | 25 | Orion |
| Hacker | 90 | 100 | 22 | 4 | 20 | Glitch |
| Engenheiro | 120 | 80 | 20 | 7 | 10 | Fixtron |

Cada tipo de adversário tem a mesma chance de ser sorteado, inclusive o mesmo tipo escolhido pelo jogador.

### Habilidades especiais

Todas custam **20 de energia por uso**.

| Tipo | Habilidade | Efeito |
| --- | --- | --- |
| Tanque | Escudo Magnético | Ativa a proteção, recupera até 20 de vida e aplica um golpe com metade do ataque. A defesa do alvo é descontada. |
| Atirador | Rajada Tripla | Dispara até três tiros. Cada um tem força equivalente a 70% do ataque, com divisão inteira. A defesa do alvo é descontada em cada tiro. |
| Assassino | Golpe Crítico | Aplica um golpe com 150% do ataque, com divisão inteira, ignorando a blindagem. |
| Hacker | Pulse EMP | Causa 10 de dano ignorando a blindagem e remove até 20 de energia do adversário. |
| Engenheiro | Autorreparo | Recupera até 25 de vida e ativa a proteção. |

A Rajada Tripla interrompe os tiros se o alvo for destruído. Habilidades que ignoram a blindagem ainda sofrem a redução da proteção ativa de defender.

## Ações da batalha

```text
1 - Atacar
2 - Defender
3 - Habilidade especial
4 - Usar item
5 - Tentar fugir
```

### Atacar

O ataque comum custa **8 de energia**. O dano é a diferença entre o ataque do agressor e a defesa do alvo, com mínimo de 1.

```text
Ataque 25 contra defesa 10 → 15 de dano
```

A vida nunca fica abaixo de zero.

### Defender

Defender não gasta energia e reduz pela metade o dano do próximo golpe recebido, mantendo o mínimo de 1. A divisão é inteira.

A proteção não acumula. Ela termina quando absorve um golpe ou no início da próxima ação do próprio robô. Em ataques com vários golpes, protege apenas contra o primeiro.

### Usar habilidade especial

Executa a habilidade do tipo escolhido e desconta 20 de energia uma única vez. Na Rajada Tripla, esse custo cobre os três tiros.

Se o robô perder energia depois de escolher a ação — por exemplo, por um ataque do Hacker — a exceção `EnergiaInsuficienteException` é tratada e a ação é substituída por defesa.

### Usar item

Cada robô começa com um inventário próprio contendo:

- Duas poções: cada uma recupera até **30 de vida**.
- Uma bateria: recupera até **30 de energia**.
- Uma granada: causa **25 de dano**, ignorando a blindagem e respeitando a proteção ativa.

O jogador escolhe o item em um submenu. É possível voltar ao menu de ações sem gastar a vez. Poções não podem ser usadas com vida cheia, e baterias não podem ser usadas com energia cheia. Os recursos recuperados nunca ultrapassam seus máximos.

Um item só é removido do inventário após um uso válido. Usá-lo consome a ação da rodada. O computador também possui inventário e só sorteia itens utilizáveis.

### Tentar fugir

A chance depende das velocidades atuais, incluindo os efeitos das peças:

```text
Chance = 50 + 2 × (velocidade do jogador − velocidade do inimigo)
```

O resultado fica limitado entre **15% e 85%**.

| Situação | Chance |
| --- | ---: |
| Velocidades iguais | 50% |
| Jogador 10 pontos mais rápido | 70% |
| Jogador 10 pontos mais lento | 30% |

A tentativa acontece antes dos ataques e encerra a proteção anterior do jogador. Se funcionar, a partida termina imediatamente. Se falhar, o jogador gasta sua ação e o inimigo executa a ação escolhida.

## Turnos e energia

- Em uma rodada normal, cada robô tem uma ação. A Rajada Tripla conta como uma única ação.
- O robô mais rápido age primeiro. Em caso de empate, o jogador começa.
- Um robô destruído não realiza sua ação.
- Ao fim de uma rodada concluída, cada robô vivo recupera até **5 de energia**. Não há recuperação após uma fuga bem-sucedida.
- O computador sorteia entre as ações que pode executar. Defender é sempre uma opção gratuita.
- Entradas inválidas no menu não consomem a vez.
- A pausa com Enter só aparece se os dois robôs ainda estiverem vivos.

## Sistema de peças

Cada robô pode equipar uma peça em cada um dos três espaços:

| Espaço | Opções |
| --- | --- |
| Arma | Canhão de Plasma, Lâminas Sônicas e Martelo Hidráulico |
| Blindagem | Blindagem Pesada, Escudo de Energia e Núcleo Reativo |
| Módulo | Turbina Íon, Reator Auxiliar e Servo Motores |

Cada peça oferece bônus e penalidades, apresentados durante a escolha. Por exemplo, uma peça pode aumentar o ataque e reduzir energia ou velocidade.

O catálogo usa uma matriz de peças, com uma linha para cada espaço. As opções são ordenadas alfabeticamente. O jogador pode deixar um espaço vazio; o adversário recebe uma escolha aleatória, incluindo a possibilidade de não equipar nada.

Os atributos são recalculados a partir dos valores básicos e das peças equipadas. Os limites são aplicados depois da soma: vida e ataque têm mínimo de 1, energia e defesa têm mínimo de 0, e velocidade tem mínimo de 1.

A montagem acontece antes da batalha e deixa vida e energia nos máximos atualizados. Ainda não existe troca de peças durante o combate ou sistema de níveis.

## Histórico de batalhas

Ao fim da partida, o programa grava o arquivo:

```text
historico-batalhas.txt
```

Ele é criado no diretório de execução, e o caminho completo aparece no console. Na IDE, esse diretório depende da configuração do projeto.

O registro inclui data e hora, atributos e peças, status por rodada, ações, danos, recuperação de recursos e resultado. Partidas novas são acrescentadas ao final, sem apagar as anteriores.

O texto usa UTF-8 e é gravado com `FileWriter`. O `try-with-resources` fecha o arquivo automaticamente. Se ocorrer uma `IOException`, o programa informa a falha sem desfazer o resultado da batalha.

O arquivo é salvo ao terminar normalmente a partida, incluindo fuga. Encerrar o programa antes do fim pode deixar a partida atual sem registro.

## Estrutura do projeto

```text
src/
├── Main.java
├── Robo.java
├── Tanque.java
├── Atirador.java
├── Assassino.java
├── Hacker.java
├── Engenheiro.java
├── Peca.java
├── Catalogo.java
├── Item.java
├── EnergiaInsuficienteException.java
