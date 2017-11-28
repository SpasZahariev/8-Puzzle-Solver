import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class GraphAStar extends SearchAlg implements Searchable<GraphAStar.Node> {

    private Node root;
    private static int lengthAcross;
    private static int area;
    private static PriorityQueue<Node> openList;
    private static ArrayList<Node> closedList;
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

    private class WeightComparator implements Comparator<Node> {
        @Override
        public int compare(Node x, Node y) {
            if (x.getWeight() < y.getWeight()) {
                return -1;
            }
            if (x.getWeight() > y.getWeight()) {
                return 1;
            }
            return 0;
        }
    }

    public GraphAStar(PuzzleBoard newBoard) {
        super(newBoard.getObstacles());
        root = new Node();
        root.agent = newBoard.getAgent();
        root.towerBlocks = newBoard.getBlocks();
        lengthAcross = newBoard.getLengthAcross();
        area = lengthAcross * lengthAcross;
        openList = new PriorityQueue<>(new WeightComparator());
        closedList = new ArrayList<>();
        nodesPassed = 0;
        isSolution = false;
    }

    //expands the root node's most promising child. Expands most promising nodes until solution is found
    //calls method to trace path from solution to start
    public void startSearch() {
        root.moveCost = 0;
        root.manhattan = heuristicSum(root.towerBlocks);
        openList.add(root);

        while (!openList.isEmpty() && !isSolution) {
            doAStar(openList.poll());
        }
        BasicNode goal = null;
        for (Node i : openList) {
            if (i.manhattan == 0) {
                goal = i;
                break;
            }
        }
        traceRouteFrom(goal);
    }

    //expands a node and adds it children to the open List
    //node is added to closed list (previously visited nodes)
    private void doAStar(Node current) {
        nodesPassed++;

        int agentPos = current.agent.getCurrPos();

        //checks for when agent is next to borders or obstacles
        //prevents moving out of the bounds of the board
        if (agentPos % lengthAcross != 0 && noObstacle(agentPos - 1)) {
            makeSwitches(new Node(), current, agentPos - 1, agentPos);
        }
        if (agentPos % lengthAcross != (lengthAcross - 1) && noObstacle(agentPos + 1)) {
            makeSwitches(new Node(), current, agentPos + 1, agentPos);
        }
        if (agentPos < (area - lengthAcross) && noObstacle(agentPos + lengthAcross)) {
            makeSwitches(new Node(), current, agentPos + lengthAcross, agentPos);
        }
        if (agentPos > (lengthAcross - 1) && noObstacle(agentPos - lengthAcross)) {
            makeSwitches(new Node(), current, agentPos - lengthAcross, agentPos);
        }
        closedList.add(current);
    }

    //manhattan distance SUM for all movable blocks
    private int heuristicSum(ArrayList<Block> blocks) {
        int sum = 0;
        for (Block i : blocks)
            sum += calcManhattan(i.getCurrPos(), i.getGoalPos());
        return sum;
    }

    //manhattan distance to goal for a single movable block
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

    //if the Sum of manhattan distances is 0 we are at the goal state
    public boolean checkSolution(Node current) {
        return current.manhattan == 0;
    }

    //configures values for child nodes.
    //Recalculates manhattan distances only when a block has been moved
    //decides if a node should be added to open list (for future expanding)
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

        //todo check if runs properly
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

        //if we have already seen a node with the same configuration of blocks but with a smaller weight -> skip this one
        if (isBestValue(child, openList) && isBestValue(child, closedList))
            openList.add(child);
    }

    //if the same node exists in the open or closed list WITH a better weight, skip this one
    //we can search the same point a few times but only if it is more promising than the last time
    //check if node position is already in openList or closedList with a smaller weight
    private boolean isBestValue(Node newNode, Iterable<Node> list) {
        for (Node i : list) {
            if (sameBlockPositions(newNode, i) && newNode.getWeight() > i.getWeight())
                return false;
        }
        return true;
    }

    //check if agent and blocks are in exact same positions in both nodes
    private boolean sameBlockPositions(Node nodeA, Node nodeB) {
        if (nodeA.agent.getCurrPos() == nodeB.agent.getCurrPos()) {
            for (Block i : nodeA.towerBlocks) {
                boolean hasBlock = false;
                for (Block j : nodeB.towerBlocks) {
                    if (j.getCurrPos() == i.getCurrPos()) {
                        hasBlock = true;
                        break;
                    }
                }
                if (!hasBlock)
                    return false;
            }
            return true;
        }
        return false;
    }
}
