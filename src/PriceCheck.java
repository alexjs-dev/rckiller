import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;

/**
 * Created by: Niklas
 * Date: 25.06.2017
 * Alias: Dinh
 * Time: 23:46
 */

public class PriceCheck {

    private boolean cache;
    private long lastRefresh, refreshRate;
    private HashMap<Integer, Integer> itemCache;

    public PriceCheck(boolean cache, long refreshRate) {
        this.cache = cache;
        this.refreshRate = refreshRate;
        this.lastRefresh = System.currentTimeMillis();
        if (cache) itemCache = new HashMap<>();
    }

    public PriceCheck() {
        this(false, Long.MAX_VALUE);
    }

    public int getPrice(PriceType priceType, int itemID, boolean useCache) {
        if (cache && useCache) {
            if (System.currentTimeMillis() - lastRefresh >= refreshRate) {
                System.out.println("Clearing cache...");
                lastRefresh = System.currentTimeMillis();
                itemCache.clear();
            } else if (itemCache.containsKey(itemID)) {
                System.out.println("Returning price from Cache");
                return itemCache.get(itemID);
            }
        }
        int price = 0;
        switch (priceType) {
            case RS_BUDDY:
                price = getBuddyPrice(itemID);
                break;
            case RUNESCAPE:
                price = getRunescapePrice(itemID);
                break;
            case GRAPHS:
                price = getGraphPrice(itemID);
                break;
        }
        if (cache && price != -1) itemCache.put(itemID, price);
        return price;
    }

    public int getPrice(PriceType priceType, int itemID) {
        return getPrice(priceType, itemID, true);
    }

    public int getPrice(int itemID) {
        int price;
        if ((price = getGraphPrice(itemID)) != -1) return price;
        else if ((price = getBuddyPrice(itemID)) != -1) return price;
        else return getRunescapePrice(itemID);
    }

    /*
     0 = overall
     1 = buying
     2 = buyingQuantity
     3 = selling
     4 = sellingQuantity
     */

    public int getBuddyPrice(int itemID, int index) {
        try {

            String response = readURL(PriceType.RS_BUDDY.getLocation(), itemID);
            int occurrence = ordinalIndexOf(response, ":", index);
            return Integer.parseInt(response.substring(occurrence + 1, response.indexOf(index == 4 ? "}" : ",", occurrence)));
        } catch (IOException e) {
            if(e.toString().contains(("FileNotFound")))
                return 0;

            e.printStackTrace();
            Main.log(e.toString());

        }
        return 0;
    }

    private int ordinalIndexOf(String main, String search, int index) {
        int pos = main.indexOf(search);
        while (index-- > 0 && pos != -1) pos = main.indexOf(search, pos + 1);
        return pos;
    }

    private int getBuddyPrice(int itemID) {
        try {
            try {
                String response = readURL(PriceType.RS_BUDDY.getLocation(), itemID);

                int price = Integer.parseInt(response.substring(response.indexOf(":") + 1, response.indexOf(",")));
         Main.log(" "+price);
                if (price >= 0)
                    return price;

            } catch (IOException e) {
                e.printStackTrace();
                Main.log("OSBUDDY EX: " + e.toString());
                return -1;
            }
        } catch(StringIndexOutOfBoundsException ex){
            return -1;
        }
        return -1;
    }

    private int getRunescapePrice(int itemID) {
        try {
        try {
            String response = readURL(PriceType.RUNESCAPE.getLocation(), itemID);
            int indexOf = response.indexOf("price");
            int price = 0;
            if(indexOf >= 0)
                price = formatPrice(response.substring(response.indexOf(":", indexOf) + 1, response.indexOf("}", indexOf)));
            if(indexOf < 0 || price <= 0)
                price = 0;

            Main.log(" OSRS "+price+" "+indexOf);
                return price;
        } catch (IOException e) {
            if(e.toString().contains(("FileNotFound")))
            return 0;

            Main.log("OSRS EX: "+e.toString());
        }
        return 0;
        } catch(StringIndexOutOfBoundsException ex){
            return 0;
        }
    }

    public int getGraphPrice(int itemID) {
        try {
        try {
            String response = readURL(PriceType.GRAPHS.getLocation(), String.valueOf(itemID) + ".json");
            int lastIndexOf = response.lastIndexOf(":") + 1;
            int price = 0;
            if(lastIndexOf >= 0)
             price = Integer.parseInt(response.substring(lastIndexOf, response.indexOf("}", lastIndexOf)));
            if(lastIndexOf < 0 || price <= 0)
                price = 0;

            Main.log(" GRAPH "+price+" "+lastIndexOf);

                return price;
        } catch (IOException e) {

            if(e.toString().contains(("FileNotFound")))
                return 0;

            e.printStackTrace();
            Main.log("GRAPH EX: "+e.toString());
        }
        return -1;
        } catch(StringIndexOutOfBoundsException ex){
            return -1;
        }
    }

    private Integer formatPrice(String price) {
        return Integer.parseInt(price.replaceAll("k", "000").replaceAll("m", "000000").replaceAll("b", "00000000").replaceAll("[^\\d+]", ""));
    }

    private String readURL(String base, Object o) throws IOException {
        InputStream streamIn = new URL(base + o).openStream();
        try (BufferedReader readIn = new BufferedReader(new InputStreamReader(streamIn, Charset.forName("UTF-8")))) {
            return getString(readIn);
        }
    }

    private String getString(Reader reader) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        int currentChar;
        while ((currentChar = reader.read()) != -1) {
            stringBuilder.append((char) currentChar);
        }
        return stringBuilder.toString();
    }

}