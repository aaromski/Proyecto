package io.github.Sonic_Test;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class Personaje {
    protected float posX;
    protected float posY = 130;
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
        sprite = new Sprite(new Texture(("spritesonic/spritesonic0.png")));
        sprite.setPosition(x,y);
        inicializarAnimaciones();
    }

    private void inicializarAnimaciones() {
        animacionCorriendo = new TextureRegion[7];
        for (int i = 0; i < 7; i++) {
            animacionCorriendo[i] = new TextureRegion(new Texture("spritesonic/spritesonic" + i + ".png"));
        }
        corriendo = new Animation<>(0.1f, animacionCorriendo);
        corriendo.setPlayMode(Animation.PlayMode.LOOP);

        animacionSaltando = new TextureRegion[8];
        for (int i = 0; i < 8; i++) {
            animacionSaltando[i] = new TextureRegion(new Texture("spritesonic/salto" + (i + 1) + ".png"));
        }
        saltando = new Animation<>(0.2f, animacionSaltando);
        saltando.setPlayMode(Animation.PlayMode.LOOP);

        frameActual = new TextureRegion();
    }

    public void actualizar(float delta, boolean izq, boolean der) {
        // Movimiento con teclado
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            posX -= velocidad * delta;
            izq = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            posX += velocidad * delta;
            der = true;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP) && !salta) {
            salta = true;
            velocidadY = velocidadSalto; // Aplicar impulso inicial
        }

        stateTime += delta;
        if (salta) {
            velocidadY += gravedad * delta; // Aplicar gravedad
            posY += velocidadY * delta; // Actualizar posición vertical

            // Si toca el suelo, detener el salto
            if (posY <= 100) { // Ajusta según el suelo de tu juego
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

    /*public Rectangle getBoundingRectangle() {
        sprite.setPosition(posX, posY); // 🔄 Asegura que la posición del sprite se actualiza
        return sprite.getBoundingRectangle();
    }*/


    public void dispose() {
        for (TextureRegion frame : animacionCorriendo) {
            frame.getTexture().dispose();
        }
        for (TextureRegion frame : animacionSaltando) {
            frame.getTexture().dispose();
        }
    }

}
