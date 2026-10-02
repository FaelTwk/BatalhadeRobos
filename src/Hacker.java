public class Hacker extends Robo {

    public Hacker(String nome) {
        super(nome, 90, 100, 22, 4, 20);
    }

    @Override
    public String getNomeHabilidade() {
        return "Pulse EMP";
    }

    @Override
    protected void executarHabilidade(Robo alvo) {
        alvo.receberDano(10, true);
        alvo.reduzirEnergia(20);
    }

}
