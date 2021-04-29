# YelpFusionJava

Build and API endpoint to process Yelp data as JSON output

Prerequisites include Maven, JSON, GSON, OKHTTP, and Yelp API account

Project requirements include:
obtain a restaurant's review data from Yelp,
add data to list,
display list as JSON

Start searching for the restaurant on Yelp - make a request and obtain the restaurant's ID from Yelp
The search businesses on YElp it is required that a term is provided like coffee or Starbucks 
as well as either a location as <city, state> or as <latitude, longitude>
Here <city, state> is used
There are optional terms such as radius (permieter around location), categories(like bar or french), price (a scale from 1 the cheapest - 4 the most expensive)
Here optional terms were not used because it tended to severely narrow the search results
During the first search get restaurants name and address for list

Then use the specific ID to search for the restaurant's reviews
During the second search get the reviewers name, image url, rating, and review text

Next make a list using the data retrieved - a list of maps<String,Object> provides key value pairing that fits JSON specificiations
Use Gson to make JSON output easier to read and display the review data
Alternatively, using a review Object instead of a list produces readable JSON

Why use Yelp Fusion API instead of scraping? 
Because API provide access to Yelp's data 
and webpages change meaning a scraper's code could be harder to maintain and get the expected results




