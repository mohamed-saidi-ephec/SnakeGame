import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener {

    private final int width;
    private final int height;
    private final int cellSize;
    private final Random random = new Random();
    private static final int FRAME_RATE = 20;
    private boolean gameStarted = false;
    private boolean gameOver = false;
    private GamePoint food;
    private Direction direction = Direction.RIGHT;
    private Direction newDirection = Direction.RIGHT;
    private final LinkedList<GamePoint> snake = new LinkedList<>();



    public SnakeGame(final int width, final int height) {
        super();
        this.height = height;
        this.width = width;
        this.cellSize = width / (FRAME_RATE * 2);
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.black);
    }

    public void startGame(){
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        requestFocusInWindow();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final  KeyEvent e) {
                handleKeyEvent(e.getKeyCode());
            }
        });

        new Timer(2000/FRAME_RATE, this).start();
    }

    private void handleKeyEvent(final  int keyCode) {
        if (!gameStarted){
            if (keyCode == KeyEvent.VK_SPACE){
                gameStarted = true;
                resetGameData();
            }

        } else if (!gameOver) {
            switch (keyCode){
                case KeyEvent.VK_UP:
                  newDirection =  Direction.UP;
                  break;

                case KeyEvent.VK_DOWN:
                    newDirection = Direction.DOWN;
                    break;

                case KeyEvent.VK_LEFT:
                    newDirection = Direction.LEFT;
                    break;

                case KeyEvent.VK_RIGHT:
                    newDirection = Direction.RIGHT;
                    break;
            }

        } else if (keyCode == KeyEvent.VK_SPACE ) {
            gameStarted =false;
            gameOver = false;
            resetGameData();
            
        }

    }

    //todo 20:30 pour la video
    private void resetGameData(){
        snake.clear();
        snake.add(new GamePoint(width /2,height/2));
        generateFood();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (!gameStarted) {
            graphics.setColor(Color.RED);
            graphics.setFont(new Font("Arial", Font.BOLD, 48));
            graphics.drawString("SnakeGame", 270, 300);
            graphics.setFont(new Font("Arial", Font.BOLD, 30));
            graphics.setColor(Color.WHITE);
            graphics.drawString("Appuyez sur espace", 255, 450);
        }else {
            graphics.setColor(Color.green);
            //for each point in our snake we make ...
            Color snakeColor = Color.green;
            for (final var point : snake){
                graphics.setColor(snakeColor);
                graphics.fillRect(point.x,point.y,cellSize,cellSize);
                final int newGreen = (int) Math.round(snakeColor.getGreen() * (0.95));
                snakeColor = new Color(0,newGreen,0);

            }

            graphics.setColor(Color.cyan);
            graphics.fillOval(food.x,food.y,cellSize,cellSize);

            if (gameOver){
                int score = snake.size() - 2;
                graphics.setFont(new Font("Arial", Font.BOLD, 30));
                graphics.setColor(Color.WHITE);
                graphics.drawString("score : " + score, 255, 450);
            }
        }
    }

    private void move() {
         final GamePoint currentHead = snake.getFirst();
        final GamePoint newHead = switch (direction) {
            case UP -> new GamePoint(currentHead.x,currentHead.y -cellSize);
            case DOWN -> new GamePoint(currentHead.x, currentHead.y +cellSize);
            case LEFT -> new GamePoint(currentHead.x - cellSize,currentHead.y );
            case RIGHT ->  new GamePoint(currentHead.x + cellSize,currentHead.y);
        };
        snake.addFirst(newHead);

        if (newHead.equals(food)){
            generateFood();
        }else if (checkCollision()){
            gameOver = true;
        }else {
            snake.removeLast();
        }



        direction = newDirection;


    }


    private boolean checkCollision(){
        final GamePoint head = snake.getFirst();
        final var invalidWidth = (head.x<0) || (head.x >= width);
        final var invalidHead = (head.y<0) || (head.y >= height);
        if (invalidHead || invalidWidth){
            return true;
        }
        return snake.size() != new HashSet<>(snake).size();


    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        if (gameStarted && !gameOver){
        move();
        }
        repaint();
    }

    private void  generateFood(){
        do {
            food = new GamePoint(random.nextInt(width/cellSize)*cellSize,
                    random.nextInt(height/cellSize)*cellSize);
        }while (snake.contains(food));

    }

    private record GamePoint(int x, int y){

    }


    private enum Direction {
        UP,DOWN, RIGHT, LEFT
    }
}


