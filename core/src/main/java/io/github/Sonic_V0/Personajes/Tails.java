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
    private boolean isFlying = false;
    private float flyTime = 0f;
    private float maxFlyTime = 3f;
    private long lastUpPressTime = 0;
    private static final long DOUBLE_PRESS_TIME = 300;

    private int objetosRecogidos = 0;
    private boolean puedeRecoger = true;
    private float tiempoRecoger = 0f;

    private boolean imanActivo = false;
    private float radioAtraccionIman = 1.8f;
    protected TextureRegion imanSprite;
    private TextureAtlas imanAtlas;
    private Animation<TextureRegion> imanAnimation;
    private long lastImanPressTime = 0;
    private static final long DOUBLE_PRESS_IMAN_TIME = 200;

    private float imanOrbitAngle = 0f;
    private float imanOrbitRadius = 0.5f;

    private float robotOffsetDistance = 0.1f;

    private Animation<TextureRegion> volar;
    private Animation<TextureRegion> vueloarriba;
    private Animation<TextureRegion> vueloabajo;
    private Animation<TextureRegion> recoger;

    private MouseJoint robotAtraidoJoint;
    private Robot robotSiendoAtraido;

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

        volar = crearAnimacion("vuelo", 7, 0.1f);
        vueloabajo = crearAnimacion("vuelo", 7, 0.1f);
        vueloarriba = crearAnimacion("vuelo", 3, 0.1f);

        recoger = crearAnimacion("recoger", 4, 0.1f);

        try {
            imanAtlas = new TextureAtlas(Gdx.files.internal("SpriteTails/iman.atlas"));
            Array<TextureRegion> imanFrames = new Array<>();
            for (int i = 1; i <= 4; i++) {
                TextureRegion frame = imanAtlas.findRegion("iman" + i);
                if (frame != null) {
                    imanFrames.add(frame);
                } else {
                    Gdx.app.error("Tails", "¡Advertencia! No se encontró el frame 'iman" + i + "' en el atlas del imán.");
                }
            }

            if (imanFrames.size > 0) {
                imanAnimation = new Animation<>(0.15f, imanFrames, Animation.PlayMode.LOOP);
                imanSprite = imanAnimation.getKeyFrame(0);
            } else {
                Gdx.app.error("Tails", "No se pudieron cargar frames para la animación del imán. imanAnimation y imanSprite serán nulos.");
                imanAnimation = null;
                imanSprite = null;
            }
        } catch (Exception e) {
            Gdx.app.error("Tails", "Error al cargar 'SpriteTails/iman.atlas': " + e.getMessage());
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

        if (!puedeRecoger) {
            tiempoRecoger += delta;
            frameActual = recoger.getKeyFrame(tiempoRecoger, false);

            if (tiempoRecoger >= 0.4f) {
                puedeRecoger = true;
                tiempoRecoger = 0f;
            }

            sprite.setPosition(
                body.getPosition().x - sprite.getWidth() / 2f,
                body.getPosition().y - sprite.getHeight() / 2f
            );
            return;
        }

        izq = der = abj = arr = false;
        boolean presionando = false;
        stateTime += delta;

        if (imanActivo) {
            imanOrbitAngle = (imanOrbitAngle + (200f * delta)) % 360f;

            if (robotAtraidoJoint != null && robotSiendoAtraido != null && robotSiendoAtraido.body != null) {
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

        } else {
            imanOrbitAngle = 0f;
            if (robotAtraidoJoint != null) {
                Tails.this.world.destroyJoint(robotAtraidoJoint);
                robotAtraidoJoint = null;
                robotSiendoAtraido = null;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastImanPressTime <= DOUBLE_PRESS_IMAN_TIME) {
                imanActivo = !imanActivo;
                Gdx.app.log("Tails", "Imán " + (imanActivo ? "ACTIVADO" : "DESACTIVADO"));

                if (!imanActivo && robotAtraidoJoint != null) {
                    Tails.this.world.destroyJoint(robotAtraidoJoint);
                    robotAtraidoJoint = null;
                    robotSiendoAtraido = null;
                    Gdx.app.log("Tails", "Robot soltado.");
                }
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

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastUpPressTime <= DOUBLE_PRESS_TIME) {
                isFlying = true;
                flyTime = 0f;
            }
            lastUpPressTime = currentTime;
        }

        if (isFlying) {
            flyTime += delta;

            if (flyTime >= maxFlyTime) {
                isFlying = false;
            } else {
                float velX = body.getLinearVelocity().x * 0.9f;
                float velY = 0;

                if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                    velY = -velocidad.y * 0.8f;
                    frameActual = vueloabajo.getKeyFrame(stateTime, true);
                    abj = true;
                } else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                    velY = velocidad.y * 1.8f;
                    frameActual = vueloarriba.getKeyFrame(stateTime, true);
                    arr = true;
                } else {
                    frameActual = volar.getKeyFrame(stateTime, true);
                }

                if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                    velX = -velocidad.x * 0.7f;
                    if (!frameActual.isFlipX()) frameActual.flip(true, false);
                    izq = true;
                } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                    velX = velocidad.x * 0.7f;
                    if (frameActual.isFlipX()) frameActual.flip(true, false);
                    der = true;
                }

                body.setLinearVelocity(velX, velY);
                presionando = true;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && !isFlying) {
            puedeRecoger = false;
            objetosRecogidos++;
        }

        if (!isFlying) {
            float velX = 0, velY = 0;
            boolean movingHorizontally = false;
            boolean movingVertically = false;

            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                velY = velocidad.y;
                arr = true;
                movingVertically = true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                velY = -velocidad.y;
                abj = true;
                movingVertically = true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                velX = -velocidad.x;
                izq = true;
                movingHorizontally = true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                velX = velocidad.x;
                der = true;
                movingHorizontally = true;
            }

            body.setLinearVelocity(velX, velY);
            presionando = movingHorizontally || movingVertically;

            if (presionando) {
                if (movingHorizontally && movingVertically) {
                    if (arr) {
                        frameActual = diagonalarr.getKeyFrame(stateTime, true);
                    } else if (abj) {
                        frameActual = diagonalabj.getKeyFrame(stateTime, true);
                    }
                    if (izq && !frameActual.isFlipX()) {
                        frameActual.flip(true, false);
                    } else if (der && frameActual.isFlipX()) {
                        frameActual.flip(true, false);
                    }
                } else if (movingHorizontally) {
                    frameActual = correr.getKeyFrame(stateTime, true);
                    if (izq && !frameActual.isFlipX()) {
                        frameActual.flip(true, false);
                    } else if (der && frameActual.isFlipX()) {
                        frameActual.flip(true, false);
                    }
                } else if (movingVertically) {
                    if (arr) {
                        frameActual = arriba.getKeyFrame(stateTime, true);
                    } else if (abj) {
                        frameActual = abajo.getKeyFrame(stateTime, true);
                    }
                }
            }
        }

        if (!presionando) {
            body.setLinearVelocity(body.getLinearVelocity().x * 0.5f, body.getLinearVelocity().y * 0.5f);
            if (!isFlying) {
                frameActual = sprite;
                if (frameActual.isFlipX()) {
                    frameActual.flip(true, false);
                }
            }
        }

        sprite.setPosition(
            body.getPosition().x - sprite.getWidth() / 2f,
            body.getPosition().y - sprite.getHeight() / 2f
        );
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
