package io.github.Sonic_Test;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class crearObjetos {
    World world;
    BodyDef bd;
    BodyDef s;
    CircleShape shape;
    PolygonShape suelo;

    public crearObjetos() {
        world = new World(new Vector2(0,-9.8f), true);
        bd = new BodyDef();
        bd.position.set( -14 , 2 );
        bd.type = BodyDef.BodyType.DynamicBody;
        s = new BodyDef();
        s.position.set(0,0);
        s.type = BodyDef.BodyType.StaticBody;
        objetos();

    }

    public void actualizar(float delta) {
        world.step(delta, 8,6);
    }

    public void objetos() {
        shape = new CircleShape();
        shape.setRadius(.1f);
        FixtureDef fixDef = new FixtureDef();
        fixDef.shape = shape;
        Body oBody = world.createBody(bd);
        oBody.createFixture(fixDef);

        PolygonShape sueloShape = new PolygonShape();
        sueloShape.setAsBox(100f, 0.5f); // ancho = 10, alto = 0.5 (mitades)

// Fixture
        FixtureDef fixDef2 = new FixtureDef();
        fixDef2.shape = sueloShape;
        fixDef2.friction = 0.8f; // puedes ajustar la fricción si el personaje resbala

        Body sue = world.createBody(s);
        sue.createFixture(fixDef2);

    }

    public void crearPlataforma(float x, float y) {
        BodyDef body = new BodyDef();
        body.position.set( x , y );
        body.type = BodyDef.BodyType.StaticBody;

        PolygonShape plataforma = new PolygonShape();
        plataforma.setAsBox(2f, 0.4f); // Mitades, porque setAsBox mide desde el centro
        FixtureDef fixDef = new FixtureDef();
        fixDef.shape = plataforma;
        fixDef.friction = 0f; // En la plataforma
        Body oBody = world.createBody(body);
        oBody.createFixture(fixDef);
    }

    public void dispose() {
        shape.dispose();
        suelo.dispose();
    }

}
