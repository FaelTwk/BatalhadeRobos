public class Assassino extends Robo {

    public Assassino(String nome) {
        super(nome, 80, 80, 30, 3, 25);
    }

    @Override
    public String getNomeHabilidade() {
        return "Golpe Crítico";
    }

    @Override
    protected void executarHabilidade(Robo alvo) {
        alvo.receberDano(getAtaque() * 3 / 2, true);
    }
}