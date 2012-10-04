package dungeonGenerators;


import dungeonGenerators.WallRange;

public class WallStack
{
    private StackNode top = null;
    
    public void add(WallRange wallRange)
    {
        StackNode newNode = new StackNode(wallRange);
        newNode.next = top;
        top = newNode;
    }
    
    public WallRange pop()
    {
        if (top == null)
            return null;
        
        StackNode temp = top;
        top = top.next;
        return temp.wallRange;
    }
    
    public boolean empty()
    {
        return top == null;
    }
    
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

