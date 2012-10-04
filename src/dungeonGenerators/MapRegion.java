package dungeonGenerators;

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
    public static enum Position {UPMOST, RIGHTMOST, DOWNMOST, LEFTMOST}

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
    
    public MapRegion(int x1, int x2, int y1, int y2, boolean horizontalDiv) throws IllegalArgumentException
    {
        if (x2 <= x1 || y2 <= y1)
            throw new IllegalArgumentException();
        
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        subRegion1 = null;
        subRegion2 = null;
        this.horizontalDiv = horizontalDiv;
    }
    
    public void setHorizontalDiv(boolean div)
    {
        horizontalDiv = div;
    }

    public void setSubRegion1(MapRegion subRegion1)
    {
        this.subRegion1 = subRegion1;
    }

    public void setSubRegion2(MapRegion subRegion2)
    {
        this.subRegion2 = subRegion2;
    }

    public void setRoom(MapRegion room)
    {
        this.room = room;
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
