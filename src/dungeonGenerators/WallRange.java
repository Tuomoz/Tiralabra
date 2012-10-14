package dungeonGenerators;

/**
 * Luokka koordinaattivälille, jota hyödynnetään Dungeon minerissä.
 * @author Ambiga
 */
public class WallRange
{
    private final int x1, x2, y1, y2;
    private final DungeonMiner.Direction direction;
    private final boolean roomWall;

    /**
     * Konstruktori luokalle, jossa määritellään seinän rajat. Käytännössä aina siis joko molemmat
     * x- tai y-koordinaatit ovat samat, koska seinät eivät ole suorakulmioita vaan viivoja
     * @param x1 Seinän vasen x-koordinaatti
     * @param x2 Seinän oikea x-koordinaatti
     * @param y1 Seinän ylempi y-koordinaatti
     * @param y2 Seinän alempi y-koordinaatti
     * @param direction Mihin suuntaan seinä osoittaa
     * @param roomWall Onko kyseessä huoneen seinä?
     */
    public WallRange(int x1, int x2, int y1, int y2, DungeonMiner.Direction direction, boolean roomWall)
    {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.direction = direction;
        this.roomWall = roomWall;
    }
    
    /**
     * Palauttaa seinältä satunnaisen x-koordinaatin.
     * @return Arvottu x-koordinaatti
     */
    public int getRandomX()
    {
        if (x1 == x2)
            return x1;
        return Tiralabra.rand.nextInt(x2 - x1) + x1;
    }
    
    /**
     * Palauttaa seinältä satunnaisen y-koordinaatin.
     * @return Arvottu y-koordinaatti
     */
    public int getRandomY()
    {
        if (y1 == y2)
            return y1;
        return Tiralabra.rand.nextInt(y2 - y1) + y1;
    }

    /**
     * Palauttaa seinän suunnan.
     * @return Seinän suunta
     */
    public DungeonMiner.Direction getDirection()
    {
        return direction;
    }
    
    /**
     * Palauttaa truen, jos kyseessä on huoneen seinä. Muuten falsen.
     */
    public boolean isRoomWall()
    {
        return roomWall;
    }
}
