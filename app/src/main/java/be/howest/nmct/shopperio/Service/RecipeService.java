package be.howest.nmct.shopperio.Service;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import be.howest.nmct.shopperio.Admin.Globals;
import be.howest.nmct.shopperio.Admin.Models.Ingredient;
import be.howest.nmct.shopperio.Admin.Models.Recipe;

public class RecipeService {
    static ArrayList<Recipe> lstRecipes = new ArrayList<>();
    static Recipe recipe = null;

    public static void deleteRecipe(int id) {
        URL url = null;
        Globals g = Globals.getInstance();
        try {
            url = new URL(g.getAPIurl()+"/Api/Recipe/DeleteRecipe?recipeid=" + id);
        } catch (MalformedURLException exception) {
            exception.printStackTrace();
        }
        HttpURLConnection httpURLConnection = null;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            httpURLConnection.setRequestMethod("DELETE");
            httpURLConnection.setRequestProperty("Authorization", "bearer " + g.getToken());
            System.out.println(httpURLConnection.getResponseCode());
        } catch (IOException exception) {
            exception.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
    }

    public static void setToggleFavorite(int id, boolean isFavorite) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httpPost;
        Globals g = Globals.getInstance();
        if (isFavorite) {
            //Unfavorite
            httpPost = new HttpPost(g.getAPIurl()+"/Api/Recipe/PostFavoriteRecipeToUser?recipeid=" + id);
        } else {
            //Favorite
            httpPost = new HttpPost(g.getAPIurl()+"/Api/Recipe/PostFavoriteRecipeToUser?recipeid=" + id);
        }

        try {
            httpPost.setHeader("Authorization", "Bearer " + g.getToken());
            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httpPost);
            System.out.println(response);

        } catch (ClientProtocolException e) {
            Log.e(e.getClass().getName(), e.getMessage());
        } catch (IOException e) {
            Log.e(e.getClass().getName(), e.getMessage());
        }
    }

    private static Recipe makeRecipe(JSONObject jObj) throws JSONException {
        JSONArray jArr = jObj.getJSONArray("Ingredients");
        List<Ingredient> sIngredients = new ArrayList<>();

        for (int i = 0; i < jArr.length(); i++) {
            JSONObject ingredient = jArr.getJSONObject(i);
            Ingredient ing = makeIngredient(ingredient);
            sIngredients.add(ing);
        }

        Recipe rc = new Recipe(
                jObj.getInt("ID"), jObj.getString("Name"), jObj.getString("URLFoto"), jObj.getString("Description"), jObj.getString("Instruction"), sIngredients
        );
        return rc;
    }

    private static Ingredient makeIngredient(JSONObject jObj) throws JSONException {
        Ingredient i = new Ingredient(
                jObj.getInt("ID"), jObj.getString("Name"), "", ""
        );
        i.setIsChecked(false);
        return i;
    }

    public static class getRecipesAsync extends AsyncTask<String, String, ArrayList<Recipe>> {
        public ArrayList<Recipe> doInBackground(String... args) {
            Globals g = Globals.getInstance();

            try {

                URL url = new URL(g.getAPIurl() + "/Api/Recipe/GetRecipesFromUser");

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setInstanceFollowRedirects(false);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "bearer " + g.getToken());
                connection.setUseCaches(false);


                BufferedReader rdr = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                StringBuffer sb = new StringBuffer();
                String line = null;
                while ((line = rdr.readLine()) != null) {
                    sb.append(line);
                }

                rdr.close();

                connection.disconnect();

                JSONArray jArr = new JSONArray(sb.toString());
                for (int i = 0; i < jArr.length(); i++) {
                    JSONObject jObj = jArr.getJSONObject(i);
                    Recipe rc = makeRecipe(jObj);
                    lstRecipes.add(rc);
                }
                return lstRecipes;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static class getByIdAsync extends AsyncTask<String, String, Recipe> {
        public Recipe doInBackground(String... id) {
            Globals g = Globals.getInstance();
            try {
                URL url = new URL(g.getAPIurl()+"/Api/Recipe/Get?id=" + id[0]);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setInstanceFollowRedirects(false);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "bearer " + g.getToken());
                connection.setUseCaches(false);


                BufferedReader rdr = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                StringBuffer sb = new StringBuffer();
                String line = null;
                while ((line = rdr.readLine()) != null) {
                    sb.append(line);
                }

                rdr.close();

                connection.disconnect();

                JSONObject jObj = new JSONObject(sb.toString());
                recipe = makeRecipe(jObj);

                return recipe;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static class createNewRecipe extends AsyncTask<Object, Bitmap, Boolean> {

        @Override
        protected Boolean doInBackground(Object... params) {
            HttpClient httpclient = new DefaultHttpClient();
            String recipeName = params[0].toString().replaceAll(" ", "%20");
            String recipeDescription = params[1].toString().replaceAll(" ", "%20");
            String recipeInstruction = params[2].toString().replaceAll(" ", "%20");
            List<Ingredient> recipeIngredient = (List<Ingredient>) params[3];
            String addtoUrl = "";
            for (Ingredient i : recipeIngredient) {

                addtoUrl += i.getName();
                addtoUrl += "-";
            }
            Globals g = Globals.getInstance();
            HttpPost httppost = new HttpPost(g.getAPIurl()+"/Api/Recipe/AddNewRecipe?name=" + recipeName + "&description=" + recipeDescription + "&instruction=" + recipeInstruction +"&ingredients="+addtoUrl);

            byte[] data = null;
            try {
                httppost.setHeader("Authorization", "bearer " + g.getToken());
                Bitmap bm = null;

                bm = (Bitmap) params[4];

                MultipartEntity entity = new MultipartEntity();

                if (bm != null) {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 70, bos);
                    data = bos.toByteArray();
                    entity.addPart("upload_img", new ByteArrayBody(data, "image/jpeg", "pic.jpg"));
                }


                httppost.setEntity(entity);


                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                System.out.println(response);

            } catch (ClientProtocolException e) {
                Log.e(e.getClass().getName(), e.getMessage());
            } catch (IOException e) {
                Log.e(e.getClass().getName(), e.getMessage());
            }
            return true;
        }
    }

}
