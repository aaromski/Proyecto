package io.github.Sonic_V0.Personajes;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;

public abstract class Personaje {
    protected Vector2 velocidad;  //200
    protected float stateTime = 0f;
    protected TextureRegion frameActual;
    protected Sprite sprite;
    protected Animation<TextureRegion> correr;
    protected Body body;
    public static final float PPM = 32f; // Pixels Per Meter
    protected TextureAtlas atlas;
    protected Vector2 posicion;
    protected String name;
    protected boolean ko = false;

    public Personaje (Body b) {
        this.body = b;
        this.posicion = body.getPosition();
        velocidad = new Vector2(3f, 3f);
    }


    abstract void inicializarAnimaciones(float x, float y);
    abstract void actualizar(float delta);

    protected Animation<TextureRegion> crearAnimacion(String baseNombre, int cantidadFrames, float duracion) {
        Array<TextureRegion> frames = new Array<>();
        for (int i = 1; i <= cantidadFrames; i++) {
            frames.add(atlas.findRegion(baseNombre + i));
        }
        return new Animation<>(duracion, frames);
    }

     public void render(SpriteBatch batch) {
        // Si estás usando solo TextureRegion:
         if (frameActual != null) {
             batch.draw(
                 frameActual,
                 posicion.x - sprite.getWidth() / 2f,
                 posicion.y - sprite.getHeight() / 2f,
                 sprite.getWidth(),
                 sprite.getHeight()
             );
         } else {
             System.out.println("⚠️ Textura nula en: " + this.name);
         }
    }

    public Vector2 getPosicion(){
        return posicion;
    }

    public Body getCuerpo() {return body;}

    public boolean getKO() {return ko;}

    public void setKO() {ko = !ko;
    stateTime = 0f;
    }

    public abstract void dispose();
}

