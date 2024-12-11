package pt.ulusofona.aed.deisiworldmeter;

public class Populacao {
    int id;
    int ano;
    int populacaoMasculina;
    int populacaoFemenina;
    float densidade;
    int line;

    public Populacao(int id, int ano, int populacaoMasculina, int populacaoFemenina, float densidade, int line) {
        this.id = id;
        this.ano = ano;
        this.populacaoMasculina = populacaoMasculina;
        this.populacaoFemenina = populacaoFemenina;
        this.densidade = densidade;
        this.line = line;
    }

    public Populacao() {

    }

    @Override
    public String toString() {
        return id + " | " + ano + " | " + populacaoMasculina + " | " + populacaoFemenina + " | " + densidade;
    }
}
