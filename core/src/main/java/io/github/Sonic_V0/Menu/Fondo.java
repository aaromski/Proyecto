package io.github.Sonic_V0.Menu;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Fondo {
    static Texture fondo;
    private float scrollX;

    public Fondo() {
        fondo = new Texture("Menu/Sunset_Hill.png");
    }

    public void actualizar(float delta) {
        // Desplazamiento del fondo
        scrollX -= delta * 50f; // velocidad de desplazamiento
        if (scrollX <= -fondo.getWidth()) scrollX += fondo.getWidth();
    }

    public void dibujar(SpriteBatch batch, int screenW, int screenH) {
        // Dibujar copias encadenadas del fondo
        for (int x = (int)scrollX; x < screenW; x += fondo.getWidth()) {
            batch.draw(fondo, x, 0, fondo.getWidth(), screenH);
        }
    }

    public int getWidth() {
        return fondo.getWidth();
    }

    public void dispose(){
        fondo.dispose();
    }
}
