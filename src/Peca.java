import java.util.StringJoiner;
// Peça de upgrade. Toda peça tem BÔNUS e PENALIDADE (trade-off).
// Implementa Comparable para poder ser ordenada com Arrays.sort().

public class Peca implements Comparable<Peca> {
    private final String nome;
    private final int slot;        // 0 = arma, 1 = blindagem, 2 = módulo
    private final int bonusVida;
    private final int bonusEnergia;
    private final int bonusAtaque;
    private final int bonusDefesa;
    private final int bonusVelocidade;

    public Peca(String nome, int slot, int bonusVida, int bonusEnergia, int bonusAtaque, int bonusDefesa, int bonusVelocidade) {
        this.nome = nome;
        this.slot = slot;
        this.bonusVida = bonusVida;
        this.bonusEnergia = bonusEnergia;
        this.bonusAtaque = bonusAtaque;
        this.bonusDefesa = bonusDefesa;
        this.bonusVelocidade = bonusVelocidade;
    }

    public String getNome() { return nome; }
    public int getSlot() { return slot; }
    public int getBonusVida() { return bonusVida; }
    public int getBonusEnergia() { return bonusEnergia; }
    public int getBonusAtaque() { return bonusAtaque; }
    public int getBonusDefesa() { return bonusDefesa; }
    public int getBonusVelocidade() { return bonusVelocidade; }

    // Ex.: "Canhão de Plasma [ENERGIA -10, ATAQUE +8, VELOCIDADE -2]"
    // Só mostra o que a peça realmente altera.
    public String resumo() {
        StringJoiner joiner = new StringJoiner(", ", " [", "]");

        if (bonusVida != 0)       joiner.add(String.format("VIDA %+d", bonusVida));
        if (bonusEnergia != 0)    joiner.add(String.format("ENERGIA %+d", bonusEnergia));
        if (bonusAtaque != 0)     joiner.add(String.format("ATAQUE %+d", bonusAtaque));
        if (bonusDefesa != 0)     joiner.add(String.format("DEFESA %+d", bonusDefesa));
        if (bonusVelocidade != 0) joiner.add(String.format("VELOCIDADE %+d", bonusVelocidade));

        return nome + joiner;
    }

    // Ordem alfabética pelo nome
    @Override
    public int compareTo(Peca outra) {
        return this.nome.compareTo(outra.nome);
    }
}