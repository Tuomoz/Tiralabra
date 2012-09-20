public class MapRegion
{
    public static final int REGION_MIN_SIZE = 7;
    // Koska huone ei saa sijaita alueen reunoilla, täytyy sen olla vähintään 2 pituusyksikköä pienempi
    public static final int ROOM_MIN_SIZE = REGION_MIN_SIZE - 2;
    // Huoneen vähimmäiskoko alueseensa nähden prosentuaalisesti
    public static final float ROOM_REGION_MIN_RATIO = 0.4f;
    // Alueen jakopisteen vähimmäissuhde
    public static final float REGION_DIV_MIN_RATIO = 0.4f;
    public static final byte POS_UP = 1, POS_RIGHT = 2, POS_DOWN = 4, POS_LEFT = 8;
    // Alueen/huoneen koordinaattipisteet
    private final int x1, x2, y1, y2;
    private MapRegion subRegion1, subRegion2, room;
    // Jaetaanko alue pituus- vai leveyssuunnassa
    private boolean horizontalDiv;

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
     */
    public void divide()
    {
        // Jos tämä alue on jo jaettu, jaetaan ala-alueet
        if (subRegion1 != null)
        {
            subRegion1.divide();
            subRegion2.divide();
            return;
        }
        
        int regionHeight = y2 - y1;
        int regionWidth = x2 - x1;
        boolean canDivHor = regionHeight >= REGION_MIN_SIZE * 2;
        boolean canDivVer =  regionWidth >= REGION_MIN_SIZE * 2;
        
        // Jos alue on liian pieni sekä korkeudeltaan että leveydeltään, ei sitä voi jakaa ollenkaan
        if (!canDivHor && !canDivVer)
            return;
        if (!canDivHor)
            horizontalDiv = false;
        else if (!canDivVer)
            horizontalDiv = true;
        else
            horizontalDiv = Main.rand.nextBoolean(); // Jos alue on tarpeeksi iso, voidaan jakaa miten vaan
        
        if (horizontalDiv)
        {
            
            int dividePoint; // Relatiivinen y-koordinaatti, jonka perusteella jaetaan alue
            if (regionHeight == REGION_MIN_SIZE * 2)
                dividePoint = REGION_MIN_SIZE;
            else
                dividePoint = Main.rand.nextInt(regionHeight - REGION_MIN_SIZE * 2) + REGION_MIN_SIZE;
                // Valitaan jakopiste satunnaisesti niin, että kumpikin ala-alue on vähintään REGION_MIN_SIZE:n kokoinen
            
            if ((dividePoint) / (float)regionHeight < REGION_DIV_MIN_RATIO)
                dividePoint = (int)Math.round(Math.ceil(REGION_DIV_MIN_RATIO * regionHeight));
            if ((regionHeight - dividePoint) / (float)regionHeight < REGION_DIV_MIN_RATIO)
                dividePoint = (int)Math.round(Math.floor((1 - REGION_DIV_MIN_RATIO) * regionHeight));

            subRegion1 = new MapRegion(x1, x2, y1, y1 + dividePoint);
            subRegion2 = new MapRegion(x1, x2, dividePoint + y1, y2);
        }
        else
        {
            int dividePoint; // Relatiivinen x-koordinaatti, jonka perusteella jaetaan alue
            if (regionWidth == REGION_MIN_SIZE * 2)
                dividePoint = REGION_MIN_SIZE;
            else
                dividePoint = Main.rand.nextInt(regionWidth - REGION_MIN_SIZE * 2) + REGION_MIN_SIZE;
            
            if ((dividePoint) / (float)regionWidth < REGION_DIV_MIN_RATIO)
                dividePoint = (int)Math.round(Math.ceil(REGION_DIV_MIN_RATIO * regionWidth));
            if ((regionWidth - dividePoint) / (float)regionHeight < REGION_DIV_MIN_RATIO)
                dividePoint = (int)Math.round(Math.floor((1 - REGION_DIV_MIN_RATIO) * regionWidth));

            subRegion1 = new MapRegion(x1, dividePoint + x1, y1, y2);
            subRegion2 = new MapRegion(dividePoint + x1, x2, y1, y2);
        }
    }
    
    /**
     * Metodi luo alueelle tai sen ala-alueille satunnaisen kokoisen huoneen, joka ei kuitenkaan ole isompi kuin alueensa ja
     * mitoiltaan vähintään ROOM_REGION_MIN_RATIO:n verran alueensa mitoista.
     */
    public void generateRoom()
    {
        if (subRegion1 != null && subRegion2 != null) // Jos ei ole lehtisolu, mennään puussa alemmas
        {
            subRegion1.generateRoom();
            subRegion2.generateRoom();
            return;
        }
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
    
    public void generateCorridors(String[][] dungeon)
    {
        MapRegion parent = this;
        while(parent.subRegion1.subRegion1 != null)
            parent = parent.subRegion1;
        MapRegion sub1 = parent.subRegion1, sub2 = parent.subRegion2;
        if (parent.getHorizontalDiv())
            generateVerticalCorridor(sub1.getRoom(), sub2.getRoom(), dungeon);
        else
            generateHorizontalCorridor(sub1.getRoom(), sub2.getRoom(), dungeon);
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
        if (subRegion1 != null)
        {
            subRegion1.addRoomToArray(dungeon);
            subRegion2.addRoomToArray(dungeon);
            return;
        }
        
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
