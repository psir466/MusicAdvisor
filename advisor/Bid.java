package advisor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Bid {

    protected String title;
    protected List<String> items;
    protected String urlPI;
    protected HttpRequest httpRequest;
    protected HttpClient httpClient = HttpClient.newHttpClient();

    protected static HashMap<Integer, List<String>> hPage = new HashMap<>();

    protected static int totalPage = 0;

    public Bid(String title, List<String> items) {
        this.title = title;
        this.items = items;
    }

    public Bid(String urlPI) {
        this.urlPI = urlPI;
    }

    public Bid() {
    }

    public void sendAPISpotifyWithParameter(String accessToken, String debutURL, boolean authOK, String param) {

        if (authOK) {
            String s = "";
            Category c = new Category(s);

            // Chargement des catégories et recherche Id par name
            String idCat = c.catNameID(accessToken, debutURL, authOK).get(param);

            if (idCat != null) {
                this.urlPI = this.urlPI + "/" + idCat + "/playlists";

                this.sendAPISpotify(accessToken, debutURL, authOK);

            } else {

                System.out.println("Unknown category name.");

            }
        } else {

            System.out.println("Please, provide access for application.");
        }
    }

    public void sendAPISpotify(String accessToken, String debutURL, boolean authOK) {

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

          //System.out.println("##################### " + response.statusCode());

                if (response.statusCode() == 200 || response.statusCode() == 201) {

                    responsFromSpotify = response.body();       // a JSON response with ID

                    // remplissage
                    this.analyseJson(responsFromSpotify);


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


    }


    public void dspList(boolean authOK) {


        if (authOK) {
            System.out.println("---" + this.title + "---");

            this.items.stream().forEach(s -> System.out.println(s));

        } else {

            System.out.println("Please, provide access for application.");

        }
    }


    public void analyseJson(String jsonString) {

    }

    ;


    public void affParPage() {

    }

    ;

    public void affList(boolean authOK) {

        if (authOK) {

            this.affParPage();

        } else {

            System.out.println("Please, provide access for application.");

        }

    }


}
