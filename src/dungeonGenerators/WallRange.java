package dungeonGenerators;


import dungeonGenerators.DungeonMiner;

public class WallRange
{
    private final int x1, x2, y1, y2;
    private final DungeonMiner.Direction direction;
    private final boolean roomWall;

    public WallRange(int x1, int x2, int y1, int y2, DungeonMiner.Direction direction, boolean roomWall)
    {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.direction = direction;
        this.roomWall = roomWall;
    }
    
    public int getRandomX()
    {
        if (x1 == x2)
            return x1;
        return Main.rand.nextInt(x2 - x1) + x1;
    }
    
    public int getRandomY()
    {
        if (y1 == y2)
            return y1;
        return Main.rand.nextInt(y2 - y1) + y1;
    }

    public DungeonMiner.Direction getDirection()
    {
        return direction;
    }
    
    public boolean isRoomWall()
    {
        return roomWall;
    }
}
