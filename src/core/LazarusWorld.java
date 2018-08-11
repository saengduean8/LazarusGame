package core;

import commons.AudioPlayer;
import commons.Globals;
import commons.MapReader;
import commons.SpawnBoxes;
import component.*;

import javax.swing.JComponent;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.List;

public class LazarusWorld extends JComponent implements Runnable {

    private Thread thread;

    public static boolean moveLeft,moveRight,movingLeft,movingRight;

    private KeysControl keysControl;

    private Lazarus lazarus;

    private List<Box> boxes;

    private SpawnBoxes spawnBoxes;

    public  static int startX,startY;

    public static  int width = 50, endLeft,endRight;

    private CollisionDetector collision;

    private String[][] map;

    private AudioPlayer playMusic;

    private Animation currentAnimation;

    private Globals.GameState gameState;

    private int level;

    public LazarusWorld() throws Exception {
        this.level = Globals.INITIAL_LEVEL;     // Start from level 1
        this.map = MapReader.readMap(level);
        this.gameState = Globals.GameState.READY;
        this.boxes = new ArrayList<Box>();
        this.collision = new CollisionDetector(map);

        setFocusable(true);

        // Set lazarus position
        findStartPosition();
        this.lazarus = new Lazarus(startX, startY, Globals.LIVES, this);

        // Initialize key control to track and act of user keyboard inputs
        this.keysControl = new KeysControl(this.lazarus, this);
        addKeyListener(keysControl);

        this.spawnBoxes = new SpawnBoxes(boxes, lazarus);

        // Set music control
        playMusic = new AudioPlayer(this, "resources/backgroundTune.wav");
        playMusic.play();
        playMusic.loop();
    }

    /**
     * Resets/Restarts game to given level
     */
    public void gameReset(int nextLevel) throws Exception {
        this.level = nextLevel;
        this.map = MapReader.readMap(level);
        this.gameState = Globals.GameState.READY;
        this.collision = new CollisionDetector(map);

        clearBoxes();

        findStartPosition();
        this.lazarus.resetPosition(startX, startY);
    }

    public void moveToNextLevel() throws Exception {
        int nextLevel = level + 1;
        if(nextLevel > Globals.FINAL_LEVEL) {
            setGameState(Globals.GameState.GAME_WON);
            return;
        }

        // Load next level
        gameReset(nextLevel);
    }

    public void clearBoxes() {
        synchronized (boxes) {
            this.boxes.clear();
        }
    }

    public void setGameState(Globals.GameState state) {
        this.gameState = state;
    }

    public Globals.GameState getGameState() {
        return gameState;
    }

    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        if (gameState == Globals.GameState.READY) {
            // Render ready/reset background
            renderReadyScreen(g2);
            return;
        }

        if (gameState == Globals.GameState.GAME_OVER) {
            // Render game over background
            renderGameOverScreen(g2);
            playMusic.stop();
            return;
        }

        if (gameState == Globals.GameState.GAME_WON) {
            // Render game won background
            renderGameWonScreen(g2);
            playMusic.stop();
            return;
        }

        renderBackground(g2);
        renderMap(g2);

        // For Lazarus
        drawLazarus(g2, lazarus.x, lazarus.y);

        renderLevel(g2);
        renderLives(g2);

        // For boxes
        synchronized (boxes) {
            renderBoxes(g2);
        }
        // Render next box
        renderNextBox(g2);
    }

    private void runGame() throws Exception{

        if (gameState != Globals.GameState.RUNNING) {
            return;
        }

        synchronized (boxes) {
            moveBoxes();
        }

        // Read key press from user
        handleMovement();

        if (collision.validateLazarustoBoxesCollision(lazarus.x, lazarus.y)) {
            lazarusDie();
            return;
        }

        if (collision.validateLazarusToStopCollision(lazarus.x, lazarus.y)) {
            if(level == Globals.FINAL_LEVEL) {
                setGameState(Globals.GameState.GAME_WON);
            } else {
                setGameState(Globals.GameState.READY);
                moveToNextLevel();
            }
        }
    }

    private void renderLives(Graphics2D g2) {
        Image image = Toolkit.getDefaultToolkit().getImage("resources/lazarus/Lazarus_stand.png");
        for(int i = 0; i <= lazarus.lives; i++) {
            g2.drawImage(image, 25 + (i*30), 25, 40, 40, this);
            g2.finalize();
        }
    }

    private void renderGameWonScreen(Graphics2D g2) {
        Image image = Toolkit.getDefaultToolkit().getImage("resources/gameWon.png");
        g2.drawImage(image, 0, 0, Globals.BOARD_SIZE, Globals.BOARD_SIZE, this);
        g2.finalize();
    }

    private void renderGameOverScreen(Graphics2D g2) {
        Image image = Toolkit.getDefaultToolkit().getImage("resources/gameOver.png");
        g2.drawImage(image, 0, 0, Globals.BOARD_SIZE, Globals.BOARD_SIZE, this);
        g2.finalize();
    }

    private void renderReadyScreen(Graphics2D g2) {
        String fileName = ((level == Globals.INITIAL_LEVEL) ? "gameReady.png" : "gameReset.png");
        Image image = Toolkit.getDefaultToolkit().getImage("resources/" + fileName);
        g2.drawImage(image, 0, 0, Globals.BOARD_SIZE, Globals.BOARD_SIZE, this);
        g2.finalize();
    }

    private void renderNextBox(Graphics2D g2) {
        renderBox(g2, spawnBoxes.getNextBoxType(), 0, 700);
    }

    public void handleMovement(){

        int newX;
        // check if there is any boxes underneath the lazarus if not it will keep falling
        while (!collision.validateLazarusCollision(lazarus.x, lazarus.y + Globals.BLOCK_SIZE)) {
            lazarus.y++;
        }

            if (moveRight) {

                if (movingRight) {

                    newX = lazarus.x + Globals.BLOCK_SIZE;

                    // lazarus can't move up if there is more than one box
                    if (collision.validateLazarusCollision(newX, lazarus.y - Globals.BLOCK_SIZE)) {
                        movingRight = false;

                        return;
                    }

                    // if there is collision then lazarus moves up one box at a time
                    if (collision.validateLazarusCollision(newX, lazarus.y)) {
                        //jump right
                        currentAnimation = new animateJumpRight(lazarus.x,lazarus.y); // create jump right animation
                            /*update position after the animation has finished.
                            lazarus.y = lazarus.y - Globals.BLOCK_SIZE;
                            lazarus.x = newX;*/


                    } else {
                        currentAnimation = new animateRight(lazarus.x,lazarus.y); // create move right animation
                        // Update position after the animation has finished.  lazarus.x = newX;
                        // move right


                    }

                    if (lazarus.x == endRight) {
                        movingRight = false;

                        return;
                    }
                }
            }

                if (moveLeft) {

                    if (movingLeft) {

                        newX = lazarus.x - Globals.BLOCK_SIZE;

                        if (collision.validateLazarusCollision(newX, lazarus.y - Globals.BLOCK_SIZE)) {
                            movingLeft = false;

                            return;
                        }

                        if (collision.validateLazarusCollision(newX, lazarus.y)) {
                            // jump left
                            currentAnimation = new animateJumpLeft(lazarus.x,lazarus.y);//create jump left animation
                            /*update position after the animation has finished.
                            lazarus.y = lazarus.y - Globals.BLOCK_SIZE;
                            lazarus.x = newX;*/


                        } else {
                            currentAnimation = new animateLeft(lazarus.x,lazarus.y); // create move left animation
                            // Update position after the animation has finished.lazarus.x = newX--;

                        }

                        if (lazarus.x == endLeft) {
                            movingLeft = false;

                            return;
                        }
                    }
                }
            }

    private void lazarusDie() {
        // animate death
        currentAnimation = new animateSquished(lazarus.x,lazarus.y);

        // empty existing moving boxes
        clearBoxes();

        // update lazarus current position to new position
        lazarus.resetLazarusPosition();

        // change game state and update lives
        if(--lazarus.lives < 0) {
            setGameState(Globals.GameState.GAME_OVER);
        }
    }

    private void renderBoxes(Graphics2D g2) {
        for (Box box : boxes) {
            renderBox(g2, box.getBoxType(), box.getX(), box.getY());
        }
    }

    private void renderBox(Graphics2D g2, String boxType, int newX, int newY) {
        Image image = null;
        if(boxType.equals(MapReader.CARDBOARD_BOX)) {
            image = Toolkit.getDefaultToolkit().getImage("resources/boxes/cardbox.png");
        } else if(boxType.equals(MapReader.WOOD_BOX)) {
            image = Toolkit.getDefaultToolkit().getImage("resources/boxes/woodbox.png");
        } else if(boxType.equals(MapReader.STONE_BOX)) {
            image = Toolkit.getDefaultToolkit().getImage("resources/boxes/stonebox.png");
        } else if(boxType.equals(MapReader.METAL_BOX)) {
            image = Toolkit.getDefaultToolkit().getImage("resources/boxes/metalbox.png");
        } else {
            System.err.println("Unknown Block Type : " + boxType);
        }
        g2.drawImage(image, newX, newY, Globals.BLOCK_SIZE, Globals.BLOCK_SIZE, this);
        g2.finalize();
    }

    public void moveBoxes() {
        Iterator<Box> itr = boxes.iterator();
        Box box;
        while(itr.hasNext()) {
            box = itr.next();
            if (collision.validateBoxToWallCollision(box)) {
                map[box.getY() / Globals.BLOCK_SIZE][box.getX() / Globals.BLOCK_SIZE] = box.getBoxType();
                itr.remove();
            } else if (collision.validateBoxToBoxCollision(box)) {
                // If there is box to box collision there are three possiblilities
                // 1. Heavy box (Priority higher) is on top of light box (Priority lower)
                // 2. Light box (Priority lower) is on top of heavy box (Priority higher)
                // 3. Both boxes of same type (same priority lower)
                stopBoxToBoxOnCollision(box, itr);
            } else {
                box.moveBoxDown();
            }
        }
    }

    /**
     * At this point the collision has already happned with another and we need to take some action.
     * Stops the box if it collides with another stationary box already on the floor
     */
    private void stopBoxToBoxOnCollision(Box currentBox, Iterator<Box> itr) {
        int newX = currentBox.getX();
        int newY = currentBox.getNextBoxDownPosition();
        String bottomBoxType = collision.getMapping(newX, newY);
        // Get box priorities
        int bottomBoxPriority = Box.getBoxPriority(bottomBoxType);
        int currentBoxPriority = currentBox.getBoxPriority(currentBox.getBoxType());

        if(bottomBoxPriority >= currentBoxPriority) {
            // Dont break bottom box and stop current box
            map[currentBox.getY() / Globals.BLOCK_SIZE][currentBox.getX() / Globals.BLOCK_SIZE] = currentBox.getBoxType();
            itr.remove();
        } else {
            // Break bottom box
            map[newY / Globals.BLOCK_SIZE][newX / Globals.BLOCK_SIZE] = MapReader.SPACE;
        }
    }

    public void renderBackground(Graphics2D g2) {
        Image image = Toolkit.getDefaultToolkit().getImage("resources/Background.png");
        g2.drawImage(image, 0, 0, Globals.BOARD_SIZE, Globals.BOARD_SIZE, this);
        g2.finalize();
    }

    public void renderLevel(Graphics2D g2) {
        Image image = Toolkit.getDefaultToolkit().getImage("resources/levels/level" + level + ".png");
        g2.drawImage(image, 250, 65, 300, 120, this);
        g2.finalize();
    }

    public void findStartPosition() {
        for (int row = 0; row < Globals.MAX_NUMBER_OF_BLOCKS; row++) {
            for (int col = 0; col < Globals.MAX_NUMBER_OF_BLOCKS; col++) {
                String value = map[row][col];
                int y = row * Globals.BLOCK_SIZE;
                int x = col * Globals.BLOCK_SIZE;
                if (value.equals(MapReader.LAZARUS)) {
                    startX = x;
                    startY = y;
                    continue;
                }
            }
        }
    }

    private void renderMap(Graphics2D g2) {

        for (int row = 0; row < Globals.MAX_NUMBER_OF_BLOCKS; row++) {
            for (int col = 0; col < Globals.MAX_NUMBER_OF_BLOCKS; col++) {
                String value = map[row][col];
                int y = row * Globals.BLOCK_SIZE;
                int x = col * Globals.BLOCK_SIZE;
                if (value.equals(MapReader.WALL)) {
                    renderWall(g2, x, y);
                    continue;
                }
                if (value.equals(MapReader.STOP)) {
                    renderButton(g2, x, y);
                    continue;
                }
                if (value.equals(MapReader.SPACE)) {
                    // Do nothing
                    continue;
                }
                if (value.equals(MapReader.LAZARUS)) {
                    startX = x;
                    startY = y;
                    continue;
                }
                if (MapReader.ALL_BOX_SET.contains(value)) {
                    renderBox(g2, value, x , y);
                }
            }
        }
    }

    private void renderWall(Graphics2D g2, int x, int y) {
        Image image = Toolkit.getDefaultToolkit().getImage("resources/Wall.png");
        g2.drawImage(image, x, y, Globals.BLOCK_SIZE, Globals.BLOCK_SIZE, this);
        g2.finalize();
    }

    private void renderButton(Graphics2D g2, int x, int y) {
        Image image = Toolkit.getDefaultToolkit().getImage("resources/Button.png");
        g2.drawImage(image, x, y, Globals.BLOCK_SIZE, Globals.BLOCK_SIZE, this);
        g2.finalize();
    }

    private void drawLazarus(Graphics2D g2, int x, int y) {

         if(this.currentAnimation != null){

            //when there is animation to play
           Image image = currentAnimation.nextImageOrNull();
            if(image == null){
                currentAnimation.updatePosition(this.lazarus);
                currentAnimation = null;
            }
            else {
                if(collision.validateLazarustoBoxesCollision(lazarus.x, lazarus.y)){
                    moveLeft = false;
                    moveRight = false;

                }
                g2.drawImage(image, x, y, Globals.BLOCK_SIZE, Globals.BLOCK_SIZE, this);
                g2.finalize();

            }

        }

        else {
            Image image = Toolkit.getDefaultToolkit().getImage("resources/lazarus/Lazarus_stand.png");
            g2.drawImage(image, x, y, Globals.BLOCK_SIZE, Globals.BLOCK_SIZE, this);
            g2.finalize();

        }
    }

   public void run() {
        Thread me = Thread.currentThread();
        while (thread == me) {
            repaint();
            try {
                runGame();
                thread.sleep(15);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public void gameStart() throws Exception {
        // Spawn boxes by using timer
        Timer timer = new Timer();
        timer.schedule(spawnBoxes, 0, 2000);

        thread = new Thread(this);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
        thread.join();
    }

}