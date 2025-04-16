package exchanger.CurrencyConverter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@SpringBootApplication
public class CurrencyConverterApplication {

    private static final String API_KEY = "16cdc2d31eab14c6c6336b7d";
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/pair/";

    public static void main(String[] args) throws IOException, InterruptedException {
        SpringApplication.run(CurrencyConverterApplication.class, args);

        Scanner scanner = new Scanner(System.in);

        String[][] options = {
                {"USD", "ARS"},
                {"ARS", "USD"},
                {"USD", "BRL"},
                {"BRL", "USD"},
                {"USD", "COP"},
                {"COP", "USD"}
        };

        boolean running = true;

        while (running) {
            System.out.println("\n==== Conversor de Moedas ====");
            System.out.println("Escolha uma opção de conversão:");
            System.out.println("1 - Dólar (USD) para Peso Argentino (ARS)");
            System.out.println("2 - Peso Argentino (ARS) para Dólar (USD)");
            System.out.println("3 - Dólar (USD) para Real Brasileiro (BRL)");
            System.out.println("4 - Real Brasileiro (BRL) para Dólar (USD)");
            System.out.println("5 - Dólar (USD) para Peso Colombiano (COP)");
            System.out.println("6 - Peso Colombiano (COP) para Dólar (USD)");
            System.out.println("7 - Sair");
            System.out.print("Digite a opção (1-7): ");

            int option = scanner.nextInt();

            if (option == 7) {
                System.out.println("Encerrando o programa. Obrigado por usar o conversor!");
                running = false;
                continue;
            }

            if (option < 1 || option > 6) {
                System.out.println("Opção inválida. Tente novamente.");
                continue;
            }

            String fromCurrency = options[option - 1][0];
            String toCurrency = options[option - 1][1];

            System.out.print("Digite o valor para converter: ");
            double amount = scanner.nextDouble();

            double result = convertCurrency(fromCurrency, toCurrency, amount);
            if (result >= 0) {
                System.out.printf("Resultado: %.2f %s = %.2f %s%n", amount, fromCurrency, result, toCurrency);
            } else {
                System.out.println("Erro na conversão. Tente novamente mais tarde.");
            }
        }

        scanner.close();
    }

    public static double convertCurrency(String from, String to, double amount) throws IOException, InterruptedException {
        String url = API_URL + from + "/" + to;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();

        if (jsonObject.has("conversion_rate")) {
            double rate = jsonObject.get("conversion_rate").getAsDouble();
            return amount * rate;
        } else {
            System.out.println("Erro na resposta da API: " + jsonObject);
            return -1;
        }
    }
}
