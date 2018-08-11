package component;

import commons.Globals;

import java.awt.*;

public class animateRight extends Animation {

    Image[] aniRightImage;
    int index;

    public animateRight(int x, int y) {
        super(x, y);
        this.index = 0;
        this.aniRightImage = new Image[7];
        aniRightImage[0] = Toolkit.getDefaultToolkit().getImage("resources/lazarus/LazRight/Background.gif");
        aniRightImage[1] = Toolkit.getDefaultToolkit().getImage("resources/lazarus/LazRight/Frame 2.gif");
        aniRightImage[2] = Toolkit.getDefaultToolkit().getImage("resources/lazarus/LazRight/Frame 3.gif");
        aniRightImage[3] = Toolkit.getDefaultToolkit().getImage("resources/lazarus/LazRight/Frame 4.gif");
        aniRightImage[4] = Toolkit.getDefaultToolkit().getImage("resources/lazarus/LazRight/Frame 5.gif");
        aniRightImage[5] = Toolkit.getDefaultToolkit().getImage("resources/lazarus/LazRight/Frame 6.gif");
        aniRightImage[6] = Toolkit.getDefaultToolkit().getImage("resources/lazarus/LazRight/Frame 7.gif");
    }

    public Image nextImageOrNull() {
        if (index >= aniRightImage.length) {
            return null;

        }
        Image returnImage = aniRightImage[index];
        index++;


        return returnImage;
    }


    public void updatePosition(Lazarus lazarus) {
        lazarus.x += Globals.BLOCK_SIZE;
    }

}