package io.github.Sonic_V0.Personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.Filter;
import io.github.Sonic_V0.Constantes; // Ensure this is imported

public class Knuckles extends Amigas {
    protected Animation<TextureRegion> golpe;
    protected boolean golpeando = false;
    private Fixture golpeFixtureRight; // Fixture for right punch
    private Fixture golpeFixtureLeft;  // Fixture for left punch
    private float tiempoGolpeActivo = 0f;
    private final float DURACION_GOLPE_ACTIVO = 0.1f;
    private final float TIEMPO_GOLPE_COMIENZO = 0.2f;

    public Knuckles (Vector2 posicion, World world) {
        super(posicion, world);
        inicializarAnimaciones(body.getPosition().x, body.getPosition().y);
        this.name = "Knuckles";
        crearGolpeFixtures(); // Call this to create both left and right fixtures
    }

    private void crearGolpeFixtures() {
        // Offset for the punch hitbox relative to Knuckles' center (in meters)
        // This is the distance from the center of Knuckles' body to the center of the punch hitbox.
        final float HORIZONTAL_PUNCH_OFFSET = 0.8f;
        final float PUNCH_RADIUS = 0.7f; // Radius of the punch hitbox

        // --- Fixture for punching RIGHT ---
        CircleShape shapeRight = new CircleShape();
        shapeRight.setRadius(PUNCH_RADIUS);
        // Position the circle's center OFFSET to the right of Knuckles' body center
        shapeRight.setPosition(new Vector2(HORIZONTAL_PUNCH_OFFSET, 0));

        FixtureDef fdefRight = new FixtureDef();
        fdefRight.shape = shapeRight;
        fdefRight.isSensor = true;
        fdefRight.filter.categoryBits = Constantes.CATEGORY_GOLPE_PERSONAJES;
        fdefRight.filter.maskBits = Constantes.CATEGORY_ROBOT;

        golpeFixtureRight = body.createFixture(fdefRight);
        golpeFixtureRight.setUserData("golpeKnuckles");
        shapeRight.dispose();

        // --- Fixture for punching LEFT ---
        CircleShape shapeLeft = new CircleShape();
        shapeLeft.setRadius(PUNCH_RADIUS);
        // Position the circle's center OFFSET to the left of Knuckles' body center
        shapeLeft.setPosition(new Vector2(-HORIZONTAL_PUNCH_OFFSET, 0));

        FixtureDef fdefLeft = new FixtureDef();
        fdefLeft.shape = shapeLeft;
        fdefLeft.isSensor = true;
        fdefLeft.filter.categoryBits = Constantes.CATEGORY_GOLPE_PERSONAJES;
        fdefLeft.filter.maskBits = Constantes.CATEGORY_ROBOT;

        golpeFixtureLeft = body.createFixture(fdefLeft);
        golpeFixtureLeft.setUserData("golpeKnuckles");
        shapeLeft.dispose();

        // Ensure both are inactive at start
        desactivarGolpeFixtures();
    }

    // New method to activate the correct punch fixture based on direction
    private void activarGolpeFixture() {
        if (body == null) return; // Guard against null body

        if (sprite.isFlipX()) { // Knuckles is facing left
            setFixtureActive(golpeFixtureLeft, true);
            setFixtureActive(golpeFixtureRight, false);
        } else { // Knuckles is facing right
            setFixtureActive(golpeFixtureRight, true);
            setFixtureActive(golpeFixtureLeft, false);
        }
    }

    // New method to deactivate all punch fixtures
    private void desactivarGolpeFixtures() {
        setFixtureActive(golpeFixtureRight, false);
        setFixtureActive(golpeFixtureLeft, false);
    }

    // Helper method to set a fixture's active state
    private void setFixtureActive(Fixture fixture, boolean active) {
        if (fixture != null) {
            Filter filter = fixture.getFilterData();
            if (active) {
                filter.maskBits = Constantes.CATEGORY_ROBOT; // Enable collision
            } else {
                filter.maskBits = 0; // Disable collision
            }
            fixture.setFilterData(filter);
        }
    }

    @Override
    void inicializarAnimaciones(float x, float y) {
        atlas = new TextureAtlas(Gdx.files.internal("SpriteKnuckles/KnucklesSprite.atlas"));
        sprite = atlas.createSprite("KnucklesStanding1");
        // Corrected PPM usage:
        sprite.setSize(30f / Constantes.PPM, 39f / Constantes.PPM);
        sprite.setPosition(
            x - sprite.getWidth() / 2f,
            y - sprite.getHeight() / 2f
        );
        correr = crearAnimacion("KnucklesRunning", 10, 0.09f);
        abajo = crearAnimacion("Abajo", 6, 0.1f);
        arriba = crearAnimacion("Arriba", 8, 0.1f);
        diagonalarr = crearAnimacion("DiagonalTrasera", 8, 0.1f);
        diagonalabj = crearAnimacion("DiagonalDelantera", 6, 0.1f);

        golpe = crearAnimacion("knucklesFist", 8, 0.1f);

        frameActual = new TextureRegion(sprite);
    }

    @Override
    public void configurarFiltro(FixtureDef fdef) {
        fdef.filter.categoryBits = Constantes.CATEGORY_PERSONAJES;
        fdef.filter.maskBits = (short) (Constantes.CATEGORY_ROBOT | Constantes.CATEGORY_TRASH |
            Constantes.CATEGORY_SENSOR | Constantes.CATEGORY_NUBE |
            Constantes.CATEGORY_OBJETOS);
    }

    public void golpear() {
        if (!golpeando) {
            golpeando = true;
            stateTime = 0f;
            tiempoGolpeActivo = 0f;
        }
    }

    @Override
    public void actualizar(float alpha) {
        if (ko) {
            desactivarGolpeFixtures(); // Deactivate both if Knuckles is KO
            return;
        }

        if (golpeando) {
            tiempoGolpeActivo += alpha;
            stateTime += alpha;

            if (tiempoGolpeActivo >= TIEMPO_GOLPE_COMIENZO &&
                tiempoGolpeActivo < (TIEMPO_GOLPE_COMIENZO + DURACION_GOLPE_ACTIVO)) {
                activarGolpeFixture(); // Activate the correct fixture based on direction
            } else {
                desactivarGolpeFixtures(); // Deactivate both outside the active window
            }

            frameActual = golpe.getKeyFrame(stateTime);

            if (golpe.isAnimationFinished(stateTime)) {
                golpeando = false;
                desactivarGolpeFixtures(); // Ensure both are deactivated when animation finishes
            }
        } else {
            boolean presionando = false;
            izq = der = abj = arr = false;

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

            if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
                golpear();
                presionando = true;
            }

            if (presionando) {
                stateTime += alpha;
            } else {
                body.setLinearVelocity(0, 0);
                stateTime = 0f;
            }

            super.actualizar(alpha);
        }
        posicion = body.getPosition();
    }

    @Override
    public void dispose() {
        atlas.dispose();
    }
}
