package devalrykemes.exchangeconverterapp.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import devalrykemes.exchangeconverterapp.exceptions.SystemException;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class ExchageRateAPI {
    private HttpClient client = HttpClient.newHttpClient();
    private Gson gson = new Gson();

    private HashMap<String, String> getSupportedCoins() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("https://v6.exchangerate-api.com/v6/dba82db2f502850d87fb69e2/codes"))
                .build();

        HttpResponse<String> response = client
                .send(request, HttpResponse.BodyHandlers.ofString());

        JsonObject json = gson.fromJson(response.body(), JsonObject.class);
        HashMap<String, String> mapCoins = new HashMap<>();

        for (int i = 0; i < json.get("supported_codes").getAsJsonArray().size(); i++) {
            mapCoins.put(json.get("supported_codes")
                            .getAsJsonArray().get(i)
                            .getAsJsonArray().get(0)
                            .toString()
                            .replaceAll("\"", ""),
                        json.get("supported_codes")
                            .getAsJsonArray().get(i)
                            .getAsJsonArray().get(1)
                            .toString()
                            .replaceAll("\"", ""));
        }

        return mapCoins;
   }

   public String getSiglaCoinbyName(String nameCoin) throws SystemException {
       HashMap<String, String> mapCoins = null;
       try {
           mapCoins = getSupportedCoins();
       } catch (Exception ex) {
           System.out.println(ex.getMessage());
           throw new SystemException("Erro na comunicação do sistema, tente novamente mais tarde!", ex);
       }
       for(Map.Entry<String, String> entry : mapCoins.entrySet()) {
            if(entry.getValue().equals(nameCoin)) {
                return entry.getKey();
            }
        }
        return null;
   }

   public List<String> getAbbreviatedCoins() throws SystemException {
       List<String> listAbbreviatedCoins = null;
       try {
           listAbbreviatedCoins = new ArrayList<>(getSupportedCoins().keySet());
       } catch (Exception ex) {
           System.out.println(ex.getMessage());
           throw new SystemException("Erro na comunicação do sistema, tente novamente mais tarde!", ex);
       }

       Collections.sort(listAbbreviatedCoins);
       return listAbbreviatedCoins;
   }

    public List<String> getNameCoins() throws SystemException {
        List<String> listCoinsName = null;
        try {
            listCoinsName = new ArrayList<>(getSupportedCoins().values());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw new SystemException("Erro na comunicação do sistema, tente novamente mais tarde!", ex);
        }

        Collections.sort(listCoinsName);
        return listCoinsName;
    }

    private BigDecimal getConversionRate(String coinBase, String coinConvert) throws IOException, InterruptedException {

        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("https://v6.exchangerate-api.com/v6/dba82db2f502850d87fb69e2/pair/"+ coinBase +"/"+ coinConvert))
                .build();

        HttpResponse<String> response = client
                .send(request, HttpResponse.BodyHandlers.ofString());

        JsonObject json = gson.fromJson(response.body(), JsonObject.class);

        return BigDecimal.valueOf(json.get("conversion_rate").getAsDouble());
    }

    public BigDecimal convertCurrency(String coinBase, String coinConvert, BigDecimal value) throws SystemException {
        BigDecimal conversionRate = null;
        try {
            conversionRate = getConversionRate(coinBase, coinConvert);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            throw new SystemException("Erro na comunicação do sistema, tente novamente mais tarde!", ex);
        }
        return value.multiply(conversionRate);
    }

    public static void main(String[] args) {
        ExchageRateAPI exchageRateAPI = new ExchageRateAPI();
        try {
            System.out.println(exchageRateAPI.getSiglaCoinbyName("United States Dollar"));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            System.out.println("Erro: " + ex.getMessage());
        }
    }
}
