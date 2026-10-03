import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        System.out.println("=== CRIAÇÃO DO ROBÔ ===");

        Robo jogador = criarRobo(scanner);
        montarPecas(scanner, jogador);

        System.out.println("\nSeu robô foi criado!");
        jogador.exibirFicha();

        Robo inimigo = criarInimigo(random);
        equiparAleatoriamente(random, inimigo);
        System.out.println("Ficha do adversário:");
        inimigo.exibirFicha();

        iniciarBatalha(scanner, random, jogador, inimigo);

        scanner.close();
    }

    // Controla os turnos e mostra o resultado da partida.
    private static void iniciarBatalha(
            Scanner scanner, Random random,
            Robo jogador, Robo inimigo) {

        int turno = 1;
        boolean fugiu = false;

        System.out.println("\n=== BATALHA DE ROBÔS ===");

        while (jogador.estaVivo() && inimigo.estaVivo()) {
            System.out.println("\n========== TURNO " + turno + " ==========");

            System.out.println("\nSEU ROBÔ:");
            jogador.exibirStatus();

            System.out.println("\nINIMIGO:");
            inimigo.exibirStatus();

            int acaoJogador = escolherAcao(scanner, jogador);

            if (acaoJogador == 5) {
                fugiu = true;
                break;
            }

            int acaoInimigo = escolherAcaoComputador(random, inimigo);

            System.out.println("\n--- AÇÕES ---");

            if (jogador.getVelocidade() >= inimigo.getVelocidade()) {
                executarAcao(acaoJogador, jogador, inimigo);

                if (inimigo.estaVivo()) {
                    executarAcao(acaoInimigo, inimigo, jogador);
                }
            } else {
                executarAcao(acaoInimigo, inimigo, jogador);

                if (jogador.estaVivo()) {
                    executarAcao(acaoJogador, jogador, inimigo);
                }
            }

            if (jogador.estaVivo() && inimigo.estaVivo()) {
                System.out.print("\nPressione Enter para começar o próximo turno...");
                scanner.nextLine();
            }

            turno++;
        }

        System.out.println("\n=== FIM DA BATALHA ===");

        if (fugiu) {
            System.out.println("Você fugiu da batalha.");
        } else if (jogador.estaVivo()) {
            System.out.println("Você venceu! " + inimigo.getNome() + " foi derrotado.");
        } else {
            System.out.println("Você perdeu! " + jogador.getNome() + " foi derrotado.");
        }
    }

    // Recebe o nome e o tipo escolhido pelo jogador.
    private static Robo criarRobo(Scanner scanner) {
        String nome;

        while (true) {
            System.out.print("Digite o nome do seu robô (3 a 20 caracteres): ");

            nome = scanner.nextLine().trim();

            if (nome.isEmpty()) {
                System.out.println("O nome não pode ficar vazio.");
            } else if (nome.length() < 3 || nome.length() > 20) {
                System.out.println("O nome deve ter entre 3 e 20 caracteres.");
            } else {
                break;
            }
        }

        String[] tipos = {
                "Tanque: mais vida e defesa",
                "Atirador: mais energia",
                "Assassino: mais ataque e velocidade",
                "Hacker: remove energia do adversário",
                "Engenheiro: recupera vida e se protege"
        };

        System.out.println("\nEscolha o tipo do seu robô:");
        exibirMenu(tipos);

        int tipo = lerOpcao(scanner, tipos.length);

        switch (tipo) {
            case 1:
                return new Tanque(nome);
            case 2:
                return new Atirador(nome);
            case 3:
                return new Assassino(nome);
            case 4:
                return new Hacker(nome);
            case 5:
                return new Engenheiro(nome);
            default:
                throw new IllegalArgumentException("Tipo de robô desconhecido.");
        }
    }
    // Deixa o jogador escolher uma peça (ou nenhuma) para cada slot.
    private static void montarPecas(Scanner scanner, Robo robo) {
        System.out.println("\n=== MONTAGEM DE PEÇAS ===");
        System.out.println("Toda peça tem bônus e penalidade. Escolha com cuidado!");

        for (int slot = 0; slot < Catalogo.totalSlots(); slot++) {
            Peca[] opcoes = Catalogo.getOpcoes(slot);

            String[] menu = new String[opcoes.length + 1];
            menu[0] = "Nenhuma peça";

            for (int i = 0; i < opcoes.length; i++) {
                menu[i + 1] = opcoes[i].resumo();
            }

            System.out.println("\nSlot: " + Catalogo.NOMES_SLOTS[slot]);
            exibirMenu(menu);

            int escolha = lerOpcao(scanner, menu.length);

            if (escolha > 1) {
                robo.equipar(opcoes[escolha - 2]);
            }
        }
    }

    // O adversário recebe uma peça aleatória (ou nenhuma) em cada slot.
    private static void equiparAleatoriamente(Random random, Robo robo) {
        for (int slot = 0; slot < Catalogo.totalSlots(); slot++) {
            Peca[] opcoes = Catalogo.getOpcoes(slot);
            int sorteio = random.nextInt(opcoes.length + 1);

            if (sorteio > 0) {
                robo.equipar(opcoes[sorteio - 1]);
            }
        }
    }

    // Sorteia um dos cinco tipos de adversário.
    private static Robo criarInimigo(Random random) {
        Robo[] inimigos = {
                new Tanque("Titan"),
                new Atirador("Blaster"),
                new Assassino("Orion"),
                new Hacker("Glitch"),
                new Engenheiro("Fixtron")
        };

        String[] tipos = {
                "Tanque",
                "Atirador",
                "Assassino",
                "Hacker",
                "Engenheiro"
        };

        int indice = random.nextInt(inimigos.length);

        System.out.println("\nAdversário: " + inimigos[indice].getNome() + " | Tipo: " + tipos[indice]);

        return inimigos[indice];
    }

    // Percorre um array e exibe a suas opções numeradas.
    private static void exibirMenu(String[] opcoes) {
        for (int i = 0; i < opcoes.length; i++) {
            System.out.println((i + 1) + " - " + opcoes[i]);
        }
    }

    // Trata entradas não numéricas e opções fora do intervalo.
    private static int lerOpcao(Scanner scanner, int totalOpcoes) {
        while (true) {
            System.out.print("Sua opção: ");

            try {
                int opcao = Integer.parseInt(scanner.nextLine().trim());

                if (opcao >= 1 && opcao <= totalOpcoes) {
                    return opcao;
                }

                System.out.println("Escolha uma opção de 1 a " + totalOpcoes + ".");
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite um número inteiro.");
            }
        }
    }

    // Mostra as ações e verifica se o jogador pode usá-las.
    private static int escolherAcao(Scanner scanner, Robo jogador) {
        while (true) {
            String[] acoes = {
                    "Atacar",
                    "Defender",
                    jogador.getNomeHabilidade()
                            + " (" + Robo.CUSTO_ESPECIAL + " de energia)",
                    "Usar poção (" + jogador.getPocoes() + " restantes)",
                    "Fugir"
            };

            System.out.println("\nEscolha uma ação:");
            exibirMenu(acoes);

            int opcao = lerOpcao(scanner, acoes.length);

            if (opcao == 3 && !jogador.podeUsarEspecial()) {
                System.out.println("Você não tem energia suficiente!");
                continue;
            }

            if (opcao == 4 && !jogador.podeUsarItem()) {
                System.out.println("Você está sem poções ou sua vida já está cheia!");
                continue;
            }

            return opcao;
        }
    }

    // Sorteia uma ação entre as disponíveis para o computador.
    private static int escolherAcaoComputador(
            Random random, Robo robo) {

        int[] acoesDisponiveis = new int[4];
        int quantidade = 0;

        acoesDisponiveis[quantidade++] = 1;
        acoesDisponiveis[quantidade++] = 2;

        if (robo.podeUsarEspecial()) {
            acoesDisponiveis[quantidade++] = 3;
        }

        if (robo.podeUsarItem()) {
            acoesDisponiveis[quantidade++] = 4;
        }

        int indice = random.nextInt(quantidade);

        return acoesDisponiveis[indice];
    }

    // Executa a ação escolhida no alvo indicado.
    private static void executarAcao(int acao, Robo robo, Robo alvo) {
        robo.encerrarDefesa();
        switch (acao) {
            case 1:
                robo.atacar(alvo);
                break;
            case 2:
                robo.defender();
                break;
            case 3:
                robo.usarHabilidadeEspecial(alvo);
                break;
            case 4:
                robo.usarItem();
                break;
            default:
                System.out.println("Ação desconhecida.");
        }
    }
}