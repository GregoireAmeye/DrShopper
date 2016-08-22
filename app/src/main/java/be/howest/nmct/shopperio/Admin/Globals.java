package be.howest.nmct.shopperio.Admin;

//Singleton classe https://androidresearch.wordpress.com/2012/03/22/defining-global-variables-in-android/
public class Globals {
    private static Globals instance;

    // Global variable
    private String token = "";
    private String username = "No username found";
    private String email = "No email found";
    private String fotoUrl = "http://tr3.cbsistatic.com/fly/299-fly/bundles/techrepubliccore/images/icons/standard/icon-user-default.png";
    private String APIurl = "http://drshopper.gear.host";

    // Restrict the constructor from being instantiated
    private Globals() {
    }

    public static synchronized Globals getInstance() {
        if (instance == null) {
            instance = new Globals();
        }
        return instance;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public String getAPIurl() {
        return APIurl;
    }
}
