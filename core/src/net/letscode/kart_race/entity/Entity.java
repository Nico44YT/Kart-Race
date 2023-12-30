package net.letscode.kart_race.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.UUID;

public class Entity {

    Vector2 position;

    Vector2 velocity = new Vector2();
    public Sprite sprite;
    UUID uuid;

    public Entity(Vector2 position, Sprite sprite) {
        this.position = position;
        this.uuid = UUID.randomUUID();
        this.sprite = sprite;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(float x, float y) {
        this.position = new Vector2(x,y);
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }

    public void setVelocity(float x, float y) {
        this.velocity = new Vector2(x,y);
    }

    public boolean collidesWith(Entity otherEntity) {
        float thisX = getPosition().x;
        float thisY = getPosition().y;
        float thisWidth = getSprite().getWidth();
        float thisHeight = getSprite().getHeight();

        float otherX = otherEntity.getPosition().x;
        float otherY = otherEntity.getPosition().y;
        float otherWidth = otherEntity.getSprite().getWidth();
        float otherHeight = otherEntity.getSprite().getHeight();

        // Check for collision
        return thisX < otherX + otherWidth &&
                thisX + thisWidth > otherX &&
                thisY < otherY + otherHeight &&
                thisY + thisHeight > otherY;
    }

    public void tick() {
        position.x = position.x + velocity.x * Gdx.graphics.getDeltaTime();
        position.y = position.y + velocity.y * Gdx.graphics.getDeltaTime();

        if(position.x < 0) position.x = 0;
        if(position.x > Gdx.graphics.getWidth() - sprite.getWidth()) position.x = Gdx.graphics.getWidth() - sprite.getWidth();

        /*if(position.y < 0) position.y = 0;
        if(position.y > Gdx.graphics.getHeight() - sprite.getHeight()) position.y = Gdx.graphics.getHeight() - sprite.getHeight();*/

        velocity.x = 0;
        velocity.y = 0;
    }

    public void drawTick(SpriteBatch batch) {
        sprite.setPosition(position.x, position.y);
        sprite.draw(batch);
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }
    public Sprite getSprite() {
        return this.sprite;
    }

}
