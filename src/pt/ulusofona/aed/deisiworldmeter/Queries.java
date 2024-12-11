package pt.ulusofona.aed.deisiworldmeter;

import java.text.CollationElementIterator;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

import static pt.ulusofona.aed.deisiworldmeter.Main.*;

public class Queries {
    public static void blabla(){

    }

    // Returns the count of cities with a population greater than or equal to <min_population>
    public static int countCities(int minPopulation) {
        int cont = 0;

        for (Cidade cidade : cidades) {
            if (minPopulation <= Double.parseDouble(cidade.populacao)) {
                cont++;
            }
        }

        return cont;
    }

    // Returns the cities from a specific country with a limit of <num-results> and <country-name>
    public static String getCitiesByCountry(int numResults, String countryName) {
        StringBuilder output = new StringBuilder();
        int cont = 0;

        ArrayList<Cidade> cidades = cidadesPorPaisNome.get(countryName);
        int numCidades = cidades.size();

        ArrayList<Cidade> cidadeArrayList = new ArrayList<>();

        //Itera cidades da query, não global
        for (Cidade cidade : cidades) {
            if (Integer.parseInt(cidade.populacao.substring(0, cidade.populacao.length() - 2)) >= 100000) {
                cidadeArrayList.add(cidade);
                if (cont >= numResults - 1 || cont >= numCidades - 1) {
                    break;
                }
                cont++;
            }
        }

        cidadeArrayList.sort((c1, c2) -> {
            int pop1, pop2;
            pop1 = Integer.parseInt(c1.populacao.substring(0, c1.populacao.length() - 3));
            pop2 = Integer.parseInt(c2.populacao.substring(0, c2.populacao.length() - 3));

            return Integer.compare(pop2, pop1);
        });

        for (Cidade cidade : cidadeArrayList) {
            output.append(cidade.nome).append("\n");
        }

        if (output.isEmpty()) {
            return "Zero cidades encontradas";
        }

        return output.toString();
    }

    // Returns the sum of populations for a list of countries provided in <countries-list>
    public static int sumPopulations(String country) {
        int id = -1;
        for (Pais pais : paises) {
            if (Objects.equals(pais.nome, country)) {
                id = pais.id;
                break;
            }
        }

        if (id == -1) {
            return 0;
        }

        Map<Integer, Integer> countryPopulation = new HashMap<>();
        for (Populacao populacao : populacoes) {
            if (populacao.ano == 2024 && populacao.id == id) {
                countryPopulation.put(id, populacao.populacaoFemenina + populacao.populacaoMasculina);
                break;
            }
        }

        if (countryPopulation.isEmpty()) {
            return 0;
        }

        if (countryPopulation.containsKey(id)) {
            return countryPopulation.get(id);
        }

        return 0;
    }

    // Returns the history for a specific country between <year-start> and <year-end>
    public static String getHistory(int yearStart, int yearEnd, String countryName) {
        String history = "";
        String pm;
        String pf;

        if (yearStart > yearEnd) {
            return "intervalo invalido";
        }
        ArrayList<String > historico = new ArrayList<>();
        for (Pais pais : paises) {
            if (pais.nome.equals(countryName)) {
                for (Populacao populacao : populacoes) {
                    if (populacao.id == pais.id && populacao.ano >= yearStart && populacao.ano <= yearEnd) {
                        int PM = populacao.populacaoMasculina;
                        int PF = populacao.populacaoFemenina;

                        pm = (populacao.populacaoMasculina + "").substring(0, String.valueOf(PM).length() - 3);
                        pf = (populacao.populacaoFemenina + "").substring(0, String.valueOf(PF).length() - 3);

                        history = history.concat(populacao.ano + ":" + pm + "k:" + pf + "k\n");
                        historico.add(history);
                    }
                }
            }
        }

        if (historico.size() >3) {
            return "demasiados resultados";
        }
        return history;
    }

    // Returns the missing history data between <year-start> and <year-end>
    public static String getMissingHistory(int yearStart, int yearEnd,String novo) {
        if (yearEnd > 2100) {
            yearEnd = 2100;
        }

        StringBuilder missingHistory = new StringBuilder();
        Set<String> pop = new HashSet<>();

        for (Populacao populacao : populacoes) {
            pop.add(populacao.id + ":" + populacao.ano);
        }

        for (Pais pais : paises) {
            if (!pais.nome.contains(novo)) {
                for (int i = yearStart; i <= yearEnd; i++) {
                    if (!pop.contains(pais.id + ":" + i)) {
                        missingHistory.append(pais.alfa2).append(":").append(pais.nome).append("\n");
                        break;
                    }
                }
            }
        }

        return missingHistory.toString();
    }

    // Returns the most populous cities with a limit of <num-results>
    public static String getMostPopulous(int numResults) {
        StringBuilder output = new StringBuilder();
        ArrayList<Cidade> maioresCidades = new ArrayList<>();

        if (numResults <= 0) {
            return output.toString();
        }

        for (ArrayList<Cidade> cidadesDoPais : cidadePorA2.values()) {
            Cidade maior = null;
            double maiorPopulacao = -1;

            for (Cidade cidade : cidadesDoPais) {
                double populacao = Double.parseDouble(cidade.populacao);
                if (populacao > maiorPopulacao) {
                    maior = cidade;
                    maiorPopulacao = populacao;
                }
            }

            if (maior != null) {
                maioresCidades.add(maior);
            }
        }

        //sort decrescente
        maioresCidades.sort((c1, c2) -> (int) (Double.parseDouble(c2.populacao) - Double.parseDouble(c1.populacao)));

        Map<String, Pais> paisMap = new HashMap<>();
        for (Pais pais : paises) {
            paisMap.put(pais.alfa2, pais);
        }

        for (int i = 0; i < numResults && i < maioresCidades.size(); i++) {
            Cidade cidade = maioresCidades.get(i);
            Pais pais = paisMap.get(cidade.alfa2);
            if (pais != null) {
                output.append(pais.nome).append(":")
                        .append(cidade.nome)
                        .append(":")
                        .append((int) Double.parseDouble(cidade.populacao))
                        .append("\n");
            }
        }

        return output.toString();
    }

    // Returns the top cities by population for a specific country with a limit of <num-results>
    public static String getTopCitiesByCountry(int numResults, String country) {
        //GET_TOP_CITIES_BY_COUNTRY -1 Estados Unidos

        StringBuilder output = new StringBuilder();
        ArrayList<Cidade> maioresCidades = new ArrayList<>();

        if (numResults == 0 || country.isEmpty()) {
            return output.toString();
        }

        for (HashMap.Entry<Pais, ArrayList<Cidade>> entry : cidadePorPais.entrySet()) {
            Pais pais = entry.getKey();
            if (Objects.equals(country, pais.nome)) {
                ArrayList<Cidade> cidadesDoPais = entry.getValue();
                maioresCidades.addAll(cidadesDoPais);
                break;
            }
        }

        //remove .0
        for (Cidade cidade : maioresCidades) {
            if (cidade.populacao.contains(".0")) {
                cidade.populacao = cidade.populacao.substring(0, cidade.populacao.length() - 2);
            }
        }

        //remove cidades abaixo de 10000
        maioresCidades.removeIf(cidade -> Integer.parseInt(cidade.populacao) < 10000);

        //sort decrescente, help with ai and intellij
        maioresCidades.sort((c1, c2) -> {
            int pop1, pop2;
            pop1 = Integer.parseInt(c1.populacao.substring(0, c1.populacao.length() - 3));
            pop2 = Integer.parseInt(c2.populacao.substring(0, c2.populacao.length() - 3));

            int comparison = Integer.compare(pop2, pop1);
            if (comparison == 0) {
                return c1.nome.compareTo(c2.nome);
            } else {
                return comparison;
            }
        });


        //condicao melhorada com AI
        int results = (numResults == -1) ? maioresCidades.size() : Math.min(numResults, maioresCidades.size());
        for (int i = 0; i < results; i++) {
            Cidade cidade = maioresCidades.get(i);
            String pop;
            pop = cidade.populacao.substring(0, cidade.populacao.length() - 3);
            output.append(cidade.nome).append(":").append(pop).append("K\n");
        }

        return output.toString();
    }

    // Returns the duplicate cities with a population greater than or equal to <min_population>
    public static String getDuplicateCities(int minPopulation) {
        StringBuilder output = new StringBuilder();
        HashMap<String, List<Cidade>> cityByName = new HashMap<>();

        for (Cidade cidade : cidades) {
            if (cidade.populacao != null && !cidade.populacao.isEmpty() &&
                    Double.parseDouble(cidade.populacao) >= minPopulation) {
                cityByName.computeIfAbsent(cidade.nome, k -> new ArrayList<>()).add(cidade);
            }
        }

        for (List<Cidade> cityList : cityByName.values()) {
            if (cityList.size() > 1) {
                for (int i = 1; i < cityList.size(); i++) {
                    Cidade city = cityList.get(i);
                    String countryName = "";
                    for (Pais pais : paises) {
                        if (pais.alfa2.equals(city.alfa2)) {
                            countryName = pais.nome;
                            break;
                        }
                    }
                    output.append(city.nome).append(" (").append(countryName)
                            .append(",").append(city.regiao).append(")\n");
                }
            }
        }

        return output.toString();
    }

    // Returns the countries with a gender gap greater than or equal to <min-gender-gap>
    public static String getCountriesGenderGap(int minGenderGap) {
        StringBuilder output = new StringBuilder();

        HashMap<Integer, String> inbalanceByID = new HashMap<>();

        for (Map.Entry<Integer, Populacao> entry : populacaoPorID2024.entrySet()) {
            Populacao populacao = entry.getValue();

            //Remove decimals
            String inbalance = calculateGenderInbalance(populacao.populacaoMasculina, populacao.populacaoFemenina);

            Pais pais = paisPorID.get(populacao.id);
            inbalanceByID.put(pais.id, inbalance);
        }

        for (Integer id : inbalanceByID.keySet()) {
            Pais pais = paisPorID.get(id);
            if (Double.parseDouble(inbalanceByID.get(id)) > minGenderGap) {
                output.append(pais.nome).append(":").append(inbalanceByID.get(id)).append("\n");
            }
        }

        return output.toString();
    }

    // Returns the top population increases between <year-start> and <year_end>
    public static String getTopPopulationIncrease(int yearStart, int yearEnd) {
        StringBuilder output = new StringBuilder();
        ArrayList<String> stats = new ArrayList<>();

        for (Pais pais : paises) {
            ArrayList<Populacao> populations = new ArrayList<>();
            for (Populacao populacao : populacoes) {
                if (pais.id == populacao.id && populacao.ano >= yearStart && populacao.ano <= yearEnd) {
                    populations.add(populacao);
                }
            }

            populations.sort(Comparator.comparingInt(p -> p.ano));

            for (int i = 0; i < populations.size(); i++) {
                for (int j = i + 1; j < populations.size(); j++) {

                    if (populations.size() > 1) {
                        int sumYS = populations.get(i).populacaoMasculina + populations.get(i).populacaoFemenina;
                        int sumYE = populations.get(j).populacaoMasculina + populations.get(j).populacaoFemenina;

                        String increase = calculatePopulationIncrease(sumYS, sumYE);

                        if (!increase.isEmpty() && !increase.equals("0.00")) {
                            stats.add(pais.nome + ":" + populations.get(i).ano + "-" + populations.get(j).ano + ":" + increase + "%\n");
                        }
                    }
                }
            }
        }

        //AI ajudou
        stats.sort((s1, s2) -> {
            double increase1 = Double.parseDouble(s1.split(":")[2].replace("%", ""));
            double increase2 = Double.parseDouble(s2.split(":")[2].replace("%", ""));

            int comparison = Double.compare(increase2, increase1);
            if (comparison == 0) {
                return s1.split(":")[0].compareTo(s2.split(":")[0]);
            } else {
                return comparison;
            }
        });

        for (int i = 0; i < 5 && i < stats.size(); i++) {
            output.append(stats.get(i));
        }

        return output.toString();
    }

    // Returns the duplicate cities across different countries with a population greater than or equal to <min-population>
    public static String getDuplicateCitiesDifferentCountries(int minPopulation) {
        StringBuilder output = new StringBuilder();
        HashMap<String, Set<String>> cityMap = new HashMap<>();

        for (Cidade cidade : cidades) {
            if (cidade.populacao != null && !cidade.populacao.isEmpty() &&
                    Double.parseDouble(cidade.populacao) >= minPopulation) {
                String countryName = "";
                for (Pais pais : paises) {
                    if (pais.alfa2.equals(cidade.alfa2)) {
                        countryName = pais.nome;
                        break;
                    }
                }
                cityMap.computeIfAbsent(cidade.nome, k -> new TreeSet<>()).add(countryName);
            }
        }

        for (Map.Entry<String, Set<String>> entry : cityMap.entrySet()) {
            if (entry.getValue().size() > 1) {
                output.append(entry.getKey()).append(": ");

                //AI ajudou
                Iterator<String> iterator = entry.getValue().iterator();
                while (iterator.hasNext()) {
                    output.append(iterator.next());
                    if (iterator.hasNext()) {
                        output.append(",");
                    }
                }
                output.append("\n");
            }
        }

        return output.toString();
    }

    // Returns the cities within a certain distance from a specific country
    public static String getCitiesAtDistance(int distance, String country) {
        String output = "";
        ArrayList<String> cidadesValidas = new ArrayList<>();

        String a2 = "";
        for (Pais pais : paises) {
            if (pais.nome.equals(country)) {
                a2 = pais.alfa2;
                break;
            }
        }

        if (a2.isEmpty()) {
            return "";
        }

        ArrayList<Cidade> cidadesDoPais = new ArrayList<>();
        for (Cidade cidade : cidades) {
            if (cidade.alfa2.equals(a2)) {
                cidadesDoPais.add(cidade);
            }
        }

        if (cidadesDoPais.isEmpty()) {
            return "";
        }

        for (int i = 0; i < cidadesDoPais.size(); i++) {
            double lat1 = Double.parseDouble(cidadesDoPais.get(i).latitude);
            double lon1 = Double.parseDouble(cidadesDoPais.get(i).longitude);

            //Se der erro, é o enhanced for loop
            for (Cidade cidadePais : cidadesDoPais) {
                double lat2 = Double.parseDouble(cidadePais.latitude);
                double lon2 = Double.parseDouble(cidadePais.longitude);

                double calculatedDistance = calculateDistance(lat1, lon1, lat2, lon2);

                String s;
                if (cidadesDoPais.get(i).nome.compareTo(cidadePais.nome) < 0) {
                    s = cidadesDoPais.get(i).nome + "->" + cidadePais.nome;
                } else {
                    s = cidadePais.nome + "->" + cidadesDoPais.get(i).nome;
                }

                if (calculatedDistance >= (distance - 1) && calculatedDistance <= (distance + 1) && !cidadesValidas.contains(s)) {
                    cidadesValidas.add(s);
                    output = output.concat(s + "\n");
                }
            }
        }

        return output;
    }

    // Returns the cities within a certain distance from a specific country without the country
    public static String getCitiesAtDistance2(int distance, String country) {
        StringBuilder output = new StringBuilder();
        Set<String> cidadesValidas = new HashSet<>();

        String a2 = paisPorNome.get(country).alfa2;

        if (a2.isEmpty()) {
            return "";
        }

        ArrayList<Cidade> cidadesDoInput = new ArrayList<>(cidadePorA2.get(a2));

        if (cidadesDoInput.isEmpty()) {
            return "";
        }

        //Itera cidades do pais de input
        for (Cidade cidadeInput : cidadesDoInput) {
            double lat1 = Double.parseDouble(cidadeInput.latitude);
            double lon1 = Double.parseDouble(cidadeInput.longitude);

            //Itera todas as cidades
            for (Cidade cidadeMundo : cidades) {
                //Verifica se as cidades não pertencem
                if (!cidadeInput.alfa2.equals(cidadeMundo.alfa2)) {
                    double lat2 = Double.parseDouble(cidadeMundo.latitude);
                    double lon2 = Double.parseDouble(cidadeMundo.longitude);

                    //Guardar a distancia e os nomes das cidades para otimizar performance
                    double calculatedDistance = calculateDistance(lat1, lon1, lat2, lon2);

                    String s;
                    if (cidadeInput.nome.compareTo(cidadeMundo.nome) < 0) {
                        s = cidadeInput.nome + "->" + cidadeMundo.nome;
                    } else {
                        s = cidadeMundo.nome + "->" + cidadeInput.nome;
                    }

                    if (calculatedDistance >= (distance - 1) && calculatedDistance <= (distance + 1)) {
                        cidadesValidas.add(s);
                    }
                }
            }
        }

        ArrayList<String> sorter = new ArrayList<>(cidadesValidas);
        sorter.sort(Comparator.naturalOrder());

        for (String s : sorter) {
            output.append(s).append("\n");
        }


        return output.toString();
    }

    // Inserts a new city with specified parameters
    public static String insertCity(String alfa2, String city, String region, String population) {
        if (alfa2.isEmpty() || city.isEmpty() || region.isEmpty() || population.isEmpty()) {
            return "Pais invalido";
        }

        Pais pHash = null;
        for (Pais pais : cidadePorPais.keySet()) {
            if (pais.alfa2.equals(alfa2)) {
                pHash = pais;
                break;
            }
        }

        if (pHash == null || !cidadePorPais.containsKey(pHash)) {
            return "Pais invalido";
        }

        if (!population.contains(".")) {
            population = population.concat(".0");
        }

        ArrayList<Cidade> cidadesDoPais = cidadePorPais.get(pHash);
        Cidade cidadeNova = new Cidade(alfa2, city, region, population, "0.0", "0.0");
        cidadesDoPais.add(cidadeNova);
        cidadePorPais.put(pHash, cidadesDoPais);
        cidades.add(cidadeNova);

        return "Inserido com sucesso";
    }

    // Removes a country and associated data
    public static boolean removeCountry(String country) {
        boolean exists = false;

        for (HashMap.Entry<Pais, ArrayList<Cidade>> entry : cidadePorPais.entrySet()) {
            Pais paisEntry = entry.getKey();

            if (paisEntry.nome.equals(country)) {
                exists = true;

                for (Pais paisIterador : paises) {
                    if (paisIterador.nome.equals(paisEntry.nome)) {
                        String a2 = paisIterador.alfa2;
                        int id = paisIterador.id;

                        populacoes.removeIf(populacao -> populacao.id == id);       //intellij simplificou
                        cidades.removeIf(cidade -> cidade.alfa2.equals(a2));  //intellij simplificou
                        paises.removeIf(pais -> pais.id == id);                     //intellij simplificou
                        break;
                    }
                }
                cidadePorPais.remove(paisEntry);
                break;
            }
        }

        return exists;
    }

    // Provides list of commands
    public static void help() {
        System.out.println("""
                ------------------------------
                Commands available:
                COUNT_CITIES <min_population>
                GET_CITIES_BY_COUNTRY <num-results> <country-name>
                SUM_POPULATIONS <countries-list>
                GET_HISTORY <year-start> <year-end> <country-name>
                GET_MISSING_HISTORY <year-start> <year-end>
                GET_MOST_POPULOUS <num-results>
                GET_TOP_CITIES_BY_COUNTRY <num-results> <country-name>
                GET_DUPLICATE_CITIES <min_population>
                GET_COUNTRIES_GENDER_GAP <min-gender-gap>
                GET_TOP_POPULATION_INCREASE <year-start> <year-end>
                GET_DUPLICATE_CITIES_DIFFERENT_COUNTRIES <min-population>
                GET_CITIES_AT_DISTANCE <distance> <country-name>
                GET_CITIES_AT_DISTANCE2 <distance> <country-name>
                INSERT_CITY <alfa2> <city-name> <region> <population>
                REMOVE_COUNTRY <country-name>
                HELP
                QUIT
                ------------------------------
                """);
    }

    public static String calculateGenderInbalance(int pm, int pf) {
        double numerador = Math.abs(pm - pf);
        double denominador = pm + pf;
        double inbalance = (numerador / denominador) * 100;

        //Stack overflow ajudou
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat df = new DecimalFormat("#0.00", symbols);

        return df.format(inbalance);
    }

    public static String calculatePopulationIncrease(int populacaoInicial, int populacaoFinal) {
        double numerador = populacaoFinal - populacaoInicial;
        double increase = (numerador / (double) populacaoFinal) * 100;

        if (increase < 0) {
            return "";
        }

        //Stack overflow ajudou
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat df = new DecimalFormat("#0.00", symbols);

        return df.format(increase);
    }

    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double raio = 6371.0;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return raio * c;
    }
}
