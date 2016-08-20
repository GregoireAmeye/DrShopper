package be.howest.nmct.drshopper.Service;


import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
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


import be.howest.nmct.drshopper.Admin.Models.Ingredient;
import be.howest.nmct.drshopper.Admin.Models.Quantity;
import be.howest.nmct.drshopper.Admin.Models.ShoppingList;
import be.howest.nmct.drshopper.Admin.Globals;

public class ShoppingListService {
    static ArrayList<ShoppingList> sLists = new ArrayList<ShoppingList>();
    static ShoppingList sl = null;
    public static int ResponseIngredientId=0;

    public static class getListsAsync extends AsyncTask<String, String, ArrayList<ShoppingList>> {
        public ArrayList<ShoppingList> doInBackground(String... args) {
            Globals g = Globals.getInstance();

            try {
                if(sLists.size()!=0)
                    sLists.clear();
                URL url = new URL(g.getEmail()+"/api/shoppinglist/GetAllShoppingListsFromUser");

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setInstanceFollowRedirects(false);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "bearer " + g.getToken());
                connection.setUseCaches(false);


                BufferedReader rdr = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                StringBuffer sb = new StringBuffer();
                String line = null;
                while ((line = rdr.readLine()) != null)
                    sb.append(line);

                rdr.close();

                connection.disconnect();

                JSONArray jArr = new JSONArray(sb.toString());
                for (int i = 0; i < jArr.length(); i++) {
                    JSONObject jObj = jArr.getJSONObject(i);
                    ShoppingList sl = makeShoppingList(jObj);
                    sLists.add(sl);
                }
                return sLists;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static class getByIdAsync extends AsyncTask<String, String, ShoppingList> {
        public ShoppingList doInBackground(String... id) {
            Globals g = Globals.getInstance();

            try {
                Log.d("FUJITORA", "ID LIST: " +id[0]);
                URL url = new URL("http://drshopperapi.azurewebsites.net/Api/ShoppingList/" + id[0]);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setInstanceFollowRedirects(false);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "bearer " + g.getToken());
                connection.setUseCaches(false);


                BufferedReader rdr = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                StringBuffer sb = new StringBuffer();
                String line = null;
                while ((line = rdr.readLine()) != null)
                    sb.append(line);

                rdr.close();

                connection.disconnect();

                JSONObject jObj = new JSONObject(sb.toString());
                sl = makeShoppingList(jObj);

                return sl;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static class createNewShoppingListb extends AsyncTask<Object, Bitmap, Boolean> {

        @Override
        protected Boolean doInBackground(Object... params) {
            HttpClient httpclient = new DefaultHttpClient();
            String listname = params[0].toString();
           listname = listname.replaceAll(" ","%20");
            HttpPost httppost = new HttpPost("http://drshopperapi.azurewebsites.net/Api/ShoppingList/postshoplistpic?listname=" + listname);
            Globals g = Globals.getInstance();
            byte[] data = null;
            try {
                httppost.setHeader("Authorization", "Bearer " + g.getToken());
                Bitmap bm = null;
                if(params.length>1){
                    bm = (Bitmap)params[1];
                }

                MultipartEntity entity = new MultipartEntity();

                if (bm != null) {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 70, bos);
                    data = bos.toByteArray();
                    entity.addPart("uplod_img", new ByteArrayBody(data, "image/jpeg", "pic.jpg"));
                }

                httppost.setEntity(entity);


                // Add your data
                /*
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("id", "12345"));
                nameValuePairs.add(new BasicNameValuePair("stringdata", "Hi"));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
*/
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


    public static void deleteShoppingList(int id){
        URL url = null;
        Globals g = Globals.getInstance();
        try {
            url = new URL("http://drshopperapi.azurewebsites.net/Api/ShoppingList/DeleteShoppingList?id=" +id);
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

    public static class getQuantities extends AsyncTask<String, String, List<Quantity>> {
        public List<Quantity> doInBackground(String... id) {
            Globals g = Globals.getInstance();
            List<Quantity> quantities = new ArrayList<Quantity>();
            try {
                URL url = new URL("http://drshopperapi.azurewebsites.net/Api/ShoppingList/getquantities?shoppinglistid=" + id[0]);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setInstanceFollowRedirects(false);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "bearer " + g.getToken());
                connection.setUseCaches(false);


                BufferedReader rdr = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                StringBuffer sb = new StringBuffer();
                String line = null;
                while ((line = rdr.readLine()) != null)
                    sb.append(line);

                rdr.close();

                connection.disconnect();


                JSONArray jArr = new JSONArray(sb.toString());
                quantities = makeQuantityList(jArr);

                return quantities;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        private List<Quantity> makeQuantityList(JSONArray jArr) throws JSONException {
            List<Quantity> quantities = new ArrayList<>();
            for(int i = 0; i<jArr.length();i++){
                JSONObject quantity = jArr.getJSONObject(i);
                Quantity q = makeQuantity(quantity);
                quantities.add(q);
            }
            return quantities;
        }

        private Quantity makeQuantity(JSONObject quantity) throws JSONException {
            Quantity q = new Quantity();
            q.setQuantityName(quantity.getString("QuantityName"));
            q.setQuantityValue(quantity.getString("QuantityValue"));
            q.setId(quantity.getInt("Id"));
            JSONObject ingredient = quantity.getJSONObject("Ingredient");
            q.setIngredientId(ingredient.getInt("ID"));

            return q;
        }
    }

    public static void addIngredientsToShoppingList(final List<Ingredient> ingredients, final int shoppinglistId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Globals g = Globals.getInstance();
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://drshopperapi.azurewebsites.net/IngredientDel/AddIngredientsToShoppingList");

                try {
                    httppost.setHeader("Authorization", "Bearer " + g.getToken());




                    List<NameValuePair> nameValuePairs = new ArrayList<>(ingredients.size());
                    int count = 0;
                    for(Ingredient i : ingredients){

                        nameValuePairs.add(new BasicNameValuePair("ingr",i.getName()+""));
                    }
                    nameValuePairs.add(new BasicNameValuePair("shoplistid", shoppinglistId+""));
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    // Execute HTTP Post Request
                    HttpResponse response = httpclient.execute(httppost);



                    System.out.println(response);

                } catch (ClientProtocolException e) {
                    Log.e(e.getClass().getName(), e.getMessage());
                } catch (IOException e) {
                    Log.e(e.getClass().getName(), e.getMessage());
                }
            }
        }).start();
    }

    public static void clearIngredients(final List<Ingredient> ingredients){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Globals g = Globals.getInstance();
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://drshopperapi.azurewebsites.net/IngredientDel/RemoveIngredients");

                try {
                    httppost.setHeader("Authorization", "Bearer " + g.getToken());




                List<NameValuePair> nameValuePairs = new ArrayList<>(ingredients.size());
                    int count = 0;
                    for(Ingredient i : ingredients){

                        nameValuePairs.add(new BasicNameValuePair("ids",i.getID()+""));
                    }
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    // Execute HTTP Post Request
                    HttpResponse response = httpclient.execute(httppost);



                    System.out.println(response);

                } catch (ClientProtocolException e) {
                    Log.e(e.getClass().getName(), e.getMessage());
                } catch (IOException e) {
                    Log.e(e.getClass().getName(), e.getMessage());
                }
            }
        }).start();
    }



    public static class addIngredientToList extends AsyncTask<String, String, Boolean>{
        String ingredientName, quantity, measure;
        int shoplistId;
        public addIngredientToList(String ingredientName, String quanitity, String measure, int shoplistId) {
            this.ingredientName = ingredientName;
            this.quantity = quanitity;
            this.measure = measure;
            this.shoplistId = shoplistId;
        }

        public Boolean doInBackground(String... args) {
            Globals g = Globals.getInstance();
             HttpClient httpclient = new DefaultHttpClient();
             HttpPost httppost = new HttpPost("http://drshopperapi.azurewebsites.net/Api/ShoppingList/PostAddIngredientToShoppingListWithName?ingredient="+ingredientName+"&shoppinglistId="+shoplistId+"&quantityName="+ measure +"&quantityValue=" + quantity);

            try {
                httppost.setHeader("Authorization", "Bearer " + g.getToken());




                //Add your data
                /*
                List<NameValuePair> nameValuePairs = new ArrayList<>(2);
                nameValuePairs.add(new BasicNameValuePair("id", "12345"));
                nameValuePairs.add(new BasicNameValuePair("stringdata", "Hi"));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
*/
                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity, "UTF-8");

                ResponseIngredientId = Integer.parseInt(responseString);


                System.out.println(response);

            } catch (ClientProtocolException e) {
                Log.e(e.getClass().getName(), e.getMessage());
            } catch (IOException e) {
                Log.e(e.getClass().getName(), e.getMessage());
            }
            return true;
        }
    }

    //HULP METHODES
    private static ShoppingList makeShoppingList(JSONObject jObj) throws JSONException {
        JSONArray jArr = jObj.getJSONArray("Ingredients");
        List<Ingredient> sIngredients = new ArrayList<Ingredient>();


        for (int i = 0;i < jArr.length();i++)
        {
            JSONObject ingredient = jArr.getJSONObject(i);
            Ingredient ing = makeIngredient(ingredient);
            sIngredients.add(ing);
        }

        ShoppingList sl = new ShoppingList(
                jObj.getInt("ID"), jObj.getString("Name"), "NZ GA SD", sIngredients, jObj.getString("URLFoto")
        );
        return sl;
    }
    private static Ingredient makeIngredient(JSONObject jObj) throws JSONException {
        Ingredient i = new Ingredient(
                jObj.getInt("ID"), jObj.getString("Name"), "",""
        );
        i.setIsChecked(false);

        return i;
    }




}
