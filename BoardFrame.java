import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

public class BoardFrame extends JFrame {

    private JPanel gridContainer;
    private PuzzleBoard board;
    private JLabel[] tiles;

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
        setSize(1280, 920);//16:9 resolution
        setLocationRelativeTo(null);
        setResizable(false);

        Color bgColor = new Color(100,130,100);
        setLayout(new BorderLayout());
        gridContainer = new JPanel();
        gridContainer.setBackground(new Color(70,90,70));
        JPanel optionContainer = new JPanel();
        optionContainer.setLayout(new BoxLayout(optionContainer, BoxLayout.Y_AXIS));
        optionContainer.setBackground(bgColor);

        //input board size
        JPanel boardSizePanel = new JPanel();
        boardSizePanel.setBackground(bgColor);
        boardSizePanel.add(new JLabel("N*N Board rows:"));
        JTextField sizeField = new JTextField(3);
        boardSizePanel.add(sizeField);
        Button inputSize = new Button("Set/Reset!");
        inputSize.addActionListener(e -> visualizeBoard(sizeField));
        boardSizePanel.add(inputSize);
        optionContainer.add(boardSizePanel);

        //input buttons for adding agent
        JPanel agentPositionPanel = new JPanel();
        agentPositionPanel.setBackground(bgColor);
        agentPositionPanel.add(new JLabel("Agent Start Position:"));
        agentStartPos = new JTextField(3);
        agentPositionPanel.add(agentStartPos);
        inputAgentPos = new Button("OK");
        inputAgentPos.setEnabled(false);
        inputAgentPos.addActionListener(e -> colorAgentTile());
        agentPositionPanel.add(inputAgentPos);
        optionContainer.add(agentPositionPanel);

        //input buttons for adding blocks and their goal positions
        JPanel blockAddingPanel = new JPanel();
        blockAddingPanel.setBackground(bgColor);
        JPanel miniPanel = new JPanel(new GridLayout(2, 1));
        JPanel resize1 = new JPanel(new BorderLayout());
        JPanel resize2 = new JPanel(new BorderLayout());
        resize1.setBackground(bgColor);
        resize2.setBackground(bgColor);
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

        //input buttons for adding obstacles
        JPanel obstaclePanel = new JPanel();
        obstaclePanel.setBackground(bgColor);
        obstaclePanel.add(new JLabel("Obstacle Position:"));
        JTextField obstaclePos = new JTextField(3);
        obstaclePanel.add(obstaclePos);
        inputObstacle = new Button("Add");
        inputObstacle.setEnabled(false);
        inputObstacle.addActionListener(e -> colorObstacle(obstaclePos));
        obstaclePanel.add(inputObstacle);
        optionContainer.add(obstaclePanel);

        //text fields to display solution depth and nodes expanded
        JPanel depthPanel = new JPanel();
        depthPanel.setBackground(bgColor);
        depthPanel.add(new JLabel("Solution Depth:"));
        solutionDepth = new JTextField(5);
        solutionDepth.setEditable(false);
        depthPanel.add(solutionDepth);
        optionContainer.add(depthPanel);
        JPanel nodesPanel = new JPanel();
        nodesPanel.setBackground(bgColor);
        nodesPanel.add(new JLabel("Nodes Expanded:"));
        nodesPassed = new JTextField(8);
        nodesPassed.setEditable(false);
        nodesPanel.add(nodesPassed);
        optionContainer.add(nodesPanel);

        //solution algorithm selector and start button
        String[] searchTypes = {"Breadth-First", "Depth-First", "Iterative Deepening", "A Star", "Graph Search A*" , "Graph BFS"};
        JPanel solutionPanel = new JPanel();
        solutionPanel.setBackground(bgColor);
        JComboBox<String> typePicker = new JComboBox<>(searchTypes);
        solutionPanel.add(typePicker);
        inputStartSolution = new Button("Find Solution");
        inputStartSolution.addActionListener(e -> findSolution(String.valueOf(typePicker.getSelectedItem())));
        inputStartSolution.setEnabled(false);
        solutionPanel.add(inputStartSolution);
        optionContainer.add(solutionPanel);

        add(gridContainer, BorderLayout.CENTER);
        add(optionContainer, BorderLayout.EAST);
        setVisible(true);
    }

    //when board size is changed
    //removes all JPanels and tiles
    private void clearBoard() {
        gridContainer.removeAll();
        agentAdded = false;
        blockAdded = false;

        agentStartPos.setEnabled(true);
        inputAgentPos.setEnabled(true);
        inputStartSolution.setEnabled(false);
    }

    //done when a board size is specified
    private void unlockButtons() {
        inputAgentPos.setEnabled(true);
        inputBlock.setEnabled(true);
        inputObstacle.setEnabled(true);
    }

    //unlocks only when an agent and a movable block are present
    //unlocks button that starts the solution calculation
    private void unlockSolution() {
        if (agentAdded && blockAdded)
            inputStartSolution.setEnabled(true);
    }

    private void visualizeBoard(JTextField sizeField) {
        try {
            clearBoard();
            unlockButtons();
            int lengthAcross = Integer.parseInt(sizeField.getText());
            if (lengthAcross < 2)
                throw new NumberFormatException();
            gridContainer.setLayout(new GridLayout(lengthAcross, lengthAcross));
            tiles = new JLabel[lengthAcross * lengthAcross];
            //could change font size to adjust better
            Font verdana = new Font("Verdana", 1, 2000 / tiles.length);
            for (int i = 0; i < tiles.length; i++) {
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

    //checks if input is ok and colors agent green
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

    //checks if input is ok and colors the specified tile Black and gives the Goal grey borders
    private void colorBlock(JTextField startText, JTextField goalText) {
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

    //checks if input is ok and colors the specified tile Red
    private void colorObstacle(JTextField positionText) {
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

    //returns false if it detects clashes with other goalStates or obstacles
    //is position suitable for a goal state
    private boolean goalTileFree(int wantedPosition) {
        HashSet<Integer> obstacles = board.getObstacles();
        if (obstacles.contains(wantedPosition))
            return false;

        ArrayList<Block> blocks = board.getBlocks();
        for (Block i : blocks) {
            if (wantedPosition == i.getGoalPos())
                return false;
        }
        return true;
    }

    //starts the selected search algorithm
    private void findSolution(String searchType) {
        SearchAlg algorithm;
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

            case "Graph Search A*":
                algorithm = new GraphAStar(board);
                break;

            case "Graph BFS":
                algorithm = new GraphBFS(board);
                break;

            default:
                System.err.println("Invalid algorithm input. How did you manage to do that?");
                return;

        }
        algorithm.setTextFields(nodesPassed, solutionDepth);
        PathWorker worker = new PathWorker(algorithm);
        worker.execute();
    }

    //separate thread to do the demanding solution calculation instead of the Event Dispatch Thread
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
            System.err.println("\n!Working On Solution!");
            //finds optimal path
            chosenAlg.startSearch();
            //get optimal path to solution
            solutionRoute = chosenAlg.getSolutionRoute();
            //printing steps
            Stack<Integer[]> clone = (Stack<Integer[]>) solutionRoute.clone();
            while(!clone.isEmpty()) {
                Integer[] positions = clone.pop();
                System.out.print("\nAgent: " + positions[0]);
                for (int j = 1; j < positions.length; j++) {
                    System.out.print("   Block(" + j + "): " + positions[j]);
                }
            }
            obstacles = chosenAlg.getObstacles();

            Integer[] startState = solutionRoute.peek();
            //visualise the blocks moving
            int sleepTime = 20000 / solutionRoute.size();
            if (sleepTime > 1000)
                sleepTime = 1000;
            while (!solutionRoute.isEmpty()) {
                process();
                Thread.sleep(sleepTime);
            }
            Thread.sleep(500);
            //resetting board colors
            solutionRoute.push(startState);
            process();
            return null;
        }

        //recolours the tiles to simulate the solution path
        //scheduled publications to the EDT that should not cause errors
        protected void process() {
            Integer[] movableBlocks = solutionRoute.pop();
            for (int i = 0; i < tiles.length; i++) {

                boolean match = false;
                //check if tile is occupied by the moved agent
                if (i == movableBlocks[0]) {
                    tiles[i].setBackground(new Color(0, 100, 0));
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
                if (obstacles.contains(i)) {
                    tiles[i].setBackground(Color.red);
                    continue;
                }
                if (!match) {
                    tiles[i].setBackground(Color.lightGray);
                    tiles[i].setForeground(Color.black);
                }
            }
        }

    }
}
