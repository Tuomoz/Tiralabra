public class AStarNode
{
    private int x, y;
    private AStarNode cameFrom;

    public AStarNode(int x, int y)
    {
        this.x = x;
        this.y = y;
        cameFrom = null;
    }
    
    public void setCameFrom(AStarNode node)
    {
        cameFrom = node;
    }

    public AStarNode getCameFrom()
    {
        return cameFrom;
    }
    
    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }
}
