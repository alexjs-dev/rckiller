import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.io.BufferedReader;

public class PriceLookup {

    private static URLConnection con;
    private static InputStream is;
    private static InputStreamReader isr;
    private static BufferedReader br;

    private static String[] getData(int itemID) {
        try {
            Main.log("Attempting to get price from RSBUDDY for item: "+itemID);
            URL url = new URL(
                    "https://api.rsbuddy.com/grandExchange?a=guidePrice&i="
                            + itemID);
            con = url.openConnection();
            is = con.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            String line = br.readLine();
            if (line != null) {
                return line.split(",");
            }
            Main.log(line);
        } catch (Exception e) {

            Main.log(e.toString());
        } finally {
            try {
                if (br != null) {
                    br.close();
                } else if (isr != null) {
                    isr.close();
                } else if (is != null) {
                    is.close();
                }
            } catch (Exception e) {

                Main.log(e.toString());
            }
        }
        return null;
    }

    private static BufferedReader bReader;
    private static StringBuilder buffer;

    private static String[] getPriceOld(int itemID) {
        try {
            Main.log("Attempting to get price from OSRS services for item: "+itemID);
            buffer = new StringBuilder();
            int read;
            char[] chars = new char[1024];
            URL url = new URL(
                    "http://services.runescape.com/m=itemdb_oldschool/api/catalogue/detail.json?item=" + itemID);
            bReader = new BufferedReader(new InputStreamReader(url.openStream()));

            while ((read = bReader.read(chars)) != -1) {
                buffer.append(chars, 0, read);
            }
            String line = buffer.toString();

            if (line != null) {
                return line.split(",");
            }
            Main.log(line);
            return null;
        } catch (Exception e) {
            Main.log(e.toString());
        } finally {
            try {
                if (bReader != null) {
                    bReader.close();
                }
            } catch (Exception e) {
                Main.log(e.toString());
            }
        }
        return null;
    }



    //http://services.runescape.com/m=itemdb_oldschool/api/catalogue/detail.json?item=

    public static int getPrice(int itemID) {
        String[] data = getData(itemID);
        if (data != null && data.length == 5) {
            return Integer.parseInt(data[0].replaceAll("\\D", ""));
        }
        return 0;
    }

    public static int getAverageBuyOffer(int itemID) {
        String[] data = getData(itemID);
        if (data != null && data.length == 5) {
            return Integer.parseInt(data[1].replaceAll("\\D", ""));
        }
        return 0;
    }

    public static int getAverageSellOffer(int itemID) {
        String[] data = getData(itemID);
        if (data != null && data.length == 5) {
            return Integer.parseInt(data[3].replaceAll("\\D", ""));
        }
        return 0;
    }
} 