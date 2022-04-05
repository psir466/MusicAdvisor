package advisor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class Featured extends Bid {

    static final List<String> nList = List.of("Mellow Morning",
            "Wake Up and Smell the Coffee",
            "Monday Motivation",
            "Songs to Sing in the Shower");

    static final String nTitle = "FEATURED";

    static final String nUrlAPI = "/v1/browse/featured-playlists";

    public Featured(String title, List<String> items) {
        super(title, items);

        this.items = nList;
        this.title = nTitle;
    }

    public Featured(String urlPI) {
        super(urlPI);

        this.urlPI = nUrlAPI;
    }

    public Featured() {

    }

    @Override
    public void analyseJson(String jsonString) {

        Singleton singleton = Singleton.getInstance();

        int nbrecordByPage = singleton.getRecordNumberByPage();

        JsonObject jo = JsonParser.parseString(jsonString).getAsJsonObject();

        JsonObject playlists = jo.getAsJsonObject("playlists");

        int page = 0;

        int record = 0;

        // réinit hpage
        hPage.clear();
        // réinit page porté par le singleton
        singleton.setNbPageTotale(0);

        List<String> itemString = new ArrayList<>();

        for (JsonElement item : playlists.getAsJsonArray("items")) {

            record++;

            // Si changement de page
            if (record > nbrecordByPage) {

                page++;
                hPage.put(page, itemString);
                itemString = new ArrayList<>();


            }

            JsonObject itemJsonObject = item.getAsJsonObject();

            JsonObject externalurlsObj = itemJsonObject.getAsJsonObject("external_urls");

            itemString.add(itemJsonObject.get("name").getAsString() + "\n" +
                    externalurlsObj.get("spotify").getAsString());

        }

        // Chargement dernière page
        if (!itemString.isEmpty()) {
            page++;
            // Chargement dernière page
            hPage.put(page, itemString);
        }

        // nb totale page porté par le singleton
        singleton.setNbPageTotale(page);

    }

    ;

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
