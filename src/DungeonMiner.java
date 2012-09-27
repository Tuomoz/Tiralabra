
import java.util.Stack;

public class DungeonMiner
{
    public enum Direction {UP, RIGHT, DOWN, LEFT}
    public static final int ROOM_MAX_SIZE = 20;
    public static final int ROOM_MIN_SIZE = 5;
    public static final int PATH_MAX_SIZE = 10;
    private String[][] dungeon;
    private Stack<WallRange> walls = new Stack<>();

    public DungeonMiner(String[][] dungeon)
    {
        this.dungeon = dungeon;
    }
    
    public void generateDungeon()
    {
        generatePath(dungeon.length/2, dungeon[0].length/2, Direction.DOWN);
        WallRange wall;
//        walls.pop();
//        wall = walls.pop();
//        generatePath(wall.getRandomX(), wall.getRandomY(), wall.getDirection());
        while(!walls.empty())
        {
            System.out.println(walls.size());
            wall = walls.pop();
            if (Main.rand.nextBoolean())
                generatePath(wall.getRandomX(), wall.getRandomY(), wall.getDirection());
            else
                generateRoom(wall.getRandomX(), wall.getRandomY(), wall.getDirection());
        }
        
    }
    
    private boolean generateRoom(int x, int y, Direction direction)
    {
        int roomWidth = Main.rand.nextInt(ROOM_MAX_SIZE - ROOM_MIN_SIZE) + ROOM_MIN_SIZE;
        int roomHeight = Main.rand.nextInt(ROOM_MAX_SIZE - ROOM_MIN_SIZE) + ROOM_MIN_SIZE;
        int x1, x2, y1, y2;
        
        if (direction == Direction.UP)
        {
            x1 = x - roomWidth/2;
            x2 = x + roomWidth/2;
            y1 = y - roomHeight - 1;
            y2 = y - 1;
            if (!isAreaEmpty(x1 - 1, x2 + 1, y1 - 1, y2))
                return false;
            
            walls.add(new WallRange(x1 - 1, x1 - 1, y1 + 1, y2 - 1, Direction.LEFT));
            walls.add(new WallRange(x2 + 1, x2 + 1, y1 + 1, y2 - 1, Direction.RIGHT));
            walls.add(new WallRange(x1 + 1, x2 - 1, y1 - 1, y1 - 1, Direction.UP));
        }
        else if (direction == Direction.DOWN)
        {
            x1 = x - roomWidth/2;
            x2 = x + roomWidth/2;
            y1 = y + 1;
            y2 = y + roomHeight + 1;
            if (!isAreaEmpty(x1 - 1, x2 - 1, y1, y2 + 1))
                return false;
            
            walls.add(new WallRange(x1 - 1, x1 - 1, y1 + 1, y2 - 1, Direction.LEFT));
            walls.add(new WallRange(x2 + 1, x2 + 1, y1 + 1, y2 - 1, Direction.RIGHT));
            walls.add(new WallRange(x1 + 1, x2 - 1, y2 + 1, y2 + 1, Direction.DOWN));
        }
        else if (direction == Direction.LEFT)
        {
            x1 = x - roomWidth - 1;
            x2 = x - 1;
            y1 = y - roomHeight/2;
            y2 = y + roomHeight/2;
            if (!isAreaEmpty(x1 - 1, x2, y1 - 1, y2 + 1))
                return false;
            
            walls.add(new WallRange(x1 + 1, x2 - 1, y1 - 1, y1 - 1, Direction.UP));
            walls.add(new WallRange(x1 + 1, x2 - 1, y2 + 1, y2 + 1, Direction.DOWN));
            walls.add(new WallRange(x1 - 1, x1 - 1, y1 + 1, y2 - 1, Direction.LEFT));
        }
        else
        {
            x1 = x + 1;
            x2 = x + roomWidth + 1;
            y1 = y - roomHeight/2;
            y2 = y + roomHeight/2;
            if (!isAreaEmpty(x1, x2 + 1, y1 - 1, y2 + 1))
                return false;
            
            walls.add(new WallRange(x1 + 1, x2 - 1, y1 - 1, y1 - 1, Direction.UP));
            walls.add(new WallRange(x1 + 1, x2 - 1, y2 + 1, y2 + 1, Direction.DOWN));
            walls.add(new WallRange(x2 - 1, x2 - 1, y1 + 1, y2 - 1, Direction.LEFT));
        }
        
        for(int i = y1; i <= y2; i++)
            for( int k = x1; k <= x2; k++)
                dungeon[k][i] = ".";
        dungeon[x][y] = ".";
        
        return true;
    }
    
    private boolean generatePath(int x, int y, Direction direction)
    {
        int lenght = Main.rand.nextInt(PATH_MAX_SIZE - 1) + 1;
        
        if (direction == Direction.UP)
        {
            if (!isAreaEmpty(x - 1, x + 1, y - lenght - 1, y))
                return false;
            walls.add(new WallRange(x - 1, x - 1, y - lenght, y + 1, Direction.LEFT));
            walls.add(new WallRange(x + 1, x + 1, y - lenght, y + 1, Direction.RIGHT));
            walls.add(new WallRange(x, x, y - lenght - 1, y - lenght - 1, Direction.UP));
            for( int k = y - lenght; k <= y; k++)
                dungeon[x][k] = ".";
        }
        if (direction == Direction.DOWN)
        {
            if (!isAreaEmpty(x - 1, x + 1, y, y + lenght))
                return false;
            walls.add(new WallRange(x - 1, x - 1, y - lenght, y + 1, Direction.LEFT));
            walls.add(new WallRange(x + 1, x + 1, y - lenght, y + 1, Direction.RIGHT));
            walls.add(new WallRange(x, x, y + lenght + 1, y + lenght + 1, Direction.DOWN));
            for( int k = y; k <= y + lenght; k++)
                dungeon[x][k] = ".";
        }
        if (direction == Direction.LEFT)
        {
            if (!isAreaEmpty(x - lenght - 1, x, y - 1, y + 1))
                return false;
            walls.add(new WallRange(x - lenght, x, y - 1, y - 1, Direction.UP));
            walls.add(new WallRange(x - lenght, x, y + 1, y + 1, Direction.DOWN));
            walls.add(new WallRange(x - lenght - 1, x - lenght - 1, y, y, Direction.LEFT));
            for( int k = x - lenght; k <= x; k++)
                dungeon[k][y] = ".";
        }
        else
        {
            if (!isAreaEmpty(x, x + lenght + 1, y - 1, y + 1))
                return false;
            walls.add(new WallRange(x, x + lenght, y - 1, y - 1, Direction.UP));
            walls.add(new WallRange(x, x + lenght, y + 1, y + 1, Direction.DOWN));
            walls.add(new WallRange(x + lenght + 1, x + lenght + 1, y, y, Direction.RIGHT));
            for( int k = x; k <= x + lenght; k++)
                dungeon[k][y] = ".";
        }
        
        return true;
    }
    
    private boolean isAreaEmpty(int x1, int x2, int y1, int y2)
    {
        if (x1 < 0 || y1 < 0 || x2 >= dungeon.length || y2 >= dungeon[0].length)
            return false;
        if (!isHorizontalLineEmpty(y1, x1, x2))
            return false;
        if (!isHorizontalLineEmpty(y2, x1, x2))
            return false;
        if (!isVerticalLineEmpty(x1, y1, y2))
            return false;
        if (!isVerticalLineEmpty(x2, y1, y2))
            return false;
        return true;
    }
    
    private boolean isVerticalLineEmpty(int x, int y1, int y2)
    {
        while(y1++ < y2)
            if (dungeon[x][y1] == ".")
                return false;
        return true;
    }
    
    private boolean isHorizontalLineEmpty(int y, int x1, int x2)
    {
        while(x1++ < x2)
            if (dungeon[x1][y] == ".")
                return false;
        return true;
    }
}
