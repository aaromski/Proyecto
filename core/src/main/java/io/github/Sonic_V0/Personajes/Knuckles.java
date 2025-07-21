package io.github.Sonic_V0.Personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import io.github.Sonic_V0.Constantes;

public class Knuckles extends Amigas {
    protected Animation<TextureRegion> golpe;
    protected boolean golpeando = false;
    public Knuckles (Vector2 posicion, World world) {
        super(posicion, world);
        inicializarAnimaciones(body.getPosition().x, body.getPosition().y);
        this.name = "Knuckles";
    }

    @Override
    void inicializarAnimaciones(float x, float y) {
        atlas = new TextureAtlas(Gdx.files.internal("SpriteKnuckles/KnucklesSprite.atlas"));
        sprite = atlas.createSprite("KnucklesStanding1");
        sprite.setSize(30f / PPM, 39f / PPM); // ≈ 0.91 x 1.19
        sprite.setPosition(
            x - sprite.getWidth() / 2f,
            y - sprite.getHeight() / 2f
        );
        correr = crearAnimacion("KnucklesRunning", 10, 0.09f);
        abajo = crearAnimacion("Abajo", 6, 0.1f); // Asegúrate de que esta animación sea correcta para "abajo"
        arriba = crearAnimacion("Arriba", 8, 0.1f);
        diagonalarr = crearAnimacion("DiagonalTrasera", 8, 0.1f);
        diagonalabj = crearAnimacion("DiagonalDelantera", 6, 0.1f);

        // Golpe
        golpe = crearAnimacion("knucklesFist", 8, 0.1f); // Asegúrate de que "knucklesFist" es el nombre correcto en tu atlas

        frameActual = new TextureRegion(sprite);
    }

    public void configurarFiltro(FixtureDef fdef) {
        fdef.filter.categoryBits = Constantes.CATEGORY_PERSONAJES;
        fdef.filter.maskBits = -1;
    }

    public void golpear() {
        if (!golpeando) {
            golpeando = true;
            stateTime = 0f;
        }
    }

    @Override
    public void actualizar(float alpha) {
        if (ko) { return; }
            izq = der = abj = arr = false; // Reset movement states
            boolean presionando = false;

            // Movimiento
            if (Gdx.input.isKeyPressed(Input.Keys.I)) {
                body.setLinearVelocity(0, velocidad.y);
                arr = true;
                presionando = true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.K)) {
                body.setLinearVelocity(0, -velocidad.y);
                abj = true;
                presionando = true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.J)) {
                body.setLinearVelocity(-velocidad.x, body.getLinearVelocity().y);
                izq = true;
                presionando = true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.L)) {
                body.setLinearVelocity(velocidad.x, body.getLinearVelocity().y);
                der = true;
                presionando = true;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.P)) { // Puedes elegir la tecla que prefieras, por ejemplo, 'H'
                golpear(); // Llama al método golpear() definido en la clase Amigas
                presionando = true; // Marca como presionando para que no entre en el estado inactivo inmediatamente
            }

            // Sumar tiempo si se mueve o golpea
            if (presionando || golpeando) {
                stateTime += alpha;
            } else {
                body.setLinearVelocity(0, 0);
                stateTime = 0f;
            }

            posicion = body.getPosition();

            if (golpeando) {
                frameActual = golpe.getKeyFrame(stateTime);
                if (golpe.isAnimationFinished(stateTime)) {
                    golpeando = false;
                }
            } else {
                super.actualizar(alpha); // Solo llama al padre si no estás golpeando
            }
    }

    @Override
    public void dispose() {
        atlas.dispose();
    }
}
