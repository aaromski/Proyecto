package io.github.Sonic_Test;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Sonic extends Personaje{
    public Sonic (float x, float y) {
        super (x,y);
        inicializarAnimaciones(x,y);
    }

    @Override
    void inicializarAnimaciones(float x, float y) {
        sprite = new Sprite(new Texture("spritesonic/spritesonic0.png"));
        sprite.setPosition(x,y);
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

    @Override
    protected void actualizar(float delta) {
        izq = false;
        der = false;
        // Movimiento con teclado
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            posX -= velocidad * delta;
            izq = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            posX += velocidad * delta;
            der = true;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W) && !salta) {
            salta = true;
            velocidadY = velocidadSalto; // Aplicar impulso inicial
        }
        super.actualizar(delta);
    }
}
