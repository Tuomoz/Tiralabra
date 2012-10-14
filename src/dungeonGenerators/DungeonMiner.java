package dungeonGenerators;

/**
 * Luokka Dungeon minerillä, jonka toiminnallisuus on esitelty dokumentaatiossa.
 * @author Tuomo Kärkkäinen
 */
public class DungeonMiner
{
    public enum Direction {UP, RIGHT, DOWN, LEFT}
    public static final int ROOM_MAX_SIZE = 20;
    public static final int ROOM_MIN_SIZE = 5;
    public static final int PATH_MAX_SIZE = 10;
    private char[][] dungeon;
    private WallStack walls = new WallStack();

    /**
     * Konstruktori luokalle.
     * @param dungeon Taulukko, johon luolasto generoidaan
     */
    public DungeonMiner(char[][] dungeon)
    {
        this.dungeon = dungeon;
    }
    
    /**
     * Generoi konstruktorissa annettuun taulukkoon uuden satunnaisen luolaston.
     * Algoritmi odottaa, että taulukko on tässä vaiheessa täynnä pelkkiä '#'-merkkejä
     * eikä toimi muussa tapauksessa oikein.
     */
    public void generateDungeon()
    {
        // Luodaan aluksi luolaston keskelle yksi huone, josta lähdetään liikkeelle
        generateRoom(dungeon.length/2, dungeon[0].length/2, Direction.RIGHT);
        WallRange wall; 

        // Yritetään luoda uusia huoneita ja käytäviä kunnes kaikki seinät on käyty läpi
        while(!walls.empty())
        {
            wall = walls.pop();
            // Riippuen siitä, onko seinä polun vai huoneen, voidaan säätää todennäköisyyttä
            // jolla luodaan joko huone tai polku
            if (wall.isRoomWall())
            {
                if (Tiralabra.rand.nextInt(1) == 0)
                    generatePath(wall.getRandomX(), wall.getRandomY(), wall.getDirection());
                else
                    generateRoom(wall.getRandomX(), wall.getRandomY(), wall.getDirection());
            }
            else
            {
                if (Tiralabra.rand.nextInt(2) == 0)
                    generatePath(wall.getRandomX(), wall.getRandomY(), wall.getDirection());
                else
                    generateRoom(wall.getRandomX(), wall.getRandomY(), wall.getDirection());
            }
        }
        
    }
    
    /**
     *  Yrittää luoda luolastoon uuden satunnaisen huoneen annetusta koordinaatista annettuun suuntaan.
     * @param x X-koordinaatti
     * @param y Y-koordinaatti
     * @param direction Suunta
     * @return Onnistuiko huoneen luominen
     */
    private boolean generateRoom(int x, int y, Direction direction)
    {
        int roomWidth = Tiralabra.rand.nextInt(ROOM_MAX_SIZE - ROOM_MIN_SIZE) + ROOM_MIN_SIZE;
        int roomHeight = Tiralabra.rand.nextInt(ROOM_MAX_SIZE - ROOM_MIN_SIZE) + ROOM_MIN_SIZE;
        int x1 = 0, x2 = 0, y1 = 0, y2 = 0;
        
        // Tässä yritetään hieman välttää toistettavan koodin määrää, koska metodi on muutenkin aika pitkä
        if (direction == Direction.UP || direction == Direction.DOWN)
        {
            x1 = x - roomWidth/2;
            x2 = x + roomWidth/2;
        }
        else
        {
            y1 = y - roomHeight/2;
            y2 = y + roomHeight/2;
        }
        
        if (direction == Direction.UP)
        {
            y1 = y - roomHeight - 1;
            y2 = y - 1;
            if (!isAreaEmpty(x1 - 1, x2 + 1, y1 - 1, y2 + 1))
                return false;
            
            addWallRanges(x1, x2, y1, y2, Direction.DOWN);
        }
        else if (direction == Direction.DOWN)
        {
            y1 = y + 1;
            y2 = y + roomHeight + 1;
            if (!isAreaEmpty(x1 - 1, x2 + 1, y1 - 1, y2 + 1))
                return false;
            
            addWallRanges(x1, x2, y1, y2, Direction.UP);
        }
        else if (direction == Direction.LEFT)
        {
            x1 = x - roomWidth - 1;
            x2 = x - 1;
            if (!isAreaEmpty(x1 - 1, x2 + 1, y1 - 1, y2 + 1))
                return false;
            
            addWallRanges(x1, x2, y1, y2, Direction.RIGHT);
        }
        else
        {
            x1 = x + 1;
            x2 = x + roomWidth + 1;
            if (!isAreaEmpty(x1 - 1, x2 + 1, y1 - 1, y2 + 1))
                return false;
            
            addWallRanges(x1, x2, y1, y2, Direction.LEFT);
        }
        
        // Tallennetaan huone taulukkoon
        for(int i = y1; i <= y2; i++)
            for( int k = x1; k <= x2; k++)
                dungeon[k][i] = '.';
        // Lisätään vielä tyhjäksi yhdyspiste uuden huoneen ja seinän välillä
        dungeon[x][y] = '.';
        
        return true;
    }
    
    /**
     * Yritää luoda uuden polun annetusta koordinaatista annettuun suuntaan
     * @param x X-koordinaatti
     * @param y Y-koordinaatti
     * @param direction Suunta
     * @return Onnistuiko polun luominen
     */
    private boolean generatePath(int x, int y, Direction direction)
    {
        // Tätä luokkaa olisi varmasti saanut siistittyä enemmän, mutta toimintatapa poikkeaa
        // silti generateRoom:sta sen verran, että samaa tekniikkaa ei voinut tässä suoraan käyttää
        int lenght = Tiralabra.rand.nextInt(PATH_MAX_SIZE - 1) + 1;
        
        if (direction == Direction.UP)
        {
            if (!isAreaEmpty(x - 1, x + 1, y - lenght - 1, y))
                return false;
            walls.add(new WallRange(x - 1, x - 1, y - lenght, y - 1, Direction.LEFT, false));
            walls.add(new WallRange(x + 1, x + 1, y - lenght, y - 1, Direction.RIGHT, false));
            walls.add(new WallRange(x, x, y - lenght - 1, y - lenght - 1, Direction.UP, false));
            for( int k = y - lenght; k <= y; k++)
                dungeon[x][k] = '.';
        }
        if (direction == Direction.DOWN)
        {
            if (!isAreaEmpty(x - 1, x + 1, y, y + lenght + 1))
                return false;
            walls.add(new WallRange(x - 1, x - 1, y + 1, y + lenght, Direction.LEFT, false));
            walls.add(new WallRange(x + 1, x + 1, y + 1, y + lenght, Direction.RIGHT, false));
            walls.add(new WallRange(x, x, y + lenght + 1, y + lenght + 1, Direction.DOWN, false));
            for( int k = y; k <= y + lenght; k++)
                dungeon[x][k] = '.';
        }
        if (direction == Direction.LEFT)
        {
            if (!isAreaEmpty(x - lenght - 1, x, y - 1, y + 1))
                return false;
            walls.add(new WallRange(x - lenght, x - 1, y - 1, y - 1, Direction.UP, false));
            walls.add(new WallRange(x - lenght, x - 1, y + 1, y + 1, Direction.DOWN, false));
            walls.add(new WallRange(x - lenght - 1, x - lenght - 1, y, y, Direction.LEFT, false));
            for( int k = x - lenght; k <= x; k++)
                dungeon[k][y] = '.';
        }
        else
        {
            if (!isAreaEmpty(x, x + lenght + 1, y - 1, y + 1))
                return false;
            walls.add(new WallRange(x + 1, x + lenght, y - 1, y - 1, Direction.UP, false));
            walls.add(new WallRange(x + 1, x + lenght, y + 1, y + 1, Direction.DOWN, false));
            walls.add(new WallRange(x + lenght + 1, x + lenght + 1, y, y, Direction.RIGHT, false));
            for( int k = x; k <= x + lenght; k++)
                dungeon[k][y] = '.';
        }
        
        return true;
    }
    
    private void addWallRanges(int x1, int x2, int y1, int y2, Direction notInThisDirection)
    {
        if (notInThisDirection != Direction.LEFT)
            walls.add(new WallRange(x1 - 1, x1 - 1, y1 + 1, y2 - 1, Direction.LEFT, true));
        if (notInThisDirection != Direction.RIGHT)
            walls.add(new WallRange(x2 + 1, x2 + 1, y1 + 1, y2 - 1, Direction.RIGHT, true));
        if (notInThisDirection != Direction.UP)
            walls.add(new WallRange(x1 + 1, x2 - 1, y1 - 1, y1 - 1, Direction.UP, true));
        if (notInThisDirection != Direction.DOWN)
            walls.add(new WallRange(x1 + 1, x2 - 1, y2 + 1, y2 + 1, Direction.DOWN, true));
    }
    
    /**
     * Tarkistaa, onko annettu suorakulmion muotoinen alue tyhjä, eli sisältää vain '#'-merkkejä. Generoidun
     * luolaston ominaisuuksien vuoksi jokainen polku ja huone on kiinni toisessa polussa tai huoneessa,
     * joten tässä riittää vain tarkistaa, ovatko aluetta ympäröivät reunat tyhjät. Reunojen sisäpuolella
     * ei siis voi koskaan olla mitään, jos reunoillakaan ei ole mitään.
     * @param x1 suorakulmion vasen x-koordinaatti
     * @param x2 suorakulmion oikea x-koordinaatti
     * @param y1 suorakulmion ylempi y-koordinaatti
     * @param y2 suorakulmion alempi y-koordinaatti
     * @return Onko alue tyhjä
     */
    private boolean isAreaEmpty(int x1, int x2, int y1, int y2)
    {
        // Tarkistettava alue ei saa olla taulukon ulkopuolella
        if (x1 < 0 || y1 < 0 || x2 >= dungeon.length || y2 >= dungeon[0].length)
            return false;
        // Pitkän debugfestin jälkeenkään ei selvinnyt, miksi toisesta parametrista pitää vähentää 1
        if (!isHorizontalLineEmpty(y1, x1 - 1, x2))
            return false;
        if (!isHorizontalLineEmpty(y2, x1, x2))
            return false;
        if (!isVerticalLineEmpty(x1, y1, y2))
            return false;
        if (!isVerticalLineEmpty(x2, y1, y2))
            return false;
        return true;
    }
    
    /**
     * Tarkistaa, onko vertikaalinen linja tyhjä
     * @param x Linjan x-koordinaatti
     * @param y1 Ylempi y-koordinaatti
     * @param y2 Alempi y-koordinaatti
     * @return Onko linja tyhjä
     */
    private boolean isVerticalLineEmpty(int x, int y1, int y2)
    {
        while(y1++ < y2)
        {
            if (dungeon[x][y1] == '.')
                return false;
        }
        return true;
    }
    
    /**
     * Tarkista, onko horisontaalinen linja tyhjä
     * @param y Linjan y-koordinaatti
     * @param x1 Vasen x-koordinaatti
     * @param x2 Oikea x-koordinaatti
     * @return Onko linja tyhjä
     */
    private boolean isHorizontalLineEmpty(int y, int x1, int x2)
    {
        while(x1++ < x2)
        {
            if (dungeon[x1][y] == '.')
                return false;
//            else
//                dungeon[x1][y] = "*";
        }
        return true;
    }
}
