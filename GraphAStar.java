import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;

public class GraphAStar {

    private Node root;
    private static int lengthAcross;
    private static int area;
    private static PriorityQueue<Node> openList;
    private static ArrayList<Node> closedList;
    private static long nodesPassed;

    private static boolean isSolution;

    private class Node {

        private Block agent;
        private Node parent;
        private int moveCost;
        private int manhattan;
        //private int weight;
        private ArrayList<Block> towerBlocks;
        //private ArrayList<Node> children;

        public Node(Block agent) {
            this.agent = agent;
            towerBlocks = new ArrayList<>();
            //children = new ArrayList<>(2);
        }

        /*private Node(Node parent) {
            this.parent = parent;
            towerBlocks = new ArrayList<>(3);
            //children = new ArrayList<>(4);
        }*/

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
        root = new Node(newBoard.getAgent());
        root.towerBlocks = newBoard.getBlocks();
        lengthAcross = newBoard.getLengthAcross();
        area = lengthAcross * lengthAcross;

        openList = new PriorityQueue<>(new WeightComparator());
        closedList = new ArrayList<>();
        
        nodesPassed = 0;
        isSolution = false;
    }

    /*public AStar(Block rootAgent) {
        root = new Node(rootAgent);
        openList = new PriorityQueue<>(new WeightComparator());
        closedList = new ArrayList<>();
        nodesPassed = 0;
        isSolution = false;
    }*/

    public void doAStar() {
        root.moveCost = 0;
        root.manhattan = heuristicSum(root.towerBlocks);
        openList.add(root);

        while (!openList.isEmpty() && !isSolution) {
            doAStar(openList.poll());
        }
        Node goal = null;
        for (Node i : openList) {
            if (i.manhattan == 0) {
                goal = i;
                break;
            }
        }
        //printing solution
        System.out.println("nodes Passed: " + nodesPassed);
        printAnswer(goal);
        while (goal.parent != null) {
            printAnswer(goal.parent);
            goal = goal.parent;
        }
    }

    private void doAStar(Node current) {
        nodesPassed++;

        int agentPos = current.agent.getCurrPos();

        //checks for when agent is next to borders
        //prevents moving out of the bounds of the board
        if (agentPos % lengthAcross != 0) {
            makeSwitches(current, agentPos - 1, agentPos);
        }
        if (agentPos % lengthAcross != (lengthAcross - 1)) {
            makeSwitches(current, agentPos + 1, agentPos);
        }
        if (agentPos < (area - lengthAcross)) {
            makeSwitches(current, agentPos + lengthAcross, agentPos);
        }
        if (agentPos > (lengthAcross - 1)) {
            makeSwitches(current, agentPos - lengthAcross, agentPos);
        }
        closedList.add(current);
    }

    //manhattan distance for all blocks
    private int heuristicSum(ArrayList<Block> blocks) {
        int sum = 0;
        for (Block i : blocks)
            sum += calcManhattan(i.getCurrPos(), i.getGoalPos());
        return sum;
    }

    //todo wtf is going on in here
    //TODO say we can use a BETTER heuristic
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
    //TODO CODE BELOW THIS "todo" is copied...Think about it

    //to add BlockA, BlockB, BlockC (can add blocks at same position but irrelevant right now)
    /*public void insertBlock(Block newBlock) {
        root.towerBlocks.add(newBlock);
    }*/

    /*private boolean checkSolution(Node current) {
        for (Block i : current.towerBlocks) {
            if (i.getCurrPos() != i.getGoalPos()) {
                return false;
            }
        }
        return true;
    }*/

    private void makeSwitches(Node parent, int futureAgentPos, int currentAgentPos) {
        if (isSolution)
            return;
        boolean recalculate = false;
        Node child = new Node(new Block(futureAgentPos));
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
        if(recalculate) {
            //child.manhattan = parent.manhattan + 1;
            child.manhattan = heuristicSum(child.towerBlocks);
        } else {
            child.manhattan = parent.manhattan;
        }
        //System.out.println("child manhattan " + child.manhattan);
        if(child.manhattan == 0) {
            //child.towerBlocks.forEach(i -> System.err.println("ha? " + i.getCurrPos()));
            //System.out.println("Manhat dist: " + heuristicSum(child.towerBlocks));
            isSolution = true;
            openList.add(child);
            return;
        }

        //parent.children.add(child);
        if (isBestValue(child,openList) && isBestValue(child,closedList))
            openList.add(child);
    }

    //if the same node exists in the open or closed list WITH a better weight, skip this one
    //we can search the same point a few times but only if it is more promising than the last time
    //check if node position is already in openList or closedList with a smaller weight
    private boolean isBestValue (Node newNode, Iterable<Node> list) {
        for (Node i : list) {
            if (sameBlockPositions(newNode, i) && newNode.getWeight() > i.getWeight())
                return false;
        }
        return true;
    }

    //check if agents and blocks are in same places in both nodes
    private boolean sameBlockPositions (Node nodeA, Node nodeB) {
        if (nodeA.agent.getCurrPos() == nodeB.agent.getCurrPos()) {
            for (Block i : nodeA.towerBlocks) {
                boolean hasBlock = false;
                for (Block j : nodeB.towerBlocks) {
                    if (j.getCurrPos() == i.getCurrPos()) {
                        hasBlock = true;
                        break;
                    }
                }
                if(!hasBlock)
                    return false;
            }
            return true;
        }
        return false;
    }

    private void printAnswer (Node state) {
        System.out.println("------------------------------");
        for (Block i : state.towerBlocks){
            System.out.println("Block Position: " + i.getCurrPos());
        }
        System.out.print("  AGENT: " + state.agent.getCurrPos() + "\n");
        System.out.println("Heuristic: " + state.manhattan);
        //state.towerBlocks.forEach(i -> System.out.println("Block Position: " + i.getCurrPos() + " Goal Pos??? " + i.getGoalPos()));
        System.out.println("------------------------------");
    }
}