package pl.edu.pb.sm_zad_8;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.core.view.WindowCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import pl.edu.pb.sm_zad_8.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private  BookViewModel bookViewModel;

    public static final int NEW_BOOK_ACTIVITY_REQUEST_CODE = 1;
    public static final int EDIT_BOOK_ACTIVITY_REQUEST_CODE = 2;
    private Book editedBook;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final BookAdapter adapter = new BookAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bookViewModel = new ViewModelProvider(this).get(BookViewModel.class);
        bookViewModel.findAll().observe(this,adapter::setBooks);

        FloatingActionButton addBookButton = findViewById(R.id.add_button);
        addBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,EditBookActivity.class);
                startActivityForResult(intent,NEW_BOOK_ACTIVITY_REQUEST_CODE);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter.setOnItemClickListener(new BookAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Book book) {
                Intent intent = new Intent(MainActivity.this, EditBookActivity.class);
                intent.putExtra(EditBookActivity.EXTRA_EDIT_BOOK_TITLE, book.getTitle());
                intent.putExtra(EditBookActivity.EXTRA_EDIT_BOOK_AUTHOR, book.getAuthor());
                startActivityForResult(intent, EDIT_BOOK_ACTIVITY_REQUEST_CODE);
                editedBook = book;
            }

            @Override
            public void onItemLongClick(Book book) {
                bookViewModel.delete(book);
                Snackbar.make(findViewById(R.id.coordinator_layout),getString(R.string.book_deleted)
                        ,Snackbar.LENGTH_LONG).show();            }

        });
    }



    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_BOOK_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Book book = new Book(data.getStringExtra(EditBookActivity.EXTRA_EDIT_BOOK_TITLE),
                    data.getStringExtra(EditBookActivity.EXTRA_EDIT_BOOK_AUTHOR));
            bookViewModel.insert(book);
            Snackbar.make(findViewById(R.id.coordinator_layout), getString(R.string.book_added),
                    Snackbar.LENGTH_LONG).show();
        } else if (requestCode == EDIT_BOOK_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            String title = data.getStringExtra(EditBookActivity.EXTRA_EDIT_BOOK_TITLE);
            String author = data.getStringExtra(EditBookActivity.EXTRA_EDIT_BOOK_AUTHOR);
            Log.d("debug",author);

            if (editedBook != null) {
                editedBook.setTitle(title);
                editedBook.setAuthor(author);
                bookViewModel.update(editedBook);

                Snackbar.make(findViewById(R.id.coordinator_layout),
                        getString(R.string.book_updated),
                        Snackbar.LENGTH_LONG).show();
            }
        } else {
            Snackbar.make(findViewById(R.id.main_layout),
                    getString(R.string.empty_not_saved),
                    Snackbar.LENGTH_LONG).show();
        }
    }


    private class BookHolder extends RecyclerView.ViewHolder {
        private TextView bookTitleTextView;
        private TextView bookAuthorTextView;

        public BookHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.book_list_item,parent,false));

            bookTitleTextView = itemView.findViewById(R.id.book_title);
            bookAuthorTextView = itemView.findViewById(R.id.book_author);
        }

        public void bind (Book book) {
            bookTitleTextView.setText(book.getTitle());
            bookAuthorTextView.setText(book.getAuthor());
        }

    }

    private class BookAdapter extends RecyclerView.Adapter<BookHolder> {
        private List<Book> books;
        private OnItemClickListener onItemClickListener;

        public interface OnItemClickListener {
            void onItemClick(Book book);
            void onItemLongClick(Book book);
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.onItemClickListener = (OnItemClickListener) listener;
        }

        @NonNull
        @Override
        public BookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new BookHolder(getLayoutInflater(),parent);
        }

        @Override
        public void onBindViewHolder(@NonNull BookHolder holder, int position) {

            Book currentBook = books.get(position);
            holder.bind(currentBook);

            holder.itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(currentBook);
                }
            });

            holder.itemView.setOnLongClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemLongClick(currentBook);
                    return true;
                }
                return false;
            });

            if(books != null) {
                Book book = books.get(position);
                holder.bind(book);
            }
            else {
                Log.d("MainActivity","No books");
            }
        }

        @Override
        public int getItemCount() {
            if(books != null) {
                return books.size();
            }
            else {
                return 0;
            }
        }

        void setBooks(List <Book> books) {
            this.books = books;
            notifyDataSetChanged();
        }
    }

}
