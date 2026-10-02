public class Engenheiro extends Robo {

    public Engenheiro(String nome) {
        super(nome, 120, 80, 20, 7, 10);
    }

    @Override
    public String getNomeHabilidade() {
        return "Autorreparo";
    }

    @Override
    protected void executarHabilidade(Robo alvo) {
        curar(25);
        defender();
    }
}
