package kr.or.womanup.nambu.hjy.bookfinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String END_POINT = "https://dapi.kakao.com/v3/search/book?query=%s&page=%d";
    private static final String API_KEY = "your key";
    SearchView searchView;
    RecyclerView recyclerView;
    BookAdapter adapter;
    int page = 1;
    boolean isEnd = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter = new BookAdapter(this,R.layout.book_item);
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setAdapter(adapter);

        //RecyclerView는 레이아웃매니저 세팅이 필요.
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        //선 그리기
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                DividerItemDecoration.VERTICAL));

        //지금 페이지에서 마지막 책에서 스크롤 될 때 새로운 페이지
        recyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                int totalItemCount = manager.getItemCount();
                int lastVisible = manager.findLastVisibleItemPosition();
                if(lastVisible>=totalItemCount-1){
                    if(isEnd){
                        //alertDialog 마지막 페이지 입니다.
                        return;
                    }
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            page++;
                            String json = search(searchView.getQuery().toString());
                            parsing(json);
                        }
                    });
                    thread.start();
                }
            }
        });

    }

    @Override //메뉴 인플레이션하려면
    public boolean onCreateOptionsMenu(Menu menu) {//안드로이드가 메뉴를 만들어서 매개변수로 넘겨줌
        //거기에 내가 만든 메뉴를 인플레이션을 해서 붙여준다.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        MenuItem searchItem = menu.findItem(R.id.app_bar_search); //서치아이템(메뉴아이템) 가져온 것.
        searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("검색어를 입력하세요:D");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                isEnd = false;
                //메인쓰레드에서 search하면 네트워크OnMain쓰레드에러발생.
                //네트워크는 시간상 오래걸리기 때문에 외부 쓰레드를 만들어 돌려야함
                //쓰레드 만드는법. 1.Tread 상속해서 클래스 만들기 2.Runnable을 매개변수로 넣고 익명클래스 구현
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        page = 1;
                        adapter.clear();
                        String json = search(query);
                        parsing(json);
                        System.out.println(json);
                    }
                });
                thread.start();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    
    //검색 함수 만들기
    public String search(String query) {
        String strURL = String.format(END_POINT, query, page); //포맷에 맞춰 urlString 만들기
        String str;
        String result = null;
        try {
            URL url = new URL(strURL); //Sting을 실제 URL로 바꿔주기
            HttpURLConnection con = (HttpURLConnection)url.openConnection(); //URLConnection을 반환하니까 캐스팅
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization","KakaoAK "+API_KEY);
            con.connect();
            if(con.getResponseCode()==con.HTTP_OK){
                InputStreamReader streamReader = new InputStreamReader(con.getInputStream()); //스트림에 리더 연결
                BufferedReader reader = new BufferedReader(streamReader); //리더에 버퍼 리더 연결
                StringBuffer buffer = new StringBuffer(); //임시저장용
                while ((str = reader.readLine())!=null){
                    buffer.append(str);
                }
                result = buffer.toString();
            }
        }catch (IOException e){
            System.out.println("예외 발생");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    //{}:JSON Object, []:JSON Array
    void parsing(String json){
        try{
            JSONObject root = new JSONObject(json);
            JSONObject meta = root.getJSONObject("meta");
            JSONArray documents = root.getJSONArray("documents");
            isEnd = meta.getBoolean("is_end");

            for(int i=0;i<documents.length();i++){
                JSONObject book = documents.getJSONObject(i);
                String title = book.getString("title");
                JSONArray tmpAuthors = book.getJSONArray("authors");
                String authors = "";
                for(int j=0;j<tmpAuthors.length();j++){
                    authors+=tmpAuthors.getString(j);
                    if(j<(tmpAuthors.length()-1)){ authors+=", ";}
                }
                String publisher = book.getString("publisher");
                String contents = book.getString("contents");
                String url = book.getString("url");
                String isbn = book.getString("isbn");
                String date = book.getString("datetime");

                JSONArray tmpTrans = book.getJSONArray("translators");
                String translators="";
                for(int j=0;j<tmpTrans.length();j++){
                    translators+=tmpTrans.getString(j);
                    if(j<(tmpTrans.length()-1)){ translators+=", ";}
                }

                int price = book.getInt("price");
                int salePrice = book.getInt("sale_price");
                String thumbnail = book.getString("thumbnail");
                String status = book.getString("status");

                Book newBook = new Book(title,contents,url,isbn,date,authors,publisher,
                        translators,price,salePrice,thumbnail,status);
                adapter.addItem(newBook);
            }
            recyclerView.post(new Runnable() {
                //러너블을 메인 쓰레드로 보내줌. UI변경은 메인쓰레드에서 가능.
                @Override
                public void run() {
                    adapter.notifyDataSetChanged(); //이 함수를 메인에서 실행시켜야함
                }
            });
            //adapter.notifyDataSetChanged(); //어뎁터에 데이터 셋 끝난걸 알려주기 여기서 하면 오류남
        }catch (JSONException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}