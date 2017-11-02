package solomonkey.bestfoodies;

/**
 * Created by arvin on 10/1/2017.
 */

public class TempHolder {
  // public  static String HOST_ADDRESS="http://10.0.2.2:80/bestfoodies"; //OFFLINE (emulator)
 //  public static String HOST_ADDRESS="http://192.168.1.7:80/bestfoodies"; //OFFLINE (localhost-Pc)
   public static String HOST_ADDRESS="http://arvinmarquez.tech/bestfoodies"; //ONLINE (new hosting)
   // public  static String HOST_ADDRESS="http://firetrackph.cf/bestfoodies"; //ONLINE
    public static int TIME_OUT = 5 * 1000;
    public static String listLoaderWhereClause;
    public static String selectedCategory;
    public static String selectedRecipeID;
    public static boolean writeReviewMode;
}
