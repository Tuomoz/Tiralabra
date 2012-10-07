package dungeonGenerators;

import dungeonGenerators.MapRegion;
import dungeonGenerators.DungeonMiner;
import java.util.Random;
import javax.swing.JFrame;

public class Main 
{
    public static Random rand = new Random();
    
    public static void main(String[] args) 
    {
        int mapWidth = 200;
        int mapHeight = 100;
        //MapRegion root = new MapRegion(0, mapWidth, 0, mapHeight);
        
        // Täytetään luolasto aluksi pelkillä seinillä
        char[][] dungeon = new char[mapWidth][mapHeight];
        for(int a = 0; a < dungeon.length; a++)
            for(int b = 0; b < dungeon[0].length; b++)
                dungeon[a][b] = '#';
        
        DungeonDivider divider = new DungeonDivider(dungeon);
        DungeonMiner miner = new DungeonMiner(dungeon);
        
        long alkuAika, loppuAika, kulutettuAika;
        alkuAika = System.currentTimeMillis();
        
        miner.generateDungeon();
        //divider.generateDungeon(6);
        
        loppuAika = System.currentTimeMillis();
        kulutettuAika = loppuAika - alkuAika;
        System.out.println("Time to build dungeon: " + kulutettuAika + " ms");
        
        JFrame frame = new JFrame();
        frame.add(new Gui(dungeon, 6));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(mapWidth * 6, mapHeight * 6 + 20);
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
