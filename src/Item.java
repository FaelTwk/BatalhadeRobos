public class Item {
    public static final int POCAO = 1;
    public static final int BATERIA = 2;
    public static final int GRANADA = 3;

    private final int tipo;

    public Item(int tipo) {
        if (tipo < POCAO || tipo > GRANADA) {
            throw new IllegalArgumentException("Tipo de item inválido.");
        }
        this.tipo = tipo;
    }

    public String getNome() {
        switch (tipo) {
            case POCAO:
                return "Poção (+30 de vida)";
            case BATERIA:
                return "Bateria (+30 de energia)";
            default:
                return "Granada (25 de dano, ignora blindagem)";
        }
    }

    public boolean podeUsar(Robo usuario) {
        if (!usuario.estaVivo()) {
            return false;
        }
        switch (tipo) {
            case POCAO:
                return usuario.getVida() < usuario.getVidaMaxima();
            case BATERIA:
                return usuario.getEnergia() < usuario.getEnergiaMaxima();
            default:
                return true;
        }
    }

    public void aplicar(Robo usuario, Robo alvo) {
        switch (tipo) {
            case POCAO:
                usuario.curar(30);
                break;
            case BATERIA:
                usuario.recuperarEnergia(30);
                break;
            case GRANADA:
                alvo.receberDano(25, true);
                break;
            default:
                throw new IllegalStateException("Item desconhecido.");
        }
    }
}
