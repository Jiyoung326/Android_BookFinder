package kr.or.womanup.nambu.hjy.bookfinder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Book implements Serializable {
    String title;
    String contents;
    String url; //상세 정보 url
    String isbn;
    String date;
    String authors;
    String publisher;
    String translators;
    int price;
    int salePrice;
    String thumbnail;
    String status;

    public Book(String title, String contents, String url, String isbn, String date,
                String authors, String publisher, String translators, int price,
                int salePrice, String thumbnail, String status) {
        this.title = title;
        this.contents = contents;
        this.url = url;
        this.isbn = isbn;
        this.date = date;
        this.authors = authors;
        this.publisher = publisher;
        this.translators = translators;
        this.price = price;
        this.salePrice = salePrice;
        this.thumbnail = thumbnail;
        this.status = status;
    }


}
