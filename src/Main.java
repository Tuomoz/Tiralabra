
import java.util.ArrayList;
import java.util.Random;

public class Main 
{
    public static Random rand = new Random();
    
    public static void main(String[] args) 
    {
        MapRegion root = new MapRegion(1, 79, 1, 39);
        
        String[][] dungeon = new String[80][40];
        for(int a = 0; a < dungeon.length; a++)
            for(int b = 0; b < dungeon[0].length; b++)
                dungeon[a][b] = "#";
        
        int divisions = 0;
        while (divisions++ < 4)
            root.divide();
        root.generateRoom();
        root.addRoomToArray(dungeon);
        
        
        
        for(int i=0; i < dungeon[0].length;i++)
        {
            for(int k=0; k < dungeon.length;k++)
                System.out.print(dungeon[k][i]);
            System.out.println("");
        }
    }
}
