package org.example;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            String city;
            do {
                System.out.println("-----------------------------");
                System.out.print("Enter city (say No to quit): ");
                city = scanner.nextLine();

                if(city.equalsIgnoreCase("No")) {System.out.println("Bye!"); break;}

                String rawData = String.valueOf(getLocationData(city));
                JSONParser parser = new JSONParser();

                // 1. Parse as a JSONArray first!
                JSONArray resultsArray = (JSONArray) parser.parse(rawData);

                // 2. Check if the API actually found the city
                if (resultsArray == null || resultsArray.isEmpty()) {
                    System.out.println("City not found. Please try again.");
                    continue;
                }

                // 3. Get the first city object from the list
                JSONObject cityLocationData = (JSONObject) resultsArray.get(0);

                // 4. Use Double.parseDouble to avoid ClassCastExceptions with Long vs Double
                double latitude = Double.parseDouble(cityLocationData.get("latitude").toString());
                double longitude = Double.parseDouble(cityLocationData.get("longitude").toString());

                displayWeatherData(latitude, longitude);
            }while(!city.equalsIgnoreCase("No"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void displayWeatherData(double latitude, double longitude) {
        try {
            String url = "https://api.open-meteo.com/v1/forecast?latitude="+latitude+"&longitude="+longitude+"&current=temperature_2m,relative_humidity_2m,wind_speed_10m";

            HttpURLConnection apiConnection = fetchApiResponse(url);

            if(apiConnection.getResponseCode() != 200){
                System.out.println("Error: Could not connect to api");
                return;
            }

            String jsonResponse = readApiResponse(apiConnection);

            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(jsonResponse);
            JSONObject currentWeatherJson = (JSONObject) jsonObject.get("current");

            // Change these lines in displayWeatherData:
            double temperature = Double.parseDouble(currentWeatherJson.get("temperature_2m").toString());
            System.out.println("Temperature: " + temperature);

            double relativeHumidity = Double.parseDouble(currentWeatherJson.get("relative_humidity_2m").toString());
            System.out.println("Relative Humidity: " + relativeHumidity);

            double windSpeed = Double.parseDouble(currentWeatherJson.get("wind_speed_10m").toString());
            System.out.println("Wind Speed: " + windSpeed);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String readApiResponse(HttpURLConnection apiConnection) throws IOException {
        StringBuilder resultJson = new StringBuilder();
        try (Scanner scanner = new Scanner(apiConnection.getInputStream())) {
            while (scanner.hasNext()) {
                resultJson.append(scanner.nextLine());
            }
        }
        return resultJson.toString();
    }

    public static JSONArray getLocationData(String locationName){
        // replace any whitespace in location name to + to adhere to API's request format
        locationName = locationName.replaceAll(" ", "+");

        // build API url with location parameter
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name="+locationName+"&count=1&language=en&format=json";

        try{
            // call api and get a response
            HttpURLConnection conn = fetchApiResponse(urlString);

            // check response status
            // 200 means successful connection
            if(conn.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return null;
            }else{
                // store the API results
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(conn.getInputStream());

                // read and store the resulting json data into our string builder
                while(scanner.hasNext()){
                    resultJson.append(scanner.nextLine());
                }

                // close scanner
                scanner.close();

                // close url connection
                conn.disconnect();

                // parse the JSON string into a JSON obj
                JSONParser parser = new JSONParser();
                JSONObject resultsJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

                // get the list of location data the API generated from the location name
                JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
                return locationData;
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        // couldn't find location
        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString) {
        try{
            // attempt to create connection
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // set request method to get
            conn.setRequestMethod("GET");

            // connect to our API
            conn.connect();
            return conn;
        }catch(IOException e){
            e.printStackTrace();
        }

        // could not make connection
        return null;
    }

}