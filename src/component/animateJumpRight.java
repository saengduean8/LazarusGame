package component;

import commons.Globals;

import java.awt.*;

public class animateJumpRight extends Animation {

    Image [] aniJumpRightImage;
    int index;

    public animateJumpRight(int x, int y) {
        super(x, y);
        this.index = 0;
        this.aniJumpRightImage = new Image[7];
        aniJumpRightImage[0] = Toolkit.getDefaultToolkit().getImage("resources/lazarus/LazJumpRight/Background.gif");
        aniJumpRightImage[1] = Toolkit.getDefaultToolkit().getImage("resources/lazarus/LazJumpRight/Frame 2.gif");
        aniJumpRightImage[2] = Toolkit.getDefaultToolkit().getImage("resources/lazarus/LazJumpRight/Frame 3.gif");
        aniJumpRightImage[3] = Toolkit.getDefaultToolkit().getImage("resources/lazarus/LazJumpRight/Frame 4.gif");
        aniJumpRightImage[4] = Toolkit.getDefaultToolkit().getImage("resources/lazarus/LazJumpRight/Frame 5.gif");
        aniJumpRightImage[5] = Toolkit.getDefaultToolkit().getImage("resources/lazarus/LazJumpRight/Frame 6.gif");
        aniJumpRightImage[6] = Toolkit.getDefaultToolkit().getImage("resources/lazarus/LazJumpRight/Frame 7.gif");
    }
    public  Image nextImageOrNull(){
        if(index >= aniJumpRightImage.length ) {
            return null;

        }
        Image returnImage = aniJumpRightImage[index];
        index++;


        return returnImage;
    }

    public void updatePosition(Lazarus lazarus) {
        lazarus.y = lazarus.y - Globals.BLOCK_SIZE;
        lazarus.x = lazarus.x + Globals.BLOCK_SIZE;

    }

}
