import javax.swing.*;
import java.util.ArrayList;
import java.util.HashSet;

public class PuzzleBoard {
    private static int lengthAcross;
    private Block agent;
    private ArrayList<Block> blocks;
    private HashSet<Integer> obstacles;


    public PuzzleBoard(int length) {
        lengthAcross = length;
        blocks = new ArrayList<>();
        obstacles = new HashSet<>();
    }

    public void setAgent(Block agent) {
        this.agent = agent;
    }

    public void insertBlocks(Block newBlock) {
        blocks.add(newBlock);
    }

    public void insertObstacle(int obst) {
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

    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> {
            BoardFrame gui = new BoardFrame();
        });
    }
}
