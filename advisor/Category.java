package advisor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Category extends Bid {

    static final List<String> nList = List.of("Top Lists",
            "Pop",
            "Mood",
            "Latin");

    static final String nTitle = "CATEGORIES";

    static final String nUrlAPI = "/v1/browse/categories";

    public Category(String title, List<String> items) {
        super(title, items);

        this.items = nList;
        this.title = nTitle;
    }

    public Category(String urlPI) {
        super(urlPI);

        this.urlPI = nUrlAPI;
    }

    @Override
    public void analyseJson(String jsonString) {

        Singleton singleton = Singleton.getInstance();

        int nbrecordByPage = singleton.getRecordNumberByPage();


        JsonObject jo = JsonParser.parseString(jsonString).getAsJsonObject();

        JsonObject categories = jo.getAsJsonObject("categories");

        int page = 0;

        int record = 0;

        // réinit hpage
        hPage.clear();
        // réinit page porté par le singleton
        singleton.setNbPageTotale(0);

        List<String> catString = new ArrayList<>();

        for (JsonElement item : categories.getAsJsonArray("items")) {

            record++;

            // Si changement de page
            if (record > nbrecordByPage) {

                page++;


                hPage.put(page, catString);

                catString = new ArrayList<>();

            }

            JsonObject itemJsonObject = item.getAsJsonObject();

            catString.add(itemJsonObject.get("name").getAsString());


        }

        if (catString != null) {
            if (!catString.isEmpty()) {


                page++;


                // Chargement dernière page
                hPage.put(page, catString);
            }
        }

        // nb totale page porté par le singleton
        singleton.setNbPageTotale(page);


    }

    ;

    public HashMap<String, String> catNameID(String accessToken, String debutURL, boolean authOK) {

        String responsFromSpotify = "";


        if (authOK) {

            this.httpRequest = HttpRequest.newBuilder()
                    .header("Authorization", "Bearer " + accessToken)
                    .uri(URI.create(debutURL + this.urlPI))
                    .GET()
                    .build();

            try {

                HttpResponse<String> response = this.httpClient.send(
                        this.httpRequest, HttpResponse.BodyHandlers.ofString());


                if (response.statusCode() == 200 || response.statusCode() == 201) {

                    responsFromSpotify = response.body();       // a JSON response with ID

                    return this.jsonCatNameID(responsFromSpotify);

                } else {

                    JsonObject jo = JsonParser.parseString(response.body()).getAsJsonObject();

                    JsonObject errorObj = jo.getAsJsonObject("error");
                    System.out.println(errorObj.get("message").getAsString());

                }


            } catch (Exception e) {
                System.out.println("We cannot send data. Please, try later.");
            }


        } else {

            System.out.println("Please, provide access for application.");

        }


        return null;

    }

    public HashMap<String, String> jsonCatNameID(String jsonString) {

        HashMap<String, String> hCatNameId = new HashMap<>();

        JsonObject jo = JsonParser.parseString(jsonString).getAsJsonObject();

        JsonObject categories = jo.getAsJsonObject("categories");

        for (JsonElement item : categories.getAsJsonArray("items")) {

            JsonObject itemJsonObject = item.getAsJsonObject();

            hCatNameId.put(itemJsonObject.get("name").getAsString(), itemJsonObject.get("id").getAsString());

        }

        return hCatNameId;
    }

    @Override
    public void affParPage() {

        Singleton singleton = Singleton.getInstance();

        int page = singleton.getPageCourante();
        List<String> l = hPage.get(page);

        if (l != null) {
            for (String s : l) {

                System.out.println(s);

                System.out.println(" ");

            }

            System.out.println("---PAGE " + page + " OF " + singleton.getNbPageTotale() + "---");
        } else {

            singleton.setPageCourante(singleton.getPagePrcedente());

            System.out.println("No more pages.");
        }
    }

}
