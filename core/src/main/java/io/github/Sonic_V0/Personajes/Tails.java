package io.github.Sonic_V0.Personajes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;

public class Tails extends Amigas {
    private boolean isFlying = false;
    private float flyTime = 0f;
    private float maxFlyTime = 3f; // Tiempo máximo de vuelo en segundos
    private long lastUpPressTime = 0;
    private static final long DOUBLE_PRESS_TIME = 300;

    // Sistema de recoger objetos
    private int objetosRecogidos = 0;
    private boolean puedeRecoger = true;
    private float tiempoRecoger = 0f;


    private Animation<TextureRegion> volar; // Animación de vuelo
    private Animation<TextureRegion> vueloarriba;
    private Animation<TextureRegion> vueloabajo;
    private Animation<TextureRegion> recoger;


    public Tails(Body body) {
        super(body);
        inicializarAnimaciones(body.getPosition().x, body.getPosition().y);
        this.name = "Tails";
    }

    @Override
    void inicializarAnimaciones(float x, float y) {
        atlas = new TextureAtlas(Gdx.files.internal("SpriteTails/Tails..atlas"));
        sprite = atlas.createSprite("TailsSprite0");
        sprite.setSize(30 / PPM, 39f / PPM);
        sprite.setPosition(
            x - sprite.getWidth() / 2f,
            y - sprite.getHeight() / 2f
        );

        // Animaciones normales
        correr = crearAnimacion("TailsSprite", 7, 0.09f);
        abajo = crearAnimacion("abajo", 4, 0.1f);
        arriba = crearAnimacion("arriba", 6, 0.1f);
        diagonalarr = crearAnimacion("diagonal", 5, 0.1f);
        diagonalabj = crearAnimacion("diagonalabj", 3, 0.1f);

        // Animaciones de vuelo
        volar = crearAnimacion("vuelo", 7, 0.1f);
        vueloabajo = crearAnimacion("vuelo", 7, 0.1f);
        vueloarriba = crearAnimacion("vuelo", 7, 0.1f);

        // Animación para recoger objetos
        recoger = crearAnimacion("recoger", 4, 0.1f);

        frameActual = new TextureRegion();
    }

    @Override
    public void actualizar(float delta) {
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

        // Si no se está presionando ninguna tecla
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

    public int getObjetosRecogidos() {
        return objetosRecogidos;
    }

    @Override
    public void dispose() {
        atlas.dispose();
    }
}
