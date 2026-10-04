// Exceção verificada: quem chama o método deve tratar ou declarar throws.
public class EnergiaInsuficienteException extends Exception {
    private static final long serialVersionUID = 1L;

    public EnergiaInsuficienteException(String nome, int atual, int custo) {
        super(nome + " precisa de " + custo + " de energia, mas tem " + atual + ".");
    }
}
