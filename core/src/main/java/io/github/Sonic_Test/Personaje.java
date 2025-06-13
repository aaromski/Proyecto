package io.github.Sonic_Test;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class Personaje {
    protected float posX;
    protected float posY = 130;
    protected float velocidad = 200;
    protected boolean salta = false;
    protected float velocidadSalto = 300; // Velocidad inicial del salto
    protected float gravedad = -900; // Gravedad que afecta el salto
    protected float velocidadY = 0; // Velocidad vertical
    protected TextureRegion[] animacionCorriendo;
    protected Animation<TextureRegion> corriendo;
    protected Animation<TextureRegion> saltando;
    protected float stateTime = 0f;
    protected TextureRegion[] animacionSaltando;
    protected TextureRegion frameActual;

    public Personaje(float x, float y) {
        this.posX = x;
        this.posY = y;
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

    public void actualizar(float delta, boolean moviendo, boolean saltando) {
        stateTime += delta;

        // ✅ Usamos 'salta' correctamente
        if (saltando && !salta) {
            salta = true;
            velocidadY = velocidadSalto; // Aplicamos impulso inicial
        }

        if (salta) {
            velocidadY += gravedad * delta; // Aplicamos gravedad
            posY += velocidadY * delta; // Ajustamos posición vertical

            // Si toca el suelo, detenemos el salto
            if (posY <= 130) { // Ajusta según el suelo del juego
                posY = 130;
                salta = false;
                velocidadY = 0;
            }
        }

        frameActual = salta ? this.saltando.getKeyFrame(stateTime, false) : this.corriendo.getKeyFrame(stateTime, true);
    }

    public void render(SpriteBatch batch) {
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
