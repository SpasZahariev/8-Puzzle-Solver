public class PuzzleBoard {

    //private static int[][] grid;
    /*private static Block blockA;
    private static Block blockB;
    private static Block blockC;
    private static Block agent;*/

    //constructor for default 4x4 A,B,C grid
    public static void main(String args[]){
        //grid = new int[4][4];
        Block blockA = new Block(0,9);
        Block blockB = new Block(1,5);
        Block blockC = new Block(2,1);
        Block agent = new Block(3);
        //BFS nodeBFS = new BFS(agent);
        //nodeBFS.insertBlock(blockA);
        //nodeBFS.insertBlock(blockB);
        //nodeBFS.insertBlock(blockC);
        DFS nodeDFS = new DFS(agent);
        nodeDFS.insertBlock(blockA);
        nodeDFS.insertBlock(blockB);
        nodeDFS.insertBlock(blockC);
        System.out.println("AT START: " + blockA.getCurrPos() + " " + blockB.getCurrPos() + " " + blockC.getCurrPos() + " " + agent.getCurrPos());
        //nodeBFS.doBFS();
        nodeDFS.doDFS();
    }

    /*public Block getBlockA() {
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
    }*/
}
