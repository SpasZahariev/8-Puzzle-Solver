import javax.swing.*;
import java.util.ArrayList;
import java.util.HashSet;

public class PuzzleBoard {
    private static int lengthAcross;
    private Block agent;
    private ArrayList<Block> blocks;
    private HashSet<Integer> obstacles;


    public PuzzleBoard (int length) {
        lengthAcross = length;
        blocks = new ArrayList<>();
        obstacles = new HashSet<>();
    }

    public void setAgent (Block agent) {
        this.agent = agent;
    }

    public void insertBlocks(Block newBlock) {
        blocks.add(newBlock);
    }

    public void insertObstacle (int obst) {
        obstacles.add(obst);
    }

    public int getLengthAcross() {
        return lengthAcross;
    }

    public Block getAgent() {
        return agent;
    }

    public ArrayList<Block> getBlocks() {
        return blocks;
    }

    public HashSet<Integer> getObstacles() {
        return obstacles;
    }

    /*//return a copy of to be able to change the values freely
    public PuzzleBoard getCopy() {
        PuzzleBoard copy = new PuzzleBoard(lengthAcross);
        copy.setAgent(new Block(agent.getCurrPos()));
        blocks.forEach(i -> copy.insertBlocks(new Block(i.getCurrPos(), i.getGoalPos())));
        obstacles.forEach(copy::insertObstacle);
        return copy;
    }*/

    //private static int[][] grid;
    /*private static Block blockA;
    private static Block blockB;
    private static Block blockC;
    private static Block agent;*/

    //constructor for default 4x4 A,B,C grid
    public static void main(String args[]){

        Block blockA = new Block(12,5);
        Block blockB = new Block(13,9);
        Block blockC = new Block(14,13);
        //Block blockD = new Block(3,1);
        Block agent = new Block(15);
        int size = 4;

//        int obst15 = 10;

        PuzzleBoard startBoard = new PuzzleBoard(size);
        startBoard.setAgent(agent);
        startBoard.insertBlocks(blockA);
        startBoard.insertBlocks(blockB);
        startBoard.insertBlocks(blockC);
//        startBoard.addObstacle(obst15);
        //startBoard.insertBlocks(blockD);

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

        SwingUtilities.invokeLater(() -> {
            BoardFrame gui = new BoardFrame();
            gui.setUp();
        });


//        AStar aStarSolution = new AStar(startBoard);

//        BFS bfsSolution = new BFS(startBoard);

//        IDS idsSolution = new IDS(startBoard);

//        DFS dfsSolution = new DFS(startBoard);

        System.out.println("AT START: " + blockA.getCurrPos() + " " + blockB.getCurrPos() + " " + blockC.getCurrPos() + " " + agent.getCurrPos());

//        aStarSolution.startSearch();

//        bfsSolution.startSearch();

//        idsSolution.startSearch();

//        dfsSolution.startSearch();

    }
}
