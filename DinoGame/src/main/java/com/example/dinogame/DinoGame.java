package com.example.dinogame;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class DinoGame extends Application {

    private static final int WIDTH = 900;
    private static final int HEIGHT = 250;

    private ImageView dino;
    private static final int DINO_SIZE = 45;
    private double dinoX = 80;
    private double dinoY;
    private double velocityY = 0;
    private static final double GRAVITY = 0.9;
    private static final double JUMP_VY = -14;
    private double groundY = HEIGHT - 50;

    private final ArrayList<Group> obstacles = new ArrayList<>();
    private final ArrayList<javafx.scene.Group> clouds = new ArrayList<>();

    private double spawnTimer = 0;
    private double spawnInterval = 1.2;
    private double gameSpeed = 300;

    private boolean running = true;
    private boolean onGround = true;
    private long lastTimeNs = 0;
    private int score = 0;
    private double scoreTimer = 0;

    private Label scoreLabel;
    private Label infoLabel;
    private final Random rand = new Random();

    @Override
    public void start(Stage stage) {
        Pane root = new Pane();
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        scene.setFill(Color.web("#f7f7f7"));

        // === Ground ===
        Rectangle ground = new Rectangle(0, groundY, WIDTH, HEIGHT - groundY);
        ground.setFill(Color.web("#e6e6e6"));
        ground.setStroke(Color.web("#d0d0d0"));

        // === Procedural Clouds (без изображений) ===
        for (int i = 0; i < 4; i++) {
            javafx.scene.Group cloud = new javafx.scene.Group();

            // Основное "тело" облака
            javafx.scene.shape.Circle c1 = new javafx.scene.shape.Circle(30 + rand.nextInt(15), Color.rgb(255, 255, 255, 0.85));
            javafx.scene.shape.Circle c2 = new javafx.scene.shape.Circle(25 + rand.nextInt(10), Color.rgb(255, 255, 255, 0.8));
            javafx.scene.shape.Circle c3 = new javafx.scene.shape.Circle(20 + rand.nextInt(10), Color.rgb(255, 255, 255, 0.9));

            c2.setCenterX(25);
            c3.setCenterX(50);
            c1.setCenterY(rand.nextInt(5));
            c2.setCenterY(-5 + rand.nextInt(8));
            c3.setCenterY(rand.nextInt(5));

            cloud.getChildren().addAll(c1, c2, c3);
            cloud.setLayoutX(rand.nextInt(WIDTH));
            cloud.setLayoutY(20 + rand.nextInt(80));
            cloud.setOpacity(0.8 + rand.nextDouble() * 0.2);
            clouds.add(cloud);
        }


        // === Dino ===
        Image dinoImg = new Image(getClass().getResource("/png-transparent-dinosaur-illustration-google-chrome-guess-the-font-dinosaur-game-nvidia-shield-chrome-angle-white-text.png").toExternalForm());
        dino = new ImageView(dinoImg);
        dino.setFitWidth(DINO_SIZE);
        dino.setFitHeight(DINO_SIZE);
        dinoY = groundY - DINO_SIZE;
        dino.setX(dinoX);
        dino.setY(dinoY);

        // === Score ===
        scoreLabel = new Label("Score: 00000");
        scoreLabel.setFont(Font.font("Consolas", 18));
        scoreLabel.setTextFill(Color.web("#222222"));
        scoreLabel.setLayoutX(WIDTH - 180);
        scoreLabel.setLayoutY(10);

        infoLabel = new Label("Press SPACE or Click to jump — R to restart");
        infoLabel.setFont(Font.font(13));
        infoLabel.setTextFill(Color.web("#444444"));
        infoLabel.setLayoutX(10);
        infoLabel.setLayoutY(10);

        root.getChildren().addAll(clouds);
        root.getChildren().addAll(ground, dino, scoreLabel, infoLabel);

        // === Controls ===
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE) jump();
            else if (e.getCode() == KeyCode.R && !running) resetGame(root);
        });
        scene.setOnMouseClicked(e -> {
            if (running) jump();
            else resetGame(root);
        });

        // === Main Loop ===
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastTimeNs == 0) lastTimeNs = now;
                double dt = (now - lastTimeNs) / 1_000_000_000.0;
                lastTimeNs = now;
                if (running) update(dt, root);
            }
        };
        timer.start();

        stage.setScene(scene);
        stage.setTitle("Dino Runner — JavaFX Edition");
        stage.getIcons().add(new Image(getClass().getResource("/png-transparent-dinosaur-illustration-google-chrome-guess-the-font-dinosaur-game-nvidia-shield-chrome-angle-white-text.png").toExternalForm()));
        stage.setResizable(false);
        stage.show();
    }

    private void update(double dt, Pane root) {
        // === Gravity and Jump ===
        velocityY += GRAVITY;
        dinoY += velocityY;
        if (dinoY >= groundY - DINO_SIZE) {
            dinoY = groundY - DINO_SIZE;
            velocityY = 0;
            onGround = true;
        } else {
            onGround = false;
        }
        dino.setY(dinoY);

        // === Clouds movement ===
        for (javafx.scene.Node cloud : clouds) {
            cloud.setLayoutX(cloud.getLayoutX() - dt * (gameSpeed * 0.2));
            if (cloud.getLayoutX() + 100 < 0) {
                cloud.setLayoutX(WIDTH + rand.nextInt(200));
                cloud.setLayoutY(20 + rand.nextInt(80));
            }
        }


        // === Spawn Obstacles ===
        spawnTimer += dt;
        if (spawnTimer >= spawnInterval) {
            spawnTimer = 0;
            spawnInterval = 0.9 + rand.nextDouble() * 1.0;
            createObstacle(root);
        }

        // === Move Obstacles ===
        double moveX = gameSpeed * dt;
        Iterator<Group> it = obstacles.iterator();
        while (it.hasNext()) {
            Group cactus = it.next();
            cactus.setLayoutX(cactus.getLayoutX() - moveX);

            Bounds b = cactus.getBoundsInParent();
            if (b.getMaxX() < -50) {
                root.getChildren().remove(cactus);
                it.remove();
            } else if (dino.getBoundsInParent().intersects(b)) {
                gameOver(root);
                return;
            }
        }

        // === Dynamic Difficulty ===
        scoreTimer += dt;
        if (scoreTimer >= 5.0) {
            scoreTimer = 0;
            gameSpeed += 20;
            if (spawnInterval > 0.6) spawnInterval -= 0.05;
        }

        // === Realistic Scoring ===
        score += (int) (dt * gameSpeed / 6);
        scoreLabel.setText(String.format("Score: %05d", score));
    }

    private void createObstacle(Pane root) {
        int cactusType = rand.nextInt(3); // 0, 1, 2
        Group cactus = new Group();

        Color cactusColor = Color.web("#228B22");
        double baseY = groundY;

        switch (cactusType) {
            case 0 -> {
                Rectangle c1 = new Rectangle(0, baseY - 40, 18, 40);
                c1.setFill(cactusColor);
                cactus.getChildren().add(c1);
            }
            case 1 -> {
                Rectangle c1 = new Rectangle(0, baseY - 35, 16, 35);
                Rectangle c2 = new Rectangle(20, baseY - 50, 16, 50);
                c1.setFill(cactusColor);
                c2.setFill(cactusColor);
                cactus.getChildren().addAll(c1, c2);
            }
            default -> {
                Rectangle c1 = new Rectangle(0, baseY - 40, 16, 40);
                Rectangle c2 = new Rectangle(18, baseY - 55, 16, 55);
                Rectangle c3 = new Rectangle(36, baseY - 35, 16, 35);
                c1.setFill(cactusColor);
                c2.setFill(cactusColor);
                c3.setFill(cactusColor);
                cactus.getChildren().addAll(c1, c2, c3);
            }
        }

        cactus.setLayoutX(WIDTH + 30);
        cactus.setLayoutY(0);

        root.getChildren().add(cactus);
        obstacles.add(cactus);
    }

    private void jump() {
        if (onGround) {
            velocityY = JUMP_VY;
            onGround = false;
        }
    }

    private void gameOver(Pane root) {
        running = false;
        Label over = new Label("GAME OVER — Press R to restart");
        over.setFont(Font.font("Consolas", 18));
        over.setTextFill(Color.RED);
        over.setLayoutX(WIDTH / 2.0 - 180);
        over.setLayoutY(HEIGHT / 2.0 - 10);
        root.getChildren().add(over);
    }

    private void resetGame(Pane root) {
        // удалить все кактусы
        for (Group g : obstacles) root.getChildren().remove(g);
        obstacles.clear();

        // сброс позиций
        dinoY = groundY - DINO_SIZE;
        dino.setY(dinoY);
        velocityY = 0;
        onGround = true;
        spawnTimer = 0;
        spawnInterval = 1.2;
        gameSpeed = 300;
        running = true;
        lastTimeNs = 0;
        score = 0;
        scoreLabel.setText("Score: 00000");

        // удалить текст GAME OVER
        root.getChildren().removeIf(node -> (node instanceof Label) && node != scoreLabel && node != infoLabel);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
