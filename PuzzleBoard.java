import java.util.ArrayList;

public class PuzzleBoard {
    private static int sideSize;
    private static int boardSize;
    private Block agent;
    private ArrayList<Block> blocks;


    private PuzzleBoard (int width, Block agent) {
        sideSize = width;
        boardSize = sideSize * sideSize;
        this.agent = agent;
        blocks = new ArrayList<>();
    }

    private void insertBlocks(Block newBlock) {
        blocks.add(newBlock);
    }
    //private static int[][] grid;
    /*private static Block blockA;
    private static Block blockB;
    private static Block blockC;
    private static Block agent;*/

    //constructor for default 4x4 A,B,C grid
    public static void main(String args[]){

        Block blockA = new Block(0,9);
        Block blockB = new Block(1,5);
        Block blockC = new Block(2,1);
        Block agent = new Block(3);

        PuzzleBoard startBoard = new PuzzleBoard(4, agent);
        startBoard.insertBlocks(blockA);
        startBoard.insertBlocks(blockB);
        startBoard.insertBlocks(blockC);

        /*OptimizedBFS optBFS = new OptimizedBFS(agent);
        optBFS.insertBlock(blockA);
        optBFS.insertBlock(blockB);
        optBFS.insertBlock(blockC);*/

        /*BFS nodeBFS = new BFS(agent);
        nodeBFS.insertBlock(blockA);
        nodeBFS.insertBlock(blockB);
        nodeBFS.insertBlock(blockC);*/

        /*DFS nodeDFS = new DFS(agent);
        nodeDFS.insertBlock(blockA);
        nodeDFS.insertBlock(blockB);
        nodeDFS.insertBlock(blockC);*/

        /*IDS nodeIDS = new IDS(agent);
        nodeIDS.insertBlock(blockA);
        nodeIDS.insertBlock(blockB);
        nodeIDS.insertBlock(blockC);*/

        AStar nodeAStar = new AStar(agent);
        nodeAStar.insertBlock(blockA);
        nodeAStar.insertBlock(blockB);
        nodeAStar.insertBlock(blockC);

        System.out.println("AT START: " + blockA.getCurrPos() + " " + blockB.getCurrPos() + " " + blockC.getCurrPos() + " " + agent.getCurrPos());

//        optBFS.doBFS();

//        nodeBFS.doBFS();

//        nodeDFS.doDFS();

//        nodeIDS.doIDS();

        nodeAStar.doAStar();

    }
}
