package net.letscode.kart_race;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import net.letscode.kart_race.entity.EnemyEntity;
import net.letscode.kart_race.entity.PlayerEntity;

import java.util.*;

public class KartRace extends Game {
	SpriteBatch batch;
	Texture playerTexture;
	Texture enemyTexture;
	Texture backgroundTexture;
	private float backgroundPos = 0;
	private float speed = 50f;
	private int score = 0;

	private Timer spawnTimer;
	private float initialEnemySpawnInterval = 2f; // Initial spawn interval
	private float enemySpawnInterval = initialEnemySpawnInterval; // Initial spawn interval
	private float minEnemySpawnInterval = 1f;    // Minimum spawn interval
	private float initialEnemySpeed = 50f;         // Initial enemy speed
	private float maxEnemySpeed = 150f;            // Maximum enemy speed
	private float timeElapsed = 0f;

	private OrthographicCamera camera;
	public static final int GAME_WORLD_WIDTH = 162;
	public static final int GAME_WORLD_HEIGHT = 240;

	static Sound hitSound;

	static BitmapFont font;
	static GlyphLayout layout = new GlyphLayout();

	PlayerEntity player = new PlayerEntity(new Vector2(0,0), null);

	public enum PossiblePositions {
		LEFT, MIDDLE, RIGHT;
	}

	List<EnemyEntity> enemyList = new ArrayList<EnemyEntity>();
	HashMap<PossiblePositions, Float> positionMap = new HashMap<PossiblePositions, Float>();
	PossiblePositions currentPosition = PossiblePositions.MIDDLE;

	@Override
	public void create() {
		batch = new SpriteBatch();

		font = new BitmapFont(Gdx.files.internal("font/font.fnt"), false);
		font.setColor(Color.WHITE);
		font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		font.getData().setScale(0.25f);

		hitSound = Gdx.audio.newSound(Gdx.files.internal("sounds/hit.ogg"));

		backgroundTexture = new Texture("textures/background.png");
		backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

		enemyTexture = new Texture("textures/cone.png");
		playerTexture = new Texture("textures/car.png");
		player.setSprite(new Sprite(playerTexture));

		float playerMiddlePos = (GAME_WORLD_WIDTH - player.sprite.getWidth())/2;
		player.setPosition(new Vector2(playerMiddlePos, 20));

		positionMap.put(PossiblePositions.LEFT, playerMiddlePos - 43);
		positionMap.put(PossiblePositions.MIDDLE, playerMiddlePos);
		positionMap.put(PossiblePositions.RIGHT, playerMiddlePos + 43);

		camera = new OrthographicCamera(GAME_WORLD_WIDTH, GAME_WORLD_HEIGHT);
		camera.setToOrtho(false, GAME_WORLD_WIDTH, GAME_WORLD_HEIGHT);
		camera.position.set(GAME_WORLD_WIDTH / 2f, GAME_WORLD_HEIGHT / 2f, 0);
		camera.update();

		spawnTimer = new Timer();
		scheduleEnemySpawn();
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		batch.setProjectionMatrix(camera.combined);

		if(Gdx.input.isKeyJustPressed(Input.Keys.A) && !Gdx.input.isKeyJustPressed(Input.Keys.D)) {
			switch(currentPosition) {
				case LEFT:
				case MIDDLE: {currentPosition = PossiblePositions.LEFT; break;}
				case RIGHT: {currentPosition = PossiblePositions.MIDDLE; break;}
			}
		} else if(Gdx.input.isKeyJustPressed(Input.Keys.D) && !Gdx.input.isKeyJustPressed(Input.Keys.A)) {
			switch(currentPosition) {
				case RIGHT:
				case MIDDLE: {currentPosition = PossiblePositions.RIGHT; break;}
				case LEFT: {currentPosition = PossiblePositions.MIDDLE; break;}
			}
		}

		float x = GAME_WORLD_WIDTH-backgroundTexture.getWidth();
		batch.draw(backgroundTexture, x, backgroundPos);
		batch.draw(backgroundTexture, x, backgroundPos+GAME_WORLD_HEIGHT);

		backgroundPos -= speed*Gdx.graphics.getDeltaTime();

		if(backgroundPos + GAME_WORLD_HEIGHT <= 0) {
			backgroundPos = 0;
		}

		for(int i = 0;i<enemyList.size();i++) {
			EnemyEntity enemy = (EnemyEntity) enemyList.toArray()[i];
			enemy.setVelocity(0, -speed);
			enemy.drawTick(batch);
			enemy.tick();

			//Collide
			if(enemy.collidesWith(player)) {
				score -= 1;
				speed /= 2;
				hitSound.play();
				enemyList.remove(i);
			}

			//Despawn
			if(enemy.getPosition().y < -20) {
				score += 1;
				//scoreSound.play(0.5f);
				enemyList.remove(i);
			}
		}

		player.setPosition(positionMap.get(currentPosition), player.getPosition().y);
		player.tick();
		player.drawTick(batch);

		layout.setText(font, String.valueOf(score));

		font.setColor(Color.BLACK);
		font.draw(batch, String.valueOf(score), ((GAME_WORLD_WIDTH - layout.width)/2)+2, GAME_WORLD_HEIGHT-22);

		font.setColor(Color.WHITE);
		font.draw(batch, String.valueOf(score), (GAME_WORLD_WIDTH - layout.width)/2, GAME_WORLD_HEIGHT-20);

		batch.end();
		speed += 0.025f;

		// Update elapsed time
		timeElapsed += Gdx.graphics.getDeltaTime();

		// Adjust spawn interval and enemy speed based on elapsed time
		enemySpawnInterval = Math.max(minEnemySpawnInterval, initialEnemySpawnInterval - timeElapsed * 0.1f);
		initialEnemySpeed = Math.min(maxEnemySpeed, initialEnemySpeed + timeElapsed * 2f);

	}

	private void scheduleEnemySpawn() {
		spawnTimer.scheduleTask(new Timer.Task() {
			@Override
			public void run() {
				enemySpawn();
				scheduleEnemySpawn(); // Schedule the next enemy spawn
			}
		}, enemySpawnInterval);
	}


	void enemySpawn() {
		Random rand = new Random();
		int location = rand.nextInt(0, 3);
		float randomSpeed = 0;

		float x = 0;

		switch(location) {
			case(0): {
				x = positionMap.get(PossiblePositions.LEFT);
				break;
			}
			case(1): {
				x = positionMap.get(PossiblePositions.MIDDLE);
				break;
			}
			case(2): {
				x = positionMap.get(PossiblePositions.RIGHT);
				break;
			}
		}

		EnemyEntity enemy = new EnemyEntity(new Vector2(0, 0), new Sprite(enemyTexture), randomSpeed);
		enemy.setPosition(x, GAME_WORLD_HEIGHT);
		enemyList.add(enemy);

	}
	
	@Override
	public void dispose() {
		batch.dispose();
		hitSound.dispose();
		playerTexture.dispose();
		backgroundTexture.dispose();
	}
}
