package io.github.Sonic_V0.Personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch; // Añadido para dibujarIman
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import io.github.Sonic_V0.Constantes;
import com.badlogic.gdx.utils.Array; // Necesario para TextureAtlas

public class Tails extends Amigas {
    private boolean isFlying = false;
    private float flyTime = 0f;
    private float maxFlyTime = 3f;
    private long lastUpPressTime = 0;
    private static final long DOUBLE_PRESS_TIME = 300;

    private int objetosRecogidos = 0;
    private boolean puedeRecoger = true;
    private float tiempoRecoger = 0f;

    // --- Variables del Imán ---
    private boolean imanActivo = false;
    protected TextureRegion imanSprite; // Se usa para el frame actual, si es necesario antes de la animación
    private TextureAtlas imanAtlas;
    private Animation<TextureRegion> imanAnimation;
    private long lastImanPressTime = 0;
    private static final long DOUBLE_PRESS_IMAN_TIME = 200;
    private float imanOrbitAngle = 0f;
    private float imanOrbitRadius = 0.5f;
    // --- Fin Variables del Imán ---

    private Animation<TextureRegion> volar;
    private Animation<TextureRegion> vueloarriba;
    private Animation<TextureRegion> vueloabajo;
    private Animation<TextureRegion> recoger;


    public Tails(Vector2 posicion, World world) {
        super(posicion, world);
        inicializarAnimaciones(body.getPosition().x, body.getPosition().y);
        this.name = "Tails";
    }

    @Override
    void inicializarAnimaciones(float x, float y) {
        atlas = new TextureAtlas(Gdx.files.internal("SpriteTails/Tails.atlas"));
        sprite = atlas.createSprite("TailsSprite0");
        sprite.setSize(30 / Constantes.PPM, 39f / Constantes.PPM); // Usar Constantes.PPM aquí
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

        // --- Carga del Atlas del Imán ---
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
        // --- Fin Carga del Atlas del Imán ---

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

        // --- Lógica de Actualización del Imán ---
        if (imanActivo) {
            imanOrbitAngle = (imanOrbitAngle + (200f * delta)) % 360f;
        } else {
            imanOrbitAngle = 0f;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastImanPressTime <= DOUBLE_PRESS_IMAN_TIME) {
                imanActivo = !imanActivo;
                Gdx.app.log("Tails", "Imán " + (imanActivo ? "ACTIVADO" : "DESACTIVADO"));
            }
            lastImanPressTime = currentTime;
        }
        // --- Fin Lógica de Actualización del Imán ---


        // LÓGICA DE VUELO
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

        // Detectar barra espaciadora para recoger (solo en modo terrestre)
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && !isFlying) {
            puedeRecoger = false;
            objetosRecogidos++;
        }


        // MOVIMIENTO TERRESTRE
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
                movingHorizontally = true; // Corregido: antes era 'horizontally'
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

    // --- Método para dibujar el imán ---
    public void dibujarIman(SpriteBatch batch) {
        if (imanActivo && imanAnimation != null) {
            TextureRegion currentImanFrame = imanAnimation.getKeyFrame(stateTime, true);

            float imanWidth = currentImanFrame.getRegionWidth() / Constantes.PPM;
            float imanHeight = currentImanFrame.getRegionHeight() / Constantes.PPM;

            float tailsCenterX = body.getPosition().x;
            float tailsCenterY = body.getPosition().y + (sprite.getHeight() / 4f / Constantes.PPM);

            float angleRadians = (float) Math.toRadians(imanOrbitAngle);

            float orbitOffsetX = (float) (imanOrbitRadius * Math.cos(angleRadians));
            float orbitOffsetY = (float) (imanOrbitRadius * Math.sin(angleRadians));

            float drawX = tailsCenterX + orbitOffsetX - (imanWidth / 2f);
            float drawY = tailsCenterY + orbitOffsetY - (imanHeight / 2f);

            float rotationAngle = 0f;

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
    // --- Fin Método para dibujar el imán ---

    @Override
    public void dispose() {
        atlas.dispose();
        // --- Disponer el atlas del imán ---
        if (imanAtlas != null) {
            imanAtlas.dispose();
        }
        // --- Fin Disponer el atlas del imán ---
    }
}
