package pl.edu.pb.sm_zad_8;
import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import javax.net.ssl.SSLSession;

public class BookViewModel extends AndroidViewModel {

    private final BookRepository bookRepository;
    private final LiveData<List<Book>> books;

    public BookViewModel(@NonNull Application application) {
        super(application);
        bookRepository = new BookRepository(application);
        books = bookRepository.findAllBooks();
    }

    public LiveData<List<Book>> findAll() {
        return books;
    }

    public void insert(Book book) {
        bookRepository.insert(book);
    }

    public void update(Book book) {
        bookRepository.update(book);
    }

    public void delete(Book book) {
        bookRepository.delete(book);
    }

    private MutableLiveData<Book> editedBook = new MutableLiveData<>();

}
