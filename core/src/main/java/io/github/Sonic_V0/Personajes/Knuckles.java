package io.github.Sonic_V0.Personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.utils.Array;
import io.github.Sonic_V0.Constantes; // Ensure this is imported

public class Knuckles extends Amigas {
    private Animation<TextureRegion> explosion;
    private boolean golpeando = false;
    private Fixture golpeFixture; // único fixture
    private CircleShape golpeShape;
    private float tiempoGolpeActivo = 0f;
    private final float DURACION_GOLPE_ACTIVO = 0.1f;
    private final float TIEMPO_GOLPE_COMIENZO = 0.2f;
    private boolean mostrarImpacto = false;
    private float estadoImpacto = 0f;
    private TextureRegion frameImpacto;
    private int direccionImpacto = 0; // 0=Ninguna, 1=Izq, 2=Der, 3=Arriba, 4=Abajo

    public Knuckles (Vector2 posicion, World world) {
        super(posicion, world);
        inicializarAnimaciones(body.getPosition().x, body.getPosition().y);
        this.name = "Knuckles";
        crearGolpeFixture(); // Call this to create both left and right fixtures
    }

    private void crearGolpeFixture() {
        final float PALO_RADIUS = 0.7f;
        golpeShape = new CircleShape();
        golpeShape.setRadius(PALO_RADIUS);

        // Posición inicial en el centro
        golpeShape.setPosition(new Vector2(0f, 0f));

        FixtureDef fdef = new FixtureDef();
        fdef.shape = golpeShape;
        fdef.isSensor = true;
        fdef.filter.categoryBits = Constantes.CATEGORY_GOLPE_PERSONAJES;
        fdef.filter.maskBits = 0; // inicialmente inactivo

        golpeFixture = body.createFixture(fdef);
        golpeFixture.setUserData("golpeKnuckles");
    }

    private void moverGolpeSegunDireccion() {
        if (golpeFixture != null) {
            body.destroyFixture(golpeFixture); // eliminar anterior
        }

        final float RADIO = 0.7f;
        final float DISTANCIA = 0.8f;
        Vector2 offset = new Vector2();

        switch (direccionImpacto) {
            case 1: offset.set(-DISTANCIA, 0); break;
            case 2: offset.set(DISTANCIA, 0); break;
            case 3: offset.set(0, DISTANCIA); break;
            case 4: offset.set(0, -DISTANCIA); break;
            case 5: offset.set(-DISTANCIA, DISTANCIA); break;
            case 6: offset.set(DISTANCIA, DISTANCIA); break;
            case 7: offset.set(-DISTANCIA, -DISTANCIA); break;
            case 8: offset.set(DISTANCIA, -DISTANCIA); break;
        }

        CircleShape newShape = new CircleShape();
        newShape.setRadius(RADIO);
        newShape.setPosition(offset);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = newShape;
        fdef.isSensor = true;
        fdef.filter.categoryBits = Constantes.CATEGORY_GOLPE_PERSONAJES;
        fdef.filter.maskBits = Constantes.CATEGORY_ROBOT;

        golpeFixture = body.createFixture(fdef);
        golpeFixture.setUserData("golpeKnuckles");

        newShape.dispose();
    }

    private void activarGolpeFixture() {
        activarGolpe(); // ya tienes este método
    }

    private void desactivarGolpeFixtures() {
        if (golpeFixture != null) {
            Filter filter = golpeFixture.getFilterData();
            filter.maskBits = 0; // desactivado
            golpeFixture.setFilterData(filter);
        }
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
        TextureAtlas atlas2 = new TextureAtlas((Gdx.files.internal("SpriteKnuckles/explosion.atlas")));
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

        habilidad = crearAnimacion("knucklesFist", 8, 0.1f);

        Array<TextureRegion> framesExplosion = new Array<>();
        for (int i = 1; i <= 12; i++) {
            TextureRegion frame = atlas2.findRegion("explosion" + i);
            if (frame == null) {
                System.out.println("No se encontró explosion" + i);
            } else {
                framesExplosion.add(frame);
            }
        }
        explosion = new Animation<>(0.1f, framesExplosion, Animation.PlayMode.NORMAL);

        frameActual = new TextureRegion(sprite);
    }

    @Override
    public void configurarFiltro(FixtureDef fdef) {
        fdef.filter.categoryBits = Constantes.CATEGORY_PERSONAJES;
        fdef.filter.maskBits = (short) (Constantes.CATEGORY_ROBOT | Constantes.CATEGORY_TRASH |
            Constantes.CATEGORY_SENSOR | Constantes.CATEGORY_NUBE |
            Constantes.CATEGORY_OBJETOS);
    }

    private void activarGolpe() {
        if (golpeFixture != null) {
            Filter filter = golpeFixture.getFilterData();
            filter.maskBits = Constantes.CATEGORY_ROBOT;
            golpeFixture.setFilterData(filter);
        }
    }

    public void golpear() {
        if (!golpeando) {
            golpeando = true;
            stateTime = 0f;
            tiempoGolpeActivo = 0f;

            if (izq && arr) direccionImpacto = 5;
            else if (der && arr) direccionImpacto = 6;
            else if (izq && abj) direccionImpacto = 7;
            else if (der && abj) direccionImpacto = 8;
            else if (izq) direccionImpacto = 1;
            else if (der) direccionImpacto = 2;
            else if (arr) direccionImpacto = 3;
            else if (abj) direccionImpacto = 4;
            else direccionImpacto = 0;
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

                moverGolpeSegunDireccion(); // <-- aquí se actualiza la posición del palo

                activarGolpeFixture();      // activa el fixture con la posición ya actualizada

                if (!mostrarImpacto) {
                    mostrarImpacto = true;
                    estadoImpacto = 0f;
                }
            } else {
                desactivarGolpeFixtures(); // Deactivate both outside the active window
            }

            frameActual = habilidad.getKeyFrame(stateTime);

            if (habilidad.isAnimationFinished(stateTime)) {
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
    public void render(SpriteBatch batch) {
        super.render(batch);
        if (mostrarImpacto) {
            estadoImpacto += Gdx.graphics.getDeltaTime();
            frameImpacto = explosion.getKeyFrame(estadoImpacto, false); // no looping

            float distancia = 0.8f;
            float offsetX = 0, offsetY = 0;

            switch (direccionImpacto) {
                case 1: offsetX = -distancia; break;
                case 2: offsetX = distancia; break;
                case 3: offsetY = distancia; break;
                case 4: offsetY = -distancia; break;
                case 5: offsetX = -distancia; offsetY = distancia; break;
                case 6: offsetX = distancia;  offsetY = distancia; break;
                case 7: offsetX = -distancia; offsetY = -distancia; break;
                case 8: offsetX = distancia;  offsetY = -distancia; break;
            }

            float x = body.getPosition().x + offsetX;
            float y = body.getPosition().y + offsetY;

            if (frameImpacto != null) {
                batch.draw(frameImpacto,
                    x - frameImpacto.getRegionWidth() / 2f / Constantes.PPM,
                    y - frameImpacto.getRegionHeight() / 2f / Constantes.PPM,
                    frameImpacto.getRegionWidth() / Constantes.PPM,
                    frameImpacto.getRegionHeight() / Constantes.PPM
                );
            }

            // Cuando termina la animación, desactívala
            if (explosion.isAnimationFinished(estadoImpacto)) {
                mostrarImpacto = false;
            }
        }
    }

    @Override
    public void dispose() {
        atlas.dispose();
    }
}
