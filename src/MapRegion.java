public class MapRegion
{
    public static final int REGION_MIN_SIZE = 7;
    // Koska huone ei saa sijaita alueen reunoilla, täytyy sen olla vähintään 2 pituusyksikköä pienempi
    public static final int ROOM_MIN_SIZE = REGION_MIN_SIZE - 2;
    // Huoneen vähimmäiskoko alueseensa nähden prosentuaalisesti
    public static final float ROOM_REGION_MIN_RATIO = 0.4f;
    // Alueen jakopisteen vähimmäissuhde
    public static final float REGION_DIV_MIN_RATIO = 0.4f;
    // Alueen/huoneen koordinaattipisteet
    private final int x1, x2, y1, y2;
    private MapRegion subRegion1, subRegion2, room;
    // Jaetaanko alue pituus- vai leveyssuunnassa
    private boolean horizontalDiv;
    // Käytetään apuna polkujen luonnissa
    private enum Position {UPMOST, RIGHTMOST, DOWNMOST, LEFTMOST}

    /**
     * BSP-puun solu, joka tietää omat mittansa, ala-solunsa sekä lehtisoluilla alueella sijaitsevan huoneen.
     * Käytetään myös kuvaamaan huoneita.
     * 
     * @param x1 Alueen vasen X-koordinaatti
     * @param x2 Alueen oikea X-koordinaatti
     * @param y1 Alueen ylempi Y-koordinaatti
     * @param y2 Alueen alempi Y-koordinaatti
     */
    public MapRegion(int x1, int x2, int y1, int y2) throws IllegalArgumentException
    {
        if (x2 <= x1 || y2 <= y1)
            throw new IllegalArgumentException();
        
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        subRegion1 = null;
        subRegion2 = null;
    }
    
    /**
     * Metodi jakaa alueen satunnaisesti kahteen uuteen alueeseen joko vaaka- tai pystysuunnassa. Satunnaisuutta
     * ei esiinny, jos jako voidaan ylipäätänsä tehdä vain toisessa suunnassa ja jakoa ei suoriteta ollenkaan,
     * jos muodostettavat ala-alueet olisivat liian pieniä(eli sinne ei mahtuisi järkevän kokoista huonetta).
     * Jos alue on jo jaettu, kutsutaan operaatiota ala-alueille.
     * @return Onnistuiko jako vai ei
     */
    public boolean divide()
    {        
        int regionHeight = y2 - y1;
        int regionWidth = x2 - x1;
        // Millä tavalla alue voidaan jakaa?
        boolean canDivHor = regionHeight >= REGION_MIN_SIZE * 2;
        boolean canDivVer =  regionWidth >= REGION_MIN_SIZE * 2;
        
        // Jos alue on liian pieni sekä korkeudeltaan että leveydeltään, ei sitä voi jakaa ollenkaan
        if (!canDivHor && !canDivVer)
            return false;
        if (!canDivHor)
            horizontalDiv = false;
        else if (!canDivVer)
            horizontalDiv = true;
        else
            horizontalDiv = Main.rand.nextBoolean(); // Jos alue on tarpeeksi iso, voidaan jakaa miten vaan
        
        int dividePoint; // Relatiivinen y- tai x-koordinaatti, jonka perusteella jaetaan alue
        
        if (horizontalDiv)
        {
            dividePoint = getDividePoint(regionHeight);
            subRegion1 = new MapRegion(x1, x2, y1, y1 + dividePoint);
            subRegion2 = new MapRegion(x1, x2, dividePoint + y1, y2);
        }
        else
        {
            dividePoint = getDividePoint(regionWidth);
            subRegion1 = new MapRegion(x1, dividePoint + x1, y1, y2);
            subRegion2 = new MapRegion(dividePoint + x1, x2, y1, y2);
        }
        return true;
    }
    
    private int getDividePoint(int widthOrLength)
    {
        if (widthOrLength == REGION_MIN_SIZE * 2)
            return REGION_MIN_SIZE;
        
        int dividePoint = Main.rand.nextInt(widthOrLength - REGION_MIN_SIZE * 2) + REGION_MIN_SIZE;
        // Valitaan jakopiste satunnaisesti niin, että kumpikin ala-alue on vähintään REGION_MIN_SIZE:n kokoinen
        if ((dividePoint) / (float)widthOrLength < REGION_DIV_MIN_RATIO)
            dividePoint = (int)Math.round(Math.ceil(REGION_DIV_MIN_RATIO * widthOrLength));
        else if ((widthOrLength - dividePoint) / (float)widthOrLength < REGION_DIV_MIN_RATIO)
            dividePoint = (int)Math.round(Math.floor((1 - REGION_DIV_MIN_RATIO) * widthOrLength));
        
        return dividePoint;
    }
    
    public void generateDungeon(String[][] dungeon, int divisions)
    {
        generateDungeon(dungeon, this, divisions);
    }
    
    private void generateDungeon(String[][] dungeon, MapRegion region, int divisions)
    {
        if (divisions > 0 && region.divide())
        {
            generateDungeon(dungeon, region.subRegion1, divisions - 1);
            generateDungeon(dungeon, region.subRegion2, divisions - 1);
            MapRegion room1, room2;
            if (region.horizontalDiv)
            {
                room1 = getRoomWithPosition(Position.DOWNMOST, region.subRegion1);
                room2 = getRoomWithPosition(Position.UPMOST, region.subRegion2);
            }
            else
            {
                room1 = getRoomWithPosition(Position.RIGHTMOST, region.subRegion1);
                room2 = getRoomWithPosition(Position.LEFTMOST, region.subRegion2);
            }
            generateCorridors(dungeon, room1, room2, region.horizontalDiv);
            
        }
        else
        {
            region.generateRoom();
            region.addRoomToArray(dungeon);
        }
    }
    
    private MapRegion getRoomWithPosition(Position position, MapRegion region)
    {
        if (region.room != null)
            return region.room;
        if (((position == Position.LEFTMOST || position == Position.RIGHTMOST) && region.horizontalDiv)  ||
            ((position == Position.UPMOST || position == Position.DOWNMOST)) && !region.horizontalDiv)
        {
            if(Main.rand.nextBoolean())
                return getRoomWithPosition(position, region.subRegion1);
            else
                return getRoomWithPosition(position, region.subRegion2);
        }
        if (position == Position.LEFTMOST || position == Position.UPMOST)
            return getRoomWithPosition(position, region.subRegion1);
        else
            return getRoomWithPosition(position, region.subRegion2);
    }
    
    /**
     * Metodi luo alueelle satunnaisen kokoisen huoneen, joka ei kuitenkaan ole isompi kuin alueensa ja
     * mitoiltaan vähintään ROOM_REGION_MIN_RATIO:n verran alueensa mitoista.
     */
    public void generateRoom()
    {
        int regionHeight = y2 - y1 - 2;
        int regionWidth = x2 - x1 - 2;
        
        // Muodostetaan huoneen mitat siten, että mitat ovat vähintään ROOM_MIN_SIZE tai satunnaisesti niin isot kuin mahtuu
        int roomWidth = (regionWidth == ROOM_MIN_SIZE) ? ROOM_MIN_SIZE : Main.rand.nextInt(regionWidth - ROOM_MIN_SIZE) + ROOM_MIN_SIZE;
        int roomHeight = (regionHeight == ROOM_MIN_SIZE) ? ROOM_MIN_SIZE : Main.rand.nextInt(regionHeight - ROOM_MIN_SIZE) + ROOM_MIN_SIZE;
        
        // Varmistetaan, että tuleva huone ei ole liian pieni verrattuna alueeseen, jossa se sijaitsee
        roomWidth = ((float)roomWidth / regionWidth >= ROOM_REGION_MIN_RATIO) ? roomWidth : Math.round(regionWidth * ROOM_REGION_MIN_RATIO);
        roomHeight = ((float)roomHeight / regionHeight >= ROOM_REGION_MIN_RATIO) ? roomHeight : Math.round(regionHeight * ROOM_REGION_MIN_RATIO);
        
        // Jos huone ei ole niin iso, että täyttäisi koko alueen, voidaan siirtää huoneen sijaintia satunnaisesti alueen rajojen sisällä
        int roomXOffset = (roomWidth == regionWidth) ? 0 : Main.rand.nextInt(regionWidth - roomWidth);
        int roomYOffset = (roomHeight == regionHeight) ? 0 : Main.rand.nextInt(regionHeight - roomHeight);
        // Luodaan huone siten, että se ei ole koskaan alueen reunoilla(eli huoneiden välillä aina vähintään yksi seinä)
        room = new MapRegion(x1 + 1 + roomXOffset, x1 + roomXOffset + roomWidth, y1 + 1 + roomYOffset, y1 + roomYOffset + roomHeight);
    }
    
    public void generateCorridors(String[][] dungeon, MapRegion room1 ,MapRegion room2, boolean horizontalDiv)
    {
        if (horizontalDiv)
            generateVerticalCorridor(room1, room2, dungeon);
        else
            generateHorizontalCorridor(room1, room2, dungeon);
    }
    
    public void generateHorizontalCorridor(MapRegion room1, MapRegion room2, String[][] dungeon)
    {
        // Etäisyys lähekkäimpien seinien välillä
        int distance = room2.getX1() - room1.getX2();
        int startY = Main.rand.nextInt(room1.getY2() - room1.getY1() - 2) + room1.getY1() + 1;
        int endY = Main.rand.nextInt(room2.getY2() - room2.getY1() - 2) + room2.getY1() + 1;
        int x = room1.getX2(), y = startY;
        
        for(int i = 0; i < distance/2; i++)
            dungeon[x++][y] = ".";
        
        if (startY < endY)
            while (y != endY)
                dungeon[x][y++] = ".";
        else if (startY > endY)
            while (y != endY)
                dungeon[x][y--] = ".";
        
        while(x != room2.getX1())
            dungeon[x++][y] = ".";
    }
    
    public void generateVerticalCorridor(MapRegion room1, MapRegion room2, String[][] dungeon)
    {
        // Etäisyys lähekkäimpien seinien välillä
        int distance = room2.getY1() - room1.getY2();
        int startX = Main.rand.nextInt(room1.getX2() - room1.getX1() - 2) + room1.getX1() + 1;
        int endX = Main.rand.nextInt(room2.getX2() - room2.getX1() - 2) + room2.getX1() + 1;
        int x = startX, y = room1.getY2();
        
        for(int i = 0; i < distance/2; i++)
            dungeon[x][y++] = ".";
        
        if (startX < endX)
            while (x != endX)
                dungeon[x++][y] = ".";
        else if (startX > endX)
            while (x != endX)
                dungeon[x--][y] = ".";
        
        while(y != room2.getY1())
            dungeon[x][y++] = ".";
    }

    /**
     * Lisää kaikki alueen ala-alueiden huoneet annettuun taulukkoon.
     * @param dungeon Kaksiuloitteinen taulukko, johon huoneet piirretään
     */
    public void addRoomToArray(String[][] dungeon)
    {
//        if (subRegion1 != null)
//        {
//            subRegion1.addRoomToArray(dungeon);
//            subRegion2.addRoomToArray(dungeon);
//            return;
//        }
//        
        int roomHeight = y2 - y1;
        int roomWidth = x2 - x1;
        for(int i = room.y1; i < room.y2; i++)
            for( int k = room.x1; k < room.x2; k++)
                dungeon[k][i] = ".";
    }
    public int getX1()
    {
        return x1;
    }

    public int getX2()
    {
        return x2;
    }

    public int getY1()
    {
        return y1;
    }

    public int getY2()
    {
        return y2;
    }

    public MapRegion getSubRegion1()
    {
        return subRegion1;
    }

    public MapRegion getSubRegion2()
    {
        return subRegion2;
    }

    public MapRegion getRoom()
    {
        return room;
    }    
    public boolean getHorizontalDiv()
    {
        return horizontalDiv;
    }
}
