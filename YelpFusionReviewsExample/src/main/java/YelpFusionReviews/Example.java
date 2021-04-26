package YelpFusionReviews;

/*
 An API endpoint in Java and Spring Boot
 This uses key authentication to access Yelp Fusion API
 2 requests are made to access Yelp's data
 The 1st request retrieves a business ID and business data to display later
 The ID is used to find the referenced business' reviews
 The 2nd request retrieves review data including the rating, text, and some user information
 Ultimately, the review data and the business data are displayed
 */

import java.io.IOException;
import okhttp3.*;
import okhttp3.Request.Builder;
import org.json.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Example {

    public static void main(String[] args)throws JSONException, IOException {

        // unique API key from Yelp located in Manage App
        final String YOUR_API_KEY = "Your API Key";

        // indexes for traversing JSON String body
        final int INDEX_ID = 0;
        final int INDEX_REVIEW = 0; // review only returns 3 reviews so values 3 and on will fail

        // GET /businesses/search parameters
        final String TERM = "pasta"; // Yelp requires a String term which can be food like coffee or even a name like Starbucks
        final String SEARCH_LOCATION = "Durham, NC"; // Yelp requires a location <city, state>
        String id = "";
        String business = "";
        JSONObject businessLocation = null;
        JSONArray address = null;
        String addressFormatted = "";
        JSONObject user = null;

        // client to send requests from
        OkHttpClient client = new OkHttpClient();

        /*
         1st request
         YelpAPI uses businesses/search to determine unique business ID
         business ID will be used to pull reviews
         */
        Request requestID = new Builder()
                .url("https://api.yelp.com/v3/businesses/search?term=" + TERM + "&location=" + SEARCH_LOCATION + "")
                .get()
                .addHeader("authorization", "Bearer"+" "+ YOUR_API_KEY)
                .build();

        try {
            /*
             Get business ID to get review information
             Get business information to display
             Information to display includes name of venue and its location as an address
             Location is an array and needs to the brackets removed
             */
            Response responseID = client.newCall(requestID).execute();

            JSONObject jsonObject = new JSONObject(responseID.body().string().trim()); // parser
            JSONArray myResponseID = (JSONArray)jsonObject.get("businesses");
            id = myResponseID.getJSONObject(INDEX_ID).getString("id");
            business = myResponseID.getJSONObject(INDEX_ID).getString("name");
            businessLocation = myResponseID.getJSONObject(INDEX_ID).getJSONObject("location");
            address = (JSONArray)businessLocation.get("display_address");
            String temp = address.toString();
            addressFormatted = temp.replace("\"","");
          

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        /*
         2nd request
         use business Id to pull reviews
         According to Yelp API documentation only 3 reviews are pulled per request
         */
        Request requestReviews = new Builder()
                .url("https://api.yelp.com/v3/businesses/"+id+"/reviews")
                .get()
                .addHeader("authorization", "Bearer"+" "+ YOUR_API_KEY)
                .build();

        try {
            /*
             Get user information to display from review
             User information to display includes name and image_url
             Get rating and text from review to display
             Display business info collected from 1st response
             */
            Response responseReviews = client.newCall(requestReviews).execute();

            JSONObject jsonObject = new JSONObject(responseReviews.body().string().trim()); // parser
            JSONArray myResponseReviews = (JSONArray)jsonObject.get("reviews");
            user = myResponseReviews.getJSONObject(INDEX_REVIEW).getJSONObject("user");
            String name = (String)user.get("name");
            String imageUrl = (String)user.get("image_url");
            int rating = myResponseReviews.getJSONObject(INDEX_REVIEW).getInt("rating");
            String text = myResponseReviews.getJSONObject(INDEX_REVIEW).getString("text"); // According to Yelp API only up to 160 characters of text will be retrieved

            // display output in JSON format
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String[] yelpData = {"name: " + name,
                    "image url : " + imageUrl,
                    "business : " + business,
                    "location : " + addressFormatted,
                    "rating : " + rating,
                    "text: " + text};
            String json = gson.toJson(yelpData);
            System.out.println("{ review: \n" + json + " \n}");


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}

