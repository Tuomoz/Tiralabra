package dungeonGenerators;

/**
 * BSP-puun solu, joka tietää omat mittansa, ala-solunsa sekä lehtisoluilla alueella sijaitsevan huoneen.
 * @author Tuomo Kärkkäinen
 */
public class MapRegion
{
    // Alueen/huoneen koordinaattipisteet
    private final int x1, x2, y1, y2;
    private MapRegion subRegion1, subRegion2, room;
    // Jaetaanko alue pituus- vai leveyssuunnassa
    private boolean horizontalDiv;

    /**
     * BSP-puun solu, joka tietää omat mittansa, ala-solunsa sekä lehtisoluilla alueella sijaitsevan huoneen.
     * Käytetään myös kuvaamaan huoneita. Tätä konstruktoria tulisi käyttää vain juuren ja huoneiden luonnissa, 
     * koska nyt alueen jakamiseen ei oteta mitään kantaa.
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
        horizontalDiv = false;
    }
    
    /**
     * BSP-puun solu, joka tietää omat mittansa, ala-solunsa sekä lehtisoluilla alueella sijaitsevan huoneen.
     * Tätä kostruktoria on tarkoitus käyttää vain juurisolun ala-alueiden luomiseen.
     * 
     * @param x1 Alueen vasen X-koordinaatti
     * @param x2 Alueen oikea X-koordinaatti
     * @param y1 Alueen ylempi Y-koordinaatti
     * @param y2 Alueen alempi Y-koordinaatti
     * @param horizontalDiv Jaettiinko ylempi alue horisontaalisesti
     */
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

    /**
     * Asettaa vasemman/ylemmän ala-alueen
     * @param subRegion1 Ala-alue
     */
    public void setSubRegion1(MapRegion subRegion1)
    {
        this.subRegion1 = subRegion1;
    }

    /**
     * Asettaa oikean/alemman ala-alueen
     * @param subRegion2 Ala-alue
     */
    public void setSubRegion2(MapRegion subRegion2)
    {
        this.subRegion2 = subRegion2;
    }

    /**
     * Asettaa alueelle generoidun huoneen
     * @param room Huone
     */
    public void setRoom(MapRegion room)
    {
        this.room = room;
    }

    public void setHorizontalDiv(boolean horizontalDiv)
    {
        this.horizontalDiv = horizontalDiv;
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
