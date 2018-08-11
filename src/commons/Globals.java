package commons;

public class Globals {

    public static int BLOCK_SIZE = 50;

    public static int BOARD_SIZE = 800;

    public static int MAX_NUMBER_OF_BLOCKS = 16;

    public static int BOX_SPEED = 2;

    public static String MAP1_FILENAME = "maps/map1.csv";

    public static String MAP2_FILENAME = "maps/map2.csv";

    public enum GameState{READY, RUNNING, GAME_OVER, GAME_WON};

    public static final int LIVES = 2;

    public static final int INITIAL_LEVEL = 1;

    public static final int FINAL_LEVEL = 2;

}
