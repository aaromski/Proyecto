package io.github.Sonic_V0.Personajes;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

public abstract class Personaje {
    protected Vector2 velocidad;
    protected float stateTime = 0f;
    protected TextureRegion frameActual;
    protected Sprite sprite;
    protected Animation<TextureRegion> correr;
    protected Body body;
    public static final float PPM = 32f;
    protected TextureAtlas atlas;
    protected Vector2 posicion;
    protected String name;
    protected boolean ko = false;
    protected final World world; // <-- ¡NUEVO! Se guarda la referencia al mundo

    public Personaje (Vector2 posicion, World w) {
        this.world = w; // <-- ¡NUEVO! Asignamos el mundo al campo de la clase
        crearCuerpo(posicion, w);
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
        if (frameActual != null && body != null) {
            batch.draw(
                frameActual,
                posicion.x - sprite.getWidth() / 2f,
                posicion.y - sprite.getHeight() / 2f,
                sprite.getWidth(),
                sprite.getHeight()
            );
        }
    }

    abstract void configurarFiltro(FixtureDef fdef);

    // Nota: Este método también recibe 'world' como parámetro, pero ahora 'this.world' también está disponible.
    public void crearCuerpo(Vector2 posicion, World world) {
        BodyDef bd = new BodyDef();
        bd.position.set(posicion);
        bd.type = BodyDef.BodyType.DynamicBody;

        CircleShape circle = new CircleShape();
        circle.setRadius(0.5f);

        FixtureDef fixDef = new FixtureDef();
        fixDef.shape = circle;

        body = world.createBody(bd); // Usamos el 'world' que se pasa como parámetro aquí

        body.setUserData(this);

        body.setLinearDamping(5f);

        configurarFiltro(fixDef);

        body.createFixture(fixDef).setUserData(this);
        circle.dispose();
    }

    public Body getCuerpo() {return body;}

    public boolean getKO() {return ko;}

    public void setKO() {ko = !ko;
        stateTime = 0f;
    }

    // Método destruir para personajes. No necesita parámetro porque 'this.world' ya está disponible.
    public void destruir() {
        if (ko && body != null) {
            this.world.destroyBody(body); // Usa el 'this.world' almacenado
            body = null;
        }
    }

    public abstract void dispose();
}
