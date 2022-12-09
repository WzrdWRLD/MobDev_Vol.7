package kz.talipovsn.json_micro;

import android.os.Bundle;
import android.os.StrictMode;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

public class MainActivity extends AppCompatActivity {

    private TextView textView; // Компонент для отображения данных

    //String url = "https://air-quality-api.open-meteo.com/v1/air-quality?latitude=52.28&longitude=76.97&hourly=pm10";

    String url = "https://api.calmradio.com/channels.json"; // Адрес получения JSON - данных

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//         ЭТОТ КУСОК КОДА НЕОБХОДИМ ДЛЯ ТОГО, ЧТОБЫ ОТКРЫВАТЬ САЙТЫ С HTTPS!
        try {
            // Google Play will install latest OpenSSL
            ProviderInstaller.installIfNeeded(getApplicationContext());
            SSLContext sslContext;
            sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            sslContext.createSSLEngine();
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException
                | NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
        // ----------------------------------------------------------------------

        // Разрешаем запуск в общем потоке выполнеия длительных задач (например, чтение с сети)
        // ЭТО ТОЛЬКО ДЛЯ ПРИМЕРА, ПО-НОРМАЛЬНОМУ НАДО ВСЕ В ОТДЕЛЬНЫХ ПОТОКАХ
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        textView = findViewById(R.id.textView);

        onClick(null); // Нажмем на кнопку "Обновить"
    }


    // Кнопка "Обновить"
    public void onClick(View view) {
        System.out.println("***");
        textView.setText(R.string.not_data);
        String json = getHTMLData(url);
        System.out.println(json);
        if (json != null) {
            try {

                textView.setText("");
                JSONArray array = new JSONArray(json);
                int itr = 1;
                for (int i = 0; i < array.length(); i ++) {
                    JSONObject array1 = array.getJSONObject(i);

                    System.out.println(array1);
                    textView.append("Каналы:" + i + "\n");
                    JSONArray array2 = array1.getJSONArray("channels");
                    for (int j = 0; j < array2.length(); j ++) {
                        JSONObject array3 = array2.getJSONObject(j);
                        System.out.println(array3);
                        String arrayel = array3.getString("title");
                        System.out.println(arrayel);
                        textView.append(itr + ") Канал: " + arrayel + "\n" + "\n");
                        itr ++;
                    }
                }
            } catch (Exception e) {
                textView.setText(R.string.error);
            }
        }
    }

    // Метод чтения данных с сети по протоколу HTTP
    public static String getHTMLData(String url) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            int response = conn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                StringBuilder data = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        data.append(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return data.toString();
            } else {
                System.out.println("####");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return null;
    }
}
