import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;
import java.util.Scanner;

public class Main {
    // 1. PREPARAÇÃO: cria os robôs e monta as peças.
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            Random random = new Random();
            System.out.println("""
                === BATALHA DE ROBÔS ===
                Ataque: 8 de energia | Habilidade: 20 de energia.
                Defender não gasta energia. Cada rodada concluída recupera 5 de energia.
                Fuga: depende da velocidade. Se falhar, o inimigo age.
                """);
            Robo jogador = criarRobo(scanner);
            montarPecas(scanner, jogador);
            Robo inimigo = criarInimigo(random);
            equiparAleatoriamente(random, inimigo);
            iniciarBatalha(scanner, random, jogador, inimigo);
        }
    }

    // 2. BATALHA: repete a escolha e a execução das ações.
    private static void iniciarBatalha(Scanner scanner, Random random,
                                       Robo jogador, Robo inimigo) {
        Historico historico = new Historico();
        jogador.setHistorico(historico);
        inimigo.setHistorico(historico);
        historico.mostrar("JOGADOR: " + jogador.resumoFicha());
        historico.mostrar("INIMIGO: " + inimigo.resumoFicha());
        int turno = 1;
        boolean fugiu = false;

        while (jogador.estaVivo() && inimigo.estaVivo()) {
            historico.mostrar("\n========== TURNO " + turno + " ==========");
            historico.mostrar("SEU ROBÔ:\n" + jogador.resumoStatus());
            historico.mostrar("INIMIGO:\n" + inimigo.resumoStatus());
            historico.anotar("T" + turno + " | " + jogador.resumoCompacto() + " | " + inimigo.resumoCompacto());

            // Cada variável guarda uma informação, sem índices escondidos.
            int acaoJogador;
            int itemJogador = -1;
            do {
                acaoJogador = escolherAcao(scanner, jogador);
                if (acaoJogador == 4) {
                    itemJogador = escolherItem(scanner, jogador);
                }
                // -1 significa que o jogador voltou do menu de itens.
            } while (acaoJogador == 4 && itemJogador == -1);

            int acaoInimigo = escolherAcaoComputador(random, inimigo);
            int itemInimigo = -1;
            if (acaoInimigo == 4) {
                itemInimigo = escolherItemComputador(random, inimigo);
            }
            historico.mostrar("\n--- AÇÕES ---");

            if (acaoJogador == 5) {
                // A tentativa acontece antes dos ataques e gasta a ação do jogador.
                jogador.encerrarDefesa();
                int chance = chanceDeFuga(jogador, inimigo);
                fugiu = random.nextInt(100) < chance;
                historico.registrar("Chance de fuga: " + chance + "%.");
                if (fugiu) {
                    break;
                }
                historico.registrar("A fuga falhou! O adversário realiza sua ação.");
                executarAcao(acaoInimigo, itemInimigo, inimigo, jogador, historico);
            } else if (jogador.getVelocidade() >= inimigo.getVelocidade()) {
                executarAcao(acaoJogador, itemJogador, jogador, inimigo, historico);
                if (inimigo.estaVivo()) {
                    executarAcao(acaoInimigo, itemInimigo, inimigo, jogador, historico);
                }
            } else {
                executarAcao(acaoInimigo, itemInimigo, inimigo, jogador, historico);
                if (jogador.estaVivo()) {
                    executarAcao(acaoJogador, itemJogador, jogador, inimigo, historico);
                }
            }

            // Robôs destruídos não recuperam energia.
            jogador.recuperarEnergia(Robo.RECUPERACAO_RODADA);
            inimigo.recuperarEnergia(Robo.RECUPERACAO_RODADA);
            if (jogador.estaVivo() && inimigo.estaVivo()) {
                System.out.print("\nPressione Enter para começar o próximo turno...");
                scanner.nextLine();
                turno++;
            }
        }

        historico.mostrar("\n=== FIM DA BATALHA ===");
        if (fugiu) {
            historico.registrar("Você fugiu da batalha.");
        } else if (jogador.estaVivo()) {
            historico.registrar("Você venceu! " + inimigo.getNome() + " foi derrotado.");
        } else {
            historico.registrar("Você perdeu! " + jogador.getNome() + " foi derrotado.");
        }
        historico.registrar("Rodadas: " + turno);
        historico.mostrar("Estado final:\n" + jogador.resumoStatus() + "\n" + inimigo.resumoStatus());
        historico.anotar("FIM | " + jogador.resumoCompacto() + " | " + inimigo.resumoCompacto());

        Path arquivo = Path.of("historico-batalhas.txt").toAbsolutePath();
        try {
            historico.salvar(arquivo);
            System.out.println("Histórico salvo em: " + arquivo);
        } catch (IOException e) {
            System.out.println("A batalha terminou, mas não foi possível salvar o histórico: " + e.getMessage());
        }
    }

    private static int chanceDeFuga(Robo jogador, Robo inimigo) {
        int diferenca = jogador.getVelocidade() - inimigo.getVelocidade();
        int chance = 50 + 2 * diferenca;

        if (chance < 15) {
            chance = 15;
        }
        if (chance > 85) {
            chance = 85;
        }
        return chance;
    }

    private static Robo criarRobo(Scanner scanner) {
        String nome;
        while (true) {
            System.out.print("Nome do robô (3 a 20 caracteres; digite ajuda para instruções): ");
            nome = scanner.nextLine().trim();
            // equalsIgnoreCase aceita AJUDA, ajuda, Ajuda etc.
            if (nome.equalsIgnoreCase("ajuda")) {
                System.out.println("Escolha um nome, um tipo e uma peça opcional para cada slot.");
                continue;
            }
            if (nome.isEmpty()) {
                System.out.println("O nome não pode ficar vazio.");
                continue;
            }
            // split separa palavras; join normaliza espaços repetidos no nome.
            nome = String.join(" ", nome.split("\\s+"));
            if (nome.length() >= 3 && nome.length() <= 20) {
                break;
            }
            System.out.println("O nome deve ter entre 3 e 20 caracteres.");
        }
        String[] tipos = {
                "Tanque: mais vida e defesa", "Atirador: mais energia",
                "Assassino: mais ataque e velocidade", "Hacker: remove energia",
                "Engenheiro: recupera vida e se protege"
        };
        exibirMenu(tipos);
        switch (lerOpcao(scanner, tipos.length)) {
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

    private static void equiparAleatoriamente(Random random, Robo robo) {
        for (int slot = 0; slot < Catalogo.totalSlots(); slot++) {
            Peca[] opcoes = Catalogo.getOpcoes(slot);
            int sorteio = random.nextInt(opcoes.length + 1);
            if (sorteio > 0) {
                robo.equipar(opcoes[sorteio - 1]);
            }
        }
    }

    private static Robo criarInimigo(Random random) {
        Robo[] inimigos = {
                new Tanque("Titan"), new Atirador("Blaster"), new Assassino("Orion"),
                new Hacker("Glitch"), new Engenheiro("Fixtron")
        };
        return inimigos[random.nextInt(inimigos.length)];
    }

    private static void exibirMenu(String[] opcoes) {
        for (int i = 0; i < opcoes.length; i++) {
            System.out.println((i + 1) + " - " + opcoes[i]);
        }
    }

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

    private static int escolherAcao(Scanner scanner, Robo jogador) {
        while (true) {
            String[] acoes = {
                    "Atacar (" + Robo.CUSTO_ATAQUE + " de energia)", "Defender",
                    jogador.getNomeHabilidade() + " (" + Robo.CUSTO_ESPECIAL + " de energia)",
                    "Usar item (" + jogador.getQuantidadeItens() + " restantes)", "Tentar fugir"
            };
            exibirMenu(acoes);
            int opcao = lerOpcao(scanner, acoes.length);
            if (opcao == 1 && !jogador.podeAtacar()) {
                System.out.println("Sem energia para atacar. Defenda ou use uma bateria.");
                continue;
            }
            if (opcao == 3 && !jogador.podeUsarEspecial()) {
                System.out.println("Sem energia para a habilidade. Defenda ou use uma bateria.");
                continue;
            }
            return opcao;
        }
    }

    private static int escolherItem(Scanner scanner, Robo robo) {
        if (robo.inventarioVazio()) {
            System.out.println("Inventário vazio!");
            return -1;
        }
        while (true) {
            String[] menu = new String[robo.getQuantidadeItens() + 1];
            for (int i = 0; i < robo.getQuantidadeItens(); i++) {
                menu[i] = robo.getItem(i).getNome();
                if (!robo.podeUsarItem(i)) {
                    menu[i] = menu[i] + " [indisponível agora]";
                }
            }
            menu[menu.length - 1] = "Voltar";
            exibirMenu(menu);
            int escolha = lerOpcao(scanner, menu.length);
            if (escolha == menu.length) {
                return -1;
            }
            int indice = escolha - 1;
            if (robo.podeUsarItem(indice)) {
                return indice;
            }
            System.out.println("Esse recurso já está cheio. Escolha outro item ou volte.");
        }
    }

    private static int escolherAcaoComputador(Random random, Robo robo) {
        int[] acoes = new int[4];
        int quantidade = 0;
        if (robo.podeAtacar()) {
            acoes[quantidade] = 1;
            quantidade++;
        }

        acoes[quantidade] = 2; // Defender sempre é permitido.
        quantidade++;

        if (robo.podeUsarEspecial()) {
            acoes[quantidade] = 3;
            quantidade++;
        }
        if (robo.podeUsarItem()) {
            acoes[quantidade] = 4;
            quantidade++;
        }

        int sorteio = random.nextInt(quantidade);
        return acoes[sorteio];
    }

    private static int escolherItemComputador(Random random, Robo robo) {
        int[] itensDisponiveis = new int[robo.getQuantidadeItens()];
        int quantidade = 0;

        for (int i = 0; i < robo.getQuantidadeItens(); i++) {
            if (robo.podeUsarItem(i)) {
                itensDisponiveis[quantidade] = i;
                quantidade++;
            }
        }

        // Este método só é chamado quando há um item utilizável.
        int sorteio = random.nextInt(quantidade);
        return itensDisponiveis[sorteio];
    }

    private static void executarAcao(int acao, int indiceItem,
                                     Robo robo, Robo alvo, Historico historico) {
        robo.encerrarDefesa();
        try {
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
                    if (!robo.usarItem(indiceItem, alvo)) {
                        historico.registrar("Item indisponível na execução. " + robo.getNome() + " vai defender.");
                        robo.defender();
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Ação desconhecida.");
            }
        } catch (EnergiaInsuficienteException e) {
            // O Hacker pode retirar energia DEPOIS que a ação foi escolhida.
            historico.registrar(e.getMessage() + " A ação foi substituída por defesa.");
            robo.defender();
        }
    }
}
