package pt.ulusofona.aed.deisiworldmeter;

public class Cidade {
    String alfa2;
    String nome;
    String regiao;
    String populacao;
    String latitude;
    String longitude;

    public Cidade(String alfa2, String cidade, String regiao, String populacao, String latitude, String longitude) {
        this.alfa2 = alfa2;
        this.nome = cidade;
        this.regiao = regiao;
        this.populacao = populacao;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Cidade() {

    }


    @Override
    public String toString() {
        return nome + " | " + alfa2.toUpperCase() + " | " + regiao + " | " + Double.valueOf(populacao).intValue() + " | (" + latitude + "," + longitude + ")";
    }
}
