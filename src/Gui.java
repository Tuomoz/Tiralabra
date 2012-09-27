
import java.awt.Graphics;
import javax.swing.JPanel;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


public class Gui extends JPanel
{
    String[][] dungeon;
    int blockSize;

    public Gui(String[][] dungeon, int blockSize)
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
                if (dungeon[k][i] == "#")
                    g.fillRect(k * blockSize, i * blockSize, blockSize, blockSize);
            }
        }
    }
}