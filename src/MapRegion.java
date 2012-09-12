public class MapRegion
{
    public static final int REGION_MIN_SIZE = 8;
    // Koska huone ei saa sijaita alueen reunoilla, täytyy sen olla vähintään 2 pituusyksikköä pienempi
    public static final int ROOM_MIN_SIZE = REGION_MIN_SIZE - 2; 
    private final int x1, x2, y1, y2;
    private MapRegion subRegion1, subRegion2, room;

    public MapRegion(int x1, int x2, int y1, int y2)
    {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
    }
    
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
        boolean horizontalDiv; // Jaetaanko alue pituus- vai leveyssuunnassa
        
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

            subRegion1 = new MapRegion(x1, dividePoint + x1, y1, y2);
            subRegion2 = new MapRegion(dividePoint + x1, x2, y1, y2);
        }
    }
    
    public void generateRoom()
    {
        if (subRegion1 != null) // Jos ei ole lehtisolu, mennään puussa alemmas
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
        
        
        // Jos huone ei ole niin iso, että täyttäisi koko alueen, voidaan siirtää huoneen sijaintia satunnaisesti alueen rajojen sisällä
        int roomXOffset = (roomWidth == regionWidth) ? 0 : Main.rand.nextInt(regionWidth - roomWidth);
        int roomYOffset = (roomHeight == regionHeight) ? 0 : Main.rand.nextInt(regionHeight - roomHeight);
        // Luodaan huone siten, että se ei ole koskaan alueen reunoilla(eli huoneiden välillä aina vähintään yksi seinä)
        room = new MapRegion(x1 + 1 + roomXOffset, x1 + roomXOffset + roomWidth, y1 + 1 + roomYOffset, y1 + roomYOffset + roomHeight);
    }

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
            {
                dungeon[k][i] = ".";
            }
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
}
