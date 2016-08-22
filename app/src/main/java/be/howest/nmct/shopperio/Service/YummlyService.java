package be.howest.nmct.shopperio.Service;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import be.howest.nmct.shopperio.Admin.Models.Ingredient;
import be.howest.nmct.shopperio.Admin.Models.Recipe;
import be.howest.nmct.shopperio.Service.Helper.Request;
import be.howest.nmct.shopperio.Service.Helper.Response;

public class YummlyService {

    private static final String YummlyAppID = "6f704607";
    private static final String YummlyAppKey = "3bbaffefedf74fad3a743fd2e6ab5f35";
    private static final String BaseURL = String.format("http://api.yummly.com/v1/api/recipes?_app_id=%s&_app_key=%s", YummlyAppID, YummlyAppKey);


    public static ArrayList<Recipe> SearchRecipes(String SearchQuery) {

        String URL = BaseURL + "&q=" + SearchQuery;
        return FireRequest(URL, false);
    }

    public static ArrayList<Recipe> SearchRecipe(String SearchQuery) {

        String URL = BaseURL + "&q=" + SearchQuery;
        return FireRequest(URL, true);
    }

    private static ArrayList<Recipe> FireRequest(String URL, boolean oneRecipe) {
        // Have one (or more) threads ready to do the async tasks. Do this during startup of your app.
        ExecutorService executor = Executors.newFixedThreadPool(1);
        ArrayList<Recipe> recipes = new ArrayList<>();

        // Fire a request.
        try {
            Future<Response> response = executor.submit(new Request(new URL(URL)));
            // Do your other tasks here (will be processed immediately, current thread won't block).
            // ...

            // Get the response (here the current thread will block until response is returned).
            InputStream body = response.get().getBody();
            // ...

            BufferedReader rdr = new BufferedReader(new InputStreamReader(body));

            StringBuffer sb = new StringBuffer();
            String line = null;
            while ((line = rdr.readLine()) != null)
                sb.append(line);

            rdr.close();
            JSONObject jsonObject = new JSONObject(sb.toString());

            JSONArray recipesJson = jsonObject.getJSONArray("matches");
            if (oneRecipe) recipes = makeRecipe(recipesJson);
            else recipes = makeRecipes(recipesJson);

            // Shutdown the threads during shutdown of your app.
            executor.shutdown();

        } catch (MalformedURLException ex) {
            Log.e("Malformed url ex", ex.toString());
        } catch (InterruptedException | ExecutionException ex) {
            Log.e("Interrup, execution er", ex.toString());
        } catch (IOException ex) {
            Log.e("IO Exception", ex.toString());
        } catch (JSONException ex) {
            Log.e("JSON exception", ex.toString());
        }

        return recipes;


    }

    private static ArrayList<Recipe> makeRecipes(JSONArray recipesJson) throws JSONException {
        ArrayList<Recipe> recipes = new ArrayList<>();
        for (int i = 0; i < recipesJson.length(); i++) {
            JSONObject jsonrecept = recipesJson.getJSONObject(i);
            Recipe recipe = new Recipe();
            recipe.setName(jsonrecept.getString("recipeName"));
            recipe.setURLFoto(jsonrecept.getJSONObject("imageUrlsBySize").getString("90"));

            JSONArray jsoningredients = jsonrecept.getJSONArray("ingredients");
            for (int ii = 0; ii < jsoningredients.length(); ii++) {
                Ingredient ingredient = new Ingredient(jsoningredients.getString(ii));
                recipe.addIngredient(ingredient);
            }
            recipes.add(recipe);
        }


        return recipes;
    }

    private static ArrayList<Recipe> makeRecipe(JSONArray recipesJson) throws JSONException {
        ArrayList<Recipe> recipes = new ArrayList<>();
        JSONObject jsonrecept = recipesJson.getJSONObject(0);
        Recipe recipe = new Recipe();
        recipe.setName(jsonrecept.getString("recipeName"));
        recipe.setURLFoto(jsonrecept.getJSONObject("imageUrlsBySize").getString("90"));

        JSONArray jsoningredients = jsonrecept.getJSONArray("ingredients");
        for (int ii = 0; ii < jsoningredients.length(); ii++) {
            Ingredient ingredient = new Ingredient(jsoningredients.getString(ii));
            recipe.addIngredient(ingredient);
        }
        recipes.add(recipe);


        return recipes;
    }
}
