package advisor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class New extends Bid {

    static final List<String> nList = List.of("Mountains [Sia, Diplo, Labrinth]",
            "Runaway [Lil Peep]",
            "The Greatest Show [Panic! At The Disco]",
            "All Out Life [Slipknot]");

    static final String nTitle = "NEW RELEASES";

    static final String nUrlAPI = "/v1/browse/new-releases";

    public New() {


    }

    public New(String title, List<String> items) {
        super(title, items);

        this.items = nList;
        this.title = nTitle;
    }


    public New(String urlPI) {
        super(urlPI);

        this.urlPI = nUrlAPI;
    }


    @Override
    public void analyseJson(String jsonString) {

        Singleton singleton = Singleton.getInstance();

        int nbrecordByPage = singleton.getRecordNumberByPage();

        JsonObject jo = JsonParser.parseString(jsonString).getAsJsonObject();

        JsonObject albums = jo.getAsJsonObject("albums");

        int page = 0;

        int record = 0;

        // réinit hpage
        hPage.clear();
        // réinit page porté par le singleton
        singleton.setNbPageTotale(0);

        List<String> itemString = new ArrayList<>();

        for (JsonElement item : albums.getAsJsonArray("items")) {

            record++;

            // Si changement de page
            if (record > nbrecordByPage) {

                page++;
                hPage.put(page, itemString);
                itemString = new ArrayList<>();

            }

            JsonObject itemJsonObject = item.getAsJsonObject();

            String[] nameArtists = new String[itemJsonObject.getAsJsonArray("artists").size()];

            int i = 0;

            for (JsonElement artist : itemJsonObject.getAsJsonArray("artists")) {

                JsonObject artistJsonObject = artist.getAsJsonObject();

                nameArtists[i] = artistJsonObject.get("name").getAsString();

                i++;

            }

            JsonObject externalurlsObj = itemJsonObject.getAsJsonObject("external_urls");

            itemString.add(itemJsonObject.get("name").getAsString() + "\n" +
                    Arrays.toString(nameArtists) + "\n" +
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
