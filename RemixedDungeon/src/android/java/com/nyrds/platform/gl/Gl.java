package com.nyrds.platform.gl;

import android.opengl.GLES20;

import com.nyrds.platform.game.Game;

public class Gl {

    public static void clear() {
        GLES20.glScissor(0, 0, Game.width(), Game.height());
        GLES20.glClearColor(0, 0, 0, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    }
}
