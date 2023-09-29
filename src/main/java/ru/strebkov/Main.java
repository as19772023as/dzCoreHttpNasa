package ru.strebkov;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Main {
    public static final String URL_PICTURE =
            "https://api.nasa.gov/planetary/apod?api_key=mWX1EwU8ZGNU8yTGqJNhKYJjeg6exgigDc11qTEO";

    public static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        CloseableHttpClient client = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .setRedirectsEnabled(false)
                        .build())
                .build();

        HttpGet request = new HttpGet(URL_PICTURE);
        CloseableHttpResponse response = client.execute(request);

        //Преобразуем ответ в Java-объект NasaObject
        NasaPicturu nasaObject = mapper.readValue(response.getEntity().getContent(), NasaPicturu.class);
        //System.out.println(nasaObject.getUrl()); // ссылка на URL картинки
        //System.out.println(nasaObject); // объект

        // Отправляем запрос и получаем ответ с нашей картинкой, скачать картинку
        //CloseableHttpResponse pictureResponse = client.execute(new HttpGet(nasaObject.getUrl()));
        response = client.execute(new HttpGet(nasaObject.getUrl()));

        HttpEntity entity = response.getEntity();

        //Формируем автоматически название для файла
        String[] arr = nasaObject.getUrl().split("/");
        String fileName = arr[arr.length - 1];
        System.out.println("Имя файла: " + fileName);

        //сохраняем в файл
        FileOutputStream fos = new FileOutputStream(fileName);
        entity.writeTo(fos);

        fos.close();
        client.close();
    }
}