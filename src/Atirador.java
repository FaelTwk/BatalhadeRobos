public class Atirador extends Robo {

    public Atirador(String nome) {
        super(nome, 100, 100, 25, 5, 15);
    }

    @Override
    public String getNomeHabilidade() {
        return "Rajada Tripla";
    }

    @Override
    protected void executarHabilidade(Robo alvo) {
        for (int tiro = 1; tiro <= 3; tiro++) {
            if (!alvo.estaVivo()) {
                break;
            }
            System.out.println("Tiro " + tiro + "!");
            alvo.receberDano(getAtaque() * 7 / 10);
        }
    }
}