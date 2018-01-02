package ru.dol.drg.happynewyear2018;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        winPoints = Calendar.getInstance().get(Calendar.YEAR);
        mainImageView = (ImageView)findViewById(R.id.imageView);
        startButton = (ImageView)findViewById(R.id.imageView1);
        snowflake = (ImageView)findViewById(R.id.imageView2);
        textViewPoints = (TextView)findViewById(R.id.textViewPoints);
        showPoints();
        textViewLevel = (TextView)findViewById(R.id.textViewLevel);
        showLevel();
    }

    @Override
    protected void onDestroy() {
        snowflakeTimerDestroy();
        snowflake.setVisibility(View.INVISIBLE);
        startButton.setVisibility(View.VISIBLE);
        mediaPlayerDestroy();
        snowflakeMediaPlayerDestroy();
        victoryMediaPlayerDestroy();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startButton.setVisibility(View.VISIBLE);
        snowflake.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStop() {
        snowflakeTimerDestroy();
        snowflake.setVisibility(View.INVISIBLE);
        startButton.setVisibility(View.VISIBLE);
        mediaPlayerDestroy();
        snowflakeMediaPlayerDestroy();
        victoryMediaPlayerDestroy();
        super.onStop();
    }

    /**
     * Удаление объекта таймера
     */
    void snowflakeTimerDestroy() {
        if (snowflakeTimer != null) {
            snowflakeTimer.cancel();
            snowflakeTimer = null;
        }
    }

    /**
     * Реакция на нажатие кнопки "Начало" = начало игры
     * @param v
     */
    public void doStart(View v) {
        if (points >= winPoints) {
            return;
        }

        random = new Random();

        startButton.setVisibility(View.INVISIBLE);

        playNextSong();

        snowflake.setVisibility(View.VISIBLE);
        startSnowflakeTimer();
    }

    /**
     * Реакция на нажатие снежинки
     * @param view
     */
    public void snowflakeClick(View view) {
        snowflakeTimerDestroy();
        snowflakeSound();
        points += 10;
        if (points > winPoints) {
            points = winPoints;
        }
        showPoints();

        if (points < winPoints) {
            if (points % 100 == 0) {
                if (level < 9) {
                    level++;
                    showLevel();
                    snowflakeTimerPeriod -= 50;
                }
            }
            startSnowflakeTimer();
        }
        else {
            snowflake.setVisibility(View.INVISIBLE);
            textViewLevel.setText("Победа!!!");
            victorySound();
        }
    }

    /**
     * Проиграть марш победы
     */
    private void victorySound() {
        snowflakeMediaPlayerDestroy();
        victoryMediaPlayerDestroy();
        mediaPlayerDestroy();

        victoryMediaPlayer = MediaPlayer.create(this, R.raw.christmas_fun);
        victoryMediaPlayer.start();
        victoryMediaPlayer.setLooping(true);
    }

    /**
     * Звук при нажатии на снежинку
     */
    private void snowflakeSound() {
        snowflakeMediaPlayerDestroy();
        snowflakeMediaPlayer = MediaPlayer.create(this, R.raw.schademans_pipe9);
        snowflakeMediaPlayer.setOnCompletionListener(new OnCompletionListener()
        {
            @Override
            public void onCompletion(MediaPlayer mp) {
                snowflakeMediaPlayerDestroy();
            }
        });
        snowflakeMediaPlayer.start();
    }

    /**
     * Остановить медиаплейер нажатия на снежинку и удалить его объект
     */
    void snowflakeMediaPlayerDestroy() {
        if (snowflakeMediaPlayer != null) {
            snowflakeMediaPlayer.stop();
            snowflakeMediaPlayer.release();
            snowflakeMediaPlayer = null;
        }
    }

    /**
     * Остановить медиаплейер победного марша и удалить его объект
     */
    void victoryMediaPlayerDestroy() {
        if (victoryMediaPlayer != null) {
            victoryMediaPlayer.stop();
            victoryMediaPlayer.release();
            victoryMediaPlayer = null;
        }
    }

    /**
     * Запуск таймера перемещения снежинки
     */
    void startSnowflakeTimer() {
        snowflakeTimerDestroy();
        snowflakeTimer = new Timer();
        snowflakeTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        snowflake.setX(getSnowflakeRandomLeft());
                        snowflake.setY(getSnowflakeRandomTop());
                    }
                });
            }
        }, 0, snowflakeTimerPeriod);
    }

    /**
     * Остановить основной медиаплейер и удалить его объект
     */
    void mediaPlayerDestroy() {
        if (mediaPlayer != null) {
            currentPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    /**
     * Проиграть следующую фоновую мелодию
     */
    void playNextSong() {
        mediaPlayerDestroy();
        if (currentSong < 0) {
            Random rnd = new Random();
            currentSong = rnd.nextInt(songs.length);
        }
        else if (currentPosition == 0) {
            currentSong++;
            if (currentSong >= songs.length)
                currentSong = 0;
        }
        mediaPlayer = MediaPlayer.create(this, songs[currentSong]);
        mediaPlayer.setOnCompletionListener(new OnCompletionListener()
        {
            @Override
            public void onCompletion(MediaPlayer mp) {
                currentPosition = 0;
                playNextSong();
            }
        });
        if (currentPosition > 0) {
            mediaPlayer.seekTo(currentPosition);
        }
        mediaPlayer.start();
    }

    /**
     * Получить случайную x-координату снежинки
     * @return
     */
    int getSnowflakeRandomLeft() {
        return random.nextInt(mainImageView.getWidth() - snowflake.getWidth());
    }

    /**
     * Получить случайную y-координату снежинки
     * @return
     */
    int getSnowflakeRandomTop() {
        return random.nextInt(mainImageView.getHeight() - textViewPoints.getHeight() - snowflake.getHeight()) + textViewPoints.getHeight();
    }

    /**
     * Отобразить набранные очки
     */
    void showPoints() {
        textViewPoints.setText("Очки: " + Integer.toString(points));
    }

    /**
     * Отобразить уровень игры
     */
    void showLevel() {
        textViewLevel.setText("Уровень: " + Integer.toString(level));
    }

    /**
     * Медиаплейер для фоновых мелодий
     */
    MediaPlayer mediaPlayer = null;

    /**
     * Медиаплейер для звука нажатия снежинки
     */
    MediaPlayer snowflakeMediaPlayer = null;

    /**
     * Медиаплейер для марша победы
     */
    MediaPlayer victoryMediaPlayer = null;

    /**
     * Фоновые мелодии
     */
    int[] songs = {
            R.raw.jingle_bells,
            R.raw.abba_happy_new_year
    };

    /**
     * Текущая фоновая мелодия
     */
    int currentSong = -1;

    /**
     * Положение воспроизведения в текущей фоновой мелодии
     */
    int currentPosition = 0;

    /**
     * Елка
     */
    ImageView mainImageView;

    /**
     * Снежинка
     */
    ImageView snowflake;

    /**
     * Кнопка "Начало игры"
     */
    ImageView startButton;

    /**
     * Поле для вывода очков
     */
    TextView textViewPoints;

    /**
     * Поле для вывода уровня игры
     */
    TextView textViewLevel;

    /**
     * Количество очков
     */
    int points = 0;

    /**
     * Генератор для вычисления случайных координат снежинки
     */
    Random random;

    /**
     * Таймер для перемещения снежинки
     */
    Timer snowflakeTimer = null;

    /**
     * Начальное значение задержки для перемещения снежинки
     */
    int snowflakeTimerPeriod = 1000;

    /**
     * Начальный уровень игры
     */
    int level = 0;

    /**
     * Начальное количество очков
     */
    int winPoints = 0;
}
