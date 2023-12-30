package net.letscode.kart_race.entity;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.UUID;

public class EnemyEntity extends Entity {

    float randomSpeed;
    public EnemyEntity(Vector2 position, Sprite sprite, float randomSpeed) {
        super(position, sprite);
        this.randomSpeed = randomSpeed;
    }

    @Override
    public void drawTick(SpriteBatch batch) {
        this.sprite.setScale(1.2f);
        super.drawTick(batch);
    }

    @Override
    public void tick() {
        this.velocity.y += randomSpeed;
        super.tick();

    }

    public UUID getUUID() {
        return this.uuid;
    }
}
