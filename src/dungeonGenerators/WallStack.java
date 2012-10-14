package dungeonGenerators;

/**
 * Pino, johon voidaan lisätä wallRange-olioita.
 * @author Tuomo Kärkkäinen
 */
public class WallStack
{
    private StackNode top = null;
    
    /**
     * Lisää uuden wallRange-olion pinoon.
     * @param wallRange Lisättävä olio
     */
    public void add(WallRange wallRange)
    {
        StackNode newNode = new StackNode(wallRange);
        newNode.next = top;
        top = newNode;
    }
    
    /**
     * Poistaa ja palauttaa ylimmän wallRange-olion pinosta.
     * @return Ylin olio
     */
    public WallRange pop()
    {
        if (top == null)
            return null;
        
        StackNode temp = top;
        top = top.next;
        return temp.wallRange;
    }
    
    /**
     * Onko pino tyhjä
     */
    public boolean empty()
    {
        return top == null;
    }
    
    // Edustaa yhtä alkiota pinossa
    private class StackNode
    {
        private StackNode next;
        private WallRange wallRange;

        public StackNode(WallRange wallRange)
        {
            this.wallRange = wallRange;
        }
    }
}

