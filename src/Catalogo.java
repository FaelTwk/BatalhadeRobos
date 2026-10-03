import java.util.Arrays;
// Catálogo de peças em uma MATRIZ: cada linha é um slot, cada coluna é uma peça.

public class Catalogo {

    public static final String[] NOMES_SLOTS = {"ARMA", "BLINDAGEM", "MÓDULO"};
    private static final Peca[][] PECAS = {
            {   // slot 0: ARMA
                    new Peca("Canhão de Plasma",   0,   0, -10,  8,  0, -2),
                    new Peca("Lâminas Sônicas",    0,   0,   0,  5, -2,  3),
                    new Peca("Martelo Hidráulico", 0,   0,  -5, 10,  0, -5)
            },
            {   // slot 1: BLINDAGEM
                    new Peca("Blindagem Pesada",   1,  20,   0,  0,  6, -4),
                    new Peca("Escudo de Energia",  1,   0,  20, -2,  4,  0),
                    new Peca("Núcleo Reativo",     1, -15,  35,  0,  2,  0)
            },
            {   // slot 2: MÓDULO
                    new Peca("Turbina Íon",        2,   0, -10,  0, -2,  6),
                    new Peca("Reator Auxiliar",    2,   0,  25,  0,  0, -2),
                    new Peca("Servo Motores",      2, -10,   0,  2,  0,  4)
            }
    };

    // Bloco estático: roda uma vez e ordena cada linha usando o compareTo de Peca
    static {
        for (Peca[] linha : PECAS) {
            Arrays.sort(linha);
        }
    }

    public static int totalSlots() {
        return PECAS.length;
    }

    // Devolve uma CÓPIA da linha, assim ninguém altera o catálogo original
    public static Peca[] getOpcoes(int slot) {
        return Arrays.copyOf(PECAS[slot], PECAS[slot].length);
    }
}