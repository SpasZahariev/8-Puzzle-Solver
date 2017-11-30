import javax.swing.*;
import java.util.ArrayList;
import java.util.HashSet;
/**
GUI --help
 Board Size: will take an input n and make an n*n board. It can be re-sized any number of times
 Set agent position: will take input which is a position from the board and make it the agent start position
 Set blocks: takes 2 inputs; start position and goal position. Any number of blocks can be added
 Set obstacles: (optional) adds obstacles for the agent that can not be passed through. Any number of obstacles can be added
 Find solution: choose which algorithm you want to use to solve the problem.
                Unlocks only when there is an agent on the board and at least one movable block

 Green block => the agent
 Black block => A,B,C... blocks that need to be moved to a goal position
 Grey boarders => target position for a specific black block
 Red blocks => obstacles that can not be moved
*/
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
