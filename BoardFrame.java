import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

public class BoardFrame extends JFrame {

    private JPanel gridContainer;
    private PuzzleBoard board;
    private JLabel[] tiles;
    private static String[] searchTypes = {"Breadth-First", "Depth-First", "Iterative Deepening", "A Star"};

    private JTextField agentStartPos;
    private Button inputAgentPos;
    private Button inputBlock;
    private Button inputObstacle;
    private Button inputStartSolution;

    private JTextField nodesPassed;
    private JTextField solutionDepth;

    private boolean agentAdded;
    private boolean blockAdded;

    public BoardFrame() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack(); //makes all contents of frame at their preferred size
        setSize(1280,920);//16:9 resolution
        setLocationRelativeTo(null);
        setResizable(false);

        setLayout(new BorderLayout());
        gridContainer = new JPanel();
        //gridContainer.setBackground(Color.CYAN);
        JPanel optionContainer = new JPanel();
        optionContainer.setLayout(new BoxLayout(optionContainer, BoxLayout.Y_AXIS));

        JPanel boardSizePanel = new JPanel();
        boardSizePanel.add(new JLabel("N*N Board rows:"));
        JTextField sizeField = new JTextField(3);
        boardSizePanel.add(sizeField);
        Button inputSize = new Button("Set!");
        inputSize.addActionListener(e -> visualizeBoard(sizeField));
        boardSizePanel.add(inputSize);
        optionContainer.add(boardSizePanel);

        JPanel agentPositionPanel = new JPanel();
        agentPositionPanel.add(new JLabel("Agent Start Position:"));
        agentStartPos = new JTextField(3);
        agentPositionPanel.add(agentStartPos);
        inputAgentPos = new Button("OK");
        inputAgentPos.setEnabled(false);
        inputAgentPos.addActionListener(e -> colorAgentTile());
        agentPositionPanel.add(inputAgentPos);
        optionContainer.add(agentPositionPanel);

        JPanel blockAddingPanel = new JPanel();
        JPanel miniPanel = new JPanel(new GridLayout(2,1));
        JPanel resize1 = new JPanel(new BorderLayout());
        JPanel resize2 = new JPanel(new BorderLayout());
        resize1.add(new JLabel("Block Start Position: "), BorderLayout.CENTER);
        JTextField blockStartPos = new JTextField(3);
        resize1.add(blockStartPos, BorderLayout.EAST);
        resize2.add(new JLabel("Block Goal Position: "), BorderLayout.CENTER);
        JTextField blockGoalPos = new JTextField(3);
        resize2.add(blockGoalPos, BorderLayout.EAST);
        miniPanel.add(resize1);
        miniPanel.add(resize2);
        blockAddingPanel.add(miniPanel);
        inputBlock = new Button("Add");
        inputBlock.setEnabled(false);
        inputBlock.addActionListener(e -> colorBlock(blockStartPos, blockGoalPos));
        blockAddingPanel.add(inputBlock);
        optionContainer.add(blockAddingPanel);


        JPanel obstaclePanel = new JPanel();
        obstaclePanel.add(new JLabel("Obstacle Position:"));
        JTextField obstaclePos = new JTextField(3);
        obstaclePanel.add(obstaclePos);
        inputObstacle = new Button ("Add");
        inputObstacle.setEnabled(false);
        inputObstacle.addActionListener(e -> colorObstacle(obstaclePos));
        obstaclePanel.add(inputObstacle);
        optionContainer.add(obstaclePanel);


        JPanel depthPanel = new JPanel();
        depthPanel.add(new JLabel("Solution Depth:"));
        solutionDepth = new JTextField("No Answer");
        solutionDepth.setEditable(false);
        depthPanel.add(solutionDepth);
        optionContainer.add(depthPanel);
        JPanel nodesPanel = new JPanel();
        nodesPanel.add(new JLabel("Nodes Expanded:"));
        nodesPassed = new JTextField("No Answer");
        nodesPassed.setEditable(false);
        nodesPanel.add(nodesPassed);
        optionContainer.add(nodesPanel);

        JPanel solutionPanel = new JPanel();
        JComboBox<String> typePicker = new JComboBox<>(searchTypes);
        solutionPanel.add(typePicker);
        inputStartSolution = new Button("Find Solution");
        inputStartSolution.addActionListener(e -> findSolution(String.valueOf(typePicker.getSelectedItem())));
        inputStartSolution.setEnabled(false);
        solutionPanel.add(inputStartSolution);
        optionContainer.add(solutionPanel);

        add(gridContainer,BorderLayout.CENTER);
        add(optionContainer,BorderLayout.EAST);
        setVisible(true);
    }

    public void setUp() {

    }

    private void clearBoard(){
        gridContainer.removeAll();
        agentAdded = false;
        blockAdded = false;

        agentStartPos.setEnabled(true);
        inputAgentPos.setEnabled(true);
        inputStartSolution.setEnabled(false);
    }

    //done when a board size is specified
    private void unlockButtons(){
        inputAgentPos.setEnabled(true);
        inputBlock.setEnabled(true);
        inputObstacle.setEnabled(true);
    }

    //unlocks only when an agent and a movable block are present
    private void unlockSolution(){
        if (agentAdded && blockAdded)
            inputStartSolution.setEnabled(true);
    }
    private void visualizeBoard(JTextField sizeField){
        try {
            clearBoard();
            unlockButtons();
            int lengthAcross = Integer.parseInt(sizeField.getText());
            if (lengthAcross < 2)
                throw new NumberFormatException();
            gridContainer.setLayout(new GridLayout(lengthAcross,lengthAcross));
            tiles = new JLabel[lengthAcross * lengthAcross];
            //could change font size to adjust better
            Font verdana = new Font ("Verdana",1,2000 / tiles.length);
            for (int i = 0; i < tiles.length;i ++) {
                tiles[i] = new JLabel(String.valueOf(i), SwingConstants.CENTER);
                tiles[i].setOpaque(true);
                //inversely proportional font size
                tiles[i].setFont(verdana);
                gridContainer.add(tiles[i]);
            }
            gridContainer.revalidate();
            gridContainer.repaint();
            board = new PuzzleBoard(lengthAcross);
        } catch (NumberFormatException e1) {
            System.err.println("input for size is not valid. Try again.");
        }
    }

    private void colorAgentTile() {
        try {
            int agentPos = Integer.parseInt(agentStartPos.getText());
            if (agentPos < 0 || agentPos >= tiles.length)
                throw new NumberFormatException();

            if (tileFree(agentPos)) {
                board.setAgent(new Block(agentPos));
                tiles[agentPos].setBackground(new Color(0, 100, 0));
                agentStartPos.setEnabled(false);
                inputAgentPos.setEnabled(false);
                agentAdded = true;
                unlockSolution();
            } else {
                System.err.println("Position is taken. Try again.");
            }

        } catch (NumberFormatException e2) {
            System.err.println("Agent Position not valid. Try again.");
        }
    }

    private void colorBlock(JTextField startText, JTextField goalText){
        try {
            int startPos = Integer.parseInt(startText.getText());
            int goalPos = Integer.parseInt(goalText.getText());
            if ((goalPos < 0 || goalPos >= tiles.length) || (startPos < 0 || startPos >= tiles.length))
                throw new NumberFormatException();

            if (tileFree(startPos) && goalTileFree(goalPos)) {
                board.insertBlocks(new Block(startPos, goalPos));
                tiles[startPos].setBackground(Color.black);
                tiles[startPos].setForeground(Color.lightGray);
                tiles[goalPos].setBorder(BorderFactory.createLineBorder(Color.GRAY, 3));
                blockAdded = true;
                unlockSolution();
            } else {
                System.err.println("a position is taken. Try again.");
            }

        } catch (NumberFormatException e2) {
            System.err.println("Block Position invalid syntax. Try again.");
        }
    }

    private void colorObstacle (JTextField positionText) {
        try {
            int pos = Integer.parseInt(positionText.getText());
            if (pos < 0 || pos >= tiles.length)
                throw new NumberFormatException();

            if (tileFree(pos) && goalTileFree(pos)) {
                board.insertObstacle(pos);
                tiles[pos].setBackground(Color.RED);
            } else {
                System.err.println("Position is taken. Try again.");
            }
        } catch (NumberFormatException e) {
            System.err.println("Obstacle position not valid. Try again.");
        }
    }

    //is the tile suitable for a block or the agent
    private boolean tileFree(int wantedPosition) {
        if (agentAdded) {
            int agentPos = board.getAgent().getCurrPos();
            if (wantedPosition == agentPos)
                return false;
        }

            HashSet<Integer> obstacles = board.getObstacles();
        //todo do I need to check for when obstacles.isEmpty
            if (obstacles.contains(wantedPosition))
                return false;

            ArrayList<Block> blocks = board.getBlocks();
            for (Block i : blocks) {
                if (wantedPosition == i.getCurrPos() /*|| wantedPosition == i.getGoalPos()*/)
                    return false;
            }

        return true;
    }

    //returns false if clashes with other goalStates or obstacles
    //is position suitable for a goal state
    private boolean goalTileFree(int wantedPosition) {
        HashSet<Integer> obstacles = board.getObstacles();
        if (obstacles.contains(wantedPosition))
            return false;

        ArrayList<Block> blocks = board.getBlocks();
        for (Block i : blocks) {
            if(wantedPosition == i.getGoalPos())
                return false;
        }
        return true;
    }

    //todo comiting solution steps, nodesExpanded and solution depth
    //starts the search algorithm
    private void findSolution (String searchType) {
        SearchAlg algorithm = null;
        switch (searchType) {
            case "Breadth-First":
                algorithm = new BFS(board);
                break;

            case "Depth-First":
                algorithm = new DFS(board);
                break;

            case "Iterative Deepening":
                algorithm = new IDS(board);
                break;

            case "A Star":
                algorithm = new AStar(board);
                break;

        }
        algorithm.setTextFields(nodesPassed, solutionDepth);
        PathWorker worker = new PathWorker(algorithm);
        System.err.println("STARTEDDD");
        worker.execute();
    }

    private class PathWorker extends SwingWorker<Void, Void> {

        private SearchAlg chosenAlg;
        private Stack<Integer[]> solutionRoute;
        private HashSet<Integer> obstacles;


        public PathWorker(SearchAlg finalAlgorithm) {
            super();
            chosenAlg = finalAlgorithm;
        }

        //does the big calculation in another thread... not in the EDT!
        @Override
        protected Void doInBackground() throws Exception {
            //finds optimal path
            chosenAlg.startSearch();
            //get optimal path to solution
            solutionRoute = chosenAlg.getSolutionRoute();
            obstacles = chosenAlg.getObstacles();

            Integer[] startState = solutionRoute.peek();
            //visualise the blocks moving
            int sleepTime = 20000 / solutionRoute.size();
            if (sleepTime > 1000)
                sleepTime = 1000;
            while(!solutionRoute.isEmpty()) {
                process();
                Thread.sleep(sleepTime);
            }
            Thread.sleep(500);
            //resetting board colors
            solutionRoute.push(startState);
            process();
            return null;
        }

        //recolours the tiles to simulate the solution
        //scheduled publications to the EDT that should not cause errors
//        @Override
        protected void process() {
            Integer[] movableBlocks = solutionRoute.pop();
            for(int i = 0; i < tiles.length; i++) {

                boolean match = false;
                //check if tile is occupied by the moved agent
                if(i == movableBlocks[0]) {
                    tiles[i].setBackground(new Color(0,100,0));
                    continue;
                }
                //check if tile is occupied by a block
                for (int j = 1; j < movableBlocks.length; j++) {
                    if (i == movableBlocks[j]) {
                        tiles[i].setBackground(Color.black);
                        tiles[i].setForeground(Color.gray);
                        match = true;
                        break;
                    }
                }
                //check if tile is occupied by an obstacle
                if(obstacles.contains(i)) {
                    tiles[i].setBackground(Color.red);
                    continue;
                }
                if(!match) {
                    tiles[i].setBackground(Color.lightGray);
                    tiles[i].setForeground(Color.black);
                }
            }
        }

    }
}
