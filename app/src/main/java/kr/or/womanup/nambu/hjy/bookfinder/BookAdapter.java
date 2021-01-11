package kr.or.womanup.nambu.hjy.bookfinder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

//뷰홀더 클래스부터 정의하고 구현하면 편함
public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {
    Context context;
    ArrayList<Book> books;
    int layout;

    public BookAdapter(Context context, int layout) {
        this.context = context;
        this.layout = layout;
        //외부에서 넘겨 받으니까 데이터를 하나씩 추가해서 받음.
        //내부에 데이터를 통째로 가지고 있는 경우와 다르다.
        books = new ArrayList<>();
    }

    public void addItem(Book book){
        books.add(book);
    }

    public void clear(){ //새로운 책 검색시, 기존 아이템 뒤에 붙는게 아니라 새로 클리어.
        books.clear();
    }

    @NonNull
    @Override //재활용할 것이 없는 경우 호출됨
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(layout,parent,false);
        ViewHolder holder = new ViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book book = books.get(position);
//        holder.imageView.setImageResource(book.thumnail); url을 받기때문에 X
        //Gradle 파일에 Glide 라이브러리 추가
        Glide.with(context).load(book.thumbnail).into(holder.imageView);
        holder.txtTitle.setText(book.title);
        holder.txtAuthor.setText(book.authors);
        holder.txtPublisher.setText(book.publisher);
        holder.txtStatus.setText(book.status);
        holder.txtPrice.setText(book.price+"");
        holder.txtSalePrice.setText(book.salePrice+"");

    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView txtTitle;
        TextView txtAuthor;
        TextView txtPublisher;
        TextView txtStatus;
        TextView txtPrice;
        TextView txtSalePrice;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView_item);
            txtTitle = itemView.findViewById(R.id.txt_title_item);
            txtAuthor = itemView.findViewById(R.id.txt_author_item);
            txtPublisher = itemView.findViewById(R.id.txt_publisher_item);
            txtStatus = itemView.findViewById(R.id.txt_status_item);
            txtPrice = itemView.findViewById(R.id.txt_price_item);
            txtSalePrice = itemView.findViewById(R.id.txt_saleprice_item);
            //취소선
            txtPrice.setPaintFlags(txtPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    Book book = books.get(pos);
                    Intent intent = new Intent(context, DetailActivity.class);
                    //intent.putExtra("url",book.url);
                    intent.putExtra("book",book);
                    context.startActivity(intent); //현재 클래스엔 startActivity가 없어서 메인 꺼 씀
                }
            });
        }
    }
}
