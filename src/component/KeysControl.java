package component;

import commons.AudioPlayer;
import commons.Globals;
import core.LazarusWorld;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeysControl extends KeyAdapter {

    private Lazarus player;

    private LazarusWorld lazarusWorld;
    private AudioPlayer playMusic;



    public KeysControl(Lazarus player, LazarusWorld lazarusWorld) {
        this.player = player;
        this.lazarusWorld = lazarusWorld;
    }

    public void keyPressed(KeyEvent e) {

        int keysCode = e.getKeyCode();
        playMusic = new AudioPlayer(this.lazarusWorld, "resources/Move.wav");


        if (lazarusWorld.getGameState() == Globals.GameState.READY) {
            if (keysCode == KeyEvent.VK_SPACE) {
                lazarusWorld.setGameState(Globals.GameState.RUNNING);
                lazarusWorld.clearBoxes();
            }
            return;
        }

        if (lazarusWorld.getGameState() == Globals.GameState.GAME_OVER) {
            if (keysCode == KeyEvent.VK_SPACE) {
                System.exit(0);
            }
            return;
        }

        if (lazarusWorld.getGameState() == Globals.GameState.GAME_WON) {
            if (keysCode == KeyEvent.VK_SPACE) {
                System.exit(0);
            }
            return;
        }

        // When lazarus is in RUNNING state

        if (keysCode == KeyEvent.VK_LEFT) {

            playMusic.play();
            LazarusWorld.startX = player.x;
            LazarusWorld.endLeft = LazarusWorld.startX - LazarusWorld.width;
            LazarusWorld.movingLeft = true;
            LazarusWorld.moveLeft = true;
        }

        if (keysCode == KeyEvent.VK_RIGHT) {
            playMusic.play();
            LazarusWorld.startX = player.x;
            LazarusWorld.endRight = LazarusWorld.startX + LazarusWorld.width;
            LazarusWorld.movingRight = true;
            LazarusWorld.moveRight = true;
        }
    }

    public void keyReleased(KeyEvent e) {
        int keysCode = e.getKeyCode();

        if (keysCode == KeyEvent.VK_LEFT) {
            LazarusWorld.moveLeft = false;
        }
        if (keysCode == KeyEvent.VK_RIGHT) {
            LazarusWorld.moveRight = false;
        }
    }
}