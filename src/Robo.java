import java.util.ArrayList;
import java.util.Locale;

public abstract class Robo {
    private final String nome;
    private int vida;
    private int vidaMaxima;
    private int energia;
    private int energiaMaxima;
    private int ataque;
    private int defesa;
    private int velocidade;
    private boolean defendendo;
    private Historico historico;

    // Atributos originais do tipo, antes de somar os bônus das peças.
    private final int vidaBase;
    private final int energiaBase;
    private final int ataqueBase;
    private final int defesaBase;
    private final int velocidadeBase;
    private final Peca[] pecas = new Peca[Catalogo.totalSlots()];
    private final ArrayList<Item> inventario = new ArrayList<>();

    public static final int CUSTO_ATAQUE = 8;
    public static final int CUSTO_ESPECIAL = 20;
    public static final int RECUPERACAO_RODADA = 5;

    public Robo(String nome, int vida, int energia, int ataque, int defesa, int velocidade) {
        this.nome = nome;
        this.vidaBase = vida;
        this.energiaBase = energia;
        this.ataqueBase = ataque;
        this.defesaBase = defesa;
        this.velocidadeBase = velocidade;
        recalcularAtributos();
        this.vida = vidaMaxima;
        this.energia = energiaMaxima;
        // add: cada posição representa um consumível independente.
        inventario.add(new Item(Item.POCAO));
        inventario.add(new Item(Item.POCAO));
        inventario.add(new Item(Item.BATERIA));
        inventario.add(new Item(Item.GRANADA));
    }

    public abstract String getNomeHabilidade();
    protected abstract void executarHabilidade(Robo alvo);

    public void setHistorico(Historico historico) { this.historico = historico; }

    protected void registrar(String mensagem) {
        if (historico == null) {
            System.out.println(mensagem);
        } else {
            historico.registrar(mensagem);
        }
    }

    private void gastarEnergia(int custo) throws EnergiaInsuficienteException {
        if (energia < custo) {
            throw new EnergiaInsuficienteException(nome, energia, custo);
        }
        energia -= custo;
    }

    public void atacar(Robo alvo) throws EnergiaInsuficienteException {
        if (!estaVivo() || !alvo.estaVivo()) {
            return;
        }
        gastarEnergia(CUSTO_ATAQUE);
        registrar(nome + " usou ataque comum!");
        alvo.receberDano(ataque);
    }

    public void defender() {
        defendendo = true;
        registrar(nome + " se preparou para defender!");
    }

    public void encerrarDefesa() { defendendo = false; }

    public void usarHabilidadeEspecial(Robo alvo) throws EnergiaInsuficienteException {
        if (!estaVivo() || !alvo.estaVivo()) {
            return;
        }
        gastarEnergia(CUSTO_ESPECIAL);
        registrar(nome + " usou " + getNomeHabilidade().toUpperCase(Locale.ROOT) + "!");
        executarHabilidade(alvo);
    }

    public void receberDano(int forcaDoGolpe) { receberDano(forcaDoGolpe, false); }

    public void receberDano(int forcaDoGolpe, boolean ignoraDefesa) {
        if (!estaVivo()) {
            return;
        }

        int dano = forcaDoGolpe;
        if (!ignoraDefesa) {
            dano = dano - defesa;
        }
        if (dano < 1) {
            dano = 1;
        }

        if (defendendo) {
            dano = dano / 2;
            if (dano < 1) {
                dano = 1;
            }
            defendendo = false;
            registrar(nome + " reduziu o dano com sua defesa!");
        }
        vida = vida - dano;
        if (vida < 0) {
            vida = 0;
        }
        registrar(nome + " recebeu " + dano + " de dano.");
    }

    public void curar(int valor) {
        if (!estaVivo() || valor <= 0) {
            return;
        }
        int vidaAntes = vida;
        vida = vida + valor;
        if (vida > vidaMaxima) {
            vida = vidaMaxima;
        }
        int recuperacao = vida - vidaAntes;
        registrar(nome + " recuperou " + recuperacao + " de vida!");
    }

    public void recuperarEnergia(int valor) {
        if (!estaVivo() || valor <= 0) {
            return;
        }
        int energiaAntes = energia;
        energia = energia + valor;
        if (energia > energiaMaxima) {
            energia = energiaMaxima;
        }
        int recuperacao = energia - energiaAntes;
        if (recuperacao > 0) {
            registrar(nome + " recuperou " + recuperacao + " de energia.");
        }
    }

    public void reduzirEnergia(int quantidade) {
        if (quantidade <= 0) {
            return;
        }
        int energiaAntes = energia;
        energia = energia - quantidade;
        if (energia < 0) {
            energia = 0;
        }
        int removida = energiaAntes - energia;
        registrar(nome + " perdeu " + removida + " de energia!");
    }

    public int getQuantidadeItens() { return inventario.size(); }
    public boolean inventarioVazio() { return inventario.isEmpty(); }
    public Item getItem(int indice) { return inventario.get(indice); }

    public boolean podeUsarItem(int indice) {
        if (indice < 0 || indice >= inventario.size()) {
            return false;
        }
        Item item = inventario.get(indice);
        return item.podeUsar(this);
    }

    public boolean podeUsarItem() {
        if (inventario.isEmpty()) {
            return false;
        }
        for (Item item : inventario) {
            if (item.podeUsar(this)) {
                return true;
            }
        }
        return false;
    }

    public boolean usarItem(int indice, Robo alvo) {
        if (!estaVivo() || !alvo.estaVivo()) {
            return false;
        }
        if (!podeUsarItem(indice)) {
            return false;
        }
        Item item = inventario.get(indice);
        registrar(nome + " usou " + item.getNome() + "!");
        item.aplicar(this, alvo);
        inventario.remove(indice); // Remove somente após o uso válido.
        return true;
    }

    // Equipar só é chamado na preparação; começa com recursos cheios.
    public void equipar(Peca nova) {
        if (nova == null) {
            return;
        }
        if (nova.getSlot() < 0 || nova.getSlot() >= pecas.length) {
            throw new IllegalArgumentException("Slot de peça inválido.");
        }
        pecas[nova.getSlot()] = nova;
        recalcularAtributos();
        vida = vidaMaxima;
        energia = energiaMaxima;
    }

    private void recalcularAtributos() {
        vidaMaxima = vidaBase;
        energiaMaxima = energiaBase;
        ataque = ataqueBase;
        defesa = defesaBase;
        velocidade = velocidadeBase;
        for (Peca peca : pecas) {
            if (peca == null) {
                continue;
            }
            vidaMaxima += peca.getBonusVida();
            energiaMaxima += peca.getBonusEnergia();
            ataque += peca.getBonusAtaque();
            defesa += peca.getBonusDefesa();
            velocidade += peca.getBonusVelocidade();
        }
        vidaMaxima = Math.max(1, vidaMaxima);
        energiaMaxima = Math.max(0, energiaMaxima);
        ataque = Math.max(1, ataque);
        defesa = Math.max(0, defesa);
        velocidade = Math.max(1, velocidade);
    }

    public String resumoFicha() {
        String[] nomes = new String[pecas.length];
        for (int i = 0; i < pecas.length; i++) {
            if (pecas[i] == null) {
                nomes[i] = "-";
            } else {
                nomes[i] = pecas[i].getNome();
            }
        }
        return String.format("%s (%s) | VIDA %d | ENERGIA %d | ATAQUE %d | DEFESA %d | VELOCIDADE %d%nPeças: %s",
                nome, getClass().getSimpleName(), vidaMaxima, energiaMaxima,
                ataque, defesa, velocidade, String.join(", ", nomes));
    }

    public void exibirFicha() { System.out.println(resumoFicha()); }

    private String barra(int atual, int maximo) {
        int preenchidos = 0;
        if (maximo > 0) {
            preenchidos = atual * 10 / maximo;
        }
        StringBuilder barra = new StringBuilder("[");
        for (int i = 0; i < 10; i++) {
            if (i < preenchidos) {
                barra.append('#');
            } else {
                barra.append('-');
            }
        }
        barra.append(']');
        return barra.toString();
    }

    public String resumoStatus() {
        return String.format("%s%nVida: %s %d/%d%nEnergia: %s %d/%d",
                nome, barra(vida, vidaMaxima), vida, vidaMaxima,
                barra(energia, energiaMaxima), energia, energiaMaxima);
    }

    public void exibirStatus() { System.out.println(resumoStatus()); }
    public boolean estaVivo() { return vida > 0; }
    public boolean podeAtacar() { return energia >= CUSTO_ATAQUE; }
    public boolean podeUsarEspecial() { return energia >= CUSTO_ESPECIAL; }
    public String getNome() { return nome; }
    public int getVida() { return vida; }
    public int getVidaMaxima() { return vidaMaxima; }
    public int getEnergia() { return energia; }
    public int getEnergiaMaxima() { return energiaMaxima; }
    public int getAtaque() { return ataque; }
    public int getDefesa() { return defesa; }
    public int getVelocidade() { return velocidade; }
}
