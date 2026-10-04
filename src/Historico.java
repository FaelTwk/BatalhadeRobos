import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class Historico {
    // StringBuilder monta o texto sem sobrescrever os eventos anteriores.
    private final StringBuilder texto = new StringBuilder();

    public Historico() {
        texto.append("\n=== NOVA BATALHA ===\n");
        texto.append("Data e hora: " + LocalDateTime.now() + "\n");
    }

    public void registrar(String mensagem) {
        System.out.println(mensagem);
        texto.append(mensagem);
        texto.append("\n");
    }

    public void salvar(Path arquivo) throws IOException {
        // true significa acrescentar ao arquivo sem apagar as partidas anteriores.
        // try-with-resources fecha o arquivo automaticamente, mesmo em caso de erro.
        try (FileWriter escritor = new FileWriter(
                arquivo.toFile(), StandardCharsets.UTF_8, true)) {
            escritor.write(texto.toString());
        }
    }
}
