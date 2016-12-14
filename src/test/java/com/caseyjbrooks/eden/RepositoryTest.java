package com.caseyjbrooks.eden;

import com.caseyjbrooks.eden.dummy.DummyBible;
import com.caseyjbrooks.eden.dummy.DummyBook;
import com.caseyjbrooks.eden.dummy.DummyRepository;
import com.eden.bible.Bible;
import com.eden.bible.BibleList;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class RepositoryTest {

    public DummyRepository buildRepository() {
        DummyRepository repo = new DummyRepository();

        DummyBible bible = new DummyBible();
        bible.setId("dummy-1");
        bible.setName("Default Bible");
        List<DummyBook> books = new ArrayList<>();
        books.add(new DummyBook(1, "AAAAA", 6, 4, 24, 21, 17, 6));
        books.add(new DummyBook(2, "BBBBB", 56, 43, 23));
        books.add(new DummyBook(3, "CCCCC", 99));
        books.add(new DummyBook(4, "DDDDD", 1, 2, 4, 8, 16, 32, 64));
        books.add(new DummyBook(5, "EEEEE", 1, 1, 2, 3, 5, 8, 13, 21));
        bible.setBooks(books);

        repo.setSelectedBible(bible);

        return repo;
    }

    @Test
    public void testGetBibleSynchronous() throws Throwable {
        DummyRepository repo = buildRepository();

        Bible bible;

        bible = repo.getBible();
        assertThat(bible, is(notNullValue()));
        assertThat(bible.getId(), is(equalTo("dummy-1")));
        assertThat(bible.getName(), is(equalTo("Default Bible")));

        bible = repo.getBible("dummy-2");
        assertThat(bible, is(notNullValue()));
        assertThat(bible.getId(), is(equalTo("dummy-2")));
        assertThat(bible.getName(), is(equalTo("Dummy Bible")));

        repo.getBibleList();

        bible = repo.getBible("list-1");
        assertThat(bible, is(notNullValue()));
        assertThat(bible.getId(), is(equalTo("list-1")));
        assertThat(bible.getName(), is(equalTo("Dummy List")));

        bible = repo.getBible("create-1");
        assertThat(bible, is(notNullValue()));
        assertThat(bible.getId(), is(equalTo("create-1")));
        assertThat(bible.getName(), is(nullValue()));

        repo.setSelectedBible(null);
        bible = repo.getBible("create-1");
        assertThat(bible, is(notNullValue()));
        assertThat(bible.getId(), is(equalTo("create-1")));
        assertThat(bible.getName(), is(nullValue()));
    }

    @Test
    public void testAsynchronousFutures() throws Throwable {
        DummyRepository repo = buildRepository();
        final CountDownLatch lock = new CountDownLatch(1);

        CompletableFuture<Bible> future1 = repo.getBibleAsync();
        CompletableFuture<Bible> future2 = repo.getBibleAsync("dummy-2");
        CompletableFuture<BibleList> future3 = repo.getBibleListAsync();

        future1.exceptionally((error) -> { Assert.fail(); return null; })
        .thenAcceptBoth(future2.exceptionally((error) -> { Assert.fail(); return null; })
        , (bible1, bible2) -> {
            assertThat(bible1, is(notNullValue()));
            assertThat(bible1.getId(), is(equalTo("dummy-1")));

            assertThat(bible2, is(notNullValue()));
            assertThat(bible2.getId(), is(equalTo("dummy-2")));
        })
        .thenAcceptBoth(future3.exceptionally((error) -> { Assert.fail(); return null; })
        , (nothing, bibleList3) -> {
            assertThat(bibleList3, is(notNullValue()));
            assertThat(bibleList3.getBible("list-2").getId(), is(equalTo("list-2")));
            lock.countDown();
        })
        ;

        lock.await(5000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testAsynchronousCallbacks() throws Throwable {
        final DummyRepository repo = buildRepository();
        final CountDownLatch lock1 = new CountDownLatch(1);
        final CountDownLatch lock2 = new CountDownLatch(1);
        final CountDownLatch lock3 = new CountDownLatch(1);

        final FinalBoolean hitLock1 = new FinalBoolean(), hitLock2 = new FinalBoolean(), hitLock3 = new FinalBoolean();

        repo.getBibleAsync((bible1) -> {
            assertThat(bible1, is(notNullValue()));
            assertThat(bible1.getId(), is(equalTo("dummy-1")));
            lock1.countDown();
            hitLock1.flag = true;
        },
        (error) -> {
            Assert.fail();
            return null;
        });

        repo.getBibleAsync("dummy-2", (bible1) -> {
            assertThat(bible1, is(notNullValue()));
            assertThat(bible1.getId(), is(equalTo("dummy-2")));
            lock2.countDown();
            hitLock2.flag = true;
        },
        (error) -> {
            Assert.fail();
            return null;
        });

        repo.getBibleListAsync((bible1) -> {
            assertThat(bible1, is(notNullValue()));
            assertThat(bible1.getBible("list-3").getId(), is(equalTo("list-3")));
            lock3.countDown();
            hitLock3.flag = true;
        },
        (error) -> {
            Assert.fail();
            return null;
        });

        lock1.await(5000, TimeUnit.MILLISECONDS);
        lock2.await(5000, TimeUnit.MILLISECONDS);
        lock3.await(5000, TimeUnit.MILLISECONDS);

        if(!hitLock1.flag || !hitLock2.flag || !hitLock3.flag) {
            Assert.fail();
        }
    }

    private static class FinalBoolean {
        public boolean flag;
    }
}
