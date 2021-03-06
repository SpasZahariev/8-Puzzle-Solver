import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class AStar extends SearchAlg implements Searchable<AStar.Node> {

    private Node root;
    private static int lengthAcross;
    private static int area;
    private static PriorityQueue<Node> openList;
    private static boolean isSolution;

    protected class Node extends BasicNode {
        private int moveCost;
        private int manhattan;

        public Node() {
            towerBlocks = new ArrayList<>();
        }

        private int getWeight() {
            return moveCost + manhattan;
        }
    }


    //class to help with prioritising promising nodes
    private class WeightComparator implements Comparator<Node> {
        @Override
        public int compare(Node x, Node y) {
            return Integer.compare(x.getWeight(), y.getWeight());
        }
    }

    public AStar(PuzzleBoard newBoard) {
        super(newBoard.getObstacles());
        root = new Node();
        root.agent = newBoard.getAgent();
        root.towerBlocks = newBoard.getBlocks();
        lengthAcross = newBoard.getLengthAcross();
        area = lengthAcross * lengthAcross;
        openList = new PriorityQueue<>(new WeightComparator());
        nodesPassed = 0;
        isSolution = false;
    }

    //adds nodes to pen list and expands to most promising one of them
    //when solution found calls printing function
    public void startSearch() {
        root.moveCost = 0;
        root.manhattan = heuristicSum(root.towerBlocks);
        openList.add(root);

        while (!openList.isEmpty() && !isSolution) {
            doAStar(openList.poll());
        }
        //when solution is found get the final node and trace it back to the start
        BasicNode goal = null;
        for (Node i : openList) {
            if (i.manhattan == 0) {
                goal = i;
                break;
            }
        }
        traceRouteFrom(goal);
    }

    //checks possible Adjacent movements from current position of agent
    private void doAStar(Node current) {
        nodesPassed++;
        int agentPos = current.agent.getCurrPos();

        //checks for when agent is next to borders
        //prevents moving out of the bounds of the board or into obstacles
        //up
        if (agentPos > (lengthAcross - 1) && noObstacle(agentPos - lengthAcross)) {
            makeSwitches(new Node(), current, agentPos - lengthAcross, agentPos);
        }
        //left
        if (agentPos % lengthAcross != 0 && noObstacle(agentPos - 1)) {
            makeSwitches(new Node(), current, agentPos - 1, agentPos);
        }
        //down
        if (agentPos < (area - lengthAcross) && noObstacle(agentPos + lengthAcross)) {
            makeSwitches(new Node(), current, agentPos + lengthAcross, agentPos);
        }
        //right
        if (agentPos % lengthAcross != (lengthAcross - 1) && noObstacle(agentPos + 1)) {
            makeSwitches(new Node(), current, agentPos + 1, agentPos);
        }
    }

    //manhattan distance for all movable Blocks
    private int heuristicSum(ArrayList<Block> blocks) {
        int sum = 0;
        for (Block i : blocks)
            sum += calcManhattan(i.getCurrPos(), i.getGoalPos());
        return sum;
    }

    //returns manhattan distance of one block
    private int calcManhattan(int current, int goal) {
        if (current == goal) {
            return 0;
        } else {
            int steps = 0;
            //row alignment
            while (current / lengthAcross != goal / lengthAcross) {
                if (current < goal) {
                    current += lengthAcross;
                } else {
                    current -= lengthAcross;
                }
                steps++;
            }
            //column align and return
            return steps + Math.abs(current - goal);
        }
    }

    //of manhattan distance for all movable blocks is 0 then they are at their goal positions
    public boolean checkSolution(Node current) {
        return current.manhattan == 0;
    }

    //handles scenario when agent swaps with a block
    //only recalculates Manhattan distance when a block has been moved
    public void makeSwitches(Node child, Node parent, int futureAgentPos, int currentAgentPos) {
        if (isSolution)
            return;
        boolean recalculate = false;
        child.agent = new Block(futureAgentPos);
        child.parent = parent;
        child.moveCost = parent.moveCost + 1;

        for (Block i : parent.towerBlocks) {
            if (i.getCurrPos() == futureAgentPos) {
                recalculate = true;
                child.towerBlocks.add(new Block(currentAgentPos, i.getGoalPos()));
            } else {
                child.towerBlocks.add(new Block(i.getCurrPos(), i.getGoalPos()));
            }
        }
        if (recalculate) {
            child.manhattan = heuristicSum(child.towerBlocks);
        } else {
            child.manhattan = parent.manhattan;
        }
        if (checkSolution(child)) {
            isSolution = true;
            openList.add(child);
            return;
        }
            openList.add(child);
    }
}
