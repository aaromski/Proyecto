package io.github.Sonic_V0.Personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import io.github.Sonic_V0.Constantes;
import com.badlogic.gdx.utils.Array;

public class Tails extends Amigas {
    private boolean imanActivo = false;
    private final float radioAtraccionIman = 1.8f;
    protected TextureRegion imanSprite;
    private TextureAtlas imanAtlas;
    private Animation<TextureRegion> imanAnimation;
    private long lastImanPressTime = 0;
    private static final long DOUBLE_PRESS_IMAN_TIME = 200;

    private float imanOrbitAngle = 0f;
    private float imanOrbitRadius = 0.5f;

    private final float robotOffsetDistance = 0.1f;

    private MouseJoint robotAtraidoJoint;
    private Robot robotSiendoAtraido;

    private boolean puedeActivarIman = true;
    private float tiempoImanActivo = 0f;
    private float tiempoCooldownIman = 0f;

    private static final float DURACION_IMAN = 10f;
    private static final float COOLDOWN_IMAN = 10f;

    public Tails(Vector2 posicion, World world) {
        super(posicion, world);
        inicializarAnimaciones(body.getPosition().x, body.getPosition().y);
        this.name = "Tails";
    }

    @Override
    void inicializarAnimaciones(float x, float y) {
        atlas = new TextureAtlas(Gdx.files.internal("SpriteTails/Tails.atlas"));
        sprite = atlas.createSprite("TailsSprite0");
        sprite.setSize(30 / Constantes.PPM, 39f / Constantes.PPM);
        sprite.setPosition(
            x - sprite.getWidth() / 2f,
            y - sprite.getHeight() / 2f
        );

        correr = crearAnimacion("TailsSprite", 7, 0.09f);
        abajo = crearAnimacion("abajo", 3, 0.1f);
        arriba = crearAnimacion("arriba", 7, 0.1f);
        diagonalarr = crearAnimacion("diagonal", 9, 0.1f);
        diagonalabj = crearAnimacion("diagonalabj", 4, 0.1f);

        try {
            imanAtlas = new TextureAtlas(Gdx.files.internal("SpriteTails/iman.atlas"));
            Array<TextureRegion> imanFrames = new Array<>();

            for (int i = 1; i <= 4; i++) {
                TextureRegion frame = imanAtlas.findRegion("iman" + i);
                if (frame != null) {
                    imanFrames.add(frame);
                }
            }

            if (imanFrames.size > 0) {
                imanAnimation = new Animation<>(0.15f, imanFrames, Animation.PlayMode.LOOP);
                imanSprite = imanAnimation.getKeyFrame(0);
            } else {
                imanAnimation = null;
                imanSprite = null;
            }
        } catch (Exception e) {
            imanAnimation = null;
            imanSprite = null;
        }

        frameActual = new TextureRegion();
    }

    public void configurarFiltro(FixtureDef fdef) {
        fdef.filter.categoryBits = Constantes.CATEGORY_PERSONAJES;
        fdef.filter.maskBits = -1;
    }

    @Override
    public void actualizar(float delta) {
        if (body == null) {
            return;
        }

        // ⚙️ Control de duración y cooldown del imán
        if (imanActivo) {
            tiempoImanActivo += delta;

            if (tiempoImanActivo >= DURACION_IMAN) {
                imanActivo = false;
                tiempoCooldownIman = 0f;
                Gdx.app.log("Tails", "Imán desactivado automáticamente");
            }
        } else if (!puedeActivarIman) {
            tiempoCooldownIman += delta;
            if (tiempoCooldownIman >= COOLDOWN_IMAN) {
                puedeActivarIman = true;
                Gdx.app.log("Tails", "Imán listo para reactivarse");
            }
        }

        izq = der = abj = arr = false;
        boolean presionando = false;
        stateTime += delta;

        if (imanActivo) {
            imanOrbitAngle = (imanOrbitAngle + (200f * delta)) % 360f;

            if (robotAtraidoJoint != null && robotSiendoAtraido != null && robotSiendoAtraido.body != null) {
                if (!robotSiendoAtraido.body.isActive()) {
                    // El cuerpo está inactivo o destruido, desmonta el joint
                    Tails.this.world.destroyJoint(robotAtraidoJoint);
                    robotAtraidoJoint = null;
                    robotSiendoAtraido = null;
                } else {
                    Vector2 tailsCurrentPos = body.getPosition();
                    Vector2 tailsDirection = body.getLinearVelocity().cpy().nor();

                    Vector2 targetPos = tailsCurrentPos.cpy();

                    if (tailsDirection.len2() > 0.01f) {
                        targetPos.x -= tailsDirection.x * robotOffsetDistance;
                        targetPos.y -= tailsDirection.y * robotOffsetDistance;
                    } else {
                        targetPos.x += robotOffsetDistance * 0.5f;
                        targetPos.y += robotOffsetDistance * 0.5f;
                    }

                    float currentDistance = tailsCurrentPos.dst(robotSiendoAtraido.body.getPosition());
                    if (currentDistance < robotOffsetDistance * 0.8f) {
                        Vector2 currentDirToRobot = robotSiendoAtraido.body.getPosition().cpy().sub(tailsCurrentPos).nor();
                        targetPos = tailsCurrentPos.cpy().add(currentDirToRobot.scl(robotOffsetDistance));
                    }

                    robotAtraidoJoint.setTarget(targetPos);
                }
            }
        } else {
            imanOrbitAngle = 0f;
            if (robotAtraidoJoint != null) {
                Tails.this.world.destroyJoint(robotAtraidoJoint);
                robotAtraidoJoint = null;
                robotSiendoAtraido = null;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_0)) {
            long currentTime = System.currentTimeMillis();
            if (puedeActivarIman && (currentTime - lastImanPressTime <= DOUBLE_PRESS_IMAN_TIME)) {
                imanActivo = true;
                puedeActivarIman = false; // desactivar posibilidad de volver a activarlo
                tiempoImanActivo = 0f;
                tiempoCooldownIman = 0f;
                Gdx.app.log("Tails", "Imán ACTIVADO");
            }
            lastImanPressTime = currentTime;
        }

        if (imanActivo && robotAtraidoJoint == null) {
            this.world.QueryAABB(new QueryCallback() {
                                     @Override
                                     public boolean reportFixture(Fixture fixture) {
                                         if (fixture.getUserData() instanceof Robot) {
                                             Robot robot = (Robot) fixture.getUserData();

                                             if (robot.ko && robot.body != null) {
                                                 Vector2 tailsPos = body.getPosition();
                                                 Vector2 robotPos = robot.body.getPosition();
                                                 float distance = tailsPos.dst(robotPos);

                                                 if (distance < radioAtraccionIman) {
                                                     if (robotAtraidoJoint == null) {
                                                         MouseJointDef md = new MouseJointDef();
                                                         md.bodyA = body;
                                                         md.bodyB = robot.body;
                                                         md.collideConnected = false;
                                                         md.target.set(tailsPos.x, tailsPos.y + robotOffsetDistance);
                                                         md.maxForce = robot.body.getMass() * 100f;
                                                         md.frequencyHz = 8f;
                                                         md.dampingRatio = 0.8f;

                                                         robotAtraidoJoint = (MouseJoint) Tails.this.world.createJoint(md);
                                                         robotSiendoAtraido = robot;

                                                         Gdx.app.log("Tails", "Robot KO detectado y enganchado con MouseJoint.");
                                                     }
                                                     return false;
                                                 }
                                             }
                                         }
                                         return true;
                                     }
                                 },
                body.getPosition().x - radioAtraccionIman,
                body.getPosition().y - radioAtraccionIman,
                body.getPosition().x + radioAtraccionIman,
                body.getPosition().y + radioAtraccionIman);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            body.setLinearVelocity(0, velocidad.y);
            arr = true;
            presionando = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            body.setLinearVelocity(0, -velocidad.y);
            abj = true;
            presionando = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            body.setLinearVelocity(-velocidad.x, body.getLinearVelocity().y);
            izq = true;
            presionando = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            body.setLinearVelocity(velocidad.x, body.getLinearVelocity().y);
            der = true;
            presionando = true;
        }
        if (!presionando) {
            body.setLinearVelocity(0, 0);
            frameActual = sprite;
            stateTime = 0f;
        }
        super.actualizar(delta);
    }




    public void dibujarIman(SpriteBatch batch) {
        if (imanActivo && imanAnimation != null) {
            TextureRegion currentImanFrame = imanAnimation.getKeyFrame(stateTime, true);

            float imanWidth = currentImanFrame.getRegionWidth() / Constantes.PPM;
            float imanHeight = currentImanFrame.getRegionHeight() / Constantes.PPM;

            float tailsCenterX = body.getPosition().x;
            // Ajustar esta posición de Y para que el imán se vea claramente
            // Aquí lo posicionamos un poco por encima de Tails y ligeramente a la derecha.
            float tailsCenterY = body.getPosition().y + (sprite.getHeight() / 2f / Constantes.PPM) + 0.1f; // Ajuste en Y

            // Si hay un robot atraído, podemos hacer que el imán tenga un ligero desplazamiento fijo
            // o simplemente que orbite como antes, pero siempre visible.
            // Para asegurar visibilidad, vamos a hacer que orbite alrededor de Tails.
            // La lógica para que se dibujara "sobre el robot atraído" la quitamos de aquí.

            float angleRadians = (float) Math.toRadians(imanOrbitAngle);

            float orbitOffsetX = (float) (imanOrbitRadius * Math.cos(angleRadians));
            float orbitOffsetY = (float) (imanOrbitRadius * Math.sin(angleRadians));

            // Dibujamos el imán siempre orbitando alrededor de Tails cuando el imán está activo.
            // La posición del robot atraído no influye en la posición de dibujado del imán.
            float drawX = tailsCenterX + orbitOffsetX - (imanWidth / 2f);
            float drawY = tailsCenterY + orbitOffsetY - (imanHeight / 2f);

            float rotationOffset = 0f; // Puedes añadir una rotación si deseas
            float rotationAngle = imanOrbitAngle + rotationOffset;

            float originX = imanWidth / 2f;
            float originY = imanHeight / 2f;

            batch.draw(currentImanFrame,
                drawX,
                drawY,
                originX,
                originY,
                imanWidth,
                imanHeight,
                1f,
                1f,
                rotationAngle);
        }
    }

    public void setIman() {
        imanActivo = false;
    }

    @Override
    public void dispose() {
        atlas.dispose();
        if (imanAtlas != null) {
            imanAtlas.dispose();
        }
        if (robotAtraidoJoint != null) {
            Tails.this.world.destroyJoint(robotAtraidoJoint);
            robotAtraidoJoint = null;
            robotSiendoAtraido = null;
        }
    }
}
