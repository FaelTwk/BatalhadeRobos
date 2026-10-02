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
    private int pocoes;

    public static final int CUSTO_ESPECIAL = 20;

    public Robo(String nome, int vida, int energia, int ataque, int defesa, int velocidade) {
        this.nome = nome;
        this.vida = vida;
        this.vidaMaxima = vida;
        this.energia = energia;
        this.energiaMaxima = energia;
        this.ataque = ataque;
        this.defesa = defesa;
        this.velocidade = velocidade;
        this.defendendo = false;
        this.pocoes = 2;
    }

    public abstract String getNomeHabilidade();

    protected abstract void executarHabilidade(Robo alvo);

    public void atacar(Robo alvo) {
        atacar(alvo, 1.0);
    }

    public void atacar(Robo alvo, double multiplicador) {
        if (!estaVivo() || !alvo.estaVivo()) {
            return;
        }

        System.out.println(nome + " usou ataque comum!");
        alvo.receberDano((int) (ataque * multiplicador));
    }

    public void defender() {
        defendendo = true;
        System.out.println(nome + " se preparou para defender!");
    }

    public void usarHabilidadeEspecial(Robo alvo) {
        if (!estaVivo() || !alvo.estaVivo()) {
            return;
        }

        if (!podeUsarEspecial()) {
            System.out.println("Energia insuficiente!");
            return;
        }

        energia -= CUSTO_ESPECIAL;

        System.out.println(nome + " usou " + getNomeHabilidade().toUpperCase() + "!");
        executarHabilidade(alvo);
    }

    public void receberDano(int forcaDoGolpe) {
        receberDano(forcaDoGolpe, false);
    }

    public void receberDano(int forcaDoGolpe, boolean ignoraDefesa) {
        int dano = ignoraDefesa ? forcaDoGolpe : Math.max(1, forcaDoGolpe - defesa);

        if (defendendo) {
            dano = Math.max(1, dano / 2);
            defendendo = false;
            System.out.println(nome + " reduziu o dano com sua defesa!");
        }

        vida = Math.max(0, vida - dano);

        System.out.println(nome + " recebeu " + dano + " de dano.");
    }

    public void curar(int valor) {
        int recuperacao = Math.min(valor, vidaMaxima - vida);
        vida += recuperacao;
        System.out.println(nome + " recuperou " + recuperacao + " de vida!");
    }

    public void reduzirEnergia(int quantidade) {
        if (quantidade <= 0) {
            return;
        }

        int energiaRemovida = Math.min(quantidade, energia);
        energia -= energiaRemovida;

        System.out.println(nome + " perdeu " + energiaRemovida + " de energia!");
    }



    public void usarItem() {
        if (!podeUsarItem()) {
            System.out.println("Não é possível usar uma poção agora.");
            return;
        }

        int recuperacao = Math.min(30, vidaMaxima - vida);

        vida += recuperacao;
        pocoes--;

        System.out.println(nome + " usou uma poção e recuperou " + recuperacao + " de vida!");
    }

    public boolean estaVivo() {
        return vida > 0;
    }

    public boolean podeUsarEspecial() {
        return energia >= CUSTO_ESPECIAL;
    }

    public boolean podeUsarItem() {
        return estaVivo() && pocoes > 0 && vida < vidaMaxima;
    }

    public String getNome() {
        return nome;
    }

    public int getAtaque() {
        return ataque;
    }

    public int getDefesa() {
        return defesa;
    }

    public int getVelocidade() {
        return velocidade;
    }

    public int getPocoes() {
        return pocoes;
    }

    public void exibirStatus() {
        System.out.println(nome);
        System.out.println("Vida: " + vida + "/" + vidaMaxima);
        System.out.println("Energia: " + energia + "/" + energiaMaxima);
    }
}