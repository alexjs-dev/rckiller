/**
 * Created by: Niklas
 * Date: 25.06.2017
 * Alias: Dinh
 * Time: 23:55
 */

public enum PriceType {
    RS_BUDDY("http://api.rsbuddy.com/grandExchange?a=guidePrice&i="),
    RUNESCAPE("http://services.runescape.com/m=itemdb_oldschool/api/catalogue/detail.json?item="),
    GRAPHS("http://services.runescape.com/m=itemdb_oldschool/api/graph/");

    private String location;

    PriceType(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }
}