package pt.ulusofona.aed.deisiworldmeter;

public class InputInvalido {
    String nome;
    int linhasOK;
    int linhasNOK;
    int primeiraLinhaNOK;

    public InputInvalido(String nome, int linhasOK, int linhasNOK, int primeiraLinhaNOK) {
        this.nome = nome;
        this.linhasOK = linhasOK;
        this.linhasNOK = linhasNOK;
        this.primeiraLinhaNOK = primeiraLinhaNOK;
    }

    @Override
    public String toString() {
        return nome + " | " + linhasOK + " | " + linhasNOK + " | " + primeiraLinhaNOK;
    }
}
