
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.*;
import javax.swing.*;
import java.awt.*;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.LinkedList;
import java.util.*;
import java.util.List;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

@ScriptManifest(author = "PBP", description = "Checks items and combats of runescrafts, sends data to the server", name = "RCKiller", version = 1.0, category = Category.MONEYMAKING)

public class Main extends AbstractScript {


    Area wildyDitch = new Area (3104, 3520, 3116,3518,0);
    short state = 0;
    int hops = 0;
    int combatLevel = 100;
    int margin = 5;
    boolean useGui = false;
    boolean randomHop = true;
    int[] worlds = {302, 303, 304, 305, 306, 307, 309, 310, 311, 312, 313, 314, 315, 317, 319, 320, 321, 322, 323, 324, 327, 328, 329, 330, 331, 332,333,334,336,338,339,340,341,342,343,344,346,347,348,350,351,352,354,
    355,356,357,358,359,360,362,365,367,368,370,374,375,376,377,378,386,387,388,389,390};
    int minProfitValue = 4000;

    List<String> players = new ArrayList<String>();

    private List<String> getOthersEquipment(Player p) {
        List<String> equipmentList = new LinkedList<String>();
        if(p != null ) {
            int[] equipment = p.getComposite().getApperance();
            for (int i = 0; i < equipment.length; i++) {
                if (equipment[i] - 512 > 0) {
                    String itemName = new Item(equipment[i]-512, 1, null).getName();
                    int itemPrice = new PriceLookup().getPrice(equipment[i]-512);
                //    int itemPrice = new PriceCheck().getGraphPrice(equipment[i]-512);
                    equipmentList.add(itemName+" : "+Integer.toString(itemPrice));
                }
                //MOVE THIS UP!!!! 23.01.2018



                    // equipmentList.add(ItemDefinition.forId(equipment[i] - 512).getName());

            }
        }
        return equipmentList;
    }
    boolean isInwildyDitch(){
        return wildyDitch.contains(getLocalPlayer());
    }
    void walkToDitch(){
            if(!isInwildyDitch() || Calculations.random(0,100) < 15)
            getWalking().walk(wildyDitch.getRandomTile());
    }

    public static boolean pingHost(String host, int port, int timeout) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeout);
            return true;
        } catch (IOException e) {
            return false; // Either timeout or unreachable or failed DNS lookup.
        }
    }

    private final String USER_AGENT = "Mozilla/5.0";
    private void sendingPostRequest() throws Exception {

        String url = "http://209.250.250.136:3000/sendStats";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // Setting basic post request
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Content-Type","application/json");

        log(data[0]+" "+data[1]+"  "+data[2]+"  "+ data[3]+"  "+data[4]);


        String str = "{ \"name\": \""+data[0]+"\", \"key\": \"12ana2NsfA12ana2FGNsfA\", \"cmbLvl\": "+data[1]+", \"risk\": "+data[2]+", " +
                "\"world\": "+data[3]+", \"isRCer\": "+data[4]+" }";



        String postJsonData = str;


        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
       // for(String s : data)
        wr.writeBytes(postJsonData);

        wr.flush();
        wr.close();

      int responseCode = con.getResponseCode();
        log("nSending 'POST' request to URL : " + url);
        log("Post Data : " + postJsonData);
        log("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String output;
        StringBuffer response = new StringBuffer();

        while ((output = in.readLine()) != null) {
            response.append(output);
        }
        in.close();

        //printing result from response
        System.out.println(response.toString());


    }




    float getPriceSum(Player p){
        float total = 0;
        List<String> equipmentList = new LinkedList<String>();
        if(p != null) {
            int[] equipment = p.getComposite().getApperance();
            for (int i = 0; i < equipment.length; i++) {
                if (equipment[i] - 512 > 0) {
                    int itemPrice = new PriceLookup().getPrice(equipment[i]-512);
                  //  int itemPrice = new PriceCheck().getGraphPrice(equipment[i]-512);
                    total +=  (itemPrice) / 1000;

                }


            }
        }


        return total;
    }

    boolean goodProfit(Player p){
        List<String> equipmentList = new LinkedList<String>();
        if(p != null) {
            int[] equipment = p.getComposite().getApperance();
            for (int i = 0; i < equipment.length; i++) {
                if (equipment[i] - 512 > 0) {
                   // int itemPrice = new PriceCheck().getGraphPrice(equipment[i]-512);
                    int itemPrice = new PriceLookup().getPrice(equipment[i]-512);
                    if(itemPrice > minProfitValue)
                        return true;
                }


            }
        }

        return false;
    }

    void hopWorld(){

                if(hops >= worlds.length)
                    hops = Calculations.random(worlds.length);

            getWorldHopper().hopWorld(worlds[hops]);


          if(randomHop)
            hops = Calculations.random(worlds.length);
          else
            hops++;


    }

    public void sleep(){
        sleep(Calculations.random(200, 1000));
    }

    public void sleepLong(){
        sleep(Calculations.random(3200, 6000));
    }


    void actStupid(){
        int random = Calculations.random(1, 10);

        currentStatus = "Acting stupid";
        switch(random){
            case 1: //random camera movement left
                getCamera().rotateTo(Calculations.random(50,100), 0);

                break;

            case 2: //random camera movement right
                getCamera().rotateTo(0, Calculations.random(50,100));

                break;



            case 6:
                Point currentPositionA = getMouse().getPosition();
                Point movePositionA = new Point(currentPositionA.x + Calculations.random(-10,10), currentPositionA.y + Calculations.random (-10, 10));
                getMouse().move(movePositionA);
                getMouse().click(true);

                break;
            case 4: //move mouse
            default:
                Point currentPosition = getMouse().getPosition();
                Point movePosition = new Point(currentPosition.x + Calculations.random(-10,10), currentPosition.y + Calculations.random (-10, 10));
                getMouse().move(movePosition);

                break;

        }

        sleep();
    }
    public String[] data = new String[5];
    public int rcPercent = 0;

    void recordData(){
     try {
         for (Player p : getPlayers().all()) {
             if (p.isSkulled() && goodProfit(p) && !players.contains(p.getName())) {
                 log(p.getName() + "(" + Integer.toString(p.getLevel()) + ")");
                 for (String s : getOthersEquipment(p)) {
                     if (s.contains("Pickaxe") || s.contains("pickaxe"))
                         rcPercent +=50;
                     if(s.contains("Scimitar") || s.contains("scimitar"))
                         rcPercent -=100;
                     if(s.contains("Whip") || s.contains("whip"))
                         rcPercent -=100;
                     if(s.contains("Sword") || s.contains("sword"))
                         rcPercent -=100;
                     if(s.contains("Dagger") || s.contains("dagger"))
                         rcPercent -=100;
                     if(s.contains("Glory") || s.contains("glory"))
                         rcPercent +=30;
                     if(s.contains("Grace") || s.contains("grace"))
                         rcPercent +=10;
                     if(s.contains("Lightness") || s.contains("lightness"))
                         rcPercent +=20;
                     //log(s);
                 }

                 if(rcPercent > 100)
                     rcPercent = 100;
                 if(rcPercent < 0)
                     rcPercent = 0;

                 players.add(p.getName());
                 data[0] = p.getName();
                 data[1] = Integer.toString(p.getLevel());
                 data[2] = Float.toString(getPriceSum(p));
                 data[3] = Integer.toString(getClient().getCurrentWorld());
                 data[4] = Integer.toString(rcPercent);


                 //  if(p.getLevel() >= combatLevel-margin && p.getLevel() <= combatLevel+margin ) {
                 if (useGui)
                     model.addElement(p.getName() + "(" + Integer.toString(p.getLevel()) + ") - " + getClient().getCurrentWorld() + " - " + Float.toString(getPriceSum(p)) + " K");

                  log("Sending Post Request");
                  log(data[0]+" "+data[1]+" "+data[2]+" "+data[3]+" "+data[4]);
                 sendingPostRequest();


             }
         }
     } catch(Exception ex){
         log(ex.toString());
     }
    }

    void resetData(){
        sleepLong();
        cycles = Calculations.random(20,35);
        players.clear();
    }

    int cycles = Calculations.random(20,35);

    Point[] lastPositions = new Point[15];
    private long startTime;
    public void onPaint(Graphics g) {

        long runtime =(System.currentTimeMillis() - startTime);

        g.setColor(Color.white);
        g.drawString("State: " + currentStatus+" | Cycles: ("+cycles+")", 320, 285);
        g.drawString("Run time: " + runtime/1000+" seconds", 320, 300);


        Point currentPosition = getMouse().getPosition();

// Shift all elements down and insert the new element
        for(int i=0;i<lastPositions.length - 1;i++){
            lastPositions[i]=lastPositions[i+1];
        }
        lastPositions[lastPositions.length - 1] = new Point(currentPosition.x, currentPosition.y);

// This is the point before the new point to draw to
        Point lastpoint = null;

        Color mColor = new Color(255, 58, 218);
//Go in reverse
        for(int i=lastPositions.length - 1;i>=0;i--)
        {
            Point p = lastPositions[i];
            if(p != null)
            {
                if(lastpoint == null)
                    lastpoint = p;

                g.setColor(mColor);
                g.drawLine(lastpoint.x, lastpoint.y, p.x, p.y);
            }
            lastpoint = p;

            //Every 2 steps - mouse fade out
            if(i % 2 == 0)
                mColor = mColor.darker();
        }

        g.setColor(Color.YELLOW);
        g.drawRect(currentPosition.x - 3, currentPosition.y - 3, 7, 7);
        g.setColor(Color.WHITE);
        g.drawRect(currentPosition.x, currentPosition.y, 1, 1);
    }

    @Override
    public void onStart() {
        log("Starting Script");
        state = 0;
        hops = Calculations.random(worlds.length);
        getClient().getInstance().setDrawMouse(false);
        startTime = System.currentTimeMillis();

      createGUI();
    }




    JList list;

    DefaultListModel model;

    int counter = 0;

    void createGUI(){
        if( !useGui)
          return;

        JFrame frame = new JFrame("Players found:");
     //   frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 200);
        frame.setVisible(true);

        JPanel j = new JPanel();
        j.setLayout(new BorderLayout());
        model = new DefaultListModel();
        list = new JList(model);
        JScrollPane pane = new JScrollPane(list);
        JButton removeSelected = new JButton("Del Selected");
        JButton removeButton = new JButton("Del Oldest");

        removeSelected.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               if(model.getSize() > 0){
                   int selectedIndex =  list.getSelectedIndex();
                   if (selectedIndex != -1) {
                       model.remove(selectedIndex);
                   }
               }
            }
        });
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (model.getSize() > 0)
                    model.removeElementAt(0);
            }
        });

        frame.add(pane, BorderLayout.CENTER);
        frame.add(removeSelected, BorderLayout.NORTH);
        frame.add(removeButton, BorderLayout.WEST);

    }




    String currentStatus = "";

    @Override
public int onLoop(){

    switch(state){
        case 0:
            //WALK TO WILDY EDGE
            //SKIP IF ALREADY THERE
            currentStatus = "Walking to ditch";

            walkToDitch();
            resetData();
            state ++;
            break;

        case 1:

            //CHECK FOR SKULL, COMBAT, NAME, VALUABLE ITEMS
            //STAY STILL, AND REPEAT 1-2 TIMES
            //INCLUDE ANTI-BAN
            currentStatus = "Scouting ("+state+")";

            if(Calculations.random(0,100) > 70)
                actStupid();

            if(cycles > 0){
                cycles -=1;
                recordData();
            } else {

                state = 2;


            }




            break;

        case 2:
            if(hops >= worlds.length)
               hops = Calculations.random(worlds.length);

            currentStatus = "Hopping: "+worlds[hops];
            //CHANGE WORLD (NOT PVP WORLD)
            hopWorld();
            state = 0;
            break;

        case -1:
        default:

            break;
    }


return 1000 + Calculations.random(5,100);
}


}