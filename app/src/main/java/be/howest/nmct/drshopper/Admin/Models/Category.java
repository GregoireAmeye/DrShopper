package be.howest.nmct.drshopper.Admin.Models;

/**
 * Created by Greg on 09-Nov-15.
 */
    public enum Category{
        DESSERT("Dessert"),
        PASTA("Pasta"),
        FISH("Fish"),
        FASTFOOD("Fast food"),
        COLD("Cold food"),
        OTHER("Other food");

        private String Category;

        Category(String category) {
            Category = category;
        }

        public String getCategory() {
            return Category;
        }
    }