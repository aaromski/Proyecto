package io.github.Sonic_Test;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

abstract public class Personaje {
    protected  boolean izq = false,  der = false;
    protected float posX;
    protected float posY;
    protected float velocidad = 200;
    protected float velocidadSalto = 300; // Velocidad inicial del salto
    protected float gravedad = -900; // Gravedad que afecta el salto
    protected float velocidadY = 0; // Velocidad vertical
    protected boolean salta = false;
    protected TextureRegion[] animacionCorriendo;
    protected Animation<TextureRegion> corriendo;
    protected Animation<TextureRegion> saltando;
    protected float stateTime = 0f;
    protected TextureRegion[] animacionSaltando;
    protected TextureRegion frameActual;
    protected Sprite sprite;

    public Personaje(float x, float y) {
        this.posX = x;
        this.posY = y;
        inicializarAnimaciones(x, y);
    }

    abstract void inicializarAnimaciones(float x, float y);

    protected void actualizar(float delta) {
        stateTime += delta;

        if (salta) {
            velocidadY += gravedad * delta; // Aplicar gravedad
            posY += velocidadY * delta; // Actualizar posición vertical
            // Si toca el suelo, detener el salto
            if (posY <= 100) { // Ajusta según el suelo del juego
                posY = 100;
                salta = false;
                velocidadY = 0;
            }
        }

        // Seleccionar animación según el estado
        if (salta) {
            stateTime += delta;
            frameActual = saltando.getKeyFrame(stateTime, false); // No en bucle
            if (frameActual == null) {
                System.out.println("Error: getKeyFrame() devolvió NULL");
            }
        } else if (izq || der) {
            stateTime += delta;
            frameActual = corriendo.getKeyFrame(stateTime, true);
            if (corriendo.getKeyFrames().length == 0) {
                System.out.println("Error: corriendo no tiene frames cargados");
            }
        } else {
            frameActual = corriendo.getKeyFrame(0, false);
            if (frameActual == null) {
                System.out.println("Error: getKeyFrame3() devolvió NULL");
            }
        }

        // Invertir el sprite si se mueve a la izquierda
        if (frameActual != null && izq && !frameActual.isFlipX()) {
            frameActual.flip(true, false);
        }
        if (frameActual != null && der && frameActual.isFlipX()) {
            frameActual.flip(true, false);
        }
    }

    public void render(SpriteBatch batch) {
        sprite.setRegion(frameActual); // 🔄 Actualizar el sprite con la animación actual
        batch.draw(frameActual, posX, posY);
    }


    public void dispose() {
        for (TextureRegion frame : animacionCorriendo) {
            frame.getTexture().dispose();
        }
        for (TextureRegion frame : animacionSaltando) {
            frame.getTexture().dispose();
        }
    }

}
