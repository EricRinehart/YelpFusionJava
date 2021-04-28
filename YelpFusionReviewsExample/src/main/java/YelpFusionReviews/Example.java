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
import java.util.*;
import okhttp3.*;
import okhttp3.Request.Builder;
import org.json.*;


public class Example {

    public static void main(String[] args)throws JSONException, IOException {

        // unique API key from Yelp located in Manage App
        final String YOUR_API_KEY = "Your API Key";

        // indexes for traversing JSON String body
        final int INDEX_ID = 0; //by default returns 20 reviews but up to 50
        final int INDEX_REVIEW = 0; // review only returns 3 reviews so values 3 and on will fail
     
        // Strings for key values in treemap
        final String REVIEW_KEY = "review";
        final String NAME_KEY = "name";
        final String IMAGE_KEY = "image_url";
        final String BUSINESS_KEY = "business";
        final String LOCATION_KEY = "location";
        final String RATING_KEY = "rating";
        final String TEXT_KEY = "text";

        // GET /businesses/search parameters
        final String TERM = "pasta"; // Yelp requires a String term which can be food like coffee or even a name like Starbucks
        final String SEARCH_LOCATION = "Durham, NC"; // Yelp requires a location <city, state>
        String id = "";
        String business = "";
        JSONObject businessLocation = null;
        JSONArray address = null;
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

            // create a treemap to hold review object and to help access data for display
            TreeMap<String, Object> yelpReview = new TreeMap<>();
            TreeMap<String, Object> data = new TreeMap<>();
            data.put(NAME_KEY, name);
            data.put(IMAGE_KEY, imageUrl);
            data.put(BUSINESS_KEY, business);
            data.put(LOCATION_KEY, address);
            data.put(RATING_KEY, rating);
            data.put(TEXT_KEY, text);
            yelpReview.put(REVIEW_KEY, data);
         
            //convert data map to set
            Set<Map.Entry<String, Object>> dataSet = data.entrySet();
            //convert data set to array
            List<Map.Entry<String, Object>> reviewValues = new ArrayList<>( dataSet );
            //create one object{"review": [array of data]} - now data can be accessed with review key as an array of values
            yelpReview.put(REVIEW_KEY, reviewValues);

            //display ordered JSON format
            System.out.println("{ \""+ yelpReview.firstKey() +"\" :\n" +
                    "[\"" + NAME_KEY + "\" : \"" + data.get(NAME_KEY) + "\"\n" +
                    "\"" + IMAGE_KEY + "\" : \"" + data.get(IMAGE_KEY) + "\"\n" +
                    "\"" + BUSINESS_KEY + "\" : \"" + data.get(BUSINESS_KEY) + "\"\n" +
                    "\"" + LOCATION_KEY + "\" : " + data.get(LOCATION_KEY) + "\n" +
                    "\"" + RATING_KEY + "\" : " + data.get(RATING_KEY) + "\n" +
                    "\"" + TEXT_KEY + "\" : \"" + data.get(TEXT_KEY) + "\"]\n}");



        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}

