package org.example;

import org.json.JSONObject;

import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Digite o nome da cidade");

        String cidade = scanner.nextLine();

        try{
            String dadosClimaticos = getDadosClimaticos(cidade);

            if (dadosClimaticos.contains("\"code\":1006")){
                System.out.println("Localização não encontrada");
            }
            else {
                imprimirDadosClimaticos(dadosClimaticos);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            scanner.close();
        }
    }

    public static String getDadosClimaticos(String cidade) throws Exception{
        String apiKey = Files.readString(Paths.get("/home/me/dev/projects/dados-meteorologicos/dados/apiKey.txt")).trim();

        String formatNomeCidade = URLEncoder.encode(cidade, StandardCharsets.UTF_8);

        String apiUrl = "https://api.weatherapi.com/v1/current.json?key=" + apiKey + "&q=" + formatNomeCidade;

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).build();

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return httpResponse.body();
    }

    public static void imprimirDadosClimaticos(String dados) {
        JSONObject dadosJson = new JSONObject(dados);

        // Verifica se houve erro na resposta
        if (dadosJson.has("error")) {
            JSONObject erro = dadosJson.getJSONObject("error");
            System.out.println("Erro da API: " + erro.getString("message"));
            return;
        }

        JSONObject informacoesMeteorologicas = dadosJson.getJSONObject("current");

        JSONObject local = dadosJson.getJSONObject("location");

        String cidade = local.getString("name");

        String pais = local.getString("country");

        String condicaoTempo = informacoesMeteorologicas.getJSONObject("condition").getString("text");

        int umidade = informacoesMeteorologicas.getInt("humidity");

        float velocidadeVento = informacoesMeteorologicas.getFloat("wind_kph");

        float pressaoAtmosferica = informacoesMeteorologicas.getFloat("pressure_mb");

        float temperaturaAtual = informacoesMeteorologicas.getFloat("temp_c");

        String dataHoraString = informacoesMeteorologicas.getString("last_updated");

        System.out.printf(
                "Informações meteorológicas para %s, %s%n" +
                        "Data e hora: %s%n" +
                        "Temperatura atual: %.1f°C%n" +
                        "Condição do tempo: %s%n" +
                        "Umidade: %d%%%n" +
                        "Velocidade do vento: %.1f km/h%n" +
                        "Pressão atmosférica: %.1f mb%n",
                cidade, pais, dataHoraString, temperaturaAtual, condicaoTempo, umidade, velocidadeVento, pressaoAtmosferica
        );
    }

}