package dungeonGenerators;

import java.awt.Graphics;
import javax.swing.JPanel;

/**
 * JPanel, johon piirretään luolasto.
 * @author Tuomo Kärkkäinen
 */
public class Gui extends JPanel
{
    char[][] dungeon;
    int blockSize;

    /**
     * Konstruktori JPanelille, joka piirtää luolaston näyviin.
     * @param dungeon Luolaston esitys taulukkona
     * @param blockSize Yhden luolaston pisteen koko pikseleissä
     */
    public Gui(char[][] dungeon, int blockSize)
    {
        this.dungeon = dungeon;
        this.blockSize = blockSize;
    }
    
    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
        for(int i=0; i < dungeon[0].length;i++)
        {
            for(int k=0; k < dungeon.length;k++)
            {
                if (dungeon[k][i] == '#')
                    g.fillRect(k * blockSize, i * blockSize, blockSize, blockSize);
                if (dungeon[k][i] == '*')
                {
                    //g.setColor(Color.red);
                    g.drawRect(k * blockSize, i * blockSize, blockSize, blockSize);
                    //g.setColor(Color.black);
                }   
            }
        }
    }
}