package io.github.Sonic_V0.Personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
//import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import io.github.Sonic_V0.Constantes;
import io.github.Sonic_V0.Mundo.Mundo;

import java.util.List;

public class Robot extends Enemigas {
    private float tiempoBasura;
    private final Mundo world;

    protected TextureRegion chatarraSprite;
    private float tiempoEnChatarra = 0f;
    private final float DURACION_CHATARRA = 30f;

    public Robot(Vector2 posicion, Body objetivo, Mundo world) {
        super(posicion, world.getWorld());
        this.destruido = false;
        inicializarAnimaciones(body.getPosition().x, body.getPosition().y);
        this.objetivo = objetivo;
        this.name = "Robot";
        this.world = world;
    }

    @Override
    public void inicializarAnimaciones(float x, float y) {
        atlas = new TextureAtlas(Gdx.files.internal("Robot/robot.atlas"));
        sprite = atlas.createSprite("robot");
        sprite.setSize(30f / PPM, 39f / PPM);
        sprite.setPosition(
            x - sprite.getWidth() / 2f,
            y - sprite.getHeight() / 2f
        );
        correr = crearAnimacion("robotmove", 7, 0.09f);
        KO = crearAnimacion("robot", 4, 0.15f);

        chatarraSprite = atlas.createSprite("robotKO");
        if(chatarraSprite == null) {
            chatarraSprite = KO.getKeyFrame(KO.getAnimationDuration(), true);
        }
    }

    public void seleccionarObjetivoMasCercano(List<Amigas> posibles) {
        if (posibles == null || posibles.isEmpty()) return;


        if (posibles.size() == 1) {
            setObjetivo(posibles.get(0).getCuerpo());
            return;
        }

        Amigas masCercano = null;
        float distanciaMin = Float.MAX_VALUE;

        for (Amigas objetivo : posibles) {
            float distancia = objetivo.getCuerpo().getPosition().dst2(body.getPosition());
            if (distancia < distanciaMin) {
                distanciaMin = distancia;
                masCercano = objetivo;
            }
        }

        if (masCercano != null) {
            setObjetivo(masCercano.getCuerpo());
        }
    }

    @Override
    public void setKO() {
        if (!ko) {
            ko = true;
            stateTime = 0f;
            tiempoEnChatarra = 0f;
            if (body != null) {
                body.setLinearVelocity(0, 0);

                for (Fixture fixture : body.getFixtureList()) {
                    Filter filter = fixture.getFilterData();
                    filter.categoryBits = Constantes.CATEGORY_OBJETOS;
                    filter.maskBits = -1;
                    fixture.setFilterData(filter);
                }
            }
        }
    }

    @Override
    public void destruir() {
        if (destruido && body != null) {
            world.getWorld().destroyBody(body); // Usa el 'this.world' almacenado
            body = null;
        }
    }

    public boolean getDestuido() {
        return destruido;
    }

    public void setDestruido() {
        destruido = true;
    }

    public void reactivacion() {
        if (ko) {
            ko = false;
            tiempoEnChatarra = 0f;
        }
    }



    public boolean estaListoParaEliminar() {
        return destruido && tiempoEnChatarra >= DURACION_CHATARRA;
    }

    @Override
    public void actualizar(float delta) {
        if (ko) {
            tiempoEnChatarra += delta;
            stateTime += delta;

            // --- INICIO DE LA MODIFICACIÓN IMPORTANTE ---
            // Actualiza la posición del sprite a la posición actual del body
            // Esto asegura que la animación KO se dibuje donde el robot fue detenido
            if (body != null) { // Asegúrate de que el body no sea null si ya fue destruido
                sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2f,
                    body.getPosition().y - sprite.getHeight() / 2f);
            }
            // --- FIN DE LA MODIFICACIÓN IMPORTANTE ---

            if (tiempoEnChatarra >= DURACION_CHATARRA) {
                if (!destruido) {
                    reactivacion();
                }
            }
            return;
        }

        super.actualizar(delta);
        tiempoBasura += delta;
        if (tiempoBasura >= 30f) {
            Vector2 posicionActual = body.getPosition().cpy();
            world.generarBasura(posicionActual);
            tiempoBasura = 0f;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if (ko) {
            if (KO != null && !KO.isAnimationFinished(stateTime)) {
                batch.draw(KO.getKeyFrame(stateTime), sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
            } else if (chatarraSprite != null) {
                batch.draw(chatarraSprite, sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
            }
        } else {
            super.render(batch);
        }
    }

    @Override
    public void crearCuerpo(Vector2 posicion, World world) {
        super.crearCuerpo(posicion, world);

        CircleShape sensorShape = new CircleShape();
        sensorShape.setRadius(0.7f);

        FixtureDef sensorDef = new FixtureDef();
        sensorDef.shape = sensorShape;
        sensorDef.isSensor = true;

        configurarFiltroSensor(sensorDef);
        body.createFixture(sensorDef).setUserData("sensor");
        sensorShape.dispose();
    }

    public void configurarFiltroSensor(FixtureDef fdef) {
        fdef.filter.categoryBits = Constantes.CATEGORY_SENSOR;
        fdef.filter.maskBits = Constantes.CATEGORY_TRASH | Constantes.CATEGORY_NUBE |
            Constantes.CATEGORY_PERSONAJES | Constantes.CATEGORY_ROBOT | Constantes.CATEGORY_OBJETOS;
    }

    @Override
    public void configurarFiltro(FixtureDef fdef) {
        fdef.filter.categoryBits = Constantes.CATEGORY_ROBOT;
        fdef.filter.maskBits = (short) (Constantes.CATEGORY_PERSONAJES | Constantes.CATEGORY_OBJETOS |
            Constantes.CATEGORY_GOLPE_PERSONAJES);
    }


    @Override
    public void dispose() {
        atlas.dispose();
    }
}
