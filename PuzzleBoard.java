public class PuzzleBoard {

    //private static int[][] grid;
    private static Block blockA;
    private static Block blockB;
    private static Block blockC;
    private static Block agent;

    //constructor for default 4x4 A,B,C grid
    public PuzzleBoard(){
        //grid = new int[4][4];
        blockA = new Block(0,9);
        blockB = new Block(1,5);
        blockC = new Block(2,1);
        agent = new Block(3);
    }

    public Block getBlockA() {
        return blockA;
    }

    public Block getBlockB() {
        return blockB;
    }

    public Block getBlockC() {
        return blockC;
    }

    public Block getAgent() {
        return agent;
    }
}
