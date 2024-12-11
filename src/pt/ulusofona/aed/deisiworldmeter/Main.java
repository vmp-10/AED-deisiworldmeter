package pt.ulusofona.aed.deisiworldmeter;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    //Uso Geral
    public static ArrayList<Populacao> populacoes = new ArrayList<>();
    public static ArrayList<Cidade> cidades = new ArrayList<>();
    public static ArrayList<Pais> paises = new ArrayList<>();
    public static ArrayList<InputInvalido> input_invalido = new ArrayList<>();

    //Usados no parseFiles
    public static ArrayList<String> a2 = new ArrayList<>();
    public static ArrayList<Integer> ids = new ArrayList<>();

    //Usados nas queries
    public static HashMap<Integer, Populacao> populacaoPorID2024 = new HashMap<>();
    public static HashMap<Integer, Populacao> populacaoPorID = new HashMap<>();

    public static HashMap<Pais, ArrayList<Cidade>> cidadePorPais = new HashMap<>(); //alterar key para a2 ou ids
    public static HashMap<String, ArrayList<Cidade>> cidadePorA2 = new HashMap<>(); //alterar key para a2 ou ids
    public static HashMap<String, ArrayList<Cidade>> cidadesPorPaisNome = new HashMap<>();
    public static HashMap<String, Cidade> cidadePorNome = new HashMap<>();

    public static HashMap<Integer, Pais> paisPorID = new HashMap<>();
    public static HashMap<String, Pais> paisPorA2 = new HashMap<>();
    public static HashMap<String, Pais> paisPorNome = new HashMap<>();

    public static ArrayList getObjects(TipoEntidade tipo) {
        return switch (tipo) {
            case PAIS -> paises;
            case CIDADE -> cidades;
            case INPUT_INVALIDO -> input_invalido;
            default -> new ArrayList();
        };
    }

    private static void adicionaInvalido(InputInvalido inputInvalido, int linhaAtual) {
        if (inputInvalido.primeiraLinhaNOK == -1) {
            inputInvalido.primeiraLinhaNOK = linhaAtual;
        }
        inputInvalido.linhasNOK++;
    }

    public static void removePaisesSemCidades() {
        ArrayList<Pais> paisesSemCidades = new ArrayList<>();
        ArrayList<String> a2Invalidos = new ArrayList<>();
        ArrayList<Integer> idsInvalidos = new ArrayList<>();
        ArrayList<String> nomesInvalidos = new ArrayList<>();

        int linhaPais = -1;
        for (Pais pais : paises) {
            ArrayList<Cidade> cities = cidadePorPais.get(pais);

            if (cities.isEmpty()) {
                if (linhaPais == -1) {
                    linhaPais = pais.line; //para compensar os indexes
                }
                paisesSemCidades.add(pais);
                a2Invalidos.add(pais.alfa2);
                idsInvalidos.add(pais.id);
                nomesInvalidos.add(pais.nome);
            }
        }

        for (Pais pais : paisesSemCidades) {
            cidadePorPais.remove(pais);
        }

        paises.removeAll(paisesSemCidades);
        a2.removeAll(a2Invalidos);
        ids.removeAll(idsInvalidos);


        ArrayList<Populacao> populacoesInvalidas = new ArrayList<>();
        int linhaPopulacao = -1;
        for (Populacao populacao : populacoes) {
            for (Integer idInvalido : idsInvalidos) {
                if (populacao.id == idInvalido) {
                    populacoesInvalidas.add(populacao);
                    if (linhaPopulacao == -1) {
                        linhaPopulacao = populacao.line;
                    }
                }
            }
        }

        a2Invalidos.forEach(a2invalido -> cidadePorA2.remove(a2invalido));  //for loop itera cada a2 e remove
        a2Invalidos.forEach(a2invalido -> paisPorA2.remove(a2invalido));
        idsInvalidos.forEach(idinvalido -> paisPorID.remove(idinvalido));   //for loop itera cada id e remove
        idsInvalidos.forEach(idinvalido -> populacaoPorID2024.remove(idinvalido));
        idsInvalidos.forEach(idinvalido -> populacaoPorID.remove(idinvalido));
        nomesInvalidos.forEach(nomeInvalidos -> paisPorNome.remove(nomeInvalidos));

        populacoes.removeAll(populacoesInvalidas);


        if (!input_invalido.isEmpty()) {
            input_invalido.get(0).linhasOK -= paisesSemCidades.size();
            input_invalido.get(0).linhasNOK += paisesSemCidades.size();

            if (linhaPais < input_invalido.get(0).primeiraLinhaNOK && linhaPais != -1) {
                input_invalido.get(0).primeiraLinhaNOK = linhaPais;
            }

            input_invalido.get(2).linhasOK -= populacoesInvalidas.size();
            input_invalido.get(2).linhasNOK += populacoesInvalidas.size();

            if (linhaPopulacao < input_invalido.get(2).primeiraLinhaNOK && linhaPopulacao != -1) {
                input_invalido.get(2).primeiraLinhaNOK = linhaPopulacao;
            }
        }
    }

    public static boolean parsePopulacao(File file) {
        InputInvalido inputInvalido = new InputInvalido("populacao.csv", 0, 0, -1);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String linha;
            int linhaAtual = 1;
            boolean primeiraLinha = true;

            while ((linha = bufferedReader.readLine()) != null) {
                if (primeiraLinha) {
                    primeiraLinha = false;
                } else {
                    String[] partes = linha.split(",");
                    if (partes.length == 5) {
                        boolean paisExiste = false;
                        boolean parteVazia = false;

                        for (String parte : partes) {
                            if (parte.isEmpty()) {
                                parteVazia = true;
                                break;
                            }
                        }

                        int id = Integer.parseInt(partes[0]);

                        for (Pais pais : paises) {
                            if (id == pais.id) {
                                paisExiste = true;
                                if (pais.id >= 700) {
                                    pais.quantidadeLinhas700++;
                                }
                                break;
                            }
                        }

                        if (paisExiste && !parteVazia) {
                            inputInvalido.linhasOK++;

                            Populacao populacao = new Populacao(id, Integer.parseInt(partes[1]),
                                    Integer.parseInt(partes[2]), Integer.parseInt(partes[3]),
                                    Float.parseFloat(partes[4]), linhaAtual);

                            populacoes.add(populacao);
                            populacaoPorID.put(id, populacao);

                            if (populacao.ano == 2024) {
                                populacaoPorID2024.put(id, populacao);
                            }
                        } else {
                            adicionaInvalido(inputInvalido, linhaAtual);
                        }
                    } else {
                        adicionaInvalido(inputInvalido, linhaAtual);
                    }
                }
                linhaAtual++;
            }
            bufferedReader.close();
            input_invalido.add(inputInvalido);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean parseCidades(File file) {
        InputInvalido inputInvalido = new InputInvalido("cidades.csv", 0, 0, -1);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String linha;
            int linhaAtual = 1;
            boolean primeiraLinha = true;
            while ((linha = bufferedReader.readLine()) != null) {
                if (primeiraLinha) {
                    primeiraLinha = false;
                } else {
                    String[] partes = linha.split(",");
                    if (partes.length == 6) {
                        boolean populacaoVazia = false;
                        boolean alfa2pertence = false;
                        boolean temCidades = true;

                        if (partes[1].isEmpty()) {
                            temCidades = false;
                        }

                        if (partes[3].isEmpty()) {
                            populacaoVazia = true;
                        }

                        if (a2.contains(partes[0].trim())) {
                            alfa2pertence = true;
                        }

                        if (!populacaoVazia && alfa2pertence && temCidades) {
                            inputInvalido.linhasOK++;
                            Cidade novaCidade = new Cidade(partes[0].trim(), partes[1].trim(), partes[2].trim(), partes[3].trim(), partes[4].trim(), partes[5].trim());

                            cidades.add(novaCidade);
                            cidadePorNome.put(novaCidade.nome, novaCidade);

                            for (Pais pais : paises) {
                                if (Objects.equals(pais.alfa2, partes[0])) {
                                    if (pais.id >= 600 && pais.id <= 700) {
                                        pais.num600700++;
                                    }
                                }
                            }

                            for (Pais pais : paises) {
                                if (novaCidade.alfa2.equals(pais.alfa2)) {
                                    ArrayList<Cidade> cidadesDoPais = cidadePorPais.get(pais);
                                    cidadesDoPais.add(novaCidade);
                                    cidadesPorPaisNome.put(pais.nome, cidadesDoPais);
                                    cidadePorA2.put(partes[0], cidadesDoPais);
                                }
                            }
                        } else {
                            adicionaInvalido(inputInvalido, linhaAtual);
                        }
                    } else {
                        adicionaInvalido(inputInvalido, linhaAtual);
                    }
                }
                linhaAtual++;
            }
            input_invalido.add(inputInvalido);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean parsePaises(File file) {
        InputInvalido inputInvalido = new InputInvalido("paises.csv", 0, 0, -1);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            int cont = 0;
            int linhaAtual = 1;
            boolean primeiraLinha = true;
            String linha;
            while ((linha = bufferedReader.readLine()) != null) {
                if (primeiraLinha) {
                    primeiraLinha = false;
                } else {
                    String[] partes = linha.split(",");
                    if (partes.length == 4) {
                        boolean paisRepetido = false;

                        int id = Integer.parseInt(partes[0].trim());
                        String alfa2 = partes[1].trim();

                        if (ids.contains(id)) {
                            paisRepetido = true;
                        }

                        if (!paisRepetido) {
                            inputInvalido.linhasOK++;

                            Pais novoPais = new Pais(id, alfa2, partes[2].trim(), partes[3].trim(), linhaAtual);
                            paises.add(novoPais);
                            cidadePorPais.put(paises.get(cont), new ArrayList<>()); //cidade

                            ids.add(id);
                            a2.add(alfa2);

                            paisPorNome.put(partes[3].trim(), novoPais);
                            paisPorA2.put(alfa2, novoPais);
                            paisPorID.put(id, novoPais);
                            cont++;
                        } else {
                            adicionaInvalido(inputInvalido, linhaAtual);
                        }
                    } else {
                        adicionaInvalido(inputInvalido, linhaAtual);
                    }
                }
                linhaAtual++;
            }
            input_invalido.add(inputInvalido);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean parseFiles(File folder) {
        populacoes = new ArrayList<>();
        cidades = new ArrayList<>();
        paises = new ArrayList<>();
        input_invalido = new ArrayList<>();

        a2 = new ArrayList<>();
        ids = new ArrayList<>();

        cidadePorA2 = new HashMap<>();
        cidadePorPais = new HashMap<>();
        cidadePorNome = new HashMap<>();

        paisPorID = new HashMap<>();
        paisPorA2 = new HashMap<>();

        populacaoPorID2024 = new HashMap<>();
        populacaoPorID = new HashMap<>();

        boolean estado = parsePaises(new File(folder, "paises.csv"));
        estado &= parseCidades(new File(folder, "cidades.csv"));
        estado &= parsePopulacao(new File(folder, "populacao.csv"));

        removePaisesSemCidades();

        return estado;
    }

    public static Result execute(String commandLine) {
        Result result;

        result = new Result(commandLine);

        result.getQuery(commandLine);
        return result;
    }

    static ArrayList getCities(int minPopulation, int maxPopulation) {
        ArrayList<Cidade> cidadeArrayList = new ArrayList<>();

        if (minPopulation == -1 && maxPopulation == -1) {
            cidadeArrayList.addAll(cidades);
        } else {
            for (Cidade cidade : cidades) {
                if (Integer.parseInt(cidade.populacao.substring(0, cidade.populacao.length() - 2))
                        >= minPopulation &&
                        Integer.parseInt(cidade.populacao.substring(0, cidade.populacao.length() - 2)) < maxPopulation) {
                    cidadeArrayList.add(cidade);
                }
            }
        }

        return cidadeArrayList;
    }


    public static void main(String[] args) {
        System.out.println("Welcome to DEISI World Meter");
        long start = System.currentTimeMillis();

        boolean estadoParse = parseFiles(new File("."));
        System.out.println("Loaded files in " + (System.currentTimeMillis() - start) + "ms\n");


        if (!estadoParse) {
            System.out.println("!!!Os ficheiros não estão em condições");
            return;
        }

        start = System.currentTimeMillis();

        Result query = execute("HELP");
        Scanner input = new Scanner(System.in);
        String commandLine;

        System.out.println("(took " + (System.currentTimeMillis() - start) + "ms)");

        do {
            System.out.print("> ");
            commandLine = input.nextLine();

            start = System.currentTimeMillis();
            if (commandLine != null && !commandLine.equals("QUIT")) {
                query = execute(commandLine);
                if (!query.success) {
                    System.out.println("Erros: " + query.error);
                } else {
                    System.out.println(query.result);
                }
                System.out.println("(took " + (System.currentTimeMillis() - start) + "ms)");
            }
        } while (commandLine != null && !commandLine.equals("QUIT"));
    }
}
