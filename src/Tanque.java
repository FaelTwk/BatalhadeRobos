public class Tanque extends Robo {

    public Tanque(String nome) {
        super(nome, 150, 60, 18, 10, 5);
    }

    @Override
    public String getNomeHabilidade() {
        return "Escudo Magnético";
    }

    @Override
    protected void executarHabilidade(Robo alvo) {
        defender();
        curar(20);
        alvo.receberDano(getAtaque() / 2);
    }
}