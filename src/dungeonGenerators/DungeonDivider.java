package dungeonGenerators;

import static dungeonGenerators.MapRegion.Position;

import dungeonGenerators.MapRegion;
public class DungeonDivider
{
    public static final int REGION_MIN_SIZE = 7;
    // Koska huone ei saa sijaita alueen reunoilla, täytyy sen olla vähintään 2 pituusyksikköä pienempi
    public static final int ROOM_MIN_SIZE = REGION_MIN_SIZE - 2;
    // Huoneen vähimmäiskoko alueseensa nähden prosentuaalisesti
    public static final float ROOM_REGION_MIN_RATIO = 0.4f;
    // Alueen jakopisteen vähimmäissuhde
    public static final float REGION_DIV_MIN_RATIO = 0.4f;
    // Käytetään apuna polkujen luonnissa
    
    private char[][] dungeon;
    private MapRegion root;

    public DungeonDivider(char[][] dungeon)
    {
        this.dungeon = dungeon;
        root = new MapRegion(0, dungeon.length, 0, dungeon[0].length);
    }
    
    /**
     * Metodi jakaa alueen satunnaisesti kahteen uuteen alueeseen joko vaaka- tai pystysuunnassa. Satunnaisuutta
     * ei esiinny, jos jako voidaan ylipäätänsä tehdä vain toisessa suunnassa ja jakoa ei suoriteta ollenkaan,
     * jos muodostettavat ala-alueet olisivat liian pieniä(eli sinne ei mahtuisi järkevän kokoista huonetta).
     * 
     * @return Onnistuiko jako vai ei
     */
    public boolean divide(MapRegion region)
    {
        int regionHeight = region.getY2() - region.getY1();
        int regionWidth = region.getX2() - region.getX1();
        // Millä tavalla alue voidaan jakaa?
        boolean canDivHor = regionHeight >= REGION_MIN_SIZE * 2;
        boolean canDivVer =  regionWidth >= REGION_MIN_SIZE * 2;
        // Jaetaanko alue pituus- vai leveyssuunnassa
        boolean horizontalDiv;
        
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
        int x1 = region.getX1(), x2 = region.getX2(), y1 = region.getY1(), y2 = region.getY2();
        
        if (horizontalDiv)
        {
            dividePoint = getDividePoint(regionHeight);
            region.setSubRegion1( new MapRegion(x1, x2, y1, y1 + dividePoint, horizontalDiv));
            region.setSubRegion2( new MapRegion(x1, x2, dividePoint + y1, y2, horizontalDiv));
        }
        else
        {
            dividePoint = getDividePoint(regionWidth);
            region.setSubRegion1( new MapRegion(x1, dividePoint + x1, y1, y2, horizontalDiv));
            region.setSubRegion2(new MapRegion(dividePoint + x1, x2, y1, y2, horizontalDiv));
        }
        region.setHorizontalDiv(horizontalDiv);
        return true;
    }
    
    /**
     * Laskee satunnaisen relatiivisen koordinaatin, jonka perusteella alue jaetaan. Laskussa varmistetaan, että
     * kumpikaan tulevista ala-alueista ei ole pienempi kuin REGION_MIN_SIZE sekä alueet noudattavat
     * REGION_DIV_MIN_RATIO:lla asetettua suhdetta.
     * 
     * @param widthOrLength Jaettavan alueen pituus tai leveys
     * @return Relatiivinen jakopiste
     */
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
    
    /**
     * Varsinainen metodi, joka luo annettuun taulukkoon satunnaisen luolaston huoneineen ja polkuineen.
     * @param dungeon Kaksiulotteinen taulukko, johon luolasto tallennetaan
     * @param divisions Kuinka monta kertaa alue jaetaan
     */
    public void generateDungeon(int divisions)
    {
        generateDungeon(root, divisions);
    }
    
    /**
     * Luolaston luomiseen käytettävä pseudorekursiivinen metodi.
     * @param dungeon Luolaston taulukko
     * @param region Käsiteltävä alue
     * @param divisions Kuinka monta kertaa jaetaan
     */
    private void generateDungeon(MapRegion region, int divisions)
    {
        if (divisions > 0 && divide(region))
        {
            generateDungeon(region.getSubRegion1(), divisions - 1);
            generateDungeon(region.getSubRegion2(), divisions - 1);
            MapRegion room1, room2;
            if (region.getHorizontalDiv())
            {
                room1 = getRoomWithPosition(Position.DOWNMOST, region.getSubRegion1());
                room2 = getRoomWithPosition(Position.UPMOST, region.getSubRegion2());
                //generateVerticalCorridor(room1, room2);
            }
            else
            {
                room1 = getRoomWithPosition(Position.RIGHTMOST, region.getSubRegion1());
                room2 = getRoomWithPosition(Position.LEFTMOST, region.getSubRegion2());
                generateHorizontalCorridor(room1, room2);
            }
        }
        else
        {
            generateRoom(region);
            addRoomToArray(region.getRoom());
        }
    }
    
    /**
     * Hakee satunnaisesti puun lehdistä yhden huoneen, joka sijaitsee pyydetyssä paikassa. Metodi perustuu
     * puun ominaisuuteen, jonka mukaan kunkin puun vasen solu on aina jaetun alueen vasen tai ylempi puolikas ja
     * vastaavasti oikea solu oikea tai alempi puolikas. Tämä metodi mahdollistaa polun luomisen kahden puun välille
     * siten, että polku kulkee mahdollisimman lähekkäin olevien huoneiden välillä.
     * 
     * @param position Sijainti, jossa huoneen on oltava
     * @param region Alue, josta huone etsitään
     * @return Sijaintia vastaava satunnainen huone
     */
    private MapRegion getRoomWithPosition(Position position, MapRegion region)
    {
        if (region.getRoom() != null)
            return region.getRoom();
        if (((position == Position.LEFTMOST || position == Position.RIGHTMOST) && region.getHorizontalDiv())  ||
            ((position == Position.UPMOST || position == Position.DOWNMOST)) && !region.getHorizontalDiv())
        {
            if(Main.rand.nextBoolean())
                return getRoomWithPosition(position, region.getSubRegion1());
            else
                return getRoomWithPosition(position, region.getSubRegion2());
        }
        if (position == Position.LEFTMOST || position == Position.UPMOST)
            return getRoomWithPosition(position, region.getSubRegion1());
        else
            return getRoomWithPosition(position, region.getSubRegion2());
    }
    
    /**
     * Metodi luo alueelle satunnaisen kokoisen huoneen, joka ei kuitenkaan ole isompi kuin alueensa ja
     * mitoiltaan vähintään ROOM_REGION_MIN_RATIO:n verran alueensa mitoista.
     */
    private void generateRoom(MapRegion region)
    {
        int regionHeight = region.getY2() - region.getY1() - 2;
        int regionWidth = region.getX2() - region.getX1() - 2;
        
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
        region.setRoom( new MapRegion(region.getX1() + 1 + roomXOffset, region.getX1() + roomXOffset + roomWidth, 
                                      region.getY1() + 1 + roomYOffset, region.getY1() + roomYOffset + roomHeight));
    }
    
    /**
     * Luo vaakasuuntaisen polun kahden huoneen välille vasemmalta oikealle.
     * @param room1 Vasemmanpuoleinen huone
     * @param room2 Oikeanpuoleinen huone
     * @param dungeon Taulukko, johon polku tallennetaan
     */
    public void generateHorizontalCorridor(MapRegion room1, MapRegion room2)
    {
        // Etäisyys lähekkäimpien seinien välillä
        int distance = room2.getX1() - room1.getX2();
        // Valitaan alku- ja loppukoordinaatit siten, että polku ei koskaan kulje huoneen kulmasta
        int startY = Main.rand.nextInt(room1.getY2() - room1.getY1() - 2) + room1.getY1() + 1;
        int endY = Main.rand.nextInt(room2.getY2() - room2.getY1() - 2) + room2.getY1() + 1;
        int x = room1.getX2(), y = startY;
        
        // Kuljetaan ensin puoleen väliin vaakasuunnassa
        for(int i = 0; i < distance/2; i++)
            dungeon[x++][y] = '.';
        
        // Jos tarvii, siirrytään pystysuunnassa endY:n määrittämään y-koordinaattiin
        if (startY < endY)
            while (y != endY)
                dungeon[x][y++] = '.';
        else if (startY > endY)
            while (y != endY)
                dungeon[x][y--] = '.';
        
        // Siirrytään loppuun asti vaakasuunnassa
        while(x != room2.getX1())
            dungeon[x++][y] = '.';
    }
    
    /**
     * Luo pystysuuntaisen polun kahden huoneen välille ylhäältä alas.
     * @param room1 Ylempi huone
     * @param room2 Alempi huone
     * @param dungeon Taulukko, johon polku tallennetaan
     */
    public void generateVerticalCorridor(MapRegion room1, MapRegion room2)
    {
        // Etäisyys lähekkäimpien seinien välillä
        int distance = room2.getY1() - room1.getY2();
        // Valitaan alku- ja loppukoordinaatit siten, että polku ei koskaan kulje huoneen kulmasta
        int startX = Main.rand.nextInt(room1.getX2() - room1.getX1() - 2) + room1.getX1() + 1;
        int endX = Main.rand.nextInt(room2.getX2() - room2.getX1() - 2) + room2.getX1() + 1;
        int x = startX, y = room1.getY2();
        
        // Kuljetaan ensin pystysuunnassa puoleen väliin
        for(int i = 0; i < distance/2; i++)
            dungeon[x][y++] = '.';
        
        // Jos tarvii, siirrytään vaakasuunnassa endX:n määrittämään x-koordinaattiin
        if (startX < endX)
            while (x != endX)
                dungeon[x++][y] = '.';
        else if (startX > endX)
            while (x != endX)
                dungeon[x--][y] = '.';
        
        // Siirrytään loppuun asti pystysuunnassa
        while(y != room2.getY1())
            dungeon[x][y++] = '.';
    }

    /**
     * Lisää kaikki alueen ala-alueiden huoneet annettuun taulukkoon.
     * @param dungeon Kaksiuloitteinen taulukko, johon huoneet piirretään
     */
    public void addRoomToArray(MapRegion room)
    {
        for(int i = room.getY1(); i < room.getY2(); i++)
            for( int k = room.getX1(); k < room.getX2(); k++)
                dungeon[k][i] = '.';
    }
}
