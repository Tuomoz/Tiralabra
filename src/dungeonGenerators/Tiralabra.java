package dungeonGenerators;

import java.util.Random;
import javax.swing.JFrame;

/**
 * Pääluokka, jolla algoritmeja testataan. Sisältää käytännössä harjoitustyön ulkopuolista
 * tavaraa, jota ilmankin algoritmit toimisivat.
 * @author Tuomo Kärkkäinen
 */
public class Tiralabra 
{
    public static Random rand = new Random();
    
    public static void main(String[] args) 
    {
        int mapWidth = Integer.parseInt(args[0]);
        int mapHeight = Integer.parseInt(args[1]);
        
        // Täytetään luolasto aluksi pelkillä seinillä
        char[][] dungeon = new char[mapWidth][mapHeight];
        for(int a = 0; a < dungeon.length; a++)
            for(int b = 0; b < dungeon[0].length; b++)
                dungeon[a][b] = '#';
        
        DungeonDivider divider = new DungeonDivider(dungeon);
        DungeonMiner miner = new DungeonMiner(dungeon);
        
        long alkuAika, loppuAika, kulutettuAika;
        alkuAika = System.currentTimeMillis();
        
        if (Integer.parseInt(args[2]) == 1)
            divider.generateDungeon(Integer.parseInt(args[3]));
        else
            miner.generateDungeon();
        
        
        loppuAika = System.currentTimeMillis();
        kulutettuAika = loppuAika - alkuAika;
        System.out.println("Time to build dungeon: " + kulutettuAika + " ms");
        
        // Näytetään graafinen esitys luodusta luolastosta
        JFrame frame = new JFrame();
        frame.add(new Gui(dungeon, 4));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(mapWidth * 4, mapHeight * 4 + 20);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
        
        // Tulostetaan luolasto rivi kerrallaan
        for(int i=0; i < dungeon[0].length;i++)
        {
            for(int k=0; k < dungeon.length;k++)
                System.out.print(dungeon[k][i]);
            System.out.println("");
        }
    }
}
