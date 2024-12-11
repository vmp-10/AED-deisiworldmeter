package pt.ulusofona.aed.deisiworldmeter;

public class Pais {
    int id;
    String alfa2;
    String alfa3;
    String nome;
    int quantidadeLinhas700;
    int num600700;
    int line;

    public Pais(int id, String alfa2, String alfa3, String nome, int line) {
        this.id = id;
        this.alfa2 = alfa2;
        this.alfa3 = alfa3;
        this.nome = nome;
        this.quantidadeLinhas700 = 0;
        this.line = line;
        this.num600700 = 0;
    }

    public Pais() {

    }

    @Override
    public String toString() {
        if (id >= 600 && id <= 700) {
            return nome.toUpperCase() + " | " + id + " | cidades: " + num600700;
        } else if (quantidadeLinhas700 > 0) {
            return nome + " | " + id + " | " + alfa2.toUpperCase() + " | " + alfa3.toUpperCase() + " | " + quantidadeLinhas700;
        }
        return nome + " | " + id + " | " + alfa2.toUpperCase() + " | " + alfa3.toUpperCase();
    }
}