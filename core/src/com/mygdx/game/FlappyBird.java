package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.awt.Font;
import java.awt.Shape;
import java.util.Random;

public class FlappyBird extends ApplicationAdapter {

private SpriteBatch bach;
private Texture [] passaros;
private Texture fundo;
private Texture canoBaixo;
private Texture canoTopo;
private Texture gameOver;

private float larguraDisposito;
private float alturaDisposito;

private int estadoJogo=0;

private float variação=0;
private float velocidadeQueda=0;
private float posiçãoInicialVertical =0;
private float posiçãoInicialHorizontal =0;
private float posiçãoHorizontalCano;
private float espaçoCanos = 200;
private float deltaTime;
private Random random;
private float alturaCanos;
private BitmapFont fonte;
private BitmapFont mensagem;
private int pontuação=0;
private boolean marcouPonto;
private Circle passaroHitbox;
private Rectangle canoTopoHitbox;
private Rectangle canoBaixoHitbox;
private ShapeRenderer shape;
private OrthographicCamera camera;
private Viewport viewport;
private final float VIRTUAL_WIDTH = 768;
private final float VIRTUAL_HEIGHT = 1024;



	@Override
	public void create () {
		Gdx.app.log("Create", "Inicializando o jogo");
		bach = new SpriteBatch();
		passaros = new Texture[3];
		passaros [0] = new Texture("passaro1.png");
		passaros [1] = new Texture("passaro2.png");
		passaros [2] = new Texture("passaro3.png");

		canoBaixo = new Texture("cano_baixo.png");
		canoTopo = new Texture("cano_topo.png");
		gameOver = new Texture("game_over.png");

		fundo = new Texture("fundo.png");
		larguraDisposito = VIRTUAL_WIDTH ;
		alturaDisposito = VIRTUAL_HEIGHT;
		posiçãoInicialVertical = alturaDisposito/2;
		posiçãoInicialHorizontal = larguraDisposito/10;

		posiçãoHorizontalCano = larguraDisposito -100;

		random = new Random();

		fonte = new BitmapFont();
		fonte.setColor(Color.WHITE);
		fonte.getData().setScale(6);
		mensagem = new BitmapFont();
		mensagem.setColor(Color.WHITE);
		mensagem.getData().setScale(6);
		canoBaixoHitbox = new Rectangle();
		canoBaixoHitbox = new Rectangle();
		passaroHitbox = new Circle();
		shape = new ShapeRenderer();

		camera = new OrthographicCamera();
		camera.position.set(VIRTUAL_WIDTH/2,VIRTUAL_HEIGHT/2,0);
		viewport = new FitViewport(VIRTUAL_WIDTH,VIRTUAL_HEIGHT, camera);


	}

	@Override
	public void render () {

		camera.update();
		 Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		deltaTime = Gdx.graphics.getDeltaTime();

		variação += deltaTime*10;

		if (variação>2){
			variação=0;

		}

		if (estadoJogo==0){
			if(Gdx.input.justTouched()){
				estadoJogo=1;}
			}else{

				velocidadeQueda++;

			if (posiçãoInicialVertical>0 || velocidadeQueda<0){
				posiçãoInicialVertical = posiçãoInicialVertical - velocidadeQueda;
			}

			if(estadoJogo ==1)
			{posiçãoHorizontalCano -= deltaTime*400;


				if (Gdx.input.justTouched()){
					velocidadeQueda = -15;
				}


				if (posiçãoHorizontalCano<=-100){
					posiçãoHorizontalCano=larguraDisposito;
					alturaCanos = random.nextInt(400) - 200;
					marcouPonto = false;

				}

				if (posiçãoHorizontalCano<120) {
					if (!marcouPonto) {

						pontuação++;
						marcouPonto = true;
					}
				}
			}else if (Gdx.input.justTouched()){
				estadoJogo=0;
				pontuação=0;
				velocidadeQueda=0;
				posiçãoInicialVertical = alturaDisposito/2;
				posiçãoHorizontalCano = larguraDisposito;

			}
		}

		bach.setProjectionMatrix(camera.combined);
		bach.begin();

		bach.draw(fundo,0,0,larguraDisposito,alturaDisposito );
		bach.draw(passaros[(int) variação],posiçãoInicialHorizontal,posiçãoInicialVertical);
		bach.draw(canoBaixo,posiçãoHorizontalCano,alturaDisposito/2 - canoBaixo.getHeight() - espaçoCanos/2 + alturaCanos);
		bach.draw(canoTopo,posiçãoHorizontalCano,alturaDisposito/2 + espaçoCanos/2 + alturaCanos);
		fonte.draw(bach,String.valueOf(pontuação),larguraDisposito/2 -50,alturaDisposito -50);
		if (estadoJogo==2){
			bach.draw(gameOver,larguraDisposito/2 - 200,alturaDisposito/2);
			mensagem.draw(bach,"Continue",larguraDisposito/2 -200,alturaDisposito/2 - gameOver.getHeight()/2);
		}

		bach.end();


		passaroHitbox.set(posiçãoInicialHorizontal + passaros[0].getWidth()/2,posiçãoInicialVertical
				+ passaros[0].getHeight()/2,30);
		canoBaixoHitbox = new Rectangle(posiçãoHorizontalCano,alturaDisposito/2- canoBaixo.getHeight() -
				espaçoCanos/2 + alturaCanos,canoBaixo.getWidth(),canoBaixo.getHeight());

		canoTopoHitbox = new Rectangle(posiçãoHorizontalCano, alturaDisposito/2 + espaçoCanos/2 +
				alturaCanos,canoTopo.getWidth(),canoTopo.getHeight());


		/*shape.begin(ShapeRenderer.ShapeType.Filled);
		shape.circle(passaroHitbox.x,passaroHitbox.y,passaroHitbox.radius);
		shape.rect(canoBaixoHitbox.x,canoBaixoHitbox.y,canoBaixoHitbox.width,canoBaixoHitbox.height);
		shape.rect(canoTopoHitbox.x,canoTopoHitbox.y,canoTopoHitbox.width,canoTopoHitbox.height);
	*/


		shape.end();

		if (Intersector.overlaps(passaroHitbox,canoBaixoHitbox)|| Intersector.overlaps(passaroHitbox,canoTopoHitbox) || posiçãoInicialVertical<=0 ){

			estadoJogo=2;
		}



			}

	@Override
	public void resize(int width, int height) {
		viewport.update(width,height);
	}
}







