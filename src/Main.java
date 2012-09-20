
import java.util.ArrayList;
import java.util.Random;

public class Main 
{
    public static Random rand = new Random();
    
    public static void main(String[] args) 
    {
        MapRegion root = new MapRegion(1, 79, 1, 39);
        
        // Täytetään luolasto aluksi pelkillä seinillä
        String[][] dungeon = new String[80][40];
        for(int a = 0; a < dungeon.length; a++)
            for(int b = 0; b < dungeon[0].length; b++)
                dungeon[a][b] = "#";
        
        // Jaetaan luolastoa osiin halutun monta kertaa
//        int divisions = 0;
//        while (divisions++ < 2)
//            root.divide();
        
        root.generateDungeon(dungeon, 3);
        
        //root.generateRoom();
        //root.addRoomToArray(dungeon);
        //root.generateCorridors(dungeon);
             
        // Tulostetaan luolasto rivi kerrallaan
        for(int i=0; i < dungeon[0].length;i++)
        {
            for(int k=0; k < dungeon.length;k++)
                System.out.print(dungeon[k][i]);
            System.out.println("");
        }
    }
}
