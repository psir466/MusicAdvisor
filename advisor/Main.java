package advisor;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.net.URI;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main {

    public static String DEBUT_URL = "https://accounts.spotify.com";
    ;

    public static String DEBUT_URL2 = "https://api.spotify.com";

    public static final String SPOTIFY_CLIENT_ID = "7f06001afcd84aa695442ad492171be9";

    public static String CODE = "";

    public static String ACCESS_TOKEN = "";

    public static final HttpClient httpClient = HttpClient.newHttpClient();

    public static void main(String[] args) {

        Singleton singleton = Singleton.getInstance();

        singleton.setRecordNumberByPage(5);


        if (args.length != 0) {

            for (int i = 0; i < args.length; i++) {

                if ("-access".equals(args[i]) && i + 1 < args.length) {
                    DEBUT_URL = args[i + 1];
                }

                if ("-resource".equals(args[i]) && i + 1 < args.length) {
                    DEBUT_URL2 = args[i + 1];
                }

                if ("-page".equals(args[i]) && i + 1 < args.length) {

                    // Record By page accssible via un sigleton
                    singleton.setRecordNumberByPage(Integer.parseInt(args[i + 1]));
                }
            }

        }


        // pour test
        //  testJson2();

        String s = "";
        List<String> l = new ArrayList<String>();

        Scanner sc = new Scanner(System.in);

        boolean fin = false;

        Bid n = new New(s, l);
        Bid f = new Featured(s, l);
        Bid c = new Category(s, l);
        Bid m = new Mood(s, l);

        String s2 = "";

        Bid f2 = new Featured(s2);
        Bid n2 = new New(s2);
        Bid c2 = new Category(s2);
        Bid p2 = new Mood(s2);


        boolean authOK = false;

        int page = 1;

        String type = "";

        while (!fin) {

            String input = sc.nextLine();

            String choice = "";

            String playlistsType = "";

            if (input.startsWith("playlists")) {

                choice = "playlists";

                playlistsType = input.substring(10, input.length());

            } else {

                choice = input;

            }

            switch (choice) {

                case "new":
                    n2.sendAPISpotify(ACCESS_TOKEN, DEBUT_URL2, authOK);
                    type = "new";
                    singleton.setPageCourante(1);
                    if (authOK) {
                        n2.affList(authOK);
                    }
                    ;
                    break;


                case "featured":
                    f2.sendAPISpotify(ACCESS_TOKEN, DEBUT_URL2, authOK);
                    type = "featured";
                    singleton.setPageCourante(1);
                    if (authOK) {
                        f2.affList(authOK);
                    }
                    ;
                    break;


                case "categories":
                    c2.sendAPISpotify(ACCESS_TOKEN, DEBUT_URL2, authOK);
                    type = "categories";
                    singleton.setPageCourante(1);
                    if (authOK) {
                        c2.affList(authOK);
                    }
                    ;
                    break;


                case "playlists":
                    p2.sendAPISpotifyWithParameter(ACCESS_TOKEN, DEBUT_URL2, authOK, playlistsType);
                    singleton.setPageCourante(1);
                    type = "playlists";
                    if (authOK) {
                        p2.affList(authOK);
                    }
                    ;
                    break;

                case "auth":
                    try {
                        dspConnexUrl();
                        authOK = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    break;


                case "prev":
                    singleton.setPagePrcedente(singleton.getPageCourante());
                    singleton.setPageCourante(singleton.getPageCourante() - 1);
                    switch (type) {

                        case "playlists":
                            p2.affList(authOK);
                            break;

                        case "new":
                            n2.affList(authOK);
                            break;

                        case "categories":
                            c2.affList(authOK);
                            break;

                        case "featured":
                            f2.affList(authOK);
                            break;

                    }

                    break;

                case "next":
                    singleton.setPagePrcedente(singleton.getPageCourante());
                    singleton.setPageCourante(singleton.getPageCourante() + 1);
                    switch (type) {

                        case "playlists":
                            p2.affList(authOK);
                            break;

                        case "new":
                            n2.affList(authOK);
                            break;

                        case "categories":
                            c2.affList(authOK);
                            break;

                        case "featured":
                            f2.affList(authOK);
                            break;

                    }

                    break;

                case "exit":
                    System.out.println("---GOODBYE!---");
                    fin = true;
                    break;

            }

        }
    }

    public static String connex() throws IOException, InterruptedException {

        System.out.println("use this link to request the access code:");

        String urlLink = DEBUT_URL + "?" +
                "client_id=" + SPOTIFY_CLIENT_ID +
                "redirect_uri=http://localhost:8080&response_type=code\n" +
                "waiting for code...";

        System.out.println(urlLink);


        HttpServer server = HttpServer.create();
        server.bind(new InetSocketAddress(8080), 0);

        server.createContext("/",
                new HttpHandler() {
                    public void handle(HttpExchange exchange) throws IOException {

                        System.out.println("REQUEST RECU !!!!!!!!!!!");

                        String responsString = "";

                        String query = exchange.getRequestURI().getQuery();

                        System.out.println("QUERY " + query);

                        String codeOK = analyseQuery(query);

                        if (codeOK != "NOT FOUND") {

                            responsString = "Got the code. Return back to your program.";

                        } else {

                            responsString = "Authorization code not found. Try again.";
                        }

                        exchange.sendResponseHeaders(200, responsString.length());

                        exchange.getResponseBody().write(responsString.getBytes());
                        exchange.getResponseBody().close();

                        System.out.println("CODE " + codeOK);


                        CODE = codeOK;

                    }
                }
        );


        server.start();

        System.out.println("waiting for code...");

        // attend 10ms avant de tester si code <> " "
        while (CODE.equals("")) {
            Thread.sleep(10);
        }

        server.stop(10);

        String returnString = "";

        String access_Token = "";

        if (CODE != "NOT FOUND") {

            System.out.println("code received");

            returnString = requestToSpotifyForToken(CODE);

            access_Token = accessTokenSpotify(returnString);


        }


        return access_Token;

    }

    public static void dspConnexUrl() throws IOException, InterruptedException {

        ACCESS_TOKEN = connex();

        System.out.println("Success!");
        ;

    }


    public static String analyseQuery(String query) {


        System.out.println("REGEX " + query);

        String returnStr = "NOT FOUND";


        if (query != null) {

            // du coup on cherche code=ajdhagdczga (donc avec code=)
            Pattern p = Pattern.compile("code=[a-zA-Z0-9]+");

            Matcher m = p.matcher(query);

            if (m.find()) {
                returnStr = m.group();

                System.out.println("TROUVE REGEX " + returnStr);
            }
        }

        return returnStr;
    }


    public static String requestToSpotifyForToken(String code) {

        System.out.println("making http request for access_token...");

        String responsFromSpotify = "";

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .uri(URI.create(DEBUT_URL + "/api/token"))
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=authorization_code&" +
                        // "code=" + code +
                        code +
                        "redirect_uri=http://localhost:8080"))
                .build();

        try {
            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // System.out.println(response.statusCode()); // 201 if everything is OK

            responsFromSpotify = response.body();       // a JSON response with ID

        } catch (Exception e) {
            System.out.println("We cannot send data. Please, try later.");
        }

        return responsFromSpotify;
    }

    public static String accessTokenSpotify(String responseSpotify) {

        JsonObject jo = JsonParser.parseString(responseSpotify).getAsJsonObject();

        return jo.get("access_token").getAsString();

    }


    public static void testJson() {

        String json = "{\n" +
                "  \"message\" : \"Monday morning music, coming right up!\",\n" +
                "  \"playlists\" : {\n" +
                "    \"href\" : \"https://api.spotify.com/v1/browse/featured-playlists?country=SE&timestamp=2015-05-18T06:44:32&offset=0&limit=2\",\n" +
                "    \"items\" : [ {\n" +
                "      \"collaborative\" : false,\n" +
                "      \"description\" : \"Relaxed deep house to slowly help you get back on your feet and ready yourself for a productive week.\",\n" +
                "      \"external_urls\" : {\n" +
                "        \"spotify\" : \"http://open.spotify.com/user/spotify/playlist/6ftJBzU2LLQcaKefMi7ee7\"\n" +
                "      },\n" +
                "      \"href\" : \"https://api.spotify.com/v1/users/spotify/playlists/6ftJBzU2LLQcaKefMi7ee7\",\n" +
                "      \"id\" : \"6ftJBzU2LLQcaKefMi7ee7\",\n" +
                "      \"images\" : [ {\n" +
                "        \"height\" : 300,\n" +
                "        \"url\" : \"https://i.scdn.co/image/7bd33c65ebd1e45975bbcbbf513bafe272f033c7\",\n" +
                "        \"width\" : 300\n" +
                "      } ],\n" +
                "      \"name\" : \"Monday Morning Mood\",\n" +
                "      \"owner\" : {\n" +
                "        \"external_urls\" : {\n" +
                "          \"spotify\" : \"http://open.spotify.com/user/spotify\"\n" +
                "        },\n" +
                "        \"href\" : \"https://api.spotify.com/v1/users/spotify\",\n" +
                "        \"id\" : \"spotify\",\n" +
                "        \"type\" : \"user\",\n" +
                "        \"uri\" : \"spotify:user:spotify\"\n" +
                "      },\n" +
                "      \"public\" : null,\n" +
                "      \"snapshot_id\" : \"WwGvSIVUkUvGvqjgj/bQHlRycYmJ2TkoIxYfoalWlmIZT6TvsgvGMgtQ2dGbkrAW\",\n" +
                "      \"tracks\" : {\n" +
                "        \"href\" : \"https://api.spotify.com/v1/users/spotify/playlists/6ftJBzU2LLQcaKefMi7ee7/tracks\",\n" +
                "        \"total\" : 245\n" +
                "      },\n" +
                "      \"type\" : \"playlist\",\n" +
                "      \"uri\" : \"spotify:user:spotify:playlist:6ftJBzU2LLQcaKefMi7ee7\"\n" +
                "    }, {\n" +
                "      \"collaborative\" : false,\n" +
                "      \"description\" : \"Du kommer studsa ur sängen med den här spellistan.\",\n" +
                "      \"external_urls\" : {\n" +
                "        \"spotify\" : \"http://open.spotify.com/user/spotify__sverige/playlist/4uOEx4OUrkoGNZoIlWMUbO\"\n" +
                "      },\n" +
                "      \"href\" : \"https://api.spotify.com/v1/users/spotify__sverige/playlists/4uOEx4OUrkoGNZoIlWMUbO\",\n" +
                "      \"id\" : \"4uOEx4OUrkoGNZoIlWMUbO\",\n" +
                "      \"images\" : [ {\n" +
                "        \"height\" : 300,\n" +
                "        \"url\" : \"https://i.scdn.co/image/24aa1d1b491dd529b9c03392f350740ed73438d8\",\n" +
                "        \"width\" : 300\n" +
                "      } ],\n" +
                "      \"name\" : \"Upp och hoppa!\",\n" +
                "      \"owner\" : {\n" +
                "        \"external_urls\" : {\n" +
                "          \"spotify\" : \"http://open.spotify.com/user/spotify__sverige\"\n" +
                "        },\n" +
                "        \"href\" : \"https://api.spotify.com/v1/users/spotify__sverige\",\n" +
                "        \"id\" : \"spotify__sverige\",\n" +
                "        \"type\" : \"user\",\n" +
                "        \"uri\" : \"spotify:user:spotify__sverige\"\n" +
                "      },\n" +
                "      \"public\" : null,\n" +
                "      \"snapshot_id\" : \"0j9Rcbt2KtCXEXKtKy/tnSL5r4byjDBOIVY1dn4S6GV73EEUgNuK2hU+QyDuNnXz\",\n" +
                "      \"tracks\" : {\n" +
                "        \"href\" : \"https://api.spotify.com/v1/users/spotify__sverige/playlists/4uOEx4OUrkoGNZoIlWMUbO/tracks\",\n" +
                "        \"total\" : 38\n" +
                "      },\n" +
                "      \"type\" : \"playlist\",\n" +
                "      \"uri\" : \"spotify:user:spotify__sverige:playlist:4uOEx4OUrkoGNZoIlWMUbO\"\n" +
                "    } ],\n" +
                "    \"limit\" : 2,\n" +
                "    \"next\" : \"https://api.spotify.com/v1/browse/featured-playlists?country=SE&timestamp=2015-05-18T06:44:32&offset=2&limit=2\",\n" +
                "    \"offset\" : 0,\n" +
                "    \"previous\" : null,\n" +
                "    \"total\" : 12\n" +
                "  }\n" +
                "}";

        Featured f = new Featured();

        f.analyseJson(json);

    }

    public static void testJson2() {

        String json = "{\n" +
                "  \"albums\" : {\n" +
                "    \"href\" : \"https://api.spotify.com/v1/browse/new-releases?country=SE&offset=0&limit=20\",\n" +
                "    \"items\" : [ {\n" +
                "      \"album_type\" : \"single\",\n" +
                "      \"artists\" : [ {\n" +
                "        \"external_urls\" : {\n" +
                "          \"spotify\" : \"https://open.spotify.com/artist/2RdwBSPQiwcmiDo9kixcl8\"\n" +
                "        },\n" +
                "        \"href\" : \"https://api.spotify.com/v1/artists/2RdwBSPQiwcmiDo9kixcl8\",\n" +
                "        \"id\" : \"2RdwBSPQiwcmiDo9kixcl8\",\n" +
                "        \"name\" : \"Pharrell Williams\",\n" +
                "        \"type\" : \"artist\",\n" +
                "        \"uri\" : \"spotify:artist:2RdwBSPQiwcmiDo9kixcl8\"\n" +
                "      } ],\n" +
                "      \"available_markets\" : [ \"AD\", \"AR\", \"AT\", \"AU\", \"BE\", \"BG\", \"BO\", \"BR\", \"CA\", \"CH\", \"CL\", \"CO\", \"CR\", \"CY\", \"CZ\", \"DE\", \"DK\", \"DO\", \"EC\", \"EE\", \"ES\", \"FI\", \"FR\", \"GB\", \"GR\", \"GT\", \"HK\", \"HN\", \"HU\", \"ID\", \"IE\", \"IS\", \"IT\", \"JP\", \"LI\", \"LT\", \"LU\", \"LV\", \"MC\", \"MT\", \"MX\", \"MY\", \"NI\", \"NL\", \"NO\", \"NZ\", \"PA\", \"PE\", \"PH\", \"PL\", \"PT\", \"PY\", \"SE\", \"SG\", \"SK\", \"SV\", \"TR\", \"TW\", \"US\", \"UY\" ],\n" +
                "      \"external_urls\" : {\n" +
                "        \"spotify\" : \"https://open.spotify.com/album/5ZX4m5aVSmWQ5iHAPQpT71\"\n" +
                "      },\n" +
                "      \"href\" : \"https://api.spotify.com/v1/albums/5ZX4m5aVSmWQ5iHAPQpT71\",\n" +
                "      \"id\" : \"5ZX4m5aVSmWQ5iHAPQpT71\",\n" +
                "      \"images\" : [ {\n" +
                "        \"height\" : 640,\n" +
                "        \"url\" : \"https://i.scdn.co/image/e6b635ebe3ef4ba22492f5698a7b5d417f78b88a\",\n" +
                "        \"width\" : 640\n" +
                "      }, {\n" +
                "        \"height\" : 300,\n" +
                "        \"url\" : \"https://i.scdn.co/image/92ae5b0fe64870c09004dd2e745a4fb1bf7de39d\",\n" +
                "        \"width\" : 300\n" +
                "      }, {\n" +
                "        \"height\" : 64,\n" +
                "        \"url\" : \"https://i.scdn.co/image/8a7ab6fc2c9f678308ba0f694ecd5718dc6bc930\",\n" +
                "        \"width\" : 64\n" +
                "      } ],\n" +
                "      \"name\" : \"Runnin'\",\n" +
                "      \"type\" : \"album\",\n" +
                "      \"uri\" : \"spotify:album:5ZX4m5aVSmWQ5iHAPQpT71\"\n" +
                "    }, {\n" +
                "      \"album_type\" : \"single\",\n" +
                "      \"artists\" : [ {\n" +
                "        \"external_urls\" : {\n" +
                "          \"spotify\" : \"https://open.spotify.com/artist/3TVXtAsR1Inumwj472S9r4\"\n" +
                "        },\n" +
                "        \"href\" : \"https://api.spotify.com/v1/artists/3TVXtAsR1Inumwj472S9r4\",\n" +
                "        \"id\" : \"3TVXtAsR1Inumwj472S9r4\",\n" +
                "        \"name\" : \"Drake\",\n" +
                "        \"type\" : \"artist\",\n" +
                "        \"uri\" : \"spotify:artist:3TVXtAsR1Inumwj472S9r4\"\n" +
                "      } ],\n" +
                "      \"available_markets\" : [ \"AD\", \"AR\", \"AT\", \"AU\", \"BE\", \"BG\", \"BO\", \"BR\", \"CH\", \"CL\", \"CO\", \"CR\", \"CY\", \"CZ\", \"DE\", \"DK\", \"DO\", \"EC\", \"EE\", \"ES\", \"FI\", \"FR\", \"GB\", \"GR\", \"GT\", \"HK\", \"HN\", \"HU\", \"ID\", \"IE\", \"IS\", \"IT\", \"JP\", \"LI\", \"LT\", \"LU\", \"LV\", \"MC\", \"MT\", \"MY\", \"NI\", \"NL\", \"NO\", \"NZ\", \"PA\", \"PE\", \"PH\", \"PL\", \"PT\", \"PY\", \"SE\", \"SG\", \"SK\", \"SV\", \"TR\", \"TW\", \"UY\" ],\n" +
                "      \"external_urls\" : {\n" +
                "        \"spotify\" : \"https://open.spotify.com/album/0geTzdk2InlqIoB16fW9Nd\"\n" +
                "      },\n" +
                "      \"href\" : \"https://api.spotify.com/v1/albums/0geTzdk2InlqIoB16fW9Nd\",\n" +
                "      \"id\" : \"0geTzdk2InlqIoB16fW9Nd\",\n" +
                "      \"images\" : [ {\n" +
                "        \"height\" : 640,\n" +
                "        \"url\" : \"https://i.scdn.co/image/d40e9c3d22bde2fbdb2ecc03cccd7a0e77f42e4c\",\n" +
                "        \"width\" : 640\n" +
                "      }, {\n" +
                "        \"height\" : 300,\n" +
                "        \"url\" : \"https://i.scdn.co/image/dff06a3375f6d9b32ecb081eb9a60bbafecb5731\",\n" +
                "        \"width\" : 300\n" +
                "      }, {\n" +
                "        \"height\" : 64,\n" +
                "        \"url\" : \"https://i.scdn.co/image/808a02bd7fc59b0652c9df9f68675edbffe07a79\",\n" +
                "        \"width\" : 64\n" +
                "      } ],\n" +
                "      \"name\" : \"Sneakin’\",\n" +
                "      \"type\" : \"album\",\n" +
                "      \"uri\" : \"spotify:album:0geTzdk2InlqIoB16fW9Nd\"\n" +
                "    }, {\n" +
                "    ...\n" +
                "    } ],\n" +
                "    \"limit\" : 20,\n" +
                "    \"next\" : \"https://api.spotify.com/v1/browse/new-releases?country=SE&offset=20&limit=20\",\n" +
                "    \"offset\" : 0,\n" +
                "    \"previous\" : null,\n" +
                "    \"total\" : 500\n" +
                "  }\n" +
                "}";

        New n = new New();

        n.analyseJson(json);

    }

}
