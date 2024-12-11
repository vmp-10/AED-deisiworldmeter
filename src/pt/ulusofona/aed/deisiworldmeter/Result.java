package pt.ulusofona.aed.deisiworldmeter;

import java.util.Objects;

public class Result {
    String commandLine;
    boolean success = false;
    String error; //SE VALIDO, ERROR = NULL
    String result; //SE INVALIDO, RESULT = NULL

    public Result(String commandLine) {
        this.commandLine = commandLine;
    }

    public void getQuery(String commandLine) {
        commandLine = commandLine.trim();
        String[] temp = commandLine.split(" ");

        switch (temp[0]) {
            case "COUNT_CITIES" -> {
                success = true;

                String[] line = commandLine.split(" ");

                int count = Queries.countCities(Integer.parseInt(line[1]));
                if (count != -1) {
                    result = String.valueOf(count);
                } else {
                    result = "Sem resultados";
                }
            }
            case "GET_CITIES_BY_COUNTRY" -> {
                success = true;

                String s = commandLine.substring("GET_CITIES_BY_COUNTRY ".length());
                String[] line = s.split(" ", 2);

                String cities = Queries.getCitiesByCountry(Integer.parseInt(line[0]), line[1]);
                if (!Objects.equals(cities, "")) {
                    result = cities;
                } else {
                    result = "Pais invalido: " + line[1];
                }
            }
            case "SUM_POPULATIONS" -> {
                success = true;

                String input = commandLine.substring("SUM_POPULATIONS ".length());
                String[] country = input.split(",");
                int total = 0;

                boolean status = true;
                String invalid = null;

                for (String pais : country) {
                    boolean exist = false;
                    for (Pais paises : Main.paises) {
                        if (paises.nome.equals(pais)) {
                            exist = true;
                            break;
                        }
                    }
                    if (!exist) {
                        invalid = pais;
                        status = false;
                        break;
                    }
                }

                if (status) {
                    for (String pais : country) {
                        total += Queries.sumPopulations(pais);
                    }
                    result = String.valueOf(total);
                } else {
                    result = "Pais invalido: " + invalid;
                }
            }
            case "GET_HISTORY" -> {
                success = true;

                String s = commandLine.substring("GET_HISTORY ".length());
                String[] line = s.split(" ", 3);

                String history = Queries.getHistory(Integer.parseInt(line[0]), Integer.parseInt(line[1]), line[2]);
                if (!Objects.equals(history, "")) {
                    result = history;
                } else {
                    result = "Sem resultados";
                }
            }
            case "GET_MISSING_HISTORY" -> {
                success = true;
                String[] line = commandLine.split(" ");

                String missingHistoryResult = Queries.getMissingHistory(Integer.parseInt(line[1]), Integer.parseInt(line[2]), line[3]);
                if (!missingHistoryResult.isEmpty()) {
                    result = missingHistoryResult;
                } else {
                    result = "Sem resultados";
                }
            }
            case "GET_MOST_POPULOUS" -> {
                success = true;

                String[] line = commandLine.split(" ");
                String mostPopulous = Queries.getMostPopulous(Integer.parseInt(line[1]));
                if (!mostPopulous.isEmpty()) {
                    result = mostPopulous;
                } else {
                    result = "Sem resultados";
                }
            }
            case "GET_TOP_CITIES_BY_COUNTRY" -> {
                success = true;

                //GET_TOP_CITIES_BY_COUNTRY -1 Estados Unidos
                String s = commandLine.substring("GET_TOP_CITIES_BY_COUNTRY ".length());
                String[] line = s.split(" ", 2);

                String topCitiesByCountryResult = Queries.getTopCitiesByCountry(Integer.parseInt(line[0]), line[1]);
                if (!topCitiesByCountryResult.isEmpty()) {
                    result = topCitiesByCountryResult;
                } else {
                    result = "Sem resultados";
                }
            }
            case "GET_DUPLICATE_CITIES" -> {
                success = true;
                String[] line = commandLine.split(" ");

                String duplicateCities = Queries.getDuplicateCities(Integer.parseInt(line[1]));
                if (!duplicateCities.isEmpty()) {
                    result = duplicateCities;
                } else {
                    result = "Sem resultados";
                }
            }
            case "GET_COUNTRIES_GENDER_GAP" -> {
                success = true;

                String[] line = commandLine.split(" ");

                String genderGap = Queries.getCountriesGenderGap(Integer.parseInt(line[1]));
                if (!Objects.equals(genderGap, "")) {
                    result = genderGap;
                } else {
                    result = "Sem resultados";
                }
            }
            case "GET_TOP_POPULATION_INCREASE" -> {
                success = true;
                String[] line = commandLine.split(" ");

                String increase = Queries.getTopPopulationIncrease(Integer.parseInt(line[1]), Integer.parseInt(line[2]));
                if (!Objects.equals(increase, "")) {
                    result = increase;
                } else {
                    result = "Sem resultados";
                }
            }
            case "GET_DUPLICATE_CITIES_DIFFERENT_COUNTRIES" -> {
                success = true;
                String[] line = commandLine.split(" ");

                String duplicateCities = Queries.getDuplicateCitiesDifferentCountries(Integer.parseInt(line[1]));
                if (!duplicateCities.isEmpty()) {
                    result = duplicateCities;
                } else {
                    result = "Sem resultados";
                }
            }
            case "GET_CITIES_AT_DISTANCE" -> {
                success = true;

                String s = commandLine.substring("GET_CITIES_AT_DISTANCE ".length());
                String[] line = s.split(" ", 2);

                String citiesAtDistance = Queries.getCitiesAtDistance(Integer.parseInt(line[0]), line[1]);
                if (!citiesAtDistance.isEmpty()) {
                    result = citiesAtDistance;
                } else {
                    result = "Sem resultados";
                }
            }
            case "GET_CITIES_AT_DISTANCE2" -> {
                success = true;

                String s = commandLine.substring("GET_CITIES_AT_DISTANCE2 ".length());
                String[] line = s.split(" ", 2);

                String citiesAtDistance = Queries.getCitiesAtDistance2(Integer.parseInt(line[0]), line[1]);
                if (!citiesAtDistance.isEmpty()) {
                    result = citiesAtDistance;
                } else {
                    result = "Sem resultados";
                }
            }
            case "INSERT_CITY" -> {
                success = true;

                String input = commandLine.substring("INSERT_CITY ".length());
                String[] line = input.split(" ");

                result = Queries.insertCity(line[0], line[1], line[2], line[3]);
            }
            case "REMOVE_COUNTRY" -> {
                success = true;
                String country = commandLine.substring("REMOVE_COUNTRY ".length());

                boolean removeStatus = Queries.removeCountry(country);
                if (removeStatus) {
                    result = "Removido com sucesso";
                } else {
                    result = "Pais invalido";
                }
            }
            case "HELP" -> {
                success = true;
                Queries.help();
            }
            case "QUIT" -> success = true;
            default -> {
                success = false;
                error = "Comando Invalido ";
            }
        }
    }
}
